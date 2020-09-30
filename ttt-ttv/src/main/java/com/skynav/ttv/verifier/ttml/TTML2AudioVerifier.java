/*
 * Copyright 2016-2020 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.value.Audio;
import com.skynav.ttv.model.value.impl.BuiltinAudioImpl;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.AbstractVerifier;
import com.skynav.ttv.verifier.AudioVerifier;
import com.skynav.ttv.verifier.VerifierContext;

import com.xfsi.xav.test.Test;
import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.MimeType;
import com.xfsi.xav.util.Progress;
import com.xfsi.xav.util.Result;
import com.xfsi.xav.validation.audio.mpeg.MpegValidator;
import com.xfsi.xav.validation.audio.wave.WaveValidator;
import com.xfsi.xav.validation.util.AbstractTestInfo;
import com.xfsi.xav.validation.util.AbstractTestManager;

public class TTML2AudioVerifier extends AbstractVerifier implements AudioVerifier {

    public TTML2AudioVerifier(Model model) {
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
        if (content instanceof Audio)
            failed = !verify((Audio) content, locator, context);
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid audio item."));
        }
        return !failed;
    }

    protected boolean verify(Audio content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Location location = new Location(content, null, null, locator);
        MimeType[] mimeType = new MimeType[1];
        if (!sniffAudio(content, mimeType, location, context)) {
            reporter.logError(reporter.message(locator, "*KEY*", "Unable to determine audio type."));
            failed = true;
        } else {
            MimeType mt = mimeType[0];
            assert mt != null;
            if (!isSupportedMimeType(mt)) {
                reporter.logError(reporter.message(locator, "*KEY*", "Audio type ''{0}'' is not supported.", mt.getType()));
                failed = true;
            }
            if (!failed && !verifyAudio(content, mt, location, context))
                failed = true;
        }
        return !failed;
    }

    private static final short[]        mp3Signature1           =       // MPEG-1, ISO/IEC 11172-3
        new short[] { 0xFF, 0xFB };
    private static final short[]        mp3Signature2           =       // MPEG-2, ISO/IEC 13818-3
        new short[] { 0xFF, 0xF3 };
    private static final MimeType       mp3Type                 =
        new MimeType(MimeType.AUDIO_MP3_TYPE);
    private static final short[]        oggSignature            =
        new short[] { 0x4F, 0x67, 0x67, 0x53 };
    private static final MimeType       oggType                 =
        new MimeType(MimeType.AUDIO_OGG_TYPE);
    private static final short[]        wavSignature            =
        new short[] { 0x52, 0x49, 0x46, 0x46, -1, -1, -1, -1, 0x57, 0x41, 0x56, 0x45 };
    private static final MimeType       wavType                 =
        new MimeType(MimeType.AUDIO_WAV_TYPE);
    private static final MimeType       speechType              =
        new MimeType(MimeType.AUDIO_SPEECH_TYPE);
    private static final MimeType       unknownType             =
        new MimeType();
    private static final Signature[]    signatures              =
    {
        new Signature(mp3Signature1, mp3Type.getType()),
        new Signature(mp3Signature2, mp3Type.getType()),
        new Signature(oggSignature, oggType.getType()),
        new Signature(wavSignature, wavType.getType()),
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

    private boolean sniffAudio(Audio audio, MimeType[] outputType, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        MimeType mt = null;
        if (!audio.isBuiltin()) {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(audio.getURI().toURL().openStream());
                byte[] buf = new byte[signatureLengthMaximum];
                int nb = IOUtil.readCompletely(bis, buf);
                for (Signature s : signatures) {
                    MimeType mtSniffed = sniffAudio(buf, nb, s.getSignature(), s.getType());
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
        } else if (BuiltinAudioImpl.isSpeechAudio(audio)) {
            mt = speechType;
        } else {
            mt = unknownType;
        }
        if (mt != null) {
            if ((outputType != null) && (outputType.length > 0))
                outputType[0] = mt;
        }
        return !failed;
    }

    private MimeType sniffAudio(byte[] buf, int len, short[] signature, String type) {
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

    private boolean verifyAudio(Audio audio, MimeType mimeType, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Test t = getAudioValidator(mimeType);
        if (t != null) {
            BufferedInputStream bis = null;
            try {
                TestInfo ti = new TestInfoAdapter(audio, mimeType, location);
                TestManager tm = new TestManagerAdapter(context);
                URI uri = audio.getURI();
                bis = new BufferedInputStream(uri.toURL().openStream());
                ti.setResourceStream(bis);
                reporter.logInfo(reporter.message("*KEY*", "Verifying audio ''{0}'' as ''{1}''.", getAudioName(uri), mimeType.toString()));
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
        } else if (mimeType == speechType) {
            return true;
        } else {
            reporter.logError(reporter.message(locator, "*KEY*", "No audio validator for ''{0}''.", mimeType.toString()));
            return false;
        }
    }

    private String getAudioName(URI uri) {
        String p = uri.getPath();
        int i = p.lastIndexOf('/');
        if (i >= 0)
            p = p.substring(i + 1);
        return p;
    }

    private Test getAudioValidator(MimeType mimeType) {
        if (mimeType.equals(mp3Type))
            return new MpegValidator();
        else if (mimeType.equals(wavType))
            return new WaveValidator();
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
        private Audio audio;
        private MimeType mimeType;
        private Location location;
        TestInfoAdapter(Audio audio, MimeType mimeType, Location location) {
            this.audio = audio;
            this.mimeType = mimeType;
            this.location = location;
        }
        @SuppressWarnings("unused")
        public Audio getAudio() {
            return audio;
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
