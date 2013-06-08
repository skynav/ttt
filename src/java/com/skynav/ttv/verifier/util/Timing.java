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
import com.skynav.ttv.model.value.OffsetTime;
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
        if (value.indexOf(':') >= 0)
            badClockTime(value, locator, errorReporter);
        else
            badOffsetTime(value, locator, errorReporter);
    }

    public static boolean isDuration(String value, Locator locator, ErrorReporter errorReporter, Time[] outputTime) {
        return isCoordinate(value, locator, errorReporter, outputTime);
    }

    public static void badDuration(String value, Locator locator, ErrorReporter errorReporter) {
        badCoordinate(value, locator, errorReporter);
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
                        String[] parts = remainder.split("\\.", 3);
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

    public static void badClockTime(String value, Locator locator, ErrorReporter errorReporter) {
        assert value.indexOf(':') >= 0;
        String[] parts = value.split("\\:", 5);
        int numParts = parts.length;
        if (numParts > 0) {
            String hh = parts[0];
            if (hh.length() == 0)
                errorReporter.logInfo(locator, "Bad <timeExpression>, hours part is empty in clock time.");
            else if (!Strings.isDigits(hh))
                errorReporter.logInfo(locator, "Bad <timeExpression>, hours part '" + hh + "' contains non-digit character in clock time.");
            else if (hh.length() < 2)
                errorReporter.logInfo(locator, "Bad <timeExpression>, hours part must contain two or more digits in clock time.");
        } else {
            errorReporter.logInfo(locator, "Bad <timeExpression>, empty expression.");
        }
        if (numParts > 1) {
            String mm = parts[1];
            if (mm.length() == 0)
                errorReporter.logInfo(locator, "Bad <timeExpression>, minutes part is empty in clock time.");
            else if (!Strings.isDigits(mm))
                errorReporter.logInfo(locator, "Bad <timeExpression>, minutes part '" + mm + "' contains non-digit character in clock time.");
            else if (mm.length() < 2)
                errorReporter.logInfo(locator, "Bad <timeExpression>, minutes part is missing digit(s), must contain two digits in clock time.");
            else if (mm.length() > 2)
                errorReporter.logInfo(locator, "Bad <timeExpression>, minutes part contains extra digit(s), must contain two digits in clock time.");
        } else {
            errorReporter.logInfo(locator, "Bad <timeExpression>, missing minutes and seconds parts in clock time.");
        }
        if (numParts > 2) {
            String ss = parts[2];
            if (ss.length() == 0)
                errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part is empty in clock time.");
            else if (Strings.containsDecimalSeparator(ss)) {
                String[] subParts = ss.split("\\.", 3);
                if (subParts.length > 0) {
                    String w = subParts[0];
                    if (w.length() == 0)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part whole sub-part is empty in clock time.");
                    else if (!Strings.isDigits(w))
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part whole sub-part '" + w + "' contains non-digit character in clock time.");
                    else if (w.length() < 2)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part is missing digit(s), must contain two digits in clock time.");
                    else if (w.length() > 2)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part contains extra digit(s), must contain two digits in clock time.");
                }
                if (subParts.length > 1) {
                    String f = subParts[1];
                    if (f.length() == 0)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part fraction sub-part is empty in clock time.");
                    else if (!Strings.isDigits(f))
                        errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part fraction sub-part '" + f + "' contains non-digit character in clock time.");
                }
                if (subParts.length > 2) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 2, n = subParts.length; i < n; ++i) {
                        sb.append('.');
                        sb.append(subParts[i]);
                    }
                    errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part contains extra sub-parts '" + sb.toString() + "'.");
                }
            } else if (!Strings.isDigits(ss)) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part '" + ss +
                                      "' contains unexpected character (not digit or decimal separator) in clock time.");
            } else if (ss.length() < 2) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part is missing digit(s), must contain two digits in clock time.");
            } else if (ss.length() > 2) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, seconds part contains extra digit(s), must contain two digits in clock time.");
            }
        } else {
            errorReporter.logInfo(locator, "Bad <timeExpression>, missing seconds part in clock time.");
        }
        if (numParts > 3) {
            String ff = parts[3];
            if (ff.length() == 0)
                errorReporter.logInfo(locator, "Bad <timeExpression>, frames part is empty in clock time.");
            else if (Strings.containsDecimalSeparator(ff)) {
                String[] subParts = ff.split("\\.", 3);
                if (subParts.length > 0) {
                    String w = subParts[0];
                    if (w.length() == 0)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, frames part whole sub-part is empty in clock time.");
                    else if (!Strings.isDigits(w))
                        errorReporter.logInfo(locator, "Bad <timeExpression>, frames part whole sub-part '" + w + "' contains non-digit character in clock time.");
                    else if (w.length() < 2)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, frames part whole sub-part is missing digit(s), must contain two or more digits in clock time.");
                }
                if (subParts.length > 1) {
                    String f = subParts[1];
                    if (f.length() == 0)
                        errorReporter.logInfo(locator, "Bad <timeExpression>, frames part sub-frames sub-part is empty in clock time.");
                    else if (!Strings.isDigits(f))
                        errorReporter.logInfo(locator, "Bad <timeExpression>, frames part sub-frames sub-part '" + f + "' contains non-digit character in clock time.");
                }
                if (subParts.length > 2) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 2, n = subParts.length; i < n; ++i) {
                        sb.append('.');
                        sb.append(subParts[i]);
                    }
                    errorReporter.logInfo(locator, "Bad <timeExpression>, frames part contains extra sub-parts '" + sb.toString() + "'.");
                }
            } else if (!Strings.isDigits(ff)) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, frames part '" + ff +
                                      "' contains unexpected character (not digit or decimal separator) in clock time.");
            } else if (ff.length() < 2) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, frames part is missing digit(s), must contain two or more digits in clock time.");
            }
        }
        if (numParts > 4) {
            String uu = parts[4];
            if (uu.length() == 0)
                errorReporter.logInfo(locator, "Bad <timeExpression>, unexpected empty part after seconds or frames part in clock time.");
            else
                errorReporter.logInfo(locator, "Bad <timeExpression>, unexpected part ':" + uu + "' after seconds or frames part in clock time.");
        }
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

    public static void badOffsetTime(String value, Locator locator, ErrorReporter errorReporter) {
        int valueIndex = 0;
        int valueLength = value.length();
        char c;
        
        do {

            // whitespace before time count
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator, "Bad <timeExpression>, XML space padding not permitted before offset time.");
            }

            // time count (digit+)
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isDigit(c)) {
                while (Characters.isDigit(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
            }

            // optional fraction (decimal separator)
            if (valueIndex == valueLength) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, missing metric in integral offset time.");
                break;
            }
            if ((c != '.') && !Characters.isLetter(c)) {
                errorReporter.logInfo(locator,
                    "Bad <timeExpression>, time count must contain digits followed by optional fraction then metric, got '" + c + "' in offset time.");
                break;
            }

            // optional fraction (digits)
            if (c == '.') {
                if (++valueIndex == valueLength) {
                    errorReporter.logInfo(locator, "Bad <timeExpression>, missing fraction part and metric.");
                    break;
                }
                c = value.charAt(valueIndex);
                if (Characters.isDigit(c)) {
                    while (Characters.isDigit(c)) {
                        if (++valueIndex >= valueLength)
                            break;
                        c = value.charAt(valueIndex);
                    }
                } else {
                    errorReporter.logInfo(locator, "Bad <timeExpression>, missing fraction part after decimal separator, must contain one or more digits.");
                    break;
                }
            }
            
            // metric
            if (valueIndex == valueLength) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, missing metric in non-integral offset time.");
                break;
            }
            StringBuffer sb = new StringBuffer();
            c = value.charAt(valueIndex);
            if (Characters.isLetter(c)) {
                while (Characters.isLetter(c)) {
                    sb.append(c);
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
            }
            if (sb.length() == 0) {
                errorReporter.logInfo(locator, "Bad <timeExpression>, unexpected character '" + c + "', expected metric in offset time.");
                break;
            } else {
                String metric = sb.toString();
                try {
                    OffsetTime.Metric.valueOfShorthand(metric);
                } catch (IllegalArgumentException e) {
                    try {
                        OffsetTime.Metric.valueOfShorthand(metric.toLowerCase());
                        errorReporter.logInfo(locator, "Bad <timeExpression>, metric '" + metric + "' must be lower case in offset time.");
                    } catch (IllegalArgumentException ee) {
                        errorReporter.logInfo(locator, "Bad <timeExpression>, unknown metric '" + metric + "' in offset time.");
                    }
                    break;
                }
            }

            // whitespace after metric
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                if (valueIndex == valueLength)
                    errorReporter.logInfo(locator, "Bad <timeExpression>, XML space padding not permitted after offset time.");
            }

            // unrecognized non-whitespace characters after offset time
            if (valueIndex != valueLength) {
                String remainder = value.substring(valueIndex);
                errorReporter.logInfo(locator, "Bad <timeExpression>, unrecognized characters '" + remainder + "' after offset time.");
            }

        } while (false);

    }

}
