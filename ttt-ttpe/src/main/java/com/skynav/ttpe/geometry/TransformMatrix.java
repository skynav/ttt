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

package com.skynav.ttpe.geometry;

import java.awt.geom.AffineTransform;
import java.text.MessageFormat;

public class TransformMatrix extends AffineTransform {
    
    private static final long serialVersionUID = -7806424349785688446L;

    public static final TransformMatrix IDENTITY = new TransformMatrix();

    private static final MessageFormat transformMatrixFormatter =
        new MessageFormat("{0,number,#.####},{1,number,#.####},{2,number,#.####},{3,number,#.####},{4,number,#.####},{5,number,#.####}");

    public TransformMatrix() {
    }

    public TransformMatrix(TransformMatrix m) {
        super(m);
    }

    @Override
    public String toString() {
        double[] m = new double[6];
        getMatrix(m);
        return transformMatrixFormatter.format(new Object[] {m[0], m[1], m[2], m[3], m[4], m[5]});
    }
}
