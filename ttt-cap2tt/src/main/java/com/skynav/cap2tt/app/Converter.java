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

package com.skynav.cap2tt.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.Annotation;
import java.text.AttributedString;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.helpers.LocatorImpl;

import com.skynav.cap2tt.converter.AnnotatedRange;
import com.skynav.cap2tt.converter.Attribute;
import com.skynav.cap2tt.converter.AttributeSpecification;
import com.skynav.cap2tt.converter.ConverterContext;
import com.skynav.cap2tt.converter.ExternalParametersStore;
import com.skynav.cap2tt.converter.Results;
import com.skynav.cap2tt.converter.Screen;
import com.skynav.cap2tt.converter.TextAttribute;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionProcessor;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.ShowUsageException;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.app.UsageException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Models;
import com.skynav.ttv.model.ttml2.tt.TimedText;         // [TBD] remove dependency
import com.skynav.ttv.model.value.ClockTime;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.model.value.impl.ClockTimeImpl;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.util.Base64;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.NullReporter;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.Reporters;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Timing;
import com.skynav.xml.helpers.Sniffer;
import com.skynav.xml.helpers.XML;

import static com.skynav.ttv.model.ttml.TTML2.Constants.*;

public class Converter implements ConverterContext {

    public static final int RV_SUCCESS                          = 0;
    public static final int RV_FAIL                             = 1;
    public static final int RV_USAGE                            = 2;

    public static final int RV_FLAG_ERROR_UNEXPECTED            = 0x000001;
    public static final int RV_FLAG_ERROR_EXPECTED_MATCH        = 0x000002;
    public static final int RV_FLAG_ERROR_EXPECTED_MISMATCH     = 0x000004;
    public static final int RV_FLAG_WARNING_UNEXPECTED          = 0x000010;
    public static final int RV_FLAG_WARNING_EXPECTED_MATCH      = 0x000020;
    public static final int RV_FLAG_WARNING_EXPECTED_MISMATCH   = 0x000040;

    public static final String DEFAULT_INPUT_ENCODING           = "UTF-8";
    public static final String DEFAULT_OUTPUT_ENCODING          = "UTF-8";

    // uri related constants
    public static final String uriFileDescriptorScheme          = "fd";
    public static final String uriFileDescriptorStandardIn      = "stdin";
    public static final String uriFileDescriptorStandardOut     = "stdout";
    public static final String uriStandardInput                 = uriFileDescriptorScheme + ":" + uriFileDescriptorStandardIn;
    public static final String uriStandardOutput                = uriFileDescriptorScheme + ":" + uriFileDescriptorStandardOut;
    public static final String uriFileScheme                    = "file";

    // miscelaneous defaults
    public static final String defaultReporterFileEncoding      = Reporters.getDefaultEncoding();
    public static Charset defaultEncoding;
    public static Charset defaultOutputEncoding;
    public static final String defaultOutputFileNamePattern     = "tt{0,number,0000}.xml";
    public static final String defaultStyleIdPattern            = "s{0}";

    static {
        try {
            defaultEncoding = Charset.forName(DEFAULT_INPUT_ENCODING);
        } catch (RuntimeException e) {
            defaultEncoding = Charset.defaultCharset();
        }
        try {
            defaultOutputEncoding = Charset.forName(DEFAULT_OUTPUT_ENCODING);
        } catch (RuntimeException e) {
            defaultOutputEncoding = Charset.defaultCharset();
        }
    }

    // element names
    public static final QName ttBreakEltName = new QName(NAMESPACE_TT, "br");
    public static final QName ttHeadEltName = new QName(NAMESPACE_TT, "head");
    public static final QName ttInitialEltName = new QName(NAMESPACE_TT, "initial");
    public static final QName ttParagraphEltName = new QName(NAMESPACE_TT, "p");
    public static final QName ttRegionEltName = new QName(NAMESPACE_TT, "region");
    public static final QName ttSpanEltName = new QName(NAMESPACE_TT, "span");
    public static final QName ttStylingEltName = new QName(NAMESPACE_TT, "styling");
    public static final QName ttmItemEltName = new QName(NAMESPACE_TT_METADATA, "item");

    // ttml1 attribute names
    public static final QName regionAttrName = new QName("", "region");
    public static final QName ttsFontFamilyAttrName = new QName(NAMESPACE_TT_STYLE, "fontFamily");
    public static final QName ttsFontSizeAttrName = new QName(NAMESPACE_TT_STYLE, "fontSize");
    public static final QName ttsFontStyleAttrName = new QName(NAMESPACE_TT_STYLE, "fontStyle");
    public static final QName ttsTextAlignAttrName = new QName(NAMESPACE_TT_STYLE, "textAlign");
    public static final QName xmlSpaceAttrName = new QName(XML.xmlNamespace, "space");

    // ttml2 attribute names
    public static final QName ttsFontKerningAttrName = new QName(NAMESPACE_TT_STYLE, "fontKerning");
    public static final QName ttsFontShearAttrName = new QName(NAMESPACE_TT_STYLE, "fontShear");
    public static final QName ttsRubyAttrName = new QName(NAMESPACE_TT_STYLE, "ruby");
    public static final QName ttsTextEmphasisAttrName = new QName(NAMESPACE_TT_STYLE, "textEmphasis");
    public static final QName ttsTextCombineAttrName = new QName(NAMESPACE_TT_STYLE, "textCombine");

    // ttv annotation names
    public static final QName ttvaModelAttrName = new QName(Annotations.getNamespace(), "model");

    // miscellaneous
    public static final float[] shears = new float[] { 0, 6.345103f, 11.33775f, 16.78842f, 21.99875f, 27.97058f };

    // banner text
    public static final String title = "CAP To Timed Text (CAP2TT) [" + Version.CURRENT + "]";
    public static final String copyright = "Copyright 2014-16 Skynav, Inc.";
    public static final String banner = title + " " + copyright;
    public static final String creationSystem = "CAP2TT/" + Version.CURRENT;

    // usage text
    public static final String repositoryURL = "https://github.com/skynav/cap2tt";
    public static final String repositoryInfo = "Source Repository: " + repositoryURL;

    // option and usage info
    private static final String[][] shortOptionSpecifications = new String[][] {
        { "d",  "see --debug" },
        { "q",  "see --quiet" },
        { "v",  "see --verbose" },
        { "?",  "see --help" },
    };
    private static final Collection<OptionSpecification> shortOptions;
    static {
        shortOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : shortOptionSpecifications) {
            shortOptions.add(new OptionSpecification(spec[0], spec[1]));
        }
    }

    private static final String[][] longOptionSpecifications = new String[][] {
        { "add-creation-metadata",      "[BOOLEAN]","add creation metadata (default: see configuration)" },
        { "allow-modified-utf8",        "",         "allow use of modififed utf-8" },
        { "config",                     "FILE",     "specify path to configuration file" },
        { "debug",                      "",         "enable debug output (may be specified multiple times to increase debug level)" },
        { "debug-exceptions",           "",         "enable stack traces on exceptions (implies --debug)" },
        { "debug-level",                "LEVEL",    "enable debug output at specified level (default: 0)" },
        { "default-alignment",          "ALIGNMENT","specify default alignment (default: \"中央\")" },
        { "default-kerning",            "KERNING",  "specify default kerning (default: \"1\")" },
        { "default-language",           "LANGUAGE", "specify default language (default: \"\")" },
        { "default-placement",          "PLACEMENT","specify default placement (default: \"横下\")" },
        { "default-region",             "ID",       "specify identifier of default region (default: undefined)" },
        { "default-shear",              "SHEAR",    "specify default shear (default: \"3\")" },
        { "default-typeface",           "TYPEFACE", "specify default typeface (default: \"default\")" },
        { "default-whitespace",         "SPACE",    "specify default xml space treatment (\"default\"|\"preserve\"; default: \"default\")" },
        { "disable-warnings",           "",         "disable warnings (both hide and don't count warnings)" },
        { "expect-errors",              "COUNT",    "expect count errors or -1 meaning unspecified expectation (default: -1)" },
        { "expect-warnings",            "COUNT",    "expect count warnings or -1 meaning unspecified expectation (default: -1)" },
        { "external-duration",          "DURATION", "specify root temporal extent duration for document processing context" },
        { "external-extent",            "EXTENT",   "specify root container region extent for document processing context" },
        { "external-frame-rate",        "RATE",     "specify frame rate for document processing context" },
        { "help",                       "",         "show usage help" },
        { "hide-warnings",              "",         "hide warnings (but count them)" },
        { "hide-resource-location",     "",         "hide resource location (default: show)" },
        { "hide-resource-path",         "",         "hide resource path (default: show)" },
        { "merge-styles",               "[BOOLEAN]","merge styles (default: see configuration)" },
        { "no-warn-on",                 "TOKEN",    "disable warning specified by warning TOKEN, where multiple instances of this option may be specified" },
        { "no-verbose",                 "",         "disable verbose output (resets verbosity level to 0)" },
        { "output-directory",           "DIRECTORY","specify path to directory where TTML output is to be written; ignored if --output-file is specified" },
        { "output-disable",             "[BOOLEAN]","disable output (default: false)" },
        { "output-encoding",            "ENCODING", "specify character encoding of TTML output (default: " + defaultOutputEncoding.name() + ")" },
        { "output-file",                "FILE",     "specify path to TTML output file, in which case only single input URI may be specified" },
        { "output-pattern",             "PATTERN",  "specify TTML output file name pattern" },
        { "output-indent",              "",         "indent TTML output (default: no indent)" },
        { "quiet",                      "",         "don't show banner" },
        { "reporter",                   "REPORTER", "specify reporter, where REPORTER is " + Reporters.getReporterNamesJoined() + " (default: " +
             Reporters.getDefaultReporterName()+ ")" },
        { "reporter-file",              "FILE",     "specify path to file to which reporter output is to be written" },
        { "reporter-file-encoding",     "ENCODING", "specify character encoding of reporter output (default: utf-8)" },
        { "reporter-file-append",       "",         "if reporter file already exists, then append output to it" },
        { "retain-document",            "",         "retain document in results object (default: don't retain)" },
        { "show-repository",            "",         "show source code repository information" },
        { "show-resource-location",     "",         "show resource location (default: show)" },
        { "show-resource-path",         "",         "show resource path (default: show)" },
        { "show-warning-tokens",        "",         "show warning tokens (use with --verbose to show more details)" },
        { "style-id-pattern",           "PATTERN",  "specify style identifier format pattern (default: s{0})" },
        { "style-id-sequence-start",    "NUMBER",   "specify style identifier sequence starting value, must be non-negative (default: 0)" },
        { "verbose",                    "",         "enable verbose output (may be specified multiple times to increase verbosity level)" },
        { "treat-warning-as-error",     "",         "treat warning as error (overrides --disable-warnings)" },
        { "warn-on",                    "TOKEN",    "enable warning specified by warning TOKEN, where multiple instances of this option may be specified" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    private static final String usageCommand =
        "java -jar cap2tt.jar [options] URL*";

    private static final String[][] nonOptions = new String[][] {
        { "URL", "an absolute or relative URL; if relative, resolved against current working directory" },
    };

    // default warnings
    private static final Object[][] defaultWarningSpecifications = new Object[][] {
        { "all",                                        Boolean.FALSE,  "all warnings" },
        { "bad-header-drop-flags",                      Boolean.TRUE,   "bad header drop flags" },
        { "bad-header-field-count",                     Boolean.TRUE,   "header line missing field(s)" },
        { "bad-header-length",                          Boolean.TRUE,   "header line too short" },
        { "bad-header-preamble",                        Boolean.TRUE,   "header line preamble missing or incorrect" },
        { "bad-header-scene-standard",                  Boolean.TRUE,   "bad header scene standard" },
        { "empty-input",                                Boolean.TRUE,   "empty input (no lines)" },
        { "non-text-attribute-in-text-field",           Boolean.TRUE,   "non-text attribute in text field" },
        { "out-time-precedes-in-time",                  Boolean.TRUE,   "out time precedes in time" }
    };

    public enum AttrContext {
        Attribute,
        Text,
        Both;
    };

    public enum AttrCount {
        None,
        Mandatory,
        Optional;
    };

    public enum NonTextAttributeTreatment {
        Fail,           // fail silently
        Error,          // fail with message
        Warning,        // warn with message
        Info,           // allow with message
        Ignore;         // allow silently
    };

    public enum Direction {
        LR,             // left to right
        RL,             // right to left
        TB;             // top to bottom
    }

    // known attribute specifications { name, context, count, minCount, maxCount }
    private static final Object[][] knownAttributeSpecifications = new Object[][] {
        // line placement (9.1.1)
        { "横下",               AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom
        { "横上",               AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal top
        { "横適",               AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal full
        { "横中",               AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal center
        { "縦右",               AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical right
        { "縦左",               AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical left
        { "縦適",               AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical full
        { "縦中",               AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical center
        // alignment (9.1.2)
        { "中央",               AttrContext.Attribute,  null,                   AttrCount.None               }, // center
        { "行頭",               AttrContext.Attribute,  null,                   AttrCount.None               }, // start
        { "行末",               AttrContext.Attribute,  null,                   AttrCount.None               }, // end
        { "中頭",               AttrContext.Attribute,  null,                   AttrCount.None               }, // center start
        { "中末",               AttrContext.Attribute,  null,                   AttrCount.None               }, // center end
        { "両端",               AttrContext.Attribute,  null,                   AttrCount.None               }, // justify
        // mixed placement and alignment (9.1.3)
        { "横中央",             AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom, center
        { "横中頭",             AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom, center start
        { "横中末",             AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom, center end
        { "横行頭",             AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom, start
        { "横行末",             AttrContext.Attribute,  null,                   AttrCount.None               }, // horizontal bottom, end
        { "縦右頭",             AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical right, start
        { "縦左頭",             AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical left, start
        { "縦中頭",             AttrContext.Attribute,  null,                   AttrCount.None               }, // vertical full, center start
        // font style (9.1.4)
        { "正体",               AttrContext.Both,       TextAttribute.SHEAR,    AttrCount.None               }, // normal
        { "斜",                 AttrContext.Both,       TextAttribute.SHEAR,    AttrCount.Optional,  (Integer) 0, (Integer) 5 }, // italic
        // kerning (9.1.5)
        { "詰",                 AttrContext.Both,       TextAttribute.KERNING,  AttrCount.Mandatory, (Integer) 0, (Integer) 1 }, // kerning disabled
        // font width/size (9.1.6)
        { "幅広",               AttrContext.Text,       TextAttribute.WIDTH,    AttrCount.None               }, // wide
        { "倍角",               AttrContext.Text,       TextAttribute.WIDTH,    AttrCount.None               }, // double
        { "半角",               AttrContext.Text,       TextAttribute.WIDTH,    AttrCount.None               }, // half
        { "拗音",               AttrContext.Text,       TextAttribute.WIDTH,    AttrCount.None               }, // contracted sound
        { "幅",                 AttrContext.Text,       TextAttribute.WIDTH,    AttrCount.Mandatory, (Integer) 5, (Integer) 20 }, // width (scale x|y)
        { "寸",                 AttrContext.Text,       TextAttribute.SIZE,     AttrCount.Mandatory, (Integer) 5, (Integer) 20 }, // size (scale x&y)
        // ruby (9.1.7)
        { "ルビ",               AttrContext.Text,       TextAttribute.RUBY,     AttrCount.None               }, // ruby auto
        { "ルビ上",             AttrContext.Text,       TextAttribute.RUBY,     AttrCount.None               }, // ruby above
        { "ルビ下",             AttrContext.Text,       TextAttribute.RUBY,     AttrCount.None               }, // ruby below
        { "ルビ右",             AttrContext.Text,       TextAttribute.RUBY,     AttrCount.None               }, // ruby right
        { "ルビ左",             AttrContext.Text,       TextAttribute.RUBY,     AttrCount.None               }, // ruby left
        // continuation
        { "継続",               AttrContext.Attribute,  null,                   AttrCount.None               }, // continuation
        // font family (9.1.8)
        { "丸ゴ",               AttrContext.Attribute,  null,                   AttrCount.None               }, // maru go
        { "丸ゴシック",         AttrContext.Attribute,  null,                   AttrCount.None               }, // maru gothic
        { "角ゴ",               AttrContext.Attribute,  null,                   AttrCount.None               }, // kaku go
        { "太角ゴ",             AttrContext.Attribute,  null,                   AttrCount.None               }, // futo kaku go
        { "太角ゴシック",       AttrContext.Attribute,  null,                   AttrCount.None               }, // futo kaku gothic
        { "太明",               AttrContext.Attribute,  null,                   AttrCount.None               }, // futa min
        { "太明朝",             AttrContext.Attribute,  null,                   AttrCount.None               }, // futa mincho
        { "シネマ",             AttrContext.Attribute,  null,                   AttrCount.None               }, // cinema
        // extensions
        { "組",                 AttrContext.Text,       TextAttribute.COMBINE,  AttrCount.None               }, // tate-chu-yoko (combine)
    };
    private static final Map<String, AttributeSpecification> knownAttributes;
    static {
        knownAttributes = new java.util.HashMap<String,AttributeSpecification>();
        for (Object[] spec : knownAttributeSpecifications) {
            assert spec.length >= 4;
            String name = (String) spec[0];
            AttrContext context = (AttrContext) spec[1];
            TextAttribute textAttribute = (TextAttribute) spec[2];
            AttrCount count = (AttrCount) spec[3];
            int minCount = (count != AttrCount.None) ? (Integer) spec[4] : 0;
            int maxCount = (count != AttrCount.None) ? (Integer) spec[5] : 0;
            knownAttributes.put(name, new AttributeSpecification(name, context, textAttribute, count, minCount, maxCount));
        }
    }

    // options state
    private boolean allowModifiedUTF8;
    private String defaultAlignment;
    private String defaultKerning;
    private String defaultLanguage;
    private String defaultPlacement;
    private String defaultRegion;
    private String defaultShear;
    private String defaultTypeface;
    private String defaultWhitespace;
    private String expectedErrors;
    private String expectedWarnings;
    private String externalDuration;
    private String externalExtent;
    private String externalFrameRate;
    private String forceEncodingName;
    private boolean includeSource;
    private boolean mergeStyles;
    @SuppressWarnings("unused")
    private boolean metadataCreation;
    private String outputDirectoryPath;
    private boolean outputDisabled;
    private String outputEncodingName;
    private String outputFilePath;
    private String outputPattern;
    private boolean outputIndent;
    private boolean quiet;
    private boolean retainDocument;
    private boolean showRepository;
    private boolean showWarningTokens;
    private String styleIdPattern;
    private int styleIdSequenceStart;

    // derived option state
    private Configuration configuration;
    private Charset forceEncoding;
    private File outputDirectory;
    private Charset outputEncoding;
    private File outputFile;
    private MessageFormat outputPatternFormatter;
    private double parsedExternalFrameRate;
    private double parsedExternalDuration;
    private double[] parsedExternalExtent;
    private MessageFormat styleIdPatternFormatter;

    // global processing state
    private SimpleDateFormat gmtDateTimeFormat;
    private PrintWriter showOutput;
    private ExternalParametersStore externalParameters = new ExternalParametersStore();
    private Model model;
    private Reporter reporter;
    private Map<String,Results> results = new java.util.HashMap<String,Results>();

    // per-resource processing state
    private String resourceUriString;
    private Map<String,Object> resourceState;
    @SuppressWarnings("unused")
    private URI resourceUri;
    private Charset resourceEncoding;
    private CharBuffer resourceBuffer;
    private int resourceExpectedErrors = -1;
    private int resourceExpectedWarnings = -1;
    private Document outputDocument;

    // per-resource parsing state
    private List<Screen> screens;
    private boolean inTextAttribute;

    public Converter() {
        this(null, null, null, false, null);
    }

    public Converter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource, PrintWriter showOutput) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        f.setTimeZone(java.util.TimeZone.getTimeZone("GMT+0000"));
        this.gmtDateTimeFormat = f;
        if (reporter == null)
            reporter = Reporters.getDefaultReporter();
        setReporter(reporter, reporterOutput, reporterOutputEncoding, reporterIncludeSource);
        setShowOutput(showOutput);
        this.model = Models.getModel("ttml2");
    }

    // ConverterContext implementation
    
    public List<Element> getConfigurationInitials() {
        return configuration.getInitials();
    }

    public Element getConfigurationRegion(String id) {
        return configuration.getRegion(id);
    }

    public ExternalParameters getExternalParameters() {
        return externalParameters;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setResourceState(String key, Object value) {
        if (resourceState != null)
            resourceState.put(key, value);
    }

    public Object getResourceState(String key) {
        if (resourceState != null)
            return resourceState.get(key);
        else
            return null;
    }

    public String getOption(String name) {
        if (name == null)
            return null;
        else if (name.equals("defaultAlignment"))
            return defaultAlignment;
        else if (name.equals("defaultKerning"))
            return defaultKerning;
        else if (name.equals("defaultLanguage"))
            return defaultLanguage;
        else if (name.equals("defaultPlacement"))
            return defaultPlacement;
        else if (name.equals("defaultRegion"))
            return defaultRegion;
        else if (name.equals("defaultShear"))
            return defaultShear;
        else if (name.equals("defaultTypeface"))
            return defaultTypeface;
        else if (name.equals("defaultWhitespace"))
            return defaultWhitespace;
        else
            return null;
    }
    
    public boolean getOptionBoolean(String name) {
        if (name == null)
            return false;
        else if (name.equals("mergeStyles"))
            return mergeStyles;
        else if (name.equals("outputDisabled"))
            return outputDisabled;
        else if (name.equals("outputIndent"))
            return outputIndent;
        else if (name.equals("retainDocument"))
            return retainDocument;
        else
            return false;
    }
    
    public int getOptionInteger(String name) {
        if (name == null)
            return 0;
        else if (name.equals("styleIdSequenceStart"))
            return styleIdSequenceStart;
        else
            return 0;
    }
    
    public Object getOptionObject(String name) {
        if (name == null)
            return null;
        else if (name.equals("gmtDateTimeFormat"))
            return gmtDateTimeFormat;
        else if (name.equals("model"))
            return model;
        else if (name.equals("outputDirectory"))
            return outputDirectory;
        else if (name.equals("outputDocument"))
            return outputDocument;
        else if (name.equals("outputEncoding"))
            return outputEncoding;
        else if (name.equals("outputFile"))
            return outputFile;
        else if (name.equals("outputPatternFormatter"))
            return outputPatternFormatter;
        else if (name.equals("styleIdPatternFormatter"))
            return styleIdPatternFormatter;
        else
            return null;
    }
    
    public void setOptionObject(String name, Object object) {
        if (name.equals("outputDocument"))
            outputDocument = (Document) object;
    }
    
    public Map<String, AttributeSpecification> getKnownAttributes() {
        return knownAttributes;
    }

    // Converter implementation
    
    private void resetReporter() {
        setReporter(Reporters.getDefaultReporter(), null, null, false, true);
    }

    private void setReporter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource) {
        setReporter(reporter, reporterOutput, reporterOutputEncoding, reporterIncludeSource, false);
    }

    private void setReporter(String reporterName, String reporterFileName, String reporterFileEncoding, boolean reporterFileAppend, boolean reporterIncludeSource) {
        assert reporterName != null;
        Reporter reporter = Reporters.getReporter(reporterName);
        if (reporter == null)
            throw new InvalidOptionUsageException("reporter", getReporter().message("x.001", "unknown reporter: {0}", reporterName));
        if (reporterFileName != null) {
            if (reporterFileEncoding == null)
                reporterFileEncoding = defaultReporterFileEncoding;
            try {
                Charset.forName(reporterFileEncoding);
            } catch (IllegalCharsetNameException e) {
                throw new InvalidOptionUsageException("reporter-file-encoding", getReporter().message("x.002", "illegal encoding name: {0}", reporterFileEncoding));
            } catch (UnsupportedCharsetException e) {
                throw new InvalidOptionUsageException("reporter-file-encoding", getReporter().message("x.003", "unsupported encoding: {1}", reporterFileEncoding));
            }
        }

        File reporterFile = null;
        boolean createdReporterFile = false;
        PrintWriter reporterOutput = null;
        if (reporterFileName != null) {
            reporterFile = new File(reporterFileName);
            FileOutputStream os = null;
            try {
                createdReporterFile = reporterFile.createNewFile();
                os = new FileOutputStream(reporterFile,  reporterFileAppend);
                reporterOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os, reporterFileEncoding)));
            } catch (Throwable e) {
                IOUtil.closeSafely(os);
                if (createdReporterFile)
                    IOUtil.deleteSafely(reporterFile);
            }
        }
        setReporter(reporter, reporterOutput, reporterFileEncoding, reporterIncludeSource);
        if (reporterOutput != null) {
            if (getReporter().getOutput() != reporterOutput) {
                reporterOutput.close();
                if (createdReporterFile)
                    IOUtil.deleteSafely(reporterFile);
            }
        }
    }

    private void setReporter(Reporter reporter, PrintWriter reporterOutput, String reporterOutputEncoding, boolean reporterIncludeSource, boolean closeOldReporter) {
        if (reporter == this.reporter)
            return;
        if (this.reporter != null)
            this.reporter.flush();
        if (closeOldReporter) {
            try {
                if (this.reporter != null)
                    this.reporter.close();
            } catch (IOException e) {
            } finally {
                this.reporter = null;
            }
        }
        try {
            if (!reporter.isOpen())
                reporter.open(defaultWarningSpecifications, reporterOutput, getReporterBundle(), reporterOutputEncoding, reporterIncludeSource);
            this.reporter = reporter;
            this.includeSource = reporterIncludeSource;
        } catch (Throwable e) {
            this.reporter = null;
        }
    }

    private ResourceBundle getReporterBundle() {
        try {
            return ResourceBundle.getBundle("com/skynav/cap2tt/app/messages", Locale.getDefault());
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public Charset getEncoding() {
        if (this.forceEncoding != null)
            return this.forceEncoding;
        else if (this.resourceEncoding != null)
            return this.resourceEncoding;
        else
            return defaultEncoding;
    }

    private List<String> preProcessOptions(List<String> args, OptionProcessor optionProcessor) {
        args = processReporterOptions(args, optionProcessor);
        args = processConfigurationOptions(args, optionProcessor);
        if (optionProcessor != null)
            args = optionProcessor.preProcessOptions(args, configuration, shortOptions, longOptions);
        return args;
    }

    private List<String> processReporterOptions(List<String> args, OptionProcessor optionProcessor) {
        String reporterName = null;
        String reporterFileName = null;
        String reporterFileEncoding = null;
        boolean reporterFileAppend = false;
        boolean reporterIncludeSource = false;
        List<String> skippedArgs = new java.util.ArrayList<String>();
        for (int i = 0, numArgs = args.size(); i < numArgs; ++i) {
            String arg = args.get(i);
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("reporter")) {
                    if (i + 1 >= numArgs)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterName = args.get(i + 1);
                    ++i;
                } else if (option.equals("reporter-file")) {
                    if (i + 1 >= numArgs)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterFileName = args.get(i + 1);
                    ++i;
                } else if (option.equals("reporter-file-encoding")) {
                    if (i + 1 >= numArgs)
                        throw new MissingOptionArgumentException("--" + option);
                    reporterFileEncoding = args.get(i + 1);
                    ++i;
                } else if (option.equals("reporter-file-append")) {
                    reporterFileAppend = true;
                } else if (option.equals("reporter-include-source")) {
                    reporterIncludeSource = true;
                } else {
                    skippedArgs.add(arg);
                }
            } else
                skippedArgs.add(arg);
        }
        if (reporterName != null)
            setReporter(reporterName, reporterFileName, reporterFileEncoding, reporterFileAppend, reporterIncludeSource);
        return skippedArgs;
    }

    private List<String> processConfigurationOptions(List<String> args, OptionProcessor optionProcessor) {
        String configFilePath = null;
        List<String> skippedArgs = new java.util.ArrayList<String>();
        for (int i = 0, numArgs = args.size(); i < numArgs; ++i) {
            String arg = args.get(i);
            if (arg.indexOf("--") == 0) {
                String option = arg.substring(2);
                if (option.equals("config")) {
                    if (i + 1 >= numArgs)
                        throw new MissingOptionArgumentException("--" + option);
                    configFilePath = args.get(i + 1);
                    ++i;
                } else {
                    skippedArgs.add(arg);
                }
            } else
                skippedArgs.add(arg);
        }
        configuration = loadConfiguration(configFilePath, optionProcessor);
        if (configuration == null)
            configuration = new Configuration();
        return skippedArgs;
    }

    private Configuration loadConfiguration(String configFilePath, OptionProcessor optionProcessor) {
        try {
            URL locator;
            if (configFilePath != null) {
                File f = new File(configFilePath);
                if (!f.isAbsolute())
                    f = new File(new File(".").getCanonicalFile(), configFilePath);
                locator = f.toURI().toURL();
            } else
                locator = null;
            return loadConfiguration(locator, optionProcessor);
        } catch (IOException e) {
            getReporter().logError(e);
            return null;
        }
    }

    private Configuration loadConfiguration(URL locator, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        if ((locator == null) && (optionProcessor != null))
            locator = optionProcessor.getDefaultConfigurationLocator();
        if (locator == null)
            locator = Configuration.getDefaultConfigurationLocator();
        try {
            com.skynav.ttv.util.ConfigurationDefaults configDefaults = (optionProcessor != null) ? optionProcessor.getConfigurationDefaults(locator) : null;
            if (configDefaults == null)
                configDefaults = new ConfigurationDefaults(locator);
            Class<? extends com.skynav.ttv.util.Configuration> configClass = (optionProcessor != null) ? optionProcessor.getConfigurationClass() : null;
            if (configClass == null)
                configClass = Configuration.class;
            return (Configuration) Configuration.fromLocator(locator, configDefaults, configClass, reporter);
        } catch (IOException e) {
            reporter.logError(e);
            return null;
        }
    }

    private List<String> parseArgs(List<String> args, OptionProcessor optionProcessor) {
        args = processConfigurationArguments(args, optionProcessor);
        args = processOptionArguments(args, optionProcessor);
        args = processNonOptionArguments(args, optionProcessor);
        processDerivedOptions(optionProcessor);
        return args;
    }

    private List<String> processConfigurationArguments(List<String> args, OptionProcessor optionProcessor) {
        if (configuration != null) {
            for (Map.Entry<String,String> e : configuration.getOptions().entrySet()) {
                String n = e.getKey();
                String v = e.getValue();
                List<String> option = new java.util.ArrayList<String>(2);
                option.add("--" + n);
                option.add(v);
                int i = parseLongOption(option, 0, optionProcessor);
                assert i > 0;
            }
        }
        return args;
    }

    private List<String> processOptionArguments(List<String> args, OptionProcessor optionProcessor) {
        int nonOptionIndex = -1;
        for (int i = 0; i < args.size();) {
            String arg = args.get(i);
            if (arg.charAt(0) == '-') {
                if (arg.charAt(1) != '-') {
                    if (arg.length() != 2)
                        throw new UnknownOptionException(arg);
                    i = parseShortOption(args, i, optionProcessor);
                } else {
                    i = parseLongOption(args, i, optionProcessor);
                }
            } else {
                nonOptionIndex = i;
                break;
            }
        }
        List<String> nonOptionArgs = new java.util.ArrayList<String>();
        if (nonOptionIndex >= 0) {
            for (int i = nonOptionIndex, n = args.size(); i < n; ++i)
                nonOptionArgs.add(args.get(i));
            if (nonOptionArgs.isEmpty())
                nonOptionArgs.add(uriStandardInput);
        }
        return nonOptionArgs;
    }

    private int parseShortOption(List<String> args, int index, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        String arg = args.get(index);
        String option = arg;
        assert option.length() == 2;
        option = option.substring(1);
        switch (option.charAt(0)) {
        case 'd':
            reporter.incrementDebugLevel();
            break;
        case 'q':
            quiet = true;
            break;
        case 'v':
            reporter.incrementVerbosityLevel();
            break;
        case '?':
            throw new ShowUsageException();
        default:
            if ((optionProcessor != null) && optionProcessor.hasOption(args.get(index)))
                return optionProcessor.parseOption(args, index);
            else
                throw new UnknownOptionException("-" + option);
        }
        return index + 1;
    }

    private int parseLongOption(List<String> args, int index, OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("add-creation-metadata")) {
            Boolean b = Boolean.TRUE;
            if ((index + 1 < numArgs) && isBoolean(args.get(index + 1))) {
                b = Boolean.valueOf(args.get(++index));
            }
            metadataCreation = b.booleanValue();
        } else if (option.equals("allow-modified-utf8")) {
            allowModifiedUTF8 = true;
        } else if (option.equals("debug")) {
            int debug = reporter.getDebugLevel();
            if (debug < 1)
                debug = 1;
            else
                debug += 1;
            reporter.setDebugLevel(debug);
        } else if (option.equals("debug-exceptions")) {
            int debug = reporter.getDebugLevel();
            if (debug < 2)
                debug = 2;
            reporter.setDebugLevel(debug);
        } else if (option.equals("debug-level")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String level = args.get(++index);
            int debugNew;
            try {
                debugNew = Integer.parseInt(level);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("debug-level", reporter.message("x.004", "bad debug level syntax: {0}", level));
            }
            int debug = reporter.getDebugLevel();
            if (debugNew > debug)
                reporter.setDebugLevel(debugNew);
        } else if (option.equals("default-alignment")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultAlignment = args.get(++index);
        } else if (option.equals("default-kerning")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultKerning = args.get(++index);
        } else if (option.equals("default-language")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultLanguage = args.get(++index);
        } else if (option.equals("default-placement")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultPlacement = args.get(++index);
        } else if (option.equals("default-region")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultRegion = args.get(++index);
        } else if (option.equals("default-shear")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultShear = args.get(++index);
        } else if (option.equals("default-typeface")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultTypeface = args.get(++index);
        } else if (option.equals("default-whitespace")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultWhitespace = args.get(++index);
            if (!defaultWhitespace.equals("default") && !defaultWhitespace.equals("preserve"))
                throw new InvalidOptionUsageException("default-whitespace", getReporter().message("x.020", "unknown whitespace value: {0}", arg));
        } else if (option.equals("disable-warnings")) {
            reporter.disableWarnings();
        } else if (option.equals("expect-errors")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            expectedErrors = args.get(++index);
        } else if (option.equals("expect-warnings")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            expectedWarnings = args.get(++index);
        } else if (option.equals("external-duration")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalDuration = args.get(++index);
        } else if (option.equals("external-extent")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalExtent = args.get(++index);
        } else if (option.equals("external-frame-rate")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            externalFrameRate = args.get(++index);
        } else if (option.equals("force-encoding")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            forceEncodingName = args.get(++index);
        } else if (option.equals("help")) {
            throw new ShowUsageException();
        } else if (option.equals("hide-resource-location")) {
            reporter.hideLocation();
        } else if (option.equals("hide-resource-path")) {
            reporter.hidePath();
        } else if (option.equals("hide-warnings")) {
            reporter.hideWarnings();
        } else if (option.equals("merge-styles")) {
            Boolean b = Boolean.TRUE;
            if ((index + 1 < numArgs) && isBoolean(args.get(index + 1))) {
                b = parseBoolean(args.get(++index));
            }
            mergeStyles = b.booleanValue();
        } else if (option.equals("no-warn-on")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String token = args.get(++index);
            if (!reporter.hasDefaultWarning(token))
                throw new InvalidOptionUsageException("--" + option, reporter.message("x.005", "token ''{0}'' is not a recognized warning token", token));
            reporter.disableWarning(token);
        } else if (option.equals("no-verbose")) {
            reporter.setVerbosityLevel(0);
        } else if (option.equals("output-directory")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputDirectoryPath = args.get(++index);
        } else if (option.equals("output-disable")) {
            Boolean b = Boolean.TRUE;
            if ((index + 1 < numArgs) && isBoolean(args.get(index + 1))) {
                b = parseBoolean(args.get(++index));
            }
            outputDisabled = b.booleanValue();
        } else if (option.equals("output-encoding")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputEncodingName = args.get(++index);
        } else if (option.equals("output-file")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputFilePath = args.get(++index);
        } else if (option.equals("output-pattern")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputPattern = args.get(++index);
        } else if (option.equals("output-indent")) {
            outputIndent = true;
        } else if (option.equals("quiet")) {
            quiet = true;
        } else if (option.equals("retain-document")) {
            retainDocument = true;
        } else if (option.equals("show-repository")) {
            showRepository = true;
        } else if (option.equals("show-resource-location")) {
            reporter.showLocation();
        } else if (option.equals("show-resource-path")) {
            reporter.showPath();
        } else if (option.equals("show-warning-tokens")) {
            showWarningTokens = true;
        } else if (option.equals("style-id-pattern")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            styleIdPattern = args.get(++index);
        } else if (option.equals("style-id-sequence-start")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String optionArgument = args.get(++index);
            try {
                int number = Integer.parseInt(optionArgument);
                if (number < 0)
                    throw new NumberFormatException();
                styleIdSequenceStart = number;
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("--" + option, reporter.message("x.006", "number ''{0}'' is not a non-negative integer", optionArgument));
            }
        } else if (option.equals("treat-warning-as-error")) {
            reporter.setTreatWarningAsError(true);
        } else if (option.equals("verbose")) {
            reporter.incrementVerbosityLevel();
        } else if (option.equals("warn-on")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String token = args.get(++index);
            if (!reporter.hasDefaultWarning(token))
                throw new InvalidOptionUsageException("--" + option, reporter.message("x.005", "token ''{0}'' is not a recognized warning token", token));
            reporter.enableWarning(token);
        } else if ((optionProcessor != null) && optionProcessor.hasOption(args.get(index))) {
            return optionProcessor.parseOption(args, index);
        } else
            throw new UnknownOptionException("--" + option);
        return index + 1;
    }

    private static boolean isBoolean(String s) {
        String sLower = s.toLowerCase();
        if (sLower.equals("true"))
            return true;
        else if (sLower.equals("1"))
            return true;
        else if (sLower.equals("false"))
            return true;
        else if (sLower.equals("0"))
            return true;
        else
            return false;
    }

    private static Boolean parseBoolean(String s) {
        String sLower = s.toLowerCase();
        if (sLower.equals("1"))
            return Boolean.TRUE;
        else if (sLower.equals("0"))
            return Boolean.FALSE;
        else
            return Boolean.valueOf(s);
    }

    private List<String> processNonOptionArguments(List<String> nonOptionArgs, OptionProcessor optionProcessor) {
        if ((outputFile != null) && (nonOptionArgs.size() > 1)) {
            throw new InvalidOptionUsageException("output-file", getReporter().message("x.019", "must not be used when multiple URL arguments are specified"));
        }
        if (optionProcessor != null)
            nonOptionArgs = optionProcessor.processNonOptionArguments(nonOptionArgs);
        return nonOptionArgs;
    }

    private void processDerivedOptions(OptionProcessor optionProcessor) {
        Reporter reporter = getReporter();
        if (externalFrameRate != null) {
            try {
                parsedExternalFrameRate = Double.parseDouble(externalFrameRate);
                getExternalParameters().setParameter("externalFrameRate", Double.valueOf(parsedExternalFrameRate));
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException("external-frame-rate", reporter.message("x.007", "invalid syntax, must be a double: {0}", externalFrameRate));
            }
        } else
            parsedExternalFrameRate = 30.0;
        if (externalDuration != null) {
            Time[] duration = new Time[1];
            TimeParameters timeParameters = new TimeParameters(parsedExternalFrameRate);
            if (Timing.isDuration(externalDuration, null, null, timeParameters, duration)) {
                if (duration[0].getType() != Time.Type.Offset)
                    throw new InvalidOptionUsageException("external-duration", reporter.message("x.008", "must use offset time syntax only: {0}", externalDuration));
                parsedExternalDuration = duration[0].getTime(timeParameters);
                getExternalParameters().setParameter("externalDuration", Double.valueOf(parsedExternalDuration));
            } else
                throw new InvalidOptionUsageException("external-duration", reporter.message("x.009", "invalid syntax: {0}", externalDuration));
        }
        if (externalExtent != null) {
            Integer[] minMax = new Integer[] { 2, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(externalExtent, null, null, minMax, treatments, lengths)) {
                for (Length l : lengths) {
                    if (l.getUnits() != Length.Unit.Pixel)
                        throw new InvalidOptionUsageException("external-extent", reporter.message("x.010", "must use pixel (px) unit only: {0}", externalExtent));
                }
                parsedExternalExtent = new double[] { lengths.get(0).getValue(), lengths.get(1).getValue() };
                getExternalParameters().setParameter("externalExtent", parsedExternalExtent);
            } else
                throw new InvalidOptionUsageException("external-extent", reporter.message("x.011", "invalid syntax: {0}", externalExtent));
        }
        Charset forceEncoding;
        if (forceEncodingName != null) {
            try {
                forceEncoding = Charset.forName(forceEncodingName);
            } catch (Exception e) {
                forceEncoding = null;
            }
            if (forceEncoding == null)
                throw new InvalidOptionUsageException("force-encoding", reporter.message("x.012", "unknown encoding: {0}", forceEncodingName));
        } else
            forceEncoding = null;
        this.forceEncoding = forceEncoding;
        // output file
        File outputFile;
        if (outputFilePath != null) {
            outputFile = new File(outputFilePath);
            if (!outputFile.isAbsolute())
                outputFile = new File(".", outputFilePath);
            File outputFileDirectory = IOUtil.getDirectory(outputFile);
            if ((outputFileDirectory == null) || !outputFileDirectory.exists())
                throw new InvalidOptionUsageException("output-file", reporter.message("x.018", "directory does not exist: {0}", outputFile.getPath()));
        } else
            outputFile = null;
        this.outputFile = outputFile;
        // output directory
        File outputDirectory;
        if (outputDirectoryPath != null) {
            if (outputFilePath == null) {
                outputDirectory = new File(outputDirectoryPath);
                if (!outputDirectory.exists())
                    throw new InvalidOptionUsageException("output-directory", reporter.message("x.013", "directory does not exist: {0}", outputDirectoryPath));
                else if (!outputDirectory.isDirectory())
                    throw new InvalidOptionUsageException("output-directory", reporter.message("x.014", "not a directory: {0}", outputDirectoryPath));
            } else {
                reporter.logInfo(reporter.message("i.021", "The ''{0}'' option will be ignored due to explicit use of ''{1}'' option.", "output-directory", "output-file"));
                outputDirectory = null;
            }
        } else
            outputDirectory = (outputFile == null) ? new File(".") : null;
        this.outputDirectory = outputDirectory;
        Charset outputEncoding;
        if (outputEncodingName != null) {
            try {
                outputEncoding = Charset.forName(outputEncodingName);
            } catch (Exception e) {
                outputEncoding = null;
            }
            if (outputEncoding == null)
                throw new InvalidOptionUsageException("output-encoding", reporter.message("x.015", "unknown encoding: {0}", outputEncodingName));
        } else
            outputEncoding = null;
        if (outputEncoding == null)
            outputEncoding = defaultOutputEncoding;
        this.outputEncoding = outputEncoding;
        String outputPattern = this.outputPattern;
        if (outputPattern == null)
            outputPattern = defaultOutputFileNamePattern;
        this.outputPattern = outputPattern;
        this.outputPatternFormatter = new MessageFormat(outputPattern, Locale.US);
        // style id pattern formatter
        if (styleIdPattern == null)
            styleIdPattern = defaultStyleIdPattern;
        this.styleIdPatternFormatter = new MessageFormat(styleIdPattern, Locale.US);
        // handle eoption processor's derived options
        if (optionProcessor != null)
            optionProcessor.processDerivedOptions();
    }

    public void setShowOutput(PrintWriter showOutput) {
        this.showOutput = showOutput;
    }

    private PrintWriter getShowOutput() {
        if (showOutput == null)
            showOutput = new PrintWriter(new OutputStreamWriter(System.err, defaultOutputEncoding));
        return showOutput;
    }

    public void showBanner(PrintWriter out, String banner) {
        if (!quiet)
            out.println(banner);
    }

    private void showBanner(PrintWriter out, OptionProcessor optionProcessor) {
        if (optionProcessor != null)
            optionProcessor.showBanner(out);
        else
            showBanner(out, banner);
    }

    private void showUsage(PrintWriter out, OptionProcessor optionProcessor) {
        showBanner(out, optionProcessor);
        if (optionProcessor != null)
            optionProcessor.showUsage(out);
        else
            showUsage(out);
    }

    private void showUsage(PrintWriter out) {
        out.print("Usage: " + usageCommand + "\n");
        showOptions(out, "Short Options", shortOptions);
        showOptions(out, "Long Options", longOptions);
        showOptions(out, "Non-Option Arguments", nonOptions);
    }

    public static void showOptions(PrintWriter out, String label, Collection<OptionSpecification> optionSpecs) {
        StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append(label);
        sb.append(':');
        sb.append('\n');
        for (OptionSpecification os : optionSpecs) {
            sb.append("    ");
            sb.append(os.toString());
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    public static void showOptions(PrintWriter out, String label, String[][] optionSpecs) {
        StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append(label);
        sb.append(':');
        sb.append('\n');
        for (String[] option : optionSpecs) {
            assert option.length == 2;
            sb.append("    ");
            sb.append(option[0]);
            for (int i = 0, n = OptionSpecification.OPTION_FIELD_LENGTH - option[0].length(); i < n; i++)
                sb.append(' ');
            sb.append('-');
            sb.append(' ');
            sb.append(option[1]);
            sb.append('\n');
        }
        out.print(sb.toString());
    }

    private void showProcessingInfo() {
        Reporter reporter = getReporter();
        int level = reporter.getVerbosityLevel();
        if (level >  0) {
            if (level > 1)
                showConfigurationInfo();
            if (reporter.isTreatingWarningAsError())
                reporter.logInfo(reporter.message("i.002", "Warnings are treated as errors."));
            else if (reporter.areWarningsDisabled())
                reporter.logInfo(reporter.message("i.003", "Warnings are disabled."));
            else if (reporter.areWarningsHidden())
                reporter.logInfo(reporter.message("i.004", "Warnings are hidden."));
        }
    }

    private void showConfigurationInfo() {
        Reporter reporter = getReporter();
        if (configuration != null) {
            URL locator = configuration.getLocator();
            if (locator != null)
                reporter.logInfo(reporter.message("i.022", "Loaded configuration from ''{0}''.", locator.toString()));
            else
                reporter.logInfo(reporter.message("i.023", "Loaded configuration from built-in configuration defaults."));
            if (!configuration.getOptions().isEmpty()) {
                Map<String,String> options = configuration.getOptions();
                Set<String> names = new java.util.TreeSet<String>(options.keySet());
                for (String n : names) {
                    String v = options.get(n);
                    reporter.logInfo(reporter.message("i.024", "Configuration option: {0}=''{1}''.", n, v));
                }
            } else
                reporter.logInfo(reporter.message("i.025", "Configuration is empty."));
        } else
            reporter.logInfo(reporter.message("i.026", "No configuration."));
    }

    private void showRepository() {
        getShowOutput().println(repositoryInfo);
    }

    private void showWarningTokens() {
        Reporter reporter = getReporter();
        int maxTokenLength = 0;
        for (Object[] spec : defaultWarningSpecifications) {
            String token = (String) spec[0];
            int tokenLength = token.length();
            maxTokenLength = Math.max(maxTokenLength, tokenLength + ((tokenLength % 2) + 1) * 2);
        }
        StringBuffer sb = new StringBuffer();
        sb.append("Warning Tokens:\n");
        for (Object[] spec : defaultWarningSpecifications) {
            String token = (String) spec[0];
            Boolean defaultValue = (Boolean) spec[1];
            String help = (String) spec[2];
            sb.append("    ");
            sb.append(token);
            if (reporter.getVerbosityLevel() > 0) {
                if ((help != null) && (help.length() > 0)) {
                    int pad = maxTokenLength - token.length();
                    for (int i = 0; i < pad; ++i)
                        sb.append(' ');
                    sb.append(help);
                    if (!token.equals("all")) {
                        sb.append(" (default: ");
                        sb.append(defaultValue ? "enabled" : "disabled");
                        sb.append(')');
                    }
                }
            }
            sb.append("\n");
        }
        getShowOutput().println(sb.toString());
    }

    private URI getCWDAsURI() {
        return new File(".").toURI();
    }

    private URI resolve(String uriString) {
        Reporter reporter = getReporter();
        try {
            URI uri = new URI(uriString);
            if (!uri.isAbsolute()) {
                URI uriCurrentDirectory = getCWDAsURI();
                URI uriAbsolute = uriCurrentDirectory.resolve(uri);
                assert uriAbsolute != null;
                uri = uriAbsolute;
            }
            return uri;
        } catch (URISyntaxException e) {
            reporter.logError(reporter.message("e.001", "Bad URI syntax: '{'{0}'}'", uriString));
            return null;
        }
    }

    private ByteBuffer readResource(URI uri) {
        Reporter reporter = getReporter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = getInputStream(uri);
            byte[] buffer = new byte[1024];
            int nb;
            while ((nb = is.read(buffer)) >= 0) {
                os.write(buffer, 0, nb);
            }
        } catch (IOException e) {
            reporter.logError(e);
            os = null;
        } finally {
            IOUtil.closeSafely(is);
        }
        return (os != null) ? ByteBuffer.wrap(os.toByteArray()) : null;
    }

    private InputStream getInputStream(URI uri) throws IOException {
        if (isStandardInput(uri))
            return System.in;
        else if (isData(uri))
            return openStreamFromData(uri);
        else
            return uri.toURL().openStream();
    }

    public static boolean isStandardInput(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileDescriptorScheme))
            return false;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if ((schemeSpecificPart == null) || !schemeSpecificPart.equals(uriFileDescriptorStandardIn))
            return false;
        return true;
    }

    public static boolean isStandardOutput(String uri) {
        try {
            return isStandardOutput(new URI(uri));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isStandardOutput(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileDescriptorScheme))
            return false;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if ((schemeSpecificPart == null) || !schemeSpecificPart.equals(uriFileDescriptorStandardOut))
            return false;
        return true;
    }

    public static boolean isFile(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileScheme))
            return false;
        else
            return true;
    }

    private boolean isData(URI uri) {
        return uri.getScheme().equals("data");
    }

    private InputStream openStreamFromData(URI uri) {
        assert isData(uri);
        String ssp = uri.getSchemeSpecificPart();
        int dataIndex;
        byte[] bytes;
        if ((dataIndex = ssp.indexOf(',')) >= 0) {
            String metadata = ssp.substring(0, dataIndex++);
            String data = (dataIndex < ssp.length()) ? ssp.substring(dataIndex) : "";
            if (isBase64Data(metadata))
                bytes = Base64.decode(data.toCharArray());
            else {
                bytes = new byte[data.length()];
                for (int i = 0, n = bytes.length; i < n; ++i) {
                    char c = data.charAt(i);
                    if (c > 128)
                        throw new IllegalArgumentException();
                    else
                        bytes[i] = (byte) c;
                }
            }
        } else
            bytes = new byte[0];
        return new ByteArrayInputStream(bytes);
    }

    private boolean isBase64Data(String metadata) {
        return metadata.endsWith(";base64");
    }

    private static final List<Charset> permittedEncodings;

    static {
        List<Charset> l = new java.util.ArrayList<Charset>();
        try {
            l.add(Charset.forName("US-ASCII"));
            l.add(Charset.forName("UTF-8"));
            l.add(Charset.forName("UTF-16"));
            l.add(Charset.forName("UTF-16BE"));
            l.add(Charset.forName("UTF-16LE"));
            l.add(Charset.forName("SHIFT_JIS"));
        } catch (RuntimeException e) {}
        permittedEncodings = Collections.unmodifiableList(l);
    }

    public static Charset[] getPermittedEncodings() {
        return permittedEncodings.toArray(new Charset[permittedEncodings.size()]);
    }

    private boolean isPermittedEncoding(String name) {
        try {
            Charset cs = Charset.forName(name);
            for (Charset encoding : permittedEncodings) {
                if (encoding.equals(cs))
                    return true;
            }
            return false;
        } catch (UnsupportedCharsetException e) {
            return false;
        }
    }

    private CharBuffer decodeResource(ByteBuffer rawBuffer, Charset encoding, int bomLength) {
        Reporter reporter = getReporter();
        ByteBuffer bb = rawBuffer;
        bb.position(bomLength);
        List<CharBuffer> charBuffers = new java.util.ArrayList<CharBuffer>();
        do {
            CharsetDecoder cd = encoding.newDecoder();
            boolean endOfInput = false;
            CharBuffer cb = null;
            CoderResult r;
            while (true) {
                try {
                    if (cb == null)
                        cb = CharBuffer.allocate(65536);
                    if (bb != null)
                        r = cd.decode(bb, cb, endOfInput);
                    else
                        r = cd.flush(cb);
                    if (r.isOverflow()) {
                        cb.flip();
                        charBuffers.add(cb);
                        cb = null;
                    } else if (r.isUnderflow()) {
                        if (endOfInput) {
                            if (bb != null)
                                bb = null;
                            else {
                                cb.flip();
                                charBuffers.add(cb);
                                cb = null;
                                break;
                            }
                        } else {
                            endOfInput = true;
                        }
                    } else if (r.isMalformed()) {
                        Message message = reporter.message("e.002",
                            "Malformed {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    } else if (r.isUnmappable()) {
                        Message message = reporter.message("e.003",
                            "Unmappable {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    } else if (r.isError()) {
                        Message message = reporter.message("e.004",
                            "Can't decode as {0} at byte offset {1}{2,choice,0# of zero bytes|1# of one byte|1< of {2,number,integer} bytes}.",
                            encoding.name(), bb.position(), r.length());
                        reporter.logError(message);
                        return null;
                    }
                } catch (Exception e) {
                    reporter.logError(e);
                    return null;
                }
            }
        } while (false);
        rawBuffer.rewind();
        return concatenateBuffers(charBuffers);
    }

    private static CharBuffer concatenateBuffers(List<CharBuffer> buffers) {
        int length = 0;
        for (CharBuffer cb : buffers) {
            length += cb.limit();
        }
        CharBuffer newBuffer = CharBuffer.allocate(length);
        for (CharBuffer cb : buffers) {
            newBuffer.put(cb);
        }
        newBuffer.flip();
        return newBuffer;
    }

    private String[] parseLines(CharBuffer cb, Charset encoding) {
        List<String> lines = new java.util.ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        while (cb.hasRemaining()) {
            while (cb.hasRemaining()) {
                char c = cb.get();
                if (c == '\n') {
                    break;
                } else if (c == '\r') {
                    if (cb.hasRemaining()) {
                        c = cb.charAt(0);
                        if (c == '\n')
                            cb.get();
                    }
                    break;
                } else {
                    sb.append(c);
                }
            }
            lines.add(sb.toString());
            sb.setLength(0);
        }
        cb.rewind();
        return lines.toArray(new String[lines.size()]);
    }

    private void resetResourceState() {
        // processing state
        resourceUriString = null;
        resourceState = new java.util.HashMap<String,Object>();
        resourceUri = null;
        resourceEncoding = null;
        resourceBuffer = null;
        resourceExpectedErrors = -1;
        resourceExpectedWarnings = -1;
        outputDocument = null;
        getReporter().resetResourceState(false);
        // parsing state
        screens = new java.util.ArrayList<Screen>();
        inTextAttribute = false;
    }

    private void setResourceURI(String uri) {
        resourceUriString = uri;
        getReporter().setResourceURI(uri);
    }

    private void setResourceURI(URI uri) {
        resourceUri = uri;
        getReporter().setResourceURI(uri);
    }

    private void setResourceBuffer(Charset encoding, CharBuffer buffer, ByteBuffer bufferRaw) {
        resourceEncoding = encoding;
        setResourceState("encoding", encoding);
        resourceBuffer = buffer;
        setResourceState("buffer", buffer);
        if (expectedErrors != null) {
            resourceExpectedErrors = parseAnnotationAsInteger(expectedErrors, -1);
            setResourceState("resourceExpectedErrors", Integer.valueOf(resourceExpectedErrors));
        }
        if (expectedWarnings != null) {
            resourceExpectedWarnings = parseAnnotationAsInteger(expectedWarnings, -1);
            setResourceState("resourceExpectedWarnings", Integer.valueOf(resourceExpectedWarnings));
        }
    }

    private void setResourceDocumentContextState() {
        if (parsedExternalExtent != null) {
            setResourceState("externalExtent", parsedExternalExtent);
        }
    }

    private boolean readResource() {
        Reporter reporter = getReporter();
        reporter.logInfo(reporter.message("i.005", "Verifying resource presence and encoding ..."));
        URI uri = resolve(resourceUriString);
        if (uri != null) {
            setResourceURI(uri);
            ByteBuffer bytesBuffer = readResource(uri);
            if (bytesBuffer != null) {
                Object[] sniffOutputParameters = new Object[] { Integer.valueOf(0) };
                Charset encoding;
                if (this.forceEncoding != null) {
                    encoding = this.forceEncoding;
                    Charset bomEncoding = Sniffer.checkForBOMCharset(bytesBuffer, sniffOutputParameters);
                    if ((bomEncoding != null) && !encoding.equals(bomEncoding)) {
                        reporter.logError(reporter.message("e.005", "Resource encoding forced to {0}, but BOM encoding is {1}.", encoding.name(), bomEncoding.name()));
                    } else {
                        reporter.logInfo(reporter.message("i.006", "Resource encoding forced to {0}.", encoding.name()));
                    }
                } else {
                    encoding = sniff(bytesBuffer, null, sniffOutputParameters);
                    if (encoding != null) {
                        reporter.logInfo(reporter.message("i.007", "Resource encoding sniffed as {0}.", encoding.name()));
                    } else {
                        encoding = defaultEncoding;
                        reporter.logInfo(reporter.message("i.008", "Resource encoding defaulted to {0}.", encoding.name()));
                    }
                }
                if (reporter.getResourceErrors() == 0) {
                    if (isPermittedEncoding(encoding.name())) {
                        int bomLength = (Integer) sniffOutputParameters[0];
                        CharBuffer charsBuffer = decodeResource(bytesBuffer, encoding, bomLength);
                        if (charsBuffer != null) {
                            setResourceBuffer(encoding, charsBuffer, bytesBuffer);
                            if (includeSource)
                                reporter.setLines(parseLines(charsBuffer, encoding));
                            reporter.logInfo(reporter.message("i.009", "Resource length {0} bytes, decoded as {1} Java characters (char).",
                                bytesBuffer.limit(), charsBuffer.limit()));
                        }
                    } else {
                        reporter.logError(reporter.message("i.010", "Encoding {0} is not permitted.", encoding.name()));
                    }
                }
            }
        }
        return reporter.getResourceErrors() == 0;
    }

    private Charset sniff(ByteBuffer bb, Charset defaultCharset, Object[] outputParameters) {
        Charset cs;
        if ((cs = Sniffer.sniff(bb, null, outputParameters)) != null)
            return cs;
        else if ((cs = sniffShiftJIS(bb, outputParameters)) != null)
            return cs;
        else if ((cs = sniffUTF8DisregardingBOM(bb, allowModifiedUTF8, outputParameters)) != null)
            return cs;
        else
            return defaultCharset;
    }

    private static Charset asciiEncoding;
    private static Charset sjisEncoding;
    private static Charset utf8Encoding;
    static {
        try {
            asciiEncoding = Charset.forName("US-ASCII");
            sjisEncoding = Charset.forName("SHIFT_JIS");
            utf8Encoding = Charset.forName("UTF-8");
        } catch (RuntimeException e) {
            asciiEncoding = null;
            sjisEncoding = null;
            utf8Encoding = null;
        }
    }

    private static Charset sniffShiftJIS(ByteBuffer bb, Object[] outputParameters) {
        int restore = bb.position();
        int limit = bb.limit();
        int na = 0; // number of {ascii,jisx201} characters
        int nk = 0; // number of half width kana characters
        int nd = 0; // number of double byte characters
        int b1Bad = 0;
        int b2Bad = 0;
        while (bb.position() < limit) {
            int b1 = bb.get() & 0xFF;
            if (b1 < 0x80) {
                ++na;
            } else if (b1 == 0x80) {
                ++b1Bad;
            } else if (b1 < 0xA0) {
                int b2 = (bb.position() < limit) ? bb.get() & 0xFF : -1;
                if (isShiftJISByte2(b2))
                    ++nd;
                else
                    ++b2Bad;
            } else if (b1 == 0xA0) {
                ++b1Bad;
            } else if (b1 < 0xE0) {
                ++nk;
            } else if (b1 < 0xF0) {
                int b2 = (bb.position() < limit) ? bb.get() & 0xFF : -1;
                if (isShiftJISByte2(b2))
                    ++nd;
                else
                    ++b2Bad;
            } else {
                ++b1Bad;
            }
        }
        bb.position(restore);
        if ((b1Bad > 0) || (b2Bad > 0)) {
            // if any bad bytes, fail
            return null;
        } else if (nd > 0) {
            // if any double byte characters, succeed
            return sjisEncoding;
        } else if (nk > 0) {
            // if any half width kana characters, succeed
            return sjisEncoding;
        } else if (na > 0) {
            return asciiEncoding;
        } else {
            return null;
        }
    }

    private static boolean isShiftJISByte2(int b) {
        if (b < 0)
            return false;
        else if (b < 0x40)
            return false;
        else if (b < 0x7F)
            return true;
        else if (b == 0x7F)
            return false;
        else if (b < 0xFD)
            return true;
        else
            return false;
    }

    private static Charset sniffUTF8DisregardingBOM(ByteBuffer bb, boolean allowModifiedUTF8, Object[] outputParameters) {
        int restore = bb.position();
        int limit = bb.limit();
        int na = 0;     // number of ascii
        int nn = 0;     // number of non-ascii
        int ns = 0;     // number of surrogates (directly encoded low- or high-surrogate)
        int no = 0;     // number of out of range (greater than maximum code point)
        int nz = 0;     // number of zero encodings (using 2-byte form of modified utf-8)
        int nl = 0;     // number of non-zero long encodings (using > minimum number of bytes)
        int b1Bad = 0;  // number of bad 1st bytes
        int bXBad = 0;  // number of bad following bytes
        while (bb.position() < limit) {
            int c  = 0; // encoded unicode code point
            int ps = 0; // perform sync if not zero
            int nf = 0; // number of following bytes
            int b1 = bb.get() & 0xFF;
            if (b1 < 0x80) {
                c = b1 & 0x7F;
                nf = 0;
            } else if (b1 < 0xC0) {
                ps = 1;
            } else if (b1 < 0xE0) {
                c = b1 & 0x1F;
                nf = 1;
            } else if (b1 < 0xF0) {
                c = b1 & 0x0F;
                nf = 2;
            } else if (b1 < 0xF8) {
                c = b1 & 0x07;
                nf = 3;
            } else if (b1 < 0xFC) {
                c = b1 & 0x03;
                nf = 4;
            } else if (b1 < 0xFE) {
                c = b1 & 0x01;
                nf = 5;
            } else {
                ps = 1;
            }
            if (ps != 0) {
                ++b1Bad;
                bb.position(findUTF8FirstByte(bb));
                ps = 0;
                continue;
            }
            for (int k = nf; (ps == 0) && (k > 0) && (bb.position() < limit); --k) {
                int bX = bb.get() & 0xFF;
                if ((bX < 0x80) || (bX >= 0xC0))
                    ps = 1;
                else
                    c = (c << 6) | (bX & 0x3F);
            }
            if (ps != 0) {
                ++bXBad;
                bb.position(findUTF8FirstByte(bb));
                ps = 0;
                continue;
            }
            if (c < 0x80) {
                ++na;
                if (nf > 1) {
                    if (c == 0)
                        ++nz;
                    else
                        ++nl;
                }
            } else if (c < 0x07FF) {
                ++nn;
                if (nf > 2)
                    ++nl;
            } else if (c < 0xD800) {
                ++nn;
                if (nf > 3)
                    ++nl;
            } else if (c <= 0xDF00) {
                ++ns;
                if (nf > 3)
                    ++nl;
            } else if (c < 0xFFFF) {
                ++nn;
                if (nf > 3)
                    ++nl;
            } else if (c <= 0x10FFFF) {
                ++nn;
                if (nf > 4)
                    ++nl;
            } else {
                ++no;
            }
        }
        bb.position(restore);
        if ((b1Bad > 0) || (bXBad > 0)) {
            // if any bad bytes, fail
            return null;
        } else if (no > 0) {
            // if any out of range code points, fail
            return null;
        } else if (ns > 0) {
            // if any surrogate code points, fail
            return null;
        } else if (nl > 0) {
            // if any non-zero (over) long encodings, fail
            return null;
        } else if ((nz > 0) && !allowModifiedUTF8) {
            // if any zero (over) long encoding and modified utf-8 is not allowed, fail
            return null;
        } else if (nn > 0) {
            return utf8Encoding;
        } else if (na > 0) {
            return asciiEncoding;
        } else {
            // if no non-asii and no ascii characters, fail
            return null;
        }
    }

    private static int findUTF8FirstByte(ByteBuffer bb) {
        int position = bb.position();
        int limit = bb.limit();
        while (position < limit) {
            int b = bb.get(position) & 0xFF;
            if (b < 0x80)
                break;
            else if (b < 0xC0)
                ++position;
            else if (b < 0xFE)
                break;
            else
                ++position;
        }
        return position;
    }

    private boolean parseResource() {
        boolean fail = false;
        Reporter reporter = getReporter();
        reporter.logInfo(reporter.message("i.011", "Parsing resource ..."));
        try {
            BufferedReader r = new BufferedReader(new CharArrayReader(getCharArray(resourceBuffer)));
            String line;
            int lineNumber = 0;
            LocatorImpl locator = new LocatorImpl();
            locator.setSystemId(resourceUriString);
            while ((line = r.readLine()) != null) {
                locator.setLineNumber(++lineNumber);
                if (lineNumber == 1) {
                    if (!parseHeaderLine(line, locator)) {
                        reporter.logInfo(reporter.message(locator, "i.012", "Skipping remainder of resource due to bad header."));
                        fail = true;
                        break;
                    }
                } else if (!line.isEmpty()) {
                    if (!parseContentLine(line, locator)) {
                        reporter.logError(reporter.message(locator, "e.006", "Content line parse failure."));
                        fail = true;
                    }
                }
            }
            reporter.logInfo(reporter.message("i.013", "Read {0} lines.", lineNumber));
            if (lineNumber == 0) {
                if (reporter.isWarningEnabled("empty-input")) {
                    if (reporter.logWarning(reporter.message(locator, "w.011", "Empty input resource (no lines).")))
                        fail = true;
                }
            }
        } catch (Exception e) {
            reporter.logError(e);
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private char[] getCharArray(CharBuffer cb) {
        cb.rewind();
        if (cb.hasArray()) {
            return cb.array();
        } else {
            char[] chars = new char[cb.limit()];
            cb.get(chars);
            return chars;
        }
    }

    private boolean parseHeaderLine(String line, LocatorImpl locator) {
        boolean fail = false;
        int lineLength = line.length();
        final int minHeaderLength = 10;
        if (lineLength < minHeaderLength) {
            if (reporter.isWarningEnabled("bad-header-length")) {
                if (reporter.logWarning(reporter.message(locator, "w.001", "Header too short, got length {0}, expected {1}.", lineLength, minHeaderLength)))
                    fail = true;
            }
        }
        if (fail)
            return !fail;
        String[] fields = line.split("\\t+");
        final int minFieldCount = 3;
        if (fields.length != minFieldCount) {
            Message message = reporter.message(locator, "w.002", "Header bad field count, got {0}, expected {1}.", fields.length, minFieldCount);
            if (reporter.isWarningEnabled("bad-header-field-count")) {
                if (reporter.logWarning(message))
                    fail = true;
            }
        }
        if (fail)
            return !fail;
        if (fields.length < 1) {
            if (reporter.isWarningEnabled("bad-header-preamble")) {
                Message message = reporter.message(locator, "w.003", "Header preamble field missing.");
                if (reporter.logWarning(message))
                    fail = true;
            }
        }
        if (fail)
            return !fail;
        if (fields.length > 0) {
            final String preambleExpected = "Lambda字幕V4";
            String preamble = fields[0];
            if (!preamble.equals(preambleExpected)) {
                reporter.logDebug(reporter.message("d.001", "''{0}'' != ''{1}''", dump(preamble), dump(preambleExpected)));
                if (reporter.isWarningEnabled("bad-header-preamble")) {
                    Message message = reporter.message(locator, "w.004", "Header preamble field invalid, got ''{0}'', expected ''{1}''.", preamble, preambleExpected);
                    if (reporter.logWarning(message))
                        fail = true;
                }
            }
        }
        if (fail)
            return !fail;
        if (fields.length < 2) {
            if (reporter.isWarningEnabled("bad-header-drop-flags")) {
                Message message = reporter.message(locator, "w.005", "Drop flags field missing.");
                if (reporter.logWarning(message))
                    fail = true;
            }
        }
        if (fail)
            return !fail;
        if (fields.length > 1) {
            final String dropFlagsTemplate = "DF0+0";
            final int minDropFlagsLength = dropFlagsTemplate.length();
            final String dropFlagsPrefixExpected = dropFlagsTemplate.substring(0, 2);
            String dropFlags = fields[1];
            int dropFlagsLength = dropFlags.length();
            if (dropFlagsLength < minDropFlagsLength) {
                if (reporter.isWarningEnabled("bad-header-drop-flags")) {
                    Message message =
                        reporter.message(locator, "w.006", "Header drop flags field too short, got ''{0}'', expected ''{1}''.", dropFlagsLength, minDropFlagsLength);
                    if (reporter.logWarning(message))
                        fail = true;
                }
            }
            if (fail)
                return !fail;
            if (!dropFlags.startsWith(dropFlagsPrefixExpected)) {
                reporter.logDebug(reporter.message("d.002", "prefix(''{0}'') != ''{1}''", dump(dropFlags), dump(dropFlagsPrefixExpected)));
                if (reporter.isWarningEnabled("bad-header-drop-flags")) {
                    Message message =
                        reporter.message(locator, "w.007", "Header drop flags field invalid, got ''{0}'', should start with ''{1}''.", dropFlags, dropFlagsPrefixExpected);
                    if (reporter.logWarning(message))
                        fail = true;
                }
            }
            if (fail)
                return !fail;
            String dropFlagsArgument = dropFlags.substring(2, 3);
            if (!dropFlagsArgument.equals("0") && !dropFlagsArgument.equals("1")) {
                reporter.logDebug(reporter.message("d.003", "argument(''{0}'') == ''{1}''", dump(dropFlags), dump(dropFlagsArgument)));
                if (reporter.isWarningEnabled("bad-header-drop-flags")) {
                    Message message =
                        reporter.message(locator, "w.008", "Header drop flags field argument invalid, got ''{0}'', expected ''0'' or ''1''.", dropFlagsArgument);
                    if (reporter.logWarning(message))
                        fail = true;
                }
            }
        }
        if (fields.length < 3) {
            if (reporter.isWarningEnabled("bad-header-scene-standard")) {
                Message message = reporter.message(locator, "w.009", "Scene standard field missing.");
                if (reporter.logWarning(message))
                    fail = true;
            }
        }
        if (fail)
            return !fail;
        return !fail;
    }

    /**
     * Parse content line. If successful, create a Screen object and
     * add it to the list of screens in the per-resource parsing state.
     *
     * @param line to parse
     * @param locator locator for line
     * @return boolean indicating if parse was successful
     */
    private boolean parseContentLine(String line, LocatorImpl locator) {
        boolean fail = false;
        int[][] types = new int[1][];
        String[] parts = splitContentLine(line, types);
        int partCount = parts.length;
        int partIndexNext = 0;
        Screen s = new Screen(locator, getLastScreenNumber());
        // screen part - (separator* screen)?
        if (!fail) {
            int partIndex;
            partIndexNext = maybeSkipSeparators(parts, partIndexNext);
            if ((partIndex = hasScreenField(parts, partIndexNext)) >= 0) {
                if (parseScreenField(parts[partIndex], s) == s)
                    partIndexNext = partIndex + 1;
                else
                    fail = true;
            }
        }
        // time field - (separator* time)?
        if (!fail) {
            int partIndex;
            partIndexNext = maybeSkipSeparators(parts, partIndexNext);
            if ((partIndex = hasTimeField(parts, partIndexNext)) >= 0) {
                if (parseTimeField(parts[partIndex], s) == s)
                    partIndexNext = partIndex + 1;
                else
                    fail = true;
            }
        }
        // text fields - (separator* text)*
        if (!fail) {
            StringBuffer sb = new StringBuffer();
            int lastTextPartIndex = -1;
            for (int i = partIndexNext, j, n = partCount; i < n; ++i) {
                if (parts[i].length() == 0) {
                    continue;
                } else {
                    i = maybeSkipSeparators(parts, i);
                    if ((j = hasTextField(locator, parts, i, NonTextAttributeTreatment.Warning)) >= 0) {
                        // if this isn't the first (non-empty) text field, then insert encoded preceding separator text
                        if (sb.length() > 0) {
                            if (i > 0) {
                                String p = parts[i - 1];
                                String t = parseText(p, false);
                                if (t != null)
                                    sb.append(t);
                                else
                                    throw new IllegalStateException(getReporter().message("x.021", "unexpected text field parse state: part {0}", p).toText());
                            }
                        }
                        sb.append(parts[i]);
                        lastTextPartIndex = j;
                    } else
                        break;
                }
            }
            if (sb.length() > 0) {
                if (parseTextField(sb.toString(), s) == s)
                    partIndexNext = lastTextPartIndex + 1;
                else
                    fail = true;
            }
            // reset text attribute parse state
            inTextAttribute = false;
        }
        // attribute fields - (separator* attribute)*
        if (!fail) {
            while ((partIndexNext < partCount) && !fail) {
                int partIndex;
                partIndexNext = maybeSkipSeparators(parts, partIndexNext);
                if ((partIndex = hasAttributeField(parts, partIndexNext)) >= 0) {
                    if (parseAttributeField(parts[partIndex], s) == s)
                        partIndexNext = partIndex + 1;
                    else
                        fail = true;
                } else
                    break;
            }
        }
        // retain screen if no failure
        if (!fail) {
            if (!s.empty())
                screens.add(s);
        }
        return !fail;
    }

    public enum PartType {
        SEPARATOR,
        FIELD;
    }

    /**
     * Separate content line into sequence of parts, where each part is either a
     * separator part (consisting wholly of separator characters) or a field part
     * (containing no separator character). Separator characters are <tt>U+0009
     * (HORIZONTAL TAB)</tt> and <tt>U+0020 (SPACE)</tt>.
     * 
     * @param line string containing entire line
     * @param retTypes receives types array as output parameter, one type for each part
     * @return (possibly empty) array of parts
     */
    private String[] splitContentLine(String line, int[][] retTypes) {
        List<String> parts = new java.util.ArrayList<String>();
        List<Integer> types = new java.util.ArrayList<Integer>();
        StringBuffer sb = new StringBuffer();
        boolean inSeparator = false;
        for (int i = 0, n = line.length(); i < n; ++i) {
            char c = line.charAt(i);
            boolean isSeparator = isContentFieldSeparator(c);
            if (isSeparator ^ inSeparator) {
                if (sb.length() > 0) {
                    parts.add(sb.toString());
                    sb.setLength(0);
                    types.add(inSeparator ? PartType.SEPARATOR.ordinal() : PartType.FIELD.ordinal());
                }
            }
            sb.append(c);
            inSeparator = isSeparator;
        }
        if (sb.length() > 0) {
            parts.add(sb.toString());
            sb.setLength(0);
            types.add(inSeparator ? PartType.SEPARATOR.ordinal() : PartType.FIELD.ordinal());
        }
        if ((retTypes != null) && (retTypes.length > 0)) {
            int[] ta = new int[types.size()];
            for (int i = 0, n = ta.length; i < n; ++i)
                ta[i] = types.get(i);
            retTypes[0] = ta;
        }
        return parts.toArray(new String[parts.size()]);
    }

    /**
     * Skip separator parts starting from <i>partIndex</i>, returning
     * index of first part encountered that is not empty or a separator part.
     *
     * @param parts array of parts
     * @param partIndex first part index to start skipping from
     * @return index of first part that is not empty or a separator part
     */
    private int maybeSkipSeparators(String[] parts, int partIndex) {
        assert parts != null;
        assert partIndex >= 0;
        while ((partIndex < parts.length) && isEmptyOrContentFieldSeparator(parts[partIndex]))
            ++partIndex;
        return partIndex;
    }

    private boolean isEmptyOrContentFieldSeparator(String s) {
        if (isNullOrEmpty(s))
            return true;
        else if (isContentFieldSeparator(s))
            return true;
        else
            return false;
    }

    private boolean isNullOrEmpty(String s) {
        return (s == null) || s.isEmpty();
    }

    private boolean isContentFieldSeparator(String s) {
        assert s != null;
        assert !s.isEmpty();
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (!isContentFieldSeparator(c))
                return false;
        }
        return true;
    }

    private boolean isContentFieldSeparator(char c) {
        if (c == '\u0020')
            return true;
        else if (c == '\u0009')
            return true;
        else
            return false;
    }

    private int getLastScreenNumber() {
        if (screens.isEmpty())
            return 0;
        else {
            for (int i = screens.size(); i > 0; --i) {
                int k = i - 1;
                Screen s = screens.get(k);
                if (s.getNumber() > 0)
                    return s.getNumber();
            }
            return 0;
        }
    }

    private static int hasScreenField(String[] parts, int partIndex) {
        if ((partIndex == 0) && (partIndex < parts.length)) {
            if (isScreenField(parts[partIndex]))
                return partIndex;
        }
        return -1;
    }

    private static boolean isScreenField(String field) {
        int i = 0;
        int n = field.length();
        if (n == 0)
            return false;
        while (i < n) {
            char c = field.charAt(i);
            if (isScreenDigit(c))
                ++i;
            else
                break;
        }
        if (i < n) {
            char c = field.charAt(i);
            if (isScreenLetter(c))
                ++i;
        }
        return i == n;
    }

    private static Screen parseScreenField(String field, Screen s) {
        StringBuffer count = new StringBuffer();
        StringBuffer letter = new StringBuffer();
        int i = 0;
        int n = field.length();
        while (i < n) {
            char c = field.charAt(i);
            if (isScreenDigit(c)) {
                count.append((char) toASCIIDigit(c));
                ++i;
            } else
                break;
        }
        if (i < n) {
            char c = field.charAt(i);
            if (isScreenLetter(c)) {
                letter.append((char) toASCIIUpper(c));
                ++i;
            }
        }
        if (i == n) {
            try {
                s.setNumber(Integer.parseInt(count.toString()));
                s.setLetter((letter.length() == 1) ? letter.charAt(0) : 0);
            } catch (NumberFormatException e) {
                s = null;
            }
        } else
            s = null;
        return s;
    }

    private static boolean isScreenDigit(int c) {
        return isCountDigit(c);
    }

    private static boolean isScreenLetter(int c) {
        if ((c >= 'A') && (c <= 'I'))
            return true;
        else if ((c >= 'a') && (c <= 'i'))
            return true;
        else if ((c >= '\uFF21') && (c <= '\uFF29'))
            return true;
        else if ((c >= '\uFF41') && (c <= '\uFF49'))
            return true;
        else
            return false;
    }

    private static int hasTimeField(String[] parts, int partIndex) {
        if (partIndex < parts.length) {
            if (isTimeField(parts[partIndex]))
                return partIndex;
        }
        return -1;
    }

    private static boolean isTimeField(String field) {
        int i = 0;
        int n = field.length();
        if (n == 0)
            return false;
        int s = i;
        while (i < n) {
            char c = field.charAt(i);
            if (isTimeDigit(c))
                ++i;
            else
                break;
        }
        if ((i - s) != 8)
            return false;
        s = i;
        if (i < n) {
            char c = field.charAt(i);
            if ((c == '/') || (c == '\uFF0F')) {
                ++i;
            }
        }
        if ((i - s) != 1)
            return false;
        s = i;
        while (i < n) {
            char c = field.charAt(i);
            if (isTimeDigit(c))
                ++i;
            else
                break;
        }
        if ((i - s) != 8)
            return false;
        return i == n;
    }

    private static Screen parseTimeField(String field, Screen s) {
        StringBuffer ic = new StringBuffer();
        int i = 0;
        int n = field.length();
        int j = i;
        while (i < n) {
            char c = field.charAt(i);
            if (isTimeDigit(c)) {
                ic.append((char) toASCIIDigit(c));
                ++i;
            } else
                break;
        }
        if ((i - j) != 8)
            return null;
        j = i;
        if (i < n) {
            char c = field.charAt(i);
            if ((c == '/') || (c == '\uFF0F'))
                ++i;
        }
        if ((i - j) != 1)
            return null;
        j = i;
        StringBuffer oc = new StringBuffer();
        while (i < n) {
            char c = field.charAt(i);
            if (isTimeDigit(c)) {
                oc.append((char) toASCIIDigit(c));
                ++i;
            } else
                break;
        }
        if ((i - j) != 8)
            return null;
        s.setInTime(parseTimeCode(ic.toString()));
        s.setOutTime(parseTimeCode(oc.toString()));
        return s;
    }

    private static final String timePatternString = "(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
    private static final Pattern timePattern = Pattern.compile(timePatternString);
    private static final ClockTime timeZero = ClockTimeImpl.ZERO;
    private static ClockTime parseTimeCode(String code) {
        Matcher m = timePattern.matcher(code);
        if (m.matches()) {
            String hh = m.group(1);
            String mm = m.group(2);
            String ss = m.group(3);
            String ff = m.group(4);
            return new ClockTimeImpl(hh, mm, ss, ff, null);
        } else
            return timeZero;
    }

    private static boolean isTimeDigit(int c) {
        return isCountDigit(c);
    }

    private int hasTextField(LocatorImpl locator, String[] parts, int partIndex, NonTextAttributeTreatment nonTextAttributeTreatment) {
        while (partIndex < parts.length) {
            String field = parts[partIndex];
            if (field.length() == 0) {
                return -1;
            } else {
                int[] delims    = countTextAttributeDelimiters(field);  // count text attribute starts and ends
                int   ntas      = delims[0];                            // # of text attribute starts
                int   ntae      = delims[1];                            // # of text attribute ends
                if (inTextAttribute) {
                    if ((ntae > 0) && (ntas < ntae))
                        inTextAttribute = false;
                    return partIndex;
                } else if (isTextField(locator, field, partIndex, nonTextAttributeTreatment)) {
                    return partIndex;
                } else if ((ntas > 0) && (ntas > ntae)) {
                    inTextAttribute = true;
                    return partIndex;
                } else
                    return -1;
            }
        }
        return -1;
    }

    private boolean isTextField(LocatorImpl locator, String field, int partIndex, NonTextAttributeTreatment nonTextAttributeTreatment) {
        if (field.length() == 0)
            return false;
        else {
            String[] parts = splitTextField(field);
            int numParts = parts.length;
            int numNonTextAttributes = 0;
            for (int i = 0, n = numParts; i < n; ++i) {
                String part = parts[i];
                if (isNonTextAttribute(part))
                    ++numNonTextAttributes;
            }
            // if all parts are non-text-attributes, then always fail
            if (numNonTextAttributes == numParts)
                return false;
            for (int i = 0, n = numParts; i < n; ++i) {
                String part = parts[i];
                if (isTextEscape(part))
                    continue;
                else if (isTextAttributeStart(part))
                    continue;
                else if (isTextAttributeEnd(part))
                    continue;
                else if (isNonTextAttribute(part)) {
                    if (nonTextAttributeTreatment == NonTextAttributeTreatment.Ignore)
                        continue;
                    else if (nonTextAttributeTreatment == NonTextAttributeTreatment.Fail)
                        return false;
                    else {
                        Reporter reporter = getReporter();
                        Message m = reporter.message(locator, "w.010",
                            "Field {0}, part {1} contains a non-text attribute ''{2}''. Is a field separator missing?", partIndex + 1, i + 1, part);
                        if (nonTextAttributeTreatment == NonTextAttributeTreatment.Info) {
                            reporter.logInfo(m);
                            continue;
                        } if (nonTextAttributeTreatment == NonTextAttributeTreatment.Warning) {
                            if (reporter.isWarningEnabled("non-text-attribute-in-text-field")) {
                                if (reporter.logWarning(m))
                                    return false;
                            }
                        } else if (nonTextAttributeTreatment == NonTextAttributeTreatment.Warning) {
                            reporter.logError(m);
                            return false;
                        }
                    }
                    continue;
                } else if (isText(part)) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    private static final char attributePrefix           = '\uFF20';   // U+FF20 FULLWIDTH COMMERCIAL AT '＠'
    private static final char attributeTextStart        = '\uFF3B';   // U+FF3B FULLWIDTH OPEN SQUARE BRACKET
    private static final char attributeTextEnd          = '\uFF3D';   // U+FF3D FULLWIDTH CLOSE SQUARE BRACKET
    private enum SplitTextState {
        Text,
        AttrStart,
        AttrEnd;
    };
    private final String[] splitTextField(String field) {
        List<String> parts = new java.util.ArrayList<String>();
        SplitTextState s = SplitTextState.Text;
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = field.length(); i < n; ++i) {
            char c = field.charAt(i);
            if (c == attributePrefix) {
                if (s == SplitTextState.Text) {
                    // emit text if accumulator is non-empty
                    if (sb.length() > 0) {
                        parts.add(sb.toString());
                        sb.setLength(0);
                    }
                    sb.append(c);
                    s = SplitTextState.AttrStart;
                } else if (s == SplitTextState.AttrStart) {
                    if (sb.length() == 2) {
                        // emit text escape
                        sb.append(c);
                        parts.add(sb.toString());
                        // reset state
                        sb.setLength(0);
                        s = SplitTextState.Text;
                    } else {
                        throw new IllegalArgumentException(getReporter().message("x.022",
                            "''{0}'' can only appear in attribute start when attribute is a text escape", c).toText());
                    }
                } else /* if (s == SplitTextState.AttrEnd) */ {
                    // emit attribute end
                    sb.append(c);
                    parts.add(sb.toString());
                    // reset state
                    sb.setLength(0);
                    s = SplitTextState.Text;
                }
            } else if (c == attributeTextStart) {
                if (s == SplitTextState.Text) {
                    sb.append(c);
                } else if (s == SplitTextState.AttrStart) {
                    // emit attribute start
                    sb.append(c);
                    parts.add(sb.toString());
                    // reset state
                    sb.setLength(0);
                    s = SplitTextState.Text;
                } else /* if (s == SplitTextState.AttrEnd) */ {
                    throw new IllegalArgumentException(getReporter().message("x.023",
                        "''{0}'' not permitted in attribute end", c).toText());
                }
            } else if (c == attributeTextEnd) {
                if (s == SplitTextState.Text) {
                    if (((i + 1) < n) && (field.charAt(i + 1) == attributePrefix)) {
                        // emit text if accumulator is non-empty
                        if (sb.length() > 0) {
                            parts.add(sb.toString());
                            sb.setLength(0);
                        }
                        sb.append(c);
                        s = SplitTextState.AttrEnd;
                    } else
                        sb.append(c);
                } else if (s == SplitTextState.AttrStart) {
                    throw new IllegalArgumentException(getReporter().message("x.024",
                        "''{0}'' not permitted in attribute start", c).toText());
                } else /* if (s == SplitTextState.AttrEnd) */ {
                    throw new IllegalStateException();
                }
            } else {
                if (s == SplitTextState.Text) {
                    sb.append(c);
                } else if (s == SplitTextState.AttrStart) {
                    sb.append(c);
                } else /* if (s == SplitTextState.AttrEnd) */ {
                    throw new IllegalStateException();
                }
            }
        }
        if (sb.length() > 0)
            parts.add(sb.toString());
        return parts.toArray(new String[parts.size()]);
    }

    private static int[] countTextAttributeDelimiters(String s) {
        int ns = 0;
        int ne = 0;
        for (int i = 0, n = s.length(); i < n; ++i) {
            String t = s.substring(i);
            if (beginsWithTextAttributeStart(t))
                ++ns;
            if (beginsWithTextAttributeEnd(t))
                ++ne;
        }
        return new int[] { ns, ne };
    }

    private static boolean beginsWithTextAttributeStart(String s) {
        return beginsWithTextAttributeStart(s, false);
    }

    private static final String textAttributeStart = new String(new char[]{attributePrefix});
    private static boolean beginsWithTextAttributeStart(String s, boolean ignoreLeadingWhitespace) {
        if (ignoreLeadingWhitespace)
            s = trimLeadingWhitespace(s);
        if (!s.startsWith(textAttributeStart))
            return false;
        else {
            StringBuffer sb = new StringBuffer();
            int i = 1;
            int n = s.length();
            while (i < n) {
                char c = s.charAt(i);
                if ((c >= '\uFF10') && (c <= '\uFF19'))
                    break;
                else if (c == '\uFF01')
                    break;
                else if (c == '\uFF20')                 // never part of attribute start
                    break;
                else if (c == '\uFF3B')
                    break;
                else if (c == '\uFF5C')                 // never part of attribute start
                    break;
                else if (c == '\uFF3D')                 // never part of attribute start
                    break;
                else {
                    sb.append(c);
                    ++i;
                }
            }
            if (!knownAttributes.containsKey(sb.toString()))
                return false;
            while (i < n) {
                char c = s.charAt(i);
                if ((c < '\uFF10') || (c > '\uFF19'))
                    break;
                else
                    ++i;
            }
            if (i < n)
                return s.charAt(i) == '\uFF3B';         // text attribute start terminates with U+FF3B
            else
                return false;                           // otherwise, this is not the start of a text attribute
        }
    }

    private static String trimLeadingWhitespace(String s) {
        int i = 0;
        int n = s.length();
        for (; i < n; ++i) {
            char c = s.charAt(i);
            if (c == '\u0009')
                continue;
            else if (c == '\u0020')
                continue;
            else
                break;
        }
        return (i > 0) ? s.substring(i) : s;
    }

    private static boolean beginsWithTextAttributeEnd(String s) {
        return beginsWithTextAttributeEnd(s, false);
    }

    private static final String textAttributeEnd = new String(new char[]{'\uFF3D', attributePrefix});
    private static boolean beginsWithTextAttributeEnd(String s, boolean ignoreLeadingWhitespace) {
        if (ignoreLeadingWhitespace)
            s = trimLeadingWhitespace(s);
        return s.startsWith(textAttributeEnd);
    }

    private static boolean isTextEscape(String text) {
        int i = 0;
        int n = text.length();
        if (i < n) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                i++;
            else
                return false;
        }
        if (i < n) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                ;
            else if ((c == '\u005F') || (c == '\uFF3F'))
                i++;
            else
                return false;
        }
        if (i < n) {
            char c = text.charAt(i);
            if (c != attributePrefix)
                i++;
            else
                return false;
        }
        return true;
    }

    private static boolean isText(String text) {
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                return false;
            else if (c == '\t')
                return false;
        }
        return true;
    }

    private Screen parseTextField(String field, Screen s) {
        if (field.length() == 0)
            return null;
        else {
            StringBuffer sb = new StringBuffer();
            Stack<AnnotatedRange> ranges = new Stack<AnnotatedRange>();
            List<AnnotatedRange> resolvedRanges = new java.util.ArrayList<AnnotatedRange>();
            Attribute[] ra = new Attribute[1];
            for (String part : splitTextField(field)) {
                String t;
                if ((t = parseTextEscape(part)) != null) {
                    sb.append(t);
                } else if ((t = parseTextAttributeStart(part, ra)) != null) {
                    int start = sb.length();
                    ranges.push(new AnnotatedRange(new Annotation(ra[0]), start, -1));
                } else if ((t = parseTextAttributeEnd(part, ra)) != null) {
                    int end = sb.length();
                    if (!ranges.empty()) {
                        AnnotatedRange r = ranges.pop();
                        r.end = end;
                        resolvedRanges.add(r);
                    } else {
                        throw new IllegalStateException(getReporter().message("x.025", "unexpected text attribute end").toText());
                    }
                } else if (isNonTextAttribute(part)) {
                    continue;
                } else if ((t = parseTextAttributeText(part, ranges)) != null) {
                    sb.append(t);
                } else {
                    throw new IllegalStateException(getReporter().message("x.021", "unexpected text field parse state: part {0}", part).toText());
                }
            }
            if (sb.length() > 0) {
                AttributedString as = new AttributedString(sb.toString());
                for (AnnotatedRange r : resolvedRanges) {
                    Annotation          annotation      = r.annotation;
                    Attribute           a               = (Attribute) annotation.getValue();
                    TextAttribute       ta              = a.getSpecification().getTextAttribute();
                    if (ta != null) {
                        as.addAttribute(ta, a, r.start, r.end);
                    }
                }
                s.setText(as);
            }
            return s;
        }
    }

    /*
    ** Parse a text escape, which is one of the following character sequences:
    ** <tt>{0xFF20, 0xFF20, 0xFF20}</tt>, <tt>{0xFF20, 0x005F, 0xFF20}</tt>, <tt>{0xFF20, 0xFF3F, 0xFF20}</tt>.
    ** <p>
    ** Returns the escaped text or <tt>null</tt> if the input text is not one of the above sequences.
    ** 
    ** @param text string possibly containing a text escape
    ** @return unescaped text or null (if not a text escape sequence)
    */
    private static String parseTextEscape(String text) {
        int i = 0;
        int n = text.length();
        if (n == 0)
            return null;
        if (i < n) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                i++;
            else
                return null;
        }
        StringBuffer sb = new StringBuffer();
        if (i < n) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                sb.append(c);
            else if ((c == '\u005F') || (c == '\uFF3F')) {
                sb.append(c);
                i++;
            } else
                return null;
        }
        if (i < n) {
            char c = text.charAt(i);
            if (c != attributePrefix)
                i++;
            else
                return null;
        }
        return sb.toString();
    }

    private static String parseTextAttributeStart(String text, Attribute[] retAttr) {
        Attribute a = parseTextAttributeStart(text);
        if (a != null) {
            if (retAttr != null)
                retAttr[0] = a;
            return new String();
        } else
            return null;
    }

    private static String parseTextAttributeEnd(String text, Attribute[] retAttr) {
        Attribute a = parseTextAttributeEnd(text);
        if (a != null) {
            if (retAttr != null)
                retAttr[0] = a;
            return new String();
        } else
            return null;
    }

    private static String parseTextAttributeText(String text, Stack<AnnotatedRange> ranges) {
        if (!ranges.empty()) {
            AnnotatedRange range = ranges.peek();
            Attribute a = range.getAttribute();
            if ((a != null) && a.isRuby()) {
                return parseRubyAttributeText(text, a);
            } else {
                return parseText(text, true);
            }
        } else {
            return parseText(text, true);
        }
    }

    private static String parseRubyAttributeText(String text, Attribute a) {
        if (parseTextAttributeRubyText(text, a) == a)
            return parseText(a.getText(), false);
        else
            return null;
    }
    
    public static String parseText(String text, boolean mapInitialSpaceToNBSP) {
        int escapedSpaces = 0;
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                return null;
            else if (c == '\t')
                ++escapedSpaces;
            else if ((c == '\u005F') || (c == '\uFF3F'))
                ++escapedSpaces;
        }
        if (escapedSpaces == 0)
            return text;
        StringBuffer sb = new StringBuffer(text.length());
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
            if ( c == '\t')
                c = '\u005F';
            if (c == '\u005F')
                c = (mapInitialSpaceToNBSP && (sb.length() == 0)) ? '\u00A0' : '\u0020';
            else if (c == '\uFF3F')
                c = '\u3000';
            sb.append(c);
        }
        return sb.toString();
    }

    private static int hasAttributeField(String[] parts, int partIndex) {
        if (partIndex < parts.length) {
            if (isAttributeField(parts[partIndex]))
                return partIndex;
        }
        return -1;
    }

    private static boolean isAttributeField(String field) {
        return parseAttributes(field, AttrContext.Attribute) != null;
    }

    private static Screen parseAttributeField(String field, Screen s) {
        for (Attribute a : parseAttributes(field, AttrContext.Attribute)) {
            s.addAttribute(a);
        }
        return s;
    }

    private static boolean isTextAttributeStart(String field) {
        return parseTextAttributeStart(field) != null;
    }

    private static boolean isTextAttributeEnd(String field) {
        return parseTextAttributeEnd(field) != null;
    }

    private static boolean isNonTextAttribute(String field) {
        return parseNonTextAttribute(field) != null;
    }

    private static final String attributeSeparatorPatternString = "[\u0020\u3000]+";
    private static Attribute[] parseAttributes(String field, AttrContext context) {
        if (!field.startsWith("\uFF20") || (field.length() < 2))
            return null;
        else {
            List<Attribute> attributes = new java.util.ArrayList<Attribute>();
            for (String attribute : field.split(attributeSeparatorPatternString)) {
                Attribute a = parseAttribute(attribute);
                if (a != null) {
                    if ((context == AttrContext.Attribute) && (a.getSpecification().getContext() == AttrContext.Text)) {
                        // text attribute in non-text attribute context
                        return null;
                    } else if ((context == AttrContext.Text) && (a.getSpecification().getContext() == AttrContext.Attribute)) {
                        // non-text attribute in text attribute context
                        return null;
                    } else
                        attributes.add(a);
                } else
                    return null;
            }
            return attributes.toArray(new Attribute[attributes.size()]);
        }
    }

    private static Attribute parseAttribute(String attribute) {
        Attribute a;
        if ((a = parseTextAttribute(attribute)) != null)
            return a;
        else if ((a = parseNonTextAttribute(attribute)) != null)
            return a;
        else
            return null;
    }

    private static final String taDelim         = "\\uFF20";
    private static final String taTextStart     = "\\uFF3B";
    private static final String taTextEnd       = "\\uFF3D";
    private static final String taTextSep       = "\\uFF5C";
    private static final String ncDigits        = "\\p{Digit}\\uFF10-\\uFF19";
    private static final String ncPunct         = "\\uFF01\\uFF20\\uFF3B\\uFF3D";
    private static final String ncTextPunct     = "\\uFF20\\uFF3D\\uFF5C";
    private static final String ncWhite         = "\\u0009\\u000A\\u000D\\u0020\\u3000";
    private static final String ncTextWhite     = "\\u0009";
    private static final String ncAttrName      = "[^" + ncWhite + ncDigits + ncPunct + "]";
    private static final String ncCount         = "[" + ncDigits + "]";
    private static final String ncText          = "[^" + ncTextWhite + ncTextPunct + "]";
    private static final String ngAttrName      = "(" + ncAttrName + "+" + ")";
    private static final String ngOptCount      = "(" + ncCount + "+" + ")?";
    private static final String ngText          = "(" + ncText + "+" + ")";
    private static final String ngOptText       = "(" + taTextStart + ngText + "(" + taTextSep + ngText + ")?" + taTextEnd + ")?";
    private static final String taPatternString = taDelim + ngAttrName + ngOptCount + ngOptText + taDelim;
    private static final Pattern taPattern      = Pattern.compile(taPatternString);

    private static Attribute parseTextAttribute(String attribute) {
        Matcher m = taPattern.matcher(attribute);
        if (m.matches()) {
            String name = m.group(1);
            AttributeSpecification as = knownAttributes.get(name);
            if (as == null)
                return null;
            else {
                String[] groups = new String[m.groupCount() + 1];
                for (int i = 1; i < groups.length; ++i) {
                    groups[i] = m.group(i);
                }
                return new Attribute(as, parseCount(m.group(2)), false);
            }
        } else
            return null;
    }

    private static final String taStartPatternString = taDelim + ngAttrName + ngOptCount + taTextStart;
    private static final Pattern taStartPattern = Pattern.compile(taStartPatternString);
    private static Attribute parseTextAttributeStart(String attribute) {
        Matcher m = taStartPattern.matcher(attribute);
        if (m.matches()) {
            String name = m.group(1);
            AttributeSpecification as = knownAttributes.get(name);
            if (as == null)
                return null;
            else {
                String[] groups = new String[m.groupCount() + 1];
                for (int i = 1; i < groups.length; ++i) {
                    groups[i] = m.group(i);
                }
                return new Attribute(as, parseCount(m.group(2)), false);
            }
        } else
            return null;
    }

    private static final String taEndPatternString = taTextEnd + taDelim;
    private static final Pattern taEndPattern   = Pattern.compile(taEndPatternString);
    private static Attribute parseTextAttributeEnd(String attribute) {
        Matcher m = taEndPattern.matcher(attribute);
        if (m.matches()) {
            return Attribute.END;
        } else
            return null;
    }

    private static final String taRubyPatternString = ngText + taTextSep + ngText;
    private static final Pattern taRubyPattern = Pattern.compile(taRubyPatternString);
    private static Attribute parseTextAttributeRubyText(String attribute, Attribute a) {
        Matcher m = taRubyPattern.matcher(attribute);
        if (m.matches()) {
            assert m.groupCount() == 2;
            a.setText(m.group(1));
            a.setAnnotation(m.group(2));
            return a;
        } else
            return null;
    }

    private static final String ncRetainMark    = "\\uFF01";
    private static final String ncRetain        = "[" + ncRetainMark + "]";
    private static final String ngOptRetain     = "(" + ncRetain + "+" + ")?";
    private static final String ntaPatternString = taDelim + ngAttrName + ngOptCount + ngOptRetain;
    private static final Pattern ntaPattern     = Pattern.compile(ntaPatternString);
    private static Attribute parseNonTextAttribute(String attribute) {
        Matcher m = ntaPattern.matcher(attribute);
        if (m.matches()) {
            String name = m.group(1);
            AttributeSpecification as = knownAttributes.get(name);
            if (as == null)
                return null;
            else
                return new Attribute(as, parseCount(m.group(2)), m.group(3) != null);
        } else
            return null;
    }

    private static int parseCount(String count) {
        if (count == null)
            return -1;
        else if (count.length() == 0)
            return -1;
        else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, n = count.length(); i < n; ++i) {
                sb.append((char) toASCIIDigit(count.charAt(i)));
            }
            try {
                return Integer.parseInt(sb.toString());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    private static boolean isCountDigit(int c) {
        if ((c >= '0') && (c <= '9'))
            return true;
        else if ((c >= '\uFF10') && (c <= '\uFF19'))
            return true;
        else
            return false;
    }

    private static int toASCIIDigit(int c) {
        if ((c >= '0') && (c <= '9'))
            return c;
        else if ((c >= '\uFF10') && (c <= '\uFF19'))
            return '0' + (c - '\uFF10');
        else
            return c;
    }

    private static int toASCIIUpper(int c) {
        if ((c >= 'A') && (c <= 'I'))
            return c;
        else if ((c >= 'a') && (c <= 'i'))
            return 'A' + (c - 'a');
        else if ((c >= '\uFF21') && (c <= '\uFF29'))
            return 'A' + (c - '\uFF21');
        else if ((c >= '\uFF41') && (c <= '\uFF49'))
            return 'A' + (c - '\uFF41');
        else
            return c;
    }

    private static String dump(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = s.length(); i < n; ++i) {
            String hex = Integer.toString(s.charAt(i), 16).toUpperCase();
            sb.append('\\');
            sb.append('u');
            for (int k = 4 - hex.length(); k > 0; --k) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static int parseAnnotationAsInteger(String annotation, int defaultValue) {
        try {
            return Integer.parseInt(annotation);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int convert(List<String> args, String uri) {
        Reporter reporter = getReporter();
        if (!reporter.isHidingLocation())
            reporter.logInfo(reporter.message("i.016", "Converting '{'{0}'}'.", uri));
        do {
            resetResourceState();
            setResourceURI(uri);
            setResourceDocumentContextState();
            if (!readResource())
                break;
            if (!parseResource())
                break;
            if (!new com.skynav.cap2tt.converter.ttml.TTML2ResourceConverter(this).convert(screens))
                break;
        } while (false);
        int rv = rvValue();
        reporter.logInfo(reporter.message("i.017", "Conversion {0,choice,0#Failed|1#Succeeded}{1}.", rvSucceeded(rv) ? 1 : 0, resultDetails()));
        reporter.flush();
        Results results = new Results(uri, rv,
            resourceExpectedErrors, reporter.getResourceErrors(), resourceExpectedWarnings, reporter.getResourceWarnings(), getEncoding(), outputDocument);
        this.results.put(uri, results);
        return rv;
    }

    private int rvValue() {
        Reporter reporter = getReporter();
        int code = RV_SUCCESS;
        int flags = 0;
        if (resourceExpectedErrors < 0) {
            if (reporter.getResourceErrors() > 0) {
                code = RV_FAIL;
                flags |= RV_FLAG_ERROR_UNEXPECTED;
            }
        } else if (reporter.getResourceErrors() != resourceExpectedErrors) {
            code = RV_FAIL;
            flags |= RV_FLAG_ERROR_EXPECTED_MISMATCH;
        } else {
            code = RV_SUCCESS;
            if (reporter.getResourceErrors() > 0)
                flags |= RV_FLAG_ERROR_EXPECTED_MATCH;
        }
        if (resourceExpectedWarnings < 0) {
            if (reporter.getResourceWarnings() > 0)
                flags |= RV_FLAG_WARNING_UNEXPECTED;
        } else if (reporter.getResourceWarnings() != resourceExpectedWarnings) {
            code = RV_FAIL;
            flags |= RV_FLAG_WARNING_EXPECTED_MISMATCH;
        } else {
            if (reporter.getResourceWarnings() > 0)
                flags |= RV_FLAG_WARNING_EXPECTED_MATCH;
        }
        return ((flags & 0x7FFFFF) << 8) | (code & 0xFF);
    }

    public static boolean rvSucceeded(int rv) {
        return rvCode(rv) == RV_SUCCESS;
    }

    public static int rvCode(int rv) {
        return (rv & 0xFF);
    }

    public static int rvFlags(int rv) {
        return ((rv >> 8) & 0x7FFFFF);
    }

    private String resultDetails() {
        Reporter reporter = getReporter();
        int resourceErrors = reporter.getResourceErrors();
        int resourceWarnings = reporter.getResourceWarnings();
        StringBuffer details = new StringBuffer();
        if (resourceExpectedErrors < 0) {
            if (resourceErrors > 0) {
                details.append(", with " );
                details.append(resourceErrors);
                details.append(' ');
                details.append(plural("error", resourceErrors));
            }
        } else if (resourceErrors == resourceExpectedErrors) {
            if (resourceErrors > 0) {
                details.append(", with ");
                details.append(resourceErrors);
                details.append(" expected ");
                details.append(plural("error", resourceErrors));
            }
        } else {
            details.append(", with ");
            details.append(resourceErrors);
            details.append(' ');
            details.append(plural("error", resourceErrors));
            details.append(" but expected ");
            details.append(resourceExpectedErrors);
            details.append(' ');
            details.append(plural("error", resourceExpectedErrors));
        }
        if (resourceExpectedWarnings < 0) {
            if (resourceWarnings > 0) {
                details.append(details.length() > 0 ? ", and with " : ", with ");
                details.append(resourceWarnings);
                details.append(' ');
                details.append(plural("warning", resourceWarnings));
            }
        } else if (resourceWarnings == resourceExpectedWarnings) {
            if (resourceWarnings > 0) {
                details.append(details.length() > 0 ? ", and with " : ", with ");
                details.append(resourceWarnings);
                details.append(" expected ");
                details.append(plural("warning", resourceWarnings));
            }
        } else {
            details.append(details.length() > 0 ? ", and with " : ", with ");
            details.append(resourceWarnings);
            details.append(' ');
            details.append(plural("warning", resourceWarnings));
            details.append(" but expected ");
            details.append(resourceExpectedWarnings);
            details.append(' ');
            details.append(plural("warning", resourceExpectedWarnings));
        }
        return details.toString();
    }

    private String plural(String noun, int count) {
        if (count == 1)
            return noun;
        else
            return noun + "s";
    }

    private int convert(List<String> args, List<String> nonOptionArgs) {
        Reporter reporter = getReporter();
        int numFailure = 0;
        int numSuccess = 0;
        for (String uri : nonOptionArgs) {
            switch (rvCode(convert(args, maybeConvertToFileURLString(uri)))) {
            case RV_SUCCESS:
                ++numSuccess;
                break;
            case RV_FAIL:
                ++numFailure;
                break;
            default:
                break;
            }
            reporter.flush();
        }
        if (reporter.getVerbosityLevel() > 0) {
            Message message;
            if (numSuccess > 0) {
                if (numFailure > 0) {
                    message = reporter.message("i.018",
                        "Succeeded {0} {0,choice,0#resources|1#resource|1<resources}, Failed {1} {1,choice,0#resources|1#resource|1<resources}.", numSuccess, numFailure);
                } else {
                    message = reporter.message("i.019", "Succeeded {0} {0,choice,0#resources|1#resource|1<resources}.", numSuccess);
                }
            } else {
                if (numFailure > 0) {
                    message = reporter.message("i.020", "Failed {0} {0,choice,0#resources|1#resource|1<resources}.", numFailure);
                } else {
                    message = null;
                }
            }
            if (message != null)
                reporter.logInfo(message);
        }
        return numFailure > 0 ? 1 : 0;
    }

    public int run(List<String> args) {
        int rv = 0;
        try {
            List<String> argsPreProcessed = preProcessOptions(args, null);
            showBanner(getShowOutput(), (OptionProcessor) null);
            getShowOutput().flush();
            List<String> nonOptionArgs = parseArgs(argsPreProcessed, null);
            if (showRepository)
                showRepository();
            if (showWarningTokens)
                showWarningTokens();
            getShowOutput().flush();
            if (nonOptionArgs.size() > 0) {
                showProcessingInfo();
                rv = convert(args, nonOptionArgs);
            } else
                rv = RV_SUCCESS;
        } catch (ShowUsageException e) {
            showUsage(getShowOutput(), null);
            rv = RV_USAGE;
        } catch (UsageException e) {
            getShowOutput().println("Usage: " + e.getMessage());
            rv = RV_USAGE;
        }
        resetReporter();
        getShowOutput().flush();
        return rv;
    }

    public Document convert(List<String> args, URI input, Reporter reporter, Document unused) {
        assert args != null;
        assert input != null;
        if (reporter == null)
            reporter = new NullReporter();
        if (!reporter.isOpen()) {
            String pwEncoding = defaultReporterFileEncoding;
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.err, pwEncoding)));
                setReporter(reporter, pw, pwEncoding, false, true);
            } catch (Throwable e) {
            }
        } else {
            setReporter(reporter, null, null, false, false);
        }
        parseArgs(preProcessOptions(args, null), null);
        convert(null, input.toString());
        return outputDocument;
    }

    public TimedText convert(List<String> args, File input, Reporter reporter) {
        return convert(args, input.toURI(), reporter);
    }

    public TimedText convert(List<String> args, URI input, Reporter reporter) {
        Document d = convert(args, input, reporter, (Document) null);
        return (d != null) ? unmarshall(d) : null;
    }

    private TimedText unmarshall(Document d) {
        try {
            JAXBContext context = JAXBContext.newInstance(model.getJAXBContextPath());
            Binder<Node> binder = context.createBinder();
            Object unmarshalled = binder.unmarshal(d);
            if (unmarshalled instanceof JAXBElement<?>) {
                Object content = ((JAXBElement<?>) unmarshalled).getValue();
                if (content instanceof TimedText)
                    return (TimedText) content;
            }
        } catch (UnmarshalException e) {
            reporter.logError(e);
        } catch (Exception e) {
            reporter.logError(e);
        }
        return null;
    }

    public Map<String,Results> getResults() {
        return results;
    }

    public Results getResults(String uri) {
        return results.get(uri);
    }

    public int getResultCode(String uri) {
        if (results.containsKey(uri))
            return results.get(uri).getCode();
        else
            return -1;
    }

    public int getResultFlags(String uri) {
        if (results.containsKey(uri))
            return results.get(uri).getFlags();
        else
            return -1;
    }

    public static String getVersionTitle() {
        return title;
    }

    public static String getRepositoryURL() {
        return repositoryURL;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().exit(new Converter().run(Arrays.asList(args)));
    }

    private static String maybeConvertToFileURLString(String s) {
        if (isRelativeFilePath(s))
            return convertRelativeFilePathToFileURLString(s);
        else if (isAbsoluteFilePath(s))
            return convertAbsoluteFilePathToFileURLString(s);
        else
            return s;
    }

    private static boolean isRelativeFilePath(String s) {
        String rp1 = new String(new char[]{'.',File.separatorChar});
        if (s.startsWith(rp1))
            return true;
        String rp2 = new String(new char[]{'.','.',File.separatorChar});
        if (s.startsWith(rp2))
            return true;
        return false;
    }

    private static String convertRelativeFilePathToFileURLString(String s) {
        return s;
    }

    private static boolean isAbsoluteFilePath(String s) {
        return s.startsWith(File.separator);
    }

    private static String convertAbsoluteFilePathToFileURLString(String s) {
        return "file://" + escapeURL(s);
    }

    private static String escapeURL(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c))
                sb.append("%20");
            else
                sb.append(c);
        }
        return sb.toString();
    }

}

// Local Variables:
// coding: utf-8-unix
// End:
