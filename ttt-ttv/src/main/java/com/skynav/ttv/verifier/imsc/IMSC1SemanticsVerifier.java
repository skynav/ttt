/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.imsc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.imsc.IMSC1;
import com.skynav.ttv.model.imsc1.ittm.AltText;
import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.model.ttml.TTML1.TTML1Model;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Layout;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.TextOutline;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.VerificationParameters;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters1;
import com.skynav.ttv.verifier.util.Integers;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Outline;
import com.skynav.ttv.verifier.util.Timing;
import com.skynav.ttv.verifier.util.ZeroTreatment;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttv.model.imsc.IMSC1.Constants.*;

public class IMSC1SemanticsVerifier extends ST20522010SemanticsVerifier {

    public IMSC1SemanticsVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean verify(Object root, VerifierContext context) {
        boolean failed = false;
        if (!super.verify(root, context))
            failed = true;
        if (root instanceof TimedText) {
            TimedText tt = (TimedText) root;
            if (!verifyCharset(tt))
                failed = true;
            if (!verifyEmUsage(tt))
                failed = true;
            if (!verifyExtentIfPixelUnitUsed(tt))
                failed = true;
            if (!verifyFrameRateIfFramesUsed(tt))
                failed = true;
            if (!verifyRegionContainment(tt))
                failed = true;
            if (!verifyTickRateIfTicksUsed(tt))
                failed = true;
            if (!verifyTimeableContentIsTimed(tt))
                failed = true;
        } else {
            QName rootName = context.getBindingElementName(root);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root),
                "*KEY*", "Root element must be ''{0}'', got ''{1}''.", TTML1Model.timedTextElementName, rootName));
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyCharset(TimedText tt) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        try {
            Charset charsetRequired = Charset.forName(CHARSET_REQUIRED);
            String charsetRequiredName = charsetRequired.name();
            Charset charset = (Charset) getContext().getResourceState("encoding");
            String charsetName = (charset != null) ? charset.name() : "unknown";
            if (!charsetName.equals(charsetRequiredName)) {
                reporter.logError(reporter.message(getLocator(tt),
                    "*KEY*", "Document encoding uses ''{0}'', but requires ''{1}''.", charsetName, charsetRequiredName));
                failed = true;
            }
        } catch (Exception e) {
            reporter.logError(e);
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyEmUsage(TimedText tt) {
        boolean failed = false;
        VerifierContext context = getContext();
        if (isIMSCImageProfile(context)) {
            @SuppressWarnings("unchecked")
            Set<Locator> usage = (Set<Locator>) context.getResourceState("usageEm");
            if ((usage != null) && (usage.size() > 0)) {
                Reporter reporter = context.getReporter();
                for (Locator locator : usage)
                    reporter.logError(reporter.message(locator, "*KEY*", "Use of ''em'' unit prohibited in image profile."));
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verifyExtentIfPixelUnitUsed(TimedText tt) {
        boolean failed = false;
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) getContext().getResourceState("usagePixel");
        if ((usage != null) && (usage.size() > 0)) {
            String extent = tt.getExtent();
            if ((extent == null) || (extent.length() == 0)) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Uses ''px'' unit, but does not specify ''{0}'' attribute on ''{1}'' element.",
                        IMSC1StyleVerifier.extentAttributeName, TTML1Model.timedTextElementName));
                }
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verifyFrameRateIfFramesUsed(TimedText tt) {
        boolean failed = false;
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) getContext().getResourceState("usageFrames");
        if ((usage != null) && (usage.size() > 0)) {
            BigInteger frameRate = tt.getFrameRate();
            if ((frameRate == null) || IMSC1ParameterVerifier.isFrameRateDefaulted(tt)) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Uses frames (''f'') metric or frame component, but does not specify ''{0}'' attribute on ''{1}'' element.",
                        IMSC1ParameterVerifier.frameRateAttributeName, TTML1Model.timedTextElementName));
                }
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verifyTickRateIfTicksUsed(TimedText tt) {
        boolean failed = false;
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) getContext().getResourceState("usageTicks");
        if ((usage != null) && (usage.size() > 0)) {
            BigInteger tickRate = tt.getTickRate();
            if ((tickRate == null) || IMSC1ParameterVerifier.isTickRateDefaulted(tt)) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Uses ticks (''t'') metric, but does not specify ''{0}'' attribute on ''{1}'' element.",
                        IMSC1ParameterVerifier.tickRateAttributeName, TTML1Model.timedTextElementName));
                }
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verifyRegionContainment(TimedText tt) {
        boolean failed = false;
        Head head = tt.getHead();
        if (head != null) {
            Layout layout = head.getLayout();
            if (layout != null) {
                double[] rootExtent = getRootExtent(tt);
                double[] cellResolution = getCellResolution(tt);
                for (Region r : layout.getRegion()) {
                    if (!verifyRegionContainment(r, rootExtent, cellResolution))
                        failed = true;
                }
            }
        }
        return !failed;
    }

    private double[] getRootExtent(TimedText tt) {
        return getRootExtent(tt.getExtent(), getLocator(tt));
    }

    private double[] getRootExtent(String extent, Locator locator) {
        double[] externalExtent = (double[]) getContext().getResourceState("externalExtent");
        if (extent != null) {
            extent = extent.trim();
            if (extent.equals("auto"))
                return externalExtent;
            else {
                Length[] lengths = parseLengthPair(extent, locator, getContext().getReporter(), true);
                return new double[] { getPixels(lengths[0], 0, 1, 1, 1), getPixels(lengths[1], 0, 1, 1, 1) };
            }
        } else
            return externalExtent;
    }

    private double[] getCellResolution(TimedText tt) {
        return getCellResolution(tt.getCellResolution(), getLocator(tt));
    }

    private double[] getCellResolution(String cellResolution, Locator locator) {
        if (cellResolution != null) {
            cellResolution = cellResolution.trim();
            Integer[] integers = parseIntegerPair(cellResolution, locator, getContext().getReporter());
            return new double[] { integers[0], integers[1] };
        } else
            return null;
    }

    protected boolean verifyRegionContainment(Region region, double[] rootExtent, double[] cellResolution) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        Locator locator = getLocator(region);
        if (rootExtent == null)
            rootExtent = new double[] { -1, -1 };
        if (cellResolution == null)
            cellResolution = new double[] { 1, 1 };
        // extract root edges in fractional pixels
        double xRoot = 0;
        double yRoot = 0;
        double wRoot = rootExtent[0];
        double hRoot = rootExtent[1];
        // extract region origin in fractional pixels
        String origin = region.getOrigin();
        double x = xRoot;
        double y = yRoot;
        if (origin != null) {
            origin = origin.trim();
            if (!origin.equals("auto")) {
                Length[] lengths = parseLengthPair(origin, locator, reporter, false);
                if (lengths != null) {
                    x = getPixels(lengths[0], 0, wRoot, wRoot, cellResolution[0]);
                    y = getPixels(lengths[1], 0, hRoot, wRoot, cellResolution[1]);
                } else
                    failed = true;
            }
        }
        // extract region extent in fractional pixels
        String extent = region.getExtent();
        double w = wRoot;
        double h = hRoot;
        if (extent != null) {
            extent = extent.trim();
            if (!extent.equals("auto")) {
                Length[] lengths = parseLengthPair(extent, locator, reporter, false);
                if (lengths != null) {
                    w = getPixels(lengths[0], 0, wRoot, wRoot, cellResolution[0]);
                    h = getPixels(lengths[1], 0, hRoot, hRoot, cellResolution[1]);
                } else
                    failed = true;
            }
        }
        // check containment
        if (!failed) {
            if (x < xRoot) {
                reporter.logError(reporter.message(locator, "*KEY*", "Left edge at {0}px is outside root container.", x));
                failed = true;
            }
            if (y < yRoot) {
                reporter.logError(reporter.message(locator, "*KEY*", "Top edge at {0}px is outside root container.", y));
                failed = true;
            }
            if ((wRoot >= 0) && (hRoot >= 0)) {
                if ((x + w) > (xRoot + wRoot)) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Right edge at {0}px is outside root container.", x + w));
                    failed = true;
                }
                if ((y + h) > (yRoot + hRoot)) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Bottom edge at {0}px is outside root container.", y + h));
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private Length[] parseLengthPair(String pair, Locator locator, Reporter reporter, boolean enforcePixelsOnly) {
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
        List<Length> lengths = new java.util.ArrayList<Length>();
        Location location = new Location(null, null, null, locator);
        if (Lengths.isLengths(pair, location, getContext(), minMax, treatments, lengths)) {
            if (enforcePixelsOnly) {
                for (Length l : lengths) {
                    if (l.getUnits() != Length.Unit.Pixel) {
                        if (reporter != null)
                            reporter.logError(reporter.message(locator, "*KEY*", "Invalid length pair component ''{0}'', must use pixel (''px'') unit only.", l));
                        return null;
                    }
                }
            }
            return lengths.toArray(new Length[2]);
        } else {
            if (reporter != null)
                reporter.logError(reporter.message(locator, "*KEY*", "Invalid length pair ''{0}''.", pair));
            return null;
        }
    }

    private Integer[] parseIntegerPair(String pair, Locator locator, Reporter reporter) {
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, ZeroTreatment.Error };
        List<Integer> integers = new java.util.ArrayList<Integer>();
        Location location = new Location(null, null, null, locator);
        if (Integers.isIntegers(pair, location, null, minMax, treatments, integers)) {
            return integers.toArray(new Integer[2]);
        } else {
            if (reporter != null)
                reporter.logError(reporter.message(locator, "*KEY*", "Invalid integer pair ''{0}''.", pair));
            return null;
        }
    }

    private double getPixels(Length length, double fSize, double pSize, double rSize, double cSize) {
        double value = length.getValue();
        Length.Unit units = length.getUnits();
        if (pSize < 0)
            pSize = 0;
        if (rSize < 0)
            rSize = 0;
        if (units == Length.Unit.Pixel)
            return value;
        else if (units == Length.Unit.Cell)
            return value * (rSize / cSize);
        else if (units == Length.Unit.Percentage)
            return value * (pSize / 100);
        else if (units == Length.Unit.Em)
            return value * fSize;
        else
            return 0;
    }

    protected boolean verifyTimeableContentIsTimed(TimedText tt) {
        boolean failed = false;
        VerifierContext context = getContext();
        VerificationParameters verificationParameters = makeTimingVerificationParameters(tt, context);
        String timeablesKey = getModel().makeResourceStateName("timeables");
        @SuppressWarnings("unchecked")
        List<Object> timeables = (List<Object>) context.getResourceState(timeablesKey);
        if (timeables != null) {
            for (Object timeable : timeables) {
                if (!verifyTimedContent(timeable, getLocator(timeable), context, verificationParameters))
                    failed = true;
            }
        }
        context.setResourceState(timeablesKey, null);
        return !failed;
    }

    private static TimingVerificationParameters makeTimingVerificationParameters(Object content, VerifierContext context) {
        return new TimingVerificationParameters1(content, context != null ? context.getExternalParameters() : null);
    }

    private boolean verifyTimedContent(Object content, Locator locator, VerifierContext context, VerificationParameters parameters) {
        boolean failed = false;
        if (hasTimeableContent(content)) {
            if (!isSelfOrAncestorExplicitlyTimed(content, locator, context, parameters)) {
                Reporter reporter = context.getReporter();
                if (reporter.isWarningEnabled("missing-timing")) {
                    Message message = reporter.message(locator,
                        "*KEY*", "Timeable content ''{0}'' is missing explicit @begin and/or @end on self or ancestor.", context.getBindingElementName(content));
                    if (reporter.logWarning(message)) {
                        reporter.logError(message);
                        failed = true;
                    }
                }
            }
        }
        return !failed;
    }

    private boolean hasTimeableContent(Object content) {
        if (content instanceof Division) {
            return hasTimeableContent((Division) content);
        } else {
            List<Serializable> children;
            if (content instanceof Paragraph)
                children = ((Paragraph) content).getContent();
            else if (content instanceof Span)
                children = ((Span) content).getContent();
            else
                children = null;
            return (children == null) ? false : hasTimeableContent(children);
        }
    }

    private boolean hasTimeableContent(Division div) {
        String smpteBackgroundImage = div.getOtherAttributes().get(getBackgroundImageAttributeName());
        if ((smpteBackgroundImage == null) || smpteBackgroundImage.isEmpty())
            return false;
        else {
            try {
                new URI(smpteBackgroundImage);
                return true;
            } catch (URISyntaxException e) {
                return false;
            }
        }
    }

    private boolean hasTimeableContent(List<Serializable> content) {
        for (Serializable s : content) {
            if (s instanceof JAXBElement<?>) {
                Object c = ((JAXBElement<?>)s).getValue();
                if (c instanceof Break)
                    return true;
            } else if (s instanceof String)
                return true;
        }
        return false;
    }

    private boolean isSelfOrAncestorExplicitlyTimed(Object content, Locator locator, VerifierContext context, VerificationParameters parameters) {
        while (content != null) {
            if (isExplicitlyTimed(content, locator, context, parameters))
                return true;
            else if (content instanceof Body)
                break;
            else
                content = context.getBindingElementParent(content);
        }
        return false;
    }

    private boolean isExplicitlyTimed(Object content, Locator locator, VerifierContext context, VerificationParameters parameters) {
        assert parameters instanceof TimingVerificationParameters;
        TimeParameters timeParameters = ((TimingVerificationParameters) parameters).getTimeParameters();
        Time b = getBegin(content, locator, context, timeParameters);
        Time e = getEnd(content, locator, context, timeParameters);
        return (b != null) && (e != null) && (b.getTime(timeParameters) <= e.getTime(timeParameters));
    }

    private Time getBegin(Object content, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        QName name = IMSC1TimingVerifier.beginAttributeName;
        Object value = getTimingValue(content, name);
        if (value instanceof String) {
            Location location = new Location(content, context.getBindingElementName(content), name, locator);
            return parseTimeCoordinate((String) value, location, context, timeParameters);
        } else
            return null;
    }

    private Time getEnd(Object content, Locator locator, VerifierContext context, TimeParameters timeParameters) {
        QName name = IMSC1TimingVerifier.endAttributeName;
        Object value = getTimingValue(content, name);
        if (value instanceof String) {
            Location location = new Location(content, context.getBindingElementName(content), name, locator);
            return parseTimeCoordinate((String) value, location, context, timeParameters);
        } else
            return null;
    }

    private Object getTimingValue(Object content, QName timingAttributeName) {
        try {
            Class<?> contentClass = content.getClass();
            Method m = contentClass.getMethod(makeGetterName(timingAttributeName), new Class<?>[]{});
            return m.invoke(content, new Object[]{});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String makeGetterName(QName name) {
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        String ln = name.getLocalPart();
        sb.append(Character.toUpperCase(ln.charAt(0)));
        sb.append(ln.substring(1));
        return sb.toString();
    }

    private Time parseTimeCoordinate(String value, Location location, VerifierContext context, TimeParameters timeParameters) {
        Time[] time = new Time[1];
        if (Timing.isCoordinate(value, location, context, timeParameters, time))
            return time[0];
        else
            return null;
    }

    @Override
    protected boolean verifyTimedText(Object root) {
        boolean failed = false;
        assert root instanceof TimedText;
        TimedText tt = (TimedText) root;
        String profile = tt.getProfile();
        if (profile == null) {
            Reporter reporter = getContext().getReporter();
            Message message = reporter.message(getLocator(tt),
                "*KEY*", "Root element ''{0}'' should have a ''{1}'' attribute, but it is missing.",
                TTML1Model.timedTextElementName, TTML1ProfileVerifier.profileAttributeName);
            if (reporter.logWarning(message)) {
                reporter.logError(message);
                failed = true;
            }
        } else
            getContext().setResourceState(getModel().makeResourceStateName("profile"), profile);
        if (!super.verifyTimedText(root))
            failed = true;
        return !failed;
    }

    @Override
    protected boolean verifyRegion(Object region) {
        if (!super.verifyRegion(region))
            return false;
        else {
            boolean failed = false;
            assert region instanceof Region;
            if (!verifyRegionExtent((Region) region, getLocator(region), getContext()))
                failed = true;
            return !failed;
        }
    }

    private boolean verifyRegionExtent(Region region, Locator locator, VerifierContext context) {
        boolean failed = false;
        String extent = region.getExtent();
        if (extent == null) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator,
                "*KEY*", "Style attribute ''{0}'' required on ''{1}''.",
                IMSC1StyleVerifier.extentAttributeName, context.getBindingElementName(region)));
            failed = true;
        }
        return !failed;
    }

    @Override
    protected boolean verifyDivision(Object division) {
        VerifierContext context = getContext();
        if (!super.verifyDivision(division)) {
            return false;
        } else if (isIMSCImageProfile(context) && isNestedDivision(division)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(division),
                "*KEY*", "Nested ''{0}'' prohibited in image profile.", context.getBindingElementName(division)));
            return false;
        } else {
            String timeablesKey = getModel().makeResourceStateName("timeables");
            @SuppressWarnings("unchecked")
            List<Object> timeables = (List<Object>) getContext().getResourceState(timeablesKey);
            if (timeables == null) {
                timeables = new java.util.ArrayList<Object>();
                getContext().setResourceState(timeablesKey, timeables);
            }
            timeables.add(division);
            return true;
        }
    }

    private boolean isNestedDivision(Object division) {
        VerifierContext context = getContext();
        for (Object p = context.getBindingElementParent(division); p != null; p = context.getBindingElementParent(p)) {
            if (p instanceof Division)
                return true;
        }
        return false;
    }

    @Override
    protected boolean verifyParagraph(Object paragraph) {
        VerifierContext context = getContext();
        if (!super.verifyParagraph(paragraph)) {
            return false;
        } else if (isIMSCImageProfile(context)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(paragraph),
                "*KEY*", "Element ''{0}'' prohibited in image profile.", context.getBindingElementName(paragraph)));
            return false;
        } else {
            String timeablesKey = getModel().makeResourceStateName("timeables");
            @SuppressWarnings("unchecked")
            List<Object> timeables = (List<Object>) context.getResourceState(timeablesKey);
            if (timeables == null) {
                timeables = new java.util.ArrayList<Object>();
                context.setResourceState(timeablesKey, timeables);
            }
            timeables.add(paragraph);
            return true;
        }
    }

    @Override
    protected boolean verifySpan(Object span) {
        VerifierContext context = getContext();
        if (!super.verifySpan(span)) {
            return false;
        } else if (isIMSCImageProfile(context)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(span),
                "*KEY*", "Element ''{0}'' prohibited in image profile.", context.getBindingElementName(span)));
            if (isNestedSpan(span)) {
                reporter.logError(reporter.message(getLocator(span),
                    "*KEY*", "Nested ''{0}'' prohibited in image profile.", context.getBindingElementName(span)));
            }
            return false;
        } else {
            String timeablesKey = getModel().makeResourceStateName("timeables");
            @SuppressWarnings("unchecked")
            List<Object> timeables = (List<Object>) context.getResourceState(timeablesKey);
            if (timeables == null) {
                timeables = new java.util.ArrayList<Object>();
                context.setResourceState(timeablesKey, timeables);
            }
            timeables.add(span);
            return true;
        }
    }

    private boolean isNestedSpan(Object span) {
        VerifierContext context = getContext();
        for (Object p = context.getBindingElementParent(span); p != null; p = context.getBindingElementParent(p)) {
            if (p instanceof JAXBElement<?>)
                p = ((JAXBElement<?>)p).getValue();
            if (p instanceof Span)
                return true;
        }
        return false;
    }

    @Override
    protected boolean verifyBreak(Object br) {
        VerifierContext context = getContext();
        if (!super.verifyBreak(br)) {
            return false;
        } else if (isIMSCImageProfile(context)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(br),
                "*KEY*", "Element ''{0}'' prohibited in image profile.", context.getBindingElementName(br)));
            return false;
        } else {
            return true;
        }
    }

    public boolean inIMSCNamespace(QName name) {
        return IMSC1.inIMSCNamespace(name);
    }

    @Override
    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        if (!super.verifyNonTTOtherElement(content, locator, context))
            return false;
        else
            return verifyIMSCNonTTOtherElement(content, locator, context);
    }

    private boolean verifyIMSCNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        assert context == getContext();
        Node node = context.getXMLNode(content);
        if (node == null) {
            if (content instanceof Element)
                node = (Element) content;
        }
        if (node != null) {
            String nsUri = node.getNamespaceURI();
            String localName = node.getLocalName();
            if (localName == null)
                localName = node.getNodeName();
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            Model model = getModel();
            if (inIMSCNamespace(name)) {
                if (!model.isElement(name)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Unknown element in IMSC namespace ''{0}''.", name));
                    failed = true;
                } else if (isIMSCAltTextElement(content)) {
                    failed = !verifyIMSCAltText(content, locator, context);
                } else {
                    return unexpectedContent(content);
                }
            }
        }
        return !failed;
    }

    @Override
    protected boolean verifySMPTEImage(Object image, Locator locator, VerifierContext context) {
        if (!super.verifySMPTEImage(image, locator, context))
            return false;
        else {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator,
                "*KEY*", "SMPTE element ''{0}'' prohibited.", context.getBindingElementName(image)));
            return false;
        }
    }

    protected boolean isIMSCAltTextElement(Object content) {
        return content instanceof AltText;
    }

    protected boolean verifyIMSCAltText(Object image, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyOtherAttributes(image))
            failed = true;
        if (!verifyAncestry(image, locator, context))
            failed = true;
        return !failed;
    }

    @Override
    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        if (!super.verifyNonTTOtherAttributes(content, locator, context))
            return false;
        else
            return verifyIMSCNonTTOtherAttributes(content, locator, context);
    }

    @Override
    protected boolean verifySMPTEBackgroundImage(Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        if (!super.verifySMPTEBackgroundImage(content, name, valueObject, locator, context)) {
            return false;
        } else if (isIMSCTextProfile(context)) {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "SMPTE attribute ''{0}'' prohibited on ''{1}'' in {2} text profile.",
                name, context.getBindingElementName(content), getModel().getName()));
            return false;
        } else if (isIMSCImageProfile(context)) {
            return true;
        } else
            throw new IllegalStateException();
    }

    @Override
    protected boolean verifySMPTEBackgroundImageHV(Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        if (!super.verifySMPTEBackgroundImageHV(content, name, valueObject, locator, context))
            return false;
        else {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "SMPTE attribute ''{0}'' prohibited on ''{1}''.",
                name, context.getBindingElementName(content)));
            return false;
        }
    }

    private boolean verifyIMSCNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            boolean failedAttribute = false;
            Node item = attributes.item(i);
            if (!(item instanceof Attr))
                continue;
            Attr attribute = (Attr) item;
            String nsUri = attribute.getNamespaceURI();
            String localName = attribute.getLocalName();
            if (localName == null)
                localName = attribute.getName();
            if (localName.indexOf("xmlns") == 0)
                continue;
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            Model model = getModel();
            if (model.isNamespace(name.getNamespaceURI())) {
                String nsLabel;
                if (name.getNamespaceURI().indexOf(NAMESPACE_PREFIX) == 0)
                    nsLabel = "IMSC";
                else if (name.getNamespaceURI().indexOf(NAMESPACE_EBUTT_PREFIX) == 0)
                    nsLabel = "EBUTT";
                else
                    nsLabel = null;
                if (nsLabel != null) {
                    Reporter reporter = context.getReporter();
                    String value = attribute.getValue();
                    if (!model.isGlobalAttribute(name)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Unknown attribute in {0} namespace ''{1}'' not permitted on ''{2}''.",
                            nsLabel, name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!model.isGlobalAttributePermitted(name, context.getBindingElementName(content))) {
                        reporter.logError(reporter.message(locator, "*KEY*", "{0} attribute ''{1}'' not permitted on ''{2}''.",
                            nsLabel, name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!verifyNonEmptyOrPadded(content, name, value, locator, context)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                        failedAttribute = true;
                    } else if (nsLabel.equals("IMSC")) {
                        if (!verifyIMSCAttribute(content, locator, context, name, value)) {
                            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                            failedAttribute = true;
                        }
                    } else if (nsLabel.equals("EBUTT")) {
                        if (!verifyEBUTTAttribute(content, locator, context, name, value)) {
                            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                            failedAttribute = true;
                        }
                    }
                }
            }
            if (failedAttribute)
                failed = failedAttribute;
        }
        return !failed;
    }

    protected boolean verifyIMSCAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        // [TBD] - IMPLEMENT ME
        return !failed;
    }

    protected boolean verifyEBUTTAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        // [TBD] - IMPLEMENT ME
        return !failed;
    }

    @Override
    protected boolean verifyPostTransform(Object root, Document isd, VerifierContext context) {
        if (!super.verifyPostTransform(root, isd, context))
            return false;
        else {
            boolean failed = false;
            if (!verifyPostTransformStyleConstraints(root, isd, context))
                failed = true;
            if (!verifyMaximumRegionCount(root, isd, context))
                failed = true;
            return !failed;
        }
    }

    private boolean verifyPostTransformStyleConstraints(final Object root, final Document isd, final VerifierContext context) {
        final Map<String,StyleSet> styleSets = getISDStyleSets(isd);
        try {
            final boolean[] failed = new boolean[] { false };
            Traverse.traverseElements(isd, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (!verifyPostTransformStyleConstraints(root, isd, elt, styleSets, context))
                        failed[0] = true;
                    return true;
                }
            });
            return !failed[0];
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyPostTransformStyleConstraints(Object root, Document isd, Element elt, Map<String,StyleSet> styleSets, VerifierContext context) {
        boolean failed = false;
        if (isTTParagraphElement(elt)) {
            if (!verifyLineHeight(root, isd, elt, styleSets, context))
                failed = true;
        } else if (isTTSpanElement(elt)) {
            if (!verifyFontFamily(root, isd, elt, styleSets, context))
                failed = true;
            if (!verifyTextOutlineThickness(root, isd, elt, styleSets, context))
                failed = true;
        }
        return !failed;
    }

    private QName isdCSSAttributeName = new QName(TTML1.Constants.NAMESPACE_TT_ISD, "css");

    private boolean verifyFontFamily(Object root, Document isd, Element elt, Map<String,StyleSet> styleSets, VerifierContext context) {
        boolean failed = false;
        String style = Documents.getAttribute(elt, isdCSSAttributeName, null);
        String av = null;
        if (style != null) {
            StyleSet css = styleSets.get(style);
            if (css != null) {
                StyleSpecification ss = css.get(IMSC1StyleVerifier.fontFamilyAttributeName);
                if (ss != null)
                    av = ss.getValue();
            }
        }
        if (av == null)
            av = "default";
        if (!isRecommendedFontFamily(av)) {
            Reporter reporter = context.getReporter();
            if (reporter.isWarningEnabled("uses-non-recommended-font-family")) {
                Message message = reporter.message(getLocator(elt), "*KEY*", "Computed value of font family is ''{0}''.", av);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean isRecommendedFontFamily(String family) {
        assert family != null;
        if (family.equals("default"))
            return true;
        else if (family.equals("monospaceSerif"))
            return true;
        else if (family.equals("proportionalSansSerif"))
            return true;
        else
            return false;
    }

    private boolean verifyLineHeight(Object root, Document isd, Element elt, Map<String,StyleSet> styleSets, VerifierContext context) {
        boolean failed = false;
        String style = Documents.getAttribute(elt, isdCSSAttributeName, null);
        String av = null;
        if (style != null) {
            StyleSet css = styleSets.get(style);
            if (css != null) {
                StyleSpecification ss = css.get(IMSC1StyleVerifier.lineHeightAttributeName);
                if (ss != null)
                    av = ss.getValue();
            }
        }
        if (av == null)
            av = "normal";
        if (av.equals("normal")) {
            Reporter reporter = context.getReporter();
            if (reporter.isWarningEnabled("uses-line-height-normal")) {
                Message message = reporter.message(getLocator(elt), "*KEY*", "Computed value of line height is ''{0}''.", av);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean verifyTextOutlineThickness(Object root, Document isd, Element elt, Map<String,StyleSet> styleSets, VerifierContext context) {
        boolean failed = false;
        String style = Documents.getAttribute(elt, isdCSSAttributeName, null);
        String fs = null;
        String to = null;
        if (style != null) {
            StyleSet css = styleSets.get(style);
            if (css != null) {
                StyleSpecification ss;
                if ((ss = css.get(IMSC1StyleVerifier.textOutlineAttributeName)) != null)
                    to = ss.getValue();
                if ((to != null) && ((ss = css.get(IMSC1StyleVerifier.fontSizeAttributeName)) != null))
                    fs = ss.getValue();
            }
        }
        if ((to != null) && !to.equals("none")) {
            if (fs == null)
                fs = "1c";
            if (root instanceof TimedText) {
                TimedText tt = (TimedText) root;
                Locator locator = getLocator(elt);
                QName eltName = Documents.getName(elt);
                double[] rootExtent = getRootExtent(tt);
                double[] cellResolution = getCellResolution(tt);
                double hRoot, hCell;
                assert rootExtent != null;
                if (rootExtent.length > 1)
                    hRoot = rootExtent[1];
                else
                    hRoot = 1;
                assert cellResolution != null;
                if (cellResolution.length > 1)
                    hCell = cellResolution[1];
                else
                    hCell = 1;
                Location fsLocation = new Location(elt, eltName, IMSC1StyleVerifier.fontSizeAttributeName, locator);
                double fsInPixels = getFontSizeInPixels(fs, fsLocation, hRoot, hCell, context);
                Location toLocation = new Location(elt, eltName, IMSC1StyleVerifier.textOutlineAttributeName, locator);
                double toInPixels = getTextOutlineThicknessInPixels(to, toLocation, fsInPixels, hRoot, hCell, context);
                if (toInPixels > 0.1 * fsInPixels) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(getLocator(elt),
                        "*KEY*",
                        "Computed value {0}px of text outline thickness must be less than or equal to 10% of computed value {1}px of font size.",
                        toInPixels, fsInPixels));
                }
            }
        }
        return !failed;
    }

    private double getFontSizeInPixels(String value, Location location, double hRoot, double hCell, VerifierContext context) {
        Integer[] minMax = new Integer[] { 1, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(value, location, context, minMax, treatments, lengths)) {
            Length fs;
            if (lengths.size() > 1)
                fs = lengths.get(1);
            else if (lengths.size() > 0)
                fs = lengths.get(0);
            else
                fs = null;
            if (fs != null)
                return getPixels(fs, 0, hRoot / hCell, hRoot, hCell);
        }
        return 1;
    }

    private double getTextOutlineThicknessInPixels(String value, Location location, double hFont, double hRoot, double hCell, VerifierContext context) {
        TextOutline[] outline = new TextOutline[1];
        if (Outline.isOutline(value, location, context, outline)) {
            Length to = outline[0].getThickness();
            if (to != null)
                return getPixels(to, hFont, hFont, hRoot, hCell);
        }
        return 0;
    }

    private boolean verifyMaximumRegionCount(final Object root, final Document isd, final VerifierContext context) {
        boolean failed = false;
        int maxRegions = MAX_REGIONS_PER_ISD;
        List<Element> regions = getISDRegionElements(isd);
        if (regions.size() > maxRegions) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root),
                "*KEY*", "Maximum number of regions exceeded in ISD instance, expected no more than {0}, got {1}.", maxRegions, regions.size()));
            failed = true;
        }
        return !failed;
    }
    
    private boolean isIMSCTextProfile(VerifierContext context) {
        String profile = (String) context.getResourceState(getModel().makeResourceStateName("profile"));
        return (profile != null) && profile.equals(PROFILE_TEXT_ABSOLUTE);
    }

    private boolean isIMSCImageProfile(VerifierContext context) {
        String profile = (String) context.getResourceState(getModel().makeResourceStateName("profile"));
        return (profile != null) && profile.equals(PROFILE_IMAGE_ABSOLUTE);
    }

}
