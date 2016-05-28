/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.skynav.ttv.model.ttml.TTML;

public abstract class TTMLHelper extends ISDHelper {

    public static final String NAMESPACE_TT                     = TTML.Constants.NAMESPACE_TT;
    public static final String NAMESPACE_TT_METADATA            = TTML.Constants.NAMESPACE_TT_METADATA;
    public static final String NAMESPACE_TT_STYLE               = TTML.Constants.NAMESPACE_TT_STYLE;
    public static final String NAMESPACE_TT_PARAMETER           = TTML.Constants.NAMESPACE_TT_PARAMETER;

    public boolean hasUsableContent(Element elt) {
        if (isParagraphElement(elt))
            return hasUsableContentInParagraph(elt);
        else if (isImageElement(elt))
            return hasUsableContentInImage(elt);
        else
            return false;
    }

    public static boolean isRootElement(Element elt) {
        return isTimedTextElement(elt, "tt");
    }

    public static boolean isHeadElement(Element elt) {
        return isTimedTextElement(elt, "head");
    }

    public static boolean isLayoutElement(Element elt) {
        return isTimedTextElement(elt, "layout");
    }

    public static boolean isRegionElement(Element elt) {
        return isTimedTextElement(elt, "region");
    }

    public static boolean isAnonymousRegionElement(Element elt) {
        if (!isRegionElement(elt))
            return false;
        else {
            String id = getXmlIdentifier(elt);
            return (id != null) && (id.indexOf("isdRegion") == 0);
        }
    }

    public static boolean isOutOfLineRegionElement(Element elt) {
        return isRegionElement(elt) && isLayoutElement((Element) elt.getParentNode());
    }

    public static boolean isInitialElement(Element elt) {
        return isTimedTextElement(elt, "initial");
    }

    public static boolean isStyleElement(Element elt) {
        return isTimedTextElement(elt, "style");
    }

    public static boolean isRootStylingElement(Element elt) {
        /* TBD - MIGRATE TO ROOT in TTML2
           int version = getHelper(context).getVersion();
           if (version == 1)
           return TTMLHelper.isRegionElement(elt);
           else
           return TTMLHelper.isRootElement(elt);
        */
        return TTMLHelper.isRegionElement(elt);
    }

    public static boolean isBodyElement(Element elt) {
        return isTimedTextElement(elt, "body");
    }

    public static boolean isDivisionElement(Element elt) {
        return isTimedTextElement(elt, "div");
    }

    public static boolean isParagraphElement(Element elt) {
        return isTimedTextElement(elt, "p");
    }

    public static boolean isSpanElement(Element elt) {
        return isTimedTextElement(elt, "span");
    }

    public static boolean isAnonymousSpanElement(Element elt) {
        if (!isSpanElement(elt))
            return false;
        else {
            String id = getXmlIdentifier(elt);
            return (id != null) && (id.indexOf("isdSpan") == 0);
        }
    }

    public static boolean isImageElement(Element elt) {
        return isTimedTextElement(elt, "image");
    }

    public static boolean isAnimationElement(Element elt) {
        return isTimedTextElement(elt, "set");
    }

    public static boolean isContentElement(Element elt) {
        String nsUri = elt.getNamespaceURI();
        if ((nsUri == null) || !nsUri.equals(TTMLHelper.NAMESPACE_TT))
            return false;
        else {
            String localName = elt.getLocalName();
            if (localName.equals("body"))
                return true;
            else if (localName.equals("div"))
                return true;
            else if (localName.equals("p"))
                return true;
            else if (localName.equals("span"))
                return true;
            else if (localName.equals("br"))
                return true;
            else
                return false;
        }
    }

    public static boolean isRegionOrContentElement(Element elt) {
        return isRegionElement(elt) || isContentElement(elt);
    }

    public static boolean isTimedTextElement(Element elt, String localName) {
        if (elt != null) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri != null) && nsUri.equals(NAMESPACE_TT) && elt.getLocalName().equals(localName))
                return true;
        }
        return false;
    }

    public static boolean isTimedElement(Element elt) {
        String nsUri = elt.getNamespaceURI();
        if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
            return false;
        else {
            String localName = elt.getLocalName();
            if (localName.equals("animate"))
                return true;
            else if (localName.equals("body"))
                return true;
            else if (localName.equals("div"))
                return true;
            else if (localName.equals("p"))
                return true;
            else if (localName.equals("span"))
                return true;
            else if (localName.equals("br"))
                return true;
            else if (localName.equals("region"))
                return true;
            else if (localName.equals("set"))
                return true;
            else
                return false;
        }
    }

    public static String getRegionIdentifier(Element elt) {
        if (elt.hasAttributeNS(null, "region"))
            return elt.getAttributeNS(null, "region");
        else
            return null;
    }

    private static boolean hasUsableContentInParagraph(Element elt) {
        String content = elt.getTextContent();
        for (int i = 0, n = content.length(); i < n; ++i) {
            if (!Character.isWhitespace(content.charAt(i)))
                return true;
        }
        return false;
    }

    private static boolean hasUsableContentInImage(Element elt) {
        // [TBD] - return true only if image is resolvable
        return true;
    }

    public static boolean isStyleAttribute(QName attrName) {
        String nsUri = attrName.getNamespaceURI();
        return (nsUri != null) && nsUri.equals(NAMESPACE_TT_STYLE);
    }

}
