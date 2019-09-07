/*
 * Copyright 2015-2019 Skynav, Inc. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.w3c.dom.Document;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.skynav.cap2tt.app.Converter;
import com.skynav.cap2tt.converter.ConverterContext;
import com.skynav.cap2tt.converter.Results;
import com.skynav.ttv.util.TextReporter;
import com.skynav.ttv.util.Reporter;

public class ConverterTestDriver {

    protected void performConversionTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performConversionTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    protected void performConversionTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            fail("Bad test resource syntax: " + urlString + ".");
            return;
        }
        List<String> args = new java.util.ArrayList<String>();
        args.add("-v");
        args.add("-v");
        args.add("--warn-on");
        args.add("all");
        if (expectedErrors >= 0) {
            args.add("--expect-errors");
            args.add(Integer.toString(expectedErrors));
        }
        if (expectedWarnings >= 0) {
            args.add("--expect-warnings");
            args.add(Integer.toString(expectedWarnings));
        }
        maybeAddConfiguration(args, uri);
        if (additionalOptions != null) {
            args.addAll(java.util.Arrays.asList(additionalOptions));
        }
        args.add(urlString);
        Converter cvt = new Converter();
        Reporter reporter = new TextReporter();
        Document output = cvt.convert(args, uri, reporter, (Document) null);
        Results r = cvt.getResults(urlString);
        assertEquals(output, r.getDocument());
        maybeCheckDifferences(output, uri, cvt);
        int resultCode = r.getCode();
        int resultFlags = r.getFlags();
        if (resultCode == Converter.RV_SUCCESS) {
            if ((resultFlags & Converter.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
            }
            if ((resultFlags & Converter.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected success with unexpected warning(s).");
            }
            if ((resultFlags & Converter.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected success with expected warning(s) mismatch.");
            }
        } else if (resultCode == Converter.RV_FAIL) {
            if ((resultFlags & Converter.RV_FLAG_ERROR_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected error(s).");
            }
            if ((resultFlags & Converter.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected error(s) mismatch.");
            }
        } else
            fail("Unexpected result code " + resultCode + ".");
        reporter.flush();
    }

    private void maybeAddConfiguration(List<String> args, URI u) {
        String[] components = getComponents(u);
        if (hasFileScheme(components)) {
            File f = getConfiguration(components);
            if (f != null) {
                try {
                    String p = f.getCanonicalPath();
                    args.add("--config");
                    args.add(p);
                } catch (IOException e) {
                }
            }
        }
    }

    private File getConfiguration(String[] components) {
        File f1 = new File(joinComponents(components, ".config.xml"));
        if (f1.exists())
            return f1;
        File f2 = new File(joinComponents(components, "test", ".config.xml"));
        if (f2.exists())
            return f2;
        return null;
    }

    private void maybeCheckDifferences(Document output, URI input, ConverterContext context) {
        boolean checkedDifferences = false;
        URI controlURI = null;
        String[] components = getComponents(input);
        if (hasFileScheme(components)) {
            File control = new File(joinComponents(components, ".expected.xml"));
            if (control.exists()) {
                controlURI = control.toURI();
                checkDifferences(output, control);
                checkedDifferences = true;
            }
        }
        Reporter reporter = context.getReporter();
        if (!checkedDifferences) {
            if (output == null)
                reporter.logWarning(reporter.message("w.012", "[T]:Control comparison failed, no output produced from input {0}.", input));
            else
                reporter.logWarning(reporter.message("w.013", "[T]:Control comparison failed, no control for input {0}.", input));
        } else {
            assertTrue(output != null);
            assertTrue(controlURI != null);
            reporter.logInfo(reporter.message("w.014", "[T]:Control comparison passed, compared output with control {0}.", controlURI));
        }
    }

    private void checkDifferences(Document output, File control) {
        Diff diff = DiffBuilder
            .compare(Input.fromFile(control).build())
            .withTest(Input.fromDocument(output).build())
            .ignoreWhitespace()
            .build();
        assertFalse(diff.toString(), diff.hasDifferences());
    }

    private String[] getComponents(URI u) {
        String s = u.getScheme();
        String p = u.getPath();
        String n, x;
        int i = p.lastIndexOf('/');
        if (i >= 0) {
            n = p.substring(i + 1);
            p = p.substring(0, i + 1);
        } else
            n = null;
        int j = n.lastIndexOf('.');
        if (j >= 0) {
            x = n.substring(j);
            n = n.substring(0, j);
        } else {
            x = null;
        }

        return new String[] { s, p, n, x };
    }

    private boolean hasFileScheme(String[] components) {
        return (components != null) && (components[0] != null) && components[0].equals("file");
    }

    private String joinComponents(String[] components, String extension) {
        assert components != null;
        return joinComponents(components, components[2], extension);
    }

    private String joinComponents(String[] components, String name, String extension) {
        assert components != null;
        assert components[1] != null;
        assert name != null;
        assert extension != null;
        StringBuffer sb = new StringBuffer();
        sb.append(components[1]);
        sb.append('/');
        sb.append(name);
        sb.append(extension);
        return sb.toString();
    }

}
