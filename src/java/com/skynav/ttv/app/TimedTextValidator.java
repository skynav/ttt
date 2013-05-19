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

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.skynav.ttv.model.ttml10.tt.TimedText;
import com.skynav.ttv.model.ttml10.ttp.Profile;

public class TimedTextValidator implements ErrorHandler, LSResourceResolver, ValidationEventHandler {

    // options state
    private int verbose;

    // processing state
    private XMLStreamReader reader;
    private Map<Object, Location> locations;

    TimedTextValidator() {
    }

    public int run(String[] args) {
        try {
            return validate(parseArgs(args));
        } catch (ShowUsageException e) {
            System.err.println("Usage: ...");
            return 2;
        } catch (UsageException e) {
            System.err.println("Usage: " + e.getMessage());
            return 2;
        }
    }

    // org.xml.sax.ErrorHandler

    public void error(SAXParseException e) {
        throw new RuntimeException(e.getMessage(), e);
    }

    public void fatalError(SAXParseException e) {
        throw new RuntimeException(e.getMessage(), e);
    }

    public void warning(SAXParseException e) {
        throw new RuntimeException(e.getMessage(), e);
    }

    // org.w3c.dom.ls.ResourceResolver

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        return null;
    }

    // javax.xml.bin.ValidationEventHandler

    public boolean handleEvent(ValidationEvent e) {
        if (e.getSeverity() != ValidationEvent.WARNING) {
            ValidationEventLocator l = e.getLocator();
            System.out.println("[" + l.getLineNumber() + ":" + l.getColumnNumber() + "]:" + e.getMessage());
        }
        return true;
    }

    private URI getCWDAsURI() {
        try {
            return new URI("file://" + new File(".").getCanonicalPath().replace(File.separatorChar, '/'));
        } catch (IOException e) {
            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private List<URL> parseArgs(String[] args) {
        List<URL> urls = new java.util.ArrayList<URL>();
        int nonOptionIndex = -1;
        for (int i = 0; i < args.length;) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                case '-':
                    i = parseLongOption(args, i);
                    break;
                case '?':
                    throw new ShowUsageException();
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
            URI uriCurrentDirectory = getCWDAsURI();
            for (int i = nonOptionIndex; i < args.length; ++i) {
                try {
                    URI uri = new URI(args[i]);
                    if (!uri.isAbsolute()) {
                        URI uriAbsolute = uriCurrentDirectory.resolve(uri);
                        if (uriAbsolute != null)
                            uri = uriAbsolute;
                    }
                    urls.add(uri.toURL());
                } catch (URISyntaxException e) {
                    throw new BadURLArgumentException(e.getMessage());
                } catch (MalformedURLException e) {
                    throw new BadURLArgumentException(e.getMessage());
                }
            }
        }
        return urls;
    }

    private int parseLongOption(String args[], int index) {
        String option = args[index];
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("verbose"))
            this.verbose += 1;
        else
            throw new UnknownOptionException("--" + option);
        return index + 1;
    }

    private int parseShortOption(String args[], int index) {
        String option = args[index];
        assert option.length() == 2;
        option = option.substring(1);
        switch (option.charAt(0)) {
        case 'v':
            this.verbose += 1;
            break;
        default:
            throw new UnknownOptionException("-" + option);
        }
        return index + 1;
    }

    private Schema createSchema() {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            sf.setErrorHandler(this);
            sf.setResourceResolver(this);
            return sf.newSchema(getClass().getClassLoader().getResource("xsd/ttml10/ttaf1-dfxp.xsd"));
        } catch (SAXException e) {
            throw new RuntimeException("can't create schema", e);
        }
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

    private Location getLocation(Object object) {
        Location location = locations.get(object);
        if (location == null)
            location = DefaultLocation.DEFAULT;
        return location;
    }

    private int ensureAcceptableRootElement(JAXBContext context, Object unmarshalled) {
        if (unmarshalled instanceof JAXBElement) {
            @SuppressWarnings("rawtypes")
            JAXBElement e = (JAXBElement) unmarshalled;
            Object value = e.getValue();
            if (value instanceof TimedText)
                return 0;
            else if (value instanceof Profile)
                return 0;
            else {
                Location location = getLocation(value);
                System.out.println("[" + location.getLineNumber() + "," + location.getColumnNumber() + "]:unexpected root element " + e.getName() + "." + " " +
                    "Expected elements are " +
                    "<" + getXmlElementDecl(TimedText.class, "createTt") + ">" + "," +
                    "<" + getXmlElementDecl(Profile.class, "createProfile") + ">");
                return 1;
            }
        } else {
            System.out.println("[?,?]:unexpected root element, can't introspect non-JAXBElement");
            return 1;
        }
    }

    private void resetValidationState() {
        this.locations = new java.util.HashMap<Object, Location>();
        this.reader = null;
    }

    private int validate(URL url) {
        resetValidationState();
        try {
            JAXBContext context = JAXBContext.newInstance("com.skynav.ttv.model.ttml10.tt:com.skynav.ttv.model.ttml10.ttm:com.skynav.ttv.model.ttml10.ttp");
            Unmarshaller u = context.createUnmarshaller();
            u.setSchema(createSchema());
            u.setEventHandler(this);
            u.setListener(new Unmarshaller.Listener() {
                public void beforeUnmarshal(Object target, Object parent) {
                    locations.put(target, reader.getLocation());
                }
            } );
            XMLInputFactory xif = XMLInputFactory.newFactory();
            this.reader = xif.createXMLStreamReader(url.openStream());
            return ensureAcceptableRootElement(context, u.unmarshal(this.reader));
        } catch (Exception e) {
            if (this.verbose > 1)
                e.printStackTrace();
        }
        return 1;
    }

    public int validate(List<URL> urls) {
        int numFailure = 0;
        for (URL url : urls) {
            if (validate(url) != 0)
                ++numFailure;
        }
        return numFailure > 0 ? 1 : 0;
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
        UnknownOptionException(String arg) {
            super("unknown option: " + arg);
        }
    }

    private static class BadURLArgumentException extends UsageException {
        static final long serialVersionUID = 0;
        BadURLArgumentException(String arg) {
            super(arg);
        }
    }

    private static class ShowUsageException extends UsageException {
        static final long serialVersionUID = 0;
        ShowUsageException() {
            super("show usage");
        }
    }

    private static class DefaultLocation implements Location {
        static final DefaultLocation DEFAULT = new DefaultLocation();
        public int getLineNumber() {
            return 0;
        }
        public int getColumnNumber() {
            return 0;
        }
        public int getCharacterOffset() {
            return 0;
        }
        public String getPublicId() {
            return "";
        }
        public String getSystemId() {
            return "";
        }
    }

}
