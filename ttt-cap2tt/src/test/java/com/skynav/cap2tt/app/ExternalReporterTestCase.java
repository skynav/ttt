/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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
 
package com.skynav.cap2tt.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.skynav.cap2tt.converter.Results;
import com.skynav.ttv.util.Base64;
import com.skynav.ttv.util.TextReporter;

public class ExternalReporterTestCase {

    private static final String testData = "";
    private static final String mappedBundleKey = "w.011";
    private static final String mappedBundleValue = "mapped value";
    private static final String[] arguments = new String[] {
        "-v",
        "--warn-on",            "empty-input",
        "--expect-errors",      "0",
        "--expect-warnings",    "1",
        "--output-disable"
    };

    @Test
    public void testExternalReporter() throws Exception {
        ExternalReporter reporter = new ExternalReporter();
        URI uri = getDataURI(testData);
        Converter cvt = new Converter();
        cvt.convert(Arrays.asList(arguments), uri, reporter);
        Results r = cvt.getResults(uri.toString());
        assertNotNull(r);
        assertTrue(r.getWarnings() == 1);
        assertTrue(reporter.containsMappedMessage);
    }

    private URI getDataURI(String data) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("data:text/plain;charset=utf-8;base64,");
            Charset encoding = Charset.forName("utf-8");
            sb.append(Base64.encode(data.getBytes(encoding)));
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ExternalReporter extends TextReporter {
        public boolean containsMappedMessage;
        public void setBundle(ResourceBundle bundle) {
            try {
                super.setBundle((bundle instanceof PropertyResourceBundle) ? new MappedBundle((PropertyResourceBundle) bundle) : bundle);
            } catch (IOException e) {
                super.setBundle(bundle);
            }
        }
        protected void out(ReportType reportType, String message) {
            if (reportType == ReportType.Warning) {
                if (message.indexOf(mappedBundleValue) >= 0) {
                    containsMappedMessage = true;
                }
            }
            super.out(reportType, message);
        }
    }

    private static class MappedBundle extends PropertyResourceBundle {
        private MappedBundle(PropertyResourceBundle bundle) throws IOException {
            super(new ByteArrayInputStream(new byte[0]));
            setParent(bundle);
        }
        public Object handleGetObject(String key) {
            if (key.equals(mappedBundleKey))
                return mappedBundleValue;
            else
                return super.handleGetObject(key);
        }
    }

}
