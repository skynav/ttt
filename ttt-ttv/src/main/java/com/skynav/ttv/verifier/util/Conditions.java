/*
 * Copyright 2015-2018 Skynav, Inc. All rights reserved.
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

import org.xml.sax.Locator;

import com.skynav.ttv.util.Condition;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Conditions {

    public static boolean isCondition(String value, Location location, VerifierContext context, Object[] outputCondition) {
        try {
            Condition c = Condition.valueOf(value);
            if (c != null) {
                Condition.EvaluatorState state = (Condition.EvaluatorState) context.getResourceState("conditionEvaluatorState");
                if (state != null) {
                    c.evaluate(state);
                    return true;
                } else {
                    throw new IllegalStateException("no condition evaluator state");
                }
            } else {
                throw new IllegalStateException("no condition");
            }
        } catch (Condition.ParserException e) {
            return false;
        } catch (Condition.EvaluatorException e) {
            return false;
        }
    }

    public static void badCondition(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        try {
            Condition c = Condition.valueOf(value);
            if (c != null) {
                Condition.EvaluatorState state = (Condition.EvaluatorState) context.getResourceState("conditionEvaluatorState");
                if (state != null) {
                    c.evaluate(state);
                } else {
                    throw new IllegalStateException("no condition evaluator state");
                }
            } else {
                throw new IllegalStateException("no condition");
            }
        } catch (Condition.ParserException e) {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Invalid syntax of <condition> ''{0}'': {1}", value, e.getMessage()));
        } catch (Condition.EvaluatorException e) {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Incomplete evaluation of <condition> ''{0}'': {1}", value, e.getMessage()));
        }
    }

}
