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
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.TextReporter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;

/**
 *
 * @author msamek
 */
public abstract class EBUTTDAbstractTest {

    protected final TimedTextVerifier verifier = new TimedTextVerifier();
    protected final OutputStream outputStream = new ByteArrayOutputStream();
    
    protected List<String> args = new ArrayList<>(Arrays.asList(new String[]{
        "--verbose",
        "--model", "ebuttd"
    }));
    
    public abstract void setUp() throws Exception;

    protected void setUp(List<String> inputFiles) throws Exception {
        Reporter reporter = new TextReporter();
        reporter.setOutput(new PrintWriter(outputStream));
        verifier.setReporter(reporter, new PrintWriter(outputStream), "UTF-8", true, true);
        
        this.args.addAll(inputFiles);
        
        // Run the verifier
        verifier.run(args.toArray(new String[args.size()]));
    }
    
    @After
    public void tearDown() {
        System.out.println(outputStream.toString());
    }

    protected Map<Integer,String> getErrors(String uri) {
        Pattern pattern = Pattern.compile("\\[E\\]:\\{" + Pattern.quote(uri) + "\\}:\\[(\\d+),\\d+\\]:(.*)");
        Map<Integer,String> errorMap = new HashMap<>();
        String[] lines = outputStream.toString().split("\n");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                errorMap.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
            }
        }
        
        return errorMap;
    }
    protected Map<Integer,String> getWarnings(String uri) {
        Pattern pattern = Pattern.compile("\\[W\\]:\\{" + Pattern.quote(uri) + "\\}:\\[(\\d+),\\d+\\]:(.*)");
        Map<Integer,String> errorMap = new HashMap<>();
        String[] lines = outputStream.toString().split("\n");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                errorMap.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
            }
        }
        
        return errorMap;
    }
}
