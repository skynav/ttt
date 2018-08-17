/*
 * Copyright 2013-16 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttpe.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.TextReporter;

public class PresenterTestDriver {

    protected void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performPresentationTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    protected void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        URI input;
        try {
            input = new URI(urlString);
        } catch (URISyntaxException e) {
            fail("Bad test resource syntax: " + urlString + ".");
            return;
        }
        List<String> args = new java.util.ArrayList<String>();
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
        maybeAddConfiguration(args, input);
        maybeAddFontDirectory(args, input);
        maybeAddOutputDirectory(args);
        if (additionalOptions != null) {
            args.addAll(java.util.Arrays.asList(additionalOptions));
        }
        args.add(urlString);
        Presenter ttpe = new Presenter();
        List<URI> output = ttpe.present(args, new TextReporter());
        assertNotNull("Presentation failed, no output produced.", output);
        maybeCheckDifferences(output, input);
        TimedTextVerifier.Results r = ttpe.getResults(urlString);
        int resultCode = r.getCode();
        int resultFlags = r.getFlags();
        if (resultCode == TimedTextVerifier.RV_PASS) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected success with unexpected warning(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected success with expected warning(s) mismatch.");
            }
        } else if (resultCode == TimedTextVerifier.RV_FAIL) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected error(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected error(s) mismatch.");
            }
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

    private void maybeAddConfiguration(List<String> args, URI input) {
        String[] components = getComponents(input);
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

    private void maybeAddFontDirectory(List<String> args, URI input) {
        String[] components = getComponents(input);
        if (hasFileScheme(components)) {
            File f = getFontDirectory(components);
            if (f != null) {
                try {
                    String p = f.getCanonicalPath();
                    args.add("--font-directory");
                    args.add(p);
                } catch (IOException e) {
                }
            }
        }
    }

    private File getFontDirectory(String[] components) {
        File f1 = new File(components[1], "fonts");
        if (f1.exists())
            return f1;
        File f2 = new File(".", "src/test/fonts");
        if (f2.exists())
            return f2;
        File f3 = new File(".", "fonts");
        if (f3.exists())
            return f3;
        return null;
    }

    private void maybeAddOutputDirectory(List<String> args) {
        String reportsDirectory = System.getProperty("surefire.reportsDirectory");
        if (reportsDirectory != null) {
            maybeCreateOutputDirectory(reportsDirectory);
            File f = new File(reportsDirectory);
            if (f.exists()) {
                args.add("--output-directory");
                args.add(reportsDirectory);
            }
        }
    }

    private boolean maybeCreateOutputDirectory(String outputDirectory) {
        File f = new File(outputDirectory);
        if (!f.exists())
            return f.mkdir();
        else
            return false;
    }

    private void maybeCheckDifferences(List<URI> output, URI input) {
        if ((output != null) && !output.isEmpty()) {
            if (output.size() == 1) {
                URI outputURI = output.get(0);
                if (outputURI != null) {
                    String outputURIPath = outputURI.getPath();
                    if ((outputURIPath != null) && outputURIPath.endsWith(".zip")) {
                        String[] components = getComponents(input);
                        if (hasFileScheme(components)) {
                            File control = new File(joinComponents(components, ".expected.zip"));
                            if (control.exists()) {
                                checkDifferences(control.toURI(), outputURI);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkDifferences(URI uri1, URI uri2) {
        assert hasFileScheme(uri1);
        assert hasFileScheme(uri2);
        BufferedInputStream bis1 = null;
        BufferedInputStream bis2 = null;
        ZipInputStream zis1 = null;
        ZipInputStream zis2 = null;
        try {
            File[] retFile1 = new File[1];
            File[] retFile2 = new File[2];
            if ((bis1 = getArchiveInputStream(uri1, retFile1)) != null) {
                if ((bis2 = getArchiveInputStream(uri2, retFile2)) != null) {
                    File f1 = retFile1[0];
                    File f2 = retFile2[0];
                    zis1 = new ZipInputStream(bis1);
                    zis2 = new ZipInputStream(bis2);
                    checkDifferences(f1, zis1, f2, zis2);
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            IOUtil.closeSafely(zis2);
            IOUtil.closeSafely(bis2);
            IOUtil.closeSafely(zis1);
            IOUtil.closeSafely(bis1);
        }
    }

    private void checkDifferences(File f1, ZipInputStream zi1, File f2, ZipInputStream zi2) throws IOException {
        boolean done = false;
        while (!done) {
            ZipEntry e1 = zi1.getNextEntry();
            ZipEntry e2 = zi2.getNextEntry();
            if (e1 != null) {
                if (e2 != null) {
                    checkDifferences(f1, zi1, e1, f2, zi2, e2);
                } else {
                    fail("Archive entry count mismatch, extra entry in " + f1 + ".");
                    done = true;
                }
            } else if (e2 != null) {
                fail("Archive entry count mismatch, extra entry in " + f2 + ".");
                done = true;
            } else {
                done = true;
            }
        }
    }

    private void checkDifferences(File f1, InputStream is1, ZipEntry e1, File f2, InputStream is2, ZipEntry e2) {
        String n1 = e1.getName();
        String n2 = e2.getName();
        assertEquals("Archive entry name mismatch.", n1, n2);
        InputStream eis1 = null;
        InputStream eis2 = null;
        try {
            eis1 = readEntryData(e1, is1);
            eis2 = readEntryData(e2, is2);
            checkDifferences(n1, eis1, n2, eis2);
        } catch (IOException e) {
            String m = "";
            if (eis1 == null)
                m = "Can't read data for entry '" + n1 + "': ";
            else if (eis2 == null)
                m = "Can't read data for entry '" + n1 + "': ";
            fail(m + e.getMessage() + ".");
        } finally {
            IOUtil.closeSafely(eis1);
            IOUtil.closeSafely(eis2);
        }
    }

    private InputStream readEntryData(ZipEntry e, InputStream is) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            long limit = e.getSize();
            assertTrue("Entry data size exceeds 4GB limit.", limit <= (long) Integer.MAX_VALUE);
            long consumed = 0;
            byte[] buffer = new byte[4096];
            bos = new ByteArrayOutputStream(limit > 0 ? (int) limit : buffer.length);
            for (int nb; (nb = is.read(buffer)) >= 0;) {
                if (nb > 0) {
                    bos.write(buffer, 0, nb);
                    consumed += nb;
                } else
                    Thread.yield();
            }
            assertTrue("Unable to read all entry data, got " + consumed + " bytes, expected " + limit + " bytes.", (limit < 0) || (consumed == limit));
            return new ByteArrayInputStream(bos.toByteArray());
        } finally {
            IOUtil.closeSafely(bos);
        }
    }

    private void checkDifferences(String n1, InputStream is1, String n2, InputStream is2) {
        assert n1.equals(n2);
        if (n1.endsWith(".xml"))
            checkDifferencesXML(n1, is1, n2, is2);
        else if (n1.endsWith(".svg"))
            checkDifferencesXML(n1, is1, n2, is2);
        else if (n2.endsWith(".png"))
            checkDifferencesPNG(n1, is1, n2, is2);
        else
            checkDifferencesOther(n1, is1, n2, is2);
    }

    private void checkDifferencesXML(String n1, InputStream is1, String n2, InputStream is2) {
        Diff diff = DiffBuilder
            .compare(Input.fromStream(is1).build())
            .withTest(Input.fromStream(is2).build())
            .ignoreWhitespace()
            .build();
        assertFalse(diff.toString(), diff.hasDifferences());
    }

    private void checkDifferencesPNG(String n1, InputStream is1, String n2, InputStream is2) {
    }

    private void checkDifferencesOther(String n1, InputStream is1, String n2, InputStream is2) {
    }

    private BufferedInputStream getArchiveInputStream(URI uri, File[] retFile) throws IOException {
        File f = new File(uri.getPath());
        if (retFile != null)
            retFile[0] = f;
        return new BufferedInputStream(new FileInputStream(f));
    }

    private String[] getComponents(URI uri) {
        assert uri != null;
        String s = uri.getScheme();
        String p = uri.getPath();
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

    private boolean hasFileScheme(URI uri) {
        return hasFileScheme(getComponents(uri));
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
