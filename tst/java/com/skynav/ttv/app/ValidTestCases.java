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
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {
    @Test
    public void testValidSimple() throws Exception {
        performValidityTest("ttml1-valid-simple.xml", 0, 0);
    }

    @Test
    public void testValidAllElements() throws Exception {
        performValidityTest("ttml1-valid-all-elements.xml", 0, 3);
    }

    @Test
    public void testValidAllMetadata() throws Exception {
        performValidityTest("ttml1-valid-all-metadata.xml", 0, 1);
    }

    @Test
    public void testValidAllParameters() throws Exception {
        performValidityTest("ttml1-valid-all-parameters.xml", 0, 0);
    }

    @Test
    public void testValidAllProfiles() throws Exception {
        performValidityTest("ttml1-valid-all-profiles.xml", 0, 3);
    }

    @Test
    public void testValidAllStyles() throws Exception {
        performValidityTest("ttml1-valid-all-styles.xml", 0, 0);
    }

    @Test
    public void testValidAllTiming() throws Exception {
        performValidityTest("ttml1-valid-all-timing.xml", 0, 0);
    }

    @Test
    public void testValidAgentIdrefDuplicate() throws Exception {
        performValidityTest("ttml1-valid-agent-idref-duplicate.xml", 0, 1);
    }

    @Test
    public void testValidAgentMissingActor() throws Exception {
        performValidityTest("ttml1-valid-agent-missing-actor.xml", 0, 1);
    }

    @Test
    public void testValidAgentMissingName() throws Exception {
        performValidityTest("ttml1-valid-agent-missing-name.xml", 0, 1);
    }

    @Test
    public void testValidExtensionNonStandard() throws Exception {
        performValidityTest("ttml1-valid-extension-non-standard.xml", 0, 2);
    }

    @Test
    public void testValidExtensionsBaseOtherNamespace() throws Exception {
        performValidityTest("ttml1-valid-extensions-base-other-namespace.xml", 0, 1);
    }

    @Test
    public void testValidFontFamilyQuotedGeneric() throws Exception {
        performValidityTest("ttml1-valid-font-family-quoted-generic.xml", 0, 1);
    }

    @Test
    public void testValidForeignMetadata() throws Exception {
        performValidityTest("ttml1-valid-foreign-metadata.xml", 0, 2);
    }

    @Test
    public void testValidForeign() throws Exception {
        performValidityTest("ttml1-valid-foreign.xml", 0, 4);
    }

    @Test
    public void testValidOpacityOutOfRange() throws Exception {
        performValidityTest("ttml1-valid-opacity-out-of-range.xml", 0, 5);
    }

    @Test
    public void testValidOriginNegative() throws Exception {
        performValidityTest("ttml1-valid-origin-negative.xml", 0, 4);
    }

    @Test
    public void testValidProfileAttributeIgnored() throws Exception {
        performValidityTest("ttml1-valid-profile-attribute-ignored.xml", 0, 1);
    }

    @Test
    public void testValidProfileAttributeNonStandard() throws Exception {
        performValidityTest("ttml1-valid-profile-attribute-non-standard.xml", 0, 1);
    }

    @Test
    public void testValidProfileMissing() throws Exception {
        performValidityTest("ttml1-valid-profile-missing.xml", 0, 1);
    }

    @Test
    public void testValidProfileNonStandard() throws Exception {
        performValidityTest("ttml1-valid-profile-non-standard.xml", 0, 1);
    }

    @Test
    public void testValidRoleDuplicate() throws Exception {
        performValidityTest("ttml1-valid-role-duplicate.xml", 0, 1);
    }

    @Test
    public void testValidRoleExtension() throws Exception {
        performValidityTest("ttml1-valid-role-extension.xml", 0, 1);
    }

    @Test
    public void testValidStyleIdrefDuplicateWithIntervening() throws Exception {
        performValidityTest("ttml1-valid-style-idref-duplicate-with-intervening.xml", 0, 1);
    }

    @Test
    public void testValidStyleIdrefDuplicateWithoutIntervening() throws Exception {
        performValidityTest("ttml1-valid-style-idref-duplicate-without-intervening.xml", 0, 2);
    }

    @Test
    public void testValidST20522010() throws Exception {
        performValidityTest("st2052-2010-valid.xml", -1, -1);
    }

    @Test
    public void testValidST20522010AllStyles() throws Exception {
        performValidityTest("st2052-2010-valid-all-styles.xml", -1, -1);
    }

    @Test
    public void testValidST20522010BackgroundImageExternal() throws Exception {
        performValidityTest("st2052-2010-valid-background-image-external.xml", -1, -1);
    }

    @Test
    public void testValidST20522013With608Extensions() throws Exception {
        performValidityTest("st2052-2013-valid-with-608.xml", -1, -1);
    }

    @Test
    public void testValidST20522013With708Extensions() throws Exception {
        performValidityTest("st2052-2013-valid-with-708.xml", -1, -1);
    }

    @Test
    public void testValidST20522013AllStyles() throws Exception {
        performValidityTest("st2052-2013-valid-all-styles.xml", -1, -1);
    }

    private void performValidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
        args.add("-q");
        args.add("-v");
        args.add("--warn-on");
        args.add("all");
        if (expectedErrors >= 0) {
            args.add("--expect-errors");
            args.add(Integer.toString(expectedErrors));
        }
        if (expectedWarnings >= 0) {
            args.add("--expect-warnings");
            args.add(Integer.toString(expectedWarnings));
        }
        args.add(urlString);
        TimedTextVerifier ttv = new TimedTextVerifier();
        ttv.run(args.toArray(new String[args.size()]));
        int resultCode = ttv.getResultCode(urlString);
        int resultFlags = ttv.getResultFlags(urlString);
        if (resultCode == TimedTextVerifier.RV_PASS) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected success with unexpected warning(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected success with expected warning(s) mismatch.");
            }
        } else if (resultCode == TimedTextVerifier.RV_FAIL) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected error(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected error(s) mismatch.");
            }
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}

