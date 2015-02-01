/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;

import com.skynav.ttv.util.IOUtil;

public class LineBreaker {

    public static final String RULES_SOURCE_EXT = "txt";
    public static final String RULES_BINARY_EXT = "dat";

    private static String[][] rulesFileNames = new String[][] {
        { "uax14", "icu-brkiter-uax14" },
    };
    private static Map<String,String> rulesFileNameMap;
    private static Map<String,LineBreaker> breakers;
    static {
        rulesFileNameMap = new java.util.HashMap<String,String>();
        for (String[] entry : rulesFileNames) {
            rulesFileNameMap.put(entry[0], entry[1]);
        }
        breakers = new java.util.HashMap<String,LineBreaker>();
    }

    private String name;
    private LineBreakIterator iterator;

    private LineBreaker(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LineBreakIterator getIterator() {
        return iterator;
    }

    private LineBreaker load() {
        BreakIterator iterator = null;
        InputStream is = null;
        try {
            URL rulesLocator = getRulesLocator(name, RULES_BINARY_EXT);
            if (rulesLocator != null) {
                is = rulesLocator.openStream();
                iterator = RuleBasedBreakIterator.getInstanceFromCompiledRules(is);
            } else
                iterator = BreakIterator.getCharacterInstance();
        } catch (IOException e) {
        } finally {
            IOUtil.closeSafely(is);
        }
        if (iterator != null) {
            this.iterator = new LineBreakIterator(iterator);
            return this;
        } else
            return null;
    }

    private URL getRulesLocator(String name, String extension) {
        String rulesFileName = rulesFileNameMap.get(name);
        if (rulesFileName != null)
            return getClass().getResource(rulesFileName + "." + extension);
        else
            return null;
    }

    public static LineBreaker getInstance(String name) {
        if (name == null)
            name = "";
        LineBreaker lb = breakers.get(name);
        if (lb != null)
            return lb;
        lb = new LineBreaker(name).load();
        breakers.put(name, lb);
        return lb;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String inputFilePath = args[0];
            String outputFilePath = args[1];
            InputStream is = null;
            OutputStream os = null;
            BufferedReader r = null;
            try {
                is = new FileInputStream(inputFilePath);
                os = new FileOutputStream(outputFilePath);
                r = new BufferedReader(new InputStreamReader(is));
                StringBuffer rules = new StringBuffer();
                String line;
                while ((line = r.readLine()) != null) {
                    rules.append(line);
                    rules.append('\n');
                }
                RuleBasedBreakIterator.compileRules(rules.toString(), os);
            } catch (IOException e) {
            } finally {
                if (r != null) { try { r.close(); } catch (Exception e) {} }
                IOUtil.closeSafely(os);
                IOUtil.closeSafely(is);
            }
        } else {
            System.err.println("Usage: java -cp ... com.skynav.ttpe.text.LineBreaker [INPUT-FILE-PATH] [OUTPUT-FILE-PATH]");
        }
    }

}
