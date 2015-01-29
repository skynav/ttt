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

import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;

import com.skynav.ttpe.layout.LayoutProcessor;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttx.app.TimedTextTransformer;
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

    private static final String[][] longOptionSpecifications = new String[][] {
        { "layout",                     "NAME",     "specify layout name (default: " + defaultLayout.getName() + ")" },
        { "renderer",                   "NAME",     "specify renderer name (default: " + defaultRenderer.getName() + ")" },
        { "show-layouts",               "",         "show built-in layouts (use with --verbose to show more details)" },
        { "show-renderers",             "",         "show built-in renderers (use with --verbose to show more details)" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // options state
    private String layoutName;
    private String rendererName;
    private boolean showLayouts;
    private boolean showRenderers;

    // derived option state
    private LayoutProcessor layout;
    private RenderProcessor renderer;

    public Presenter() {
    }

    @Override
    public void processResult(String[] args, URI uri, Object root) {
        super.processResult(args, uri, root);
        performPresentation(args, uri, root, getResourceState(TransformerContext.ResourceState.ttxOutput.name()));
    }

    @Override
    protected void initializeResourceState() {
        super.initializeResourceState();
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
                } else if (option.equals("renderer")) {
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
        } else if (option.equals("renderer")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            rendererName = args[++index];
        } else if (option.equals("show-layouts")) {
            showLayouts = true;
        } else if (option.equals("show-renderers")) {
            showRenderers = true;
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
        RenderProcessor renderer;
        if (rendererName != null) {
            renderer = RenderProcessor.getProcessor(rendererName);
            if (renderer == null)
                throw new InvalidOptionUsageException("renderer", "unknown renderer: " + rendererName);
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
        sb.append("Renderers:\n");
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
        List<Object> frames = new java.util.ArrayList<Object>();
        if (ttxOutput instanceof List<?>) {
            List<?> documents = (List<?>) ttxOutput;
            LayoutProcessor lp = this.layout;
            RenderProcessor rp = this.renderer;
            for (Object doc : documents)
                if (doc instanceof Document)
                    frames.addAll(rp.render(lp.layout((Document) doc)));
        }
        processFrames(frames);
    }

    private void processFrames(List<Object> frames) {
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new Presenter().run(args));
    }

}
