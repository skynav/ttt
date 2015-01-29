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
 
package com.skynav.ttpe.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;

import com.skynav.ttv.util.IOUtil;

public class FontLoader {

    private FontLoader() {
    }

    public static List<FontSpecification> load(List<File> fontSpecificationFiles) {
        List<FontSpecification> fontSpecifications = new java.util.ArrayList<FontSpecification>();
        for (File f : fontSpecificationFiles) {
            fontSpecifications.addAll(load(f));
        }
        return fontSpecifications;
    }

    private static List<FontSpecification> noFontSpecifications = new java.util.ArrayList<FontSpecification>();

    private static List<FontSpecification> load(File f) {
        try {
            return fromFile(f);
        } catch (IOException e) {
            return noFontSpecifications;
        }
    }

    public static List<FontSpecification> fromFile(File f) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            return fromStream(is, f.getParentFile().getAbsolutePath()); 
        } catch (IOException e) {
            IOUtil.closeSafely(is);
            throw e;
        }
    }

    public static List<FontSpecification> fromStream(InputStream is, String sourceBase) throws IOException {
        try {
            SAXSource source = new SAXSource(new InputSource(is));
            DOMResult result = new DOMResult();
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            Document d = (Document) result.getNode();
            return FontSpecification.fromDocument(d, sourceBase);
        } catch (TransformerFactoryConfigurationError e) {
            return noFontSpecifications;
        } catch (TransformerException e) {
            return noFontSpecifications;
        }
    }

}




