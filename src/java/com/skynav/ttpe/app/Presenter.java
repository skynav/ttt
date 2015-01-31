/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.skynav.ttpe.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.skynav.ttpe.layout.LayoutProcessor;
import com.skynav.ttpe.render.DocumentFrame;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttx.app.TimedTextTransformer;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.TextTransformer;
import com.skynav.ttx.transformer.Transformers;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerOptions;

public class Presenter extends TimedTextTransformer {

    private static final LayoutProcessor defaultLayout = LayoutProcessor.getDefaultProcessor();
    private static final RenderProcessor defaultRenderer = RenderProcessor.getDefaultProcessor();

    // banner text
    private static final String title = "Timed Text Presentation Engine (TTPE) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2013-15 Skynav, Inc.";
    private static final String banner = title + " " + copyright;

    // option and usage info
    private static final String[][] shortOptionSpecifications = new String[][] {
    };
    private static final Collection<OptionSpecification> shortOptions;
    static {
        shortOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : shortOptionSpecifications) {
            shortOptions.add(new OptionSpecification(spec[0], spec[1]));
        }
    }

    private static final String DEFAULT_OUTPUT_ENCODING         = "utf-8";
    private static Charset defaultOutputEncoding;
    private static final String defaultOutputFileNamePattern    = "ttpa{0,number,0000}.xml";

    static {
        try {
            defaultOutputEncoding = Charset.forName(DEFAULT_OUTPUT_ENCODING);
        } catch (RuntimeException e) {
            defaultOutputEncoding = Charset.defaultCharset();
        }
    }

    private static final String[][] longOptionSpecifications = new String[][] {
        { "layout",                     "NAME",     "specify layout name (default: " + defaultLayout.getName() + ")" },
        { "output-clean",               "",         "clean (remove) all files matching output pattern in output directory prior to writing output" },
        { "output-directory",           "DIRECTORY","specify path to directory where output is to be written" },
        { "output-encoding",            "ENCODING", "specify character encoding of output (default: " + defaultOutputEncoding.name() + ")" },
        { "output-format",              "NAME",     "specify output format name (default: " + defaultRenderer.getName() + ")" },
        { "output-indent",              "",         "indent output (default: no indent)" },
        { "output-pattern",             "PATTERN",  "specify output file name pattern" },
        { "show-formats",               "",         "show output formats" },
        { "show-layouts",               "",         "show built-in layouts" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // uri related constants
    private static final String uriFileDescriptorScheme         = "fd";
    private static final String uriFileDescriptorStandardOut    = "stdout";
    private static final String uriStandardOutput               = uriFileDescriptorScheme + ":" + uriFileDescriptorStandardOut;
    private static final String uriFileScheme                   = "file";

    // options state
    private String layoutName;
    @SuppressWarnings("unused")
    private boolean outputDirectoryClean;
    private String outputDirectoryPath;
    private String outputEncodingName;
    private boolean outputIndent;
    private String outputPattern;
    private String rendererName;
    private boolean showLayouts;
    private boolean showRenderers;

    // derived option state
    private LayoutProcessor layout;
    private File outputDirectory;
    private Charset outputEncoding;
    private RenderProcessor renderer;

    // processing state
    private int outputFileSequence;

    public Presenter() {
    }

    @Override
    public void processResult(String[] args, URI uri, Object root) {
        super.processResult(args, uri, root);
        performPresentation(args, uri, root, getResourceState(TransformerContext.ResourceState.ttxOutput.name()));
    }

    @Override
    protected void initializeResourceState(URI uri) {
        super.initializeResourceState(uri);
        setResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name(), Boolean.TRUE);
        setResourceState(TransformerContext.ResourceState.ttxRetainLocations.name(), Boolean.FALSE);
    }

    @Override
    public Object getResourceState(String key) {
        if (key == TransformerContext.ResourceState.ttxTransformer.name())
            return Transformers.getTransformer("isd");
        else
            return super.getResourceState(key);
    }


    @Override
    public String[] preProcessOptions(String[] args, Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions) {
        TransformerOptions layoutOptions = null;
        TransformerOptions rendererOptions = null;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("layout")) {
                    if (i + 1 <= args.length)
                        layoutOptions = LayoutProcessor.getProcessor(args[++i]);
                } else if (option.equals("output-format")) {
                    if (i + 1 <= args.length)
                        rendererOptions = RenderProcessor.getProcessor(args[++i]);
                }
            }
        }
        if (layoutOptions == null)
            layoutOptions = defaultLayout;
        if (rendererOptions == null)
            rendererOptions = defaultRenderer;
        TransformerOptions[] transformerOptions = new TransformerOptions[] { layoutOptions, rendererOptions };
        populateMergedOptionsMaps(baseShortOptions, baseLongOptions, transformerOptions, shortOptions, longOptions);
        return args;
    }

    @Override
    protected void showBanner(PrintWriter out, String banner) {
        super.showBanner(out, Presenter.banner);
    }

    @Override
    public void showUsage(PrintWriter out) {
        super.showUsage(out);
    }

    @Override
    public void runOptions(PrintWriter out) {
        if (showLayouts)
            showLayouts(out);
        if (showRenderers)
            showRenderers(out);
    }

    @Override
    protected boolean doMergeTransformerOptions() {
        return false;
    }

    @Override
    protected boolean hasLongOption(String option) {
        return super.hasLongOption(option);
    }

    @Override
    protected int parseLongOption(String args[], int index) {
        String option = args[index];
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("layout")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            layoutName = args[++index];
        } else if (option.equals("output-clean")) {
            outputDirectoryClean = true;
        } else if (option.equals("output-directory")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            outputDirectoryPath = args[++index];
        } else if (option.equals("output-encoding")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            outputEncodingName = args[++index];
        } else if (option.equals("output-format")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            rendererName = args[++index];
        } else if (option.equals("output-indent")) {
            outputIndent = true;
        } else if (option.equals("output-pattern")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            outputPattern = args[++index];
        } else if (option.equals("show-formats")) {
            showRenderers = true;
        } else if (option.equals("show-layouts")) {
            showLayouts = true;
        } else {
            return super.parseLongOption(args, index);
        }
        return index + 1;
    }

    @Override
    protected boolean hasShortOption(String option) {
        return super.hasShortOption(option);
    }

    @Override
    protected int parseShortOption(String args[], int index) {
        return super.parseShortOption(args, index);
    }

    @Override
    public void processDerivedOptions() {
        super.processDerivedOptions();
        LayoutProcessor layout;
        if (layoutName != null) {
            layout = LayoutProcessor.getProcessor(layoutName);
            if (layout == null)
                throw new InvalidOptionUsageException("layout", "unknown layout: " + layoutName);
        } else
            layout = defaultLayout;
        this.layout = layout;
        layout.processDerivedOptions();
        File outputDirectory;
        if (outputDirectoryPath != null) {
            outputDirectory = new File(outputDirectoryPath);
            if (!outputDirectory.exists())
                throw new InvalidOptionUsageException("output-directory", "directory does not exist: " + outputDirectoryPath);
            else if (!outputDirectory.isDirectory())
                throw new InvalidOptionUsageException("output-directory", "not a directory: " + outputDirectoryPath);
        } else
            outputDirectory = new File(".");
        this.outputDirectory = outputDirectory;
        Charset outputEncoding;
        if (outputEncodingName != null) {
            try {
                outputEncoding = Charset.forName(outputEncodingName);
            } catch (Exception e) {
                outputEncoding = null;
            }
            if (outputEncoding == null)
                throw new InvalidOptionUsageException("output-encoding", "unknown encoding: " + outputEncodingName);
        } else
            outputEncoding = null;
        if (outputEncoding == null)
            outputEncoding = defaultOutputEncoding;
        this.outputEncoding = outputEncoding;
        String outputPattern = this.outputPattern;
        if (outputPattern == null)
            outputPattern = defaultOutputFileNamePattern;
        this.outputPattern = outputPattern;
        RenderProcessor renderer;
        if (rendererName != null) {
            renderer = RenderProcessor.getProcessor(rendererName);
            if (renderer == null)
                throw new InvalidOptionUsageException("output-format", "unknown format: " + rendererName);
        } else
            renderer = defaultRenderer;
        this.renderer = renderer;
        renderer.processDerivedOptions();
    }

    private void showLayouts(PrintWriter out) {
        String defaultLayoutName = defaultLayout.getName();
        StringBuffer sb = new StringBuffer();
        sb.append("Layouts:\n");
        for (String layoutName : LayoutProcessor.getProcessorNames()) {
            sb.append("  ");
            sb.append(layoutName);
            if (layoutName.equals(defaultLayoutName)) {
                sb.append(" (default)");
            }
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    private void showRenderers(PrintWriter out) {
        String defaultRendererName = defaultRenderer.getName();
        StringBuffer sb = new StringBuffer();
        sb.append("Formats:\n");
        for (String rendererName : RenderProcessor.getProcessorNames()) {
            sb.append("  ");
            sb.append(rendererName);
            if (rendererName.equals(defaultRendererName)) {
                sb.append(" (default)");
            }
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    private void performPresentation(String[] args, URI uri, Object root, Object ttxOutput) {
        assert this.layout != null;
        assert this.renderer != null;
        List<Frame> frames = new java.util.ArrayList<Frame>();
        if (ttxOutput instanceof List<?>) {
            List<?> documents = (List<?>) ttxOutput;
            LayoutProcessor lp = this.layout;
            RenderProcessor rp = this.renderer;
            for (Object doc : documents)
                if (doc instanceof Document)
                    frames.addAll(rp.render(lp.layout((Document) doc, this), this));
        }
        processFrames(frames);
    }

    private void processFrames(List<Frame> frames) {
        for (Frame f : frames) {
            processFrame(f);
        }
    }

    private void processFrame(Frame f) {
        writeFrame(f, this);
    }

    private boolean writeFrame(Frame f, TransformerContext context) {
        if (f instanceof DocumentFrame)
            return writeDocumentFrame((DocumentFrame) f, context);
        else
            throw new UnsupportedOperationException();
    }

    private boolean writeDocumentFrame(DocumentFrame f, TransformerContext context) {
        boolean fail = false;
        Document d = f.getDocument();
        Reporter reporter = context.getReporter();
        URI resourceUri = (URI) context.getResourceState(TransformerContext.ResourceState.ttxInputUri.name());
        Map<String,String> prefixes = f.getPrefixes();
        Set<QName> startTagExclusions = f.getStartExclusions();
        Set<QName> endTagExclusions = f.getEndExclusions();
        BufferedWriter bw = null;
        try {
            DOMSource source = new DOMSource(d);
            File[] retOutputFile = new File[1];
            bw = new BufferedWriter(new OutputStreamWriter(getOutputStream(resourceUri, retOutputFile), outputEncoding));
            StreamResult result = new StreamResult(bw);
            Transformer t = new TextTransformer(outputEncoding.name(), outputIndent, prefixes, startTagExclusions, endTagExclusions);
            t.transform(source, result);
            File outputFile = retOutputFile[0];
            reporter.logInfo(reporter.message("*KEY*", "Wrote TTPA ''{0}''.", (outputFile != null) ? outputFile.getAbsolutePath() : uriStandardOutput));
        } catch (Exception e) {
            reporter.logError(e);
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) {}
            }
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private OutputStream getOutputStream(URI uri, File[] retOutputFile) throws IOException {
        StringBuffer sb = new StringBuffer();
        if (isFile(uri)) {
            String path = uri.getPath();
            int s = 0;
            int e = path.length();
            int lastPathSeparator = path.lastIndexOf('/');
            if (lastPathSeparator >= 0)
                s = lastPathSeparator + 1;
            int lastExtensionSeparator = path.lastIndexOf('.');
            if (lastExtensionSeparator >= 0)
                e = lastExtensionSeparator;
            sb.append(path.substring(s, e));
            sb.append(".xml");
        } else {
            sb.append(MessageFormat.format(outputPattern, ++outputFileSequence));
        }
        String outputFileName = sb.toString();
        if (isStandardOutput(outputFileName))
            return System.out;
        else {
            File outputFile = new File(outputDirectory, outputFileName).getCanonicalFile();
            if (retOutputFile != null)
                retOutputFile[0] = outputFile;
            return new FileOutputStream(outputFile);
        }
    }

    private boolean isStandardOutput(String uri) {
        try {
            return isStandardOutput(new URI(uri));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isStandardOutput(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileDescriptorScheme))
            return false;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if ((schemeSpecificPart == null) || !schemeSpecificPart.equals(uriFileDescriptorStandardOut))
            return false;
        return true;
    }

    private boolean isFile(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileScheme))
            return false;
        else
            return true;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new Presenter().run(args));
    }

}
