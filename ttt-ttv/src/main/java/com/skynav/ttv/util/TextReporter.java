/*
 * Copyright 2013-14 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.UnmarshalException;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class TextReporter implements Reporter {

    public static final String NAME = "text";
    public static final String DEFAULT_ENCODING = "UTF-8";

    /* general state */
    private Map<String,Boolean> defaultWarnings;
    private PrintWriter output;
    private boolean outputDefaulted;
    private ResourceBundle bundle;
    /* options state */
    private int debug;
    private boolean disableWarnings;
    private Set<String> disabledWarnings;
    private Set<String> enabledWarnings;
    private boolean hideLocation;
    private boolean hidePath;
    private boolean hideWarnings;
    private boolean treatWarningAsError;
    private int verbose;
    /* per-resource state */
    private Set<String> resourceDisabledWarnings;
    private Set<String> resourceEnabledWarnings;
    private String[] resourceLines;
    private int resourceErrors;
    private URI resourceUri;
    private String resourceUriString;
    private int resourceWarnings;

    public TextReporter() {
    }

    public String getName() {
        return NAME;
    }

    public boolean isOpen() {
        return output != null;
    }

    public void open(Object... arguments) throws IOException {
        Object[][] defaultWarningSpecifications;
        if ((arguments.length > 0) && (arguments[0] instanceof Object[][]))
            defaultWarningSpecifications = (Object[][]) arguments[0];
        else
            defaultWarningSpecifications = null;
        PrintWriter output;
        if ((arguments.length > 1) & (arguments[1] instanceof PrintWriter))
            output = (PrintWriter) arguments[1];
        else
            output = null;
        ResourceBundle bundle;
        if ((arguments.length > 2) & (arguments[2] instanceof ResourceBundle))
            bundle = (ResourceBundle) arguments[2];
        else
            bundle = null;
        Map<String,Boolean> defaultWarnings = new java.util.HashMap<String,Boolean>();
        if (defaultWarningSpecifications != null) {
            for (Object[] spec : defaultWarningSpecifications) {
                defaultWarnings.put((String) spec[0], (Boolean) spec[1]);
            }
        }
        this.defaultWarnings = defaultWarnings;
        this.disabledWarnings = new java.util.HashSet<String>();
        this.enabledWarnings = new java.util.HashSet<String>();
        this.hideLocation = false;
        this.hidePath = false;
        setOutput(output);
        setBundle(bundle);
    }

    public void close() throws IOException {
        PrintWriter output = getOutput();
        if (!outputDefaulted)
            output.close();
        this.output = null;
    }

    public void resetResourceState() {
        resourceLines = null;
        resourceDisabledWarnings = new java.util.HashSet<String>(disabledWarnings);
        resourceEnabledWarnings = new java.util.HashSet<String>(enabledWarnings);
        resourceErrors = 0;
        resourceWarnings = 0;
    }

    public void setResourceURI(String uri) {
        resourceUriString = uri;
    }

    public void setResourceURI(URI uri) {
        resourceUri = uri;
    }

    public void setLines(String[] lines) {
        String[] sa = new String[lines.length];
        System.arraycopy(lines, 0, sa, 0, sa.length);
        resourceLines = sa;
    }

    public void hidePath() {
        this.hidePath = true;
    }

    public void showPath() {
        this.hidePath = false;
    }

    public boolean isHidingPath() {
        return this.hidePath;
    }

    public void hideLocation() {
        this.hideLocation = true;
    }

    public void showLocation() {
        this.hideLocation = false;
    }

    public boolean isHidingLocation() {
        return this.hideLocation;
    }

    public int getResourceErrors() {
        return resourceErrors;
    }

    public int getResourceWarnings() {
        return resourceWarnings;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    public PrintWriter getOutput() {
        if (output == null) {
            Charset defaultEncoding;
            try {
                defaultEncoding = Charset.forName(DEFAULT_ENCODING);
            } catch (RuntimeException e) {
                defaultEncoding = Charset.defaultCharset();
            }
            output = new PrintWriter(new OutputStreamWriter(System.err, defaultEncoding));
            outputDefaulted = true;
        }
        return output;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    protected void out(String message) {
        getOutput().print(message);
    }

    protected void out(ReportType reportType, String message) {
        char type;
        if (reportType == ReportType.Error)
            type = 'E';
        else if (reportType == ReportType.Warning)
            type = 'W';
        else if (reportType == ReportType.Info)
            type = 'I';
        else if (reportType == ReportType.Debug)
            type = 'D';
        else
            type = '?';
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(type);
        sb.append(']');
        sb.append(':');
        sb.append(message);
        sb.append('\n');
        out(sb.toString());
    }

    protected void out(ReportType type, Message message) {
        out(type, message.toText(bundle, isHidingLocation(), isHidingPath()));
    }

    public void flush() {
        PrintWriter output = getOutput();
        output.flush();
    }

    public void setVerbosityLevel(int verbose) {
        this.verbose = verbose;
    }

    public void incrementVerbosityLevel() {
        ++this.verbose;
    }

    public int getVerbosityLevel() {
        return this.verbose;
    }

    public void setDebugLevel(int debug) {
        this.debug = debug;
    }

    public void incrementDebugLevel() {
        ++this.debug;
    }

    public int getDebugLevel() {
        return this.debug;
    }

    private Message message(String message) {
        return message((String) null, message);
    }

    public Message message(String key, String format, Object... arguments) {
        return new Message(key, format, arguments);
    }

    private Message message(Locator locator, String message) {
        return message(locator, (String) null, message);
    }

    public Message message(Locator locator, String key, String format, Object... arguments) {
        String sysid = locator.getSystemId();
        String uriString;
        if ((sysid != null) && (sysid.length() > 0))
            uriString = sysid;
        else if (resourceUri != null)
            uriString = resourceUri.toString();
        else if (resourceUriString != null)
            uriString = resourceUriString;
        else
            uriString = null;
        return new LocatedMessage(uriString, locator.getLineNumber(), locator.getColumnNumber(), resourceLines, key, format, arguments);
    }

    public void logError(Message message) {
        out(ReportType.Error, message);
        ++resourceErrors;
    }

    private Locator extractLocator(SAXParseException e) {
        LocatorImpl locator = new LocatorImpl();
        locator.setSystemId(e.getSystemId());
        locator.setLineNumber(e.getLineNumber());
        locator.setColumnNumber(e.getColumnNumber());
        return locator;
    }

    private Message extractMessage(SAXParseException e) {
        String message = e.getMessage();
        if (message.indexOf("cvc") == 0) {
            int cvcEndIndex = message.indexOf(":");
            String cvcLabel = message.substring(0, cvcEndIndex);
            String cvcRemainder = message.substring(cvcEndIndex + 1);
            message = "XSD(" + cvcLabel + "):" + cvcRemainder;
        }
        return message(extractLocator(e), massageMessage(message));
    }

    private Locator extractLocator(UnmarshalException e) {
        LocatorImpl locator = new LocatorImpl();
        locator.setSystemId(resourceUriString);
        return locator;
    }

    private Message extractMessage(UnmarshalException e) {
        return message(extractLocator(e), massageMessage(e.getMessage()));
    }

    private Message extractMessage(Throwable e) {
        if ((e.getCause() != null) && (e.getCause() != e))
            return extractMessage(e.getCause());
        else if (e instanceof SAXParseException)
            return extractMessage((SAXParseException) e);
        else if (e instanceof UnmarshalException)
            return extractMessage((UnmarshalException) e);
        else {
            String message = e.getMessage();
            if ((message == null) || (message.length() == 0))
                message = e.toString();
            message = massageMessage(message);
            return message(message + ((debug < 2) ? "; retry with --debug-exceptions option for additional information." : "."));
        }
    }

    private String massageMessage(String message) {
        StringBuffer sb = new StringBuffer();
        if (message == null)
            message = "";
        for (int i = 0, n = message.length(); i < n; ++i) {
            char c = message.charAt(i);
            if (c == '\'')
                sb.append("''");
            else if (c == '{')
                sb.append("'{'");
            else if (c == '}')
                sb.append("'}'");
            else if ((i == 0) && Character.isLowerCase(c))
                sb.append(Character.toUpperCase(c));
            else
                sb.append(c);
        }
        if ((sb.length() > 0) && (sb.charAt(sb.length() - 1) != '.'))
            sb.append('.');
        return sb.toString();
    }

    public void logError(Exception e) {
        logError(extractMessage(e));
        logDebug(e);
    }

    public boolean hasDefaultWarning(String token) {
        return defaultWarnings.containsKey(token);
    }

    private Set<String> getEnabledWarnings() {
        if (this.resourceEnabledWarnings != null)
            return this.resourceEnabledWarnings;
        else
            return this.enabledWarnings;
    }

    private Set<String> getDisabledWarnings() {
        if (this.resourceDisabledWarnings != null)
            return this.resourceDisabledWarnings;
        else
            return this.disabledWarnings;
    }

    public void setTreatWarningAsError(boolean treatWarningAsError) {
        this.treatWarningAsError = treatWarningAsError;
    }

    public boolean isTreatingWarningAsError() {
        return this.treatWarningAsError;
    }

    public boolean isWarningEnabled(String token) {
        boolean enabled = hasDefaultWarning(token) ? defaultWarnings.get(token) : false;
        if (getEnabledWarnings().contains(token) || getEnabledWarnings().contains("all"))
            enabled = true;
        if (getDisabledWarnings().contains(token) || getDisabledWarnings().contains("all"))
            enabled = false;
        return enabled;
    }

    public boolean hasEnabledWarning(String token) {
        return getEnabledWarnings().contains(token);
    }

    public void enableWarning(String token) {
        getEnabledWarnings().add(token);
    }

    public boolean hasDisabledWarning(String token) {
        return getDisabledWarnings().contains(token);
    }

    public void disableWarning(String token) {
        getDisabledWarnings().add(token);
    }

    public void disableWarnings() {
        this.disableWarnings = true;
    }

    public boolean areWarningsDisabled() {
        return this.disableWarnings;
    }

    public void hideWarnings() {
        this.hideWarnings = true;
    }

    public boolean areWarningsHidden() {
        return this.hideWarnings;
    }

    public boolean logWarning(Message message) {
        if (!disableWarnings) {
            if (!hideWarnings)
                out(ReportType.Warning, message);
            ++resourceWarnings;
        }
        return treatWarningAsError;
    }

    public boolean logWarning(Exception e) {
        boolean treatedAsError = logWarning(extractMessage(e));
        logDebug(e);
        return treatedAsError;
    }

    public void logInfo(Message message) {
        if (verbose > 0) {
            out(ReportType.Info, message);
        }
    }

    public void logDebug(Message message) {
        if (isDebuggingEnabled()) {
            out(ReportType.Debug, message);
        }
    }

    private boolean isDebuggingEnabled(int level) {
        return debug >= level;
    }

    private boolean isDebuggingEnabled() {
        return isDebuggingEnabled(1);
    }

    private void logDebug(Exception e) {
        if (isDebuggingEnabled(2)) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logDebug(message(sw.toString()));
        }
    }
}
