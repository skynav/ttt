/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttx.app;

import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.ResultProcessor;
import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.Reporter;

import com.skynav.ttx.transformer.Transformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerOptions;
import com.skynav.ttx.transformer.Transformers;

public class TimedTextTransformer implements ResultProcessor, TransformerContext {

    private static final Transformer defaultTransformer = Transformers.getDefaultTransformer();

    // banner text
    private static final String banner =
        "Timed Text Transformer (TTX) [" + Version.CURRENT + "] Copyright 2013 Skynav, Inc.";

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
        { "show-transformers",          "",         "show built-in transformers (use with --verbose to show more details)" },
        { "transformer",                "NAME",     "specify transformer name (default: " + defaultTransformer.getName() + ")" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    private static final String usageCommand =
        "java -jar ttx.jar [options] URL*";

    private static final String[][] nonOptions = new String[][] {
        { "URL", "an absolute or relative URL; if relative, resolved against current working directory" },
    };

    // controlling verifier
    private TimedTextVerifier verifier;

    // options state
    private boolean showTransformers;
    private String transformerName;

    // pre-processed options state
    private TransformerOptions transformerOptions;
    private Map<String,OptionSpecification> mergedShortOptionsMap;
    private Map<String,OptionSpecification> mergedLongOptionsMap;

    // derived option state
    private Transformer transformer;

    public TimedTextTransformer() {
        transformerOptions = defaultTransformer;
    }

    @Override
    public void processResult(String[] args, URI uri, Object root) {
        if (transformer != null) {
            transformer.transform(args, root, this, null);
        }
    }

    @Override
    public ExternalParameters getExternalParameters() {
        return verifier.getExternalParameters();
    }

    @Override
    public Reporter getReporter() {
        return verifier.getReporter();
    }

    @Override
    public Model getModel() {
        return verifier.getModel();
    }

    @Override
    public QName getBindingElementName(Object value) {
        return verifier.getBindingElementName(value);
    }

    @Override
    public Object getBindingElementParent(Object value) {
        return verifier.getBindingElementParent(value);
    }

    @Override
    public Object getBindingElement(Node node) {
        return verifier.getBindingElement(node);
    }

    @Override
    public Node getXMLNode(Object value) {
        return verifier.getXMLNode(value);
    }

    @Override
    public void setResourceState(String key, Object value) {
        verifier.setResourceState(key, value);
    }

    @Override
    public Object getResourceState(String key) {
        return verifier.getResourceState(key);
    }

    public String[] preProcessOptions(String[] args, Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("transformer")) {
                    if (i + 1 <= args.length) {
                        Transformer transformerOptions = Transformers.getTransformer(args[++i]);
                        if (transformerOptions != null)
                            this.transformerOptions = transformerOptions;
                    }
                }
            }
        }
        populateMergedOptionMaps(baseShortOptions, baseLongOptions);
        return args;
    }

    private void populateMergedOptionMaps(Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions) {
        Collection<OptionSpecification> mergedShortOptions = mergeOptions(baseShortOptions, shortOptions);
        if (transformerOptions != null)
            mergedShortOptions = mergeOptions(mergedShortOptions, transformerOptions.getShortOptionSpecs());
        Map<String,OptionSpecification> mergedShortOptionsMap = new java.util.TreeMap<String,OptionSpecification>();
        for (OptionSpecification s : mergedShortOptions)
            mergedShortOptionsMap.put(s.getName(), s);
        this.mergedShortOptionsMap = mergedShortOptionsMap;
        Collection<OptionSpecification> mergedLongOptions = mergeOptions(baseLongOptions, longOptions);
        if (transformerOptions != null)
            mergedLongOptions = mergeOptions(mergedLongOptions, transformerOptions.getLongOptionSpecs());
        Map<String,OptionSpecification> mergedLongOptionsMap = new java.util.TreeMap<String,OptionSpecification>();
        for (OptionSpecification s : mergedLongOptions)
            mergedLongOptionsMap.put(s.getName(), s);
        this.mergedLongOptionsMap = mergedLongOptionsMap;
    }

    private Collection<OptionSpecification> mergeOptions(Collection<OptionSpecification> options, Collection<OptionSpecification> additionalOptions) {
        Collection<OptionSpecification> mergedOptions = new java.util.TreeSet<OptionSpecification>();
        if (options != null) {
            for (OptionSpecification os : options)
                mergedOptions.add(os);
        }
        if (additionalOptions != null) {
            for (OptionSpecification os : additionalOptions)
                mergedOptions.add(os);
        }
        return mergedOptions;
    }

    public boolean hasOption(String arg) {
        assert arg.length() >= 2;
        assert arg.charAt(0) == '-';
        if (arg.charAt(1) == '-')
            return hasLongOption(arg.substring(2));
        else
            return hasShortOption(arg.substring(1));
    }

    public int parseOption(String args[], int index) {
        String option = args[index];
        assert option.length() >= 2;
        assert option.charAt(0) == '-';
        if (option.charAt(1) == '-')
            return parseLongOption(args, index);
        else
            return parseShortOption(args, index);
    }

    public void processDerivedOptions() {
        Transformer transformer;
        if (transformerName != null) {
            transformer = Transformers.getTransformer(transformerName);
            if (transformer == null)
                throw new InvalidOptionUsageException("transformer", "unknown transformer: " + transformerName);
        } else
            transformer = Transformers.getDefaultTransformer();
        this.transformer = transformer;
        assert this.transformer == this.transformerOptions;
        transformer.processDerivedOptions();
    }

    public List<String> processNonOptionArguments(List<String> nonOptionArgs) {
        return nonOptionArgs;
    }

    public void showBanner(PrintWriter out) {
        verifier.showBanner(out, banner);
    }

    public void showUsage(PrintWriter out) {
        out.print("Usage: " + usageCommand + "\n");
        TimedTextVerifier.showOptions(out, "Short Options", mergedShortOptionsMap.values());
        TimedTextVerifier.showOptions(out, "Long Options", mergedLongOptionsMap.values());
        TimedTextVerifier.showOptions(out, "Non-Option Arguments", nonOptions);
    }

    public void runOptions(PrintWriter out) {
        if (showTransformers)
            showTransformers(out);
    }

    private boolean hasLongOption(String option) {
        return mergedLongOptionsMap.containsKey(option);
    }

    private int parseLongOption(String args[], int index) {
        String option = args[index];
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("show-transformers")) {
            showTransformers = true;
        } else if (option.equals("transformer")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            transformerName = args[++index];
        } else {
            if (transformerOptions != null)
                return transformerOptions.parseLongOption(args, index);
            else
                throw new UnknownOptionException("--" + option);
        }
            
        return index + 1;
    }

    private boolean hasShortOption(String option) {
        return mergedShortOptionsMap.containsKey(option);
    }

    private int parseShortOption(String args[], int index) {
        String option = args[index];
        assert option.length() == 2;
        option = option.substring(1);
        if (transformerOptions != null)
            return transformerOptions.parseLongOption(args, index);
        else
            throw new UnknownOptionException("-" + option);
    }

    private void showTransformers(PrintWriter out) {
        String defaultTransformerName = Transformers.getDefaultTransformer().getName();
        StringBuffer sb = new StringBuffer();
        sb.append("Transformers:\n");
        for (String transformerName : Transformers.getTransformerNames()) {
            sb.append("  ");
            sb.append(transformerName);
            if (transformerName.equals(defaultTransformerName)) {
                sb.append(" (default)");
            }
            sb.append('\n');
        }
        out.println(sb.toString());
    }

    public int run(String[] args) {
        return (verifier = new TimedTextVerifier()).run(args, this);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextTransformer().run(args));
    }

}
