/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import java.net.URI;

public class Image {

    public static final Image NONE              = new Image();

    private URI source;                         // source as uri
    private String type;                        // specified type (not necessarily resolved type)
    private String format;                      // specified format (not necessarily resolved format)
    private double width;
    private double height;

    private Image() {
        this(null, null, null);
    }

    public Image(URI source, String type, String format) {
        this.source = source;
        assert (type == null) || !type.isEmpty();
        this.type = type;
        assert (format == null) || !format.isEmpty();
        this.format = format;
    }

    public URI getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public boolean isNone() {
        return equals(NONE);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        if (source == null)
            return 0;
        else {
            int hc = 23;
            hc = hc * 31 + source.hashCode();
            if (type != null)
                hc = hc * 31 + type.hashCode();
            if (format != null)
                hc = hc * 31 + format.hashCode();
            return hc;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            Image other = (Image) o;
            if ((other.source == null) && (source == null))
                return true;
            else if ((other.source != null) ^ (source != null))
                return false;
            else if (!other.source.equals(source))
                return false;
            else if ((other.type != null) ^ (type != null))
                return false;
            else if ((other.type != null) && !other.type.equals(type))
                return false;
            else if ((other.format != null) ^ (format != null))
                return false;
            else if ((other.format != null) && !other.format.equals(format))
                return false;
            else
                return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(source.toString());
        if (type != null) {
            sb.append(',');
            sb.append(type);
        }
        if (format != null) {
            sb.append(',');
            sb.append(format);
        }
        sb.append(']');
        return sb.toString();
    }

}
