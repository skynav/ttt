/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttpe.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.TextReporter;

public class PresenterTestCases {

    @Test
    public void test001Mixed() throws Exception {
        performPresentationTest("test-001-mixed.xml", 0, 0);
    }

    @Test
    public void test002SpanTextAlignEN() throws Exception {
        performPresentationTest("test-002-span-text-align-en.xml", 0, 0);
    }

    @Test
    public void test003SpanTextAlignJA() throws Exception {
        performPresentationTest("test-003-span-text-align-ja.xml", 0, 0);
    }

    @Test
    public void test004Ruby() throws Exception {
        performPresentationTest("test-004-ruby.xml", 0, 0);
    }

    @Test
    public void test005SpanFontVariantWidth() throws Exception {
        performPresentationTest("test-005-span-font-variant-width.xml", 0, 0);
    }

    @Test
    public void test006RegionDisplayAlignLRTB() throws Exception {
        performPresentationTest("test-006-region-display-align-lrtb.xml", 0, 0);
    }

    @Test
    public void test007RegionDisplayAlignTBLR() throws Exception {
        performPresentationTest("test-007-region-display-align-tblr.xml", 0, 0);
    }

    @Test
    public void test008RegionDisplayAlignTBRL() throws Exception {
        performPresentationTest("test-008-region-display-align-tbrl.xml", 0, 0);
    }

    @Test
    public void test009SpanFontKerningLRTB() throws Exception {
        performPresentationTest("test-009-span-font-kerning-lrtb.xml", 0, 0);
    }

    @Test
    public void test010SpanFontShearLRTB() throws Exception {
        performPresentationTest("test-010-span-font-shear-lrtb.xml", 0, 0);
    }

    @Test
    public void test011SpanFontSizeAnomorphicLRTB() throws Exception {
        performPresentationTest("test-011-span-font-size-anomorphic-lrtb.xml", 0, 0);
    }

    @Test
    public void test012SpanTextEmphasisArialLRTB() throws Exception {
        performPresentationTest("test-012-span-text-emphasis-arial-lrtb.xml", 0, 0);
    }

    @Test
    public void test013SpanTextAlignJustifyEN() throws Exception {
        performPresentationTest("test-013-span-text-align-justify-en.xml", 0, 0);
    }

    @Test
    public void test014RegionDisplayAlignJustifyLRTB() throws Exception {
        performPresentationTest("test-014-region-display-align-justify-lrtb.xml", 0, 0);
    }

    @Test
    public void test015RegionPositionInitialValue() throws Exception {
        performPresentationTest("test-015-region-position-initial-value.xml", 0, 0);
    }

    @Test
    public void test016SpanTextCombine() throws Exception {
        performPresentationTest("test-016-span-text-combine.xml", 0, 0);
    }

    @Test
    public void test017Break() throws Exception {
        performPresentationTest("test-017-break.xml", 0, 0);
    }

    @Test
    public void test018BreakFontShear() throws Exception {
        performPresentationTest("test-018-break-font-shear.xml", 0, 0);
    }

    @Test
    public void test019PlacementAndAlignmentDefault() throws Exception {
        performPresentationTest("test-019-placement-and-alignment-default.xml", 0, 0);
    }

    @Test
    public void test020RubyDisplayAlignLRTB() throws Exception {
        performPresentationTest("test-020-ruby-display-align-lrtb.xml", 0, 0);
    }

    @Test
    public void test021RubyDisplayAlignTBRL() throws Exception {
        performPresentationTest("test-021-ruby-display-align-tbrl.xml", 0, 0);
    }

    @Test
    public void test022RubyDisplayAlignTBLR() throws Exception {
        performPresentationTest("test-022-ruby-display-align-tblr.xml", 0, 0);
    }

    @Test
    public void test023SpanFontShearTBRL() throws Exception {
        performPresentationTest("test-023-span-font-shear-tbrl.xml", 0, 0);
    }

    @Test
    public void test024RubyMultiLineLRTB() throws Exception {
        performPresentationTest("test-024-ruby-multi-line-lrtb.xml", 0, 0);
    }

    @Test
    public void test025RubyMultiLineTBRL() throws Exception {
        performPresentationTest("test-025-ruby-multi-line-tbrl.xml", 0, 0);
    }

    @Test
    public void test026RubyMultiLineTBLR() throws Exception {
        performPresentationTest("test-026-ruby-multi-line-tblr.xml", 0, 0);
    }

    @Test
    public void test027InlineBlockRubyMultiLineLRTB() throws Exception {
        performPresentationTest("test-027-inline-block-ruby-multi-line-lrtb.xml", 0, 0);
    }

    @Test
    public void test028InlineBlockRubyMultiLineTBRL() throws Exception {
        performPresentationTest("test-028-inline-block-ruby-multi-line-tbrl.xml", 0, 0);
    }

    @Test
    public void test029InlineBlockRubyMultiLineTBLR() throws Exception {
        performPresentationTest("test-029-inline-block-ruby-multi-line-tblr.xml", 0, 0);
    }

    @Test
    public void test030SpanTextEmphasisMixed() throws Exception {
        performPresentationTest("test-030-span-text-emphasis-mixed.xml", 0, 0);
    }

    @Test
    public void test031SpanTextEmphasisNotoLRTB() throws Exception {
        performPresentationTest("test-031-span-text-emphasis-noto-lrtb.xml", 0, 0);
    }

    @Test
    public void test032SpanTextEmphasisNotoBeforeMixedOrientation() throws Exception {
        performPresentationTest("test-032-span-text-emphasis-noto-before-mixed-orientation.xml", 0, 0);
    }

    @Test
    public void test033SpanTextEmphasisNotoAfterMixedOrientation() throws Exception {
        performPresentationTest("test-033-span-text-emphasis-noto-after-mixed-orientation.xml", 0, 0);
    }

    @Test
    public void test034SpanTextEmphasisSegmented() throws Exception {
        performPresentationTest("test-034-span-text-emphasis-segmented.xml", 0, 0);
    }

    @Test
    public void test035BreakBetweenRubyContainers() throws Exception {
        performPresentationTest("test-035-break-between-ruby-containers.xml", 0, 0);
    }

    @Test
    public void test036SpanTextEmphasisAutoPosition() throws Exception {
        performPresentationTest("test-036-span-text-emphasis-auto-position.xml", 0, 0);
    }

    @Test
    public void test037ParagraphMultiplePerRegionTBRL() throws Exception {
        performPresentationTest("test-037-p-multiple-per-region-tbrl.xml", 0, 0);
    }

    @Test
    public void test038ParagraphTextAlignHebrewRLTB() throws Exception {
        performPresentationTest("test-038-p-text-align-iw-rltb.xml", 0, 0);
    }

    @Test
    public void test039SpanBidiLROInRLTB() throws Exception {
        performPresentationTest("test-039-span-bidi-rlo-rltb.xml", 0, 0);
    }

    @Test
    public void test040MaxRegionsExceeded() throws Exception {
        performPresentationTest("test-040-max-regions-exceeded.xml", 0, 1, new String[]{"--max-regions", "3"});
    }

    @Test
    public void test041MaxLinesExceeded() throws Exception {
        performPresentationTest("test-041-max-lines-exceeded.xml", 0, 1, new String[]{"--max-lines", "5"});
    }

    @Test
    public void test042MaxLinesPerRegionExceeded() throws Exception {
        performPresentationTest("test-042-max-lines-per-region-exceeded.xml", 0, 1, new String[]{"--max-lines-per-region", "1"});
    }

    @Test
    public void test043MaxCharsExceeded() throws Exception {
        performPresentationTest("test-043-max-chars-exceeded.xml", 0, 1, new String[]{"--max-chars", "11"});
    }

    @Test
    public void test044MaxCharsPerRegionExceeded() throws Exception {
        performPresentationTest("test-044-max-chars-per-region-exceeded.xml", 0, 1, new String[]{"--max-chars-per-region", "5"});
    }

    @Test
    public void test045MaxCharsPerRegionExceeded() throws Exception {
        performPresentationTest("test-045-max-chars-per-line-exceeded.xml", 0, 1, new String[]{"--max-chars-per-line", "3"});
    }

    @Test
    public void test046OptionDefaultColorRed() throws Exception {
        performPresentationTest("test-046-option-default-color-red.xml", 0, 0, new String[]{"--default-color", "red"});
    }

    @Test
    public void test047VerticalVariants() throws Exception {
        performPresentationTest("test-047-vertical-variants.xml", 0, 0);
    }

    @Test
    public void test048SpanTextOutline() throws Exception {
        performPresentationTest("test-048-span-text-outline.xml", 0, 0);
    }

    @Test
    public void test049RubyAlignAutoLRTB() throws Exception {
        performPresentationTest("test-049-ruby-align-auto-lrtb.xml", 0, 0);
    }

    @Test
    public void test050RubyAlignAutoTBRL() throws Exception {
        performPresentationTest("test-050-ruby-align-auto-tbrl.xml", 0, 0);
    }

    @Test
    public void test051TextCombineMixed() throws Exception {
        performPresentationTest("test-051-text-combine-mixed.xml", 0, 0);
    }

    @Test
    public void test052TextOutlineOnRubyMixed() throws Exception {
        performPresentationTest("test-052-text-outline-on-ruby-mixed.xml", 0, 0);
    }

    @Test
    public void test053TextOutlineOnEmphasisMixed() throws Exception {
        performPresentationTest("test-053-text-outline-on-emphasis-mixed.xml", 0, 0);
    }

    @Test
    public void test054ShearAdvanceTBRL() throws Exception {
        performPresentationTest("test-054-shear-advance-tbrl.xml", 0, 0);
    }

    @Test
    public void test055RubyOnMixedOrientations() throws Exception {
        performPresentationTest("test-055-ruby-on-mixed-orientations.xml", 0, 0);
    }

    @Test
    public void test056ParagraphTextAlignArabicRLTB() throws Exception {
        performPresentationTest("test-056-p-text-align-ar-rltb.xml", 0, 0);
    }

    @Test
    public void test057XMLSpace() throws Exception {
        performPresentationTest("test-057-xml-space.xml", 0, 0);
    }

    @Test
    public void test058OptionDefaultWhitespacePreserve() throws Exception {
        performPresentationTest("test-058-option-default-whitespace-preserve.xml", 0, 0, new String[]{"--default-whitespace", "preserve"});
    }

    @Test
    public void test05930A1RotationIssue118() throws Exception {
        performPresentationTest("test-059-30a1-rotation-issue-118.xml", 0, 0);
    }

    @Test
    public void test060ExpandedFontWidthInVerticalIssue119() throws Exception {
        performPresentationTest("test-060-expanded-font-width-in-vertical-issue-119.xml", 0, 0);
    }

    @Test
    public void test061VerticalMappedRotations() throws Exception {
        performPresentationTest("test-061-vertical-mapped-rotations.xml", 0, 0);
    }

    @Test
    public void test062TextCombineWithShearIssue125() throws Exception {
        performPresentationTest("test-062-text-combine-with-shear-issue-125.xml", 0, 0);
    }

    @Test
    public void test063AnnotationBreakingIssue124Ruby33() throws Exception {
        performPresentationTest("test-063-annotation-breaking-issue-124-ruby33.xml", 0, 0);
    }

    @Test
    public void test064AnnotationBreakingIssue124Ruby34() throws Exception {
        performPresentationTest("test-064-annotation-breaking-issue-124-ruby34.xml", 0, 0);
    }

    @Test
    public void test065AmbiguousGlyphToCharMappingIssue128() throws Exception {
        performPresentationTest("test-065-ambiguous-glyph-to-char-mapping-issue-128.xml", 0, 0);
    }

    @Test
    public void test066AnnotationOverflowsLineAreaIssue120Ruby29() throws Exception {
        performPresentationTest("test-066-annotation-overflows-line-area-issue-120-ruby29.xml", 0, 0);
    }

    @Test
    public void test067RubyReserveIssue97Ruby13() throws Exception {
        performPresentationTest("test-067-ruby-reserve-issue-97-ruby13.xml", 0, 0);
    }

    @Test
    public void test068RubyReserveIssue97Ruby16() throws Exception {
        performPresentationTest("test-068-ruby-reserve-issue-97-ruby16.xml", 0, 0);
    }

    @Test
    public void test069BidiReorderWithIsolates() throws Exception {
        performPresentationTest("test-069-bidi-reorder-with-isolates.xml", 0, 0);
    }

    @Test
    public void test070KoranicArabic() throws Exception {
        performPresentationTest("test-070-koranic-arabic.xml", 0, 0);
    }

    @Test
    public void test071BidiMirror() throws Exception {
        performPresentationTest("test-071-bidi-mirror.xml", 0, 0);
    }

    @Test
    public void test072RubyReserveIssue147() throws Exception {
        performPresentationTest("test-072-ruby-reserve-issue-147.xml", 0, 0);
    }

    @Test
    public void test073RubyReserveOutside3Lines() throws Exception {
        performPresentationTest("test-073-ruby-reserve-outside-3-lines.xml", 0, 0);
    }

    @Test
    public void test074RubyReserveAround3Lines() throws Exception {
        performPresentationTest("test-074-ruby-reserve-around-3-lines.xml", 0, 0);
    }

    @Test
    public void test075RubyReserveBetween3Lines() throws Exception {
        performPresentationTest("test-075-ruby-reserve-between-3-lines.xml", 0, 0);
    }

    @Test
    public void test076SpanTextAlignKO() throws Exception {
        performPresentationTest("test-076-span-text-align-ko.xml", 0, 0);
    }

    @Test
    public void test077SpanTextAlignZHS() throws Exception {
        performPresentationTest("test-077-span-text-align-zhs.xml", 0, 0);
    }

    @Test
    public void test078SpanTextAlignZHT() throws Exception {
        performPresentationTest("test-078-span-text-align-zht.xml", 0, 0);
    }

    @Test
    public void test079SpanTextAlignRU() throws Exception {
        performPresentationTest("test-079-span-text-align-ru.xml", 0, 0);
    }

    @Test
    public void test080OffsetTimeMetricTicks() throws Exception {
        performPresentationTest("test-080-offset-time-metric-ticks.xml", 0, 0);
    }

    @Test
    public void test081SMPTE30FPSDropNTSC() throws Exception {
        performPresentationTest("test-081-smpte-30fps-drop-ntsc.xml", 0, 0);
    }

    @Test
    public void test082SMPTE30FPSDropPAL() throws Exception {
        performPresentationTest("test-082-smpte-30fps-drop-pal.xml", 0, 0);
    }

    @Test
    public void test083SMPTE30FPSNonDrop() throws Exception {
        performPresentationTest("test-083-smpte-30fps-non-drop.xml", 0, 0);
    }

    private void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performPresentationTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    private void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        URI input;
        try {
            input = new URI(urlString);
        } catch (URISyntaxException e) {
            fail("Bad test resource syntax: " + urlString + ".");
            return;
        }
        List<String> args = new java.util.ArrayList<String>();
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
        maybeAddConfiguration(args, input);
        maybeAddFontDirectory(args, input);
        maybeAddOutputDirectory(args);
        if (additionalOptions != null) {
            args.addAll(java.util.Arrays.asList(additionalOptions));
        }
        args.add(urlString);
        Presenter ttpe = new Presenter();
        URI output = ttpe.present(args, new TextReporter());
        maybeCheckDifferences(output, input);
        TimedTextVerifier.Results r = ttpe.getResults(urlString);
        int resultCode = r.getCode();
        int resultFlags = r.getFlags();
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

    private void maybeAddConfiguration(List<String> args, URI input) {
        String[] components = getComponents(input);
        if (hasFileScheme(components)) {
            File f = getConfiguration(components);
            if (f != null) {
                try {
                    String p = f.getCanonicalPath();
                    args.add("--config");
                    args.add(p);
                } catch (IOException e) {
                }
            }
        }
    }

    private File getConfiguration(String[] components) {
        File f1 = new File(joinComponents(components, ".config.xml"));
        if (f1.exists())
            return f1;
        File f2 = new File(joinComponents(components, "test", ".config.xml"));
        if (f2.exists())
            return f2;
        return null;
    }

    private void maybeAddFontDirectory(List<String> args, URI input) {
        String[] components = getComponents(input);
        if (hasFileScheme(components)) {
            File f = getFontDirectory(components);
            if (f != null) {
                try {
                    String p = f.getCanonicalPath();
                    args.add("--font-directory");
                    args.add(p);
                } catch (IOException e) {
                }
            }
        }
    }

    private File getFontDirectory(String[] components) {
        File f1 = new File(components[1], "fonts");
        if (f1.exists())
            return f1;
        File f2 = new File(".", "src/test/fonts");
        if (f2.exists())
            return f2;
        File f3 = new File(".", "fonts");
        if (f3.exists())
            return f3;
        return null;
    }

    private void maybeAddOutputDirectory(List<String> args) {
        String reportsDirectory = System.getProperty("surefire.reportsDirectory");
        if (reportsDirectory != null) {
            args.add("--output-directory");
            args.add(reportsDirectory);
        }
    }

    private void maybeCheckDifferences(URI output, URI input) {
        String[] components = getComponents(input);
        if (hasFileScheme(components)) {
            File control = new File(joinComponents(components, ".expected.zip"));
            if (control.exists()) {
                checkDifferences(control.toURI(), output);
            }
        }
    }

    private void checkDifferences(URI uri1, URI uri2) {
        assert hasFileScheme(uri1);
        assert hasFileScheme(uri2);
        BufferedInputStream bis1 = null;
        BufferedInputStream bis2 = null;
        ZipInputStream zis1 = null;
        ZipInputStream zis2 = null;
        try {
            File[] retFile1 = new File[1];
            File[] retFile2 = new File[2];
            if ((bis1 = getArchiveInputStream(uri1, retFile1)) != null) {
                if ((bis2 = getArchiveInputStream(uri2, retFile2)) != null) {
                    File f1 = retFile1[0];
                    File f2 = retFile2[0];
                    zis1 = new ZipInputStream(bis1);
                    zis2 = new ZipInputStream(bis2);
                    checkDifferences(f1, zis1, f2, zis2);
                }
            }
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            IOUtil.closeSafely(zis2);
            IOUtil.closeSafely(bis2);
            IOUtil.closeSafely(zis1);
            IOUtil.closeSafely(bis1);
        }
    }

    private void checkDifferences(File f1, ZipInputStream zi1, File f2, ZipInputStream zi2) throws IOException {
        boolean done = false;
        while (!done) {
            ZipEntry e1 = zi1.getNextEntry();
            ZipEntry e2 = zi2.getNextEntry();
            if (e1 != null) {
                if (e2 != null) {
                    checkDifferences(f1, zi1, e1, f2, zi2, e2);
                } else {
                    fail("Archive entry count mismatch, extra entry in " + f1 + ".");
                    done = true;
                }
            } else if (e2 != null) {
                fail("Archive entry count mismatch, extra entry in " + f2 + ".");
                done = true;
            } else {
                done = true;
            }
        }
    }

    private void checkDifferences(File f1, InputStream is1, ZipEntry e1, File f2, InputStream is2, ZipEntry e2) {
        String n1 = e1.getName();
        String n2 = e2.getName();
        assertEquals("Archive entry name mismatch.", n1, n2);
        InputStream eis1 = null;
        InputStream eis2 = null;
        try {
            eis1 = readEntryData(e1, is1);
            eis2 = readEntryData(e2, is2);
            checkDifferences(n1, eis1, n2, eis2);
        } catch (IOException e) {
            String m = "";
            if (eis1 == null)
                m = "Can't read data for entry '" + n1 + "': ";
            else if (eis2 == null)
                m = "Can't read data for entry '" + n1 + "': ";
            fail(m + e.getMessage() + ".");
        } finally {
            IOUtil.closeSafely(eis1);
            IOUtil.closeSafely(eis2);
        }
    }

    private InputStream readEntryData(ZipEntry e, InputStream is) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            long limit = e.getSize();
            assertTrue("Entry data size exceeds 4GB limit.", limit <= (long) Integer.MAX_VALUE);
            long consumed = 0;
            byte[] buffer = new byte[4096];
            bos = new ByteArrayOutputStream(limit > 0 ? (int) limit : buffer.length);
            for (int nb; (nb = is.read(buffer)) >= 0;) {
                if (nb > 0) {
                    bos.write(buffer, 0, nb);
                    consumed += nb;
                } else
                    Thread.yield();
            }
            assertTrue("Unable to read all entry data, got " + consumed + " bytes, expected " + limit + " bytes.", (limit < 0) || (consumed == limit));
            return new ByteArrayInputStream(bos.toByteArray());
        } finally {
            IOUtil.closeSafely(bos);
        }
    }

    private void checkDifferences(String n1, InputStream is1, String n2, InputStream is2) {
        assert n1.equals(n2);
        if (n1.endsWith(".xml"))
            checkDifferencesXML(n1, is1, n2, is2);
        else if (n1.endsWith(".svg"))
            checkDifferencesXML(n1, is1, n2, is2);
        else if (n2.endsWith(".png"))
            checkDifferencesPNG(n1, is1, n2, is2);
        else
            checkDifferencesOther(n1, is1, n2, is2);
    }

    private void checkDifferencesXML(String n1, InputStream is1, String n2, InputStream is2) {
        Diff diff = DiffBuilder
            .compare(Input.fromStream(is1).build())
            .withTest(Input.fromStream(is2).build())
            .ignoreWhitespace()
            .build();
        assertFalse(diff.toString(), diff.hasDifferences());
    }

    private void checkDifferencesPNG(String n1, InputStream is1, String n2, InputStream is2) {
    }

    private void checkDifferencesOther(String n1, InputStream is1, String n2, InputStream is2) {
    }

    private BufferedInputStream getArchiveInputStream(URI uri, File[] retFile) throws IOException {
        File f = new File(uri.getPath());
        if (retFile != null)
            retFile[0] = f;
        return new BufferedInputStream(new FileInputStream(f));
    }

    private String[] getComponents(URI uri) {
        String s = uri.getScheme();
        String p = uri.getPath();
        String n, x;
        int i = p.lastIndexOf('/');
        if (i >= 0) {
            n = p.substring(i + 1);
            p = p.substring(0, i + 1);
        } else
            n = null;
        int j = n.lastIndexOf('.');
        if (j >= 0) {
            x = n.substring(j);
            n = n.substring(0, j);
        } else {
            x = null;
        }

        return new String[] { s, p, n, x };
    }

    private boolean hasFileScheme(URI uri) {
        return hasFileScheme(getComponents(uri));
    }

    private boolean hasFileScheme(String[] components) {
        return (components != null) && (components[0] != null) && components[0].equals("file");
    }

    private String joinComponents(String[] components, String extension) {
        assert components != null;
        return joinComponents(components, components[2], extension);
    }

    private String joinComponents(String[] components, String name, String extension) {
        assert components != null;
        assert components[1] != null;
        assert name != null;
        assert extension != null;
        StringBuffer sb = new StringBuffer();
        sb.append(components[1]);
        sb.append('/');
        sb.append(name);
        sb.append(extension);
        return sb.toString();
    }

}
