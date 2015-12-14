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

package com.skynav.ttv.verifier.netflix;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.netflix.NFLXTT;
import com.skynav.ttv.model.smpte.tt.rel2010.Image;
import com.skynav.ttv.model.ttml.TTML1.TTML1Model;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Layout;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ParameterVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.ttml.TTML1StyleVerifier;
import com.skynav.ttv.verifier.util.Integers;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.ZeroTreatment;

public class NFLXTTSemanticsVerifier extends ST20522010SemanticsVerifier {

    public NFLXTTSemanticsVerifier(Model model) {
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
            if (!verifyCellResolutionIfCellUnitUsed(tt))
                failed = true;
            if (!verifyExtentIfPixelUnitUsed(tt))
                failed = true;
            if (!verifyLengthAvoidsEmUnit(tt))
                failed = true;
            if (!verifyRegionContainment(tt))
                failed = true;
            if (!verifyRegionNonOverlap(tt))
                failed = true;
            if (isSDHProfile(tt) && !verifySDHConstraints(tt))
                failed = true;
        } else {
            QName rootName = context.getBindingElementName(root);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root), "*KEY*", "Root element must be ''{0}'', got ''{1}''.", TTML1Model.timedTextElementName, rootName));
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyTimedText(Object tt) {
        if (!super.verifyTimedText(tt))
            return false;
        else {
            assert tt instanceof TimedText;
            boolean failed = false;
            Reporter reporter = getContext().getReporter();
            if (((TimedText)tt).getProfile() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' attribute, but it is missing.",
                    TTML1Model.timedTextElementName, TTML1ProfileVerifier.profileAttributeName));
                failed = true;
            } else {
                try {
                    Set<URI> designators = getModel().getProfileDesignators();
                    String value = ((TimedText)tt).getProfile();
                    URI uri = new URI(value);
                    if (!designators.contains(uri)) {
                        reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' has a ''{1}'' attribute with value ''{2}'', but must have a value that matches one of following: {3}.",
                            TTML1Model.timedTextElementName, TTML1ProfileVerifier.profileAttributeName, value, designators));
                        failed = true;
                    }
                } catch (URISyntaxException e) {
                    // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
                }
            }
            if (((TimedText)tt).getHead() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    TTML1Model.timedTextElementName, TTML1Model.headElementName));
                failed = true;
            }
            if (((TimedText)tt).getBody() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    TTML1Model.timedTextElementName, TTML1Model.bodyElementName));
                failed = true;
            }
            return !failed;
        }
    }

    protected boolean verifyCharset(TimedText tt) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        try {
            Charset charsetRequired = Charset.forName(NFLXTT.Constants.CHARSET_REQUIRED);
            String charsetRequiredName = charsetRequired.name();
            Charset charset = (Charset) getContext().getResourceState("encoding");
            String charsetName = (charset != null) ? charset.name() : "unknown";
            if (!charsetName.equals(charsetRequiredName)) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Document encoding uses ''{0}'', but requires ''{1}''.", charsetName, charsetRequiredName));
                failed = true;
            }
        } catch (Exception e) {
            reporter.logError(e);
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyCellResolutionIfCellUnitUsed(TimedText tt) {
        boolean failed = false;
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) getContext().getResourceState("usageCell");
        if ((usage != null) && (usage.size() > 0)) {
            Element ttElement = (Element) getContext().getXMLNode(tt);
            QName cellResolutionName = TTML1ParameterVerifier.cellResolutionAttributeName;
            if (!ttElement.hasAttributeNS(cellResolutionName.getNamespaceURI(), cellResolutionName.getLocalPart())) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Uses ''c'' unit, but does not specify ''{0}'' attribute on ''{1}'' element.",
                                                       cellResolutionName, TTML1Model.timedTextElementName));
                }
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
                    reporter.logError(reporter.message(locator, "*KEY*", "Uses ''px'' unit, but does not specify ''{0}'' attribute on ''{1}'' element.",
                                                       TTML1StyleVerifier.extentAttributeName, TTML1Model.timedTextElementName));
                }
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verifyLengthAvoidsEmUnit(TimedText tt) {
        boolean failed = false;
        @SuppressWarnings("unchecked")
        Set<Locator> usage = (Set<Locator>) getContext().getResourceState("usageEm");
        if ((usage != null) && (usage.size() > 0)) {
            Reporter reporter = getContext().getReporter();
            for (Locator locator : usage)
                reporter.logError(reporter.message(locator, "*KEY*", "Uses disallowed ''em'' unit in length expression."));
            failed = true;
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
        double[] externalExtent = (double[]) getContext().getResourceState("externalExtent");
        String extent = tt.getExtent();
        if (extent != null) {
            extent = extent.trim();
            if (extent.equals("auto"))
                return externalExtent;
            else {
                Length[] lengths = parseLengthPair(extent, getLocator(tt), getContext().getReporter(), true);
                return new double[] { getPixels(lengths[0], 1, 1), getPixels(lengths[1], 1, 1) };
            }
        } else
            return externalExtent;
    }

    private double[] getCellResolution(TimedText tt) {
        String cellResolution = tt.getCellResolution();
        if (cellResolution != null) {
            cellResolution = cellResolution.trim();
            Integer[] integers = parseIntegerPair(cellResolution, getLocator(tt), getContext().getReporter());
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
                    x = getPixels(lengths[0], rootExtent[0], cellResolution[0]);
                    y = getPixels(lengths[1], rootExtent[1], cellResolution[1]);
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
                    w = getPixels(lengths[0], rootExtent[0], cellResolution[0]);
                    h = getPixels(lengths[1], rootExtent[1], cellResolution[1]);
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

    private double getPixels(Length length, double rootExtent, double cellResolution) {
        double value = length.getValue();
        Length.Unit units = length.getUnits();
        if (rootExtent < 0)
            rootExtent = 0;
        if (units == Length.Unit.Pixel)
            return value;
        else if (units == Length.Unit.Cell)
            return value * (rootExtent / cellResolution);
        else if (units == Length.Unit.Percentage)
            return value * (rootExtent / 100);
        else
            return 0;
    }

    protected boolean verifyRegionNonOverlap(TimedText tt) {
        return true;
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
                            reporter.logError(reporter.message(locator, "*KEY*", "Invalid length pair component ''{0}'', must use pixel (px) unit only.", l));
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

    private boolean isSDHProfile(TimedText tt) {
        String profile = tt.getProfile();
        if (profile != null) {
            return profile.equals(NFLXTT.Constants.PROFILE_SDH_ABSOLUTE);
        }
        return false;
    }

    protected boolean verifySDHConstraints(TimedText tt) {
        boolean failed = false;
        if (!verifySDHRegionGeometry(tt))
            failed = true;
        return !failed;
    }

    protected boolean verifySDHRegionGeometry(TimedText tt) {
        boolean failed = false;
        Head head = tt.getHead();
        if (head != null) {
            Layout layout = head.getLayout();
            if (layout != null) {
                for (Region r : layout.getRegion()) {
                    if (!verifySDHRegionGeometry(r))
                        failed = true;
                }
            }
        }
        return !failed;
    }

    protected static final String sdhRegionOrigin = "10% 10%";
    protected static final String sdhRegionExtent = "80% 80%";
    protected boolean verifySDHRegionGeometry(Region region) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        Locator locator = getLocator(region);
        Element regionElement = (Element) getContext().getXMLNode(region);
        QName originName = TTML1StyleVerifier.originAttributeName;
        if (regionElement.hasAttributeNS(originName.getNamespaceURI(), originName.getLocalPart())) {
            String origin = regionElement.getAttributeNS(originName.getNamespaceURI(), originName.getLocalPart());
            if (!origin.equals(sdhRegionOrigin)) {
                reporter.logError(reporter.message(locator, "*KEY*", "Invalid ''{0}'' attribute, got ''{1}'', expected ''{2}''.",
                    TTML1StyleVerifier.originAttributeName, origin, sdhRegionOrigin));
                failed = true;
            }
        } else {
            reporter.logError(reporter.message(locator, "*KEY*", "Missing ''{0}'' attribute, must be specified with value ''{1}''.",
                TTML1StyleVerifier.originAttributeName, sdhRegionOrigin));
            failed = true;
        }
        QName extentName = TTML1StyleVerifier.extentAttributeName;
        if (regionElement.hasAttributeNS(extentName.getNamespaceURI(), extentName.getLocalPart())) {
            String extent = regionElement.getAttributeNS(extentName.getNamespaceURI(), extentName.getLocalPart());
            if (!extent.equals(sdhRegionExtent)) {
                reporter.logError(reporter.message(locator, "*KEY*", "Invalid ''{0}'' attribute, got ''{1}'', expected ''{2}''.",
                    TTML1StyleVerifier.extentAttributeName, extent, sdhRegionExtent));
                failed = true;
            }
        } else {
            reporter.logError(reporter.message(locator, "*KEY*", "Missing ''{0}'' attribute, must be specified with value ''{1}''.",
                TTML1StyleVerifier.extentAttributeName, sdhRegionExtent));
            failed = true;
        }
        return !failed;
    }

    protected static final QName smpteImageElementName = new com.skynav.ttv.model.smpte.tt.rel2010.ObjectFactory().createImage(new Image()).getName();
    @Override
    protected boolean verifySMPTEImage(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (isSMPTEImageElement(content)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Element type ''{0}'' is not permitted.", smpteImageElementName));
            failed = true;
        } else {
            failed = super.verifySMPTEElement(content, locator, context);
        }
        return !failed;
    }

    @Override
    protected boolean verifyPostTransform(Object root, Document isd, VerifierContext context) {
        if (!super.verifyPostTransform(root, isd, context))
            return false;
        else {
            boolean failed = false;
            int maxRegions = NFLXTT.Constants.MAX_REGIONS;
            List<Element> regions = getISDRegionElements(isd);
            if (regions.size() > maxRegions) {
                Reporter reporter = context.getReporter();
                reporter.logError(reporter.message(getLocator(root),
                    "*KEY*", "Maximum number of regions exceeded in ISD instance, expected no more than {0}, got {1}.", maxRegions, regions.size()));
                failed = true;
            }
            return !failed;
        }
    }

}




