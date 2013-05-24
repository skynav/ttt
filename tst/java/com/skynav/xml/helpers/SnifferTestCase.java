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
 
package com.skynav.xml.helpers;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.skynav.xml.helpers.Sniffer;

public class SnifferTestCase {

    private enum SpaceAroundEquals {
        None,
        One,
        Mixed
    };

    static private final int[] bomEmpty = null;

    private static Charset asciiCharset;

    static {
        try {
            asciiCharset = Charset.forName("US-ASCII");
        } catch (RuntimeException e) {
            asciiCharset = null;
        }
    }

    static private final Object[][] asciiTests = new Object[][] {
        // no encoding
        { bomEmpty, "",         Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty, "us-ascii", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "us-ascii", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty, "us-ascii", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty, "us-ascii", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty, "us-ascii", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty, "us-ascii", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty, "US-ASCII", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "Us-Ascii", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "Us-AsCiI", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "ascii",    Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffASCII() throws Exception {
        int testIndex = 0;
        for (Object[] test : asciiTests) {
            ByteBuffer bb = makeByteBuffer("US-ASCII", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, asciiCharset);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "US-ASCII");
            ++testIndex;
        }
    }

    static private final int[] bomUTF8 = new int[] {
        0xEF, 0xBB, 0xBF
    };

    static private final Object[][] utf8Tests = new Object[][] {
        // BOM only
        { bomUTF8,  "",      Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty, "utf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "utf-8", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty, "utf-8", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty, "utf-8", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty, "utf-8", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty, "utf-8", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty, "UTF-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "Utf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty, "uTf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        // both BOM and encoding
        { bomUTF8,  "utf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF8,  "utf-8", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomUTF8,  "utf-8", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomUTF8,  "utf-8", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomUTF8,  "utf-8", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomUTF8,  "utf-8", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomUTF8,  "UTF-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF8,  "Utf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF8,  "uTf-8", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffUTF8() throws Exception {
        int testIndex = 0;
        for (Object[] test : utf8Tests) {
            ByteBuffer bb = makeByteBuffer("UTF-8", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "UTF-8");
            ++testIndex;
        }
    }

    static private final int[] bomUTF16LE = new int[] {
        0xFF, 0xFE
    };

    static private final Object[][] utf16LETests = new Object[][] {
        // BOM only
        { bomUTF16LE, "",         Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty,   "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty,   "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty,   "utf-16le", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty,   "utf-16le", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty,   "utf-16le", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty,   "UTF-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "Utf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "uTf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        // both BOM and encoding
        { bomUTF16LE, "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16LE, "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomUTF16LE, "utf-16le", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomUTF16LE, "utf-16le", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomUTF16LE, "utf-16le", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomUTF16LE, "utf-16le", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomUTF16LE, "UTF-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16LE, "Utf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16LE, "uTf-16le", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffUTF16LE() throws Exception {
        int testIndex = 0;
        for (Object[] test : utf16LETests) {
            ByteBuffer bb = makeByteBuffer("UTF-16LE", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "UTF-16LE");
            ++testIndex;
        }
    }

    static private final int[] bomUTF16BE = new int[] {
        0xFE, 0xFF
    };

    static private final Object[][] utf16BETests = new Object[][] {
        // BOM only
        { bomUTF16BE, "",         Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty,   "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty,   "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty,   "utf-16be", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty,   "utf-16be", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty,   "utf-16be", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty,   "UTF-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "Utf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "uTf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        // both BOM and encoding
        { bomUTF16BE, "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16BE, "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomUTF16BE, "utf-16be", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomUTF16BE, "utf-16be", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomUTF16BE, "utf-16be", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomUTF16BE, "utf-16be", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomUTF16BE, "UTF-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16BE, "Utf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF16BE, "uTf-16be", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffUTF16BE() throws Exception {
        int testIndex = 0;
        for (Object[] test : utf16BETests) {
            ByteBuffer bb = makeByteBuffer("UTF-16BE", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "UTF-16BE");
            ++testIndex;
        }
    }

    static private final int[] bomUTF32LE = new int[] {
        0xFF, 0xFE, 0x00, 0x00
    };

    static private final Object[][] utf32LETests = new Object[][] {
        // BOM only
        { bomUTF32LE, "",         Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty,   "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty,   "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty,   "utf-32le", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty,   "utf-32le", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty,   "utf-32le", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty,   "UTF-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "Utf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "uTf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        // both BOM and encoding
        { bomUTF32LE, "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32LE, "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomUTF32LE, "utf-32le", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomUTF32LE, "utf-32le", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomUTF32LE, "utf-32le", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomUTF32LE, "utf-32le", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomUTF32LE, "UTF-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32LE, "Utf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32LE, "uTf-32le", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffUTF32LE() throws Exception {
        int testIndex = 0;
        for (Object[] test : utf32LETests) {
            ByteBuffer bb = makeByteBuffer("UTF-32LE", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "UTF-32LE");
            ++testIndex;
        }
    }

    static private final int[] bomUTF32BE = new int[] {
        0x00, 0x00, 0xFE, 0xFF
    };

    static private final Object[][] utf32BETests = new Object[][] {
        // BOM only
        { bomUTF32BE, "",         Character.valueOf('\"'), SpaceAroundEquals.None },
        // encoding only
        { bomEmpty,   "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomEmpty,   "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomEmpty,   "utf-32be", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomEmpty,   "utf-32be", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomEmpty,   "utf-32be", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomEmpty,   "UTF-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "Utf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomEmpty,   "uTf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        // both BOM and encoding
        { bomUTF32BE, "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32BE, "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.One },
        { bomUTF32BE, "utf-32be", Character.valueOf('\"'), SpaceAroundEquals.Mixed },
        { bomUTF32BE, "utf-32be", Character.valueOf('\''), SpaceAroundEquals.None },
        { bomUTF32BE, "utf-32be", Character.valueOf('\''), SpaceAroundEquals.One },
        { bomUTF32BE, "utf-32be", Character.valueOf('\''), SpaceAroundEquals.Mixed },
        { bomUTF32BE, "UTF-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32BE, "Utf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
        { bomUTF32BE, "uTf-32be", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffUTF32BE() throws Exception {
        int testIndex = 0;
        for (Object[] test : utf32BETests) {
            ByteBuffer bb = makeByteBuffer("UTF-32BE", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), "UTF-32BE");
            ++testIndex;
        }
    }

    static private final Object[][] otherEncodingSansBOMTests = new Object[][] {
        // encoding only
        { bomEmpty, "ISO-8859-1", Character.valueOf('\"'), SpaceAroundEquals.None },
    };

    @Test
    public void testSniffOther() throws Exception {
        int testIndex = 0;
        for (Object[] test : otherEncodingSansBOMTests) {
            ByteBuffer bb = makeByteBuffer((String)test[1], test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNotNull(cs);
            assertEquals("Test Index " + testIndex, cs.name(), test[1]);
            ++testIndex;
        }
    }

    static private final String[] invalidXMLDeclarationTests = new String[] {
        "",                         // missing xml declaration prefix
        "\u0000xml?>",              // invalid xml declaration prefix
        "?xml?>",                   // invalid xml declaration prefix
        "<?xml?>",                  // missing encoding pseudo-attribute
        "<?xml encoding?>",         // missing equals
        "<?xml encoding=?>",        // missing quoted encoding value
        "<?xml encoding=\"?>",      // missing encoding value and terminating quote
        "<?xml encoding=\'?>",      // missing encoding value and terminating quote
        "<?xml encoding=\"1\"?>",   // invalid initial character of encoding name (must be ascii letter)
        "<?xml encoding=\"_\"?>",   // invalid initial character of encoding name (must be ascii letter)
        "<?xml encoding=\"A+\"?>",  // invalid following character of encoding name (must be ascii letter, digit, [._-])
    };

    @Test
    public void testSniffInvalidXMLDeclaration() throws Exception {
        int testIndex = 0;
        for (String test : invalidXMLDeclarationTests) {
            ByteBuffer bb = makeByteBuffer("US-ASCII", test);
            assertNotNull(bb);
            Charset cs = Sniffer.sniff(bb, null);
            assertNull("Test Index " + testIndex, cs);
            ++testIndex;
        }
    }

    private static void addSpaceAroundEquals(StringBuffer sb, SpaceAroundEquals spaceAround, boolean beforeEquals) {
        if (spaceAround == SpaceAroundEquals.One)
            sb.append(' ');
        else if (spaceAround == SpaceAroundEquals.Mixed) {
            if (beforeEquals)
                sb.append('\n');
            else {
                sb.append(' ');
                sb.append('\t');
                sb.append(' ');
            }
        }
    }

    private static String makeXMLDeclaration(String version, String encoding, char quote, SpaceAroundEquals spaceAround) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml");
        if (version != null) {
            sb.append(" version");
            addSpaceAroundEquals(sb, spaceAround, true);
            sb.append('=');
            addSpaceAroundEquals(sb, spaceAround, false);
            sb.append(quote);
            sb.append(version);
            sb.append(quote);
        }
        if ((encoding != null) && (encoding.length() != 0)) {
            sb.append(" encoding");
            addSpaceAroundEquals(sb, spaceAround, true);
            sb.append('=');
            addSpaceAroundEquals(sb, spaceAround, false);
            sb.append(quote);
            sb.append(encoding);
            sb.append(quote);
        }
        sb.append("?>");
        return sb.toString();
    }

    private static ByteBuffer makeByteBuffer(String xmlDeclEncoding, Object[] test) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int[] bom = (int[]) test[0];
        if (bom != null) {
            for (int i = 0; i < bom.length; ++i) {
                int b = bom[i];
                assert b < 256;
                os.write(b);
            }
        }
        String xmlDecl = makeXMLDeclaration("1.0", (String) test[1], (Character) test[2], (SpaceAroundEquals) test[3]);
        try {
            byte[] bytes = xmlDecl.getBytes(Charset.forName(xmlDeclEncoding));
            os.write(bytes, 0, bytes.length);
        } catch (UnsupportedCharsetException e) {
            return null;
        }
        return ByteBuffer.wrap(os.toByteArray());
    }

    private static ByteBuffer makeByteBuffer(String xmlDeclEncoding, String xmlDecl) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            byte[] bytes = xmlDecl.getBytes(Charset.forName(xmlDeclEncoding));
            os.write(bytes, 0, bytes.length);
        } catch (UnsupportedCharsetException e) {
            return null;
        }
        return ByteBuffer.wrap(os.toByteArray());
    }

}

