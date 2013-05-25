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
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

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

import com.skynav.xml.helpers.Sniffer;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Models;
import com.skynav.ttv.util.ErrorReporter;

public class TimedTextValidator implements ErrorReporter {

    private static final Model defaultModel = Models.getDefaultModel();

    // banner text
    private static final String banner = "Timed Text Validator (TTV) " + Version.CURRENT;

    // usage text
    private static final String usage =
        "Usage: java -jar ttv.jar [options] URL*\n" +
        "  Short Options:\n" +
        "    -d                       - see --debug\n" +
        "    -q                       - see --quiet\n" +
        "    -v                       - see --verbose\n" +
        "    -?                       - see --help\n" +
        "  Long Options:\n" +
        "    --debug                  - enable debug output (may be specified multiple times to increase debug level)\n" +
        "    --debug-exceptions       - enable stack traces on exceptions (implies --debug)\n" +
        "    --disable-warnings       - disable warnings\n" +
        "    --help                   - show usage help\n" +
        "    --list-models            - list known models\n" +
        "    --model NAME             - specify model name (default: " + defaultModel.getName() + ")\n" +
        "    --quiet                  - don't show banner\n" +
        "    --verbose                - enable verbose output (may be specified multiple times to increase verbosity level)\n" +
        "    --treat-warning-as-error - treat warning as error\n" +
        "  Non-Option Arguments:\n" +
        "    URL                      - an absolute or relative URL; if relative, resolved against current working directory\n";

    // options state
    private int debug;
    private boolean disableWarnings;
    private boolean listModels;
    private String modelName;
    private boolean quiet;
    private boolean treatWarningAsError;
    private int verbose;

    // derived option state
    private Model model;

    // global processing state
    private SchemaFactory schemaFactory;
    private Map<URL,Schema> schemas = new java.util.HashMap<URL,Schema>();

    // per-resource processing state
    private URI resourceUri;
    @SuppressWarnings("unused")
    private Charset resourceEncoding;
    private ByteBuffer resourceBufferRaw;
    @SuppressWarnings("unused")
    private Object resourceRoot;
    @SuppressWarnings("unused")
    private Map<Object,Locator> resourceLocators;
    private int resourceErrors;
    private int resourceWarnings;

    public TimedTextValidator() {
    }

    @Override
    public String message(String message) {
        if ((message != null) && (message.length() > 0)) {
            if (message.charAt(0) == '{')
                return message;
            else if (this.resourceUri != null)
                return "{" + this.resourceUri.toString() + "}: " + message;
        }
        return "*** Empty Message ***";
    }

    @Override
    public String message(Locator locator, String message) {
        return "{" + locator.getSystemId() + "}:" +
               "[" + locator.getLineNumber() + ":" + locator.getColumnNumber() + "]: " + message;
    }

    @Override
    public void logError(String message) {
        System.out.println("E:" + message);
        ++this.resourceErrors;
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
            return "{" + this.resourceUri + "}: " + e.getMessage();
    }

    @Override
    public void logError(Exception e) {
        logError(extractMessage(e));
        logDebug(e);
    }

    @Override
    public void logWarning(String message) {
        if (!this.disableWarnings) {
            if (this.treatWarningAsError)
                logError(message);
            else {
                System.out.println("W:" + message);
                ++this.resourceWarnings;
            }
        }
    }

    private void logWarning(Locator locator, String message) {
        logWarning(message(locator, message));
    }

    private void logWarning(Exception e) {
        logWarning(extractMessage(e));
        logDebug(e);
    }

    @Override
    public void logInfo(String message) {
        if (this.verbose > 0) {
            System.out.println("I:" + message);
        }
    }

    private void logDebug(String message) {
        if (this.debug > 0) {
            System.out.println("T:" + message);
        }
    }

    private void logDebug(Exception e) {
        if (this.debug > 1) {
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
            this.debug += 1;
        } else if (option.equals("debug-exceptions")) {
            this.debug += 2;
        } else if (option.equals("disable-warnings")) {
            this.disableWarnings = true;
        } else if (option.equals("help")) {
            throw new ShowUsageException();
        } else if (option.equals("list-models")) {
            this.listModels = true;
        } else if (option.equals("model")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            this.modelName = args[++index];
        } else if (option.equals("quiet")) {
            this.quiet = true;
        } else if (option.equals("treat-warning-as-error")) {
            this.treatWarningAsError = true;
        } else if (option.equals("verbose")) {
            this.verbose += 1;
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
            this.debug += 1;
            break;
        case 'q':
            this.quiet = true;
            break;
        case 'v':
            this.verbose += 1;
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
        if (this.modelName != null) {
            model = Models.getModel(this.modelName);
            if (model == null)
                throw new InvalidOptionUsageException("model", "unknown model: " + modelName);
        } else
            model = Models.getDefaultModel();
        this.model = model;
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

    private int listModels() {
        String defaultModelName = Models.getDefaultModel().getName();
        StringBuffer sb = new StringBuffer();
        sb.append("Models:\n");
        for (String modelName : Models.getModelNames()) {
            sb.append("  ");
            sb.append(modelName);
            if (modelName.equals(defaultModelName)) {
                sb.append(" (default)");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
        return 0;
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

    private CharBuffer decodeResource(ByteBuffer bb, Charset charset, int bomLength) {
        try {
            CharsetDecoder cd = charset.newDecoder();
            cd.onMalformedInput(CodingErrorAction.REPORT);
            cd.onUnmappableCharacter(CodingErrorAction.REPORT);
            bb.position(bomLength);
            CharBuffer cb = cd.decode(bb);
            bb.rewind();
            cb.rewind();
            return cb;
        } catch (MalformedInputException e) {
            logError(e);
        } catch (UnmappableCharacterException e) {
            logError(e);
        } catch (CharacterCodingException e) {
            logError(e);
        }
        return null;
    }

    private static Charset asciiCharset;

    static {
        try {
            asciiCharset = Charset.forName("US-ASCII");
        } catch (RuntimeException e) {
            asciiCharset = null;
        }
    }

    private void setResource(URI uri, Charset charset, CharBuffer buffer, ByteBuffer bufferRaw) {
        this.resourceUri = uri;
        this.resourceEncoding = charset;
        this.resourceBufferRaw = bufferRaw;
        this.resourceErrors = 0;
        this.resourceWarnings = 0;
    }

    private boolean checkResource(String uri) {
        logInfo("Checking resource presence and encoding...");
        URI uriResource = resolve(uri);
        if (uriResource != null) {
            ByteBuffer bytesBuffer = readResource(uriResource);
            if (bytesBuffer != null) {
                Object[] sniffOutputParameters = new Object[] { Integer.valueOf(0) };
                Charset charset = Sniffer.sniff(bytesBuffer, asciiCharset, sniffOutputParameters);
                if (isPermittedEncoding(charset.name())) {
                    int bomLength = (Integer) sniffOutputParameters[0];
                    CharBuffer charsBuffer = decodeResource(bytesBuffer, charset, bomLength);
                    if (charsBuffer != null) {
                        setResource(uriResource, charset, charsBuffer, bytesBuffer);
                        logInfo("Resource encoding sniffed as " + charset.name() + ".");
                        logInfo("Resource length " + bytesBuffer.limit() + " bytes, decoded as " + charsBuffer.limit() + " Java characters (char).");
                    }
                } else
                    logError(message("encoding " + charset.name() + " is not permitted"));
            }
        }
        return this.resourceErrors == 0;
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

    private boolean checkWellFormedness() {
        logInfo("Checking well-formedness...");
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setValidating(false);
            pf.setNamespaceAware(true);
            SAXParser p = pf.newSAXParser();
            p.parse(openStream(this.resourceBufferRaw), new DefaultHandler() {
                public void error(SAXParseException e) {
                    logError(e);
                }
                public void fatalError(SAXParseException e) {
                    logError(e);
                }
                public void warning(SAXParseException e) {
                    logWarning(e);
                }
            }, this.resourceUri.toString());
        } catch (ParserConfigurationException e) {
            logError(e);
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
            if (this.schemaFactory == null) {
                this.schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            }
        }
        return this.schemaFactory;
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
                logWarning(e);
            }
        });
        try {
            return sf.newSchema(url);
        } catch (SAXException e) {
            logError(e);
            throw new SchemaValidationErrorException(e);
        }
    }

    private Schema getSchema(URL url) throws SchemaValidationErrorException {
        synchronized (this) {
            if (!this.schemas.containsKey(url)) {
                this.schemas.put(url, loadSchema(url));
            }
        }
        return this.schemas.get(url);
    }

    private Schema getSchema(String resourceName) throws SchemaValidationErrorException {
        return getSchema(getClass().getClassLoader().getResource(resourceName));
    }

    private Schema getSchema() throws SchemaValidationErrorException {
        return getSchema(this.model.getSchemaResourceName());
    }

    private boolean checkSchemaValidity() {
        logInfo("Checking validity...");
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            XMLReader filter = new ForeignVocabularyFilter(reader, this.model.getNamespaceUri(), true);
            SAXSource source = new SAXSource(filter, new InputSource(openStream(this.resourceBufferRaw)));
            source.setSystemId(this.resourceUri.toString());
            Validator v = getSchema().newValidator();
            v.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException e) {
                    logError(e);
                }
                public void fatalError(SAXParseException e) {
                    logError(e);
                }
                public void warning(SAXParseException e) {
                    logWarning(e);
                }
            });
            v.validate(source);
        } catch (ParserConfigurationException e) {
            logError(e);
        } catch (SchemaValidationErrorException e) {
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

    @SuppressWarnings({"rawtypes","unchecked"})
    private QName getXmlElementDecl(Class jaxbClass, String creatorMethod) {
        try {
            Class ofc = jaxbClass.getClassLoader().loadClass(jaxbClass.getPackage().getName() + ".ObjectFactory");
            return ((JAXBElement) ofc.getDeclaredMethod(creatorMethod, jaxbClass).invoke(ofc.newInstance(), new Object[] { null } )).getName();
        } catch (Exception e) {
            return new QName("", "");
        }
    }

    private String getContentClassNames(Map<Class,String> contentClasses) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (Class contentClass : contentClasses.keySet()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append('<');
            sb.append(getXmlElementDecl(contentClass, contentClasses.get(contentClass)));
            sb.append('>');
        }
        sb.append('}');
        return sb.toString();
    }

    private boolean checkRootElement(JAXBContext context, Object root, Map<Class,String> rootClasses, Map<Object,Locator> locators) {
        if (root instanceof JAXBElement) {
            @SuppressWarnings("rawtypes")
            JAXBElement e = (JAXBElement) root;
            Object contentObject = e.getValue();
            for (Class rootClass : rootClasses.keySet()) {
                if (rootClass.isInstance(contentObject))
                    return true;
            }
            Locator locator = locators.get(contentObject);
            logError(message(locator, "Unexpected root element <" + e.getName() + ">," + " expected one of " + getContentClassNames(rootClasses) + "."));
        } else
            logError(message("Unexpected root element, can't introspect non-JAXBElement"));
        return false;
    }

    private boolean checkSemanticValidity() {
        logInfo("Checking semantic validity...");
        try {
            SAXParserFactory pf = SAXParserFactory.newInstance();
            pf.setNamespaceAware(true);
            XMLReader reader = pf.newSAXParser().getXMLReader();
            final ForeignVocabularyFilter filter = new ForeignVocabularyFilter(reader, this.model.getNamespaceUri(), false);
            SAXSource source = new SAXSource(filter, new InputSource(openStream(this.resourceBufferRaw)));
            source.setSystemId(this.resourceUri.toString());
            JAXBContext context = JAXBContext.newInstance(this.model.getJAXBContextPath());
            Unmarshaller u = context.createUnmarshaller();
            final Map<Object,Locator> locators = new java.util.HashMap<Object,Locator>();
            u.setListener(new Unmarshaller.Listener() {
                public void beforeUnmarshal(Object target, Object parent) {
                    if ((target != null) && (parent != null))
                        locators.put(target, filter.getCurrentLocator());
                }
                public void afterUnmarshal(Object target, Object parent) {
                }
            });
            Object root = u.unmarshal(source);
            if (root == null)
                logError(message("Missing root element."));
            else if (checkRootElement(context, root, this.model.getRootClasses(), locators)) {
                this.resourceRoot = root;
                this.resourceLocators = locators;
            }
        } catch (UnmarshalException e) {
            logError(e);
        } catch (Exception e) {
            logError(e);
        }
        return this.resourceErrors == 0;
    }

    private int validate(String uri) {
        logInfo("Validating {" + uri + "}.");
        do {
            if (!checkResource(uri))
                break;
            if (!checkWellFormedness())
                break;
            if (!checkSchemaValidity())
                break;
            if (!checkSemanticValidity())
                break;
        } while (false);
        int rv = this.resourceErrors > 0 ? 1 : 0;
        if (rv == 0) {
            logInfo("Passed" + ((this.resourceWarnings > 0) ? ", with " + Integer.toString(this.resourceWarnings) + " warnings" : "") + ".");
        } else
            logInfo("Failed, with " + Integer.toString(this.resourceErrors) + " errors.");
        return rv;
    }

    private int validate(List<String> nonOptionArgs) {
        int numFailure = 0;
        int numSuccess = 0;
        for (String arg : nonOptionArgs) {
            if (validate(arg) != 0)
                ++numFailure;
            else
                ++numSuccess;
        }
        if (this.verbose > 0) {
            StringBuffer sb = new StringBuffer();
            if (numSuccess > 0)
                sb.append("Passed " + Integer.toString(numSuccess));
            if (numFailure > 0) {
                if (numSuccess > 0)
                    sb.append(", ");
                sb.append("Failed " + Integer.toString(numFailure));
            }
            if (sb.length() > 0)
                sb.append(" resources.");
            logInfo(sb.toString());
        }
        return numFailure > 0 ? 1 : 0;
    }

    public int run(String[] args) {
        try {
            List<String> nonOptionArgs = parseArgs(args);
            if (!this.quiet)
                System.out.println(banner);
            if (this.listModels)
                return listModels();
            else
                return validate(nonOptionArgs);
        } catch (ShowUsageException e) {
            System.out.println(banner);
            System.out.println(usage);
            return 2;
        } catch (UsageException e) {
            System.out.println("Usage: " + e.getMessage());
            return 2;
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextValidator().run(args));
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

    private static class SchemaValidationErrorException extends RuntimeException {
        static final long serialVersionUID = 0;
        SchemaValidationErrorException(Throwable cause) {
            super(cause);
        }
    }

    private class ForeignVocabularyFilter extends XMLFilterImpl {

        private static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";

        private String namespace;
        private boolean warnPrunes;

        private Stack<QName> nameStack = new Stack<QName>();
        private boolean inForeign;
        private Locator currentLocator;

        ForeignVocabularyFilter(XMLReader reader, String namespace, boolean warnPrunes) {
            super(reader);
            this.namespace = namespace;
            this.warnPrunes = warnPrunes;
        }

        public Locator getCurrentLocator() {
            return new LocatorImpl(this.currentLocator);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.currentLocator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qualName, Attributes attrs) throws SAXException {
            if (!inForeign && inNonForeignNamespace(nsUri)) {
                super.startElement(nsUri, localName, qualName, removeForeign(attrs));
            } else {
                QName qn = new QName(nsUri, localName);
                nameStack.push(qn);
                inForeign = true;
                if (warnPrunes)
                    logWarning(currentLocator, "Pruning element in foreign namespace: <" + qn + ">.");
            }
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
                if (inForeignNamespace(nsUri))
                    hasForeign = true;
            }
            if (hasForeign) {
                AttributesImpl attrsNew = new AttributesImpl();
                for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                    String nsUri = attrs.getURI(i);
                    if (inNonForeignNamespace(nsUri))
                        attrsNew.addAttribute(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
                    else if (warnPrunes) {
                        QName qn = new QName(attrs.getURI(i), attrs.getLocalName(i));
                        logWarning(currentLocator, "Pruning attribute in foreign namespace: <" + qn + ">.");
                    }
                }
                return attrsNew;
            } else
                return attrs;
        }

        private boolean inNonForeignNamespace(String nsUri) {
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

        private boolean inForeignNamespace(String nsUri) {
            return !inNonForeignNamespace(nsUri);
        }
    }

}
