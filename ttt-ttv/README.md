# ttv

Timed Text Markup Language (TTML) Verification Tools

The `ttv` tool is used to verify the contents of a [TTML](http://www.w3.org/TR/ttml1/) document represented using XML as a concrete encoding, and where this document is verified against a specific verification *model*.

Verification is performed in four phases in the following order:

 1. Verify resource presence, encoding, and decodability (into characters).
 2. Verify well-formedness.
 3. Verify schema validity.
 4. Verify semantic validity.

If errors occur in some phase, then the following phases are skipped.

The first phase, resource verification, attempts to determine (sniff) the character encoding of the resource. To do this, it uses a combination of a BOM (byte order mark) prefix, if present, and an XML encoding declaration, if present. If neither are present (or discernable), then it treats the result as ASCII. Once the encoding has been sniffed, an attempt is made to decode the entire resource using that encoding. If no encoding can be determined, is determined but not supported (by the platform), is determined but not permitted (by TTML), or if the resource cannot be successfully decoded without error using the encoding, then this phase fails.

The encodings which `ttv` can successfully resolve include:

 * `US-ASCII`
 * `UTF-8`
 * `UTF-16LE`
 * `UTF-16BE`
 * `UTF-32LE`
 * `UTF-32BE`

If both BOM is present and XML declaration is present and the two encodings differ, then the BOM encoding is used. If desired, the resource's character encoding may
be overridden by using the `--force-encoding` option. If used, then no encoding sniffing occurs, and any internal XML encoding declaration is ignored in favor of the
overriding encoding.

The second phase performs well-formedness testing by attempting to parse the resource as XML but with validation disabled. If the syntax of the resource does not adhere to the XML specification, then this phase fails.

The third phase performs schema validity testing using an XSD (XML Schema Definition) schema. The schema used during validation is determined by the specified (or default) verification model. Different models may specify different schemas. If a schema validation error occurs, then this phase fails.

Unless `--treat-foreign-as` is specified as `allow`, elements and attributes in foreign (non-TTML and non-XML) namespaces are pruned by a filtering process before attempting schema validation. In this case, potential validity of foreign namespace vocabulary is not assessed. If `--treat-foreign-as` is specified as `allow`, then foreign vocabulary is not pruned, which, depending on the schema, may result in schema validation errors if the schema is not designed to permit foreign attributes or elements. Additional schemas that cover foreign vocabulary may be specified by using one or more `--extension-schema` options.

The fourth phase performs additional semantic (and some syntactic) testing on the resource that is beyond what is tested by the previous three phases. For example, some TTML attributes are typed by the TTML schema as `xs:string`, which does not test the form of that string to determine compliance with TTML. In this fourth phase, these attributes are further verified to determine if they comply with TTML syntactic and semantic rules. In addition, a variety of other constraints specified by TTML (or a derivative specifiction) are tested during this phase.

If desired, the `--until-phase` option may be used to cause verification to stop at a particular phase, thus skipping subsequent phases.

## Verification Model

A verification *model* includes the following information:

 * a name (exposed via the `--show-models` option)
 * one or more schema resource names (paths), used to locate schema (XSD) resources
 * one or more namespace URI strings, which must match the target namespaces of their respective schemas
 * a JAXB context path, used when creating the JAXB context for unmarshalling
 * a collection of acceptable root JAXB content classes, used to verify the root element
 * additional model specific verification tools used during the semantic verification phase

The model used to perform phase 3 (validity) and phase 4 (semantics) verification is determined as follows: if a `--force-model` option is present, then the forced model is used; otherwise, if the resource specifies a `ttva:model` annotation, then the annotated model is used; otherwise, if the `--model` option is present, then it is used; otherwise, the default model is used. In order to determine the default model, invoke **ttv** with the `--show-models` option.

## Building

In order to build **ttpe**, first perform the instructions at https://github.com/skynav/ttt, then run `mvn clean install` from this `ttt-ttv` directory.
Next, change directory to the standalone JAR package, ``cd ../ttt-ttv-all``, then run `mvn clean install`.

## Running

In order to run the presentation engine (from its standalone JAR), first change directory to the standalone JAR package, ``cd ../ttt-ttv-all``,
then use the following (or equivalent):

`java -jar target/ttt-ttv-all-7.1-SNAPSHOT.jar ttml.xml`

Usage information can be obtained by using:

`java -jar target/ttt-ttv-all-7.1-SNAPSHOT.jar --help`

At present, this will output the following:

<pre>
Timed Text Verifier (TTV) [7.1-SNAPSHOT] Copyright (c) 2013-19 Skynav, Inc.
Usage: java -jar ttv.jar [options] URL*
  Short Options:
    -?                                  - see --help
    -d                                  - see --debug
    -q                                  - see --quiet
    -v                                  - see --verbose
  Long Options:
    --config FILE                       - specify path to configuration file
    --debug                             - enable debug output (may be specified multiple times to increase debug level)
    --debug-exceptions                  - enable stack traces on exceptions (implies --debug)
    --debug-level LEVEL                 - enable debug output at specified level (default: 0)
    --disable-warnings                  - disable warnings (both hide and don't count warnings)
    --expect-errors COUNT               - expect count errors or -1 meaning unspecified expectation (default: -1)
    --expect-warnings COUNT             - expect count warnings or -1 meaning unspecified expectation (default: -1)
    --extension-schema NS URL           - add schema for namespace NS at location URL to grammar pool (may be specified multiple times)
    --external-duration DURATION        - specify root temporal extent duration for document processing context
    --external-extent EXTENT            - specify root container region extent for document processing context
    --external-frame-rate RATE          - specify frame rate for document processing context
    --external-wallclock-begin TIME     - specify document wallclock begin time for document processing context
    --force-encoding NAME               - force use of named character encoding, overriding default and resource specified encoding
    --force-model NAME                  - force use of named model, overriding default model and resource specified model
    --help                              - show usage help
    --hide-resource-location            - hide resource location (default: show)
    --hide-resource-path                - hide resource path (default: show)
    --hide-warnings                     - hide warnings (but count them)
    --model NAME                        - specify model name (default: ttml1)
    --no-verbose                        - disable verbose output (resets verbosity level to 0)
    --no-warn-on TOKEN                  - disable warning specified by warning TOKEN, where multiple instances of this option may be specified
    --quiet                             - don't show banner
    --reporter REPORTER                 - specify reporter, where REPORTER is null|text|xml (default: text)
    --reporter-file FILE                - specify path to file to which reporter output is to be written
    --reporter-file-append              - if reporter file already exists, then append output to it
    --reporter-file-encoding ENCODING   - specify character encoding of reporter output (default: utf-8)
    --reporter-include-source           - include source context in report messages
    --retain-reporter                   - retain (rather than reset) reporter upon run completion
    --servlet                           - configure defaults for servlet operation
    --show-models                       - show built-in verification models (use with --verbose to show more details)
    --show-repository                   - show source code repository information
    --show-resource-location            - show resource location (default: show)
    --show-resource-path                - show resource path (default: show)
    --show-validator                    - show platform validator information
    --show-warning-tokens               - show warning tokens (use with --verbose to show more details)
    --treat-foreign-as TOKEN            - specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: warning)
    --treat-warning-as-error            - treat warning as error (overrides --disable-warnings)
    --until-phase PHASE                 - verify up to specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: all)
    --verbose                           - enable verbose output (may be specified multiple times to increase verbosity level)
    --verbose-level LEVEL               - enable verbose output at specified level (default: 0)
    --warn-on TOKEN                     - enable warning specified by warning TOKEN, where multiple instances of this option may be specified
  Non-Option Arguments:
    URL                                 - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `ttv` attempts to resolve it against the current working directory.

Run `ttv` with the `--show-models` option to determine which verification models are supported, and which is the default model. If the `-v` option is added, this will additionally show the built-in schemas used by each model, as demonstrated by the following console log:

<pre>
$ java -jar target/ttt-ttv-all-7.1-SNAPSHOT.jar -v --show-models
Timed Text Verifier (TTV) [7.1-SNAPSHOT] Copyright (c) 2013-19 Skynav, Inc.
Verification Models:
  ebuttd
    XSD: com/skynav/ttv/xsd/ebuttd/ebutt_d.xsd
  imsc1
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/imsc10/imsc10.xsd
  imsc10
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/imsc10/imsc10.xsd
  imsc11
    XSD: com/skynav/ttv/xsd/ttml2/ttml2.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/imsc11/imsc11.xsd
  st2052-2010
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt-608.xsd
  st2052-2010-ttml1
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt-608.xsd
  st2052-2010-ttml2
    XSD: com/skynav/ttv/xsd/ttml2/ttml2.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/smpte/2010/smpte-tt-608.xsd
  st2052-2013
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
    XSD: com/skynav/ttv/xsd/smpte/2013/smpte-tt.xsd
    XSD: com/skynav/ttv/xsd/smpte/2013/smpte-tt-608.xsd
    XSD: com/skynav/ttv/xsd/smpte/2013/smpte-tt-708.xsd
  ttml1 (default)
    XSD: com/skynav/ttv/xsd/ttml1/ttml1.xsd
  ttml2
    XSD: com/skynav/ttv/xsd/ttml2/ttml2.xsd</pre>

## Testing

Junit 4 is used to perform tests on `ttv` from *maven*. All tests are performed by using `mvn test`. An individual test can be performed using the following syntax, for example:

<pre>
  mvn -Dtest=ValidTestCases#testValidTTML2ConditionPrimary test
</pre>

## Annotations

In order to ease the use of `ttv`, a number of *annotation* attributes are supported to provide verification configuration parameters within a document that would otherwise have to be expressed on the command line, where the namespace of these attributes is `http://skynav.com/ns/ttv/annotations`, and a prefix of `ttva` is used out of convenience (though this can change in an actual document instance). The following annotations are defined, and, if specified, must appear on the `tt:tt` (root) element of a document:

 * `expectedErrors`
 * `expectedWarnings`
 * `model`
 * `noWarnOn`
 * `processingOptions`
 * `warnOn`

The values of the `ttva:expectedErrors` and `ttva:expectedWarnings` annotations express a non-negative integer, indicating the expected number of errors and warnings to be encountered. When running `ttv` on a resource containing these annotations, a *PASS* result will be reported if the expected and actual numbers of errors and warnings match, and *FAIL* if not matched.

The values of the `ttva:warnOn` and `ttva:noWarnOn` annotations express a whitespace separated listed of warning *TOKENs* to enable or disable, as if they had been specified on the command line.

The value of the `ttva:model` annotation expresses a model name to be used to verify the document.

The value of the `ttva:processingOptions` annotation allows including (or overriding) command line or configuration file options within
the document itself. The value consists of a token that identifies the TTT tool, e.g., `ttv` or `ttpe`, followed by open and closed curly braces that
contain with the braces a sequence of option names and (optional) values (after a colon), and where each option (name and value) is separated from
one another using semicolon characters. Note that the use of the `ttva:processingOptions` annotation causes a processing restart, i.e., the first pass of the document reads the options, then restarts processing (by means of a second pass) with the new options taking effect.

An example of a document fragment that uses annotations follows (taken from the `ttv` test suite):

<pre>
&lt;tt xml:lang="" xmlns="http://www.w3.org/ns/ttml"
    xmlns:ttp="http://www.w3.org/ns/ttml#parameter"
    ttp:profile="http://www.smpte-ra.org/schemas/2052-1/2010/profiles/smpte-tt-full"
    xmlns:smpte="http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt"
    xmlns:ttva="http://skynav.com/ns/ttv/annotations"
    ttva:warnOn="all"
    ttva:expectedErrors="1"
    ttva:expectedWarnings="0"
    ttva:processingOptions="ttv { debug-level: 2; }"
    ttva:model="st2052-2010"&gt;
...
&lt;/tt&gt;
</pre>

## Notes

 * At present, `ttv` is being developed using the following versions of tools:

<pre>
    $ java -version
    java version "1.8.0_152"
    Java(TM) SE Runtime Environment (build 1.8.0_152-b16)
    Java HotSpot(TM) 64-Bit Server VM (build 25.152-b16, mixed mode)

    $ mvn -version
    Apache Maven 3.5.3 (3383c37e1f9e9b3bc3df5050c29c8aff9f295297; 2018-02-24T12:49:05-07:00)
    Maven home: /opt/local/share/java/maven3
    Java version: 1.8.0_152, vendor: Oracle Corporation
    Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre
    Default locale: en_US, platform encoding: UTF-8
    OS name: "mac os x", version: "10.13.6", arch: "x86_64", family: "mac"
</pre>

## Issues

See [Open Issues](http://github.com/skynav/ttv/issues?state=open) for current known bugs, feature requests (enhancements), etc.

