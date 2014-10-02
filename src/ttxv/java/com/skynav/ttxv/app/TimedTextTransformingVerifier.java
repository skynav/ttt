/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttxv.app;

import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;

import com.skynav.ttv.app.OptionSpecification;

import com.skynav.ttx.app.TimedTextTransformer;
import com.skynav.ttx.transformer.Transformers;
import com.skynav.ttx.transformer.TransformerContext;

public class TimedTextTransformingVerifier extends TimedTextTransformer {

    // banner text
    private static final String title = "Timed Text Transforming Verifier (TTXV) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2013-14 Skynav, Inc.";
    private static final String banner = title + " " + copyright;

    // option and usage info
    private static final String[][] shortOptionSpecifications = new String[][] {
    };
    private static final Collection<OptionSpecification> shortOptions;
    static {
        shortOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : shortOptionSpecifications) {
            shortOptions.add(new OptionSpecification(spec[0], spec[1]));
        }
    }

    private static final String[][] longOptionSpecifications = new String[][] {
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    private static final String DEFAULT_TRANSFORMER = "isd";

    public TimedTextTransformingVerifier() {
    }

    @Override
    public void processResult(String[] args, URI uri, Object root) {
        super.processResult(args, uri, root);
        performPostTransformVerification(uri, root, getResourceState(TransformerContext.ResourceState.ttxOutput.name()));
    }

    @Override
    protected void initializeResourceState() {
        super.initializeResourceState();
        setResourceState(TransformerContext.ResourceState.ttxTransformer.name(), Transformers.getTransformer(DEFAULT_TRANSFORMER));
        setResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name(), Boolean.TRUE);
        // setResourceState(TransformerContext.ResourceState.ttxRetainMetadata.name(), Boolean.TRUE);
        setResourceState(TransformerContext.ResourceState.ttxRetainLocations.name(), Boolean.TRUE);
    }

    @Override
    public String[] preProcessOptions(String[] args, Collection<OptionSpecification> baseShortOptions, Collection<OptionSpecification> baseLongOptions) {
        return super.preProcessOptions(args, baseShortOptions, baseLongOptions);
    }

    @Override
    protected void showBanner(PrintWriter out, String banner) {
        super.showBanner(out, TimedTextTransformingVerifier.banner);
    }

    @Override
    public void showUsage(PrintWriter out) {
        super.showUsage(out);
    }

    @Override
    protected boolean doMergeTransformerOptions() {
        return false;
    }

    @Override
    protected boolean hasLongOption(String option) {
        return super.hasLongOption(option);
    }

    @Override
    protected int parseLongOption(String args[], int index) {
        return super.parseLongOption(args, index);
    }

    @Override
    protected boolean hasShortOption(String option) {
        return super.hasShortOption(option);
    }

    @Override
    protected int parseShortOption(String args[], int index) {
        return super.parseShortOption(args, index);
    }

    private void performPostTransformVerification(URI uri, Object root, Object ttxOutput) {
        getModel().getSemanticsVerifier().verifyPostTransform(root, ttxOutput, this);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextTransformingVerifier().run(args));
    }

}
