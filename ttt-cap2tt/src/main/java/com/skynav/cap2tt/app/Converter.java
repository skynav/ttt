/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
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
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import com.skynav.cap2tt.converter.ConverterContext;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionProcessor;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.ShowUsageException;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.app.UsageException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Models;
import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.Layout;
import com.skynav.ttv.model.ttml2.tt.ObjectFactory;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.Styling;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.FontStyle;
import com.skynav.ttv.model.ttml2.ttd.RubyPosition;
import com.skynav.ttv.model.ttml2.ttd.TextAlign;
import com.skynav.ttv.model.value.ClockTime;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.model.value.impl.ClockTimeImpl;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.util.Base64;
import com.skynav.ttv.util.ComparableQName;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.NullReporter;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.Reporters;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.TextTransformer;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Timing;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.Nodes;
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

    private static final String DEFAULT_INPUT_ENCODING          = "UTF-8";
    private static final String DEFAULT_OUTPUT_ENCODING         = "UTF-8";

    // uri related constants
    private static final String uriFileDescriptorScheme         = "fd";
    private static final String uriFileDescriptorStandardIn     = "stdin";
    private static final String uriFileDescriptorStandardOut    = "stdout";
    private static final String uriStandardInput                = uriFileDescriptorScheme + ":" + uriFileDescriptorStandardIn;
    private static final String uriStandardOutput               = uriFileDescriptorScheme + ":" + uriFileDescriptorStandardOut;
    private static final String uriFileScheme                   = "file";

    // miscelaneous defaults
    private static final String defaultReporterFileEncoding     = Reporters.getDefaultEncoding();
    private static Charset defaultEncoding;
    private static Charset defaultOutputEncoding;
    private static final String defaultOutputFileNamePattern    = "tt{0,number,0000}.xml";

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
    private static final QName ttBreakEltName = new QName(NAMESPACE_TT, "br");
    private static final QName ttHeadEltName = new QName(NAMESPACE_TT, "head");
    private static final QName ttInitialEltName = new QName(NAMESPACE_TT, "initial");
    private static final QName ttParagraphEltName = new QName(NAMESPACE_TT, "p");
    private static final QName ttRegionEltName = new QName(NAMESPACE_TT, "region");
    private static final QName ttSpanEltName = new QName(NAMESPACE_TT, "span");
    private static final QName ttStylingEltName = new QName(NAMESPACE_TT, "styling");
    private static final QName ttmItemEltName = new QName(NAMESPACE_TT_METADATA, "item");

    // ttml1 attribute names
    private static final QName regionAttrName = new QName("", "region");
    private static final QName ttsFontFamilyAttrName = new QName(NAMESPACE_TT_STYLE, "fontFamily");
    private static final QName ttsFontSizeAttrName = new QName(NAMESPACE_TT_STYLE, "fontSize");
    private static final QName ttsFontStyleAttrName = new QName(NAMESPACE_TT_STYLE, "fontStyle");
    private static final QName ttsTextAlignAttrName = new QName(NAMESPACE_TT_STYLE, "textAlign");
    private static final QName xmlSpaceAttrName = new QName(XML.xmlNamespace, "space");

    // ttml2 attribute names
    private static final QName ttsFontKerningAttrName = new QName(NAMESPACE_TT_STYLE, "fontKerning");
    private static final QName ttsFontShearAttrName = new QName(NAMESPACE_TT_STYLE, "fontShear");
    private static final QName ttsRubyAttrName = new QName(NAMESPACE_TT_STYLE, "ruby");
    private static final QName ttsTextEmphasisAttrName = new QName(NAMESPACE_TT_STYLE, "textEmphasis");
    private static final QName ttsTextCombineAttrName = new QName(NAMESPACE_TT_STYLE, "textCombine");

    // ttv annotation names
    private static final QName ttvaModelAttrName = new QName(Annotations.getNamespace(), "model");

    // miscellaneous
    private static final float[] shears = new float[] { 0, 6.345103f, 11.33775f, 16.78842f, 21.99875f, 27.97058f };

    // banner text
    private static final String title = "CAP To Timed Text (CAP2TT) [" + Version.CURRENT + "]";
    private static final String copyright = "Copyright 2014 Skynav, Inc.";
    private static final String banner = title + " " + copyright;
    private static final String creationSystem = "CAP2TT/" + Version.CURRENT;

    // usage text
    private static final String repositoryURL = "https://github.com/skynav/cap2tt";
    private static final String repositoryInfo = "Source Repository: " + repositoryURL;

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
        { "横下",               AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom
        { "横上",               AttrContext.Attribute,    AttrCount.None               }, // horizontal top
        { "横適",               AttrContext.Attribute,    AttrCount.None               }, // horizontal full
        { "横中",               AttrContext.Attribute,    AttrCount.None               }, // horizontal center
        { "縦右",               AttrContext.Attribute,    AttrCount.None               }, // vertical right
        { "縦左",               AttrContext.Attribute,    AttrCount.None               }, // vertical left
        { "縦適",               AttrContext.Attribute,    AttrCount.None               }, // vertical full
        { "縦中",               AttrContext.Attribute,    AttrCount.None               }, // vertical center
        // alignment (9.1.2)
        { "中央",               AttrContext.Attribute,    AttrCount.None               }, // center
        { "行頭",               AttrContext.Attribute,    AttrCount.None               }, // start
        { "行末",               AttrContext.Attribute,    AttrCount.None               }, // end
        { "中頭",               AttrContext.Attribute,    AttrCount.None               }, // center start
        { "中末",               AttrContext.Attribute,    AttrCount.None               }, // center end
        { "両端",               AttrContext.Attribute,    AttrCount.None               }, // justify
        // mixed placement and alignment (9.1.3)
        { "横中央",             AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom, center
        { "横中頭",             AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom, center start
        { "横中末",             AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom, center end
        { "横行頭",             AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom, start
        { "横行末",             AttrContext.Attribute,    AttrCount.None               }, // horizontal bottom, end
        { "縦右頭",             AttrContext.Attribute,    AttrCount.None               }, // vertical right, start
        { "縦左頭",             AttrContext.Attribute,    AttrCount.None               }, // vertical left, start
        { "縦中頭",             AttrContext.Attribute,    AttrCount.None               }, // vertical full, center start
        // font style (9.1.4)
        { "正体",               AttrContext.Both,         AttrCount.None               }, // normal
        { "斜",                 AttrContext.Both,         AttrCount.Optional,  (Integer) 0, (Integer) 5 }, // italic
        // kerning (9.1.5)
        { "詰",                 AttrContext.Both,         AttrCount.Mandatory, (Integer) 0, (Integer) 1 }, // kerning disabled
        // font width/size (9.1.6)
        { "幅広",               AttrContext.Text,         AttrCount.None               }, // wide
        { "倍角",               AttrContext.Text,         AttrCount.None               }, // double
        { "半角",               AttrContext.Text,         AttrCount.None               }, // half
        { "拗音",               AttrContext.Text,         AttrCount.None               }, // contracted sound
        { "幅",                 AttrContext.Text,         AttrCount.Mandatory, (Integer) 5, (Integer) 20 }, // width (scale in x or y, whichever is advancement dimension
        { "寸",                 AttrContext.Text,         AttrCount.Mandatory, (Integer) 5, (Integer) 20 }, // dimension (scale x and y)
        // ruby (9.1.7)
        { "ルビ",               AttrContext.Text,         AttrCount.None               }, // ruby auto
        { "ルビ上",             AttrContext.Text,         AttrCount.None               }, // ruby above
        { "ルビ下",             AttrContext.Text,         AttrCount.None               }, // ruby below
        { "ルビ右",             AttrContext.Text,         AttrCount.None               }, // ruby right
        { "ルビ左",             AttrContext.Text,         AttrCount.None               }, // ruby left
        // continuation
        { "継続",               AttrContext.Attribute,    AttrCount.None               }, // continuation
        // font family (9.1.8)
        { "丸ゴ",               AttrContext.Attribute,    AttrCount.None               }, // maru go
        { "丸ゴシック",         AttrContext.Attribute,    AttrCount.None               }, // maru gothic
        { "角ゴ",               AttrContext.Attribute,    AttrCount.None               }, // kaku go
        { "太角ゴ",             AttrContext.Attribute,    AttrCount.None               }, // futo kaku go
        { "太角ゴシック",       AttrContext.Attribute,    AttrCount.None               }, // futo kaku gothic
        { "太明",               AttrContext.Attribute,    AttrCount.None               }, // futa min
        { "太明朝",             AttrContext.Attribute,    AttrCount.None               }, // futa mincho
        { "シネマ",             AttrContext.Attribute,    AttrCount.None               }, // cinema
        // extensions
        { "組",                 AttrContext.Text,         AttrCount.None               }, // tate-chu-yoko
    };
    private static final Map<String, AttributeSpecification> knownAttributes;
    static {
        knownAttributes = new java.util.HashMap<String,AttributeSpecification>();
        for (Object[] spec : knownAttributeSpecifications) {
            assert spec.length >= 3;
            String name = (String) spec[0];
            AttrContext context = (AttrContext) spec[1];
            AttrCount count = (AttrCount) spec[2];
            int minCount = (count != AttrCount.None) ? (Integer) spec[3] : 0;
            int maxCount = (count != AttrCount.None) ? (Integer) spec[4] : 0;
            knownAttributes.put(name, new AttributeSpecification(name, context, count, minCount, maxCount));
        }
    }

    protected enum GenerationIndex {
        styleSetIndex;
    };

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
    private double parsedExternalFrameRate;
    private double parsedExternalDuration;
    private double[] parsedExternalExtent;

    // global processing state
    private SimpleDateFormat gmtDateTimeFormat;
    private PrintWriter showOutput;
    private ExternalParametersStore externalParameters = new ExternalParametersStore();
    private Model model;
    private Reporter reporter;
    private Map<String,Results> results = new java.util.HashMap<String,Results>();
    private int outputFileSequence;

    // per-resource processing state
    private String resourceUriString;
    private Map<String,Object> resourceState;
    private URI resourceUri;
    private Charset resourceEncoding;
    private CharBuffer resourceBuffer;
    private int resourceExpectedErrors = -1;
    private int resourceExpectedWarnings = -1;
    private Document outputDocument;

    // per-resource parsing state
    private List<Screen> screens;
    private int[] indices;
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

    @Override
    public ExternalParameters getExternalParameters() {
        return externalParameters;
    }

    @Override
    public Reporter getReporter() {
        return reporter;
    }

    public Charset getEncoding() {
        if (this.forceEncoding != null)
            return this.forceEncoding;
        else if (this.resourceEncoding != null)
            return this.resourceEncoding;
        else
            return defaultEncoding;
    }

    @Override
    public void setResourceState(String key, Object value) {
        if (resourceState != null)
            resourceState.put(key, value);
    }

    @Override
    public Object getResourceState(String key) {
        if (resourceState != null)
            return resourceState.get(key);
        else
            return null;
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

    private boolean isStandardInput(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileDescriptorScheme))
            return false;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if ((schemeSpecificPart == null) || !schemeSpecificPart.equals(uriFileDescriptorStandardIn))
            return false;
        return true;
    }

    private boolean isStandardOutput(String uri) {
        try {
            return isStandardOutput(new URI(uri));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isStandardOutput(URI uri) {
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equals(uriFileDescriptorScheme))
            return false;
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        if ((schemeSpecificPart == null) || !schemeSpecificPart.equals(uriFileDescriptorStandardOut))
            return false;
        return true;
    }

    private boolean isFile(URI uri) {
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
        getReporter().resetResourceState();
        // parsing state
        screens = new java.util.ArrayList<Screen>();
        indices = new int[GenerationIndex.values().length];
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

    private boolean parseContentLine(String line, LocatorImpl locator) {
        boolean fail = false;
        int[][] types = new int[1][];
        String[] parts = splitContentLine(line, types);
        int partCount = parts.length;
        int partIndexNext = 0;
        Screen s = new Screen(locator, getLastScreenNumber());
        // screen part
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
        // time field
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
        // text fields
        if (!fail) {
            StringBuffer sb = new StringBuffer();
            int lastTextPartIndex = -1;
            for (int i = partIndexNext, j, n = partCount; i < n; ++i) {
                if (parts[i].length() == 0) {
                    continue;
                } else {
                    i = maybeSkipSeparators(parts, i);
                    if ((j = hasTextField(locator, parts, i, NonTextAttributeTreatment.Warning)) >= 0) {
                        // insert encoded preceding separator text
                        if (sb.length() > 0) {
                            if (i > 0)
                                sb.append(parseText(parts[i - 1], false));
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
        }
        // attribute fields
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
                if (s.number > 0)
                    return s.number;
            }
            return 0;
        }
    }

    private static int hasScreenField(String[] parts, int partIndex) {
        if (partIndex < parts.length) {
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
                assert s.number == 0;
                s.number = Integer.parseInt(count.toString());
                assert s.letter == 0;
                s.letter = (letter.length() == 1) ? letter.charAt(0) : 0;
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
        assert s.in == null;
        s.in = parseTimeCode(ic.toString());
        assert s.out == null;
        s.out = parseTimeCode(oc.toString());
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
                int[] delims = countTextAttributeDelimiters(field);
                if (inTextAttribute) {
                    if ((delims[1] > 0) && (delims[0] < delims[1]))
                        inTextAttribute = false;
                    return partIndex;
                } else if (isTextField(locator, field, partIndex, nonTextAttributeTreatment)) {
                    return partIndex;
                } else if ((delims[0] > 0) && (delims[0] > delims[1])) {
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
                else if (isTextAttribute(part))
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
                } else if (isText(part))
                    continue;
                else
                    return false;
            }
            return true;
        }
    }

    private static final char attributePrefix = '\uFF20';   // U+FF20 FULLWIDTH COMMERCIAL AT '＠'
    private static final String[] splitTextField(String field) {
        List<String> parts = new java.util.ArrayList<String>();
        boolean inText = false;
        boolean inAttribute = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = field.length(); i < n; ++i) {
            char c = field.charAt(i);
            if (c == attributePrefix) {
                if (inText) {
                    parts.add(sb.toString());
                    sb.setLength(0);
                    sb.append(c);
                    inText = false;
                    inAttribute = true;
                } else if (inAttribute) {
                    sb.append(c);
                    parts.add(sb.toString());
                    sb.setLength(0);
                    inAttribute = false;
                } else {
                    sb.append(c);
                    inAttribute = true;
                }
            } else if (inText) {
                sb.append(c);
            } else if (inAttribute) {
                sb.append(c);
            } else {
                sb.append(c);
                inText = true;
            }
        }
        if (sb.length() > 0)
            parts.add(sb.toString());
        // FIXME - short term fix for use of nested designations
        if (hasNestedDesignations(parts))
            parts = stripNestedDesignations(parts);
        return parts.toArray(new String[parts.size()]);
    }

    private static boolean hasNestedDesignations(List<String> parts) {
        int numAttributePrefix = 0;
        for (String part : parts)
            numAttributePrefix += countAttributePrefixes(part);
        return (numAttributePrefix > 2) && ((numAttributePrefix & 1) == 0);
    }

    private static int countAttributePrefixes(String s) {
        int numAttributePrefix = 0;
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (c == attributePrefix)
                ++numAttributePrefix;
        }
        return numAttributePrefix;
    }

    private static List<String> stripNestedDesignations(List<String> parts) {
        List<String> strippedParts = new java.util.ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int nesting = 0;
        for (int i = 0, n = parts.size(); i < n; ++i) {
            String part = parts.get(i);
            if (beginsWithTextAttributeStart(part)) {
                if (!endsWithTextAttributeEnd(part)) {
                    sb.append(part);
                    nesting++;
                } else
                    strippedParts.add(part);
            } else if (endsWithTextAttributeEnd(part)) {
                sb.append(part);
                if (nesting > 0)
                    nesting--;
                if (nesting == 0) {
                    strippedParts.add(stripNestedDesignations(sb.toString()));
                    sb.setLength(0);
                }
            } else if (nesting > 0) {
                sb.append(part);
            } else
                strippedParts.add(part);
        }
        return strippedParts;
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

    private static boolean containsTextAttributeStart(String s) {
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (c == attributePrefix) {
                if (beginsWithTextAttributeStart(s.substring(i)))
                    return true;
            }
        }
        return false;
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
                else if (c == '\uFF20')
                    break;
                else if (c == '\uFF3B')
                    break;
                else if (c == '\uFF5C')
                    break;
                else if (c == '\uFF3D')
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
                return s.charAt(i) == '\uFF3B';
            else
                return false;
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

    private static boolean containsTextAttributeEnd(String s) {
        return s.lastIndexOf(textAttributeEnd) >= 0;
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

    private static boolean endsWithTextAttributeEnd(String s) {
        return endsWithTextAttributeEnd(s, false);
    }

    private static boolean endsWithTextAttributeEnd(String s, boolean ignoreTrailingWhitespace) {
        if (ignoreTrailingWhitespace)
            s = trimTrailingWhitespace(s);
        return s.endsWith(textAttributeEnd);
    }

    private static String trimTrailingWhitespace(String s) {
        int i = 0;
        int n = s.length();
        for (; i < n; ++i) {
            int k = n - i - 1;
            char c = s.charAt(k);
            if (c == '\u0009')
                continue;
            else if (c == '\u0020')
                continue;
            else
                break;
        }
        return (i < n) ? s.substring(0, i) : s;
    }

    private static String stripNestedDesignations(String field) {
        StringBuffer sb = new StringBuffer();
        int numAttributePrefix = 0;
        for (int i = 0, n = field.length(); i < n;) {
            char c = field.charAt(i);
            if (c == attributePrefix) {
                if (++numAttributePrefix > 1) {
                    int j = i + 1;
                    int k = field.indexOf(attributePrefix, i + 1);
                    if (j < k) {
                        String nestedTextAttribute = field.substring(i, ++k);
                        Attribute[] retAttr = new Attribute[1];
                        if (parseTextAttribute(nestedTextAttribute, retAttr) != null) {
                            Attribute a = retAttr[0];
                            if (a.isEmphasis()) {
                                sb.append(a.getText());
                            } else if (a.isRuby()) {
                                sb.append(a.getText());
                                sb.append('(');
                                sb.append(a.getAnnotation());
                                sb.append(')');
                            } else {
                                sb.append(a.getText());
                            }
                            i = k;
                            continue;
                        }
                    }
                }
            }
            sb.append(c);
            ++i;
        }
        return sb.toString();
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
            List<AnnotatedRange> annotations = new java.util.ArrayList<AnnotatedRange>();
            Attribute[] ra = new Attribute[1];
            for (String part : splitTextField(field)) {
                String t;
                if ((t = parseTextEscape(part)) != null) {
                    sb.append(t);
                } else if ((t = parseTextAttribute(part, ra)) != null) {
                    int start = sb.length();
                    sb.append(t);
                    int end = sb.length();
                    annotations.add(new AnnotatedRange(new Annotation(ra[0]), start, end));
                } else if (isNonTextAttribute(part)) {
                    continue;
                } else if ((t = parseText(part, true)) != null) {
                    sb.append(t);
                } else {
                    throw new IllegalStateException(getReporter().message("x.021", "unexpected text field parse state: part {0}", part).toText());
                }
            }
            if (sb.length() > 0) {
                AttributedString as = new AttributedString(sb.toString());
                for (AnnotatedRange r : annotations) {
                    as.addAttribute(TextAttribute.ANNOTATION, r.annotation, r.start, r.end);
                }
                assert s.text == null;
                s.text = as;
            }
            return s;
        }
    }

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

    private static String parseTextAttribute(String text, Attribute[] retAttr) {
        Attribute a = parseTextAttribute(text);
        if (a != null) {
            if (retAttr != null)
                retAttr[0] = a;
            return a.text;
        } else
            return null;
    }

    private static String parseText(String text, boolean mapInitialSpaceToNBSP) {
        int escapedSpaces = 0;
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
            if (c == attributePrefix)
                return null;
            else if (c == '\t')
                return null;
            else if ((c == '\u005F') || (c == '\uFF3F'))
                ++escapedSpaces;
        }
        if (escapedSpaces == 0)
            return text;
        StringBuffer sb = new StringBuffer(text.length());
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
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

    private static boolean isTextAttribute(String field) {
        return parseTextAttribute(field) != null;
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
                    if ((context == AttrContext.Attribute) && (a.specification.context == AttrContext.Text)) {
                        // text attribute in non-text attribute context
                        return null;
                    } else if ((context == AttrContext.Text) && (a.specification.context == AttrContext.Attribute)) {
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
    private static final String ncRetainMark    = "\\uFF01";
    private static final String ncWhite         = "\\u0009\\u000A\\u000D\\u0020\\u3000";
    private static final String ncTextWhite     = "\\u0009";
    private static final String ncAttrName      = "[^" + ncWhite + ncDigits + ncPunct + "]";
    private static final String ncCount         = "[" + ncDigits + "]";
    private static final String ncText          = "[^" + ncTextWhite + ncTextPunct + "]";
    private static final String ncRetain        = "[" + ncRetainMark + "]";
    private static final String ngAttrName      = "(" + ncAttrName + "+" + ")";
    private static final String ngOptCount      = "(" + ncCount + "+" + ")?";
    private static final String ngOptRetain     = "(" + ncRetain + "+" + ")?";
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
                return new Attribute(as, parseCount(m.group(2)), false, m.group(4), m.group(6));
            }
        } else
            return null;
    }

    private static final String ntaPatternString = taDelim + ngAttrName + ngOptCount + ngOptRetain;
    private static final Pattern ntaPattern      = Pattern.compile(ntaPatternString);
    private static Attribute parseNonTextAttribute(String attribute) {
        Matcher m = ntaPattern.matcher(attribute);
        if (m.matches()) {
            String name = m.group(1);
            AttributeSpecification as = knownAttributes.get(name);
            if (as == null)
                return null;
            else
                return new Attribute(as, parseCount(m.group(2)), m.group(3) != null, null, null);
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

    private static String makeTimeExpression(ClockTime time) {
        return toString(time, ':');
    }

    private static String toString(ClockTime time, char sep) {
        StringBuffer sb = new StringBuffer();
        sb.append(pad(time.getHours(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad(time.getMinutes(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad((int) time.getSeconds(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad((int) time.getFrames(), 2, '0'));
        return sb.toString();
    }

    private static String digits = "0123456789";
    private static String pad(int value, int width, char padding) {
        assert value >= 0;
        StringBuffer sb = new StringBuffer(width);
        while (value > 0) {
            sb.append(digits.charAt(value % 10));
            value /= 10;
        }
        while (sb.length() < width) {
            sb.append(padding);
        }
        return sb.reverse().toString();
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

    private boolean convertResource() {
        boolean fail = false;
        Reporter reporter = getReporter();
        reporter.logInfo(reporter.message("i.014", "Converting resource ..."));
        try {
            //  convert screens to a div of paragraphs
            State state = new State();
            state.process(screens);
            // populate body, extracting division from state object, must be performed prior to populating head
            Body body = ttmlFactory.createBody();
            state.populate(body, defaultRegion);
            // populate head
            Head head = ttmlFactory.createHead();
            state.populate(head);
            // populate root (tt)
            TimedText tt = ttmlFactory.createTimedText();
            tt.setVersion(java.math.BigInteger.valueOf(2));
            tt.getOtherAttributes().put(ttvaModelAttrName, model.getName());
            if (defaultLanguage != null)
                tt.setLang(defaultLanguage);
            if ((head.getStyling() != null) || (head.getLayout() != null))
                tt.setHead(head);
            if (!body.getDiv().isEmpty())
                tt.setBody(body);
            // marshal and serialize
            if (!convertResource(tt))
                fail = true;
        } catch (Exception e) {
            reporter.logError(e);
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private boolean convertResource(TimedText tt) {
        boolean fail = false;
        try {
            JAXBContext jc = JAXBContext.newInstance(model.getJAXBContextPath());
            Marshaller m = jc.createMarshaller();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            m.marshal(ttmlFactory.createTt(tt), d);
            mergeConfiguration(d);
            elideInitials(d, getInitials(d, model));
            if (mergeStyles)
                mergeStyles(d, indices);
            if (metadataCreation)
                addCreationMetadata(d);
            Map<String, String> prefixes = model.getNormalizedPrefixes();
            Namespaces.normalize(d, prefixes);
            if (!outputDisabled && !writeDocument(d, prefixes))
                fail = true;
            if (outputDisabled || retainDocument)
                outputDocument = d;
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        } catch (JAXBException e) {
            reporter.logError(e);
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private void mergeConfiguration(Document d) {
        assert configuration != null;
        List<Element> initials = new java.util.ArrayList<Element>(configuration.getInitials());
        Collections.reverse(initials);
        if (!initials.isEmpty()) {
            Element e = Documents.findElementByName(d, ttStylingEltName);
            if (e != null) {
                for (Element initial : initials) {
                    e.insertBefore(d.importNode(initial, true), e.getFirstChild());
                }
            }
        }
        List<Element> regions = Documents.findElementsByName(d, ttRegionEltName);
        for (Element e : regions) {
            Element eConfig = configuration.getRegion(e.getAttributeNS(XML.xmlNamespace, "id"));
            if (eConfig != null)
                mergeStyles(e, eConfig);
        }
    }

    private void mergeStyles(Element dst, Element src) {
        NamedNodeMap attrs = src.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                String ns = a.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE)) {
                    String ln = a.getLocalName();
                    String v = a.getValue();
                    dst.setAttributeNS(ns, ln, v);
                }
            }
        }
    }

    private void addCreationMetadata(Document d) {
        Element e;
        if ((e = Documents.findElementByName(d, ttHeadEltName)) != null) {
            Node m, f;
            m = createMetadataItemElement(d, "creationSystem", creationSystem);
            assert m != null;
            f = e.getFirstChild();
            e.insertBefore(m, f);
            m = createMetadataItemElement(d, "creationDate", getXSDDateString(new Date()));
            assert m != null;
            f = e.getFirstChild();
            e.insertBefore(m, f);
        }
    }

    private Element createMetadataItemElement(Document d, String name, String value) {
        QName qn = ttmItemEltName;
        Element e = d.createElementNS(qn.getNamespaceURI(), qn.getLocalPart());
        e.setAttribute("name", name);
        e.appendChild(d.createTextNode(value));
        return e;
    }

    private String getXSDDateString(Date date) {
        return gmtDateTimeFormat.format(date);
    }

    private void mergeStyles(final Document d, int[] indices) {
        try {
            final Map<Element,StyleSet> styleSets = getUniqueSpecifiedStyles(d, indices, styleIdPattern, styleIdSequenceStart);
            final Set<String> styleIds = new java.util.HashSet<String>();
            final Element styling = Documents.findElementByName(d, ttStylingEltName);
            if (styling != null) {
                Traverse.traverseElements(d, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element e = (Element) content;
                        if (styleSets.containsKey(e)) {
                            StyleSet ss = styleSets.get(e);
                            String id = ss.getId();
                            if (!id.isEmpty()) {
                                e.setAttribute("style", id);
                                if (!styleIds.contains(id)) {
                                    Element ttStyle = d.createElementNS(NAMESPACE_TT, "style");
                                    generateAttributes(ss, ttStyle);
                                    styling.appendChild(ttStyle);
                                    styleIds.add(id);
                                }
                            }
                            pruneStyles(e);
                        }
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            getReporter().logError(e);
        }
    }

    private static void generateAttributes(StyleSet ss, Element e) {
        String id = ss.getId();
        if ((id != null) && !id.isEmpty()) {
            for (StyleSpecification s : ss.getStyles().values()) {
                ComparableQName n = s.getName();
                e.setAttributeNS(n.getNamespaceURI(), n.getLocalPart(), s.getValue());
            }
            e.setAttributeNS(XML.xmlNamespace, "id", id);
        }
    }

    private Map<QName,String> getInitials(Document d, Model model) {
        Map<QName,String> initials = new java.util.HashMap<QName,String>();
        // get initials from model
        for (QName qn : model.getDefinedStyleNames()) {
            initials.put(qn, model.getInitialStyleValue(null, qn));
        }
        // get initials from document
        for (Element e : Documents.findElementsByName(d, ttInitialEltName)) {
            NamedNodeMap attrs = e.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    String ns = a.getNamespaceURI();
                    if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE)) {
                        String ln = a.getLocalName();
                        String v = a.getValue();
                        initials.put(new QName(ns, ln), v);
                    }
                }
            }
        }
        return initials;
    }

    private void elideInitials(Document d, final Map<QName,String> initials) {
        try {
            Traverse.traverseElements(d, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element e = (Element) content;
                    if (isRegionOrContentElement(e))
                        elideInitials(e, initials);
                    return true;
                }
            });
        } catch (Exception e) {
            getReporter().logError(e);
        }
    }

    private static void elideInitials(Element e, final Map<QName,String> initials) {
        List<Attr> elide = new java.util.ArrayList<Attr>();
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                String ns = a.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE)) {
                    QName qn = new QName(ns, a.getLocalName());
                    String initial = initials.get(qn);
                    if ((initial != null) && a.getValue().equals(initial)) {
                        if (!isSpan(e) || !isTextAlign(a))
                            elide.add(a);
                    }
                }
            }
        }
        for (Attr a: elide) {
            e.removeAttributeNode(a);
        }
    }

    private static boolean isSpan(Element e) {
        return Nodes.matchesName(e, ttSpanEltName);
    }

    private static boolean isTextAlign(Attr a) {
        return Nodes.matchesName(a, ttsTextAlignAttrName);
    }

    private static Map<Element, StyleSet> getUniqueSpecifiedStyles(Document d, int[] indices, String styleIdPattern, int styleIdStart) {
        // get specified style sets
        Map<Element, StyleSet> specifiedStyleSets = getSpecifiedStyles(d, indices);

        // obtain ordered set of SSs, ordered by SS generation
        Set<StyleSet> orderedStyles = new java.util.TreeSet<StyleSet>(StyleSet.getGenerationComparator());
        orderedStyles.addAll(specifiedStyleSets.values());

        // obtain unique set of SSs
        Set<StyleSet> uniqueStyles = new java.util.TreeSet<StyleSet>(StyleSet.getValuesComparator());
        uniqueStyles.addAll(orderedStyles);

        // final reorder by generation
        orderedStyles.clear();
        orderedStyles.addAll(uniqueStyles);
        List<StyleSet> styles = new java.util.ArrayList<StyleSet>(orderedStyles);

        // assign identifiers to unique SSs
        int uniqueStyleIndex = styleIdStart;
        for (StyleSet ss : styles)
            ss.setId(MessageFormat.format(styleIdPattern, uniqueStyleIndex++));

        // remap SS map entries to unique SSs
        for (Map.Entry<Element,StyleSet> e : specifiedStyleSets.entrySet()) {
            StyleSet ss = e.getValue();
            int index = styles.indexOf(ss);
            if (index >= 0) {
                StyleSet ssUnique = styles.get(index);
                if (ss != ssUnique) // N.B. must not use equals() here
                    e.setValue(ssUnique);
            }
        }

        return specifiedStyleSets;
    }

    private static Map<Element, StyleSet> getSpecifiedStyles(Document d, int[] indices) {
        Map<Element, StyleSet> specifiedStyleSets = new java.util.HashMap<Element, StyleSet>();
        specifiedStyleSets = getSpecifiedStyles(getContentElements(d), specifiedStyleSets, indices);
        return specifiedStyleSets;
    }

    private static List<Element> getContentElements(Document d) {
        final List<Element> elements = new java.util.ArrayList<Element>();
        try {
            Traverse.traverseElements(d, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element e = (Element) content;
                    if (isContentElement(e))
                        elements.add(e);
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return elements;
    }

    private static boolean isRegionOrContentElement(Element e) {
        return isRegionElement(e) || isContentElement(e);
    }

    private static boolean isRegionElement(Element e) {
        String ns = e.getNamespaceURI();
        if ((ns == null) || !ns.equals(NAMESPACE_TT))
            return false;
        else {
            String localName = e.getLocalName();
            if (localName.equals("region"))
                return true;
            else
                return false;
        }
    }

    private static boolean isContentElement(Element e) {
        String ns = e.getNamespaceURI();
        if ((ns == null) || !ns.equals(NAMESPACE_TT))
            return false;
        else {
            String localName = e.getLocalName();
            if (localName.equals("body"))
                return true;
            else if (localName.equals("div"))
                return true;
            else if (localName.equals("p"))
                return true;
            else if (localName.equals("span"))
                return true;
            else if (localName.equals("br"))
                return true;
            else
                return false;
        }
    }

    private static Map<Element, StyleSet> getSpecifiedStyles(List<Element> elements, Map<Element, StyleSet> specifiedStyleSets, int[] indices) {
        for (Element e : elements) {
            assert !specifiedStyleSets.containsKey(e);
            StyleSet ss = getInlineStyles(e, indices);
            if (!ss.isEmpty())
                specifiedStyleSets.put(e, ss);
        }
        return specifiedStyleSets;
    }

    private static StyleSet getInlineStyles(Element e, int[] indices) {
        StyleSet styles = new StyleSet(generateStyleSetIndex(indices));
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                String ns = a.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE)) {
                    styles.merge(new StyleSpecification(new ComparableQName(a.getNamespaceURI(), a.getLocalName()), a.getValue()));
                }
            }
        }
        return styles;
    }

    private static int generateStyleSetIndex(int[] indices) {
        return indices[GenerationIndex.styleSetIndex.ordinal()]++;
    }

    private void pruneStyles(Element e) {
        List<Attr> prune = new java.util.ArrayList<Attr>();
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                String ns = a.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE)) {
                    prune.add(a);
                }
            }
        }
        for (Attr a : prune) {
            e.removeAttributeNode(a);
        }
    }

    private static Set<QName> startTagIndentExclusions;
    private static Set<QName> endTagIndentExclusions;
    static {
        startTagIndentExclusions = new java.util.HashSet<QName>();
        startTagIndentExclusions.add(ttParagraphEltName);
        startTagIndentExclusions.add(ttSpanEltName);
        startTagIndentExclusions.add(ttBreakEltName);
        startTagIndentExclusions.add(ttmItemEltName);
        endTagIndentExclusions = new java.util.HashSet<QName>();
        endTagIndentExclusions.add(ttSpanEltName);
        endTagIndentExclusions.add(ttBreakEltName);
    }

    private boolean writeDocument(Document d, Map<String, String> prefixes) {
        boolean fail = false;
        Reporter reporter = getReporter();
        BufferedWriter bw = null;
        try {
            DOMSource source = new DOMSource(d);
            File[] retOutputFile = new File[1];
            bw = new BufferedWriter(new OutputStreamWriter(getOutputStream(retOutputFile), outputEncoding));
            StreamResult result = new StreamResult(bw);
            Transformer t = new TextTransformer(outputEncoding.name(), outputIndent, prefixes, startTagIndentExclusions, endTagIndentExclusions);
            t.transform(source, result);
            File outputFile = retOutputFile[0];
            reporter.logInfo(reporter.message("i.015", "Wrote TTML ''{0}''.", (outputFile != null) ? outputFile.getAbsolutePath() : uriStandardOutput));
        } catch (IOException e) {
            reporter.logError(e);
        } catch (TransformerException e) {
            reporter.logError(e);
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) {}
            }
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private OutputStream getOutputStream(File[] retOutputFile) throws IOException {
        assert resourceUri != null;
        StringBuffer sb = new StringBuffer();
        if (isFile(resourceUri)) {
            String path = resourceUri.getPath();
            int s = 0;
            int e = path.length();
            int lastPathSeparator = path.lastIndexOf('/');
            if (lastPathSeparator >= 0)
                s = lastPathSeparator + 1;
            int lastExtensionSeparator = path.lastIndexOf('.');
            if (lastExtensionSeparator >= 0)
                e = lastExtensionSeparator;
            sb.append(path.substring(s, e));
            sb.append(".xml");
        } else {
            sb.append(MessageFormat.format(outputPattern, ++outputFileSequence));
        }
        String outputFileName = sb.toString();
        if (isStandardOutput(outputFileName))
            return System.out;
        else {
            File f = (outputFile != null) ? outputFile : new File(outputDirectory, outputFileName).getCanonicalFile();
            if (retOutputFile != null)
                retOutputFile[0] = f;
            return new FileOutputStream(f);
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
            if (!convertResource())
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
            switch (rvCode(convert(args, uri))) {
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
            return results.get(uri).code;
        else
            return -1;
    }

    public int getResultFlags(String uri) {
        if (results.containsKey(uri))
            return results.get(uri).flags;
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

    public static class AttributeSpecification {
        private String name;
        private AttrContext context;
        private AttrCount count;
        private int minCount;
        private int maxCount;
        public AttributeSpecification(String name, AttrContext context, AttrCount count, int minCount, int maxCount) {
            this.name = name;
            this.context = context;
            this.count = count;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }
        public String getName() {
            return name;
        }
        public AttrContext getContext() {
            return context;
        }
        public AttrCount getCount() {
            return count;
        }
        public int getMinCount() {
            return minCount;
        }
        public int getMaxCount() {
            return maxCount;
        }
    }

    public static class Attribute {
        private AttributeSpecification specification;
        private int count;
        private boolean retain;
        private String text;
        private String annotation;
        public Attribute(AttributeSpecification specification, int count, boolean retain, String text, String annotation) {
            assert specification != null;
            this.specification = specification;
            this.count = count;
            this.retain = retain;
            this.text = normalize(text, false);
            this.annotation = normalize(annotation, true);
        }
        private static String normalize(String s, boolean trim) {
            if (s != null) {
                s = parseText(s, false);
                if (trim)
                    s = s.trim();
                return s;
            } else
                return null;
        }

        public AttributeSpecification getSpecification() {
            return specification;
        }
        public int getCount() {
            return count;
        }
        public boolean getRetain() {
            return retain;
        }
        public String getText() {
            return text;
        }
        public String getAnnotation() {
            return annotation;
        }
        @Override
        public int hashCode() {
            return specification.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Attribute) {
                Attribute other = (Attribute) obj;
                return other.specification.equals(specification);
            } else
                return false;
        }
        public boolean isRuby() {
            return specification.name.startsWith("ルビ");
        }
        public boolean isEmphasis() {
            return specification.name.startsWith("ルビ") && isEmphasisAnnotation();
        }
        public boolean isCombine() {
            return specification.name.equals("組");
        }
        public boolean isEmphasisAnnotation() {
            if (annotation != null) {
                int i = 0;
                int n = annotation.length();
                for (; i < n; ++i) {
                    char c = annotation.charAt(i);
                    if (c == '\u2191')                  // U+2191 UPWARDS ARROW
                        continue;
                    else if (c == '\u2193')             // U+2193 DOWNWARDS ARROW
                        continue;
                    else if (c == '\u30FB')             // U+30FB KATAKANA MIDDLE DOT '・'
                        continue;
                    else if (c == '\uFF3E')             // U+FF3E FULLWIDTH CIRCUMFLEX ACCENT
                        continue;
                    else
                        break;
                }
                return i == n;
            } else
                return false;
        }
        public boolean hasPlacement() {
            String name = specification.name;
            if (name.startsWith("横"))
                return true;
            else if (name.startsWith("縦"))
                return true;
            else
                return false;
        }
        public String getPlacement(boolean[] retGlobal) {
            String v;
            String name = specification.name;
            if (name.startsWith("横")) {
                if (name.equals("横下"))
                    v = name;
                else if (name.equals("横上"))
                    v = name;
                else if (name.equals("横適"))
                    v = name;
                else if (name.equals("横中"))
                    v = name;
                else if (name.equals("横中央"))
                    v = "横下";
                else if (name.equals("横中頭"))
                    v = "横下";
                else if (name.equals("横中末"))
                    v = "横下";
                else if (name.equals("横行頭"))
                    v = "横下";
                else if (name.equals("横行末"))
                    v = "横下";
                else
                    v = null;
            } else if (name.startsWith("縦")) {
                if (name.equals("縦右"))
                    v = name;
                else if (name.equals("縦左"))
                    v = name;
                else if (name.equals("縦適"))
                    v = name;
                else if (name.equals("縦中"))
                    v = name;
                else if (name.equals("縦右頭"))
                    v = "縦右";
                else if (name.equals("縦左頭"))
                    v = "縦左";
                else if (name.equals("縦中頭"))
                    v = "縦中";
                else
                    v = null;
            } else
                v = null;
            if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
                retGlobal[0] = retain;
            return v;
        }
        private static final String[] alignments = new String[] {
            "右頭",
            "行頭",
            "行末",
            "左頭",
            "中央",
            "中頭",
            "中末",
            "両端"
        };
        public boolean hasAlignment() {
            String name = specification.name;
            for (String a : alignments) {
                if (name.equals(a))
                    return true;
                else if (name.endsWith(a))
                    return true;
            }
            return false;
        }
        private String getAlignment(boolean[] retGlobal) {
            String v = null;
            String name = specification.name;
            for (String a : alignments) {
                if (name.equals(a)) {
                    v = a;
                    break;
                } else if (name.endsWith(a)) {
                    v = a;
                    break;
                }
            }
            if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
                retGlobal[0] = retain;
            return v;
        }
        public boolean hasShear() {
            String name = specification.name;
            if (name.equals("正体"))
                return true;
            else if (name.equals("斜"))
                return true;
            else
                return false;
        }
        private String getShear(boolean[] retGlobal) {
            String v;
            String name = specification.name;
            if (name.equals("正体"))
                v = "0";
            else if (name.equals("斜")) {
                if (count < 0)
                    v = "3";
                else if (count < shears.length) {
                    v = Integer.toString(count);
                } else
                    v = Integer.toString(shears.length - 1);
            } else
                v = null;
            if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
                retGlobal[0] = retain;
            return v;
        }
        public boolean hasKerning() {
            return specification.name.equals("詰");
        }
        private String getKerning(boolean[] retGlobal) {
            String v;
            String name = specification.name;
            if (name.equals("詰"))
                v = Integer.toString((count == 0) ? 0 : 1);
            else
                v = null;
            if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
                retGlobal[0] = retain;
            return v;
        }
        private RubyPosition getRubyPosition(Direction blockDirection) {
            RubyPosition v = null;
            String name = specification.name;
            int l = name.length();
            if (name.startsWith("ルビ")) {
                if (l == 2)
                    v = RubyPosition.AUTO;
                else if (l > 2) {
                    char c = name.charAt(2);
                    if (blockDirection == Direction.TB) {
                        if (c == '上')
                            v = RubyPosition.BEFORE;
                        else if (c == '下')
                            v = RubyPosition.AFTER;
                    } else if (blockDirection == Direction.RL) {
                        if (c == '右')
                            v = RubyPosition.BEFORE;
                        else if (c == '左')
                            v = RubyPosition.AFTER;
                    } else if (blockDirection == Direction.LR) {
                        if (c == '右')
                            v = RubyPosition.AFTER;
                        else if (c == '左')
                            v = RubyPosition.BEFORE;
                    }
                }
            }
            return v;
        }
        private static final String[] typefaces = new String[] {
            "丸ゴ",
            "丸ゴシック",
            "角ゴ",
            "太角ゴ",
            "太角ゴシック",
            "太明",
            "太明朝",
            "シネマ"
        };
        public boolean hasTypeface() {
            String name = specification.name;
            for (String t : typefaces) {
                if (name.equals(t))
                    return true;
            }
            return false;
        }
        private String getTypeface(boolean[] retGlobal) {
            String v = null;
            String name = specification.name;
            for (String t : typefaces) {
                if (name.equals(t)) {
                    v = t;
                    break;
                }
            }
            if ((v != null) && (retGlobal != null) && (retGlobal.length > 0))
                retGlobal[0] = retain;
            return v;
        }
        public void populate(Paragraph p, Set<QName> styles, String defaultRegion) {
            String name = specification.name;
            Map<QName, String> attributes = p.getOtherAttributes();
            if (name.equals("横下")) {
                attributes.put(regionAttrName, "横下");
            } else if (name.equals("横上")) {
                attributes.put(regionAttrName, "横上");
            } else if (name.equals("横適")) {
                attributes.put(regionAttrName, "横適");
            } else if (name.equals("横中")) {
                attributes.put(regionAttrName, "横中");
            } else if (name.equals("縦右")) {
                attributes.put(regionAttrName, "縦右");
            } else if (name.equals("縦左")) {
                attributes.put(regionAttrName, "縦左");
            } else if (name.equals("縦適")) {
                attributes.put(regionAttrName, "縦適");
            } else if (name.equals("縦中")) {
                attributes.put(regionAttrName, "縦中");
            } else if (name.equals("中央")) {
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("行頭")) {
                p.setTextAlign(TextAlign.START);
            } else if (name.equals("行末")) {
                p.setTextAlign(TextAlign.END);
            } else if (name.equals("中頭")) {
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("中末")) {
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("両端")) {
                p.setTextAlign(TextAlign.JUSTIFY);
            } else if (name.equals("横中央")) {
                attributes.put(regionAttrName, "横下");
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("横中頭")) {
                attributes.put(regionAttrName, "横下");
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("横中末")) {
                attributes.put(regionAttrName, "横下");
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("横行頭")) {
                attributes.put(regionAttrName, "横下");
                p.setTextAlign(TextAlign.START);
            } else if (name.equals("横行末")) {
                attributes.put(regionAttrName, "横下");
                p.setTextAlign(TextAlign.END);
            } else if (name.equals("縦右頭")) {
                attributes.put(regionAttrName, "縦右");
            } else if (name.equals("縦左頭")) {
                attributes.put(regionAttrName, "縦左");
            } else if (name.equals("縦中頭")) {
                attributes.put(regionAttrName, "縦中");
                p.setTextAlign(TextAlign.CENTER);
            } else if (name.equals("正体")) {
                p.setFontStyle(FontStyle.NORMAL);
                attributes.put(ttsFontShearAttrName, "0%");
            } else if (name.equals("斜")) {
                float shear;
                if (count < 0)
                    shear = shears[3];
                else if (count < shears.length)
                    shear = shears[count];
                else
                    shear = shears[shears.length - 1];
                StringBuffer sb = new StringBuffer();
                if (shear == 0)
                    sb.append('0');
                else
                    sb.append(Float.toString(shear));
                sb.append('%');
                attributes.put(ttsFontShearAttrName, sb.toString());
            } else if (name.equals("詰")) {
                String kerning;
                if (count < 0)
                    kerning = "normal";
                else if (count == 0)
                    kerning = "none";
                else
                    kerning = "normal";
                attributes.put(ttsFontKerningAttrName, kerning);
            } else if (name.equals("幅広")) {
                p.setFontSize("1.0em 1.5em");
            } else if (name.equals("倍角")) {
                p.setFontSize("1.0em 2.0em");
            } else if (name.equals("半角")) {
                p.setFontSize("1.0em 0.5em");
            } else if (name.equals("拗音")) {
                p.setFontSize("1.0em 0.9em");
            } else if (name.equals("幅")) {
                int stretch;
                if (count < 0)
                    stretch = 100;
                else if (count < 5)
                    stretch = 50;
                else if (count < 20)
                    stretch = count * 10;
                else
                    stretch = 200;
                p.setFontSize("1.0em " + Double.toString((double) stretch / 100.0) + "em");
            } else if (name.equals("寸")) {
                int scale;
                if (count < 0)
                    scale = 100;
                else if (count < 5)
                    scale = 50;
                else if (count < 20)
                    scale = count * 10;
                else
                    scale = 200;
                p.setFontSize(((double) scale / 100.0) + "em");
            } else if (name.equals("継続")) {
            } else if (name.equals("丸ゴ")) {
                p.setFontFamily("丸ゴ");
            } else if (name.equals("丸ゴシック")) {
                p.setFontFamily("丸ゴシック");
            } else if (name.equals("角ゴ")) {
                p.setFontFamily("角ゴ");
            } else if (name.equals("太角ゴ")) {
                p.setFontFamily("太角ゴ");
            } else if (name.equals("太角ゴシック")) {
                p.setFontFamily("太角ゴシック");
            } else if (name.equals("太明")) {
                p.setFontFamily("太明");
            } else if (name.equals("太明朝")) {
                p.setFontFamily("太明朝");
            } else if (name.equals("シネマ")) {
                p.setFontFamily("シネマ");
            }
            String region = attributes.get(regionAttrName);
            if ((region != null) && (defaultRegion != null) && region.equals(defaultRegion))
                attributes.remove(regionAttrName);
            updateStyles(p, styles);
        }
        public void updateStyles(Paragraph p, Set<QName> styles) {
            if (p.getFontFamily() != null) {
                styles.add(ttsFontFamilyAttrName);
            }
            if (p.getFontSize() != null) {
                styles.add(ttsFontSizeAttrName);
            }
            if (p.getFontStyle() != null) {
                styles.add(ttsFontStyleAttrName);
            }
            if (p.getTextAlign() != null) {
                styles.add(ttsTextAlignAttrName);
            }
            for (QName qn : p.getOtherAttributes().keySet()) {
                String ns = qn.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE))
                    styles.add(qn);
            }
        }
        public void populate(Span s, Set<QName> styles) {
            String name = specification.name;
            Map<QName, String> attributes = s.getOtherAttributes();
            if (name.equals("正体")) {
                attributes.put(ttsFontShearAttrName, "0%");
            } else if (name.equals("斜")) {
                float shear;
                if (count < 0)
                    shear = shears[3];
                else if (count < shears.length)
                    shear = shears[count];
                else
                    shear = shears[shears.length - 1];
                StringBuffer sb = new StringBuffer();
                if (shear == 0)
                    sb.append('0');
                else
                    sb.append(Float.toString(shear));
                sb.append('%');
                attributes.put(ttsFontShearAttrName, sb.toString());
            } else if (name.equals("詰")) {
                String kerning;
                if (count < 0)
                    kerning = "auto";
                else if (count == 0)
                    kerning = "none";
                else
                    kerning = "normal";
                attributes.put(ttsFontKerningAttrName, kerning);
            } else if (name.equals("幅広")) {
                attributes.put(ttsFontSizeAttrName, "1.0em 1.5em");
            } else if (name.equals("倍角")) {
                attributes.put(ttsFontSizeAttrName, "1.0em 2.0em");
            } else if (name.equals("半角")) {
                attributes.put(ttsFontSizeAttrName, "1.0em 0.5em");
            } else if (name.equals("拗音")) {
                attributes.put(ttsFontSizeAttrName, "1.0em 0.9em");
            } else if (name.equals("幅")) {
                int stretch;
                if (count < 0)
                    stretch = 100;
                else if (count < 5)
                    stretch = 50;
                else if (count < 20)
                    stretch = count * 10;
                else
                    stretch = 200;
                attributes.put(ttsFontSizeAttrName, "1.0em " + Double.toString((double) stretch / 100.0) + "em");
            } else if (name.equals("寸")) {
                int scale;
                if (count < 0)
                    scale = 100;
                else if (count < 5)
                    scale = 50;
                else if (count < 20)
                    scale = count * 10;
                else
                    scale = 200;
                attributes.put(ttsFontSizeAttrName, ((double) scale / 100.0) + "em");
            }
            updateStyles(s, styles);
        }
        public void updateStyles(Span s, Set<QName> styles) {
            if (s.getFontFamily() != null) {
                styles.add(ttsFontFamilyAttrName);
            }
            if (s.getFontSize() != null) {
                styles.add(ttsFontSizeAttrName);
            }
            if (s.getFontStyle() != null) {
                styles.add(ttsFontStyleAttrName);
            }
            if (s.getTextAlign() != null) {
                styles.add(ttsTextAlignAttrName);
            }
            for (QName qn : s.getOtherAttributes().keySet()) {
                String ns = qn.getNamespaceURI();
                if ((ns != null) && ns.equals(NAMESPACE_TT_STYLE))
                    styles.add(qn);
            }
        }
    }

    private static class AnnotatedRange {
        public Annotation annotation;
        public int start;
        public int end;
        public AnnotatedRange(Annotation annotation, int start, int end) {
            this.annotation = annotation;
            this.start = start;
            this.end = end;
        }
    };

    private static class TextAttribute extends AttributedCharacterIterator.Attribute {
        private static final long serialVersionUID = -2459432329768198134L;
        public TextAttribute(String name) {
            super(name);
        }
        public static final TextAttribute ANNOTATION = new TextAttribute("ANNOTATION");
    }

    public static class Screen {
        private Locator locator;
        private int number;
        private int lastNumber;
        private char letter;
        private ClockTime in;
        private ClockTime out;
        private List<Attribute> attributes;
        private AttributedString text;
        public Screen(Locator locator, int lastNumber) {
            this.locator = locator;
            this.lastNumber = lastNumber;
        }
        public Locator getLocator() {
            return locator;
        }
        public int getNumber() {
            return number;
        }
        public int getLetter() {
            return letter;
        }
        public ClockTime getInTime() {
            return in;
        }
        public ClockTime getOutTime() {
            return out;
        }
        public AttributedString getText() {
            return text;
        }
        public boolean sameNumberAsLastScreen() {
            return (number == 0) || (number == lastNumber);
        }
        public boolean empty() {
            if (hasInOutCodes())
                return false;
            else if ((attributes != null) && (attributes.size() > 0))
                return false;
            else if (text != null)
                return false;
            else
                return true;
        }
        public boolean hasInOutCodes() {
            return (in != null) && (out != null);
        }
        public String getInTimeExpression() {
            return makeTimeExpression(in);
        }
        public String getOutTimeExpression() {
            return makeTimeExpression(out);
        }
        public void addAttribute(Attribute a) {
            if (attributes == null)
                attributes = new java.util.ArrayList<Attribute>();
            attributes.add(a);
        }
        public String getPlacement(boolean[] retGlobal) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    if (a.hasPlacement()) {
                        return a.getPlacement(retGlobal);
                    }
                }
            }
            return null;
        }
        public String getAlignment(boolean[] retGlobal) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    if (a.hasAlignment()) {
                        return a.getAlignment(retGlobal);
                    }
                }
            }
            return null;
        }
        public String getShear(boolean[] retGlobal) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    if (a.hasShear()) {
                        return a.getShear(retGlobal);
                    }
                }
            }
            return null;
        }
        public String getKerning(boolean[] retGlobal) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    if (a.hasKerning()) {
                        return a.getKerning(retGlobal);
                    }
                }
            }
            return null;
        }
        public String getTypeface(boolean[] retGlobal) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    if (a.hasTypeface()) {
                        return a.getTypeface(retGlobal);
                    }
                }
            }
            return null;
        }
    }

    private static final ObjectFactory ttmlFactory = new ObjectFactory();
    private class State {
        private Division division;              // current division being constructed
        private Paragraph paragraph;            // current pargraph being constructed
        private String globalPlacement;         // global placement
        private String placement;               // current screen placement
        private String globalAlignment;         // global alignment
        private String alignment;               // current screen alignment
        private String globalShear;             // global italics
        private String shear;                   // current screen shear
        private String globalKerning;           // global kerning
        private String kerning;                 // current screen kerning
        private String globalTypeface;          // global typeface
        private String typeface;                // current screen kerning
        private Map<String,Region> regions;     // active regions
        private Set<QName> styles;
        public State() {
            this.division = ttmlFactory.createDivision();
            this.globalPlacement = defaultPlacement;
            this.globalAlignment = defaultAlignment;
            this.globalShear = defaultShear;
            this.globalKerning = defaultKerning;
            this.globalTypeface = defaultTypeface;
            this.regions = new java.util.TreeMap<String,Region>();
            this.styles = new java.util.HashSet<QName>();
        }
        public void process(List<Screen> screens) {
            for (Screen s: screens)
                process(s);
            finish();
        }
        public void populate(Head head) {
            // styling
            Styling styling = ttmlFactory.createStyling();
            head.setStyling(styling);
            // layout
            Layout layout = ttmlFactory.createLayout();
            for (Region r : regions.values())
                layout.getRegion().add(r);
            head.setLayout(layout);
        }
        public void populate(Body body, String defaultRegion) {
            if (hasParagraph()) {
                if (defaultRegion != null) {
                    body.getOtherAttributes().put(regionAttrName, defaultRegion);
                    maybeAddRegion(defaultRegion);
                }
                if (defaultWhitespace != null)
                    body.getOtherAttributes().put(xmlSpaceAttrName, defaultWhitespace);
                body.getDiv().add(division);
            }
        }
        private void finish() {
            process((Screen) null);
        }
        private boolean hasParagraph() {
            return !division.getBlockOrEmbeddedClass().isEmpty();
        }
        private void process(Screen s) {
            Paragraph p = this.paragraph;
            if (isNonContinuation(s)) {
                Paragraph pNew = populate(division, p);
                if (s != null) {
                    String begin;
                    String end;
                    if (s.hasInOutCodes()) {
                        begin = s.getInTimeExpression();
                        end = s.getOutTimeExpression();
                    } else {
                        begin = p.getBegin();
                        end = p.getEnd();
                    }
                    pNew.setBegin(begin);
                    pNew.setEnd(end);
                    resetScreenAttributes();
                    populateStyles(pNew, mergeDefaults(s.attributes));
                    populateText(pNew, s.text, false, getBlockDirection(s));
                    updateScreenAttributes(s);
                }
                this.paragraph = pNew;
            } else {
                populateText(p, s.text, true, getBlockDirection(s));
            }
        }
        private Direction getBlockDirection(Screen s) {
            Direction d;
            String p = getPlacement(s, null);
            if (p == null)
                p = placement;
            if (p == null)
                p = globalPlacement;
            if (p.startsWith("縦"))
                d = Direction.RL;
            else
                d = Direction.TB;
            return d;
        }
        private void resetScreenAttributes() {
            placement = globalPlacement;
            shear = globalShear;
            kerning = globalKerning;
            typeface = globalTypeface;
        }
        private void updateScreenAttributes(Screen s) {
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
        private String getPlacement(Screen s, boolean[] retGlobal) {
            return s.getPlacement(retGlobal);
        }
        private String getAlignment(Screen s, boolean[] retGlobal) {
            return s.getAlignment(retGlobal);
        }
        private String getShear(Screen s, boolean[] retGlobal) {
            return s.getShear(retGlobal);
        }
        private String getKerning(Screen s, boolean[] retGlobal) {
            return s.getKerning(retGlobal);
        }
        private String getTypeface(Screen s, boolean[] retGlobal) {
            return s.getTypeface(retGlobal);
        }
        private boolean isNonContinuation(Screen s) {
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
        private Paragraph populate(Division d, Paragraph p) {
            if ((p != null) && (p.getContent().size() > 0)) {
                maybeWrapContentInSpan(p);
                maybeAddRegion(p.getOtherAttributes().get(regionAttrName));
                d.getBlockOrEmbeddedClass().add(p);
            }
            return ttmlFactory.createParagraph();
        }
        private void maybeAddRegion(String id) {
            if (id != null) {
                if (!regions.containsKey(id)) {
                    Region r = ttmlFactory.createRegion();
                    r.setId(id);
                    populateStyles(r, id);
                    regions.put(id, r);
                }
            }
        }
        private void populateText(Paragraph p, AttributedString as, boolean insertBreakBefore, Direction blockDirection) {
            if (as != null) {
                List<Serializable> content = p.getContent();
                if (insertBreakBefore)
                    content.add(ttmlFactory.createBr(ttmlFactory.createBreak()));
                AttributedCharacterIterator aci = as.getIterator();
                aci.first();
                StringBuffer sb = new StringBuffer();
                while (aci.current() != CharacterIterator.DONE) {
                    int i = aci.getRunStart();
                    int e = aci.getRunLimit();
                    Annotation annotation = (Annotation) aci.getAttribute(TextAttribute.ANNOTATION);
                    while (i < e) {
                        sb.append(aci.setIndex(i++));
                    }
                    String text = sb.toString();
                    if (annotation != null)
                        content.add(ttmlFactory.createSpan(createSpan(text, (Attribute) annotation.getValue(), blockDirection)));
                    else
                        content.add(text);
                    sb.setLength(0);
                    aci.setIndex(e);
                }
            }
        }
        private Span createSpan(String text, Attribute a, Direction blockDirection) {
            if (a.isEmphasis())
                return createEmphasis(text, a, blockDirection);
            else if (a.isRuby())
                return createRuby(text, a, blockDirection);
            else if (a.isCombine())
                return createCombine(text, a, blockDirection);
            else
                return createStyledSpan(text, a);
        }
        private Span createEmphasis(String text, Attribute a, Direction blockDirection) {
            Span s = ttmlFactory.createSpan();
            StringBuffer sb = new StringBuffer();
            sb.append("dot");
            RubyPosition rp = a.getRubyPosition(blockDirection);
            if ((rp != null) && (rp != RubyPosition.AUTO)) {
                sb.append(' ');
                sb.append(rp.name().toLowerCase());
            }
            s.getOtherAttributes().put(ttsTextEmphasisAttrName, sb.toString());
            s.getContent().add(text);
            return s;
        }
        private Span createRuby(String text, Attribute a, Direction blockDirection) {
            Span sBase = ttmlFactory.createSpan();
            sBase.getOtherAttributes().put(ttsRubyAttrName, "base");
            sBase.getContent().add(text);
            Span sText = ttmlFactory.createSpan();
            sText.getOtherAttributes().put(ttsRubyAttrName, "text");
            sText.getContent().add(a.annotation);
            RubyPosition rp = a.getRubyPosition(blockDirection);
            if (rp != null)
                sText.setRubyPosition(rp);
            Span sCont = ttmlFactory.createSpan();
            sCont.getOtherAttributes().put(ttsRubyAttrName, "container");
            sCont.getContent().add(ttmlFactory.createSpan(sBase));
            sCont.getContent().add(ttmlFactory.createSpan(sText));
            return sCont;
        }
        private Span createCombine(String text, Attribute a, Direction blockDirection) {
            if (blockDirection != Direction.TB) {
                Span s = ttmlFactory.createSpan();
                s.getOtherAttributes().put(ttsTextCombineAttrName, "all");
                s.getContent().add(text);
                return s;
            } else
                return createStyledSpan(text, a);
        }
        private Span createStyledSpan(String text, Attribute a) {
            Span s = ttmlFactory.createSpan();
            populateStyles(s, a);
            s.getContent().add(text);
            return s;
        }
        private List<Attribute> mergeDefaults(List<Attribute> attributes) {
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
            List<Attribute> mergedAttributes = attributes != null ? new java.util.ArrayList<Attribute>(attributes) : new java.util.ArrayList<Attribute>();
            if (!hasAlignment) {
                String v = alignment;
                if (v == null)
                    v = globalAlignment;
                if (v != null) {
                    AttributeSpecification as = knownAttributes.get(v);
                    if (as != null)
                        mergedAttributes.add(new Attribute(as, -1, false, null, null));
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
                        mergedAttributes.add(new Attribute(as, count, false, null, null));
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
                        mergedAttributes.add(new Attribute(as, -1, false, null, null));
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
                        mergedAttributes.add(new Attribute(as, count, false, null, null));
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
                        mergedAttributes.add(new Attribute(as, -1, false, null, null));
                }
            }
            return mergedAttributes;
        }
        private void populateStyles(Region r, String id) {
        }
        private void populateStyles(Paragraph p, List<Attribute> attributes) {
            if (attributes != null) {
                for (Attribute a : attributes) {
                    a.populate(p, styles, defaultRegion);
                }
            }
        }
        private void populateStyles(Span s, Attribute a) {
            a.populate(s, styles);
        }
        private void maybeWrapContentInSpan(Paragraph p) {
            String a = (alignment != null) ? alignment : globalAlignment;
            if (isMixedAlignment(a)) {
                TextAlign sa, pa;
                if (a.equals("中頭")) {
                    pa = TextAlign.CENTER;
                    sa = TextAlign.START;
                } else if (a.equals("中末")) {
                    pa = TextAlign.CENTER;
                    sa = TextAlign.END;
                } else {
                    pa = TextAlign.CENTER;
                    sa = null;
                }
                if (sa != null) {
                    Span s = ttmlFactory.createSpan();
                    s.getContent().addAll(p.getContent());
                    s.setTextAlign(sa);
                    p.getContent().clear();
                    p.getContent().add(ttmlFactory.createSpan(s));
                    p.setTextAlign(pa);
                }
            }
        }
        private boolean isMixedAlignment(String alignment) {
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

    public static class Results {
        private static final String NOURI = "*URI NOT AVAILABLE*";
        private static final String NOENCODING = "*ENCODING NOT AVAILABLE*";
        private String uriString;
        private boolean succeeded;
        private int code;
        private int flags;
        private int errorsExpected;
        private int errors;
        private int warningsExpected;
        private int warnings;
        private String encodingName;
        private Document document;
        public Results() {
            this.uriString = NOURI;
            this.succeeded = false;
            this.code = RV_USAGE;
            this.encodingName = NOENCODING;
        }
        Results(String uriString, int rv, int errorsExpected, int errors, int warningsExpected, int warnings, Charset encoding, Document document) {
            this.uriString = uriString;
            this.succeeded = rvSucceeded(rv);
            this.code = rvCode(rv);
            this.flags = rvFlags(rv);
            this.errorsExpected = errorsExpected;
            this.errors = errors;
            this.warningsExpected = warningsExpected;
            this.warnings = warnings;
            if (encoding != null)
                this.encodingName = encoding.name();
            else
                this.encodingName = "unknown";
            this.document = document;
        }
        public String getURIString() {
            return uriString;
        }
        public boolean getSucceeded() {
            return succeeded;
        }
        public int getCode() {
            return code;
        }
        public int getFlags() {
            return flags;
        }
        public int getErrorsExpected() {
            return errorsExpected;
        }
        public int getErrors() {
            return errors;
        }
        public int getWarningsExpected() {
            return warningsExpected;
        }
        public int getWarnings() {
            return warnings;
        }
        public String getEncodingName() {
            return encodingName;
        }
        public Document getDocument() {
            return document;
        }
    }

    public static class ExternalParametersStore implements ExternalParameters {
        private Map<String, Object> parameters = new java.util.HashMap<String, Object>();
        public Object getParameter(String name) {
            return parameters.get(name);
        }
        public Object setParameter(String name, Object value) {
            return parameters.put(name, value);
        }
    }

}

// Local Variables:
// coding: utf-8-unix
// End:
