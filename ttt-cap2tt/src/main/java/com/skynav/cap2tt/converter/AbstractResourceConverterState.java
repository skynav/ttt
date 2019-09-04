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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.cap2tt.converter.Attribute;
import com.skynav.cap2tt.converter.AttributeSpecification;
import com.skynav.cap2tt.converter.ResourceConverter;
import com.skynav.cap2tt.converter.Screen;

public abstract class AbstractResourceConverterState {

    protected ResourceConverter converter;    // converter
    protected String globalPlacement;         // global placement
    protected String placement;               // current screen placement
    protected String globalAlignment;         // global alignment
    protected String alignment;               // current screen alignment
    protected String globalShear;             // global italics
    protected String shear;                   // current screen shear
    protected String globalKerning;           // global kerning
    protected String kerning;                 // current screen kerning
    protected String globalTypeface;          // global typeface
    protected String typeface;                // current screen kerning
    protected String defaultRegion;           // default region
    protected String defaultWhitespace;       // default whitespace
    protected float[] shears;                 // shear map
    protected Set<QName> styles;              // styles

    protected AbstractResourceConverterState(ResourceConverter converter) {
        this.converter = converter;
        this.globalPlacement = converter.getOption("defaultPlacement");
        this.globalAlignment = converter.getOption("defaultAlignment");
        this.globalShear = converter.getOption("defaultShear");
        this.globalKerning = converter.getOption("defaultKerning");
        this.globalTypeface = converter.getOption("defaultTypeface");
        this.defaultRegion = converter.getOption("defaultRegion");
        this.defaultWhitespace = converter.getOption("defaultWhitespace");
        this.shears = (float[]) converter.getOptionObject("shears");
        this.styles = new java.util.HashSet<QName>();
    }

    public void process(List<Screen> screens) {
        for (Screen s: screens)
            process(s);
        finish();
    }

    private void finish() {
        process((Screen) null);
    }

    public abstract void process(Screen screen);

    protected void resetScreenAttributes() {
        placement = globalPlacement;
        shear = globalShear;
        kerning = globalKerning;
        typeface = globalTypeface;
    }

    protected void updateScreenAttributes(Screen s) {
        boolean global[] = new boolean[1];
        placement = getPlacement(s, global);
        if (global[0])
            globalPlacement = placement;
        alignment = getAlignment(s, global);
        if (global[0])
            globalAlignment = alignment;
        shear = getShear(s, global);
        if (global[0])
            globalShear = shear;
        kerning = getKerning(s, global);
        if (global[0])
            globalKerning = kerning;
        typeface = getTypeface(s, global);
        if (global[0])
            globalTypeface = typeface;
    }

    protected String getPlacement(Screen s, boolean[] retGlobal) {
        return s.getPlacement(retGlobal);
    }

    protected String getAlignment(Screen s, boolean[] retGlobal) {
        return s.getAlignment(retGlobal);
    }

    protected String getShear(Screen s, boolean[] retGlobal) {
        return s.getShear(retGlobal);
    }

    protected String getKerning(Screen s, boolean[] retGlobal) {
        return s.getKerning(retGlobal);
    }

    protected String getTypeface(Screen s, boolean[] retGlobal) {
        return s.getTypeface(retGlobal);
    }

    protected boolean isNonContinuation(Screen s) {
        if (s == null)                                                              // special 'final' screen, never treat as continuation
            return true;
        else if (!s.sameNumberAsLastScreen())                                       // a screen with a different number is considered a non-continuation
            return true;
        else if (s.hasInOutCodes())                                                 // a screen with time codes is considered a non-continuation
            return true;
        else if (isNewPlacement(s))                                                 // a screen with new placement is considered a non-continuation
            return true;
        else if (isNewAlignment(s))                                                 // a screen with new alignment is considered a non-continuation
            return true;
        else if (isNewShear(s))                                                     // a screen with new shear is considered a non-continuation
            return true;
        else if (isNewKerning(s))                                                   // a screen with new kerning is considered a non-continuation
            return true;
        else if (isNewTypeface(s))                                                  // a screen with new typeface is considered a non-continuation
            return true;
        else
            return false;
    }

    private boolean isNewPlacement(Screen s) {
        String newPlacement = s.getPlacement(null);
        if (newPlacement != null) {
            if ((placement != null) || !newPlacement.equals(placement))
                return true;                                                        // new placement is different from current placement
            else
                return false;                                                       // new placement is same as current placement, treat as continuation
        } else {
            return false;                                                           // new placement not specified, treat as continuation
        }
    }

    private boolean isNewAlignment(Screen s) {
        String newAlignment = s.getAlignment(null);
        if (newAlignment != null) {
            if ((alignment != null) || !newAlignment.equals(alignment))
                return true;                                                        // new alignment is different from current alignment
            else
                return false;                                                       // new alignment is same as current alignment, treat as continuation
        } else {
            return false;                                                           // new alignment not specified, treat as continuation
        }
    }

    private boolean isNewShear(Screen s) {
        String newShear = s.getShear(null);
        if (newShear != null) {
            if ((shear != null) || !newShear.equals(shear))
                return true;                                                        // new shear is different from current shear
            else
                return false;                                                       // new shear is same as current shear, treat as continuation
        } else {
            return false;                                                           // new shear not specified, treat as continuation
        }
    }

    private boolean isNewKerning(Screen s) {
        String newKerning = s.getKerning(null);
        if (newKerning != null) {
            if ((kerning != null) || !newKerning.equals(kerning))
                return true;                                                        // new kerning is different from current kerning
            else
                return false;                                                       // new kerning is same as current kerning, treat as continuation
        } else {
            return false;                                                           // new kerning not specified, treat as continuation
        }
    }

    private boolean isNewTypeface(Screen s) {
        String newTypeface = s.getTypeface(null);
        if (newTypeface != null) {
            if ((typeface != null) || !newTypeface.equals(typeface))
                return true;                                                        // new typeface is different from current typeface
            else
                return false;                                                       // new typeface is same as current typeface, treat as continuation
        } else {
            return false;                                                           // new typeface not specified, treat as continuation
        }
    }

    protected List<Attribute> mergeDefaults(List<Attribute> attributes) {
        boolean hasAlignment = false;
        boolean hasKerning = false;
        boolean hasPlacement = false;
        boolean hasShear = false;
        boolean hasTypeface = false;
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasAlignment())
                    hasAlignment = true;
                if (a.hasKerning())
                    hasKerning = true;
                if (a.hasPlacement())
                    hasPlacement = true;
                if (a.hasShear())
                    hasShear = true;
                if (a.hasTypeface())
                    hasTypeface = true;
            }
        }
        if (hasAlignment && hasKerning && hasPlacement && hasShear && hasTypeface)
            return attributes;
        Map<String, AttributeSpecification> knownAttributes = converter.getKnownAttributes();
        List<Attribute> mergedAttributes = attributes != null ? new java.util.ArrayList<Attribute>(attributes) : new java.util.ArrayList<Attribute>();
        if (!hasAlignment) {
            String v = alignment;
            if (v == null)
                v = globalAlignment;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        if (!hasKerning) {
            String v = kerning;
            if (v == null)
                v = globalKerning;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get("詰");
                if (as != null) {
                    int count;
                    try {
                        count = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        count = -1;
                    }
                    mergedAttributes.add(new Attribute(as, count, false));
                }
            }
        }
        if (!hasPlacement) {
            String v = placement;
            if (v == null)
                v = globalPlacement;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        if (!hasShear) {
            String v = shear;
            if (v == null)
                v = globalShear;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get("斜");
                if (as != null) {
                    int count;
                    try {
                        count = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        count = -1;
                    }
                    mergedAttributes.add(new Attribute(as, count, false));
                }
            }
        }
        if (!hasTypeface) {
            String v = typeface;
            if (v == null)
                v = globalTypeface;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        return mergedAttributes;
    }

    protected boolean isMixedAlignment(String alignment) {
        if (alignment == null)
            return false;
        else if (alignment.equals("中頭"))
            return true;
        else if (alignment.equals("中末"))
            return true;
        else
            return false;
    }
}

// Local Variables:
// coding: utf-8-unix
// End:
