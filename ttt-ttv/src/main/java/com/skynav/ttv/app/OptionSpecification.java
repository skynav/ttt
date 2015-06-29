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

package com.skynav.ttv.app;

import java.util.Arrays;

public class OptionSpecification implements Comparable<OptionSpecification> {

    public static final int OPTION_FIELD_LENGTH = 36;

    private String name;
    private String[] parameters;
    private String description;

    public OptionSpecification(String name, String description) {
        this(name, null, description);
    }

    public OptionSpecification(String name, String parameters, String description) {
        assert name != null;
        assert !name.isEmpty();
        this.name = name;
        if ((parameters != null) && !parameters.isEmpty()) {
            this.parameters = parameters.split(" ");
            if (this.parameters.length == 0)
                this.parameters = null;
        }
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int compareTo(OptionSpecification os) {
        int d = name.compareTo(os.name);
        if (d < 0) {
            return d;
        } else if (d > 0) {
            return d;
        } else if (parameters == null) {
            if (os.parameters == null)
                return 0;
            else
                return -1;
        } else if (os.parameters == null) {
            return 1;
        } else if (parameters.length < os.parameters.length) {
            return -1;
        } else if (parameters.length > os.parameters.length) {
            return 1;
        } else {
            assert parameters.length == os.parameters.length;
            for (int i = 0, n = parameters.length; i < n; ++i) {
                String p1 = parameters[i];
                String p2 = os.parameters[i];
                int e = p1.compareTo(p2);
                if (e == 0)
                    continue;
                else
                    return e;
            }
            return 0;
        }
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + name.hashCode();
        hc = hc * 31 + ((parameters != null) ? Arrays.hashCode(parameters) : 0);
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OptionSpecification) {
            OptionSpecification os = (OptionSpecification) o;
            return compareTo(os) == 0;
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('-');
        if (name.length() > 1)
            sb.append('-');
        sb.append(name);
        if (parameters != null) {
            for (String parameter : parameters) {
                sb.append(' ');
                sb.append(parameter);
            }
        }
        while (sb.length() < OPTION_FIELD_LENGTH) {
            sb.append(' ');
        }
        sb.append('-');
        sb.append(' ');
        sb.append(description);
        return sb.toString();
    }

}
