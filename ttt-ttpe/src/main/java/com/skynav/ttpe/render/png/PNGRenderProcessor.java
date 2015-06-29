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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.w3c.dom.Document;

import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.FrameImage;
import com.skynav.ttpe.render.svg.SVGDocumentFrame;
import com.skynav.ttpe.render.svg.SVGRenderProcessor;
import com.skynav.ttpe.style.Color;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.util.Colors;
import com.skynav.ttx.transformer.TransformerContext;

public class PNGRenderProcessor extends SVGRenderProcessor {

    public static final String NAME                             = "png";

    // static defaults
    private static final GenerationMode defaultMode             = GenerationMode.ISD;
    private static final double defaultDensity                  = 96;
    private static final String defaultOutputFileNamePattern    = "ttpi{0,number,000000}.png";
    
    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "png-background",             "COLOR",    "paint background with specified color (default: transparent)" },
        { "png-generation-mode",        "MODE",     "specify generation mode, where MODE is isd|region (default: " + defaultMode.name().toLowerCase() + ")" },
        { "png-pixel-density",          "DENSITY",  "specify pixel density in pixels per inch (default: " +  defaultDensity + "ppi)" },
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    enum GenerationMode {
        ISD,
        REGION;
    }

    // options state
    private String backgroundOption;
    private String modeOption;
    private String outputPattern;
    private String pixelDensityOption;

    // derived options state
    private Color background;
    private GenerationMode mode;
    private double pixelDensity;

    public PNGRenderProcessor(TransformerContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getOutputPattern() {
        return outputPattern;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        Collection<OptionSpecification> options = new java.util.TreeSet<OptionSpecification>();
        options.addAll(super.getLongOptionSpecs());
        options.addAll(longOptions.values());
        return options;
    }


    @Override
    public int parseLongOption(List<String> args, int index) {
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("output-pattern")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputPattern = args.get(++index);
        } else if (option.equals("png-background")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            backgroundOption = args.get(++index);
        } else if (option.equals("png-generation-mode")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            modeOption = args.get(++index);
        } else if (option.equals("png-pixel-density")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            pixelDensityOption = args.get(++index);
        } else {
            return super.parseLongOption(args, index);
        }
        return index + 1;
    }

    @Override
    public void processDerivedOptions() {
        super.processDerivedOptions();
        // background
        Color background;
        if (backgroundOption != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(backgroundOption, null, context, retColor)) {
                background = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                throw new InvalidOptionUsageException("png-background", "invalid color: " + backgroundOption);
        } else
            background = null;
        this.background = background;
        // (isd generation) mode
        GenerationMode mode;
        if (modeOption != null) {
            try {
                mode = GenerationMode.valueOf(modeOption.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidOptionUsageException("png-generation-mode", "invalid token: " + modeOption);
            }
        } else
            mode = defaultMode;
        this.mode = mode;
        // output pattern
        String outputPattern = this.outputPattern;
        if (outputPattern == null)
            outputPattern = defaultOutputFileNamePattern;
        this.outputPattern = outputPattern;
        // pixel density
        double pixelDensity;
        if (pixelDensityOption != null) {
            try {
                pixelDensity = Double.valueOf(pixelDensityOption);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("png-pixel-density", "invalid density: " + pixelDensityOption);
            }
        } else
            pixelDensity = defaultDensity;
        this.pixelDensity = pixelDensity;
    }

    @Override
    protected Frame renderCanvas(CanvasArea a) {
        return renderImage(super.renderCanvas(a));
    }

    private Frame renderImage(Frame frame) {
        if (frame instanceof SVGDocumentFrame) {
            SVGDocumentFrame frameSVG = (SVGDocumentFrame) frame;
            Document d = frameSVG.getDocument();
            Rectangle rectRoot = new Rectangle(Point.ZERO, frameSVG.getExtent());
            List<Rectangle> regionRects = frameSVG.getRegions();
            List<FrameImage> images = new java.util.ArrayList<FrameImage>();
            if ((mode == GenerationMode.ISD) || regionRects.isEmpty()) {
                FrameImage fi = renderFrameImage(d, rectRoot, null);
                if (fi != null)
                    images.add(fi);
            } else {
                for (Rectangle rectRegion : regionRects) {
                    FrameImage fi = renderFrameImage(d, rectRoot, rectRegion);
                    if (fi != null)
                        images.add(fi);
                }
            }
            return new PNGImageFrame(frame.getBegin(), frame.getEnd(), frame.getExtent(), images);
        }
        return null;
    }

    private FrameImage renderFrameImage(Document d, Rectangle rectRoot, Rectangle rectRegion) {
        Reporter reporter = context.getReporter();
        ByteArrayOutputStream bas = null;
        BufferedOutputStream bos = null;
        try {
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput ti = new TranscoderInput(d);
            bas = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(bas);
            TranscoderOutput to = new TranscoderOutput(bos);
            if (rectRegion == null)
                rectRegion = rectRoot;
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, Float.valueOf((float) rectRegion.getWidth()));
            t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, Float.valueOf((float) rectRegion.getHeight()));
            t.addTranscodingHint(PNGTranscoder.KEY_AOI, rectRegion.getAWTRectangle());
            if (background != null) 
                t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, background.getPaint());
            if (pixelDensity != 0) 
                t.addTranscodingHint(PNGTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, Float.valueOf((float) (25.4 / pixelDensity)));
            t.transcode(ti, to);
            bos.flush();
            bos.close();
            return new PNGFrameImage(rectRegion.getExtent(), rectRegion.getOrigin(), bas.toByteArray());
        } catch (IOException e) {
            reporter.logError(e);
        } catch (TranscoderException e) {
            reporter.logError(e);
        } finally {
            IOUtil.closeSafely(bos);
        }
        return null;
    }

}
