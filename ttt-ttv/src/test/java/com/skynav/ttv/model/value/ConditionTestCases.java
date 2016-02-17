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
 
package com.skynav.ttv.model.value;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ConditionTestCases {

    private static final String[][] validConditions = {
        { "0",                  "LITERAL(#N(0))" },
        { "0.",                 "LITERAL(#N(0.))" },
        { ".0",                 "LITERAL(#N(.0))" },
        { "0.0",                "LITERAL(#N(0.0))" },
        { "0.0E0",              "LITERAL(#N(0.0E0))" },
        { "0.0E+0",             "LITERAL(#N(0.0E+0))" },
        { "0.0E-0",             "LITERAL(#N(0.0E-0))" },
        { "1",                  "LITERAL(#N(1))" },
        { "123",                "LITERAL(#N(123))" },
        { "123.",               "LITERAL(#N(123.))" },
        { "123.456",            "LITERAL(#N(123.456))" },
        { "true",               "LITERAL(#B(true))" },
        { "false",              "LITERAL(#B(false))" },
        { "\"a b c\"",          "LITERAL(#S(a b c))" },
        { "\'a b c\'",          "LITERAL(#S(a b c))" },
        { "\'\\\'\'",           "LITERAL(#S(\'))" },
        { "\'\\\"\'",           "LITERAL(#S(\"))" }, 
        { "x",                  "LITERAL(#I(x))" },
        { "x*y+z",              "ADD(MULTIPLY(LITERAL(#I(x)),LITERAL(#I(y))),LITERAL(#I(z)))" },
        { "x * y + z",          "ADD(MULTIPLY(LITERAL(#I(x)),LITERAL(#I(y))),LITERAL(#I(z)))" },
        { "x+y*z",              "ADD(LITERAL(#I(x)),MULTIPLY(LITERAL(#I(y)),LITERAL(#I(z))))" },
        { "x + y * z",          "ADD(LITERAL(#I(x)),MULTIPLY(LITERAL(#I(y)),LITERAL(#I(z))))" },
    };

    @Test
    public void testValidConditions() throws Exception {
        for (String[] spec : validConditions) {
            String condition = spec[0];
            String expected = spec[1];
            try {
                Condition c = Condition.fromValue(condition);
                assertNotNull(c);
                if (expected != null)
                    assertEquals(expected, c.toString());
            } catch (Condition.ParserException e) {
                fail("unexpected condition invalidity: \"" + condition + "\": " + e.getMessage());
            }
        }
    }

    private static final String[][] invalidConditions = {
        { "~",                  "remaining input \"~\"" },
        { "00",                 "expected #E, got #N(0), remaining input \"0\"" },
        { "\"a b c",            "remaining input \"\"a b c\"" },
        { "\'a b c",            "remaining input \"\'a b c\"" },
    };

    @Test
    public void testInvalidConditions() throws Exception {
        for (String[] spec : invalidConditions) {
            String condition = spec[0];
            String expected = spec[1];
            try {
                Condition c = Condition.fromValue(condition);
                assertNull(c);
                fail("unexpected condition validity: \"" + condition + "\"");
            } catch (Condition.ParserException e) {
                if (expected != null)
                    assertEquals(expected, e.getMessage());
            }
        }
    }

}

