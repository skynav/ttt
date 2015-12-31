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
import java.util.List;

import com.skynav.ttx.app.TimedTextTransformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.Transformers;

public class TimedTextTransformingVerifier extends TimedTextTransformer {

    // banner text
    private static final String title = "Timed Text Transforming Verifier (TTXV) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2013-14 Skynav, Inc.";
    private static final String banner = title + " " + copyright;

    private static final String DEFAULT_TRANSFORMER = Transformers.getDefaultTransformerName();

    public TimedTextTransformingVerifier() {
    }

    @Override
    public void processResult(List<String> args, URI uri, Object root) {
        super.processResult(args, uri, root);
        performPostTransformVerification(uri, root, getResourceState(TransformerContext.ResourceState.ttxOutput.name()));
    }

    @Override
    protected void initializeResourceState(URI uri) {
        super.initializeResourceState(uri);
        setResourceState(TransformerContext.ResourceState.ttxRetainLocations.name(), Boolean.TRUE);
        setResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name(), Boolean.TRUE);
        setResourceState(TransformerContext.ResourceState.ttxTransformer.name(), Transformers.getTransformer(DEFAULT_TRANSFORMER));
        // setResourceState(TransformerContext.ResourceState.ttxTransformingVerifier.name(), this);
    }

    @Override
    protected void showBanner(PrintWriter out, String banner) {
        super.showBanner(out, TimedTextTransformingVerifier.banner);
    }

    @Override
    protected boolean doMergeTransformerOptions() {
        return false;
    }

    private void performPostTransformVerification(URI uri, Object root, Object ttxOutput) {
        getModel().getSemanticsVerifier().verifyPostTransform(root, ttxOutput, this);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new TimedTextTransformingVerifier().run(args));
    }

}
