/*
 * Copyright 2013-16 Skynav, Inc. All rights reserved.
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

package com.skynav.ttx.transformer.isd;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.skynav.ttv.model.imsc.IMSC1;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.imsc.IMSC1SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML1StyleVerifier;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public class IMSC1Helper extends TTML2Helper {

    public static final String NAMESPACE_IMSC_STYLING           = IMSC1.Constants.NAMESPACE_IMSC_STYLING;
    public static final String NAMESPACE_ST20522010             = ST20522010.Constants.NAMESPACE_2010;
    public static final String NAMESPACE_EBUTT_STYLING          = IMSC1.Constants.NAMESPACE_EBUTT_STYLING;

    public static final QName  backgroundImageAttributeName     = new QName(NAMESPACE_ST20522010,       "backgroundImage");
    public static final QName  forcedDisplayAttributeName       = new QName(NAMESPACE_IMSC_STYLING,     "forcedDisplay");
    public static final QName  linePaddingAlignAttributeName    = new QName(NAMESPACE_EBUTT_STYLING,    "linePadding");
    public static final QName  multiRowAlignAttributeName       = new QName(NAMESPACE_EBUTT_STYLING,    "multiRowAlign");

    public static final String forcedParameterCondition         = "parameter('forced')";

    @Override
    public void enactISDPostTransforms(Document doc, TransformerContext context) {
        enactOrderDependentTransforms(doc, context);
        enactOrderIndependentTransforms(doc, context);
        maybeRecordTransformMetadata(doc, context);
    }

    private void enactOrderDependentTransforms(Document doc, TransformerContext context) {
        // none at this time
    }

    private void enactOrderIndependentTransforms(Document doc, TransformerContext context) {
        transformBackgroundImage(doc, context);
        transformForcedDisplay(doc, context);
        transformLinePadding(doc, context);
        transformMultiRowAlign(doc, context);
    }

    private void transformBackgroundImage(final Document doc, final TransformerContext context) {
        if (IMSC1SemanticsVerifier.isIMSCImageProfile(context)) {
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isDivisionElement(elt)) {
                            String source = Documents.getAttribute(elt, backgroundImageAttributeName);
                            if (source != null) {
                                source = source.trim();
                                if (!source.isEmpty()) {
                                    Element image = Documents.createElement(doc, imageElementName);
                                    Documents.setAttribute(image, sourceAttributeName, source);
                                    // [TBD] ensure that no image child is already present
                                    elt.appendChild(image);
                                    // record uses of ttml2 feature
                                    context.setResourceState(ResourceState.isdUsesTTML2Feature.name(), Boolean.TRUE);
                                }
                                Documents.removeAttribute(elt, backgroundImageAttributeName);
                            }
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }
    }

    private void transformForcedDisplay(Document doc, TransformerContext context) {
        final List<Element> styles  = new java.util.ArrayList<Element>();
        final List<Element> regions = new java.util.ArrayList<Element>();
        final List<Element> forced  = new java.util.ArrayList<Element>();
        try {
            Traverse.traverseElements(doc, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isISDStyleElement(elt)) {
                        styles.add(elt);
                    } else if (isISDRegionElement(elt)) {
                        regions.add(elt);
                    } else if (isContentElement(elt)) {
                        if (Documents.hasAttribute(elt, forcedDisplayAttributeName)) {
                            if (Boolean.parseBoolean(Documents.getAttribute(elt, forcedDisplayAttributeName, "").trim()))
                                forced.add(elt);
                            Documents.removeAttribute(elt, forcedDisplayAttributeName);
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            context.getReporter().logError(e);
        }
        if (!forced.isEmpty())
            transformForcedDisplay(doc, styles, regions, forced, context);
    }

    private static final Pattern styleIdentifierPattern = Pattern.compile("s(\\d+)");

    private void transformForcedDisplay(Document doc, List<Element> styles, List<Element> regions, List<Element> forced, TransformerContext context) {
        // extract style index count (maxIndex) and existing conditional index (cssIndex) if already present
        int maxIndex = -1;
        int cssIndex = -1;
        for (Element elt : styles) {
            String id = getXmlIdentifier(elt);
            Matcher m = styleIdentifierPattern.matcher(id);
            if (m.matches()) {
                String styleIndex = m.group(1);
                if (styleIndex != null) {
                    try {
                        int index = Integer.parseInt(styleIndex);
                        if (index > maxIndex)
                            maxIndex = index;
                        String condition = getCondition(elt);
                        if ((condition != null) && isForcedParameterCondition(condition))
                            cssIndex = index;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            if (cssIndex >= 0)
                break;
        }
        // create and insert conditional style if needed
        String forcedStyleIdentifier;
        if (cssIndex < 0) {
            forcedStyleIdentifier = "s" + (maxIndex + 1);
            Element css = Documents.createElement(doc, isdStyleElementName);
            Documents.setAttribute(css, TTML1StyleVerifier.visibilityAttributeName, "visible");
            Documents.setAttribute(css, XML.xmlIdentifierAttributeName, forcedStyleIdentifier);
            Documents.setAttribute(css, conditionAttributeName, forcedParameterCondition);
            if (!regions.isEmpty()) {
                Element firstRegion = regions.get(0);
                assert firstRegion != null;
                Element isdParent = (Element) firstRegion.getParentNode();
                isdParent.insertBefore(css, firstRegion);
            } else {
                context.getReporter().logError(new IllegalStateException("no content to conditionally style"));
            }
        } else {
            forcedStyleIdentifier = "s" + cssIndex;
        }
        // add reference from forced content to conditional visibility style
        for (Element elt : forced) {
            StringBuffer sb = new StringBuffer();
            sb.append(Documents.getAttribute(elt, isdStyleAttributeName, ""));
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(forcedStyleIdentifier);
            Documents.setAttribute(elt, isdStyleAttributeName, sb.toString());
        }
        // record uses forced status
        context.setResourceState(ResourceState.isdUsesForcedVisibility.name(), Boolean.TRUE);
        // record uses of ttml2 feature
        context.setResourceState(ResourceState.isdUsesTTML2Feature.name(), Boolean.TRUE);
    }

    private String getCondition(Element elt) {
        return Documents.getAttribute(elt, conditionAttributeName, null);
    }

    private boolean isForcedParameterCondition(String condition) {
        return (condition != null) && condition.trim().equals(forcedParameterCondition);
    }

    private void transformMultiRowAlign(final Document doc, final TransformerContext context) {
        if (IMSC1SemanticsVerifier.isIMSCTextProfile(context)) {
            final List<Element> styles = new java.util.ArrayList<Element>();
            final List<Element> paragraphs = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isISDStyleElement(elt)) {
                            styles.add(elt);
                        } else if (isParagraphElement(elt)) {
                            paragraphs.add(elt);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            if (!paragraphs.isEmpty())
                transformMultiRowAlign(doc, styles, paragraphs, context);
        }
    }

    private void transformMultiRowAlign(Document doc, List<Element> styles, List<Element> paragraphs, TransformerContext context) {
        Map<String,Element> styleMap = new java.util.HashMap<String,Element>();
        int maxStyleIndex = -1;
        for (Element s : styles) {
            String id = getXmlIdentifier(s);
            if (id != null) {
                Matcher m = styleIdentifierPattern.matcher(id);
                if (m.matches()) {
                    String styleIndex = m.group(1);
                    if (styleIndex != null) {
                        try {
                            int index = Integer.parseInt(styleIndex);
                            if (index > maxStyleIndex)
                                maxStyleIndex = index;
                        } catch (NumberFormatException e) {
                        }
                    }
                }
                styleMap.put(id, s);
            }
        }
        Element pFirst = null;
        List<Element> newStyles = new java.util.ArrayList<Element>();
        for (Element p : paragraphs) {
            if (pFirst == null)
                pFirst = p;
            String ma = getStyleAttribute(p, styleMap, multiRowAlignAttributeName, "auto");
            if (isMultiRowAlignValue(ma)) {
                String ta = getStyleAttribute(p, styleMap, TTML1StyleVerifier.textAlignAttributeName, "start");
                if (ma.equals("auto"))
                    ma = ta;
                if (!ma.equals(ta)) {
                    Element span = Documents.createElement(doc, spanElementName);
                    // add new style (css) element to represent text alignment as required
                    StringBuffer sb = new StringBuffer();
                    sb.append('s');
                    sb.append(++maxStyleIndex);
                    String id = sb.toString();
                    Element s = Documents.createElement(doc, isdStyleElementName);
                    setXmlIdentifier(s, id);
                    copyStyleAttributes(p, styleMap, s);
                    Documents.setAttribute(s, TTML1StyleVerifier.textAlignAttributeName, ma);
                    newStyles.add(s);
                    Documents.setAttribute(span, isdStyleAttributeName, id);
                    // capture child nodes
                    List<Node> children = new java.util.ArrayList<Node>();
                    for (Node n = p.getFirstChild(); n != null; n = n.getNextSibling())
                        children.add(n);
                    // remove child nodes, reparenting to new span
                    for (Node n : children)
                        span.appendChild(p.removeChild(n));
                    assert p.getFirstChild() == null;
                    // add new span to paragraph
                    p.appendChild(span);
                    // record uses of ttml2 feature
                    context.setResourceState(ResourceState.isdUsesTTML2Feature.name(), Boolean.TRUE);
                }
            }
        }
        // remove ebutts:multiRowAlign from current styles
        for (Element s : styles) {
            Documents.removeAttribute(s, multiRowAlignAttributeName);
        }
        // augment styles with new text align styles as generated above
        if (!newStyles.isEmpty()) {
            Element isd = getISD(pFirst);
            Element rFirst = getFirstRegion(isd);
            for (Element s : newStyles)
                isd.insertBefore(s, rFirst);
        }
    }

    private String getStyleAttribute(Element e, Map<String,Element> styles, QName styleName, String defaultValue) {
        String css = Documents.getAttribute(e, isdStyleAttributeName);
        if (css != null) {
            Element s = styles.get(css);
            if (s != null)
                return Documents.getAttribute(s, styleName, defaultValue);
        }
        return defaultValue;
    }

    private void copyStyleAttributes(Element e, Map<String,Element> styles, Element eTarget) {
        String css = Documents.getAttribute(e, isdStyleAttributeName);
        if (css != null) {
            Element s = styles.get(css);
            if (s != null) {
                Map<QName,String> attributes = Documents.getAttributes(s);
                for (Map.Entry<QName,String> a : attributes.entrySet()) {
                    QName n = a.getKey();
                    String v = a.getValue();
                    if (isStyleAttribute(n))
                        Documents.setAttribute(eTarget, n, v);
                }
            }
        }
    }

    private boolean isMultiRowAlignValue(String s) {
        if (s.equals("start"))
            return true;
        else if (s.equals("end"))
            return true;
        else if (s.equals("center"))
            return true;
        else if (s.equals("auto"))
            return true;
        else
            return false;
    }

    private void transformLinePadding(Document doc, TransformerContext context) {
    }

    private void maybeRecordTransformMetadata(Document doc, TransformerContext context) {
        maybeRecordUsesForcedVisibility(doc, context);
        maybeRecordRequiresTTML2(doc, context);
    }

    private void maybeRecordUsesForcedVisibility(Document doc, TransformerContext context) {
        Boolean usesForcedVisibility = (Boolean) context.getResourceState(ResourceState.isdUsesForcedVisibility.name());
        if ((usesForcedVisibility != null) && usesForcedVisibility) {
            Element ttm  = Documents.createElement(doc, metadataElementName);
            Element ttmi = Documents.createElement(doc, itemElementName);
            Documents.setAttribute(ttmi, nameAttributeName, "usesForced");
            ttm.appendChild(ttmi);
            Element root = doc.getDocumentElement();
            Documents.insertFirst(root, ttm);
        }
    }

    @Override
    public boolean hasUsableContent(Element elt) {
        if (super.hasUsableContent(elt))
            return true;
        else if (isImageElement(elt))
            return true;
        else if (hasBackgroundImage(elt))
            return true;
        else
            return false;
    }

    public static boolean isImageElement(Element elt) {
        return isTimedTextElement(elt, "image");
    }

    public static boolean hasBackgroundImage(Element elt) {
        if (Documents.hasAttribute(elt, backgroundImageAttributeName)) {
            String backgroundImage = Documents.getAttribute(elt, backgroundImageAttributeName, "").trim();
            return !backgroundImage.isEmpty();
        } else
            return false;
    }

}
