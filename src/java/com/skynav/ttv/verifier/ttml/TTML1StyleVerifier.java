/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.verifier.ttml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Set;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.Style;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttd.Direction;
import com.skynav.ttv.model.ttml1.ttd.Display;
import com.skynav.ttv.model.ttml1.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml1.ttd.FontStyle;
import com.skynav.ttv.model.ttml1.ttd.FontWeight;
import com.skynav.ttv.model.ttml1.ttd.Overflow;
import com.skynav.ttv.model.ttml1.ttd.ShowBackground;
import com.skynav.ttv.model.ttml1.ttd.TextAlign;
import com.skynav.ttv.model.ttml1.ttd.TextDecoration;
import com.skynav.ttv.model.ttml1.ttd.UnicodeBidi;
import com.skynav.ttv.model.ttml1.ttd.Visibility;
import com.skynav.ttv.model.ttml1.ttd.WrapOption;
import com.skynav.ttv.model.ttml1.ttd.WritingMode;
import com.skynav.ttv.util.Enums;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.style.BackgroundColorVerifier;
import com.skynav.ttv.verifier.ttml.style.ColorVerifier;
import com.skynav.ttv.verifier.ttml.style.DirectionVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayVerifier;
import com.skynav.ttv.verifier.ttml.style.ExtentVerifier;
import com.skynav.ttv.verifier.ttml.style.FontFamilyVerifier;
import com.skynav.ttv.verifier.ttml.style.FontSizeVerifier;
import com.skynav.ttv.verifier.ttml.style.FontStyleVerifier;
import com.skynav.ttv.verifier.ttml.style.FontWeightVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OpacityVerifier;
import com.skynav.ttv.verifier.ttml.style.OriginVerifier;
import com.skynav.ttv.verifier.ttml.style.OverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.PaddingVerifier;
import com.skynav.ttv.verifier.ttml.style.ShowBackgroundVerifier;
import com.skynav.ttv.verifier.ttml.style.StyleAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.RegionAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.TextAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.TextDecorationVerifier;
import com.skynav.ttv.verifier.ttml.style.TextOutlineVerifier;
import com.skynav.ttv.verifier.ttml.style.UnicodeBidiVerifier;
import com.skynav.ttv.verifier.ttml.style.VisibilityVerifier;
import com.skynav.ttv.verifier.ttml.style.WrapOptionVerifier;
import com.skynav.ttv.verifier.ttml.style.WritingModeVerifier;
import com.skynav.ttv.verifier.ttml.style.ZIndexVerifier;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Strings;

import static com.skynav.ttv.model.ttml.TTML1.Constants.*;

public class TTML1StyleVerifier implements StyleVerifier {

    public static final String NAMESPACE = NAMESPACE_TT_STYLE;

    public static final String getStyleNamespaceUri() {
        return NAMESPACE;
    }

    private static QName extentAttributeName = new QName(NAMESPACE,"extent");
    private static Object[][] styleAccessorMap = new Object[][] {
        {
            new QName(NAMESPACE,"backgroundColor"),        // attribute name
            "BackgroundColor",                               // accessor method name suffix
            String.class,                                       // value type
            BackgroundColorVerifier.class,                      // specialized verifier
            Boolean.FALSE,                                      // padding permitted
            "transparent",                                      // initial (default) value
        },
        {
            new QName(NAMESPACE,"color"),
            "Color",
            String.class,
            ColorVerifier.class,
            Boolean.FALSE,
            "white",
        },
        {
            new QName(NAMESPACE,"direction"),
            "Direction",
            Direction.class,
            DirectionVerifier.class,
            Boolean.FALSE,
            Direction.LTR,
        },
        {
            new QName(NAMESPACE,"display"),
            "Display",
            Display.class,
            DisplayVerifier.class,
            Boolean.FALSE,
            Display.AUTO,
        },
        {
            new QName(NAMESPACE,"displayAlign"),
            "DisplayAlign",
            DisplayAlign.class,
            DisplayAlignVerifier.class,
            Boolean.FALSE,
            DisplayAlign.BEFORE,
        },
        {
            extentAttributeName,
            "Extent",
            String.class,
            ExtentVerifier.class,
            Boolean.FALSE,
            "auto",
        },
        {
            new QName(NAMESPACE,"fontFamily"),
            "FontFamily",
            String.class,
            FontFamilyVerifier.class,
            Boolean.TRUE,
            "default",
        },
        {
            new QName(NAMESPACE,"fontSize"),
            "FontSize",
            String.class,
            FontSizeVerifier.class,
            Boolean.FALSE,
            "1c",
        },
        {
            new QName(NAMESPACE,"fontStyle"),
            "FontStyle",
            FontStyle.class,
            FontStyleVerifier.class,
            Boolean.FALSE,
            FontStyle.NORMAL,
        },
        {
            new QName(NAMESPACE,"fontWeight"),
            "FontWeight",
            FontWeight.class,
            FontWeightVerifier.class,
            Boolean.FALSE,
            FontWeight.NORMAL,
        },
        {
            new QName(NAMESPACE,"lineHeight"),
            "LineHeight",
            String.class,
            LineHeightVerifier.class,
            Boolean.FALSE,
            "normal",
        },
        {
            new QName(NAMESPACE,"opacity"),
            "Opacity",
            Float.class,
            OpacityVerifier.class,
            Boolean.FALSE,
            Float.valueOf(1.0F),
        },
        {
            new QName(NAMESPACE,"origin"),
            "Origin",
            String.class,
            OriginVerifier.class,
            Boolean.FALSE,
            "auto",
        },
        {
            new QName(NAMESPACE,"overflow"),
            "Overflow",
            Overflow.class,
            OverflowVerifier.class,
            Boolean.FALSE,
            Overflow.HIDDEN,
        },
        {
            new QName(NAMESPACE,"padding"),
            "Padding",
            String.class,
            PaddingVerifier.class,
            Boolean.FALSE,
            "0px",
        },
        {
            new QName("","region"),
            "Region",
            Object.class,
            RegionAttributeVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            new QName(NAMESPACE,"showBackground"),
            "ShowBackground",
            ShowBackground.class,
            ShowBackgroundVerifier.class,
            Boolean.FALSE,
            ShowBackground.ALWAYS,
        },
        {
            new QName("","style"),
            "StyleAttribute",
            List.class,
            StyleAttributeVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            new QName(NAMESPACE,"textAlign"),
            "TextAlign",
            TextAlign.class,
            TextAlignVerifier.class,
            Boolean.FALSE,
            TextAlign.START,
        },
        {
            new QName(NAMESPACE,"textDecoration"),
            "TextDecoration",
            TextDecoration.class,
            TextDecorationVerifier.class,
            Boolean.FALSE,
            TextDecoration.NONE,
        },
        {
            new QName(NAMESPACE,"textOutline"),
            "TextOutline",
            String.class,
            TextOutlineVerifier.class,
            Boolean.FALSE,
            "none",
        },
        {
            new QName(NAMESPACE,"unicodeBidi"),
            "UnicodeBidi",
            UnicodeBidi.class,
            UnicodeBidiVerifier.class,
            Boolean.FALSE,
            UnicodeBidi.NORMAL,
        },
        {
            new QName(NAMESPACE,"visibility"),
            "Visibility",
            Visibility.class,
            VisibilityVerifier.class,
            Boolean.FALSE,
            Visibility.VISIBLE,
        },
        {
            new QName(NAMESPACE,"wrapOption"),
            "WrapOption",
            WrapOption.class,
            WrapOptionVerifier.class,
            Boolean.FALSE,
            WrapOption.WRAP,
        },
        {
            new QName(NAMESPACE,"writingMode"),
            "WritingMode",
            WritingMode.class,
            WritingModeVerifier.class,
            Boolean.FALSE,
            WritingMode.LRTB,
        },
        {
            new QName(NAMESPACE,"zIndex"),
            "ZIndex",
            String.class,
            ZIndexVerifier.class,
            Boolean.FALSE,
            "auto",
        },
    };

    private Model model;
    private Map<QName, StyleAccessor> accessors;

    public TTML1StyleVerifier(Model model) {
        populate(model);
    }

    public QName getStyleAttributeName(String propertyName) {
        // assumes that property name is same as local part of qualified attribute name, which
        // is presently true in TTML1
        for (QName name : accessors.keySet()) {
            if (propertyName.equals(name.getLocalPart()))
                return name;
        }
        return null;
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else if (type == ItemType.Element)
            return verifyElementItem(content, locator, context);
        else if (type == ItemType.Other)
            return verifyOtherAttributes(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    private boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        for (QName name : accessors.keySet()) {
            StyleAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, context))
                failed = true;
        }
        return !failed;
    }

    private boolean verifyElementItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof Set)
            failed = !verify((Set) content, locator, context);
        if (failed)
            context.getReporter().logError(locator, "Invalid '" + context.getBindingElementName(content) + "' styled item.");
        return !failed;
    }

    private boolean verifyOtherAttributes(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
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
            if (name.getNamespaceURI().equals(NAMESPACE)) {
                if ((content instanceof TimedText) && name.equals(extentAttributeName))
                    continue;
                else if (!isStyleAttribute(name)) {
                    context.getReporter().logError(locator, "Unknown attribute in TT Style namespace '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                } else if (!permitsStyleAttribute(content)) {
                    context.getReporter().logError(locator, "TT Style attribute '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean permitsStyleAttribute(Object content) {
        if (content instanceof Body)
            return true;
        else if (content instanceof Division)
            return true;
        else if (content instanceof Paragraph)
            return true;
        else if (content instanceof Span)
            return true;
        else if (content instanceof Break)
            return true;
        else if (content instanceof Style)
            return true;
        else if (content instanceof Region)
            return true;
        else if (content instanceof Set)
            return true;
        else
            return false;
    }

    private boolean isStyleAttribute(QName name) {
        return name.getNamespaceURI().equals(NAMESPACE) && accessors.containsKey(name);
    }

    public boolean verify(Set content, Locator locator, VerifierContext context) {
        boolean failed = false;
        int numStyleAttributes = 0;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            Node attribute = attributes.item(i);
            String nsUri = attribute.getNamespaceURI();
            if ((nsUri != null) && nsUri.equals(NAMESPACE))
                ++numStyleAttributes;
        }
        if (numStyleAttributes > 1) {
            context.getReporter().logInfo(locator, "Style attribute count exceeds maximum, got " + numStyleAttributes + ", expected no more than 1.");
            failed = true;
        }
        return !failed;
    }

    private void populate(Model model) {
        Map<QName, StyleAccessor> accessors = new java.util.HashMap<QName, StyleAccessor>();
        for (Object[] styleAccessorEntry : styleAccessorMap) {
            assert styleAccessorEntry.length >= 6;
            QName styleName = (QName) styleAccessorEntry[0];
            String accessorName = (String) styleAccessorEntry[1];
            Class<?> valueClass = (Class<?>) styleAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) styleAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) styleAccessorEntry[4]).booleanValue();
            Object defaultValue = styleAccessorEntry[5];
            accessors.put(styleName, new StyleAccessor(styleName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private static final QName styleAttributeName = new QName("", "style");
    private static final QName regionAttributeName = new QName("", "region");
    private class StyleAccessor {

        private QName styleName;
        private String getterName;
        private String setterName;
        private Class<?> valueClass;
        private StyleValueVerifier verifier;
        private boolean paddingPermitted;
        private Object defaultValue;

        public StyleAccessor(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            populate(styleName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue);
        }

        private boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getStyleValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, content, (String) value, locator, context);
                else
                    success = verifier.verify(model, content, styleName, value, locator, context);
            } else
                setStyleDefaultValue(content);
            if (!success) {
                if (value != null) {
                    if (styleName.equals(styleAttributeName)) {
                        value = IdReferences.getIdReferences(value);
                    } else if (styleName.equals(regionAttributeName)) {
                        value = IdReferences.getIdReference(value);
                    } else
                        value = value.toString();
                    context.getReporter().logError(locator, "Invalid " + styleName + " value '" + value + "'.");
                }
            }
            return success;
        }

        private boolean verify(Model model, Object content, String value, Locator locator, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            if (value.length() == 0)
                reporter.logInfo(locator, "Empty " + styleName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                reporter.logInfo(locator, "The value of " + styleName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                reporter.logInfo(locator, "XML space padding not permitted on " + styleName + ", got '" + value + "'.");
            else
                success = verifier.verify(model, content, styleName, value, locator, context);
            return success;
        }

        private StyleValueVerifier createStyleValueVerifier(Class<?> verifierClass) {
            try {
                return (StyleValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            this.styleName = styleName;
            this.getterName = "get" + accessorName;
            this.setterName = "set" + accessorName;
            this.valueClass = valueClass;
            this.verifier = createStyleValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
            this.defaultValue = defaultValue;
        }

        private Object getStyleValue(Object content) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(getterName, new Class<?>[]{});
                return convertType(m.invoke(content, new Object[]{}), valueClass);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setStyleDefaultValue(Object content) {
            if (content instanceof Region) {
                if (defaultValue != null)
                    setStyleValue(content, defaultValue);
            }
        }

        private void setStyleValue(Object content, Object value) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(setterName, new Class<?>[]{ valueClass });
                m.invoke(content, new Object[]{ value });
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

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (targetClass.isInstance(value))
                return value;
            else if (value.getClass() == String.class) {
                if (targetClass == Float.class) {
                    try {
                        return Float.valueOf((String) value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else
                    return null;
            } else if (value.getClass() == Float.class) {
                if (targetClass == String.class)
                    return ((Float) value).toString();
                else
                    return null;
            } else if (value instanceof Enum<?>) {
                if (targetClass == String.class)
                    return Enums.getValue((Enum<?>) value);
                else
                    return null;
            } else
                return null;
        }

    }

}
