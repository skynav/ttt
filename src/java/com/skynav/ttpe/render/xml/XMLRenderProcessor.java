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

package com.skynav.ttpe.render.xml;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.area.Area;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;

public class XMLRenderProcessor extends RenderProcessor {

    public static final String PROCESSOR_NAME                   = "xml";
    public static final RenderProcessor PROCESSOR               = new XMLRenderProcessor();
    private static final String DEFAULT_OUTPUT_ENCODING         = "utf-8";

    private static Charset defaultOutputEncoding;

    static {
        try {
            defaultOutputEncoding = Charset.forName(DEFAULT_OUTPUT_ENCODING);
        } catch (RuntimeException e) {
            defaultOutputEncoding = Charset.defaultCharset();
        }
    }

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "output-clean",               "",         "clean (remove) all files matching output pattern in output directory prior to writing output" },
        { "output-directory",           "DIRECTORY","specify path to directory where output is to be written" },
        { "output-encoding",            "ENCODING", "specify character encoding of output (default: " + defaultOutputEncoding.name() + ")" },
        { "output-indent",              "",         "indent output (default: no indent)" },
        { "output-pattern",             "PATTERN",  "specify output file name pattern" },
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // options state
    @SuppressWarnings("unused")
    private boolean outputDirectoryClean;
    private String outputDirectoryPath;
    private String outputEncodingName;
    @SuppressWarnings("unused")
    private boolean outputIndent;
    @SuppressWarnings("unused")
    private String outputPattern;

    // derived option state
    @SuppressWarnings("unused")
    private File outputDirectory;
    @SuppressWarnings("unused")
    private Charset outputEncoding;

    public XMLRenderProcessor() {
    }

    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        return longOptions.values();
    }

    @Override
    public int parseLongOption(String args[], int index) {
            String option = args[index];
            assert option.length() > 2;
            option = option.substring(2);
            if (option.equals("output-clean")) {
                outputDirectoryClean = true;
            } else if (option.equals("output-directory")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                outputDirectoryPath = args[++index];
            } else if (option.equals("output-encoding")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                outputEncodingName = args[++index];
            } else if (option.equals("output-indent")) {
                outputIndent = true;
            } else if (option.equals("output-pattern")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                outputPattern = args[++index];
            } else
                index = index - 1;
            return index + 1;
    }

    @Override
    public void processDerivedOptions() {
        File outputDirectory;
        if (outputDirectoryPath != null) {
            outputDirectory = new File(outputDirectoryPath);
            if (!outputDirectory.exists())
                throw new InvalidOptionUsageException("output-directory", "directory does not exist: " + outputDirectoryPath);
            else if (!outputDirectory.isDirectory())
                throw new InvalidOptionUsageException("output-directory", "not a directory: " + outputDirectoryPath);
        } else
            outputDirectory = new File(".");
        this.outputDirectory = outputDirectory;
        Charset outputEncoding;
        if (outputEncodingName != null) {
            try {
                outputEncoding = Charset.forName(outputEncodingName);
            } catch (Exception e) {
                outputEncoding = null;
            }
            if (outputEncoding == null)
                throw new InvalidOptionUsageException("output-encoding", "unknown encoding: " + outputEncodingName);
        } else
            outputEncoding = null;
        if (outputEncoding == null)
            outputEncoding = defaultOutputEncoding;
        this.outputEncoding = outputEncoding;
    }

    @Override
    public List<Frame> render(List<Area> areas) {
        return new java.util.ArrayList<Frame>();
    }

}