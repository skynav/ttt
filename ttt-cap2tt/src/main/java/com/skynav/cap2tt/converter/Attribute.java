/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.ttd.AnnotationPosition;
import com.skynav.ttv.model.ttml2.ttd.FontStyle;
import com.skynav.ttv.model.ttml2.ttd.TextAlign;

import com.skynav.cap2tt.app.Converter;

import static com.skynav.ttv.model.ttml.TTML2.Constants.NAMESPACE_TT_STYLE;

public class Attribute {
    public static Attribute END = new Attribute();
    private AttributeSpecification specification;
    private int count;
    private boolean retain;
    private String text;
    private String annotation;
    private Attribute() {
    }
    public Attribute(AttributeSpecification specification, int count, boolean retain) {
        assert specification != null;
        this.specification = specification;
        this.count = count;
        this.retain = retain;
    }
    private static String normalize(String s, boolean trim) {
        if (s != null) {
            String t = Converter.parseText(s, false);
            if (t != null)
                s = t;
            if (trim)
                s = s.trim();
            return s;
        } else
            return null;
    }
    public AttributeSpecification getSpecification() {
        return specification;
    }
    public int getCount() {
        return count;
    }
    public boolean getRetain() {
        return retain;
    }
    public void setText(String text) {
        this.text = normalize(text, false);
    }
    public String getText() {
        return text;
    }
    public void setAnnotation(String annotation) {
        this.annotation = normalize(annotation, true);
    }
    public String getAnnotation() {
        return annotation;
    }
    @Override
    public int hashCode() {
        return specification.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute) {
            Attribute other = (Attribute) obj;
            return other.specification.equals(specification);
        } else
            return false;
    }
    public boolean isRuby() {
        return specification.getName().startsWith("ルビ");
    }
    public boolean isEmphasis() {
        return specification.getName().startsWith("ルビ") && isEmphasisAnnotation();
    }
    public boolean isCombine() {
        return specification.getName().equals("組");
    }
    public boolean isEmphasisAnnotation() {
        if (annotation != null) {
            int i = 0;
            int n = annotation.length();
            for (; i < n; ++i) {
                char c = annotation.charAt(i);
                if (c == '\u2191')                  // U+2191 UPWARDS ARROW
                    continue;
                else if (c == '\u2193')             // U+2193 DOWNWARDS ARROW
                    continue;
                else if (c == '\u30FB')             // U+30FB KATAKANA MIDDLE DOT '・'
                    continue;
                else if (c == '\uFF3E')             // U+FF3E FULLWIDTH CIRCUMFLEX ACCENT
                    continue;
                else
                    break;
            }
            return i == n;
        } else
            return false;
    }
    public boolean hasPlacement() {
        String name = specification.getName();
        if (name.startsWith("横"))
            return true;
        else if (name.startsWith("縦"))
            return true;
        else
            return false;
    }
    public String getPlacement(boolean[] retGlobal) {
        String v;
        String name = specification.getName();
        if (name.startsWith("横")) {
            if (name.equals("横下"))
                v = name;
            else if (name.equals("横上"))
                v = name;
            else if (name.equals("横適"))
                v = name;
            else if (name.equals("横中"))
                v = name;
            else if (name.equals("横中央"))
                v = "横下";
            else if (name.equals("横中頭"))
                v = "横下";
            else if (name.equals("横中末"))
                v = "横下";
            else if (name.equals("横行頭"))
                v = "横下";
            else if (name.equals("横行末"))
                v = "横下";
            else
                v = null;
        } else if (name.startsWith("縦")) {
            if (name.equals("縦右"))
                v = name;
            else if (name.equals("縦左"))
                v = name;
            else if (name.equals("縦適"))
                v = name;
            else if (name.equals("縦中"))
                v = name;
            else if (name.equals("縦右頭"))
                v = "縦右";
            else if (name.equals("縦左頭"))
                v = "縦左";
            else if (name.equals("縦中頭"))
                v = "縦中";
            else
                v = null;
        } else
            v = null;
        if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
            retGlobal[0] = retain;
        return v;
    }
    private static final String[] alignments = new String[] {
        "右頭",
        "行頭",
        "行末",
        "左頭",
        "中央",
        "中頭",
        "中末",
        "両端"
    };
    public boolean hasAlignment() {
        String name = specification.getName();
        for (String a : alignments) {
            if (name.equals(a))
                return true;
            else if (name.endsWith(a))
                return true;
        }
        return false;
    }
    public String getAlignment(boolean[] retGlobal) {
        String v = null;
        String name = specification.getName();
        for (String a : alignments) {
            if (name.equals(a)) {
                v = a;
                break;
            } else if (name.endsWith(a)) {
                v = a;
                break;
            }
        }
        if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
            retGlobal[0] = retain;
        return v;
    }
    public boolean hasShear() {
        String name = specification.getName();
        if (name.equals("正体"))
            return true;
        else if (name.equals("斜"))
            return true;
        else
            return false;
    }
    public String getShear(boolean[] retGlobal) {
        String v;
        String name = specification.getName();
        if (name.equals("正体"))
            v = "0";
        else if (name.equals("斜")) {
            if (count < 0)
                v = "3";
            else if (count < Converter.defaultShearMap.length) {
                v = Integer.toString(count);
            } else
                v = Integer.toString(Converter.defaultShearMap.length - 1);
        } else
            v = null;
        if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
            retGlobal[0] = retain;
        return v;
    }
    public boolean hasKerning() {
        return specification.getName().equals("詰");
    }
    public String getKerning(boolean[] retGlobal) {
        String v;
        String name = specification.getName();
        if (name.equals("詰"))
            v = Integer.toString((count == 0) ? 0 : 1);
        else
            v = null;
        if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
            retGlobal[0] = retain;
        return v;
    }
    public AnnotationPosition getRubyPosition(Converter.Direction blockDirection) {
        AnnotationPosition v = null;
        String name = specification.getName();
        int l = name.length();
        if (name.startsWith("ルビ")) {
            if (l == 2)
                v = AnnotationPosition.OUTSIDE;
            else if (l > 2) {
                char c = name.charAt(2);
                if (blockDirection == Converter.Direction.TB) {
                    if (c == '上')
                        v = AnnotationPosition.BEFORE;
                    else if (c == '下')
                        v = AnnotationPosition.AFTER;
                } else if (blockDirection == Converter.Direction.RL) {
                    if (c == '右')
                        v = AnnotationPosition.BEFORE;
                    else if (c == '左')
                        v = AnnotationPosition.AFTER;
                } else if (blockDirection == Converter.Direction.LR) {
                    if (c == '右')
                        v = AnnotationPosition.AFTER;
                    else if (c == '左')
                        v = AnnotationPosition.BEFORE;
                }
            }
        }
        return v;
    }
    private static final String[] typefaces = new String[] {
        "丸ゴ",
        "丸ゴシック",
        "角ゴ",
        "太角ゴ",
        "太角ゴシック",
        "太明",
        "太明朝",
        "シネマ"
    };
    public boolean hasTypeface() {
        String name = specification.getName();
        for (String t : typefaces) {
            if (name.equals(t))
                return true;
        }
        return false;
    }
    public String getTypeface(boolean[] retGlobal) {
        String v = null;
        String name = specification.getName();
        for (String t : typefaces) {
            if (name.equals(t)) {
                v = t;
                break;
            }
        }
        if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
            retGlobal[0] = retain;
        return v;
    }
    public void populate(Paragraph p, Set<QName> styles, String defaultRegion, float[] shears) {
        String name = specification.getName();
        Map<QName, String> attributes = p.getOtherAttributes();
        if (name.equals("横下")) {
            attributes.put(Converter.regionAttrName, "横下");
        } else if (name.equals("横上")) {
            attributes.put(Converter.regionAttrName, "横上");
        } else if (name.equals("横適")) {
            attributes.put(Converter.regionAttrName, "横適");
        } else if (name.equals("横中")) {
            attributes.put(Converter.regionAttrName, "横中");
        } else if (name.equals("縦右")) {
            attributes.put(Converter.regionAttrName, "縦右");
        } else if (name.equals("縦左")) {
            attributes.put(Converter.regionAttrName, "縦左");
        } else if (name.equals("縦適")) {
            attributes.put(Converter.regionAttrName, "縦適");
        } else if (name.equals("縦中")) {
            attributes.put(Converter.regionAttrName, "縦中");
        } else if (name.equals("中央")) {
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("行頭")) {
            p.setTextAlign(TextAlign.START);
        } else if (name.equals("行末")) {
            p.setTextAlign(TextAlign.END);
        } else if (name.equals("中頭")) {
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("中末")) {
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("両端")) {
            p.setTextAlign(TextAlign.JUSTIFY);
        } else if (name.equals("横中央")) {
            attributes.put(Converter.regionAttrName, "横下");
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("横中頭")) {
            attributes.put(Converter.regionAttrName, "横下");
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("横中末")) {
            attributes.put(Converter.regionAttrName, "横下");
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("横行頭")) {
            attributes.put(Converter.regionAttrName, "横下");
            p.setTextAlign(TextAlign.START);
        } else if (name.equals("横行末")) {
            attributes.put(Converter.regionAttrName, "横下");
            p.setTextAlign(TextAlign.END);
        } else if (name.equals("縦右頭")) {
            attributes.put(Converter.regionAttrName, "縦右");
        } else if (name.equals("縦左頭")) {
            attributes.put(Converter.regionAttrName, "縦左");
        } else if (name.equals("縦中頭")) {
            attributes.put(Converter.regionAttrName, "縦中");
            p.setTextAlign(TextAlign.CENTER);
        } else if (name.equals("正体")) {
            p.setFontStyle(FontStyle.NORMAL);
            attributes.put(Converter.ttsFontShearAttrName, "0%");
        } else if (name.equals("斜")) {
            float shear;
            if (count < 0)
                shear = shears[3];
            else if (count < shears.length)
                shear = shears[count];
            else
                shear = shears[shears.length - 1];
            StringBuffer sb = new StringBuffer();
            if (shear == 0)
                sb.append('0');
            else
                sb.append(Float.toString(shear));
            sb.append('%');
            attributes.put(Converter.ttsFontShearAttrName, sb.toString());
        } else if (name.equals("詰")) {
            String kerning;
            if (count < 0)
                kerning = "normal";
            else if (count == 0)
                kerning = "none";
            else
                kerning = "normal";
            attributes.put(Converter.ttsFontKerningAttrName, kerning);
        } else if (name.equals("幅広")) {
            p.setFontSize("1.5em 1.0em");
        } else if (name.equals("倍角")) {
            p.setFontSize("2.0em 1.0em");
        } else if (name.equals("半角")) {
            p.setFontSize("0.5em 1.0em");
        } else if (name.equals("拗音")) {
            p.setFontSize("0.9em 1.0em");
        } else if (name.equals("幅")) {
            int stretch;
            if (count < 0)
                stretch = 100;
            else if (count < 5)
                stretch = 50;
            else if (count < 20)
                stretch = count * 10;
            else
                stretch = 200;
            p.setFontSize("" + Double.toString((double) stretch / 100.0) + "em" + " 1.0em");
        } else if (name.equals("寸")) {
            int scale;
            if (count < 0)
                scale = 100;
            else if (count < 5)
                scale = 50;
            else if (count < 20)
                scale = count * 10;
            else
                scale = 200;
            p.setFontSize(((double) scale / 100.0) + "em");
        } else if (name.equals("継続")) {
        } else if (name.equals("丸ゴ")) {
            p.setFontFamily("丸ゴ");
        } else if (name.equals("丸ゴシック")) {
            p.setFontFamily("丸ゴシック");
        } else if (name.equals("角ゴ")) {
            p.setFontFamily("角ゴ");
        } else if (name.equals("太角ゴ")) {
            p.setFontFamily("太角ゴ");
        } else if (name.equals("太角ゴシック")) {
            p.setFontFamily("太角ゴシック");
        } else if (name.equals("太明")) {
            p.setFontFamily("太明");
        } else if (name.equals("太明朝")) {
            p.setFontFamily("太明朝");
        } else if (name.equals("シネマ")) {
            p.setFontFamily("シネマ");
        }
        String region = attributes.get(Converter.regionAttrName);
        if ((region != null) && (defaultRegion != null) && region.equals(defaultRegion))
            attributes.remove(Converter.regionAttrName);
        updateStyles(p, styles);
    }
    public void updateStyles(Paragraph p, Set<QName> styles) {
        if (p.getFontFamily() != null) {
            styles.add(Converter.ttsFontFamilyAttrName);
        }
        if (p.getFontSize() != null) {
            styles.add(Converter.ttsFontSizeAttrName);
        }
        if (p.getFontStyle() != null) {
            styles.add(Converter.ttsFontStyleAttrName);
        }
        if (p.getTextAlign() != null) {
            styles.add(Converter.ttsTextAlignAttrName);
        }
        for (QName qn : p.getOtherAttributes().keySet()) {
            String ns = qn.getNamespaceURI();
            if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE))
                styles.add(qn);
        }
    }
    public void populate(Span s, Set<QName> styles, float[] shears) {
        String name = specification.getName();
        Map<QName, String> attributes = s.getOtherAttributes();
        if (name.equals("正体")) {
            attributes.put(Converter.ttsFontShearAttrName, "0%");
        } else if (name.equals("斜")) {
            float shear;
            if (count < 0)
                shear = shears[3];
            else if (count < shears.length)
                shear = shears[count];
            else
                shear = shears[shears.length - 1];
            StringBuffer sb = new StringBuffer();
            if (shear == 0)
                sb.append('0');
            else
                sb.append(Float.toString(shear));
            sb.append('%');
            attributes.put(Converter.ttsFontShearAttrName, sb.toString());
        } else if (name.equals("詰")) {
            String kerning;
            if (count < 0)
                kerning = "auto";
            else if (count == 0)
                kerning = "none";
            else
                kerning = "normal";
            attributes.put(Converter.ttsFontKerningAttrName, kerning);
        } else if (name.equals("幅広")) {
            attributes.put(Converter.ttsFontSizeAttrName, "1.5em 1.0em");
        } else if (name.equals("倍角")) {
            attributes.put(Converter.ttsFontSizeAttrName, "2.0em 1.0em");
        } else if (name.equals("半角")) {
            attributes.put(Converter.ttsFontSizeAttrName, "0.5em 1.0em");
        } else if (name.equals("拗音")) {
            attributes.put(Converter.ttsFontSizeAttrName, "0.9em 1.0em");
        } else if (name.equals("幅")) {
            int stretch;
            if (count < 0)
                stretch = 100;
            else if (count < 5)
                stretch = 50;
            else if (count < 20)
                stretch = count * 10;
            else
                stretch = 200;
            attributes.put(Converter.ttsFontSizeAttrName, "" + Double.toString((double) stretch / 100.0) + "em" + " 1.0em");
        } else if (name.equals("寸")) {
            int scale;
            if (count < 0)
                scale = 100;
            else if (count < 5)
                scale = 50;
            else if (count < 20)
                scale = count * 10;
            else
                scale = 200;
            attributes.put(Converter.ttsFontSizeAttrName, ((double) scale / 100.0) + "em");
        }
        updateStyles(s, styles);
    }
    public void updateStyles(Span s, Set<QName> styles) {
        if (s.getFontFamily() != null) {
            styles.add(Converter.ttsFontFamilyAttrName);
        }
        if (s.getFontSize() != null) {
            styles.add(Converter.ttsFontSizeAttrName);
        }
        if (s.getFontStyle() != null) {
            styles.add(Converter.ttsFontStyleAttrName);
        }
        if (s.getTextAlign() != null) {
            styles.add(Converter.ttsTextAlignAttrName);
        }
        for (QName qn : s.getOtherAttributes().keySet()) {
            String ns = qn.getNamespaceURI();
            if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE))
                styles.add(qn);
        }
    }
}

// Local Variables:
// coding: utf-8-unix
// End:
