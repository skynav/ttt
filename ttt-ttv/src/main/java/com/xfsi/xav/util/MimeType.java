/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 * Portions Copyright 2009 Extensible Formatting Systems, Inc (XFSI).
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

package com.xfsi.xav.util;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

public class MimeType {

    public static final String UNKNOWN_TYPE                     = "application/octet-stream";

    public static final String AUDIO_MP2_TYPE                   = "audio/mpeg";
    public static final String AUDIO_MP3_TYPE                   = "audio/mpeg";
    public static final String AUDIO_AC3_TYPE                   = "audio/ac3";
    public static final String AUDIO_WAV_TYPE                   = "audio/wav";
    public static final String AUDIO_OGG_TYPE                   = "audio/ogg";

    public static final String IMAGE_PNG_TYPE                   = "image/png";
    public static final String IMAGE_GIF_TYPE                   = "image/gif";
    public static final String IMAGE_JPG_TYPE                   = "image/jpeg";
    public static final String IMAGE_MPG_TYPE                   = "image/mpeg";
    public static final String IMAGE_BMP_TYPE                   = "image/x-bmp";
    public static final String IMAGE_ICO_TYPE                   = "image/x-win-bitmap";

    private String type;
    private boolean hasContentTypeWildcard;
    private boolean hasSubTypeWildcard;

    public MimeType() {
        setType(UNKNOWN_TYPE);
    }

    public MimeType(String type) {
        setType(type);
    }

    public void setType(String type) {
        this.type = type;
        this.hasContentTypeWildcard = false;
        this.hasSubTypeWildcard = false;
        if (type.indexOf('/') == -1)
            throw new IllegalArgumentException("Invalid MIME type " + type);
        String ct = getMajorType();
        String st = getSubType();
        if (ct.equals("*"))
            this.hasContentTypeWildcard = true;
        if (st.equals("*"))
            this.hasSubTypeWildcard = true;
    }

    public String getType() {
        return type;
    }

    /**
     * Do these MIME types match, with wild card matching
     * @param o MIME type to compare with
     */
    public boolean matchType(MimeType o) {
        return matchMajorType(o) && matchSubType(o);
    }

    /**
     * Do these MIME types match, without wild card matching
     * @param o MIME type to compare with
     */
    public boolean matchExactType(MimeType o) {
        return getMajorType().equals(o.getMajorType()) && getSubType().equals(o.getSubType());
    }

    /**
     * Do MIME major types match, with wild card matching
     * @param o MIME type to compare with
     */
    public boolean matchMajorType(MimeType o) {
        if (hasContentTypeWildCard() || o.hasContentTypeWildCard())
            return true;
        else
            return getMajorType().equals(o.getMajorType());
    }

    /**
     * Do MIME sub types match, with wild card matching
     * @param o MIME type to compare with
     */
    public boolean matchSubType(MimeType o) {
        if (hasSubTypeWildCard() || o.hasSubTypeWildCard())
            return true;
        else
            return getSubType().equals(o.getSubType());
    }

    /**
     * return major content type
     * @return name of major content type
     */
    public String getMajorType() {
        int sidx = type.indexOf('/');
        if (sidx != -1)
            return type.substring(0, sidx);
        else
            return "";
    }

    /**
     * return content sub type
     * @return name of content sub type
     */
    public String getSubType() {
        int sidx = type.indexOf('/');
        if (sidx != -1)
            return type.substring(sidx+1);
        else
            return "";
    }

    public boolean hasContentTypeWildCard() {
        return hasContentTypeWildcard;
    }

    public boolean hasSubTypeWildCard() {
        return hasSubTypeWildcard;
    }

    private final static String charsetParamPrefix = "charset=";
    private final static String asciiName = "us-ascii";
    public Charset getCharset() {
        String csName = asciiName;
        int i;
        if ((i = type.indexOf(charsetParamPrefix)) > 0) {
            csName = type.substring(i + charsetParamPrefix.length());
        }
        Charset cs;
        if ((cs = getCharsetSafely(csName)) == null)
            cs = getCharsetSafely(asciiName);
        return cs;
    }

    private Charset getCharsetSafely(String name) {
        try {
            return Charset.forName(name);
        } catch (IllegalCharsetNameException e) {
            return null;
        } catch (UnsupportedCharsetException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MimeType))
            return false;
        MimeType mt = (MimeType) o;
        return matchExactType(mt);
    }

    @Override
    public String toString() {
        return type;
    }

}
