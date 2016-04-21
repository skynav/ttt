/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 * Portions Copyright 2009 Extensible Formatting Systems, Inc (XFSI).
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

package com.xfsi.xav.util;

// import java.util.logging.Level;

public class Error {

    public static final class Category {
        public static final int OTHER_CATEGORY                  = 0;
        public static final int VALIDITY_CATEGORY               = 1;
        public static final int INTEROPERABILITY_CATEGORY       = 2;
        public static final int SECURITY_CATEGORY               = 3;
        public static final int EFFICIENCY_CATEGORY             = 4;
        public static final int INTERNAL_CATEGORY               = 5;
        public static final int COVERAGE_CATEGORY               = 6;

        public static final Category VALIDITY                   = new Category(VALIDITY_CATEGORY);
        public static final Category INTEROPERABILITY           = new Category(INTEROPERABILITY_CATEGORY);
        public static final Category SECURITY                   = new Category(SECURITY_CATEGORY);
        public static final Category EFFICIENCY                 = new Category(EFFICIENCY_CATEGORY);
        public static final Category OTHER                      = new Category(OTHER_CATEGORY);
        public static final Category INTERNAL                   = new Category(INTERNAL_CATEGORY);
        public static final Category COVERAGE                   = new Category(COVERAGE_CATEGORY);

        private int type;

        private Category(int type) {
            this.type = type;
        }

        public int getCategory() { return type; }

        public String toString() {
            switch (type) {
            case VALIDITY_CATEGORY:
                return "validity";
            case INTEROPERABILITY_CATEGORY:
                return "interoperability";
            case SECURITY_CATEGORY:
                return "security";
            case EFFICIENCY_CATEGORY:
                return "efficiency";
            case OTHER_CATEGORY:
                return "other";
            case INTERNAL_CATEGORY:
                return "internal";
            case COVERAGE_CATEGORY:
                return "coverage";
            default:
                return "unknown";
            }
        }
    }

    public static final class ContentType {
        public static final int UNSPECIFIED_TYPE                = 0;
        public static final int OTHER_TYPE                      = 1;
        public static final int PACKAGE_TYPE                    = 2;
        public static final int APPLICATION_TYPE                = 3;
        public static final int JAVA_TYPE                       = 4;
        public static final int AUDIO_AC3_TYPE                  = 5;
        public static final int AUDIO_MP2_TYPE                  = 6;
        public static final int AUDIO_MP3_TYPE                  = 7;
        public static final int IMAGE_PNG_TYPE                  = 8;
        public static final int IMAGE_JPG_TYPE                  = 9;
        public static final int IMAGE_MPG_TYPE                  = 10;
        public static final int VIDEO_DRIP_TYPE                 = 11;
        public static final int VIDEO_MPG_TYPE                  = 12;
        public static final int TEXT_TYPE                       = 13;
        public static final int FONT_TYPE                       = 14;
        public static final int DIGEST_TYPE                     = 15;
        public static final int SIGNATURE_TYPE                  = 16;
        public static final int CERTIFICATE_TYPE                = 17;
        public static final int PERMISSION_TYPE                 = 18;
        public static final int ZIP_TYPE                        = 19;
        public static final int GZIP_TYPE                       = 20;
        public static final int FONT_INDEX_TYPE                 = 21;
        public static final int PACKAGE_MANIFEST_TYPE           = 22;
        public static final int HASHFILE_TYPE                   = 23;
        public static final int TEXT_XML_TYPE                   = 24;
        public static final int CRL_TYPE                        = 25;
        public static final int DESCRIPTION_TYPE                = 26;
        public static final int TEXT_PROPERTIES_TYPE            = 27;
        public static final int EBIF_TYPE                       = 28;
        public static final int SAVR_TYPE                       = 29;
        public static final int TESTLET_CONF_TYPE               = 30;
        public static final int CODF_TYPE                       = 31;

        public static final ContentType UNSPECIFIED             = new ContentType(UNSPECIFIED_TYPE);
        public static final ContentType OTHER                   = new ContentType(OTHER_TYPE);
        public static final ContentType PACKAGE                 = new ContentType(PACKAGE_TYPE);
        public static final ContentType APPLICATION             = new ContentType(APPLICATION_TYPE);
        public static final ContentType JAVA                    = new ContentType(JAVA_TYPE);
        public static final ContentType AUDIO_AC3               = new ContentType(AUDIO_AC3_TYPE);
        public static final ContentType AUDIO_MP2               = new ContentType(AUDIO_MP2_TYPE);
        public static final ContentType AUDIO_MP3               = new ContentType(AUDIO_MP3_TYPE);
        public static final ContentType IMAGE_PNG               = new ContentType(IMAGE_PNG_TYPE);
        public static final ContentType IMAGE_JPG               = new ContentType(IMAGE_JPG_TYPE);
        public static final ContentType IMAGE_MPG               = new ContentType(IMAGE_MPG_TYPE);
        public static final ContentType VIDEO_DRIP              = new ContentType(VIDEO_DRIP_TYPE);
        public static final ContentType VIDEO_MPG               = new ContentType(VIDEO_MPG_TYPE);
        public static final ContentType TEXT                    = new ContentType(TEXT_TYPE);
        public static final ContentType FONT                    = new ContentType(FONT_TYPE);
        public static final ContentType DIGEST                  = new ContentType(DIGEST_TYPE);
        public static final ContentType SIGNATURE               = new ContentType(SIGNATURE_TYPE);
        public static final ContentType CERTIFICATE             = new ContentType(CERTIFICATE_TYPE);
        public static final ContentType PERMISSION              = new ContentType(PERMISSION_TYPE);
        public static final ContentType ZIP                     = new ContentType(ZIP_TYPE);
        public static final ContentType GZIP                    = new ContentType(GZIP_TYPE);
        public static final ContentType FONT_INDEX              = new ContentType(FONT_INDEX_TYPE);
        public static final ContentType PACKAGE_MANIFEST        = new ContentType(PACKAGE_MANIFEST_TYPE);
        public static final ContentType HASHFILE                = new ContentType(HASHFILE_TYPE);
        public static final ContentType TEXT_XML                = new ContentType(TEXT_XML_TYPE);
        public static final ContentType CRL                     = new ContentType(CRL_TYPE);
        public static final ContentType DESCRIPTION             = new ContentType(DESCRIPTION_TYPE);
        public static final ContentType TEXT_PROPERTIES         = new ContentType(TEXT_PROPERTIES_TYPE);
        public static final ContentType EBIF                    = new ContentType(EBIF_TYPE);
        public static final ContentType SAVR                    = new ContentType(SAVR_TYPE);
        public static final ContentType TESTLET_CONF            = new ContentType(TESTLET_CONF_TYPE);
        public static final ContentType CODF                    = new ContentType(CODF_TYPE);

        private int type;

        private ContentType(int type) {
            this.type = type;
        }

        public int getContentType() { return type; }

        public String toString() {
            switch (type) {
            case PACKAGE_TYPE:
                return "package";
            case APPLICATION_TYPE:
                return "application";
            case JAVA_TYPE:
                return "java";
            case AUDIO_AC3_TYPE:
                return "AC3 audio";
            case AUDIO_MP2_TYPE:
                return "MP2 audio";
            case AUDIO_MP3_TYPE:
                return "MP3 audio";
            case IMAGE_PNG_TYPE:
                return "PNG image";
            case IMAGE_JPG_TYPE:
                return "JPEG image";
            case IMAGE_MPG_TYPE:
                return "MPG image";
            case VIDEO_DRIP_TYPE:
                return "drip video";
            case VIDEO_MPG_TYPE:
                return "mpg video";
            case TEXT_TYPE:
                return "text";
            case FONT_TYPE:
                return "font";
            case DIGEST_TYPE:
                return "digest";
            case SIGNATURE_TYPE:
                return "signature";
            case CERTIFICATE_TYPE:
                return "certificate";
            case PERMISSION_TYPE:
                return "permission";
            case ZIP_TYPE:
                return "ZIP file";
            case GZIP_TYPE:
                return "GZIP file";
            case FONT_INDEX_TYPE:
                return "font index";
            case PACKAGE_MANIFEST_TYPE:
                return "package manifest";
            case HASHFILE_TYPE:
                return "hashfile";
            case TEXT_XML_TYPE:
                return "XML";
            case CRL_TYPE:
                return "CRL";
            case DESCRIPTION_TYPE:
                return "application description";
            case TEXT_PROPERTIES_TYPE:
                return "Java properties";
            case OTHER_TYPE:
                return "other";
            case UNSPECIFIED_TYPE:
                return "unspecified";
            case EBIF_TYPE:
                return "EBIF 1.0";
            case SAVR_TYPE:
                return "secure application validation record";
            case TESTLET_CONF_TYPE:
                return "testlet configuration";
            case CODF_TYPE:
                return "CODF package";
            default:
                return "unknown";
            }
        }
    }

    public static final class Reference {
        public static final int OTHER_REFERENCE         = 0;
        public static final int JVM12_REFERENCE         = 1;
        public static final int JDK11_REFERENCE         = 2;
        public static final int JDK12_REFERENCE         = 3;
        public static final int PJAE1_REFERENCE         = 4;
        public static final int JSSE1_REFERENCE         = 5;
        public static final int JMF1_REFERENCE          = 6;
        public static final int JTV1_REFERENCE          = 7;
        public static final int GEM_REFERENCE           = 8;
        public static final int MHP_REFERENCE           = 9;
        public static final int OCAP_REFERENCE          = 10;
        public static final int ZIP_REFERENCE           = 11;
        public static final int GZIP_REFERENCE          = 12;
        public static final int DAVIC_REFERENCE         = 13;
        public static final int XML10_REFERENCE         = 14;
        public static final int JVM11_REFERENCE         = 15;
        public static final int OCSS_REFERENCE          = 16;
        public static final int JPEG_REFERENCE          = 17;
        public static final int JFIF_REFERENCE          = 18;
        public static final int AC3_REFERENCE           = 19;
        public static final int MP3_REFERENCE           = 20;
        public static final int JDK122_REFERENCE        = 21;
        public static final int XAV10_REFERENCE         = 22;
        public static final int IFRAME_REFERENCE        = 23;
        public static final int ACAP_REFERENCE          = 24;
        public static final int JSR242_REFERENCE        = 25;
        public static final int PNG_REFERENCE           = 26;
        public static final int EBIF_REFERENCE          = 27;
        public static final int CLDC10_REFERENCE        = 28;
        public static final int CLDC11_REFERENCE        = 29;
        public static final int SAVR1_REFERENCE         = 30;
        public static final int OCAP_DVR_REFERENCE      = 31;
        public static final int CODF_REFERENCE          = 32;

        public static final Reference OTHER     = new Reference(OTHER_REFERENCE);
        public static final Reference JVM12     = new Reference(JVM12_REFERENCE);
        public static final Reference JDK11     = new Reference(JDK11_REFERENCE);
        public static final Reference JDK12     = new Reference(JDK12_REFERENCE);
        public static final Reference PJAE1     = new Reference(PJAE1_REFERENCE);
        public static final Reference JMF1      = new Reference(JMF1_REFERENCE);
        public static final Reference JSSE1     = new Reference(JSSE1_REFERENCE);
        public static final Reference JTV1      = new Reference(JTV1_REFERENCE);
        public static final Reference GEM       = new Reference(GEM_REFERENCE);
        public static final Reference MHP       = new Reference(MHP_REFERENCE);
        public static final Reference OCAP      = new Reference(OCAP_REFERENCE);
        public static final Reference ZIP       = new Reference(ZIP_REFERENCE);
        public static final Reference GZIP      = new Reference(GZIP_REFERENCE);
        public static final Reference DAVIC     = new Reference(DAVIC_REFERENCE);
        public static final Reference XML10     = new Reference(XML10_REFERENCE);
        public static final Reference JVM11     = new Reference(JVM11_REFERENCE);
        public static final Reference OCSS      = new Reference(OCSS_REFERENCE);
        public static final Reference JPEG      = new Reference(JPEG_REFERENCE);
        public static final Reference JFIF      = new Reference(JFIF_REFERENCE);
        public static final Reference AC3       = new Reference(AC3_REFERENCE);
        public static final Reference MP3       = new Reference(MP3_REFERENCE);
        public static final Reference JDK122    = new Reference(JDK122_REFERENCE);
        public static final Reference XAV10     = new Reference(XAV10_REFERENCE);
        public static final Reference IFRAME    = new Reference(IFRAME_REFERENCE);
        public static final Reference ACAP      = new Reference(ACAP_REFERENCE);
        public static final Reference JSR242    = new Reference(JSR242_REFERENCE);
        public static final Reference PNG       = new Reference(PNG_REFERENCE);
        public static final Reference EBIF      = new Reference(EBIF_REFERENCE);
        public static final Reference CLDC10    = new Reference(CLDC10_REFERENCE);
        public static final Reference CLDC11    = new Reference(CLDC11_REFERENCE);
        public static final Reference SAVR1     = new Reference(SAVR1_REFERENCE);
        public static final Reference OCAP_DVR  = new Reference(OCAP_DVR_REFERENCE);
        public static final Reference CODF      = new Reference(CODF_REFERENCE);
        private int type;

        private Reference(int type) {
            this.type = type;
        }

        public int getCategory() { return type; }

        public String toString() {
            switch (type) {
            case OTHER_REFERENCE:
                return "other";
            case JVM11_REFERENCE:
                return "JVM 1.1";
            case JVM12_REFERENCE:
                return "JVM 1.2";
            case JDK11_REFERENCE:
                return "JDK 1.1";
            case JDK12_REFERENCE:
                return "JDK 1.2";
            case JDK122_REFERENCE:
                return "JDK 1.2.2";
            case PJAE1_REFERENCE:
                return "PJAE 1.2a";
            case JSSE1_REFERENCE:
                return "JSSE 1.0";
            case JMF1_REFERENCE:
                return "JMF 1.0";
            case JTV1_REFERENCE:
                return "JAVA TV 1.0";
            case GEM_REFERENCE:
                return "GEM 1.0.2";
            case MHP_REFERENCE:
                return "MHP 1.0.3";
            case OCAP_REFERENCE:
                return "OCAP 1.0";
            case ZIP_REFERENCE:
                return "PKWARE APPNOTE 4.0";
            case GZIP_REFERENCE:
                return "RFC-1952";
            case DAVIC_REFERENCE:
                return "DAVIC 1.4.1";
            case XML10_REFERENCE:
                return "XML 1.0";
            case OCSS_REFERENCE:
                return "OC-SP-SEC-I06";
            case JPEG_REFERENCE:
                return "ISO/IEC 10918-1";
            case JFIF_REFERENCE:
                return "JFIF 1.02";
            case AC3_REFERENCE:
                return "AC-3, Revision A";
            case MP3_REFERENCE:
                return "ISO/IEC 11172-3";
            case IFRAME_REFERENCE:
                return "ISO/IEC 13818-2";
            case XAV10_REFERENCE:
                return "XAV 1.0";
            case ACAP_REFERENCE:
                return "ACAP A/101";
            case JSR242_REFERENCE:
                return "JSR-242";
            case PNG_REFERENCE:
                return "PNG 1.0";
            case EBIF_REFERENCE:
                return "ETV BIF 1.0"; // TBD: use OC-SP-ETV-BIF1.0-I03-06MMDD?
            case CLDC10_REFERENCE:
                return "CLDC 1.0";
            case CLDC11_REFERENCE:
                return "CLDC 1.1";
            case SAVR1_REFERENCE:
                return "SAVR 1.0";
            case OCAP_DVR_REFERENCE:
                return "OCAP 1.0 DVR";
            case CODF_REFERENCE:
                return "CODF 1.0";
            default:
                return "unknown";
            }
        }
    }

    public static final class Severity {
        public static final int TRACE_SEVERITY          = 0;
        public static final int INFO_SEVERITY           = 1;
        public static final int WARNING_SEVERITY        = 2;
        public static final int ERROR_SEVERITY          = 3;
        public static final int FATAL_SEVERITY          = 4;
        public static final int OFF_SEVERITY            = 5;   // Note: disables logging
        public static final int UNSPECIFIED_SEVERITY    = 6;
        public static final int ANY_SEVERITY            = 7;

        public static final Severity TRACE              = new Severity(TRACE_SEVERITY);
        public static final Severity INFO               = new Severity(INFO_SEVERITY);
        public static final Severity WARNING            = new Severity(WARNING_SEVERITY);
        public static final Severity ERROR              = new Severity(ERROR_SEVERITY);
        public static final Severity FATAL              = new Severity(FATAL_SEVERITY);
        public static final Severity OFF                = new Severity(OFF_SEVERITY);
        public static final Severity UNSPECIFIED        = new Severity(UNSPECIFIED_SEVERITY);
        public static final Severity ANY                = new Severity(ANY_SEVERITY);
        private int type;

        private Severity(int type) {
            this.type = type;
        }

        public int getSeverity() { return type; }

        public String toString() {
            switch (type) {
            case TRACE_SEVERITY:
                return "trace";
            case INFO_SEVERITY:
                return "info";
            case WARNING_SEVERITY:
                return "warning";
            case ERROR_SEVERITY:
                return "error";
            case FATAL_SEVERITY:
                return "fatal";
            case OFF_SEVERITY:
                return "off";
            case UNSPECIFIED_SEVERITY:
                return "unspecified";
            case ANY_SEVERITY:
                return "*";
            default:
                return "unknown";
            }
        }

        /*
        public Level getLevel() {
            switch (type) {
            case Error.Severity.TRACE_SEVERITY:
                return Level.FINE;
            case Error.Severity.INFO_SEVERITY:
                return Level.INFO;
            case Error.Severity.WARNING_SEVERITY:
                return Level.WARNING;
            case Error.Severity.ERROR_SEVERITY:
                return Level.SEVERE;
            case Error.Severity.FATAL_SEVERITY:
                return Level.SEVERE;
            case Error.Severity.OFF_SEVERITY:
                return Level.OFF;
            case Error.Severity.UNSPECIFIED_SEVERITY:
            case Error.Severity.ANY_SEVERITY:
            default:
                return Level.SEVERE;
            }
        }
        */
    }

    public static final class TestType {

        public static final int STATIC_TEST                     = 0;
        public static final int DYNAMIC_TEST                    = 1;

        public static final TestType STATIC                     = new TestType(STATIC_TEST);
        public static final TestType DYNAMIC                    = new TestType(DYNAMIC_TEST);

        private int type;
        private TestType(int type) {
            this.type = type;
        }

        public int getType() { return type; }

        public String toString() {
            switch(type) {
                case STATIC_TEST:
                    return "static";
                case DYNAMIC_TEST:
                    return "dynamic";
                default:
                    return "unknown";
            }
        }
    }

    /**
     * Error code string
     */
    private String code;

    /**
     * Test type
     */
    private TestType type;

    /**
     * Error category
     */
    private Category category;

    /**
     * Error severity
     */
    private Severity severity;

    /**
     * Reference to specification
     */
    private Reference reference;

    /**
     * Test data content type
     */
    private ContentType contentType;

    /**
     * Error message
     */
    private String message;

    /**
     * Error sub-validator reference
     */
    private String subReference;

    public Error() {
        this.code = "UNK01X001";
        this.type = TestType.STATIC;
        this.category = Category.OTHER;
        this.severity = Severity.ERROR;
        this.reference = Reference.OTHER;
        this.contentType = ContentType.OTHER;
        this.message = "Unknown error";
        this.subReference = "";
    }

    public Error(String messageKey, TestType type, Category category, Severity severity, ContentType contentType, Reference reference, String message, String subReference) {
        this.code = messageKey;
        this.type = type;
        this.category = category;
        this.severity = severity;
        this.reference = reference;
        this.contentType = contentType;
        this.message = message;
        this.subReference = subReference;
    }

    public String toString() {
        String s = this.type.toString() + " " + this.category.toString() + " ";
        if (this.contentType != ContentType.UNSPECIFIED)
            s = s + this.contentType.toString() + " ";
        s = s + this.severity.toString() + " " + this.getCode();
        s = s + ": ";
        s = s + this.getMessage();
        return s;
    }

    public String getCode()             { return this.code; }
    public TestType getType()           { return this.type; }
    public Category getCategory()       { return this.category; }
    public Severity getSeverity()       { return this.severity; }
    public Reference getReference()     { return this.reference; }
    public ContentType getContentType() { return this.contentType; }
    public String getMessage()          { return this.message; }
    public String getSubReference()     { return this.subReference; }

}
