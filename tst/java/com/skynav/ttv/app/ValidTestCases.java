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
 
package com.skynav.ttv.app;

import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {
    @Test
    public void testValidSimple() throws Exception {
        performValidityTest("ttml10-valid-simple.xml");
    }

    @Test
    public void testValidAllElements() throws Exception {
        performValidityTest("ttml10-valid-all-elements.xml");
    }

    @Test
    public void testValidAllParameters() throws Exception {
        performValidityTest("ttml10-valid-all-parameters.xml");
    }

    @Test
    public void testValidAllProfiles() throws Exception {
        performValidityTest("ttml10-valid-all-profiles.xml");
    }

    @Test
    public void testValidAllStyles() throws Exception {
        performValidityTest("ttml10-valid-all-styles.xml");
    }

    @Test
    public void testValidAllTiming() throws Exception {
        performValidityTest("ttml10-valid-all-timing.xml");
    }

    @Test
    public void testValidExtensionsBaseOtherNamespace() throws Exception {
        performValidityTest("ttml10-valid-extensions-base-other-namespace.xml");
    }

    @Test
    public void testValidExtensionNonStandard() throws Exception {
        performValidityTest("ttml10-valid-extension-non-standard.xml");
    }

    @Test
    public void testValidFontFamilyQuotedGeneric() throws Exception {
        performValidityTest("ttml10-valid-font-family-quoted-generic.xml");
    }

    @Test
    public void testValidForeign() throws Exception {
        performValidityTest("ttml10-valid-foreign.xml");
    }

    @Test
    public void testValidForeignMetadata() throws Exception {
        performValidityTest("ttml10-valid-foreign-metadata.xml");
    }

    @Test
    public void testValidOpacityOutOfRange() throws Exception {
        performValidityTest("ttml10-valid-opacity-out-of-range.xml");
    }

    @Test
    public void testValidOriginNegative() throws Exception {
        performValidityTest("ttml10-valid-origin-negative.xml");
    }

    @Test
    public void testValidProfileAttributeIgnored() throws Exception {
        performValidityTest("ttml10-valid-profile-attribute-ignored.xml");
    }

    @Test
    public void testValidProfileAttributeNonStandard() throws Exception {
        performValidityTest("ttml10-valid-profile-attribute-non-standard.xml");
    }

    @Test
    public void testValidProfileMissing() throws Exception {
        performValidityTest("ttml10-valid-profile-missing.xml");
    }

    @Test
    public void testValidProfileNonStandard() throws Exception {
        performValidityTest("ttml10-valid-profile-non-standard.xml");
    }

    @Test
    public void testValidStyleIdrefDuplicateWithIntervening() throws Exception {
        performValidityTest("ttml10-valid-style-idref-duplicate-with-intervening.xml");
    }

    @Test
    public void testValidStyleIdrefDuplicateWithoutIntervening() throws Exception {
        performValidityTest("ttml10-valid-style-idref-duplicate-without-intervening.xml");
    }

    private void performValidityTest(String resourceName) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String[] args = { "-q", "-v", "--warn-on", "all", url.toString() };
        int rv = new TimedTextVerifier().run(args);
        if (rv == 1)
            fail("Unexpected validation failure.");
        else if (rv != 0)
            fail("Unexpected validation failure code: expected 0, got " + rv + ".");
    }
}

