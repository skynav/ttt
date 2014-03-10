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
 
package com.skynav.ttv.util;

import java.io.PrintWriter;
import java.net.URI;

import org.xml.sax.Locator;

public class NullReporter implements Reporter {
    public static final NullReporter Reporter = new NullReporter();
    private static PrintWriter out = new PrintWriter(System.out);
    public void resetResourceState() {}
    public void setResourceURI(String uri) {}
    public void setResourceURI(URI uri) {}
    public void hideLocation() {}
    public boolean isHidingLocation() { return true; }
    public int getResourceErrors() { return 0; }
    public int getResourceWarnings() { return 0; }
    public void setOutput(PrintWriter out) { this.out = out; }
    public PrintWriter getOutput() { return out; }
    public void flush() { if (out != null) out.flush(); }
    public void setVerbosityLevel(int level) {}
    public void incrementVerbosityLevel() {}
    public int getVerbosityLevel() { return 0; }
    public void setDebugLevel(int level) {}
    public void incrementDebugLevel() {}
    public int getDebugLevel() { return 0; }
    public Message message(String key, String format, Object... arguments) { return new Message(key, format, arguments); }
    public Message message(Locator locator, String key, String format, Object... arguments) { return new LocatedMessage(locator, key, format, arguments); }
    public void logError(Message message) {}
    public void logError(Locator locator, Message message) {}
    public void logError(Exception e) {}
    public boolean hasDefaultWarning(String token) { return false; }
    public void setTreatWarningAsError(boolean treatWarningAsError) {}
    public boolean isWarningEnabled(String token) { return false; }
    public void enableWarning(String token) {}
    public void disableWarning(String token) {}
    public void disableWarnings() {}
    public void hideWarnings() {}
    public boolean logWarning(Message message) { return false; }
    public boolean logWarning(Locator locator, Message message) { return false; }
    public boolean logWarning(Exception e) { return false; }
    public void logInfo(Message message) {}
    public void logInfo(Locator locator, Message message) {}
    public void logDebug(Message message) {}
    public void logDebug(Locator locator, Message message) {}
    public void showProcessingInfo() {}
}
