/*
 * Copyright 2014-16 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import java.awt.Paint;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public class Color {

    public static final Color AQUA                              = new Color(0,1,1);
    public static final Color BLACK                             = new Color(0,0,0);
    public static final Color BLUE                              = new Color(0,0,1);
    public static final Color CYAN                              = new Color(0,1,1);
    public static final Color FUCHSIA                           = new Color(1,0,1);
    public static final Color GRAY                              = new Color(0.5,0.5,0.5);
    public static final Color GREEN                             = new Color(0,0.5,0);
    public static final Color LIME                              = new Color(0,1,0);
    public static final Color MAGENTA                           = new Color(1,0,1);
    public static final Color MAROON                            = new Color(0.5,0,0);
    public static final Color NAVY                              = new Color(0,0,0.5);
    public static final Color OLIVE                             = new Color(0.5,0.5,0);
    public static final Color PURPLE                            = new Color(0.5,0,0.5);
    public static final Color RED                               = new Color(1,0,0);
    public static final Color SILVER                            = new Color(0.75,0.75,0.75);
    public static final Color TEAL                              = new Color(0,0.5,0.5);
    public static final Color TRANSPARENT                       = new Color(0,0,0,0);
    public static final Color WHITE                             = new Color(1,1,1);
    public static final Color YELLOW                            = new Color(1,1,0);

    public static final String rgbFormat                        = "#%02X%02X%02X";
    public static final String rgbaFormat                       = "#%02X%02X%02X%02X";
    public static final MessageFormat tupleFormatter            =
        new MessageFormat("[{0,number,0.0},{1,number,0.0},{2,number,0.0},{3,number,0.0}]", Locale.US);

    private double red;
    private double green;
    private double blue;
    private double alpha;                                       // 0 = transparent, 1 = opaque

    public Color(double red, double green, double blue) {
        this(red, green, blue, 1);
    }

    public Color(Color color) {
        this(color.red, color.green, color.blue, color.alpha);
    }

    public Color(double red, double green, double blue, double alpha) {
        assert red >= 0;
        assert red <= 1;
        this.red = red;
        assert green >= 0;
        assert green <= 1;
        this.green = green;
        assert blue >= 0;
        assert blue <= 1;
        this.blue = blue;
        assert alpha >= 0;
        assert alpha <= 1;
        this.alpha = alpha;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getHue() {
        double a = (2 * red + green + blue) * 0.5000;
        double b = (green - blue) * 0.8660;
        return Math.toDegrees(Math.atan2(a, b));
    }

    public double getLuminance() {
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    public double getSaturation() {
        return (max() - min())/max();
    }

    public boolean isTransparent() {
        return alpha == 0;
    }

    private double max() {
        double m1 = Math.max(red, green);
        double m2 = Math.max(green, blue);
        return Math.max(m1, m2);
    }

    private double min() {
        double m1 = Math.min(red, green);
        double m2 = Math.min(green, blue);
        return Math.min(m1, m2);
    }

    public Color contrast() {
        if (getSaturation() < 0.4)
            return (getLuminance() > 0.5) ? BLACK : WHITE;
        else {
            double h = getHue();
            if ((h >= 30) && (h < 90))
                return BLUE;
            else if ((h >= 90) && (h < 150))
                return MAGENTA;
            else if ((h >= 150) && (h < 210))
                return RED;
            else if ((h >= 210) && (h < 270))
                return YELLOW;
            else if ((h >= 270) && (h < 330))
                return LIME;
            else
                return CYAN;
        }
    }

    public Paint getPaint() {
        return new java.awt.Color((float) red, (float) green, (float) blue, (float) alpha);
    }

    public String toRGBString() {
        return String.format(rgbFormat, (int) (red * 255), (int) (green * 255), (int) (blue * 255));
    }

    public String toRGBAString(boolean abbreviate) {
        if (abbreviate && (alpha == 0))
            return String.format(rgbFormat, (int) (red * 255), (int) (green * 255), (int) (blue * 255));
        else
            return String.format(rgbaFormat, (int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + Double.valueOf(red).hashCode();
        hc = hc * 31 + Double.valueOf(green).hashCode();
        hc = hc * 31 + Double.valueOf(blue).hashCode();
        hc = hc * 31 + Double.valueOf(alpha).hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Color) {
            Color other = (Color) o;
            if (other.red != red)
                return false;
            else if (other.green != green)
                return false;
            else if (other.blue != blue)
                return false;
            else if (other.alpha != alpha)
                return false;
            else
                return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        return tupleFormatter.format(new Object[]{red, green, blue, alpha});
    }

    private static Map<String,Color> namedColors;
    private static final Object[][] namedColorSpecifications = new Object[][] {
        { "aqua",                       AQUA            },
        { "black",                      BLACK           },
        { "blue",                       BLUE            },
        { "cyan",                       CYAN            },
        { "fuchsia",                    FUCHSIA         },
        { "gray",                       GRAY            },
        { "green",                      GREEN           },
        { "lime",                       LIME            },
        { "magenta",                    MAGENTA         },
        { "maroon",                     MAROON          },
        { "navy",                       NAVY            },
        { "olive",                      OLIVE           },
        { "purple",                     PURPLE          },
        { "red",                        RED             },
        { "silver",                     SILVER          },
        { "teal",                       TEAL            },
        { "transparent",                TRANSPARENT     },
        { "white",                      WHITE           },
        { "yellow",                     YELLOW          },
    };
    static {
        namedColors = new java.util.HashMap<String,Color>();
        for (Object[] spec : namedColorSpecifications) {
            namedColors.put((String) spec[0], (Color) spec[1]);
        }
    }

    public static Color fromName(String name) {
        Color c = namedColors.get(name.toLowerCase());
        if (c != null)
            return c;
        else
            throw new IllegalArgumentException();
    }
}
