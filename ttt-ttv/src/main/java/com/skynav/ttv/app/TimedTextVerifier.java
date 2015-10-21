/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.app;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Stack;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Node;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Models;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.util.Configuration;
import com.skynav.ttv.util.ConfigurationDefaults;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Locators;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.Reporters;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Timing;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.Sniffer;
import com.skynav.xml.helpers.XML;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

public class TimedTextVerifier implements VerifierContext {

    public static final int RV_PASS                             = 0;
    public static final int RV_FAIL                             = 1;
    public static final int RV_USAGE                            = 2;

    public static final int RV_FLAG_ERROR_UNEXPECTED            = 0x000001;
    public static final int RV_FLAG_ERROR_EXPECTED_MATCH        = 0x000002;
    public static final int RV_FLAG_ERROR_EXPECTED_MISMATCH     = 0x000004;
    public static final int RV_FLAG_WARNING_UNEXPECTED          = 0x000010;
    public static final int RV_FLAG_WARNING_EXPECTED_MATCH      = 0x000020;
    public static final int RV_FLAG_WARNING_EXPECTED_MISMATCH   = 0x000040;

    public static final String DEFAULT_ENCODING                 = "UTF-8";

    // miscelaneous defaults
    private static final String defaultReporterFileEncoding = Reporters.getDefaultEncoding();

    // banner text
    private static final String title = "Timed Text Verifier (TTV) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2013-15 Skynav, Inc.";
    private static final String banner = title + " " + copyright;

    // usage text
    private static final String repositoryURL =
        "https://github.com/skynav/ttt";
    private static final String repositoryInfo =
        "Source Repository: " + repositoryURL;

    // option and usage info
    private static final String[][] shortOptionSpecifications = new String[][] {
        { "d",  "see --debug" },
        { "q",  "see --quiet" },
        { "v",  "see --verbose" },
        { "?",  "see --help" },
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
        { "config",                     "FILE",     "specify path to configuration file" },
        { "debug",                      "",         "enable debug output (may be specified multiple times to increase debug level)" },
        { "debug-exceptions",           "",         "enable stack traces on exceptions (implies --debug)" },
        { "debug-level",                "LEVEL",    "enable debug output at specified level (default: 0)" },
        { "disable-warnings",           "",         "disable warnings (both hide and don't count warnings)" },
        { "expect-errors",              "COUNT",    "expect count errors or -1 meaning unspecified expectation (default: -1)" },
        { "expect-warnings",            "COUNT",    "expect count warnings or -1 meaning unspecified expectation (default: -1)" },
        { "extension-schema",           "NS URL",   "add schema for namespace NS at location URL to grammar pool (may be specified multiple times)" },
        { "external-duration",          "DURATION", "specify root temporal extent duration for document processing context" },
        { "external-extent",            "EXTENT",   "specify root container region extent for document processing context" },
        { "external-frame-rate",        "RATE",     "specify frame rate for document processing context" },
        { "force-encoding",             "NAME",     "force use of named character encoding, overriding default and resource specified encoding" },
        { "force-model",                "NAME",     "force use of named model, overriding default model and resource specified model" },
        { "help",                       "",         "show usage help" },
        { "hide-warnings",              "",         "hide warnings (but count them)" },
        { "hide-resource-location",     "",         "hide resource location (default: show)" },
        { "hide-resource-path",         "",         "hide resource path (default: show)" },
        { "model",                      "NAME",     "specify model name (default: " + Models.getDefaultModelName() + ")" },
        { "no-warn-on",                 "TOKEN",    "disable warning specified by warning TOKEN, where multiple instances of this option may be specified" },
        { "no-verbose",                 "",         "disable verbose output (resets verbosity level to 0)" },
        { "quiet",                      "",         "don't show banner" },
        { "reporter",                   "REPORTER", "specify reporter, where REPORTER is " + Reporters.getReporterNamesJoined() + " (default: " +
             Reporters.getDefaultReporterName()+ ")" },
        { "reporter-file",              "FILE",     "specify path to file to which reporter output is to be written" },
        { "reporter-file-encoding",     "ENCODING", "specify character encoding of reporter output (default: utf-8)" },
        { "reporter-file-append",       "",         "if reporter file already exists, then append output to it" },
        { "reporter-include-source",    "",         "include source context in report messages" },
        { "servlet",                    "",         "configure defaults for servlet operation" },
        { "show-models",                "",         "show built-in verification models (use with --verbose to show more details)" },
        { "show-repository",            "",         "show source code repository information" },
        { "show-resource-location",     "",         "show resource location (default: show)" },
        { "show-resource-path",         "",         "show resource path (default: show)" },
        { "show-validator",             "",         "show platform validator information" },
        { "show-warning-tokens",        "",         "show warning tokens (use with --verbose to show more details)" },
        { "verbose",                    "",         "enable verbose output (may be specified multiple times to increase verbosity level)" },
        { "treat-foreign-as",           "TOKEN",    "specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: " +
            ForeignTreatment.getDefault().name().toLowerCase() + ")" },
        { "treat-warning-as-error",     "",         "treat warning as error (overrides --disable-warnings)" },
        { "until-phase",                "PHASE",    "verify up to specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: " +
            Phase.getDefault().name().toLowerCase() + ")" },
        { "warn-on",                    "TOKEN",    "enable warning specified by warning TOKEN, where multiple instances of this option may be specified" },
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
        "java -jar ttv.jar [options] URL*";

    private static final String[][] nonOptions = new String[][] {
        { "URL", "an absolute or relative URL; if relative, resolved against current working directory" },
    };

    // default warnings
    private static final Object[][] defaultWarningSpecifications = new Object[][] {
        { "all",                                        Boolean.FALSE,  "all warnings" },
        { "duplicate-idref-in-agent",                   Boolean.FALSE,  "duplicate IDREF in 'ttm:agent' attribute"},
        { "duplicate-idref-in-style",                   Boolean.FALSE,  "duplicate IDREF in 'style' attribute"},
        { "duplicate-idref-in-style-no-intervening",    Boolean.TRUE,   "duplicate IDREF in 'style' attribute without intervening IDREF"  },
        { "duplicate-role",                             Boolean.TRUE,   "duplicate role token in 'ttm:role' attribute"},
        { "foreign",                                    Boolean.FALSE,  "attribute or element in non-TTML (foreign) namespace"},
        { "ignored-profile-attribute",                  Boolean.TRUE,   "'ttp:profile' attribute ignored when 'ttp:profile' element is present"},
        { "missing-agent-actor",                        Boolean.TRUE,   "no 'ttm:agent' child present in 'ttm:agent' element of type 'character'"},
        { "missing-agent-name",                         Boolean.TRUE,   "no 'ttm:name' child present in 'ttm:agent' element"},
        { "missing-profile",                            Boolean.FALSE,  "neither 'ttp:profile' attribute nor 'ttp:profile' element is present"},
        { "negative-origin",                            Boolean.FALSE,  "either coordinate in 'tts:origin' is negative"},
        { "out-of-range-opacity",                       Boolean.TRUE,   "'tts:opacity' is out of range [0,1]"},
        { "quoted-generic-font-family",                 Boolean.FALSE,  "generic font family appears in quoted form, negating generic name function" },
        { "references-extension-role",                  Boolean.FALSE,  "'ttp:role' attribute specifies extension role"},
        { "references-external-image",                  Boolean.FALSE,  "'smpte:backgroundImage' referernces external image"},
        { "references-non-standard-extension",          Boolean.FALSE,  "'ttp:extension' element references non-standard extension"},
        { "references-non-standard-profile",            Boolean.FALSE,  "'ttp:profile' attribute or element references non-standard profile"},
        { "references-other-extension-namespace",       Boolean.FALSE,  "'ttp:extensions' element references other extension namespace"},
        { "xsi-schema-location",                        Boolean.FALSE,  "'xsi:schemaLocation' attribute used"},
        { "xsi-no-namespace-schema-location",           Boolean.TRUE,   "'xsi:noNamespaceSchemaLocation' attribute used"},
        { "xsi-other-attribute",                        Boolean.FALSE,  "'xsi:nil' or 'xsi:type' attribute used"},
    };

    // encodings
    private static final Charset defaultEncoding;
    private static final Charset defaultOutputEncoding;
    private static final Charset asciiEncoding;
    static {
        Charset de, ae;
        try {
            de = Charset.forName(DEFAULT_ENCODING);
            ae = Charset.forName("US-ASCII");
        } catch (RuntimeException e) {
            de = Charset.defaultCharset();
            ae = null;
        }
        defaultEncoding = de;
        defaultOutputEncoding = defaultEncoding;
        asciiEncoding = ae;
    }
    private static final List<Charset> permittedEncodings;
    static {
        List<Charset> l = new java.util.ArrayList<Charset>();
        try {
            l.add(Charset.forName("US-ASCII"));
            l.add(Charset.forName("ISO-8859-1"));
            l.add(Charset.forName("UTF-8"));
            l.add(Charset.forName("UTF-16LE"));
            l.add(Charset.forName("UTF-16BE"));
            l.add(Charset.forName("UTF-16"));
            l.add(Charset.forName("UTF-32LE"));
            l.add(Charset.forName("UTF-32BE"));
            l.add(Charset.forName("UTF-32"));
        } catch (RuntimeException e) {
        }
        permittedEncodings = Collections.unmodifiableList(l);
    }

    // miscellaneous statics
    private static final QName emptyName = new QName("", "");

    // options state
    private String expectedErrors;
    private String expectedWarnings;
    private String externalDuration;
    private String externalExtent;
    private String externalFrameRate;
    private Map<String,String> extensionSchemas = new java.util.HashMap<String,String>();
    private String forceEncodingName;
    private String forceModelName;
    private boolean includeSource;
    private String modelName;
    private boolean quiet;
    private boolean showModels;
    private boolean showRepository;
    private boolean showValidator;
    private boolean showWarningTokens;
    private String treatForeignAs;
    private String untilPhase;

    // derived option state
    private Configuration configuration;
    private Charset forceEncoding;
    private Model forceModel;
    private Model model;
    private ForeignTreatment foreignTreatment;
    private Phase lastPhase;
    private double parsedExternalFrameRate;
    private double parsedExternalDuration;
    private double[] parsedExternalExtent;

    // global processing state
    private PrintWriter showOutput;
    private ExternalParametersStore externalParameters = new ExternalParametersStore();
    private Reporter reporter;
    private SchemaFactory schemaFactory;
    private boolean nonPoolGrammarSupported;
    private Map<List<URL>,Schema> schemas = new java.util.HashMap<List<URL>,Schema>();
    private Map<String,Results> results = new java.util.HashMap<String,Results>();

    // per-resource processing state
    private Phase currentPhase;
    private Model resourceModel;
    private String resourceUriString;
    private Map<String,Object> resourceState;
    private URI resourceUri;
    private Charset resourceEncoding;
    private ByteBuffer resourceBufferRaw;
    private int resourceExpectedErrors = -1;
    private int resourceExpectedWarnings = -1;
    private Binder<Node> binder;
    private Object rootBinding;
    private QName rootName;

    private enum ForeignTreatment {
        Error,          // error, don't apply foreign validation
        Warning,        // warning, but apply foreign validation
        Info,           // info only, but apply foreign validation
        Allow;          // no logging, but apply foreign validation

        public static ForeignTreatment valueOfIgnoringCase(String value) {
            if (value == null)
                throw new IllegalArgumentException();
            for (ForeignTreatment v: values()) {
                if (value.equalsIgnoreCase(v.name()))
                    return v;
            }
            throw new IllegalArgumentException();
        }

        public static ForeignTreatment getDefault() {
            return Warning;
        }
    }

    private enum Phase {
        // N.B. Do not change the following order, since ordinal() is used below.
        None,
        Resource,
        WellFormedness,
        Validity,
        Semantics,
        All;

        public boolean isEnabled(Phase phase) {
            return phase.ordinal() <= ordinal();
        }

        public static Phase valueOfIgnoringCase(String value) {
            if (value == null)
                throw new IllegalArgumentException();
            for (Phase v: values()) {
                if (value.equalsIgnoreCase(v.name()))
                    return v;
            }
            throw new IllegalArgumentException();
        }

        public static Phase getDefault() {
            return All;
        }
    }

    public TimedTextVerifier() {
        this(null, null, null, false, null);
    }

    public TimedTextVerifier(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource, PrintWriter showOutput) {
        if (reporter == null)
            reporter = Reporters.getDefaultReporter();
        setReporter(reporter, reporterOutput, reporterOutputEncoding, reporterIncludeSource);
        setShowOutput(showOutput);
    }

    private void resetReporter() {
        reporter.flush();
        setReporter(Reporters.getDefaultReporter(), null, null, false, true);
    }

    private void setReporter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource) {
        setReporter(reporter, reporterOutput, reporterOutputEncoding, reporterIncludeSource, false);
    }

    public void setReporter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource, boolean closeOldReporter) {
        if (reporter == this.reporter)
            return;
        if (this.reporter != null)
            this.reporter.flush();
        if (closeOldReporter) {
            try {
                if (this.reporter != null)
                    this.reporter.close();
            } catch (IOException e) {
            } finally {
                this.reporter = null;
            }
        }
        try {
            reporter.open(defaultWarningSpecifications, reporterOutput, null, reporterOutputEncoding, reporterIncludeSource);
            this.reporter = reporter;
            this.includeSource = reporterIncludeSource;
        } catch (Throwable e) {
            this.reporter = null;
        }
    }

    private void setReporter(String reporterName, String reporterFileName, String reporterFileEncoding, boolean reporterFileAppend, boolean reporterIncludeSource) {
        assert reporterName != null;
        Reporter reporter = Reporters.getReporter(reporterName);
        if (reporter == null)
            throw new InvalidOptionUsageException("reporter", "unknown reporter: " + reporterName);
        if (reporterFileName != null) {
            if (reporterFileEncoding == null)
                reporterFileEncoding = defaultReporterFileEncoding;
            try {
                Charset.forName(reporterFileEncoding);
            } catch (IllegalCharsetNameException e) {
                throw new InvalidOptionUsageException("reporter-file-encoding", "illegal encoding name: " + reporterFileEncoding);
            } catch (UnsupportedCharsetException e) {
                throw new InvalidOptionUsageException("reporter-file-encoding", "unsupported encoding: " + reporterFileEncoding);
            }
        }

        File reporterFile = null;
        boolean createdReporterFile = false;
        PrintWriter reporterOutput = null;
        if (reporterFileName != null) {
            reporterFile = new File(reporterFileName);
            FileOutputStream os = null;
            try {
                createdReporterFile = reporterFile.createNewFile();
                os = new FileOutputStream(reporterFile,  reporterFileAppend);
                reporterOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os, reporterFileEncoding)));
            } catch (Throwable e) {
                IOUtil.closeSafely(os);
                if (createdReporterFile)
                    IOUtil.deleteSafely(reporterFile);
            }
        }
        setReporter(reporter, reporterOutput, reporterFileEncoding, reporterIncludeSource);
        if (reporterOutput != null) {
            if (getReporter().getOutput() != reporterOutput) {
                reporterOutput.close();
                if (createdReporterFile)
                    IOUtil.deleteSafely(reporterFile);
            }
        }
    }

    @Override
    public ExternalParameters getExternalParameters() {
        return externalParameters;
    }

    @Override
    public Reporter getReporter() {
        return reporter;
    }

    @Override
    public Model getModel() {
        if (this.forceModel != null)
            return this.forceModel;
        else if (this.resourceModel != null)
            return this.resourceModel;
        else
            return this.model;
    }

    public Charset getEncoding() {
        if (this.forceEncoding != null)
            return this.forceEncoding;
        else if (this.resourceEncoding != null)
            return this.resourceEncoding;
        else
            return defaultEncoding;
    }

    @Override
    public QName getBindingElementName(Object value) {
        Node xmlNode = binder.getXMLNode(value);
        if (xmlNode != null) {
            Object jaxbBinding = binder.getJAXBNode(xmlNode);
            if (jaxbBinding instanceof JAXBElement<?>) {
                JAXBElement<?> jaxbNode = (JAXBElement<?>) jaxbBinding;
                return jaxbNode.getName();
            } else
                return new QName(xmlNode.getNamespaceURI(), xmlNode.getLocalName());
        }
        return emptyName;
    }

    @Override
    public Object getBindingElementParent(Object value) {
        Node node = getXMLNode(value);
        if (node == null)
            return null;
        else {
            Node parentNode = node.getParentNode();
            Object parent = getBindingElement(parentNode);
            if (parent != null)
                return parent;
            else if (rootBinding != null) {
                return getModel().getSemanticsVerifier().findBindingElement(rootBinding, parentNode);
            } else
                return null;
        }
    }

    @Override
    public Object getBindingElement(Node node) {
        return binder.getJAXBNode(node);
    }

    @Override
    public Node getXMLNode(Object value) {
        return binder.getXMLNode(value);
    }

    @Override
    public void setResourceState(String key, Object value) {
        if (resourceState != null)
            resourceState.put(key, value);
    }

    @Override
    public Object getResourceState(String key) {
        if (resourceState != null)
            return resourceState.get(key);
        else
            return null;
    }

    @Override
    public Object extractResourceState(String key) {
        Object state = getResourceState(key);
        setResourceState(key, null);
        return state;
    }

    private List<String> preProcessOptions(List<String> args, OptionProcessor optionProcessor) {
        args = processReporterOptions(args, optionProcessor);
        args = processConfigurationOptions(args, optionProcessor);
        if (optionProcessor != null)
            args = optionProcessor.preProcessOptions(args, configuration, shortOptions, longOptions);
        return args;
    }

    private List<String> processReporterOptions(List<String> args, OptionProcessor optionProcessor) {
        String reporterName = null;
        String reporterFileName = null;
        String reporterFileEncoding = null;
        boolean reporterFileAppend = false;
        boolean reporterIncludeSource = false;
        List<String> skippedArgs = new java.util.ArrayList<String>();
        for (int i = 0, n = args.size(); i < n; ++i) {
            String arg = args.get(i);
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("reporter")) {
                    if (i + 1 >= n)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterName = args.get(++i);
                } else if (option.equals("reporter-file")) {
                    if (i + 1 >= n)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterFileName = args.get(++i);
                } else if (option.equals("reporter-file-encoding")) {
                    if (i + 1 >= n)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterFileEncoding = args.get(++i);
                } else if (option.equals("reporter-file-append")) {
                    reporterFileAppend = true;
                } else if (option.equals("reporter-include-source")) {
                    reporterIncludeSource = true;
                } else {
                    skippedArgs.add(arg);
                }
            } else
                skippedArgs.add(arg);
        }
        if (reporterName != null)
            setReporter(reporterName, reporterFileName, reporterFileEncoding, reporterFileAppend, reporterIncludeSource);
        return skippedArgs;
    }

    private List<String> processConfigurationOptions(List<String> args, OptionProcessor optionProcessor) {
        String configFilePath = null;
        List<String> skippedArgs = new java.util.ArrayList<String>();
        for (int i = 0, n = args.size(); i < n; ++i) {
            String arg = args.get(i);
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("config")) {
                    if (i + 1 >= n)
                        throw new MissingOptionArgumentException("--" + option);
                    configFilePath = args.get(++i);
                } else {
                    skippedArgs.add(arg);
                }
            } else
                skippedArgs.add(arg);
        }
        configuration = loadConfiguration(configFilePath, optionProcessor);
        return skippedArgs;
    }

    private Configuration loadConfiguration(String configFilePath, OptionProcessor optionProcessor) {
        try {
            URL locator;
            if (configFilePath != null) {
                File f = new File(configFilePath);
                if (!f.isAbsolute())
                    f = new File(new File(".").getCanonicalFile(), configFilePath);
                locator = f.toURI().toURL();
            } else
                locator = null;
            return loadConfiguration(locator, optionProcessor);
        } catch (IOException e) {
            getReporter().logError(e);
            return null;
        }
    }

    private Configuration loadConfiguration(URL locator, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        if ((locator == null) && (optionProcessor != null))
            locator = optionProcessor.getDefaultConfigurationLocator();
        if (locator == null)
            locator = Configuration.getDefaultConfigurationLocator(TimedTextVerifier.class, null);
        try {
            ConfigurationDefaults configDefaults = (optionProcessor != null) ? optionProcessor.getConfigurationDefaults(locator) : null;
            if (configDefaults == null)
                configDefaults = new ConfigurationDefaults(locator);
            Class<? extends Configuration> configClass = (optionProcessor != null) ? optionProcessor.getConfigurationClass() : null;
            if (configClass == null)
                configClass = Configuration.class;
            return Configuration.fromLocator(locator, configDefaults, configClass, reporter);
        } catch (IOException e) {
            reporter.logError(e);
            return null;
        }
    }

    private List<String> parseArgs(List<String> args, OptionProcessor optionProcessor) {
        args = processConfigurationArguments(args, optionProcessor);
        args = processOptionArguments(args, optionProcessor);
        args = processNonOptionArguments(args, optionProcessor);
        processDerivedOptions(optionProcessor);
        return args;
    }

    private List<String> processConfigurationArguments(List<String> args, OptionProcessor optionProcessor) {
        if (configuration != null) {
            for (Map.Entry<String,String> e : configuration.getOptions().entrySet()) {
                String n = e.getKey();
                String v = e.getValue();
                List<String> option = new java.util.ArrayList<String>(2);
                option.add("--" + n);
                option.add(v);
                int i = parseLongOption(option, 0, optionProcessor);
                assert i > 0;
            }
        }
        return args;
    }

    private List<String> processOptionArguments(List<String> args, OptionProcessor optionProcessor) {
        int nonOptionIndex = -1;
        for (int i = 0; i < args.size();) {
            String arg = args.get(i);
            if (arg.charAt(0) == '-') {
                if (arg.charAt(1) != '-') {
                    if (arg.length() != 2)
                        throw new UnknownOptionException(arg);
                    i = parseShortOption(args, i, optionProcessor);
                } else {
                    i = parseLongOption(args, i, optionProcessor);
                }
            } else {
                nonOptionIndex = i;
                break;
            }
        }
        List<String> nonOptionArgs = new java.util.ArrayList<String>();
        if (nonOptionIndex >= 0) {
            for (int i = nonOptionIndex, n = args.size(); i < n; ++i)
                nonOptionArgs.add(args.get(i));
        }
        return nonOptionArgs;
    }

    private int parseShortOption(List<String> args, int index, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        String option = args.get(index);
        assert option.length() == 2;
        option = option.substring(1);
        switch (option.charAt(0)) {
        case 'd':
            reporter.incrementDebugLevel();
            break;
        case 'q':
            quiet = true;
            break;
        case 'v':
            reporter.incrementVerbosityLevel();
            break;
        case '?':
            throw new ShowUsageException();
        default:
            if ((optionProcessor != null) && optionProcessor.hasOption(args.get(index)))
                return optionProcessor.parseOption(args, index);
            else
                throw new UnknownOptionException("-" + option);
        }
        return index + 1;
    }

    private int parseLongOption(List<String> args, int index, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("debug")) {
            int debug = reporter.getDebugLevel();
            if (debug < 1)
                debug = 1;
            else
                debug += 1;
            reporter.setDebugLevel(debug);
        } else if (option.equals("debug-exceptions")) {
            int debug = reporter.getDebugLevel();
            if (debug < 2)
                debug = 2;
            reporter.setDebugLevel(debug);
        } else if (option.equals("debug-level")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String level = args.get(++index);
            int debugNew;
            try {
                debugNew = Integer.parseInt(level);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("debug-level", "bad syntax: " + level);
            }
            int debug = reporter.getDebugLevel();
            if (debugNew > debug)
                reporter.setDebugLevel(debugNew);
        } else if (option.equals("disable-warnings")) {
            reporter.disableWarnings();
        } else if (option.equals("expect-errors")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            expectedErrors = args.get(++index);
        } else if (option.equals("expect-warnings")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            expectedWarnings = args.get(++index);
        } else if (option.equals("extension-schema")) {
            if (index + 2 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String namespaceURI = args.get(++index);
            String schemaResourceURL = args.get(++index);
            extensionSchemas.put(namespaceURI, schemaResourceURL);
        } else if (option.equals("external-duration")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalDuration = args.get(++index);
        } else if (option.equals("external-extent")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalExtent = args.get(++index);
        } else if (option.equals("external-frame-rate")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalFrameRate = args.get(++index);
        } else if (option.equals("force-encoding")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            forceEncodingName = args.get(++index);
        } else if (option.equals("force-model")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            forceModelName = args.get(++index);
        } else if (option.equals("help")) {
            throw new ShowUsageException();
        } else if (option.equals("hide-resource-location")) {
            reporter.hideLocation();
        } else if (option.equals("hide-resource-path")) {
            reporter.hidePath();
        } else if (option.equals("hide-warnings")) {
            reporter.hideWarnings();
        } else if (option.equals("model")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            modelName = args.get(++index);
        } else if (option.equals("no-warn-on")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String token = args.get(++index);
            if (!reporter.hasDefaultWarning(token))
                throw new InvalidOptionUsageException("--" + option, "token '" + token + "' is not a recognized warning token");
            reporter.disableWarning(token);
        } else if (option.equals("no-verbose")) {
            reporter.setVerbosityLevel(0);
        } else if (option.equals("quiet")) {
            quiet = true;
        } else if (option.equals("servlet")) {
            reporter.hideLocation();
        } else if (option.equals("show-models")) {
            showModels = true;
        } else if (option.equals("show-repository")) {
            showRepository = true;
        } else if (option.equals("show-resource-location")) {
            reporter.showLocation();
        } else if (option.equals("show-resource-path")) {
            reporter.showPath();
        } else if (option.equals("show-validator")) {
            showValidator = true;
        } else if (option.equals("show-warning-tokens")) {
            showWarningTokens = true;
        } else if (option.equals("treat-foreign-as")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            treatForeignAs = args.get(++index);
        } else if (option.equals("treat-warning-as-error")) {
            reporter.setTreatWarningAsError(true);
        } else if (option.equals("until-phase")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            untilPhase = args.get(++index);
        } else if (option.equals("verbose")) {
            reporter.incrementVerbosityLevel();
        } else if (option.equals("warn-on")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String token = args.get(++index);
            if (!reporter.hasDefaultWarning(token))
                throw new InvalidOptionUsageException("--" + option, "token '" + token + "' is not a recognized warning token");
            reporter.enableWarning(token);
        } else if ((optionProcessor != null) && optionProcessor.hasOption(arg)) {
            return optionProcessor.parseOption(args, index);
        } else
            throw new UnknownOptionException("--" + option);
        return index + 1;
    }

    private List<String> processNonOptionArguments(List<String> args, OptionProcessor optionProcessor) {
        if (optionProcessor != null)
            args = optionProcessor.processNonOptionArguments(args);
        return args;
    }

    private void processDerivedOptions(OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        Charset forceEncoding;
        if (forceEncodingName != null) {
            try {
                forceEncoding = Charset.forName(forceEncodingName);
            } catch (Exception e) {
                forceEncoding = null;
            }
            if (forceEncoding == null)
                throw new InvalidOptionUsageException("force-encoding", "unknown encoding: " + forceEncodingName);
        } else
            forceEncoding = null;
        this.forceEncoding = forceEncoding;
        Model forceModel;
        if (forceModelName != null) {
            forceModel = Models.getModel(forceModelName);
            if (forceModel == null)
                throw new InvalidOptionUsageException("force-model", "unknown model: " + forceModelName);
        } else
            forceModel = null;
        this.forceModel = forceModel;
        Model model;
        if ((modelName != null) && !modelName.equals("auto")) {
            model = Models.getModel(modelName);
            if (model == null)
                throw new InvalidOptionUsageException("model", "unknown model: " + modelName);
        } else
            model = Models.getDefaultModel();
        this.model = model;
        if (treatForeignAs != null) {
            try {
                foreignTreatment = ForeignTreatment.valueOfIgnoringCase(treatForeignAs);
            } catch (IllegalArgumentException e) {
                throw new InvalidOptionUsageException("treat-foreign-as", "unknown token: " + treatForeignAs);
            }
        } else
            foreignTreatment = ForeignTreatment.getDefault();
        if (foreignTreatment == ForeignTreatment.Warning)
            reporter.enableWarning("foreign");
        if (untilPhase != null) {
            try {
                lastPhase = Phase.valueOfIgnoringCase(untilPhase);
            } catch (IllegalArgumentException e) {
                throw new InvalidOptionUsageException("until-phase", "unknown token: " + untilPhase);
            }
        } else
            lastPhase = Phase.getDefault();
        if (externalFrameRate != null) {
            try {
                parsedExternalFrameRate = Double.parseDouble(externalFrameRate);
                getExternalParameters().setParameter("externalFrameRate", Double.valueOf(parsedExternalFrameRate));
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("external-frame-rate", "invalid syntax, must be a double: " + externalFrameRate);
            }
        } else
            parsedExternalFrameRate = 30.0;
        if (externalDuration != null) {
            Time[] duration = new Time[1];
            TimeParameters timeParameters = new TimeParameters(parsedExternalFrameRate);
            if (Timing.isDuration(externalDuration, null, this, timeParameters, duration)) {
                if (duration[0].getType() != Time.Type.Offset)
                    throw new InvalidOptionUsageException("external-duration", "must use offset time syntax only: " + externalDuration);
                parsedExternalDuration = duration[0].getTime(timeParameters);
                getExternalParameters().setParameter("externalDuration", Double.valueOf(parsedExternalDuration));
            } else
                throw new InvalidOptionUsageException("external-duration", "invalid syntax: " + externalDuration);
        }
        if (externalExtent != null) {
            Integer[] minMax = new Integer[] { 2, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(externalExtent, null, this, minMax, treatments, lengths)) {
                for (Length l : lengths) {
                    if (l.getUnits() != Length.Unit.Pixel)
                        throw new InvalidOptionUsageException("external-extent", "must use pixel (px) unit only: " + externalExtent);
                }
                parsedExternalExtent = new double[] { lengths.get(0).getValue(), lengths.get(1).getValue() };
                getExternalParameters().setParameter("externalExtent", parsedExternalExtent);
            } else
                throw new InvalidOptionUsageException("external-extent", "invalid syntax: " + externalExtent);
        }
        if (optionProcessor != null)
            optionProcessor.processDerivedOptions();
    }

    public void setShowOutput(PrintWriter showOutput) {
        this.showOutput = showOutput;
    }

    private PrintWriter getShowOutput() {
        if (showOutput == null)
            showOutput = new PrintWriter(new OutputStreamWriter(System.err, defaultOutputEncoding));
        return showOutput;
    }

    public void showBanner(PrintWriter out, String banner) {
        if (!quiet)
            out.println(banner);
    }

    private void showBanner(PrintWriter out, OptionProcessor optionProcessor) {
        if (optionProcessor != null)
            optionProcessor.showBanner(out);
        else
            showBanner(out, banner);
    }

    private void showUsage(PrintWriter out, OptionProcessor optionProcessor) {
        showBanner(out, optionProcessor);
        if (optionProcessor != null)
            optionProcessor.showUsage(out);
        else
            showUsage(out);
    }

    private void showUsage(PrintWriter out) {
        out.print("Usage: " + usageCommand + "\n");
        showOptions(out, "Short Options", shortOptions);
        showOptions(out, "Long Options", longOptions);
        showOptions(out, "Non-Option Arguments", nonOptions);
    }

    public static void showOptions(PrintWriter out, String label, Collection<OptionSpecification> optionSpecs) {
        StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append(label);
        sb.append(':');
        sb.append('\n');
        for (OptionSpecification os : optionSpecs) {
            sb.append("    ");
            sb.append(os.toString());
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    public static void showOptions(PrintWriter out, String label, String[][] optionSpecs) {
        StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append(label);
        sb.append(':');
        sb.append('\n');
        for (String[] option : optionSpecs) {
            assert option.length == 2;
            sb.append("    ");
            sb.append(option[0]);
            for (int i = 0, n = OptionSpecification.OPTION_FIELD_LENGTH - option[0].length(); i < n; i++)
                sb.append(' ');
            sb.append('-');
            sb.append(' ');
            sb.append(option[1]);
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    private void showProcessingInfo() {
        Reporter reporter = getReporter();
        int level = reporter.getVerbosityLevel();
        if (level >  0) {
            if (level > 1)
                showConfigurationInfo();
            if (reporter.isTreatingWarningAsError())
                reporter.logInfo(reporter.message("*KEY*", "Warnings are treated as errors."));
            else if (reporter.areWarningsDisabled())
                reporter.logInfo(reporter.message("*KEY*", "Warnings are disabled."));
            else if (reporter.areWarningsHidden())
                reporter.logInfo(reporter.message("*KEY*", "Warnings are hidden."));
        }
    }

    private void showConfigurationInfo() {
        Reporter reporter = getReporter();
        if (configuration != null) {
            URL locator = configuration.getLocator();
            reporter.logInfo(reporter.message("*KEY*", "Loaded configuration from ''{0}''.", (locator != null) ? locator : "default empty configuration"));
            if (!configuration.getOptions().isEmpty()) {
                Map<String,String> options = configuration.getOptions();
                Set<String> names = new java.util.TreeSet<String>(options.keySet());
                for (String n : names) {
                    String v = options.get(n);
                    reporter.logInfo(reporter.message("*KEY*", "Configuration option: {0} = ''{1}''.", n, v));
                }
            } else
                reporter.logInfo(reporter.message("*KEY*", "Configuration is empty."));
        } else
            reporter.logInfo(reporter.message("*KEY*", "No configuration."));
    }

    private void showModels() {
        Reporter reporter = getReporter();
        String defaultModelName = Models.getDefaultModelName();
        StringBuffer sb = new StringBuffer();
        sb.append("Verification Models:\n");
        for (String modelName : Models.getModelNames()) {
            sb.append("  ");
            sb.append(modelName);
            if (modelName.equals(defaultModelName)) {
                sb.append(" (default)");
            }
            sb.append('\n');
            if (reporter.getVerbosityLevel() > 0) {
                Model model = Models.getModel(modelName);
                String[] schemaResourceNames = model.getSchemaResourceNames();
                if (schemaResourceNames != null) {
                    for (String schemaResourceName : schemaResourceNames) {
                        sb.append("    XSD: ");
                        sb.append(schemaResourceName);
                        sb.append('\n');
                    }
                }
            }
        }
        getShowOutput().println(sb.toString());
    }

    private void showRepository() {
        getShowOutput().println(repositoryInfo);
    }

    private void showValidator() {
        getShowOutput().println(getValidatorInfo());
    }

    private void showWarningTokens() {
        Reporter reporter = getReporter();
        int maxTokenLength = 0;
        for (Object[] spec : defaultWarningSpecifications) {
            String token = (String) spec[0];
            int tokenLength = token.length();
            maxTokenLength = Math.max(maxTokenLength, tokenLength + ((tokenLength % 2) + 1) * 2);
        }
        StringBuffer sb = new StringBuffer();
        sb.append("Warning Tokens:\n");
        for (Object[] spec : defaultWarningSpecifications) {
            String token = (String) spec[0];
            Boolean defaultValue = (Boolean) spec[1];
            String help = (String) spec[2];
            sb.append("    ");
            sb.append(token);
            if (reporter.getVerbosityLevel() > 0) {
                if ((help != null) && (help.length() > 0)) {
                    int pad = maxTokenLength - token.length();
                    for (int i = 0; i < pad; ++i)
                        sb.append(' ');
                    sb.append(help);
                    if (!token.equals("all")) {
                        sb.append(" (default: ");
                        sb.append(defaultValue ? "enabled" : "disabled");
                        sb.append(')');
                    }
                }
            }
            sb.append("\n");
        }
        getShowOutput().println(sb.toString());
    }

    private URI getCWDAsURI() {
        return new File(".").toURI();
    }

    private URI resolve(String uriString) {
        Reporter reporter = getReporter();
        try {
            URI uri = new URI(uriString);
            if (!uri.isAbsolute()) {
                URI uriCurrentDirectory = getCWDAsURI();
                URI uriAbsolute = uriCurrentDirectory.resolve(uri);
                assert uriAbsolute != null;
                uri = uriAbsolute;
            }
            return uri;
        } catch (URISyntaxException e) {
            reporter.logError(reporter.message("*KEY*", "Bad URI syntax: '{'{0}'}'", uriString));
            return null;
        }
    }

    private ByteBuffer readResource(URI uri) {
        Reporter reporter = getReporter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = uri.toURL().openStream();
            byte[] buffer = new byte[1024];
            int nb;
            while ((nb = is.read(buffer)) >= 0) {
                os.write(buffer, 0, nb);
            }
        } catch (IOException e) {
            reporter.logError(e);
            os = null;
        } finally {
            IOUtil.closeSafely(is);
        }
        return (os != null) ? ByteBuffer.wrap(os.toByteArray()) : null;
    }

    public static Charset[] getPermittedEncodings() {
        return permittedEncodings.toArray(new Charset[permittedEncodings.size()]);
    }

    private boolean isPermittedEncoding(String name) {
        try {
            Charset cs = Charset.forName(name);
            for (Charset encoding : permittedEncodings) {
                if (encoding.equals(cs))
                    return true;
            }
            return false;
        } catch (UnsupportedCharsetException e) {
            return false;
        }
    }

    private CharBuffer decodeResource(ByteBuffer rawBuffer, Charset encoding, int bomLength) {
        Reporter reporter = getReporter();
        ByteBuffer bb = rawBuffer;
        bb.position(bomLength);
        List<CharBuffer> charBuffers = new java.util.ArrayList<CharBuffer>();
        do {
            CharsetDecoder cd = encoding.newDecoder();
            boolean endOfInput = false;
            CharBuffer cb = null;
            CoderResult r;
            while (true) {
                try {
                    if (cb == null)
                        cb = CharBuffer.allocate(65536);
                    if (bb != null)
                        r = cd.decode(bb, cb, endOfInput);
                    else
                        r = cd.flush(cb);
                    if (r.isOverflow()) {
                        cb.flip();
                        charBuffers.add(cb);
                        cb = null;
                    } else if (r.isUnderflow()) {
                        if (endOfInput) {
                            if (bb != null)
                                bb = null;
                            else {
                                cb.flip();
                                charBuffers.add(cb);
                                cb = null;
                                break;
                            }
                        } else {
                            endOfInput = true;
                        }
                    } else if (r.isMalformed()) {
                        Message message = reporter.message("*KEY*",
                            "Malformed {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    } else if (r.isUnmappable()) {
                        Message message = reporter.message("*KEY*",
                            "Unmappable {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    } else if (r.isError()) {
                        Message message = reporter.message("*KEY*",
                            "Can't decode as {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    }
                } catch (Exception e) {
                    reporter.logError(e);
                    return null;
                }
            }
        } while (false);
        rawBuffer.rewind();
        return concatenateBuffers(charBuffers);
    }

    private static CharBuffer concatenateBuffers(List<CharBuffer> buffers) {
        int length = 0;
        for (CharBuffer cb : buffers) {
            length += cb.limit();
        }
        CharBuffer newBuffer = CharBuffer.allocate(length);
        for (CharBuffer cb : buffers) {
            newBuffer.put(cb);
        }
        newBuffer.flip();
        return newBuffer;
    }

    private String[] parseLines(CharBuffer cb, Charset encoding) {
        List<String> lines = new java.util.ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        while (cb.hasRemaining()) {
            while (cb.hasRemaining()) {
                char c = cb.get();
                if (c == '\n') {
                    break;
                } else if (c == '\r') {
                    if (cb.hasRemaining()) {
                        c = cb.charAt(0);
                        if (c == '\n')
                            cb.get();
                    }
                    break;
                } else {
                    sb.append(c);
                }
            }
            lines.add(sb.toString());
            sb.setLength(0);
        }
        cb.rewind();
        return lines.toArray(new String[lines.size()]);
    }

    private void resetResourceState() {
        resourceModel = model;
        resourceState = new java.util.HashMap<String,Object>();
        resourceUriString = null;
        resourceUri = null;
        resourceEncoding = null;
        resourceBufferRaw = null;
        resourceExpectedErrors = -1;
        resourceExpectedWarnings = -1;
        binder = null;
        rootBinding = null;
        rootName = null;
        getReporter().resetResourceState();
    }

    private void setResourceURI(String uri) {
        resourceUriString = uri;
        getReporter().setResourceURI(uri);
    }

    private void setResourceURI(URI uri) {
        resourceUri = uri;
        getReporter().setResourceURI(uri);
    }

    private void setResourceBuffer(Charset encoding, CharBuffer buffer, ByteBuffer bufferRaw) {
        resourceEncoding = encoding;
        setResourceState("encoding", encoding);
        resourceBufferRaw = bufferRaw;
        setResourceState("bufferRaw", bufferRaw);
        if (expectedErrors != null) {
            resourceExpectedErrors = parseAnnotationAsInteger(expectedErrors, -1);
            setResourceState("resourceExpectedErrors", Integer.valueOf(resourceExpectedErrors));
        }
        if (expectedWarnings != null) {
            resourceExpectedWarnings = parseAnnotationAsInteger(expectedWarnings, -1);
            setResourceState("resourceExpectedWarnings", Integer.valueOf(resourceExpectedWarnings));
        }
    }

    private void setResourceDocumentContextState() {
        if (parsedExternalExtent != null) {
            setResourceState("externalExtent", parsedExternalExtent);
        }
    }

    private boolean verifyResource() {
        Reporter reporter = getReporter();
        currentPhase = Phase.Resource;
        if (!lastPhase.isEnabled(Phase.Resource)) {
            reporter.logInfo(reporter.message("*KEY*", "Skipping resource presence and encoding verification phase {0}.", currentPhase.ordinal()));
            return true;
        } else {
            reporter.logInfo(reporter.message("*KEY*", "Verifying resource presence and encoding phase {0}...", currentPhase.ordinal()));
        }
        URI uri = resolve(resourceUriString);
        if (uri != null) {
            setResourceURI(uri);
            ByteBuffer bytesBuffer = readResource(uri);
            if (bytesBuffer != null) {
                Object[] sniffOutputParameters = new Object[] { Integer.valueOf(0) };
                Charset encoding;
                if (this.forceEncoding != null)
                    encoding = this.forceEncoding;
                else
                    encoding = Sniffer.sniff(bytesBuffer, asciiEncoding, sniffOutputParameters);
                if (isPermittedEncoding(encoding.name())) {
                    int bomLength = (Integer) sniffOutputParameters[0];
                    CharBuffer charsBuffer = decodeResource(bytesBuffer, encoding, bomLength);
                    if (charsBuffer != null) {
                        setResourceBuffer(encoding, charsBuffer, bytesBuffer);
                        if (includeSource)
                            reporter.setLines(parseLines(charsBuffer, encoding));
                        if (this.forceEncoding != null)
                            reporter.logInfo(reporter.message("*KEY*", "Resource encoding forced to {0}.", encoding.name()));
                        else
                            reporter.logInfo(reporter.message("*KEY*", "Resource encoding sniffed as {0}.", encoding.name()));
                        reporter.logInfo(reporter.message("*KEY*", "Resource length {0} bytes, decoded as {1} Java characters (char).",
                            bytesBuffer.limit(), charsBuffer.limit()));
                    }
                } else {
                    reporter.logError(reporter.message("*KEY*", "Encoding {0} is not permitted", encoding.name()));
                }
            }
        }
        return reporter.getResourceErrors() == 0;
    }

    private InputStream openStream(ByteBuffer bb) {
        bb.rewind();
        if (bb.hasArray()) {
            return new ByteArrayInputStream(bb.array());
        } else {
            byte[] bytes = new byte[bb.limit()];
            bb.get(bytes);
            return new ByteArrayInputStream(bb.array());
        }
    }

    private void processAnnotations(Attributes attributes) {
        Reporter reporter = getReporter();
        if (attributes != null) {
            String annotationsNamespace = Annotations.getNamespace();
            for (int i = 0, n = attributes.getLength(); i < n; ++i) {
                if (attributes.getURI(i).equals(annotationsNamespace)) {
                    String localName = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    if (localName.equals("expectedErrors")) {
                        resourceExpectedErrors = parseAnnotationAsInteger(value, -1);
                    } else if (localName.equals("expectedWarnings")) {
                        resourceExpectedWarnings = parseAnnotationAsInteger(value, -1);
                    } else if (localName.equals("model")) {
                        Model model = Models.getModel(value);
                        if (model != null)
                            resourceModel = model;
                        else
                            throw new InvalidAnnotationException(localName, "unknown model '" + value + "'");
                    } else if (localName.equals("warnOn")) {
                        String[] tokens = value.split("\\s+");
                        for (String token : tokens) {
                            if (reporter.hasDefaultWarning(token))
                                reporter.enableWarning(token);
                        }
                    } else if (localName.equals("noWarnOn")) {
                        String[] tokens = value.split("\\s+");
                        for (String token : tokens) {
                            if (reporter.hasDefaultWarning(token))
                                reporter.disableWarning(token);
                        }
                    } else if (localName.equals("loc")) {
                        // no processing required here
                    } else {
                        throw new InvalidAnnotationException(localName, "unknown annotation");
                    }
                }
            }
        }
    }

    private boolean verifyWellFormedness() {
        Reporter reporter = getReporter();
        currentPhase = Phase.WellFormedness;
        if (!lastPhase.isEnabled(Phase.WellFormedness)) {
            reporter.logInfo(reporter.message("*KEY*", "Skipping XML well-formedness verification phase ({0}).", currentPhase.ordinal()));
            return true;
        } else
            reporter.logInfo(reporter.message("*KEY*", "Verifying XML well-formedness phase {0}...", currentPhase.ordinal()));
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setValidating(false);
            pf.setNamespaceAware(true);
            SAXParser p = pf.newSAXParser();
            Charset encoding = getEncoding();
            InputSource is = new InputSource(new InputStreamReader(openStream(resourceBufferRaw), encoding));
            is.setEncoding(encoding.name());
            is.setSystemId(resourceUri.toString());
            p.parse(is, new DefaultHandler() {
                private boolean expectRootElement = true;
                public void startElement(String nsUri, String localName, String qualName, Attributes attrs) throws SAXException {
                    if (expectRootElement) {
                        processAnnotations(attrs);
                        expectRootElement = false;
                    }
                }
                public void error(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness error
                    getReporter().logError(e);
                    throw new WellFormednessErrorException(e);
                }
                public void fatalError(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness error
                    getReporter().logError(e);
                    throw new WellFormednessErrorException(e);
                }
                public void warning(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness warning treated as error
                    if (getReporter().logWarning(e))
                        throw new WellFormednessErrorException(e);
                }
            });
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        } catch (WellFormednessErrorException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXParseException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXException e) {
            reporter.logError(e);
        } catch (InvalidAnnotationException e) {
            reporter.logError(e);
        } catch (IOException e) {
            reporter.logError(e);
        }
        return reporter.getResourceErrors() == 0;
    }

    private SchemaFactory getSchemaFactory() {
        if (schemaFactory == null) {
            schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        }
        return schemaFactory;
    }

    private Source[] getSources(URL[] components) {
        List<Source> sources = new java.util.ArrayList<Source>(components.length);
        for ( URL url : components ) {
            sources.add(new StreamSource(url.toExternalForm()));
        }
        return sources.toArray(new Source[sources.size()]);
    }

    private Schema loadSchema(List<URL> components) throws SchemaValidationErrorException {
        Reporter reporter = getReporter();
        SchemaFactory sf = getSchemaFactory();
        sf.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException e) {
                getReporter().logError(e);
                throw new SchemaValidationErrorException(e);
            }
            public void fatalError(SAXParseException e) {
                getReporter().logError(e);
                throw new SchemaValidationErrorException(e);
            }
            public void warning(SAXParseException e) {
                if (getReporter().logWarning(e))
                    throw new SchemaValidationErrorException(e);
            }
        });
        try {
            // attempt to enable non-pool grammars, i.e., use of xsi:schemaLocation pool extensions
            sf.setFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", false);
            nonPoolGrammarSupported = true;
        } catch (SAXException e) {
            nonPoolGrammarSupported = false;
        }
        try {
            reporter.logDebug(reporter.message("*KEY*", "Loading (and validating) schema components at '{'{0}'}'...", components));
            return sf.newSchema(getSources(components.toArray(new URL[components.size()])));
        } catch (SAXException e) {
            reporter.logError(e);
            throw new SchemaValidationErrorException(e);
        }
    }

    private Schema getSchema(List<URL> components) throws SchemaValidationErrorException {
        if (!schemas.containsKey(components)) {
            schemas.put(components, loadSchema(components));
        }
        return schemas.get(components);
    }

    private URL getSchemaResource(String resourceName) throws SchemaValidationErrorException {
        Reporter reporter = getReporter();
        reporter.logDebug(reporter.message("*KEY*", "Searching for built-in schema at {0}...", resourceName));
        try {
            URL urlSchema = null;
            Enumeration<URL> resources = getClass().getClassLoader().getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String urlPath = url.getPath();
                if (urlPath.indexOf(resourceName) == (urlPath.length() - resourceName.length())) {
                    reporter.logDebug(reporter.message("*KEY*", "Found resource match at '{'{0}'}'.", url));
                    urlSchema = url;
                    break;
                } else {
                    reporter.logDebug(reporter.message("*KEY*", "Skipping partial resource match at '{'{0}'}'.", url));
                }
            }
            if (urlSchema == null) {
                Message message = reporter.message("*KEY*:", "Can't find schema resource '{'{0}'}'.", resourceName);
                reporter.logDebug(message);
                throw new SchemaValidationErrorException(new MissingResourceException(message.toString(), getClass().getName(), resourceName));
            }
            return urlSchema;
        } catch (IOException e) {
            throw new SchemaValidationErrorException(e);
        }
    }

    private Schema getSchema() throws SchemaValidationErrorException {
        Reporter reporter = getReporter();
        List<URL> schemaComponents = new java.util.ArrayList<URL>();
        for (String name : getModel().getSchemaResourceNames())
            schemaComponents.add(getSchemaResource(name));
        for (String schemaResourceLocation : extensionSchemas.values()) {
            URI uri = resolve(schemaResourceLocation);
            if (uri != null) {
                try {
                    schemaComponents.add(uri.toURL());
                } catch (IOException e) {
                    reporter.logError(e);
                }
            }
        }
        return getSchema(schemaComponents);
    }

    private boolean verifyValidity() {
        Reporter reporter = getReporter();
        currentPhase = Phase.Validity;
        if (!lastPhase.isEnabled(Phase.Validity)) {
            reporter.logInfo(reporter.message("*KEY*", "Skipping XSD validity verification phase ({0}).", currentPhase.ordinal()));
            return true;
        } else
            reporter.logInfo(reporter.message("*KEY*", "Verifying XSD validity phase {0}...", currentPhase.ordinal()));
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            XMLReader filter = new ForeignVocabularyFilter(reader, getModel().getNamespaceURIs(), extensionSchemas.keySet(), foreignTreatment);
            Charset encoding = getEncoding();
            InputSource is = new InputSource(new InputStreamReader(openStream(resourceBufferRaw), encoding));
            is.setEncoding(encoding.name());
            is.setSystemId(resourceUri.toString());
            SAXSource source = new SAXSource(filter, is);
            source.setSystemId(resourceUri.toString());
            Validator v = getSchema().newValidator();
            v.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException e) {
                    // don't terminated validation on validation error
                    getReporter().logError(e);
                }
                public void fatalError(SAXParseException e) {
                    // don't terminated validation on validation error
                    getReporter().logError(e);
                }
                public void warning(SAXParseException e) {
                    // don't terminated validation on validation warning treated as error
                    getReporter().logWarning(e);
                }
            });
            v.validate(source);
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        } catch (SchemaValidationErrorException e) {
            reporter.logError(e);
        // } catch (ForeignVocabularyException e) {
            // Already logged error via foreign vocabulary filter
        } catch (SAXParseException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXException e) {
            reporter.logError(e);
        } catch (IOException e) {
            reporter.logError(e);
        }
        return reporter.getResourceErrors() == 0;
    }

    private static final String saxPropertyPrefix = "http://xml.org/sax/properties/";
    private static final String[] saxPropertyNames = {
        "declaration-handler",
        "document-xml-version",
        "dom-node",
        "lexical-handler",
        "xml-string",
    };

    private static final String saxFeaturePrefix = "http://xml.org/sax/features/";
    private static final String[] saxFeatureNames = {
        "allow-dtd-events-after-endDTD",
        "external-general-entities",
        "external-parameter-entities",
        "is-standalone",
        "lexical-handler/parameter-entities",
        "namespace-prefixes",
        "namespaces",
        "resolve-dtd-uris",
        "string-interning",
        "unicode-normalization-checking",
        "use-attributes2",
        "use-entity-resolver2",
        "use-locator2",
        "validation",
        "xml-1.1",
        "xmlns-uris",
    };

    private static final String jaxpPropertyPrefix = "http://java.sun.com/xml/jaxp/properties/";
    private static final String[] jaxpPropertyNames = {
        "schemaLanguage",
        "schemaSource",
        "elementAttributeLimit",
    };

    private static final String jaxpFeaturePrefix = "http://java.sun.com/xml/jaxp/features/";
    private static final String[] jaxpFeatureNames = {
    };

    private static final String xercesPropertyPrefix = "http://apache.org/xml/properties/";
    private static final String[] xercesPropertyNames = {
        "dom/current-element-node",
        "dom/document-class-name",
        "input-buffer-size",
        "internal/datatype-validator-factory",
        "internal/document-scanner",
        "internal/dtd-processor",
        "internal/dtd-scanner",
        "internal/entity-manager",
        "internal/entity-resolver",
        "internal/error-handler",
        "internal/error-reporter",
        "internal/grammar-pool",
        "internal/namespace-binder",
        "internal/namespace-context",
        "internal/symbol-table",
        "internal/validation-manager",
        "internal/validator",
        "internal/validator/dtd",
        "internal/validator/schema",
        "internal/xinclude-handler",
        "internal/xpointer-handler",
        "schema/external-noNamespaceSchemaLocation",
        "schema/external-schemaLocation",
        "security-manager",
    };

    private static final String xercesFeaturePrefix = "http://apache.org/xml/features/";
    private static final String[] xercesFeatureNames = {
        "allow-java-encodings",
        "continue-after-fatal-error",
        "disallow-doctype-decl",
        "dom/create-entity-ref-nodes",
        "dom/defer-node-expansion",
        "dom/include-ignorable-whitespace",
        "generate-synthetic-annotations",
        "honour-all-schemaLocations",
        "internal/parser-settings",
        "internal/validation/schema/use-grammar-pool-only",
        "nonvalidating/load-dtd-grammar",
        "nonvalidating/load-external-dtd",
        "scanner/notify-builtin-refs",
        "scanner/notify-char-refs",
        "standard-uri-conformant",
        "validate-annotations",
        "validation/change-ignorable-characters-into-ignorable-whitespaces",
        "validation/default-attribute-values",
        "validation/dynamic",
        "validation/schema",
        "validation/schema-full-checking",
        "validation/schema/augment-psvi",
        "validation/schema/element-default",
        "validation/schema/ignore-schema-location-hints",
        "validation/schema/normalized-value",
        "validation/validate-content-models",
        "validation/validate-datatypes",
        "validation/warn-on-duplicate-attdef",
        "validation/warn-on-undeclared-elemdef",
        "warn-on-duplicate-entitydef",
        "xinclude",
        "xinclude-aware",
        "xinclude/fixup-base-uris",
        "xinclude/fixup-language",
    };

    private String getValidatorInfo() {
        StringBuffer sb = new StringBuffer();
        Validator v = null;
        try {
            v = getSchema().newValidator();
        } catch (Exception e) {
            sb.append("Unable to obtain validator information!\n");
        }
        if (v != null) {
            sb.append("Validator class: " + v.getClass().getName() + "\n");
            for (String pn : saxPropertyNames) {
                try {
                    String ppn = saxPropertyPrefix + pn;
                    Object pv = v.getProperty(ppn);
                    assert pv != null;
                    sb.append("SAX property: {" + ppn + "}: " + pv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
            for (String fn : saxFeatureNames) {
                try {
                    String pfn = saxFeaturePrefix + fn;
                    Object pv = v.getFeature(pfn);
                    if (pv != null)
                        sb.append("SAX feature: {" + pfn + "}: " + pv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
            for (String pn : jaxpPropertyNames) {
                try {
                    String ppn = jaxpPropertyPrefix + pn;
                    Object pv = v.getProperty(ppn);
                    assert pv != null;
                    sb.append("JAXP property: {" + ppn + "}: " + pv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
            for (String fn : jaxpFeatureNames) {
                try {
                    String pfn = jaxpFeaturePrefix + fn;
                    Object fv = v.getFeature(pfn);
                    if (fv != null)
                        sb.append("JAXP feature: {" + pfn + "}: " + fv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
            for (String pn : xercesPropertyNames) {
                try {
                    String ppn = xercesPropertyPrefix + pn;
                    Object pv = v.getProperty(ppn);
                    assert pv != null;
                    sb.append("XERCES property: {" + ppn + "}: " + pv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
            for (String fn : xercesFeatureNames) {
                try {
                    String pfn = xercesFeaturePrefix + fn;
                    Object fv = v.getFeature(pfn);
                    if (fv != null)
                        sb.append("XERCES feature: {" + pfn + "}: " + fv.toString() + "\n");
                } catch (SAXNotRecognizedException e) {
                    continue;
                } catch (SAXNotSupportedException e) {
                    continue;
                }
            }
        }
        return sb.toString();
    }

    private QName getXmlElementDecl(Class<?> jaxbClass, String creatorMethod) {
        try {
            Class<?> ofc = jaxbClass.getClassLoader().loadClass(jaxbClass.getPackage().getName() + ".ObjectFactory");
            return ((JAXBElement<?>) ofc.getDeclaredMethod(creatorMethod, jaxbClass).invoke(ofc.newInstance(), new Object[] { null } )).getName();
        } catch (ClassNotFoundException e) {
            return emptyName;
        } catch (IllegalAccessException e) {
            return emptyName;
        } catch (InstantiationException e) {
            return emptyName;
        } catch (InvocationTargetException e) {
            return emptyName;
        } catch (NoSuchMethodException e) {
            return emptyName;
        }
    }

    private String getContentClassNames(Map<Class<?>,String> contentClasses) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (Map.Entry<Class<?>,String> e : contentClasses.entrySet()) {
            Class<?> contentClass = e.getKey();
            if (sb.length() > 0)
                sb.append(",");
            sb.append('<');
            sb.append(getXmlElementDecl(contentClass, e.getValue()));
            sb.append('>');
        }
        sb.append('}');
        return sb.toString();
    }

    private boolean verifyRootElement(JAXBElement<?> root, Map<Class<?>,String> rootClasses) {
        Reporter reporter = getReporter();
        Object contentObject = root.getValue();
        for (Class<?> rootClass : rootClasses.keySet()) {
            if (rootClass.isInstance(contentObject))
                return true;
        }
        reporter.logError(reporter.message(Locators.getLocator(contentObject), "*KEY*",
            "Unexpected root element <{0}>, expected one of {1}.", root.getName(), getContentClassNames(rootClasses)));
        return false;
    }

    private int parseAnnotationAsInteger(String annotation, int defaultValue) {
        try {
            return Integer.parseInt(annotation);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean verifySemantics() {
        Reporter reporter = getReporter();
        currentPhase = Phase.Semantics;
        if (!lastPhase.isEnabled(Phase.Semantics)) {
            reporter.logInfo(reporter.message("*KEY*", "Skipping semantics verification phase ({0}).", currentPhase.ordinal()));
            return true;
        } else
            reporter.logInfo(reporter.message("*KEY*", "Verifying semantics phase {0} using ''{1}'' model...", currentPhase.ordinal(), getModel().getName()));
        try {
            // construct source pipeline
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            StripWhitespaceFilter filter0 = new StripWhitespaceFilter(reader);
            ForeignVocabularyFilter filter1 = new ForeignVocabularyFilter(filter0, getModel().getNamespaceURIs(), extensionSchemas.keySet(), ForeignTreatment.Allow);
            LocationAnnotatingFilter filter2 = new LocationAnnotatingFilter(filter1);
            Charset encoding = getEncoding();
            InputSource is = new InputSource(new InputStreamReader(openStream(resourceBufferRaw), encoding));
            is.setEncoding(encoding.name());
            is.setSystemId(resourceUri.toString());
            SAXSource source = new SAXSource(filter2, is);
            source.setSystemId(resourceUri.toString());

            // construct annotated infoset destination
            DOMResult result = new DOMResult();
            result.setSystemId(resourceUri.toString());

            // transform into annotated infoset
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.newTransformer().transform(source, result);

            // unmarshall annotated infoset
            JAXBContext context = JAXBContext.newInstance(getModel().getJAXBContextPath());
            Binder<Node> binder = context.createBinder();
            Object unmarshalled = binder.unmarshal(result.getNode());

            // retain reference to binder at instance scope for error reporter utilities
            this.binder = binder;

            // verify root then remaining semantics
            if (unmarshalled == null)
                reporter.logError(reporter.message("*KEY*", "Missing root element."));
            else if (!(unmarshalled instanceof JAXBElement))
                reporter.logError(reporter.message("*KEY*", "Unexpected root element, can't introspect non-JAXBElement"));
            else {
                JAXBElement<?> root = (JAXBElement<?>) unmarshalled;
                Documents.assignIdAttributes(binder.getXMLNode(root).getOwnerDocument(), getModel().getIdAttributes());
                if (verifyRootElement(root, getModel().getRootClasses())) {
                    this.rootBinding = root.getValue();
                    this.rootName = root.getName();
                    setResourceDocumentContextState();
                    getModel().getSemanticsVerifier().verify(this.rootBinding, this);
                }
            }
        } catch (UnmarshalException e) {
            reporter.logError(e);
        } catch (TransformerFactoryConfigurationError e) {
            reporter.logError(new Exception(e));
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        } catch (TransformerConfigurationException e) {
            reporter.logError(e);
        } catch (TransformerException e) {
            reporter.logError(e);
        } catch (JAXBException e) {
            reporter.logError(e);
        } catch (SAXException e) {
            reporter.logError(e);
        }
        return reporter.getResourceErrors() == 0;
    }

    private int verify(List<String> args, String uri, ResultProcessor resultProcessor) {
        Reporter reporter = getReporter();
        if (!reporter.isHidingLocation())
            reporter.logInfo(reporter.message("*KEY*", "Verifying '{'{0}'}'.", uri));
        do {
            resetResourceState();
            setResourceURI(uri);
            if (!verifyResource())
                break;
            if (!verifyWellFormedness())
                break;
            if (!verifyValidity())
                break;
            if (!verifySemantics())
                break;
            if (resultProcessor != null)
                resultProcessor.processResult(args, resourceUri, rootBinding);
        } while (false);
        int rv = rvValue();
        reporter.logInfo(reporter.message("*KEY*", "Verification {0}{1}.", rvPassed(rv) ? "Passed" : "Failed", resultDetails()));
        reporter.flush();
        Results results = new Results(uri, rv,
            resourceExpectedErrors, reporter.getResourceErrors(), resourceExpectedWarnings, reporter.getResourceWarnings(), getModel(), getEncoding(), rootName);
        this.results.put(uri, results);
        return rv;
    }

    private int rvValue() {
        Reporter reporter = getReporter();
        int code = RV_PASS;
        int flags = 0;
        if (resourceExpectedErrors < 0) {
            if (reporter.getResourceErrors() > 0) {
                code = RV_FAIL;
                flags |= RV_FLAG_ERROR_UNEXPECTED;
            }
        } else if (reporter.getResourceErrors() != resourceExpectedErrors) {
            code = RV_FAIL;
            flags |= RV_FLAG_ERROR_EXPECTED_MISMATCH;
        } else {
            code = RV_PASS;
            if (reporter.getResourceErrors() > 0)
                flags |= RV_FLAG_ERROR_EXPECTED_MATCH;
        }
        if (resourceExpectedWarnings < 0) {
            if (reporter.getResourceWarnings() > 0)
                flags |= RV_FLAG_WARNING_UNEXPECTED;
        } else if (reporter.getResourceWarnings() != resourceExpectedWarnings) {
            code = RV_FAIL;
            flags |= RV_FLAG_WARNING_EXPECTED_MISMATCH;
        } else {
            if (reporter.getResourceWarnings() > 0)
                flags |= RV_FLAG_WARNING_EXPECTED_MATCH;
        }
        return ((flags & 0x7FFFFF) << 8) | (code & 0xFF);
    }

    public static boolean rvPassed(int rv) {
        return rvCode(rv) == RV_PASS;
    }

    public static int rvCode(int rv) {
        return (rv & 0xFF);
    }

    public static int rvFlags(int rv) {
        return ((rv >> 8) & 0x7FFFFF);
    }

    private String resultDetails() {
        Reporter reporter = getReporter();
        int resourceErrors = reporter.getResourceErrors();
        int resourceWarnings = reporter.getResourceWarnings();
        StringBuffer details = new StringBuffer();
        if (resourceExpectedErrors < 0) {
            if (resourceErrors > 0) {
                details.append(", with " );
                details.append(resourceErrors);
                details.append(' ');
                details.append(plural("error", resourceErrors));
            }
        } else if (resourceErrors == resourceExpectedErrors) {
            if (resourceErrors > 0) {
                details.append(", with ");
                details.append(resourceErrors);
                details.append(" expected ");
                details.append(plural("error", resourceErrors));
            }
        } else {
            details.append(", with ");
            details.append(resourceErrors);
            details.append(' ');
            details.append(plural("error", resourceErrors));
            details.append(" but expected ");
            details.append(resourceExpectedErrors);
            details.append(' ');
            details.append(plural("error", resourceExpectedErrors));
        }
        if (resourceExpectedWarnings < 0) {
            if (resourceWarnings > 0) {
                details.append(details.length() > 0 ? ", and with " : ", with ");
                details.append(resourceWarnings);
                details.append(' ');
                details.append(plural("warning", resourceWarnings));
            }
        } else if (resourceWarnings == resourceExpectedWarnings) {
            if (resourceWarnings > 0) {
                details.append(details.length() > 0 ? ", and with " : ", with ");
                details.append(resourceWarnings);
                details.append(" expected ");
                details.append(plural("warning", resourceWarnings));
            }
        } else {
            details.append(details.length() > 0 ? ", and with " : ", with ");
            details.append(resourceWarnings);
            details.append(' ');
            details.append(plural("warning", resourceWarnings));
            details.append(" but expected ");
            details.append(resourceExpectedWarnings);
            details.append(' ');
            details.append(plural("warning", resourceExpectedWarnings));
        }
        return details.toString();
    }

    private String plural(String noun, int count) {
        if (count == 1)
            return noun;
        else
            return noun + "s";
    }

    private int verify(List<String> args, List<String> nonOptionArgs, ResultProcessor resultProcessor) {
        Reporter reporter = getReporter();
        int numFailure = 0;
        int numSuccess = 0;
        for (String uri : nonOptionArgs) {
            switch (rvCode(verify(args, uri, resultProcessor))) {
            case RV_PASS:
                ++numSuccess;
                break;
            case RV_FAIL:
                ++numFailure;
                break;
            default:
                break;
            }
            reporter.flush();
        }
        if (reporter.getVerbosityLevel() > 0) {
            Message message;
            if (numSuccess > 0) {
                if (numFailure > 0) {
                    message = reporter.message("*KEY*",
                        "Passed {0} {0,choice,0#resources|1#resource|1<resources}, Failed {1} {1,choice,0#resources|1#resource|1<resources}.", numSuccess, numFailure);
                } else {
                    message = reporter.message("*KEY*",
                        "Passed {0} {0,choice,0#resources|1#resource|1<resources}.", numSuccess);
                }
            } else {
                if (numFailure > 0) {
                    message = reporter.message("*KEY*",
                        "Failed {0} {0,choice,0#resources|1#resource|1<resources}.", numFailure);
                } else {
                    message = null;
                }
            }
            if (message != null)
                reporter.logInfo(message);
        }
        return numFailure > 0 ? 1 : 0;
    }

    public int run(String[] args) {
        return run(Arrays.asList(args), null);
    }

    public int run(List<String> args, ResultProcessor resultProcessor) {
        int rv = 0;
        OptionProcessor optionProcessor = (OptionProcessor) resultProcessor;
        try {
            List<String> argsPreProcessed = preProcessOptions(args, optionProcessor);
            showBanner(getShowOutput(), optionProcessor);
            getShowOutput().flush();
            List<String> nonOptionArgs = parseArgs(argsPreProcessed, optionProcessor);
            if (showModels)
                showModels();
            if (showRepository)
                showRepository();
            if (showValidator)
                showValidator();
            if (showWarningTokens)
                showWarningTokens();
            if (optionProcessor != null)
                optionProcessor.runOptions(getShowOutput());
            if (nonOptionArgs.size() > 1) {
                if (expectedErrors != null)
                    throw new InvalidOptionUsageException("expect-errors", "must not specify more than one URL with this option");
                if (expectedWarnings != null)
                    throw new InvalidOptionUsageException("expect-warnings", "must not specify more than one URL with this option");
            }
            getShowOutput().flush();
            if (nonOptionArgs.size() > 0) {
                showProcessingInfo();
                rv = verify(args, nonOptionArgs, resultProcessor);
            } else
                rv = RV_PASS;
        } catch (ShowUsageException e) {
            showUsage(getShowOutput(), optionProcessor);
            rv = RV_USAGE;
        } catch (UsageException e) {
            getShowOutput().println("Usage: " + e.getMessage());
            rv = RV_USAGE;
        }
        resetReporter();
        getShowOutput().flush();
        return rv;
    }

    public Map<String,Results> getResults() {
        return results;
    }

    public Results getResults(String uri) {
        return results.get(uri);
    }

    public int getResultCode(String uri) {
        if (results.containsKey(uri))
            return results.get(uri).code;
        else
            return -1;
    }

    public int getResultFlags(String uri) {
        if (results.containsKey(uri))
            return results.get(uri).flags;
        else
            return -1;
    }

    public static String getVersionTitle() {
        return title;
    }

    public static String getRepositoryURL() {
        return repositoryURL;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextVerifier().run(args));
    }

    private static String[] externalRepresentations(URI[] uris) {
        List<String> uriStrings = new java.util.ArrayList<String>(uris.length);
        for (URI uri : uris) {
            uriStrings.add(uri.toString());
        }
        return uriStrings.toArray(new String[uriStrings.size()]);
    }

    private static class InvalidAnnotationException extends RuntimeException {
        static final long serialVersionUID = 0;
        InvalidAnnotationException(String name, String details) {
            super("invalid annotation: " + name + ": " + details);
        }
    }

    private static class WellFormednessErrorException extends RuntimeException {
        static final long serialVersionUID = 0;
        WellFormednessErrorException(Throwable cause) {
            super(cause);
        }
    }

    private static class SchemaValidationErrorException extends RuntimeException {
        static final long serialVersionUID = 0;
        SchemaValidationErrorException(Throwable cause) {
            super(cause);
        }
    }

    private class StripWhitespaceFilter extends XMLFilterImpl {

        private StripWhitespaceFilter(XMLReader reader) {
            super(reader);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String str = new String(ch, start, length);
            String trimmed = str.trim();
            super.characters(trimmed.toCharArray(), 0, trimmed.length());
//            super.characters(ch, start, length); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private class ForeignVocabularyFilter extends XMLFilterImpl {

        private Set<String> standardNamespaces;
        private Set<String> extensionNamespaces;
        private ForeignTreatment foreignTreatment;
        private Stack<QName> nameStack = new Stack<QName>();
        private boolean inForeign;
        private Locator currentLocator;

        ForeignVocabularyFilter(XMLReader reader, String[] standardNamespaces, Set<String> extensionNamespaces, ForeignTreatment foreignTreatment) {
            super(reader);
            this.standardNamespaces = new java.util.HashSet<String>(Arrays.asList(standardNamespaces));
            this.extensionNamespaces = new java.util.HashSet<String>(extensionNamespaces);
            this.foreignTreatment = foreignTreatment;
        }

        ForeignVocabularyFilter(XMLReader reader, URI[] namespaceURIs, Set<String> extensionNamespaces, ForeignTreatment foreignTreatment) {
            this(reader, externalRepresentations(namespaceURIs), extensionNamespaces, foreignTreatment);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            currentLocator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qualName, Attributes attrs) throws SAXException {
            Reporter reporter = getReporter();
            if (foreignTreatment == ForeignTreatment.Allow)
                super.startElement(nsUri, localName, qualName, attrs);
            else if (!inForeign && isNonForeignNamespace(nsUri))
                super.startElement(nsUri, localName, qualName, filterAttributes(attrs));
            else {
                QName qn = new QName(nsUri, localName);
                nameStack.push(qn);
                inForeign = true;
                logPruning(reporter.message(currentLocator, "*KEY*", "Pruning element in foreign namespace '{'{0}'}'.", qn));
            }
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            if (!inForeign)
                super.characters(chars, start, length);
        }

        @Override
        public void endElement(String nsUri, String localName, String qualName) throws SAXException {
            if (!inForeign) {
                super.endElement(nsUri, localName, qualName);
            } else if (!nameStack.empty()) {
                QName name = new QName(nsUri, localName);
                if (nameStack.peek().equals(name)) {
                    nameStack.pop();
                    if (nameStack.empty())
                        inForeign = false;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                throw new IllegalStateException();
            }
        }

        private Attributes filterAttributes(Attributes attrs) {
            Reporter reporter = getReporter();
            boolean hasForeign = false;
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                String nsUri = attrs.getURI(i);
                if (isNonForeignNamespace(nsUri)) {
                    if (XML.isXSINamespace(nsUri))
                        checkXSIAttribute(attrs, i);
                    continue;
                } else
                    hasForeign = true;
            }
            if (hasForeign && (foreignTreatment != ForeignTreatment.Allow)) {
                AttributesImpl attrsNew = new AttributesImpl();
                for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                    String nsUri = attrs.getURI(i);
                    if (isNonForeignNamespace(nsUri))
                        attrsNew = copyAttribute(attrsNew, attrs, i);
                    else {
                        logPruning(reporter.message(currentLocator, "*KEY*",
                            "Pruning attribute in foreign namespace '{'{0}'}'.", new QName(attrs.getURI(i), attrs.getLocalName(i))));
                    }
                }
                return attrsNew;
            } else
                return attrs;
        }

        private AttributesImpl copyAttribute(AttributesImpl attrsNew, Attributes attrsOld, int i) {
            attrsNew.addAttribute(attrsOld.getURI(i), attrsOld.getLocalName(i), attrsOld.getQName(i), attrsOld.getType(i), attrsOld.getValue(i));
            return attrsNew;
        }

        private void checkXSIAttribute(Attributes attrs, int index) {
            Reporter reporter = getReporter();
            QName qn = new QName(attrs.getURI(index), attrs.getLocalName(index));
            String ln = qn.getLocalPart();
            if (ln.equals("schemaLocation")) {
                String locations = attrs.getValue(index);
                if (reporter.isWarningEnabled("xsi-schema-location")) {
                    Message message = reporter.message(currentLocator, "*KEY*",
                        "An '{'{0}'}' attribute was used, with value '{'{1}'}'.", qn, locations);
                    if (reporter.logWarning(message))
                        reporter.logError(message);
                }
                checkXSISchemaLocations(qn, locations);
            } else if (ln.equals("noNamespaceSchemaLocation")) {
                if (reporter.isWarningEnabled("xsi-no-namespace-schema-location")) {
                    Message message = reporter.message(currentLocator, "*KEY*",
                        "An '{'{0}'}' attribute was used, but foreign vocabulary in no namespace is not supported.", qn);
                    if (reporter.logWarning(message))
                        reporter.logError(message);
                }
            } else if (ln.equals("type") || ln.equals("nil")) {
                if (reporter.isWarningEnabled("xsi-other-attribute")) {
                    Message message = reporter.message(currentLocator, "*KEY*",
                        "An '{'{0}'}' attribute was used, but may not be supported by platform supplied validator.", qn);
                    if (reporter.logWarning(message))
                        reporter.logError(message);
                }
            } else
                reporter.logError(reporter.message(currentLocator, "*KEY*", "Unknown attribute '{'{0}'}' in XSI namespace.", qn));
        }

        private void checkXSISchemaLocations(QName qn, String locations) {
            Reporter reporter = getReporter();
            String[] pairs = locations.trim().split("\\s+");
            if ((pairs.length & 1) != 0) {
                reporter.logError(reporter.message(currentLocator, "*KEY*", "Unpaired schema location in '{'{0}'}': '{'{1}'}'.", qn, locations));
            } else {
                for (int i = 0, n = pairs.length / 2; i < n; ++i) {
                    String schemaNamespace = pairs[2*i+0];
                    if (!nonPoolGrammarSupported && !extensionSchemas.containsKey(schemaNamespace)) {
                        if (reporter.isWarningEnabled("xsi-schema-location")) {
                            Message message = reporter.message(currentLocator, "*KEY*",
                                "Platform validator doesn't support non-pool schemas, try specifying location {0} with ''--external-schema'' option.", schemaNamespace);
                            if (reporter.logWarning(message))
                                reporter.logError(message);
                        }
                    }
                }
            }
        }

        private boolean isNonForeignNamespace(String nsUri) {
            if (nsUri == null)
                return true;
            else if (nsUri.length() == 0)
                return true;
            else if (XML.isXMLNamespace(nsUri))
                return true;
            else if (XML.isXSINamespace(nsUri))
                return true;
            else if (isStandardNamespace(nsUri))
                return true;
            else if (isExtensionNamespace(nsUri))
                return true;
            else if (isAnnotationNamespace(nsUri))
                return true;
            else
                return false;
        }

        private boolean isStandardNamespace(String nsUri) {
            return standardNamespaces.contains(nsUri);
        }

        private boolean isExtensionNamespace(String nsUri) {
            return extensionNamespaces.contains(nsUri);
        }

        private boolean isAnnotationNamespace(String nsUri) {
            if (nsUri.equals(Annotations.getNamespace()))
                return true;
            else
                return false;
        }

        private void logPruning(Message message) {
            Reporter reporter = getReporter();
            if (foreignTreatment == ForeignTreatment.Error) {
                reporter.logError(message);
            } if (foreignTreatment == ForeignTreatment.Warning) {
                if (reporter.isWarningEnabled("foreign")) {
                    if (reporter.logWarning(message))
                        reporter.logError(message);
                }
            } else if (foreignTreatment == ForeignTreatment.Info)
                reporter.logInfo(message);
        }
    }

    private static class LocationAnnotatingFilter extends XMLFilterImpl {

        private Locator currentLocator;

        LocationAnnotatingFilter(XMLReader reader) {
            super(reader);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            currentLocator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qualName, Attributes attrs) throws SAXException {
            super.startElement(nsUri, localName, qualName, addLocationAttribute(attrs));
        }

        private final QName qnLoc = Locators.getLocatorAttributeQName();
        private String qnLocQualName = qnLoc.getPrefix() + ":" + qnLoc.getLocalPart();
        private boolean rootElement = true;
        private Attributes addLocationAttribute(Attributes attrs) {
            if (currentLocator != null) {
                AttributesImpl attrsNew = new AttributesImpl(attrs);
                StringBuilder sb = new StringBuilder();
                if (rootElement) {
                    sb.append('{');
                    sb.append(currentLocator.getSystemId());
                    sb.append('}');
                    rootElement = false;
                } else
                    sb.append("{}");
                sb.append(':');
                sb.append(Integer.toString(currentLocator.getLineNumber()));
                sb.append(':');
                sb.append(Integer.toString(currentLocator.getColumnNumber()));
                attrsNew.addAttribute(qnLoc.getNamespaceURI(), qnLoc.getLocalPart(), qnLocQualName, "CDATA", sb.toString());
                return attrsNew;
            } else
                return attrs;
        }

    }

    public static class Results {

        private static final String NOURI = "*URI NOT AVAILABLE*";
        private static final String NOENCODING = "*ENCODING NOT AVAILABLE*";
        private static final String NOMODEL = "*MODEL NOT AVAILABLE*";

        private String uriString;
        private boolean succeeded;
        private int code;
        private int flags;
        private int errorsExpected;
        private int errors;
        private int warningsExpected;
        private int warnings;
        private String modelName;
        private String encodingName;
        private QName root;

        public Results() {
            this.uriString = NOURI;
            this.succeeded = false;
            this.code = RV_USAGE;
            this.modelName = NOMODEL;
            this.encodingName = NOENCODING;
        }

        public Results(String uriString, int rv, int errorsExpected, int errors, int warningsExpected, int warnings, Model model, Charset encoding, QName root) {
            this.uriString = uriString;
            this.succeeded = rvPassed(rv);
            this.code = rvCode(rv);
            this.flags = rvFlags(rv);
            this.errorsExpected = errorsExpected;
            this.errors = errors;
            this.warningsExpected = warningsExpected;
            this.warnings = warnings;
            if (model != null)
                this.modelName = model.getName();
            else
                this.modelName = "unknown";
            if (encoding != null)
                this.encodingName = encoding.name();
            else
                this.encodingName = "unknown";
            this.root = root;
        }

        public String getURIString() {
            return uriString;
        }

        public boolean getSucceeded() {
            return succeeded;
        }

        public int getCode() {
            return code;
        }

        public int getFlags() {
            return flags;
        }

        public int getErrorsExpected() {
            return errorsExpected;
        }

        public int getErrors() {
            return errors;
        }

        public int getWarningsExpected() {
            return warningsExpected;
        }

        public int getWarnings() {
            return warnings;
        }

        public String getModelName() {
            return modelName;
        }

        public String getEncodingName() {
            return encodingName;
        }

        public QName getRoot() {
            return root;
        }

    }

    public static class ExternalParametersStore implements ExternalParameters {

        private Map<String, Object> parameters = new java.util.HashMap<String, Object>();

        public Object getParameter(String name) {
            return parameters.get(name);
        }

        public Object setParameter(String name, Object value) {
            return parameters.put(name, value);
        }

    }

}
