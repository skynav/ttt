/*
 * Copyright (c) 2015, msamek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttv.verifier.ebuttd;

import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.app.TimedTextVerifier.Results;
import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author msamek
 */
public class EBUTTDTimingTestCases extends EBUTTDAbstractTest {

    private final String correct_filename = "testfile_timing_correct.xml";
    private final String correct_uri = getClass().getResource(correct_filename).toString();
    private final String incorrect_filename = "testfile_timing_incorrect.xml";
    private final String incorrect_uri = getClass().getResource(incorrect_filename).toString();

    @Before
    public void setUp() throws Exception {
        super.setUp(Arrays.asList(new String[]{correct_uri, incorrect_uri}));
    }
    
    @Test
    public void verifyCorrect() throws Exception {
        Results results = verifier.getResults(correct_uri);
        assertEquals(results.getErrors(), 0);
        assertEquals(results.getWarnings(), 0);
        assertEquals(verifier.getResultCode(correct_uri), TimedTextVerifier.RV_PASS);
    }
    
    @Test
    public void verifyIncorrect() throws Exception {
        Map<Integer,String> errorMap = getErrors(incorrect_uri);
        System.out.println(outputStream.toString());
        System.out.println(errorMap.keySet().toString());
        assertTrue(errorMap.get(19).contains("Missing timing attributes"));
        assertTrue(errorMap.get(20).contains("Timing attributes not specified"));
        assertTrue(errorMap.get(22).contains("Missing timing attributes"));
        assertTrue(errorMap.get(27).contains("Attribute end is missing"));
        assertTrue(errorMap.get(28).contains("Attribute begin is missing"));
        assertTrue(errorMap.get(30).contains("Timing attributes incorrect"));
        assertTrue(errorMap.get(31).contains("Attribute end is missing"));
        assertTrue(errorMap.get(34).contains("Attribute begin is missing"));
        assertTrue(errorMap.get(36).contains("Timing attributes incorrect"));
        assertTrue(errorMap.get(40).contains("Attribute end is missing"));
        assertTrue(errorMap.get(45).contains("Missing timing attributes"));
        assertTrue(errorMap.get(48).contains("Attribute end is missing"));
        assertTrue(errorMap.get(50).contains("Attribute end is missing"));
        assertTrue(errorMap.get(53).contains("Attribute begin is missing"));
        assertTrue(errorMap.get(56).contains("Missing timing attributes"));
        assertEquals(verifier.getResultCode(incorrect_uri), TimedTextVerifier.RV_FAIL);
    }
    
}
