/*
 * Copyright 2013-2019 Skynav, Inc. All rights reserved.
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
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.RestartOptions;
import com.skynav.ttv.app.ResultProcessor;
import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Condition;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.Reporter;

import com.skynav.ttx.transformer.Transformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerOptions;
import com.skynav.ttx.transformer.Transformers;

public class TimedTextTransformer implements ResultProcessor, TransformerContext {

    // banner text
    private static final String title = "Timed Text Transformer (TTX) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2013-21 Skynav, Inc.";
    private static final String banner = title + " " + copyright;

    // option and usage info
    private static final String[][] shortOptionSpecifications = new String[][] {
    };
    private static final Collection<OptionSpecification> shortOptions;
    static {
        Set<OptionSpecification> s = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : shortOptionSpecifications) {
            s.add(new OptionSpecification(spec[0], spec[1]));
        }
        shortOptions = Collections.unmodifiableSet(s);
    }

    private static final String[][] longOptionSpecifications = new String[][] {
        { "show-memory",                "",         "show memory statistics" },
        { "show-transformers",          "",         "show built-in transformers (use with --verbose to show more details)" },
        { "transformer",                "NAME",     "specify transformer name (default: " + Transformers.getDefaultTransformerName() + ")" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        Set<OptionSpecification> s = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            s.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
        longOptions = Collections.unmodifiableSet(s);
    }

    private static final String usageCommand =
        "java -jar ttx.jar [options] URL*";

    private static final String[][] nonOptions = new String[][] {
        { "URL", "an absolute or relative URL; if relative, resolved against current working directory" },
    };

    // controlling verifier
    private TimedTextVerifier verifier;

    // options state
    private boolean showMemory;
    private boolean showTransformers;

    // pre-processed options state
    private List<TransformerOptions> transformerOptions;
    private Map<String,OptionSpecification> mergedShortOptionsMap;
    private Map<String,OptionSpecification> mergedLongOptionsMap;

    // derived option state
    private Transformer transformer;

    public TimedTextTransformer() {
    }

    public void resetAllState(boolean restart) {
        resetResourceState(restart);
        resetOptionsState(restart);
        resetGlobalState(restart);
    }

    private void resetResourceState(boolean restart) {
    }

    private void resetOptionsState(boolean restart) {
        showMemory = false;
        showTransformers = false;
        transformerOptions = null;
        mergedShortOptionsMap = null;
        mergedLongOptionsMap = null;
        if (transformer != null)
            transformer.resetAllState(restart);
        transformer = null;
    }

    private void resetGlobalState(boolean restart) {
        if (!restart) {
            verifier = null;
        }
    }

    protected boolean showMemory() {
        return showMemory;
    }

    protected void setShowMemory(boolean showMemory) {
        this.showMemory = showMemory;
    }

    protected long getUsedMemory() {
        return getUsedMemory(true);
    }

    protected long getUsedMemory(boolean gc) {
        Runtime r = Runtime.getRuntime();
        if (gc)
            r.gc();
        return r.totalMemory() - r.freeMemory();
    }

    protected void setReporter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource, boolean closeOldReporter) {
        if (verifier != null)
            verifier.setReporter(reporter, reporterOutput, reporterOutputEncoding, reporterIncludeSource, closeOldReporter);
    }

    @Override
    public void processResult(List<String> args, URI uri, Object root) {
        Reporter reporter = getReporter();
        initializeResourceState(uri, getModel());
        if (transformer != null) {
            long preTransformMemory = 0;
            long postTransformMemory = 0;
            if (showMemory) {
                preTransformMemory = getUsedMemory();
                reporter.logInfo(reporter.message("*KEY*", "Pre-transform memory usage: {0}", preTransformMemory));
            }
            transformer.transform(args, root, null);
            if (showMemory) {
                postTransformMemory = getUsedMemory();
                reporter.logInfo(reporter.message("*KEY*", "Post-transform memory usage: {0}, delta: {1}", postTransformMemory, postTransformMemory - preTransformMemory));
            }
        }
    }

    protected void initializeResourceState(URI uri, Model model) {
        this  .initializeResourceState(uri);
        model .initializeResourceState(uri, verifier.getResourceState());
    }

    protected void initializeResourceState(URI uri) {
        setResourceState(TransformerContext.ResourceState.ttxConditionEvaluatorState.name(), makeConditionEvaluatorState());
        setResourceState(TransformerContext.ResourceState.ttxDontElideInitials.name(), Boolean.FALSE);
        setResourceState(TransformerContext.ResourceState.ttxInputUri.name(), uri);
        setResourceState(TransformerContext.ResourceState.ttxOutput.name(), null);
        setResourceState(TransformerContext.ResourceState.ttxRetainLocations.name(), Boolean.FALSE);
        setResourceState(TransformerContext.ResourceState.ttxRetainMetadata.name(), Boolean.FALSE);
        setResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name(), Boolean.FALSE);
        setResourceState(TransformerContext.ResourceState.ttxTransformer.name(), transformer);
    }

    protected Condition.EvaluatorState makeConditionEvaluatorState() {
        return Condition.makeEvaluatorState(getConditionMediaParameters(), getConditionBoundParameters(), getConditionSupportedFeatures());
    }

    protected Map<String,Object> getConditionBoundParameters() {
        Map<String,Object> parameters = new java.util.HashMap<String,Object>();
        parameters.put("forced", Boolean.FALSE);
        parameters.put("mediaAspectRatio", null);
        parameters.put("mediaLanguage", null);
        parameters.put("userLanguage", Locale.getDefault().getLanguage());
        return parameters;
    }

    protected Map<String,Object> getConditionMediaParameters() {
        return new java.util.HashMap<String,Object>();
    }

    protected Set<String> getConditionSupportedFeatures() {
        return new java.util.HashSet<String>();
    }

    // TransformerContext implementation

    public ExternalParameters getExternalParameters() {
        return verifier.getExternalParameters();
    }

    public Reporter getReporter() {
        return verifier.getReporter();
    }

    public Model getModel() {
        return verifier.getModel();
    }

    public QName getBindingElementName(Object value) {
        return verifier.getBindingElementName(value);
    }

    public Object getBindingElementParent(Object value) {
        return verifier.getBindingElementParent(value);
    }

    public Object getBindingElement(Node node) {
        return verifier.getBindingElement(node);
    }

    public Node getXMLNode(Object value) {
        return verifier.getXMLNode(value);
    }

    public void setResourceState(String key, Object value) {
        verifier.setResourceState(key, value);
    }

    public Object getResourceState(String key) {
        return verifier.getResourceState(key);
    }

    public Object extractResourceState(String key) {
        return verifier.extractResourceState(key);
    }

    public Condition.EvaluatorState getConditionEvaluatorState() {
        return (Condition.EvaluatorState) getResourceState(ResourceState.ttxConditionEvaluatorState.name());
    }

    public URL getDefaultConfigurationLocator() {
        return Configuration.getDefaultConfigurationLocator();
    }

    public com.skynav.ttv.util.ConfigurationDefaults getConfigurationDefaults(URL locator) {
        return new ConfigurationDefaults(locator);
    }

    public Class<? extends com.skynav.ttv.util.Configuration> getConfigurationClass() {
        return Configuration.class;
    }

    public List<String> preProcessOptions(List<String> args,
        com.skynav.ttv.util.Configuration configuration,
        Collection<OptionSpecification> baseShortOptions,
        Collection<OptionSpecification> baseLongOptions) {
        String transformerName = null;
        // extract transformer name from configuration options if present
        if (configuration != null) {
            for (Map.Entry<String,String> e : configuration.getOptions().entrySet()) {
                String n = e.getKey();
                String v = e.getValue();
                if (n.equals("transformer"))
                    transformerName = v;
            }
        }
        // extract transformer name from argument options if present
        List<String> skippedArgs = new java.util.ArrayList<String>();
        for (int i = 0, n = args.size(); i < n; ++i) {
            String arg = args.get(i);
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("transformer")) {
                    if (i + 1 >= n)
                        throw new MissingOptionArgumentException("--" + option);
                    transformerName = args.get(++i);
                } else {
                    skippedArgs.add(arg);
                }
            } else
                skippedArgs.add(arg);
        }
        // derive transformer
        Transformer transformer;
        if (transformerName != null) {
            transformer = Transformers.getTransformer(transformerName, this);
            if (transformer == null)
                throw new InvalidOptionUsageException("transformer", "unknown transformer: " + transformerName);
        } else {
            transformer = Transformers.getDefaultTransformer(this);
        }
        this.transformer = transformer;
        populateMergedOptionMaps(baseShortOptions, baseLongOptions);
        return skippedArgs;
    }

    protected boolean doMergeTransformerOptions() {
        return true;
    }

    private void populateMergedOptionMaps(Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions) {
        // configure for transformer options merging
        List<TransformerOptions> transformerOptions;
        Collection<OptionSpecification> shortOptions;
        Collection<OptionSpecification> longOptions;
        if (doMergeTransformerOptions()) {
            transformerOptions = new java.util.ArrayList<TransformerOptions>(Arrays.asList(new TransformerOptions[] { transformer }));
            shortOptions = TimedTextTransformer.shortOptions;
            longOptions = TimedTextTransformer.longOptions;
        } else {
            transformerOptions = null;
            shortOptions = null;
            longOptions = null;
        }
        populateMergedOptionsMaps(baseShortOptions, baseLongOptions, transformerOptions, shortOptions, longOptions);
    }

    protected void populateMergedOptionsMaps(Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions,
        List<TransformerOptions> transformerOptions, Collection<OptionSpecification> shortOptions, Collection<OptionSpecification> longOptions) {
        // short options
        Collection<OptionSpecification> mergedShortOptions = mergeOptions(baseShortOptions, shortOptions);
        if (transformerOptions != null) {
            for (TransformerOptions options : transformerOptions) {
                mergedShortOptions = mergeOptions(mergedShortOptions, options.getShortOptionSpecs());
            }
        }
        Map<String,OptionSpecification> mergedShortOptionsMap = new java.util.TreeMap<String,OptionSpecification>();
        if ((this.mergedShortOptionsMap != null) && !this.mergedShortOptionsMap.isEmpty())
            mergedShortOptionsMap.putAll(this.mergedShortOptionsMap);
        for (OptionSpecification s : mergedShortOptions)
            mergedShortOptionsMap.put(s.getName(), s);
        this.mergedShortOptionsMap = mergedShortOptionsMap;
        // long options
        Collection<OptionSpecification> mergedLongOptions = mergeOptions(baseLongOptions, longOptions);
        if (transformerOptions != null) {
            for (TransformerOptions options : transformerOptions) {
                mergedLongOptions = mergeOptions(mergedLongOptions, options.getLongOptionSpecs());
            }
        }
        Map<String,OptionSpecification> mergedLongOptionsMap = new java.util.TreeMap<String,OptionSpecification>();
        if ((this.mergedLongOptionsMap != null) && !this.mergedLongOptionsMap.isEmpty())
            mergedLongOptionsMap.putAll(this.mergedLongOptionsMap);
        for (OptionSpecification s : mergedLongOptions)
            mergedLongOptionsMap.put(s.getName(), s);
        this.mergedLongOptionsMap = mergedLongOptionsMap;
        // update transformer options
        List<TransformerOptions> mergedTransformerOptions = new java.util.ArrayList<TransformerOptions>();
        if ((this.transformerOptions != null) && !this.transformerOptions.isEmpty())
            mergedTransformerOptions.addAll(this.transformerOptions);
        if ((transformerOptions != null) && !transformerOptions.isEmpty())
            mergedTransformerOptions.addAll(transformerOptions);
        this.transformerOptions = mergedTransformerOptions;
    }

    protected Collection<OptionSpecification> mergeOptions(Collection<OptionSpecification> options, Collection<OptionSpecification> additionalOptions) {
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

    public int parseOption(List<String> args, int index) {
        String option = args.get(index);
        assert option.length() >= 2;
        assert option.charAt(0) == '-';
        if (option.charAt(1) == '-')
            return parseLongOption(args, index);
        else
            return parseShortOption(args, index);
    }

    public void processDerivedOptions() {
        /*
        Transformer transformer;
        if (transformerName != null) {
            transformer = Transformers.getTransformer(transformerName);
            if (transformer == null)
                throw new InvalidOptionUsageException("transformer", "unknown transformer: " + transformerName);
        } else if (getResourceState(TransformerContext.ResourceState.ttxTransformer.name()) != null) {
            transformer = (Transformer) getResourceState(TransformerContext.ResourceState.ttxTransformer.name());
        } else
            transformer = Transformers.getDefaultTransformer();
        this.transformer = transformer;
        */
        assert transformer != null;
        transformer.processDerivedOptions();
    }

    public List<String> processNonOptionArguments(List<String> nonOptionArgs) {
        return nonOptionArgs;
    }

    public List<String> processRestartArguments(List<String> args, List<String> nonOptionArgs, RestartOptions restartOptions) {
        return args;
    }

    public void showBanner(PrintWriter out) {
        showBanner(out, TimedTextTransformer.banner);
    }

    protected void showBanner(PrintWriter out, String banner) {
        verifier.showBanner(out, banner);
    }

    public void showUsage(PrintWriter out) {
        out.print("Usage: " + getShowUsageCommand() + "\n");
        TimedTextVerifier.showOptions(out, "Short Options", mergedShortOptionsMap.values());
        TimedTextVerifier.showOptions(out, "Long Options", mergedLongOptionsMap.values());
        TimedTextVerifier.showOptions(out, "Non-Option Arguments", nonOptions);
    }

    public String getShowUsageCommand() {
        return usageCommand;
    }

    public void runOptions(PrintWriter out) {
        if (showTransformers)
            showTransformers(out);
    }

    protected boolean hasLongOption(String option) {
        return mergedLongOptionsMap.containsKey(option);
    }

    protected int parseLongOption(List<String> args, int index) {
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("show-memory")) {
            showMemory = true;
        } else if (option.equals("show-transformers")) {
            showTransformers = true;
        } else if (option.equals("transformer")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            ++index; // ignore - already processed by #preProcessOptions
        } else {
            if (transformerOptions != null) {
                for (TransformerOptions options: transformerOptions) {
                    int i =  options.parseLongOption(args, index);
                    if (i > index)
                        return i;
                }
            }
            throw new UnknownOptionException("--" + option);
        }
        return index + 1;
    }

    protected boolean hasShortOption(String option) {
        return mergedShortOptionsMap.containsKey(option);
    }

    protected int parseShortOption(List<String> args, int index) {
        String arg = args.get(index);
        String option = arg;
        assert option.length() == 2;
        option = option.substring(1);
        if (transformerOptions != null) {
            for (TransformerOptions options: transformerOptions) {
                int i =  options.parseShortOption(args, index);
                if (i > index)
                    return i;
            }
        }
        throw new UnknownOptionException("--" + option);
    }

    private void showTransformers(PrintWriter out) {
        String defaultTransformerName = Transformers.getDefaultTransformerName();
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
        return run(Arrays.asList(args));
    }

    public int run(List<String> args) {
        return (verifier = new TimedTextVerifier()).run(args, this);
    }

    public TimedTextVerifier.Results getResults(String uri) {
        return verifier.getResults(uri);
    }

    public int getResultCode(String uri) {
        return verifier.getResultCode(uri);
    }

    public int getResultFlags(String uri) {
        return verifier.getResultFlags(uri);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextTransformer().run(args));
    }

}
