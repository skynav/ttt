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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.ClockTime;
import com.skynav.ttv.model.value.OffsetTime;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.model.value.impl.ClockTimeImpl;
import com.skynav.ttv.model.value.impl.OffsetTimeImpl;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Timing {

    public static boolean isCoordinate(String value, Locator locator, VerifierContext context, TimeParameters timeParameters, Time[] outputTime) {
        if (isClockTime(value, locator, context, timeParameters, outputTime))
            return true;
        else if (isOffsetTime(value, locator, context, timeParameters, outputTime))
            return true;
        else
            return false;
    }

    public static void badCoordinate(String value, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        if (value.indexOf(':') >= 0)
            badClockTime(value, locator, context, timeParameters);
        else
            badOffsetTime(value, locator, context, timeParameters);
    }

    public static boolean isDuration(String value, Locator locator, VerifierContext context, TimeParameters timeParameters, Time[] outputTime) {
        return isCoordinate(value, locator, context, timeParameters, outputTime);
    }

    public static void badDuration(String value, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        badCoordinate(value, locator, context, timeParameters);
    }

    private static final Pattern clockTimePattern = Pattern.compile("(\\d{2,3}):(\\d{2}):(\\d{2})(\\.\\d+|:\\d{2,}(?:\\.\\d+)?)?");
    public static boolean isClockTime(String value, Locator locator, VerifierContext context, TimeParameters timeParameters, Time[] outputTime) {
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
                        String[] parts = remainder.substring(1).split("\\.", 3);
                        if (parts.length > 0)
                            frames = parts[0];
                        if (parts.length > 1)
                            subFrames = parts[1];
                    } else
                        seconds += remainder;
                }
            }
            if ((timeParameters.getTimeBase() == TimeBase.CLOCK) && ((frames != null) || (subFrames != null)))
                return false;
            ClockTime t = new ClockTimeImpl(hours, minutes, seconds, frames, subFrames);
            if (t.getMinutes() > 59)
                return false;
            if (t.getSeconds() > 60.0)
                return false;
            if (t.getFrames() >= timeParameters.getFrameRate())
                return false;
            if (t.getSubFrames() >= timeParameters.getSubFrameRate())
                return false;
            if (outputTime != null)
                outputTime[0] = t;
            if (locator != null) {
                if (frames != null)
                    updateUsage(context, locator, OffsetTime.Metric.Frames);
            }
            return true;
        } else
            return false;
    }

    public static void badClockTime(String value, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        Reporter reporter = context.getReporter();
        assert value.indexOf(':') >= 0;
        String[] parts = value.split("\\:", 5);
        int numParts = parts.length;
        if (numParts > 0) {
            String hh = parts[0];
            if (hh.length() == 0)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, hours part is empty in clock time."));
            else if (!Strings.isDigits(hh))
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, hours part ''{0}'' contains non-digit character in clock time.", hh));
            else if (hh.length() < 2)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, hours part must contain two or more digits in clock time."));
        } else {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, empty expression."));
        }
        if (numParts > 1) {
            String mm = parts[1];
            if (mm.length() == 0)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, minutes part is empty in clock time."));
            else if (!Strings.isDigits(mm))
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, minutes part ''{0}'' contains non-digit character in clock time.", mm));
            else if (mm.length() < 2)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, minutes part is missing digit(s), must contain two digits in clock time."));
            else if (mm.length() > 2)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, minutes part contains extra digit(s), must contain two digits in clock time."));
            else if (Integer.parseInt(mm) >= 60)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, minutes ''{0}'' must be less than 60.", mm));
        } else {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, missing minutes and seconds parts in clock time."));
        }
        if (numParts > 2) {
            String ss = parts[2];
            if (ss.length() == 0)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, seconds part is empty in clock time."));
            else if (Strings.containsDecimalSeparator(ss)) {
                String[] subParts = ss.split("\\.", 3);
                if (subParts.length > 0) {
                    String w = subParts[0];
                    if (w.length() == 0) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part whole sub-part is empty in clock time."));
                    } else if (!Strings.isDigits(w)) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part whole sub-part ''{0}'' contains non-digit character in clock time.", w));
                    } else if (w.length() < 2) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part is missing digit(s), must contain two digits in clock time."));
                    } else if (w.length() > 2) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part contains extra digit(s), must contain two digits in clock time."));
                    }
                }
                if (subParts.length > 1) {
                    String f = subParts[1];
                    if (f.length() == 0) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part fraction sub-part is empty in clock time."));
                    } else if (!Strings.isDigits(f)) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, seconds part fraction sub-part ''{0}'' contains non-digit character in clock time.", f));
                    }
                }
                if (subParts.length == 2) {
                    String w = subParts[0];
                    String f = subParts[1];
                    if (Strings.isDigits(w) && Strings.isDigits(f)) {
                        if (Double.parseDouble(ss) > 60.0) {
                            reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, seconds ''{0}'' must be less than 60.0.", ss));
                        }
                    }
                } else if (subParts.length > 2) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 2, n = subParts.length; i < n; ++i) {
                        sb.append('.');
                        sb.append(subParts[i]);
                    }
                    reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, seconds part contains extra sub-parts ''{0}''.", sb.toString()));
                }
            } else if (!Strings.isDigits(ss)) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, seconds part ''{0}'' contains unexpected character (not digit or decimal separator) in clock time.", ss));
            } else if (ss.length() < 2) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, seconds part is missing digit(s), must contain two digits in clock time."));
            } else if (ss.length() > 2) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, seconds part contains extra digit(s), must contain two digits in clock time."));
            } else if (Integer.parseInt(ss) > 60) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, seconds ''{0}'' must be less than 60.0.", ss));
            }
        } else {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, missing seconds part in clock time."));
        }
        if (numParts > 3) {
            String ff = parts[3];
            int frames = 0;
            int subFrames = 0;
            if (ff.length() == 0)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, frames part is empty in clock time."));
            else if (Strings.containsDecimalSeparator(ff)) {
                String[] subParts = ff.split("\\.", 3);
                if (subParts.length > 0) {
                    String w = subParts[0];
                    if (w.length() == 0) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames part whole sub-part is empty in clock time."));
                    } else if (!Strings.isDigits(w)) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames part whole sub-part ''{0}'' contains non-digit character in clock time.", w));
                    } else if (w.length() < 2) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames part whole sub-part is missing digit(s), must contain two or more digits in clock time."));
                    }
                }
                if (subParts.length > 1) {
                    String f = subParts[1];
                    if (f.length() == 0) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames part sub-frames sub-part is empty in clock time."));
                    } else if (!Strings.isDigits(f)) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames part sub-frames sub-part ''{0}'' contains non-digit character in clock time.", f));
                    }
                }
                if (subParts.length == 2) {
                    String w = subParts[0];
                    String f = subParts[1];
                    if (Strings.isDigits(w) && Strings.isDigits(f)) {
                        frames = Integer.parseInt(w);
                        subFrames = Integer.parseInt(f);
                    }
                } else if (subParts.length > 2) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 2, n = subParts.length; i < n; ++i) {
                        sb.append('.');
                        sb.append(subParts[i]);
                    }
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, frames part contains extra sub-parts ''{0}''.", sb.toString()));
                }
            } else if (!Strings.isDigits(ff)) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, frames part ''{0}'' contains unexpected character (not digit or decimal separator) in clock time.", ff));
            } else if (ff.length() < 2) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                                                  "Bad <timeExpression>, frames part is missing digit(s), must contain two or more digits in clock time."));
            } else {
                frames = Integer.parseInt(ff);
            }
            if (ff.length() > 0) {
                if (timeParameters.getTimeBase() == TimeBase.CLOCK) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, frames part not permitted when using 'clock' time base."));
                }
                double frameRate = timeParameters.getFrameRate();
                if (frames >= frameRate) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, frames ''{0}'' must be less than frame rate {1}.", frames, frameRate));
                }
                double subFrameRate = timeParameters.getSubFrameRate();
                if (subFrames >= subFrameRate) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, sub-frames ''{0}'' must be less than sub-frame rate {1}.", subFrames, subFrameRate));
                }
            }
        }
        if (numParts > 4) {
            String uu = parts[4];
            if (uu.length() == 0) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, unexpected empty part after seconds or frames part in clock time."));
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, unexpected part '':{0}'' after seconds or frames part in clock time.", uu));
            }
        }
    }

    private static final Pattern offsetTimePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)(h|m|s|ms|f|t)");
    public static boolean isOffsetTime(String value, Locator locator, VerifierContext context, TimeParameters timeParameters, Time[] outputTime) {
        Matcher m = offsetTimePattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() == 2;
            String offset = m.group(1);
            String metric = m.group(2);
            OffsetTime t = new OffsetTimeImpl(offset, metric);
            if ((timeParameters.getTimeBase() == TimeBase.CLOCK) && (t.getMetric() == OffsetTime.Metric.Frames))
                return false;
            if (outputTime != null)
                outputTime[0] = t;
            if (locator != null)
                updateUsage(context, locator, t.getMetric());
            return true;
        } else
            return false;
    }

    private static void updateUsage(VerifierContext context, Locator locator, OffsetTime.Metric metric) {
        String key = "usage" + metric.name();
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) context.getResourceState(key);
        if (usage == null) {
            usage = new java.util.HashSet<Locator>();
            context.setResourceState(key, usage);
        }
        usage.add(locator);
    }

    public static void badOffsetTime(String value, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        Reporter reporter = context.getReporter();
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
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, XML space padding not permitted before offset time."));
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
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, missing metric in integral offset time."));
                break;
            }
            if ((c != '.') && !Characters.isLetter(c)) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, time count must contain digits followed by optional fraction then metric, got ''{0}'' in offset time.", c));
                break;
            }

            // optional fraction (digits)
            if (c == '.') {
                if (++valueIndex == valueLength) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, missing fraction part and metric."));
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
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, missing fraction part after decimal separator, must contain one or more digits."));
                    break;
                }
            }

            // metric
            if (valueIndex == valueLength) {
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <timeExpression>, missing metric in non-integral offset time."));
                break;
            }
            StringBuffer sb = new StringBuffer();
            c = value.charAt(valueIndex);
            while (Characters.isLetter(c)) {
                sb.append(c);
                if (++valueIndex >= valueLength)
                    break;
                c = value.charAt(valueIndex);
            }
            if (sb.length() == 0) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, unexpected character ''{0}'', expected metric in offset time.", c));
                break;
            } else {
                String metric = sb.toString();
                try {
                    OffsetTime.Metric m = OffsetTime.Metric.valueOfShorthand(metric);
                    if ((timeParameters.getTimeBase() == TimeBase.CLOCK) && (m == OffsetTime.Metric.Frames)) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, frames metric not permitted when using 'clock' time base."));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        OffsetTime.Metric.valueOfShorthand(metric.toLowerCase());
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, metric ''{0}'' must be lower case in offset time.", metric));
                    } catch (IllegalArgumentException ee) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <timeExpression>, unknown metric ''{0}'' in offset time.", metric));
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
                if (valueIndex == valueLength) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <timeExpression>, XML space padding not permitted after offset time."));
                }
            }

            // unrecognized non-whitespace characters after offset time
            if (valueIndex != valueLength) {
                String remainder = value.substring(valueIndex);
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <timeExpression>, unrecognized characters ''{0}'' after offset time.", remainder));
            }

        } while (false);

    }

}
