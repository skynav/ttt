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

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.style.Annotation;
import com.skynav.ttpe.style.AnnotationStyleCollector;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.Emphasis;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.StyleCollector;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.style.Constants.*;
import static com.skynav.ttpe.text.Constants.*;

public class AnnotationCollector extends PhraseCollector {

    private List<Phrase> bases;                                 // base phrases
    private List<Integer> baseStarts;                           // indices into base phrases list of start of base containers
    private Map<Phrase,List<Phrase>> annotations;               // annotations expressed as map from bases to annotation lists
    private int currentBase;                                    // current index into bases when processing text container and text spans

    public AnnotationCollector(AnnotationStyleCollector styleCollector) {
        super(styleCollector);
        this.currentBase = -1;
    }

    public List<Phrase> collect(Element e) {
        Annotation annotation = styleCollector.getAnnotation(e);
        if (annotation == Annotation.EMPHASIS)
            return collectEmphasis(e);
        else
            return collectAnnotation(e);
    }

    @Override
    protected void emit(Element e) {
        if (bases != null) {
            for (Phrase base : bases) {
                if (annotations != null) {
                    List<Phrase> baseAnnotations = annotations.get(base);
                    if (baseAnnotations != null) {
                        base.add(StyleAttribute.ANNOTATIONS, baseAnnotations.toArray(new Phrase[baseAnnotations.size()]), 0, base.length());
                    }
                }
            }
            add(new AnnotationPhrase(e, bases, styleCollector.extract()));
        }
    }

    private List<Phrase> collectEmphasis(Element e) {
        clear();
        collectBaseWithEmphasis(e);
        collectTextWithEmphasis(e);
        emit(e);
        return extract();
    }

    private void collectBaseWithEmphasis(Element eBase) {
        collectBase(eBase, true);
    }

    private void collectTextWithEmphasis(Element eBase) {
        Element eText = null;
        if (currentBase < 0) {
            if ((baseStarts == null) || baseStarts.isEmpty())
                currentBase = -1;
            else
                currentBase = baseStarts.get(baseStarts.get(0));
        }
        if (currentBase >= 0) {
            Defaults defaults = styleCollector.getDefaults();
            Document d = eBase.getOwnerDocument();
            for (int i = currentBase, n = bases.size(); i < n; ++i) {
                Phrase b = bases.get(i);
                if (b != null) {
                    int nb = b.length();
                    if (nb > 0) {
                        Emphasis emphasis = b.getEmphasis(-1, defaults);
                        if ((emphasis != null) && !emphasis.isNone()) {
                            String emphasisText = emphasis.resolveText(b.getFont(-1, defaults).isVertical() ? Axis.VERTICAL : Axis.HORIZONTAL);
                            assert (emphasisText != null) && (emphasisText.length() > 0);
                            emphasisText = emphasisText.substring(0,1);
                            StringBuffer sb = new StringBuffer();
                            for (int j = 0; j < nb; ++j)
                                sb.append(emphasisText);
                            Text t = d.createTextNode(sb.toString());
                            eText = Documents.createElement(d, ttSpanElementName);
                            styleCollector.addStyle(eText, ttsRubyPositionAttrName, getRubyPosition(emphasis));
                            eText.appendChild(t);
                            collectText(eText, true);
                        }
                    }
                }
            }
        }
    }

    private String getRubyPosition(Emphasis e) {
        Emphasis.Position p = e.getPosition();
        if (p == Emphasis.Position.BEFORE)
            return "before";
        else if (p == Emphasis.Position.AFTER)
            return "after";
        else
            return "auto";
    }

    private List<Phrase> collectAnnotation(Element e) {
        clear();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                // ignore #PCDATA
            } else if (n instanceof Element) {
                Element c = (Element) n;
                if (Documents.isElement(c, ttSpanElementName)) {
                    Annotation annotation = styleCollector.getAnnotation(c);
                    if (annotation == null) {
                        // ignore span children that do not specify tts:ruby
                    } else if (annotation == Annotation.BASE_CONTAINER) {
                        addBaseStart();
                        collectBaseContainer(c);
                    } else if (annotation == Annotation.TEXT_CONTAINER) {
                        if (baseStarts != null) {
                            collectTextContainer(c);
                        }
                    } else if (annotation == Annotation.BASE) {
                        addBaseStart();
                        collectBase(c);
                    } else if (annotation == Annotation.TEXT) {
                        if (baseStarts != null) {
                            collectText(c);
                        }
                    } else if (annotation == Annotation.DELIMITER) {
                        collectDelimiter(c);
                    } else {
                        throw new IllegalStateException();
                    }
                } else {
                    // ignore non-span children
                }
            }
        }
        if (bases != null)
            addBaseStart();
        emit(e);
        return extract();
    }

    private void collectBaseContainer(Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                // ignore #PCDATA
            } else if (n instanceof Element) {
                Element c = (Element) n;
                if (Documents.isElement(c, ttSpanElementName)) {
                    Annotation annotation = styleCollector.getAnnotation(c);
                    if (annotation == null) {
                        // ignore span children that do not specify tts:ruby
                    } else if (annotation == Annotation.BASE) {
                        collectBase(c);
                    } else {
                        // ignore non-base annotation children
                    }
                } else {
                    // ignore non-span children
                }
            }
        }
    }

    private void collectTextContainer(Element e) {
        if (currentBase < 0) {
            if ((baseStarts == null) || baseStarts.isEmpty())
                currentBase = -1;
            else
                currentBase = baseStarts.get(baseStarts.size() - 1);
        }
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                // ignore #PCDATA
            } else if (n instanceof Element) {
                Element c = (Element) n;
                if (Documents.isElement(c, ttSpanElementName)) {
                    Annotation annotation = styleCollector.getAnnotation(c);
                    if (annotation == null) {
                        // ignore span children that do not specify tts:ruby
                    } else if (annotation == Annotation.TEXT) {
                        collectText(c);
                    } else {
                        // ignore non-text annotation children
                    }
                } else {
                    // ignore non-span children
                }
            }
        }
        currentBase = -1;
    }

    private void collectBase(Element e) {
        collectBase(e, false);
    }

    private void collectBase(Element e, boolean emphasis) {
        StringBuffer text = new StringBuffer();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                String t = ((Text) n).getWholeText();
                if (t != null)
                    text.append(t);
            } else {
                // [TBD] handle nested spans when collecting emphasis as base content
            }
        }
        if (emphasis) {
            for (int i = 0, n = text.length(); i < n; ++i) {
                char c = text.charAt(i);
                addBaseStart();
                collectBase(e, new String(new char[]{c}));
            }
        } else
            collectBase(e, text.toString());
    }

    private void collectBase(Element e, String content) {
        StyleCollector sc = new AnnotationStyleCollector(styleCollector, null);
        sc.collectSpanStyles(e, -1, -1);
        sc.collectContentStyles(content, 0, content.length());
        addBase(e, content, sc.extract());
    }

    private void collectText(Element e) {
        collectText(e, false);
    }

    private void collectText(Element e, boolean emphasis) {
        if (currentBase < 0) {
            if ((baseStarts == null) || baseStarts.isEmpty())
                currentBase = -1;
            else
                currentBase = baseStarts.get(baseStarts.size() - 1);
        }
        if (currentBase < bases.size()) {
            StringBuffer text = new StringBuffer();
            for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Text) {
                    String t = ((Text) n).getWholeText();
                    if (t != null)
                        text.append(t);
                }
            }
            StyleCollector sc = new AnnotationStyleCollector(styleCollector, (currentBase < 0) ? null : bases.get(currentBase).getElement());
            sc.collectSpanStyles(e, -1, -1);
            String content = text.toString();
            sc.collectContentStyles(content, 0, content.length());
            if (!emphasis || !bases.get(currentBase).isWhitespace())
                addAnnotation(e, content, sc.extract());
            ++currentBase;
        } else
            currentBase = -1;
    }

    private void collectDelimiter(Element e) {
        // [TBD] - IMPLEMENT ME
    }

    private void addBase(Element e, String base, List<StyleAttributeInterval> attributes) {
        if (bases == null)
            bases = new java.util.ArrayList<Phrase>();
        bases.add(newPhrase(e, base, attributes));
    }

    private void addBaseStart() {
        if (baseStarts == null)
            baseStarts = new java.util.ArrayList<Integer>();
        baseStarts.add(bases != null ? bases.size() : 0);
    }

    private void addAnnotation(Element e, String annotation, List<StyleAttributeInterval> attributes) {
        if (currentBase >= 0) {
            Phrase base = bases.get(currentBase);
            if (annotations == null)
                annotations = new java.util.HashMap<Phrase,List<Phrase>>();
            List<Phrase> baseAnnotations = annotations.get(base);
            if (baseAnnotations == null) {
                baseAnnotations = new java.util.ArrayList<Phrase>();
                annotations.put(base, baseAnnotations);
            }
            baseAnnotations.add(newPhrase(e, annotation, attributes));
        }
    }

}
