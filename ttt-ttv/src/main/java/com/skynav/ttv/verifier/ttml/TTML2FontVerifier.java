/*
 * Copyright 2016-2018 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.ttml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.value.Font;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.AbstractVerifier;
import com.skynav.ttv.verifier.FontVerifier;
import com.skynav.ttv.verifier.VerifierContext;

import com.xfsi.xav.test.Test;
import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.MimeType;
import com.xfsi.xav.util.Progress;
import com.xfsi.xav.util.Result;
import com.xfsi.xav.validation.fonts.otf.OpenTypeValidator;
import com.xfsi.xav.validation.fonts.ttf.TrueTypeValidator;
import com.xfsi.xav.validation.fonts.ttc.TrueTypeCollectionValidator;
import com.xfsi.xav.validation.fonts.woff.WebFontValidator;
import com.xfsi.xav.validation.util.AbstractTestInfo;
import com.xfsi.xav.validation.util.AbstractTestManager;

public class TTML2FontVerifier extends AbstractVerifier implements FontVerifier {

    public TTML2FontVerifier(Model model) {
        super(model);
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        setState(content, context);
        if (type == ItemType.Other)
            return verifyOtherItem(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    protected boolean verifyOtherItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof Font)
            failed = !verify((Font) content, locator, context);
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid font item."));
        }
        return !failed;
    }

    protected boolean verify(Font content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Location location = new Location(content, null, null, locator);
        MimeType[] mimeType = new MimeType[1];
        if (!sniffFont(content, mimeType, location, context)) {
            reporter.logError(reporter.message(locator, "*KEY*", "Unable to determine font type."));
            failed = true;
        } else {
            MimeType mt = mimeType[0];
            assert mt != null;
            if (!isSupportedMimeType(mt)) {
                reporter.logError(reporter.message(locator, "*KEY*", "Font type ''{0}'' is not supported.", mt.getType()));
                failed = true;
            }
            if (!failed && !verifyFont(content, mt, location, context))
                failed = true;
        }
        return !failed;
    }

    private static final short[]        otfSignature            =
        new short[] { 0x4F, 0x54, 0x54, 0x4F };
    private static final MimeType       otfType                 =
        new MimeType(MimeType.FONT_OTF_TYPE);
    private static final short[]        ttfSignature            =
        new short[] { 0x00, 0x01, 0x00, 0x00 };
    private static final short[]        truSignature            =
        new short[] { 0x74, 0x72, 0x75, 0x65 };
    private static final MimeType       ttfType                 =
        new MimeType(MimeType.FONT_TTF_TYPE);
    private static final short[]        ttcSignature            =
        new short[] { 0x74, 0x74, 0x63, 0x66 };
    private static final MimeType       ttcType                 =
        new MimeType(MimeType.FONT_TTC_TYPE);
    private static final short[]        wf1Signature            =
        new short[] { 0x77, 0x4F, 0x46, 0x46 };
    private static final MimeType       wf1Type                 =
        new MimeType(MimeType.FONT_WF1_TYPE);
    private static final short[]        wf2Signature            =
        new short[] { 0x77, 0x4F, 0x46, 0x32 };
    private static final MimeType       wf2Type                 =
        new MimeType(MimeType.FONT_WF2_TYPE);
    private static final MimeType       unknownType             =
        new MimeType();
    private static final Signature[]    signatures              =
    {
        new Signature(otfSignature, otfType.getType()),
        new Signature(ttfSignature, ttfType.getType()),
        new Signature(truSignature, ttfType.getType()),
        new Signature(ttcSignature, ttcType.getType()),
        new Signature(wf1Signature, wf1Type.getType()),
        new Signature(wf2Signature, wf2Type.getType()),
    };
    private static final int            signatureLengthMaximum;

    static {
        int saLenMax = 0;
        for (Signature s : signatures) {
            short[] sa = s.getSignature();
            int saLen = sa.length;
            if (saLen > saLenMax)
                saLenMax = saLen;
        }
        signatureLengthMaximum = saLenMax;
    }

    private boolean sniffFont(Font font, MimeType[] outputType, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        MimeType mt = unknownType;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(font.getURI().toURL().openStream());
            byte[] buf = new byte[signatureLengthMaximum];
            int nb = IOUtil.readCompletely(bis, buf);
            for (Signature s : signatures) {
                MimeType mtSniffed = sniffFont(buf, nb, s.getSignature(), s.getType());
                if (mtSniffed != null) {
                    mt = mtSniffed;
                    break;
                }
            }
        } catch (MalformedURLException e) {
            reporter.logError(e);
            failed = true;
        } catch (IOException e) {
            reporter.logError(e);
            failed = true;
        } finally {
            IOUtil.closeSafely(bis);
        }
        if (mt != null) {
            if ((outputType != null) && (outputType.length > 0))
                outputType[0] = mt;
        }
        return !failed;
    }

    private MimeType sniffFont(byte[] buf, int len, short[] signature, String type) {
        if (matchAtStart(signature, buf))
            return new MimeType(type);
        else
            return null;
    }

    private boolean matchAtStart(short[] b1, byte[] b2) {
        if (b1 != null && b2 != null) {
            // Need to have sniffable array as long as signature
            if (b2.length >= b1.length) {
                for (int i = 0; i < b1.length; i++) {
                    // Negative values matches all bytes
                    if (b1[i] < 0)
                        continue;
                    else {
                        short b2s = (short) (0xFF & b2[i]);
                        if (b1[i] != b2s)
                            return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedMimeType(MimeType mt) {
        String[] components = mt.getType().split(";");
        String t = (components.length > 0) ? components[0] : null;
        String p = (components.length > 1) ? components[1] : null;
        if (t != null)
            t = t.trim();
        if (p != null)
            p = p.trim();
        return getModel().isSupportedResourceType(t, p);
    }

    private boolean verifyFont(Font font, MimeType mimeType, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Test t = getFontValidator(mimeType);
        if (t != null) {
            BufferedInputStream bis = null;
            try {
                TestInfo ti = new TestInfoAdapter(font, mimeType, location);
                TestManager tm = new TestManagerAdapter(context);
                URI uri = font.getURI();
                bis = new BufferedInputStream(uri.toURL().openStream());
                ti.setResourceStream(bis);
                reporter.logInfo(reporter.message("*KEY*", "Verifying font ''{0}'' as ''{1}''.", getFontName(uri), mimeType.toString()));
                Result r = t.run(tm, ti);
                if (r.isFailure())
                    return false;
                else
                    return true;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                reporter.logError(e);
                return false;
            } finally {
                IOUtil.closeSafely(bis);
            }
        } else {
            reporter.logError(reporter.message(locator, "*KEY*", "No font validator for ''{0}''.", mimeType.toString()));
            return false;
        }
    }

    private String getFontName(URI uri) {
        String p = uri.getPath();
        int i = p.lastIndexOf('/');
        if (i >= 0)
            p = p.substring(i + 1);
        return p;
    }

    private Test getFontValidator(MimeType mimeType) {
        System.out.println("getFontValidator(" + mimeType + ")");
        if (mimeType.equals(otfType))
            return new OpenTypeValidator();
        else if (mimeType.equals(ttfType))
            return new TrueTypeValidator();
        else if (mimeType.equals(ttcType))
            return new TrueTypeCollectionValidator();
        else if (mimeType.equals(wf1Type))
            return new WebFontValidator();
        else if (mimeType.equals(wf2Type))
            return new WebFontValidator();
        else
            return null;
    }

    private static class Signature {
        private short[] signature;
        private String type;
        Signature(short[] signature, String type) {
            this.signature = signature;
            this.type = type;
        }
        short[] getSignature() {
            return signature;
        }
        String getType() {
            return type;
        }
    }

    private static class TestInfoAdapter extends AbstractTestInfo {
        private Font font;
        private MimeType mimeType;
        private Location location;
        TestInfoAdapter(Font font, MimeType mimeType, Location location) {
            this.font = font;
            this.mimeType = mimeType;
            this.location = location;
        }
        @SuppressWarnings("unused")
        public Font getFont() {
            return font;
        }
        public MimeType getMimeType() {
            return mimeType;
        }
        public Location getLocation() {
            return location;
        }
    }

    private static class TestManagerAdapter extends AbstractTestManager {
        private VerifierContext context;
        TestManagerAdapter(VerifierContext context) {
            this.context = context;
        }
        public void reportError(TestInfo ti, Error error) {
            Error.Severity s = error.getSeverity();
            if (s.isSevereAs(Error.Severity.ERROR_SEVERITY)) {
                Reporter reporter = context.getReporter();
                reporter.logError(reporter.message(getLocation(ti).getLocator(), "*KEY*", error.getMessage()));
            }
        }
        public void reportProgress(TestInfo ti, Progress progress) {
        }
        private Location getLocation(TestInfo ti) {
            if ((ti != null) && (ti instanceof TestInfoAdapter))
                return ((TestInfoAdapter) ti).getLocation();
            else
                return new Location();
        }
    }

}
