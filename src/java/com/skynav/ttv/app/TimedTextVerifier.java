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
 
package com.skynav.ttv.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Stack;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Models;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.Locators;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.Sniffer;

public class TimedTextVerifier implements VerifierContext, Reporter {

    public static final int RV_PASS                             = 0;
    public static final int RV_FAIL                             = 1;
    public static final int RV_USAGE                            = 2;

    public static final int RV_FLAG_ERROR_UNEXPECTED            = 0x000001;
    public static final int RV_FLAG_ERROR_EXPECTED_MATCH        = 0x000002;
    public static final int RV_FLAG_ERROR_EXPECTED_MISMATCH     = 0x000004;
    public static final int RV_FLAG_WARNING_UNEXPECTED          = 0x000010;
    public static final int RV_FLAG_WARNING_EXPECTED_MATCH      = 0x000020;
    public static final int RV_FLAG_WARNING_EXPECTED_MISMATCH   = 0x000040;

    private static final Model defaultModel = Models.getDefaultModel();

    // banner text
    private static final String banner = "Timed Text Verifier (TTV) [" + Version.CURRENT + "] Copyright 2013 Skynav, Inc.";

    // usage text
    private static final String repositoryInfo =
        "Source Repository: https://github.com/skynav/ttv";

    // usage text
    private static final String usage =
        "Usage: java -jar ttv.jar [options] URL*\n" +
        "  Short Options:\n" +
        "    -d                         - see --debug\n" +
        "    -q                         - see --quiet\n" +
        "    -v                         - see --verbose\n" +
        "    -?                         - see --help\n" +
        "  Long Options:\n" +
        "    --debug                    - enable debug output (may be specified multiple times to increase debug level)\n" +
        "    --debug-exceptions         - enable stack traces on exceptions (implies --debug)\n" +
        "    --disable-warnings         - disable warnings (both hide and don't count warnings)\n" +
        "    --expect-errors COUNT      - expect count errors or -1 meaning unspecified expectation (default: -1)\n" +
        "    --expect-warnings COUNT    - expect count warnings or -1 meaning unspecified expectation (default: -1)\n" +
        "    --external-extent EXTENT   - specify extent for document processing context\n" +
        "    --external-frame-rate RATE - specify frame rate for document processing context\n" +
        "    --help                     - show usage help\n" +
        "    --hide-warnings            - hide warnings (but count them)\n" +
        "    --model NAME               - specify model name (default: " + defaultModel.getName() + ")\n" +
        "    --no-warn-on TOKEN         - disable warning specified by warning TOKEN, where multiple instances of this option may be specified\n" +
        "    --quiet                    - don't show banner\n" +
        "    --show-models              - show built-in verification models (use with --verbose to show more details)\n" +
        "    --show-repository          - show source code repository information\n" +
        "    --show-warning-tokens      - show warning tokens (use with --verbose to show more details)\n" +
        "    --verbose                  - enable verbose output (may be specified multiple times to increase verbosity level)\n" +
        "    --treat-foreign-as TOKEN   - specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: " +
             ForeignTreatment.getDefault().name().toLowerCase() + ")\n" +
        "    --treat-warning-as-error   - treat warning as error (overrides --disable-warnings)\n" +
        "    --until-phase PHASE        - verify up to and including specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: " +
             Phase.getDefault().name().toLowerCase() + ")\n" +
        "    --warn-on TOKEN            - enable warning specified by warning TOKEN, where multiple instances of this option may be specified\n" +
        "  Non-Option Arguments:\n" +
        "    URL                        - an absolute or relative URL; if relative, resolved against current working directory\n" +
        "";

    // default warnings
    private static final Map<String,Boolean> defaultWarnings;
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
        { "references-non-standard-extension",          Boolean.FALSE,  "'ttp:extension' element references non-standard extension"},
        { "references-non-standard-profile",            Boolean.FALSE,  "'ttp:profile' element references non-standard profile from 'use' attribute"},
        { "references-other-extension-namespace",       Boolean.FALSE,  "'ttp:extensions' element references other extension namespace"},
    };
    static {
        defaultWarnings = new java.util.HashMap<String,Boolean>();
        for (Object[] spec : defaultWarningSpecifications) {
            defaultWarnings.put((String) spec[0], (Boolean) spec[1]);
        }
    }

    // options state
    private int debug;
    private boolean disableWarnings;
    private Set<String> disabledWarnings = new java.util.HashSet<String>();
    private Set<String> enabledWarnings = new java.util.HashSet<String>();
    private String expectedErrors;
    private String expectedWarnings;
    @SuppressWarnings("unused")
    private String externalExtent;
    @SuppressWarnings("unused")
    private String externalFrameRate;
    private boolean hideWarnings;
    private String modelName;
    private boolean quiet;
    private boolean showModels;
    private boolean showRepository;
    private boolean showWarningTokens;
    private String treatForeignAs;
    private String untilPhase;
    private boolean treatWarningAsError;
    private int verbose;

    // derived option state
    private Model model;
    private ForeignTreatment foreignTreatment;
    private Phase lastPhase;

    // global processing state
    private SchemaFactory schemaFactory;
    private Map<URL,Schema> schemas = new java.util.HashMap<URL,Schema>();
    private Map<String,Integer> results = new java.util.HashMap<String,Integer>();

    // per-resource processing state
    private Phase currentPhase;
    private String resourceUriString;
    private URI resourceUri;
    private ByteBuffer resourceBufferRaw;
    private int resourceExpectedErrors = -1;
    private int resourceErrors;
    private int resourceExpectedWarnings = -1;
    private int resourceWarnings;
    private Binder<Node> binder;
    private Object rootBinding;

    private enum ForeignTreatment {
        Error,
        Warning,
        Info,
        Allow;

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
    }

    @Override
    public Reporter getReporter() {
        return (Reporter) this;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    private static final QName qnEmpty = new QName("", "");

    @Override
    public QName getBindingElementName(Object value) {
        Node xmlNode = binder.getXMLNode(value);
        if (xmlNode != null) {
            Object jaxbBinding = binder.getJAXBNode(xmlNode);
            if (jaxbBinding instanceof JAXBElement<?>) {
                JAXBElement<?> jaxbNode = (JAXBElement<?>) jaxbBinding;
                if (jaxbNode != null)
                    return jaxbNode.getName();
            } else
                return new QName(xmlNode.getNamespaceURI(), xmlNode.getLocalName());
        }
        return qnEmpty;
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
            else if (rootBinding != null)
                return model.getSemanticsVerifier().findBindingElement(rootBinding, parentNode);
            else
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
    public String message(String message) {
        if ((message != null) && (message.length() > 0)) {
            if (message.charAt(0) == '{')
                return message;
            else if (resourceUri != null)
                return "{" + resourceUri.toString() + "}: " + message;
            else if (resourceUriString != null)
                return "{" + resourceUriString + "}: " + message;
            else
                return message;
        }
        return "*** Empty Message ***";
    }

    @Override
    public String message(Locator locator, String message) {
        String sysid = (locator != null) ? locator.getSystemId() : null;
        if ((sysid == null) || (sysid.length() == 0)) {
            if (resourceUri != null)
                sysid = resourceUri.toString();
            else if (resourceUriString != null)
                sysid = resourceUriString;
        }
        StringBuffer sb = new StringBuffer();
        if (sysid != null) {
            sb.append('{');
            sb.append(sysid);
            sb.append('}');
        }
        if (locator != null) {
            sb.append(':');
            sb.append('[');
            sb.append(locator.getLineNumber());
            sb.append(',');
            sb.append(locator.getColumnNumber());
            sb.append(']');
        }
        if ((message != null) && (message.length() > 0)) {
            sb.append(':');
            sb.append(message);
        }
        return sb.toString();
    }

    @Override
    public void logError(String message) {
        System.out.println("[E]:" + message);
        ++resourceErrors;
    }

    @Override
    public void logError(Locator locator, String message) {
        logError(message(locator, message));
    }

    private Locator  extractLocator(SAXParseException e) {
        LocatorImpl locator = new LocatorImpl();
        locator.setSystemId(e.getSystemId());
        locator.setLineNumber(e.getLineNumber());
        locator.setColumnNumber(e.getColumnNumber());
        return locator;
    }

    private String extractMessage(SAXParseException e) {
        return message(extractLocator(e), e.getMessage());
    }

    private String extractMessage(Throwable e) {
        if ((e.getCause() != null) && (e.getCause() != e))
            return extractMessage(e.getCause());
        else if (e instanceof SAXParseException)
            return extractMessage((SAXParseException) e);
        else
            return "{" + resourceUri + "}: " + e.getMessage();
    }

    @Override
    public void logError(Exception e) {
        logError(extractMessage(e));
        logDebug(e);
    }

    @Override
    public boolean isWarningEnabled(String token) {
        boolean enabled = defaultWarnings.get(token);
        if (enabledWarnings.contains(token) || enabledWarnings.contains("all"))
            enabled = true;
        if (disabledWarnings.contains(token) || disabledWarnings.contains("all"))
            enabled = false;
        return enabled;
    }

    @Override
    public boolean logWarning(String message) {
        if (!disableWarnings) {
            if (!hideWarnings)
                System.out.println("[W]:" + message);
            ++resourceWarnings;
        }
        return treatWarningAsError;
    }

    @Override
    public boolean logWarning(Locator locator, String message) {
        return logWarning(message(locator, message));
    }

    private boolean logWarning(Exception e) {
        boolean treatedAsError = logWarning(extractMessage(e));
        logDebug(e);
        return treatedAsError;
    }

    @Override
    public void logInfo(String message) {
        if (verbose > 0) {
            System.out.println("[I]:" + message);
        }
    }

    @Override
    public void logInfo(Locator locator, String message) {
        logInfo(message(locator, message));
    }

    @Override
    public void logDebug(String message) {
        if (debug > 0) {
            System.out.println("[D]:" + message);
        }
    }

    @Override
    public void logDebug(Locator locator, String message) {
        logDebug(message(locator, message));
    }

    private void logDebug(Exception e) {
        if (debug > 1) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logDebug(sw.toString());
        }
    }

    private int parseLongOption(String args[], int index) {
        String option = args[index];
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("debug")) {
            if (debug < 1)
                debug = 1;
            else
                debug += 1;
        } else if (option.equals("debug-exceptions")) {
            if (debug < 2)
                debug = 2;
        } else if (option.equals("disable-warnings")) {
            disableWarnings = true;
        } else if (option.equals("expect-errors")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            expectedErrors = args[++index];
        } else if (option.equals("expect-warnings")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            expectedWarnings = args[++index];
        } else if (option.equals("external-extent")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            externalExtent = args[++index];
        } else if (option.equals("external-frame-rate")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            externalFrameRate = args[++index];
        } else if (option.equals("help")) {
            throw new ShowUsageException();
        } else if (option.equals("hide-warnings")) {
            hideWarnings = true;
        } else if (option.equals("model")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            modelName = args[++index];
        } else if (option.equals("no-warn-on")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            String token = args[++index];
            if (!defaultWarnings.containsKey(token))
                throw new InvalidOptionUsageException("--" + option, "token '" + token + "' is not a recognized warning token");
            disabledWarnings.add(token);
        } else if (option.equals("quiet")) {
            quiet = true;
        } else if (option.equals("show-models")) {
            showModels = true;
        } else if (option.equals("show-repository")) {
            showRepository = true;
        } else if (option.equals("show-warning-tokens")) {
            showWarningTokens = true;
        } else if (option.equals("treat-foreign-as")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            treatForeignAs = args[++index];
        } else if (option.equals("treat-warning-as-error")) {
            treatWarningAsError = true;
        } else if (option.equals("until-phase")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            untilPhase = args[++index];
        } else if (option.equals("verbose")) {
            verbose += 1;
        } else if (option.equals("warn-on")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            String token = args[++index];
            if (!defaultWarnings.containsKey(token))
                throw new InvalidOptionUsageException("--" + option, "token '" + token + "' is not a recognized warning token");
            enabledWarnings.add(token);
        } else
            throw new UnknownOptionException("--" + option);
        return index + 1;
    }

    private int parseShortOption(String args[], int index) {
        String option = args[index];
        assert option.length() == 2;
        option = option.substring(1);
        switch (option.charAt(0)) {
        case 'd':
            debug += 1;
            break;
        case 'q':
            quiet = true;
            break;
        case 'v':
            verbose += 1;
            break;
        case '?':
            throw new ShowUsageException();
        default:
            throw new UnknownOptionException("-" + option);
        }
        return index + 1;
    }

    private void processDerivedOptions() {
        Model model;
        if (modelName != null) {
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
        if (untilPhase != null) {
            try {
                lastPhase = Phase.valueOfIgnoringCase(untilPhase);
            } catch (IllegalArgumentException e) {
                throw new InvalidOptionUsageException("until-phase", "unknown token: " + untilPhase);
            }
        } else
            lastPhase = Phase.getDefault();
    }

    private List<String> processOptionsAndArgs(List<String> nonOptionArgs) {
        processDerivedOptions();
        return nonOptionArgs;
    }

    private List<String> parseArgs(String[] args) {
        List<String> nonOptionArgs = new java.util.ArrayList<String>();
        int nonOptionIndex = -1;
        for (int i = 0; i < args.length;) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                case '-':
                    i = parseLongOption(args, i);
                    break;
                default:
                    if (arg.length() != 2)
                        throw new UnknownOptionException(arg);
                    else
                        i = parseShortOption(args, i);
                    break;
                }
            } else {
                nonOptionIndex = i;
                break;
            }
        }
        if (nonOptionIndex >= 0) {
            for (int i = nonOptionIndex; i < args.length; ++i)
                nonOptionArgs.add(args[i]);
        }
        return processOptionsAndArgs(nonOptionArgs);
    }

    private void showBanner() {
        if (!quiet)
            System.out.println(banner);
    }

    private void showProcessingInfo() {
        if (verbose >  0) {
            if (treatWarningAsError)
                logInfo("Warnings are treated as errors.");
            else if (disableWarnings)
                logInfo("Warnings are disabled.");
            else if (hideWarnings)
                logInfo("Warnings are hidden.");
        }
    }

    private void showModels() {
        String defaultModelName = Models.getDefaultModel().getName();
        StringBuffer sb = new StringBuffer();
        sb.append("Verification Models:\n");
        for (String modelName : Models.getModelNames()) {
            sb.append("  ");
            sb.append(modelName);
            if (modelName.equals(defaultModelName)) {
                sb.append(" (default)");
            }
            sb.append('\n');
            if (verbose > 0) {
                Model model = Models.getModel(modelName);
                String schemaResourceName = model.getSchemaResourceName();
                if (schemaResourceName != null) {
                    sb.append("    XSD: ");
                    sb.append(schemaResourceName);
                    sb.append('\n');
                }
            }
        }
        System.out.println(sb.toString());
    }

    private void showRepository() {
        System.out.println(repositoryInfo);
    }

    private void showWarningTokens() {
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
            if (verbose > 0) {
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
        System.out.println(sb.toString());
    }

    private URI getCWDAsURI() {
        try {
            return new URI("file://" + new File(".").getCanonicalPath().replace(File.separatorChar, '/') + File.separatorChar);
        } catch (IOException e) {
            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private URI resolve(String uriString) {
        try {
            URI uri = new URI(uriString);
            if (!uri.isAbsolute()) {
                URI uriCurrentDirectory = getCWDAsURI();
                if (uriCurrentDirectory != null) {
                    URI uriAbsolute = uriCurrentDirectory.resolve(uri);
                    if (uriAbsolute != null)
                        uri = uriAbsolute;
                } else {
                    logError(message("Unable to resolve relative URI: {" + uriString + "}"));
                    uri = null;
                }
            }
            return uri;
        } catch (URISyntaxException e) {
            logError(message("Bad URI syntax: {" + uriString + "}"));
            return null;
        }
    }

    private ByteBuffer readResource(URI uri) {
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
            logError(e);
            os = null;
        } finally {
            if (is != null)
                try { is.close(); } catch (Exception e) {}
        }
        return (os != null) ? ByteBuffer.wrap(os.toByteArray()) : null;
    }

    private static final List<Charset> permittedEncodings;

    static {
        permittedEncodings = new java.util.ArrayList<Charset>();
        try { permittedEncodings.add(Charset.forName("US-ASCII")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("ISO-8859-1")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-8")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-16LE")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-16BE")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-16")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-32LE")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-32BE")); } catch (RuntimeException e) {}
        try { permittedEncodings.add(Charset.forName("UTF-32")); } catch (RuntimeException e) {}
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

    private CharBuffer decodeResource(ByteBuffer rawBuffer, Charset charset, int bomLength) {
        ByteBuffer bb = rawBuffer;
        bb.position(bomLength);
        List<CharBuffer> charBuffers = new java.util.ArrayList<CharBuffer>();
        do {
            CharsetDecoder cd = charset.newDecoder();
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
                        logError(decoderMessage(r, bb, "Malformed " + charset.name()));
                        return null;
                    } else if (r.isUnmappable()) {
                        logWarning(decoderMessage(r, bb, "Unmappable " + charset.name()));
                        return null;
                    } else if (r.isError()) {
                        logError(decoderMessage(r, bb, "Can't decode " + charset.name()));
                        return null;
                    }
                } catch (Exception e) {
                    logError(e);
                    return null;
                }
            }
        } while (false);
        rawBuffer.rewind();
        return concatenateBuffers(charBuffers);
    }

    private String decoderMessage(CoderResult r, ByteBuffer bb, String prefix) {
        return message(prefix + " at byte offset " + bb.position() + ", " + r.length() + " byte" + ((r.length() > 1) ? "s" : "") + ".");
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

    private static Charset asciiCharset;

    static {
        try {
            asciiCharset = Charset.forName("US-ASCII");
        } catch (RuntimeException e) {
            asciiCharset = null;
        }
    }

    private void setResource(String uri) {
        resourceUriString = uri;
    }

    private void setResource(URI uri) {
        resourceUri = uri;
    }

    private void setResource(Charset charset, CharBuffer buffer, ByteBuffer bufferRaw) {
        resourceBufferRaw = bufferRaw;
        if (expectedErrors != null)
            resourceExpectedErrors = parseAnnotationAsInteger(expectedErrors, -1);
        if (expectedWarnings != null)
            resourceExpectedWarnings = parseAnnotationAsInteger(expectedWarnings, -1);
        resourceErrors = 0;
        resourceWarnings = 0;
        binder = null;
    }

    private boolean verifyResource() {
        currentPhase = Phase.Resource;
        if (!lastPhase.isEnabled(Phase.Resource)) {
            logInfo("Skipping resource presence and encoding verification phase (" + currentPhase.ordinal() + ").");
            return true;
        } else
            logInfo("Verifying resource presence and encoding phase (" + currentPhase.ordinal() + ")...");
        URI uri = resolve(resourceUriString);
        if (uri != null) {
            setResource(uri);
            ByteBuffer bytesBuffer = readResource(uri);
            if (bytesBuffer != null) {
                Object[] sniffOutputParameters = new Object[] { Integer.valueOf(0) };
                Charset charset = Sniffer.sniff(bytesBuffer, asciiCharset, sniffOutputParameters);
                if (isPermittedEncoding(charset.name())) {
                    int bomLength = (Integer) sniffOutputParameters[0];
                    CharBuffer charsBuffer = decodeResource(bytesBuffer, charset, bomLength);
                    if (charsBuffer != null) {
                        setResource(charset, charsBuffer, bytesBuffer);
                        logInfo("Resource encoding sniffed as " + charset.name() + ".");
                        logInfo("Resource length " + bytesBuffer.limit() + " bytes, decoded as " + charsBuffer.limit() + " Java characters (char).");
                    }
                } else
                    logError(message("Encoding " + charset.name() + " is not permitted"));
            }
        }
        return resourceErrors == 0;
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

    private boolean verifyWellFormedness() {
        currentPhase = Phase.WellFormedness;
        if (!lastPhase.isEnabled(Phase.WellFormedness)) {
            logInfo("Skipping XML well-formedness verification phase (" + currentPhase.ordinal() + ").");
            return true;
        } else
            logInfo("Verifying XML well-formedness phase (" + currentPhase.ordinal() + ")...");
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setValidating(false);
            pf.setNamespaceAware(true);
            SAXParser p = pf.newSAXParser();
            p.parse(openStream(resourceBufferRaw), new DefaultHandler() {
                public void error(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness error
                    logError(e);
                    throw new WellFormednessErrorException(e);
                }
                public void fatalError(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness error
                    logError(e);
                    throw new WellFormednessErrorException(e);
                }
                public void warning(SAXParseException e) {
                    // ensure parsing is terminated on well-formedness warning treated as error
                    if (logWarning(e))
                        throw new WellFormednessErrorException(e);
                }
            }, resourceUri.toString());
        } catch (ParserConfigurationException e) {
            logError(e);
        } catch (WellFormednessErrorException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXParseException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return resourceErrors == 0;
    }

    private SchemaFactory getSchemaFactory() {
        synchronized (this) {
            if (schemaFactory == null) {
                schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            }
        }
        return schemaFactory;
    }

    private Schema loadSchema(URL url) throws SchemaValidationErrorException {
        SchemaFactory sf = getSchemaFactory();
        sf.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException e) {
                logError(e);
                throw new SchemaValidationErrorException(e);
            }
            public void fatalError(SAXParseException e) {
                logError(e);
                throw new SchemaValidationErrorException(e);
            }
            public void warning(SAXParseException e) {
                if (logWarning(e))
                    throw new SchemaValidationErrorException(e);
            }
        });
        try {
            logDebug("Loading (and validting) schema at " + url + "...");
            return sf.newSchema(url);
        } catch (SAXException e) {
            logError(e);
            throw new SchemaValidationErrorException(e);
        }
    }

    private Schema getSchema(URL url) throws SchemaValidationErrorException {
        synchronized (this) {
            if (!schemas.containsKey(url)) {
                schemas.put(url, loadSchema(url));
            }
        }
        return schemas.get(url);
    }

    private Schema getSchema(String resourceName) throws SchemaValidationErrorException {
        logDebug("Searching for built-in schema at " + resourceName + "...");
        try {
            URL urlSchema = null;
            Enumeration<URL> resources = getClass().getClassLoader().getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String urlPath = url.getPath();
                if (urlPath.indexOf(resourceName) == (urlPath.length() - resourceName.length())) {
                    logDebug("Found resource match at " + url + ".");
                    urlSchema = url;
                    break;
                } else {
                    logDebug("Skipping partial resource match at " + url + ".");
                }
            }
            if (urlSchema == null) {
                String message = "Can't find schema resource " + resourceName + ".";
                logDebug(message);
                throw new SchemaValidationErrorException(new MissingResourceException(message, getClass().getName(), resourceName));
            }
            return getSchema(urlSchema);
        } catch (IOException e) {
            throw new SchemaValidationErrorException(e);
        }
    }

    private Schema getSchema() throws SchemaValidationErrorException {
        return getSchema(model.getSchemaResourceName());
    }

    private boolean verifyValidity() {
        currentPhase = Phase.Validity;
        if (!lastPhase.isEnabled(Phase.Validity)) {
            logInfo("Skipping XSD validity verification phase (" + currentPhase.ordinal() + ").");
            return true;
        } else
            logInfo("Verifying XSD validity phase (" + currentPhase.ordinal() + ")...");
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            XMLReader filter = new ForeignVocabularyFilter(reader, model.getNamespaceUri().toString(), foreignTreatment);
            SAXSource source = new SAXSource(filter, new InputSource(openStream(resourceBufferRaw)));
            source.setSystemId(resourceUri.toString());
            Validator v = getSchema().newValidator();
            v.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException e) {
                    // don't terminated validation on validation error
                    logError(e);
                }
                public void fatalError(SAXParseException e) {
                    // don't terminated validation on validation error
                    logError(e);
                }
                public void warning(SAXParseException e) {
                    // don't terminated validation on validation warning treated as error
                    logWarning(e);
                }
            });
            v.validate(source);
        } catch (ParserConfigurationException e) {
            logError(e);
        } catch (SchemaValidationErrorException e) {
            logError(e);
        } catch (ForeignVocabularyException e) {
            // Already logged error via foreign vocabulary filter
        } catch (SAXParseException e) {
            // Already logged error via default handler overrides above.
        } catch (SAXException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return resourceErrors == 0;
    }

    private QName getXmlElementDecl(Class<?> jaxbClass, String creatorMethod) {
        try {
            Class<?> ofc = jaxbClass.getClassLoader().loadClass(jaxbClass.getPackage().getName() + ".ObjectFactory");
            return ((JAXBElement<?>) ofc.getDeclaredMethod(creatorMethod, jaxbClass).invoke(ofc.newInstance(), new Object[] { null } )).getName();
        } catch (Exception e) {
            return new QName("", "");
        }
    }

    private String getContentClassNames(Map<Class<?>,String> contentClasses) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (Class<?> contentClass : contentClasses.keySet()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append('<');
            sb.append(getXmlElementDecl(contentClass, contentClasses.get(contentClass)));
            sb.append('>');
        }
        sb.append('}');
        return sb.toString();
    }

    private boolean verifyRootElement(JAXBElement<?> root, Map<Class<?>,String> rootClasses) {
        Object contentObject = root.getValue();
        for (Class<?> rootClass : rootClasses.keySet()) {
            if (rootClass.isInstance(contentObject))
                return true;
        }
        logError(message(Locators.getLocator(contentObject),
            "Unexpected root element <" + root.getName() + ">," + " expected one of " + getContentClassNames(rootClasses) + "."));
        return false;
    }

    private void processAnnotations(Object root) {
        NamedNodeMap attributes = getXMLNode(root).getAttributes();
        String annotationsNamespace = Annotations.getNamespace();
        if (attributes != null) {
            for (int i = 0, n = attributes.getLength(); i < n; ++i) {
                Node attr = attributes.item(i);
                if (attr.getNamespaceURI().equals(annotationsNamespace)) {
                    String localName = attr.getLocalName();
                    String value = attr.getNodeValue();
                    if (localName.equals("expectedErrors"))
                        resourceExpectedErrors = parseAnnotationAsInteger(value, -1);
                    else if (localName.equals("expectedWarnings"))
                        resourceExpectedWarnings = parseAnnotationAsInteger(value, -1);
                    else if (localName.equals("warnOn")) {
                        String[] tokens = value.split("\\s+");
                        for (String token : tokens) {
                            if (defaultWarnings.containsKey(token))
                                enabledWarnings.add(token);
                        }
                    } else if (localName.equals("noWarnOn")) {
                        String[] tokens = value.split("\\s+");
                        for (String token : tokens) {
                            if (defaultWarnings.containsKey(token))
                                disabledWarnings.add(token);
                        }
                    }
                }
            }
        }
    }

    private int parseAnnotationAsInteger(String annotation, int defaultValue) {
        try {
            return Integer.parseInt(annotation);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean verifySemantics() {
        currentPhase = Phase.Semantics;
        if (!lastPhase.isEnabled(Phase.Semantics)) {
            logInfo("Skipping semantics verification phase (" + currentPhase.ordinal() + ").");
            return true;
        } else
            logInfo("Verifying semantics phase (" + currentPhase.ordinal() + ")...");
        try {
            // construct source pipeline
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            ForeignVocabularyFilter filter1 = new ForeignVocabularyFilter(reader, model.getNamespaceUri().toString(), ForeignTreatment.Allow);
            LocationAnnotatingFilter filter2 = new LocationAnnotatingFilter(filter1);
            SAXSource source = new SAXSource(filter2, new InputSource(openStream(resourceBufferRaw)));
            source.setSystemId(resourceUri.toString());

            // construct annotated infoset destination
            DOMResult result = new DOMResult();
            result.setSystemId(resourceUri.toString());

            // transform into annotated infoset
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.newTransformer().transform(source, result);

            // unmarshall annotated infoset
            JAXBContext context = JAXBContext.newInstance(model.getJAXBContextPath());
            Binder<Node> binder = context.createBinder();
            Object unmarshalled = binder.unmarshal(result.getNode());

            // retain reference to binder at instance scope for error reporter utilities
            this.binder = binder;

            // verify root then remaining semantics
            if (unmarshalled == null)
                logError(message("Missing root element."));
            else if (!(unmarshalled instanceof JAXBElement))
                logError(message("Unexpected root element, can't introspect non-JAXBElement"));
            else {
                JAXBElement<?> root = (JAXBElement<?>) unmarshalled;
                Documents.assignIdAttributes(binder.getXMLNode(root).getOwnerDocument(), model.getIdAttributes());
                if (verifyRootElement(root, model.getRootClasses())) {
                    this.rootBinding = root.getValue();
                    processAnnotations(this.rootBinding);
                    model.getSemanticsVerifier().verify(this.rootBinding, this);
                }
            }
        } catch (UnmarshalException e) {
            logError(e);
        } catch (TransformerFactoryConfigurationError e) {
            logError(new Exception(e));
        } catch (Exception e) {
            logError(e);
        }
        return resourceErrors == 0;
    }

    private int verify(String uri) {
        logInfo("Verifying {" + uri + "}.");
        do {
            setResource(uri);
            if (!verifyResource())
                break;
            if (!verifyWellFormedness())
                break;
            if (!verifyValidity())
                break;
            if (!verifySemantics())
                break;
        } while (false);
        int rv = rvValue();
        logInfo((rvPassed(rv) ? "Passed" : "Failed") + resultDetails() + ".");
        results.put(uri, rv);
        return rv;
    }

    private int rvValue() {
        int code = RV_PASS;
        int flags = 0;
        if (resourceErrors > 0) {
            if (resourceExpectedErrors < 0) {
                code = RV_FAIL;
                flags |= RV_FLAG_ERROR_UNEXPECTED;
            } else if (resourceErrors != resourceExpectedErrors) {
                code = RV_FAIL;
                flags |= RV_FLAG_ERROR_EXPECTED_MISMATCH;
            } else {
                code = RV_PASS;
                flags |= RV_FLAG_ERROR_EXPECTED_MATCH;
            }
        }
        if (resourceWarnings > 0) {
            if (resourceExpectedWarnings < 0) {
                flags |= RV_FLAG_WARNING_UNEXPECTED;
            } else if (resourceWarnings != resourceExpectedWarnings) {
                flags |= RV_FLAG_WARNING_EXPECTED_MISMATCH;
            } else
                flags |= RV_FLAG_WARNING_EXPECTED_MATCH;
        }
        return ((flags & 0x7FFFFF) << 8) | (code & 0xFF);
    }

    private boolean rvPassed(int rv) {
        return rvCode(rv) == RV_PASS;
    }

    private int rvCode(int rv) {
        return (rv & 0xFF);
    }

    private int rvFlags(int rv) {
        return ((rv >> 8) & 0x7FFFFF);
    }

    private String resultDetails() {
        StringBuffer details = new StringBuffer();
        if (resourceErrors > 0) {
            details.append(", with");
            if (resourceExpectedErrors < 0) {
                details.append(' ');
                details.append(resourceErrors);
                details.append(' ');
                details.append(plural("error", resourceErrors));
            } else if (resourceErrors == resourceExpectedErrors) {
                details.append(' ');
                details.append(resourceErrors);
                details.append(" expected ");
                details.append(plural("error", resourceErrors));
            } else {
                details.append(' ');
                details.append(resourceErrors);
                details.append(' ');
                details.append(plural("error", resourceErrors));
                details.append(" but expected ");
                details.append(resourceExpectedErrors);
                details.append(' ');
                details.append(plural("error", resourceExpectedErrors));
            }
        }
        if (resourceWarnings > 0) {
            details.append(details.length() > 0 ? ", and with" : ", with");
            if (resourceExpectedWarnings < 0) {
                details.append(' ');
                details.append(resourceWarnings);
                details.append(' ');
                details.append(plural("warning", resourceWarnings));
            } else if (resourceWarnings == resourceExpectedWarnings) {
                details.append(' ');
                details.append(resourceWarnings);
                details.append(" expected ");
                details.append(plural("warning", resourceWarnings));
            } else {
                details.append(' ');
                details.append(resourceWarnings);
                details.append(' ');
                details.append(plural("warning", resourceWarnings));
                details.append(" but expected ");
                details.append(resourceExpectedWarnings);
                details.append(' ');
                details.append(plural("warning", resourceExpectedWarnings));
            }
        }
        return details.toString();
    }

    private String plural(String noun, int count) {
        if (count == 1)
            return noun;
        else
            return noun + "s";
    }

    private int verify(List<String> nonOptionArgs) {
        int numFailure = 0;
        int numSuccess = 0;
        for (String uri : nonOptionArgs) {
            switch (rvCode(verify(uri))) {
            case RV_PASS:
                ++numSuccess;
                break;
            case RV_FAIL:
                ++numFailure;
                break;
            default:
                break;
            }
        }
        if (verbose > 0) {
            StringBuffer sb = new StringBuffer();
            if (numSuccess > 0)
                sb.append("Passed " + numSuccess);
            if (numFailure > 0) {
                if (numSuccess > 0)
                    sb.append(", ");
                sb.append("Failed " + numFailure);
            }
            if (sb.length() > 0)
                sb.append(" resources.");
            logInfo(sb.toString());
        }
        return numFailure > 0 ? 1 : 0;
    }

    public int run(String[] args) {
        int rv = 0;
        try {
            List<String> nonOptionArgs = parseArgs(args);
            showBanner();
            if (showModels)
                showModels();
            else if (showRepository)
                showRepository();
            else if (showWarningTokens)
                showWarningTokens();
            else {
                if (nonOptionArgs.size() > 1) {
                    if (expectedErrors != null)
                        throw new InvalidOptionUsageException("expect-errors", "must not specify more than one URL with this option");
                    if (expectedWarnings != null)
                        throw new InvalidOptionUsageException("expect-warnings", "must not specify more than one URL with this option");
                }
                showProcessingInfo();
                rv = verify(nonOptionArgs);
            }
        } catch (ShowUsageException e) {
            System.out.println(banner);
            System.out.println(usage);
            rv = RV_USAGE;
        } catch (UsageException e) {
            System.out.println("Usage: " + e.getMessage());
            rv = RV_USAGE;
        }
        return rv;
    }

    public Map<String,Integer> getResults() {
        return results;
    }

    public int getResultCode(String uri) {
        if (results.containsKey(uri))
            return rvCode(results.get(uri));
        else
            return -1;
    }

    public int getResultFlags(String uri) {
        if (results.containsKey(uri))
            return rvFlags(results.get(uri));
        else
            return -1;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextVerifier().run(args));
    }

    private static class UsageException extends RuntimeException {
        static final long serialVersionUID = 0;
        UsageException(String message) {
            super(message);
        }
    }

    private static class UnknownOptionException extends UsageException {
        static final long serialVersionUID = 0;
        UnknownOptionException(String option) {
            super("unknown option: " + option);
        }
    }

    private static class MissingOptionArgumentException extends UsageException {
        static final long serialVersionUID = 0;
        MissingOptionArgumentException(String option) {
            super("missing option argument: " + option);
        }
    }

    private static class InvalidOptionUsageException extends UsageException {
        static final long serialVersionUID = 0;
        InvalidOptionUsageException(String option, String details) {
            super("invalid option argument: " + option + ": " + details);
        }
    }

    private static class ShowUsageException extends UsageException {
        static final long serialVersionUID = 0;
        ShowUsageException() {
            super("show usage");
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

    private static class ForeignVocabularyException extends RuntimeException {
        static final long serialVersionUID = 0;
        ForeignVocabularyException(QName name) {
            super(name.toString());
        }
    }

    private class ForeignVocabularyFilter extends XMLFilterImpl {

        private static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";

        private String namespace;
        private ForeignTreatment foreignTreatment;

        private Stack<QName> nameStack = new Stack<QName>();
        private boolean inForeign;
        private Locator currentLocator;

        ForeignVocabularyFilter(XMLReader reader, String namespace, ForeignTreatment foreignTreatment) {
            super(reader);
            this.namespace = namespace;
            this.foreignTreatment = foreignTreatment;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            currentLocator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qualName, Attributes attrs) throws SAXException {
            if (foreignTreatment == ForeignTreatment.Allow)
                super.startElement(nsUri, localName, qualName, attrs);
            else if (!inForeign && isNonForeignNamespace(nsUri))
                super.startElement(nsUri, localName, qualName, removeForeign(attrs));
            else if (foreignTreatment == ForeignTreatment.Error)
                throw new ForeignVocabularyException(new QName(nsUri, localName));
            else {
                QName qn = new QName(nsUri, localName);
                nameStack.push(qn);
                inForeign = true;
                if (logPruning("Pruning element in foreign namespace: <" + qn + ">."))
                    throw new ForeignVocabularyException(new QName(nsUri, localName));
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

        private Attributes removeForeign(Attributes attrs) {
            boolean hasForeign = false;
            for (int i = 0, n = attrs.getLength(); i < n && !hasForeign; ++i) {
                String nsUri = attrs.getURI(i);
                if (isForeignNamespace(nsUri) && !isAnnotationNamespace(nsUri))
                    hasForeign = true;
            }
            if (hasForeign) {
                AttributesImpl attrsNew = new AttributesImpl();
                for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                    String nsUri = attrs.getURI(i);
                    if (isNonForeignNamespace(nsUri) || isAnnotationNamespace(nsUri))
                        attrsNew.addAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
                    else if (logPruning("Pruning attribute in foreign namespace: <" + new QName(attrs.getURI(i), attrs.getLocalName(i)) + ">."))
                        throw new ForeignVocabularyException(new QName(nsUri, attrs.getLocalName(i)));
                }
                return attrsNew;
            } else
                return attrs;
        }

        private boolean isNonForeignNamespace(String nsUri) {
            if (nsUri == null)
                return true;
            else if (nsUri.length() == 0)
                return true;
            else if (nsUri.indexOf(namespace) == 0)
                return true;
            else if (nsUri.indexOf(xmlNamespace) == 0)
                return true;
            else
                return false;
        }

        private boolean isForeignNamespace(String nsUri) {
            return !isNonForeignNamespace(nsUri);
        }

        private boolean isAnnotationNamespace(String nsUri) {
            return nsUri.indexOf(Annotations.getNamespace()) == 0;
        }

        private boolean logPruning(String message) {
            if (foreignTreatment == ForeignTreatment.Error) {
                logError(currentLocator, message);
                return true;
            } if (foreignTreatment == ForeignTreatment.Warning) {
                if (isWarningEnabled("foreign")) {
                    if (logWarning(currentLocator, message))
                        return true;
                }
            } else if (foreignTreatment == ForeignTreatment.Info)
                logInfo(currentLocator, message);
            return false;
        }
    }

    private class LocationAnnotatingFilter extends XMLFilterImpl {

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

}
