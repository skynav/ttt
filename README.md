# ttv

Timed Text Markup Language (TTML) Verification Tools

The `ttv` tool is used to verify the contents of a [TTML](http://www.w3.org/TR/ttaf1-dfxp/) document represented using XML as a concrete encoding, and where this document is verified against a specific verification *model*.

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

If both BOM is present and XML declaration is present and the two encodings differ, then the BOM encoding is used.

The second phase performs well-formedness testing by attempting to parse the resource as XML but with validation disabled. If the syntax of the resource does not adhere to the XML specification, then this phase fails.

The third phase performs schema validity testing using an XSD (XML Schema Definition) schema. The schema used during validation is determined by the specified (or default) verification model. Different models may specify different schemas. If a schema validation error occurs, then this phase fails.

Unless `--treat-foreign-as` is specified as `allow`, elements and attributes in foreign (non-TTML and non-XML) namespaces are pruned by a filtering process before attempting schema validation. In this case, potential validity of foreign namespace vocabulary is not assessed. If `--treat-foreign-as` is specified as `allow`, then foreign vocabulary is not pruned, which, depending on the schema, may result in schema validation errors if the schema is not designed to permit foreign attributes or elements.

The fourth phase performs additional semantic (and some syntactic) testing on the resource that is beyond what is tested by the previous three phases. For example, some TTML attributes are typed by the TTML schema as `xs:string`, which does not test the form of that string to determine compliance with TTML. In this fourth phase, these attributes are further verified to determine if they comply with TTML syntactic and semantic rules. In addition, a variety of other constraints specified by TTML are tested during this phase.

If desired, the `--until-phase` option may be used to cause verification to stop at a particular phase, thus skipping subsequent phases.

The following `xs:string` schema typed attributes are tested for syntactic correctness during this fourth phase:

 * `tts:color`
 * `tts:backgroundColor`
 * `tts:extent`
 * `tts:fontFamily`
 * `tts:fontSize`
 * `tts:lineHeight`
 * `tts:origin`
 * `tts:padding`
 * `tts:textOutline`
 * `tts:zIndex`

In addition, the following similarly typed attributes are similarly verified in this phase:

 * `begin`
 * `dur`
 * `end`
 * `ttp:cellResolution`
 * `ttp:frameRateMultiplier`
 * `ttp:pixelAspectRatio`

## Additional Semantic Tests

A number of additional semantic constraints are tested in the fourth phase:

 * Error if style attribute IDREF does not reference a style element. (8.2.1)
 * Error if style attribute IDREF references a style element that is not a descendant of styling element. (8.2.1)
 * Warn if same IDREF appears more than once in specified tts:style attribute without intervening IDREF. (8.2.1)
 * Error if tts:extent attribute uses a negative length value. (8.2.7)
 * Error if tts:extent on root (tt) element specifies non-pixel unit for either width or height. (8.2.7)
 * Notify if tts:fontFamily attribute uses quoted generic family name. (8.2.8)
 * Error if tts:fontSize attribute uses a negative length value. (8.2.9)
 * Error if tts:fontSize attribute uses different units when two length expressions are specified. (8.2.9)
 * Error if tts:lineHeight attribute uses a negative length value. (8.2.12)
 * Warn if tts:opacity attribute uses a legal xs:float value outside the range [0,1]. (8.2.13)
 * Warn if tts:origin attribute uses a negative length value. (8.2.14)
 * Error if tts:padding attribute uses a negative length value. (8.2.16)
 * Error if tts:textOutline attribute uses a negative length value for thickness or blur radius. (8.2.20)
 * Error if loop in sequence of chained style references. (8.4.1.3)
 * Error if region attribute IDREF does not reference a region element. (9.2.1)
 * Error if ttp:clockMode is smpte, ttp:markerMode is discontinuous, and an otherwise well-formed dur attribute is specified. (10.2.3)
 * Error if time expression expressed as clock-time and minutes are greater than 59 (10.3.1)
 * Error if time expression expressed as clock-time and seconds (including fractional part) are greater than 60 (10.3.1)
 * Error if time expression expressed as clock-time and frames are not less than frame rate. (10.3.1)
 * Error if time expression expressed as clock-time and sub-frames are not less than sub-frame rate. (10.3.1)
 * Error if time expression uses frame component or `f` metric when tts:timeBase is clock. (10.3.1)

## Verification Model

A verification *model* includes the following information:

 * a name (exposed via the `--show-models` option)
 * a schema resource name (path), used to locate the schema (XSD) resource
 * a namespace URI string, which must match the target namespace of the schema
 * a JAXB context path, used when creating the JAXB context for unmarshalling
 * a collection of acceptable root JAXB content classes, used to verify the root element
 * additional model specific verification tools used during the semantic verification phase

## Building

In order to build **ttv**, run *ant* with one of the following targets:

`ant build`

Using the `build` target will (re)create the `ttv.jar` target under `bld/artifacts`.

`ant clean-build`

Using the `clean-build` target will first clean (delete) previously built classes and artifacts, and then perform a `build`.

The full set of (public) `ant` targets can be listed by using `ant -p`, which presently outputs the following:

    Main targets:

    build            Build TTV
    clean-build      Clean and build TTV
    clean-test       Clean, build, and test TTV
    run-valid        Run verifier with valid TTML10 input files
    test             Run all TTV test suites
    test-apps        Run TTV application tests
    test-ttml10      Run TTML10 tests
    test-verifier    Run TTV verification test suite
    test-xml         Run XML tests

    Default target: clean-test

## Running

In order to run the verifier, use the following (or an equivalent):

`java -jar bld/artifacts/ttv.jar ttml.xml`

Usage information can be obtained by using:

`java -jar bld/artifacts/ttv.jar --help`

At present, this will output the following:

<pre>
Timed Text Verifier (TTV) [0.0.0dev] Copyright 2013 Skynav, Inc.
Usage: java -jar ttv.jar [options] URL*
  Short Options:
    -d                       - see --debug
    -q                       - see --quiet
    -v                       - see --verbose
    -?                       - see --help
  Long Options:
    --debug                  - enable debug output (may be specified multiple times to increase debug level)
    --debug-exceptions       - enable stack traces on exceptions (implies --debug)
    --disable-warnings       - disable warnings (both hide and don't count warnings)
    --help                   - show usage help
    --hide-warnings          - hide warnings (but count them)
    --model NAME             - specify model name (default: ttml10)
    --quiet                  - don't show banner
    --show-models            - show built-in verification models (use with --verbose to show more details)
    --show-repository        - show source code repository information
    --verbose                - enable verbose output (may be specified multiple times to increase verbosity level)
    --treat-foreign-as TOKEN - specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: warning)
    --treat-warning-as-error - treat warning as error (overrides --disable-warnings)
    --until-phase PHASE      - verify up to and including specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: all)
  Non-Option Arguments:
    URL                      - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `ttv` attempts to resolve it against the current working directory.

Run `ttv` with the `--show-models` option to determine which verification models are supported, and which is the default model.

## Testing

Junit 4 is used to perform tests on `ttv`. You should have previously installed the appropriate Junit 4 files in your `ant` runtime in order to use these features.

A number of test targets are listed above for invocation from `ant`. The `clean-test` target is useful in order to perform a clean build then run all tests.

In addition, the `run-valid` target will use the command line (not Junit) invocation path in order to run `ttv` on all valid test files included in the Junit testing process.

## Notes

 * At present, `ttv` is being developed using the following versions of tools:

<pre>
    $ java -version
    java version "1.6.0_45"
    Java(TM) SE Runtime Environment (build 1.6.0_45-b06-451-11M4406)
    Java HotSpot(TM) 64-Bit Server VM (build 20.45-b01-451, mixed mode)

    $ ant -version
    Apache Ant(TM) version 1.8.2 compiled on June 20 2012

    $ java -cp /usr/share/java/junit.jar junit.runner.Version
    4.10
</pre>

 * An (Helios) Eclipse workspace and `ttv` project are available under the `.eclipse` directory.

 * The primary developer (skynavga) is using Mac OSX 10.8.3 on an MBP 15" Retina.

 * Work on the first three testing phases is essentially complete as of May 24, 2013; however, the fourth phase remains work in progress, and, as such, users should not rely upon it until this documentation indicates it is (reasonably) complete.

## To Do (Essential)

 * Warn if neither ttp:profile attribute nor ttp:profile element are present. (3.3 P3)
 * Warn if both ttp:profile attribute and ttp:profile element are present. (5.2 P7)
 * Error if xml:base on ttp:features is not absolute. (6.1.2 P4)
 * Error if xml:base on ttp:features isn't equal to standard feature namespace "http://www.w3.org/ns/ttml/feature/". (6.1.2 P4)
 * Error if absolutized feature designator doesn't conform to feature-designation defined in D.1. (6.1.3. P4)
 * Warn if absolutized feature designator is not a known standard designator. ([Issue 221](https://www.w3.org/AudioVideo/TT/tracker/issues/221)).
 * Error if xml:base on ttp:extensions is not absolute. (6.1.4 P4)
 * Error if absolutized extension designator doesn't conform to extension-designation defined in E.1. (6.1.5. P4)
 * Error if actor element's agent attribute does not reference an agent element. (12.1.7 P3)
 * Error if ttm:agent attribute IDREF does not reference an agent element. (12.2.1 P2)
 * Warn if same IDREF appears more than once in specified ttm:agent attribute. (12.2.1)
 * Update schema to use constrained definition of role token syntax, and not just NMTOKENS. See ttd:role data type.
 * Warn if same token appears more than once in specified ttm:role attribute. (12.2.2)
 * Error if other extension namespace uses form that doesn't take a fragment identifier. (E.1 P4)

## To Do (Optional)

 * Notify if use attribute on ttp:profile references a non-standard profile.
 * Notify if ttp:role uses an "x-" extension role.
 * Notify on unreferenced style element.
 * Notify on unreferenced region element.
 * Notify on unreferenced ttm:agent element.
 * Notify on presence of both end and dur such that dur (simple duration) is not same as end minus begin (preliminary active duration).
 * Notify computed time interval of body based on content timing alone.
 * Notify (or warn) if computed color or computed background color are problematic for color impaired viewers.
 * Notify (or warn) if computed color and computed background color are low contrast.
 * Notify (or warn) if style property specified on content element that cannot apply to any content element, e.g., extent, opacity, origin.
 * Notify (or warn) if style property specified on content element that is initial value and is not overriding an inherited value.
 * Notify (or warn) if redundant style IDREF is used twice in a row in style attribute.