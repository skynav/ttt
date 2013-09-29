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
 
package com.skynav.ttv.verifier.smpte;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.smpte.ST20522013;
import com.skynav.ttv.model.smpte.tt.rel2013.Image;
import com.skynav.ttv.model.smpte.tt.rel2013.Information;
import com.skynav.ttv.model.smpte.tt.rel2013.Data;

import static com.skynav.ttv.model.smpte.ST20522013.Constants.*;

public class ST20522013SemanticsVerifier extends ST20522010SemanticsVerifier {

    public ST20522013SemanticsVerifier(Model model) {
        super(model);
    }

    public boolean inSMPTEPrimaryNamespace(QName name) {
        return ST20522013.inSMPTEPrimaryNamespace(name);
    }

    public boolean inSMPTESecondaryNamespace(QName name) {
        return ST20522013.inSMPTESecondaryNamespace(name);
    }

    private static final QName imageElementName = new QName(NAMESPACE_2013, ELT_IMAGE);
    protected QName getImageElementName() {
        return imageElementName;
    }

    protected boolean isDataElement(Object content) {
        return content instanceof Data;
    }

    protected String getDataDatatype(Object data) {
        assert isDataElement(data);
        return ((Data) data).getDatatype();
    }

    protected boolean isStandardDataType(String dataType) {
        if (dataType.equals(DATA_TYPE_608))
            return true;
        else if (dataType.equals(DATA_TYPE_708))
            return true;
        else
            return false;
    }

    protected String getDataValue(Object data) {
        assert isDataElement(data);
        return ((Data) data).getValue();
    }

    protected boolean isImageElement(Object content) {
        return content instanceof Image;
    }

    protected String getImageValue(Object image) {
        assert isImageElement(image);
        return ((Image) image).getValue();
    }

    protected boolean isInformationElement(Object content) {
        return content instanceof Information;
    }

}

