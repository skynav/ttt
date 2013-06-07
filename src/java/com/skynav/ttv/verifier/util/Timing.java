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
 
package com.skynav.ttv.verifier.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.impl.ClockTimeImpl;
import com.skynav.ttv.model.value.impl.OffsetTimeImpl;
import com.skynav.ttv.util.ErrorReporter;

public class Timing {

    public static boolean isCoordinate(String value, Locator locator, ErrorReporter errorReporter, Time[] outputTime) {
        if (isClockTime(value, locator, errorReporter, outputTime))
            return true;
        else if (isOffsetTime(value, locator, errorReporter, outputTime))
            return true;
        else
            return false;
    }

    public static void badCoordinate(String value, Locator locator, ErrorReporter errorReporter) {
    }

    public static boolean isDuration(String value, Locator locator, ErrorReporter errorReporter, Time[] outputTime) {
        if (isClockTime(value, locator, errorReporter, outputTime))
            return true;
        else if (isOffsetTime(value, locator, errorReporter, outputTime))
            return true;
        else
            return false;
    }

    public static void badDuration(String value, Locator locator, ErrorReporter errorReporter) {
    }

    private static Pattern clockTimePattern = Pattern.compile("(\\d{2,3}):(\\d{2}):(\\d{2})(\\.\\d+|:\\d{2,}(?:\\.\\d+)?)?");
    public static boolean isClockTime(String value, Locator locator, ErrorReporter errorReporter, Time[] outputTime) {
        Matcher m = clockTimePattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() >= 3;
            String hours = m.group(1);
            String minutes = m.group(2);
            String seconds = m.group(3);
            String frames = null;
            String subFrames = null;
            if (m.groupCount() > 3) {
                String remainder = m.group(4);
                if (remainder != null) {
                    if (remainder.indexOf(':') == 0) {
                        String[] parts = remainder.split(".");
                        if (parts.length > 0)
                            frames = parts[0];
                        if (parts.length > 1)
                            subFrames = parts[1];
                    } else
                        seconds += remainder;
                }
            }
            if (outputTime != null)
                outputTime[0] = new ClockTimeImpl(hours, minutes, seconds, frames, subFrames);
            return true;
        } else
            return false;
    }

    private static Pattern offsetTimePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)(h|m|s|ms|f|t)");
    public static boolean isOffsetTime(String value, Locator locator, ErrorReporter errorReporter, Time[] outputTime) {
        Matcher m = offsetTimePattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() == 2;
            String offset = m.group(1);
            String units = m.group(2);
            if (outputTime != null)
                outputTime[0] = new OffsetTimeImpl(offset,  units);
            return true;
        } else
            return false;
    }

}
