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
 
package com.skynav.ttv.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConditionTestCases {

    private static final String[][] validConditions = {
        { "0",                                  "LITERAL(#N(0))" },
        { "0.",                                 "LITERAL(#N(0.))" },
        { ".0",                                 "LITERAL(#N(.0))" },
        { "0.0",                                "LITERAL(#N(0.0))" },
        { "0.0E0",                              "LITERAL(#N(0.0E0))" },
        { "0.0E+0",                             "LITERAL(#N(0.0E+0))" },
        { "0.0E-0",                             "LITERAL(#N(0.0E-0))" },
        { "1",                                  "LITERAL(#N(1))" },
        { "123",                                "LITERAL(#N(123))" },
        { "123.",                               "LITERAL(#N(123.))" },
        { "123.456",                            "LITERAL(#N(123.456))" },
        { "true",                               "LITERAL(#B(true))" },
        { "false",                              "LITERAL(#B(false))" },
        { "\"a b c\"",                          "LITERAL(#S(a b c))" },
        { "\'a b c\'",                          "LITERAL(#S(a b c))" },
        { "\'\\\'\'",                           "LITERAL(#S(\'))" },
        { "\'\\\"\'",                           "LITERAL(#S(\"))" }, 
        { "x",                                  "LITERAL(#I(x))" },
        { "x*y+z",                              "ADD(MULTIPLY(LITERAL(#I(x)),LITERAL(#I(y))),LITERAL(#I(z)))" },
        { "x * y + z",                          "ADD(MULTIPLY(LITERAL(#I(x)),LITERAL(#I(y))),LITERAL(#I(z)))" },
        { "x+y*z",                              "ADD(LITERAL(#I(x)),MULTIPLY(LITERAL(#I(y)),LITERAL(#I(z))))" },
        { "x + y * z",                          "ADD(LITERAL(#I(x)),MULTIPLY(LITERAL(#I(y)),LITERAL(#I(z))))" },
        { "f(x)",                               "APPLY(LITERAL(#I(f)),GROUP(LITERAL(#I(x))))" },
        { "f ( x )",                            "APPLY(LITERAL(#I(f)),GROUP(LITERAL(#I(x))))" },
        { "f(x,y,z)",                           "APPLY(LITERAL(#I(f)),GROUP(LITERAL(#I(z)),LITERAL(#I(y)),LITERAL(#I(x))))" },
        { "f ( x, y, z )",                      "APPLY(LITERAL(#I(f)),GROUP(LITERAL(#I(z)),LITERAL(#I(y)),LITERAL(#I(x))))" },
        { "f(g(x))",                            "APPLY(LITERAL(#I(f)),GROUP(APPLY(LITERAL(#I(g)),GROUP(LITERAL(#I(x))))))" },
        { "f ( g ( x ) )",                      "APPLY(LITERAL(#I(f)),GROUP(APPLY(LITERAL(#I(g)),GROUP(LITERAL(#I(x))))))" },
    };

    @Test
    public void testValidConditions() throws Exception {
        for (String[] spec : validConditions) {
            String condition = spec[0];
            String expected = spec[1];
            try {
                Condition c = Condition.valueOf(condition);
                assertNotNull(c);
                if (expected != null)
                    assertEquals(expected, c.toString());
            } catch (Condition.ParserException e) {
                fail("unexpected condition invalidity: \"" + condition + "\": " + e.getMessage());
            }
        }
    }

    private static final Object[][] boundParameterBindings = {
        { "forced",                             Boolean.FALSE                   },
        { "mediaAspectRatio",                   Double.valueOf(16.0/9.0)        },
        { "mediaLanguage",                      "en"                            },
        { "userLanguage",                       "en"                            },
    };
        
    private static final Object[][] evaluatedConditionBindings = {
        // constant bindings
        { "i",                                  Integer.valueOf(1)              },
        { "j",                                  Integer.valueOf(2)              },
        { "k",                                  Integer.valueOf(3)              },
        { "p",                                  Boolean.FALSE                   },
        { "q",                                  Boolean.TRUE                    },
        { "s",                                  "s"                             },
        { "u",                                  "u u"                           },
        { "v",                                  "v v v"                         },
        { "x",                                  Double.valueOf(1.1)             },
        { "y",                                  Double.valueOf(2.2)             },
        { "z",                                  Double.valueOf(3.3)             },
        { "PI",                                 Double.valueOf(Math.PI)         },
        { "ZERO",                               Integer.valueOf(0)              },
        // function bindings
        { "log10",                              new TestFunctionLog10()         },
        { "pow10",                              new TestFunctionPow10()         },
    };
        
    private static class TestFunctionLog10 implements Condition.EvaluatorFunction {
        public Object apply(Condition.EvaluatorState state, List<Object> arguments) {
            if (arguments.size() < 1)
                throw new Condition.BadOperandCountException(arguments.size(), 1, 1);
            else {
                Class<?> operandClass = Number.class;
                Object o0 = arguments.get(0);
                if (Condition.checkCompatibleOperand(o0, operandClass)) {
                    o0 = Condition.convertCompatibleOperand(o0, operandClass);
                    assertTrue(o0 instanceof Number);
                    return Double.valueOf(Math.log10(((Number) o0).doubleValue()));
                } else
                    throw new Condition.IncompatibleOperandException(o0, operandClass);
            }
        }
    }

    private static class TestFunctionPow10 implements Condition.EvaluatorFunction {
        public Object apply(Condition.EvaluatorState state, List<Object> arguments) {
            if (arguments.size() < 1)
                throw new Condition.BadOperandCountException(arguments.size(), 1, 1);
            else {
                Class<?> operandClass = Number.class;
                Object o0 = arguments.get(0);
                if (Condition.checkCompatibleOperand(o0, operandClass)) {
                    o0 = Condition.convertCompatibleOperand(o0, operandClass);
                    assertTrue(o0 instanceof Number);
                    return Double.valueOf(Math.pow(10, ((Number) o0).doubleValue()));
                } else
                    throw new Condition.IncompatibleOperandException(o0, operandClass);
            }
        }
    }

    private static final Object[][] evaluatedConditions = {
        { "i == 1",                                     Boolean.TRUE            },
        { "j == 2",                                     Boolean.TRUE            },
        { "k == 3",                                     Boolean.TRUE            },
        { "p",                                          Boolean.FALSE           },
        { "p == false",                                 Boolean.TRUE            },
        { "q",                                          Boolean.TRUE            },
        { "q == true",                                  Boolean.TRUE            },
        { "s == 's'",                                   Boolean.TRUE            },
        { "u == 'u u'",                                 Boolean.TRUE            },
        { "v == 'v v v'",                               Boolean.TRUE            },
        { "x == 1.1",                                   Boolean.TRUE            },
        { "y == 2.2",                                   Boolean.TRUE            },
        { "z == 3.3",                                   Boolean.TRUE            },
        { "PI > 3",                                     Boolean.TRUE            },
        { "PI < 22/7",                                  Boolean.TRUE            },
        { "PI - 3.141592653589792 < 1.5E-15",           Boolean.TRUE            },
        { "ZERO == 0",                                  Boolean.TRUE            },
        { "ZERO == 0.0",                                Boolean.TRUE            },
        { "ZERO != 0",                                  Boolean.FALSE           },
        { "ZERO != 0.0",                                Boolean.FALSE           },
        { "ZERO != 1",                                  Boolean.TRUE            },
        { "ZERO != 0.1",                                Boolean.TRUE            },
        { "(0) == 0",                                   Boolean.TRUE            },
        { "log10(1000) == 3",                           Boolean.TRUE            },
        { "log10(pow10(3)) == 3",                       Boolean.TRUE            },
        { "pow10(3) == 1000",                           Boolean.TRUE            },
        { "pow10(log10(1000)) == 1000",                 Boolean.TRUE            },
        { "parameter('forced') == false",               Boolean.TRUE            },
        { "parameter('mediaAspectRatio') == 16/9",      Boolean.TRUE            },
        { "parameter('mediaLanguage') == 'en'",         Boolean.TRUE            },
        { "parameter('userLanguage') == 'en'",          Boolean.TRUE            },
    };
        
    @Test
    public void testEvaluatedConditions() throws Exception {
        Map<String,Object> mp = new java.util.HashMap<String,Object>();
        Map<String,Object> bp = new java.util.HashMap<String,Object>();
        for (Object[] spec : boundParameterBindings) {
            String name = (String) spec[0];
            Object value = spec[1];
            bp.put(name, value);
        }
        Set<String> sf = new java.util.HashSet<String>();
        Condition.EvaluatorState state = Condition.makeEvaluatorState(mp, bp, sf);
        for (Object[] spec : evaluatedConditionBindings) {
            String identifier = (String) spec[0];
            Object value = spec[1];
            state.setBinding(identifier, value);
        }
        for (Object[] spec : evaluatedConditions) {
            String condition = (String) spec[0];
            Boolean expected = (Boolean) spec[1];
            try {
                Condition c = Condition.valueOf(condition);
                assertNotNull(c);
                boolean actual = c.evaluate(state);
                assertEquals(condition, expected, actual);
            } catch (Condition.ParserException e) {
                fail("unexpected condition invalidity: \"" + condition + "\": " + e.getMessage());
            } catch (Condition.EvaluatorException e) {
                fail("unexpected condition evaluation: \"" + condition + "\": " + e.getMessage());
            }
        }
    }

    private static final String[][] invalidConditions = {
        { "~",                                  "remaining input \"~\"" },
        { "00",                                 "expected #E, got #N(0), remaining input \"0\"" },
        { "\"a b c",                            "remaining input \"\"a b c\"" },
        { "\'a b c",                            "remaining input \"\'a b c\"" },
    };

    @Test
    public void testInvalidConditions() throws Exception {
        for (String[] spec : invalidConditions) {
            String condition = spec[0];
            String expected = spec[1];
            try {
                Condition c = Condition.valueOf(condition);
                assertNull(c);
                fail("unexpected condition validity: \"" + condition + "\"");
            } catch (Condition.ParserException e) {
                if (expected != null)
                    assertEquals(expected, e.getMessage());
            }
        }
    }

}

