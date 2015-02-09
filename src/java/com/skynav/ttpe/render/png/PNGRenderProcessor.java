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

package com.skynav.ttpe.render.png;

import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.render.DocumentFrame;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.FrameImage;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.render.svg.SVGRenderProcessor;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttx.transformer.TransformerContext;

public class PNGRenderProcessor extends SVGRenderProcessor {

    public static final RenderProcessor PROCESSOR               = new PNGRenderProcessor();

    private static final String PROCESSOR_NAME                  = "png";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    public PNGRenderProcessor() {
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
    protected Frame renderCanvas(CanvasArea a, TransformerContext context) {
        return renderImage(super.renderCanvas(a, context), context);
    }

    private Frame renderImage(Frame frame, TransformerContext context) {
        if (frame instanceof DocumentFrame) {
            Reporter reporter = context.getReporter();
            ByteArrayOutputStream bas = null;
            BufferedOutputStream bos = null;
            try {
                List<FrameImage> images = new java.util.ArrayList<FrameImage>();
                PNGTranscoder t = new PNGTranscoder();
                TranscoderInput ti = new TranscoderInput(((DocumentFrame) frame).getDocument());
                bas = new ByteArrayOutputStream();
                bos = new BufferedOutputStream(bas);
                TranscoderOutput to = new TranscoderOutput(bos);
                t.transcode(ti, to);
                bos.flush();
                bos.close();
                images.add(new PNGFrameImage(Extent.EMPTY, Point.ZERO, bas.toByteArray()));
                return new PNGImageFrame(frame.getBegin(), frame.getEnd(), frame.getExtent(), images);
            } catch (IOException e) {
                reporter.logError(e);
            } catch (TranscoderException e) {
                reporter.logError(e);
            }
        }
        return null;
    }

}