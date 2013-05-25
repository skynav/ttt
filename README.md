# TTV

Timed Text Markup Language (TTML) Validation Tools

The `TTV` tool is used to validate the contents of a [TTML](http://www.w3.org/TR/ttaf1-dfxp/) document represented using XML as a concrete encoding, and where this document is validated against a specific validation *model*.

Validation is performed in four phases in the following order:

 1. Check resource presence, encoding, and decodability (into characters).
 2. Check well-formedness.
 3. Check schema validity.
 4. Check semantic validity.

If errors occur in some phase, then the following phases are skipped.

The first phase, resource checking, attempts to determine (sniff) the character encoding of the resource. To do this, it uses a combination of a BOM (byte order mark) prefix, if present, and an XML encoding declaration, if present. If neither are present (or discernable), then it treats the result as ASCII. Once the encoding has been sniffed, an attempt is made to decode the entire resource using that encoding. If no encoding can be determined, is determined but not supported (by the platform), is determined but not permitted (by TTML), or if the resource cannot be successfully decoded without error using the encoding, then this phase fails.

The encodings which `TTV` can successfully resolve include:

 * `US-ASCII`
 * `UTF-8`
 * `UTF-16LE`
 * `UTF-16BE`
 * `UTF-32LE`
 * `UTF-32BE`

If both BOM is present and XML declaration is present and the two encodings differ, then the BOM encoding is used.

The second phrase performs well-formedness testing by attempting to parse the resource as XML but with validation disabled. If the syntax of the resource does not adhere to the XML specification, then this phase fails.

The third phase performs schema validity testing using an XSD (XML Schema Definition) schema. By default, the published TTML XSD schema is used. However, provisions are being made to support alternative schemas, either built-in to the tool or provided by the user. During this phase, elements and attributes in foreign (non-TTML and non-XML) namespaces are pruned by a filtering process before attempting validation. As a consequence, the potential validity of foreign namespace vocabulary is not assessed. [A future version of this tool may provide a mechanism to validate foreign vocabulary.] If a schema validation error occurs, then this phase fails.

The fourth phase performs additional semantic (and some syntactic) testing on the resource that is beyond what is tested by the previous three phases. For example, some TTML style properties are specified by the TTML schema using the `xs:string` type, which does not test the form of that string to determine compliance with TTML. In this fourth phase, these values are further tested to determine if they comply with TTML syntactic and semantic rules. In addition, a variety of other constraints specified by TTML are tested during this phase.

The following `xs:string` schema typed TTML style properties are tested for syntactic correctness during this fourth phase:

 * `tts:color`
 * `tts:backgroundColor`
 * `tts:extent`
 * `tts:fontFamily`
 * `tts:fontSize`
 * `tts:lineHeight`
 * `tts:opacity`
 * `tts:origin`
 * `tts:padding`
 * `tts:textOutline`
 * `tts:zIndex`

In addition, the following TTML attributes' values are similarly tested:

 * `begin`
 * `dur`
 * `end`
 * `ttp:cellResolution`
 * `ttp:frameRateMultiplier`
 * `ttp:pixelAspectRatio`

A number of additional semantic constraints are tested in the fourth phase, which will (in the meausure of time), be summarizeed here.

A validation *model* includes the following information:

 * a name (exposed via the `--list-models` option)
 * a schema resource name (path), used to locate the schema (XSD) resource
 * a namespace URI string, which must match the target namespace of the schema
 * a JAXB context path, used when creating the JAXB context for unmarshalling
 * a collection of acceptable root JAXB content classes, used to validate the root element
 * additional model specific validation tools used during the semantic validation phase

## Building

In order to build **ttv**, run *ant* with one of the following targets:

`ant build`

Using the `build` target will (re)create the `ttv.jar` target under `bld/artifacts`.

`ant clean-build`

Using the `clean-build` target will first clean (delete) previously built classes and artifacts, and then performs a `build`.

The full set of (public) `ant` targets can be listed by using `ant -p`, which presently outputs the following:

    Main targets:

    build            Build TTV
    clean-build      Clean and build TTV
    clean-test       Clean, build, and test TTV
    run-valid-tests  Run validator with valid TTML10 input files
    test             Run all TTV test suites
    test-apps        Run TTV application tests
    test-ttml10      Run TTML10 tests
    test-validator   Run TTV validator application test
    test-xml         Run XML tests

    Default target: clean-test

## Running

In order to run the validator, use the following (or an equivalent):

`java -jar bld/artifacts/ttv.jar ttml.xml`

Usage information can be obtained by using:

`java -jar bld/artifacts/ttv.jar --help`

At present, this will output the following:

<pre>
Timed Text Validator (TTV) 0.0.0dev
Usage: java -jar ttv.jar [options] URL*
  Short Options:
    -d                       - see --debug
    -q                       - see --quiet
    -v                       - see --verbose
    -?                       - see --help
  Long Options:
    --debug                  - enable debug output (may be specified multiple times to increase debug level)
    --debug-exceptions       - enable stack traces on exceptions (implies --debug)
    --disable-warnings       - disable warnings
    --help                   - show usage help
    --list-models            - list known models
    --model NAME             - specify model name (default: ttml10)
    --quiet                  - don't show banner
    --verbose                - enable verbose output (may be specified multiple times to increase verbosity level)
    --treat-warning-as-error - treat warning as error
  Non-Option Arguments:
    URL                      - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `TTV` attempts to resolve it against the current working directory.

Run `TTV` with the `--list-models` option to determine which validation models are supported, and which is the default model.

## Testing

We use Junit 4 to perform tests on `TTV`. You should have previously installed the appropriate Junit 4 files in your `ant` runtime in order to use these features.

A number of test targets are listed above for invocation from `ant`. The `clean-test` target is useful in order to perform a clean build then run all tests.

In addition, the `run-valid-tests` target will use the command line (not Junit) invocation of `TTV` in order to run `TTV` on all valid test files included in the Junit testing process.

The following shows the output of running `ant run-valid-tests`:

<pre>
Buildfile: /Users/glenn/work/ttv/build.xml
Trying to override old definition of task javac

run-valid-tests:
     [java] Timed Text Validator (TTV) 0.0.0dev
     [java] I:Validating {/Users/glenn/work/ttv/tst/resources/com/skynav/ttv/app/ttml10-valid-simple.xml}.
     [java] I:Checking resource presence and encoding...
     [java] I:Resource encoding sniffed as UTF-8.
     [java] I:Resource length 148 bytes, decoded as 148 Java characters (char).
     [java] I:Checking well-formedness...
     [java] I:Checking validity...
     [java] I:Checking semantic validity...
     [java] I:Passed.
     [java] I:Validating {/Users/glenn/work/ttv/tst/resources/com/skynav/ttv/app/ttml10-valid-all-elements.xml}.
     [java] I:Checking resource presence and encoding...
     [java] I:Resource encoding sniffed as US-ASCII.
     [java] I:Resource length 2333 bytes, decoded as 2333 Java characters (char).
     [java] I:Checking well-formedness...
     [java] I:Checking validity...
     [java] I:Checking semantic validity...
     [java] I:Passed.
     [java] I:Validating {/Users/glenn/work/ttv/tst/resources/com/skynav/ttv/app/ttml10-valid-all-styles.xml}.
     [java] I:Checking resource presence and encoding...
     [java] I:Resource encoding sniffed as US-ASCII.
     [java] I:Resource length 17598 bytes, decoded as 17598 Java characters (char).
     [java] I:Checking well-formedness...
     [java] I:Checking validity...
     [java] I:Checking semantic validity...
     [java] I:Passed.
     [java] I:Validating {/Users/glenn/work/ttv/tst/resources/com/skynav/ttv/app/ttml10-valid-all-parameters.xml}.
     [java] I:Checking resource presence and encoding...
     [java] I:Resource encoding sniffed as US-ASCII.
     [java] I:Resource length 1138 bytes, decoded as 1138 Java characters (char).
     [java] I:Checking well-formedness...
     [java] I:Checking validity...
     [java] I:Checking semantic validity...
     [java] I:Passed.
     [java] I:Validating {/Users/glenn/work/ttv/tst/resources/com/skynav/ttv/app/ttml10-valid-foreign.xml}.
     [java] I:Checking resource presence and encoding...
     [java] I:Resource encoding sniffed as UTF-8.
     [java] I:Resource length 281 bytes, decoded as 281 Java characters (char).
     [java] I:Checking well-formedness...
     [java] I:Checking validity...
     [java] I:Checking semantic validity...
     [java] I:Passed.
     [java] I:Passed 5 resources.

BUILD SUCCESSFUL
Total time: 2 seconds
</pre>

## Notes

 * At present, `TTV` is being developed using the following versions of tools:

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

 * The primary developer (skynavga) is using Mac OSX 10.8.3 on a MBP 15" Retina.

 * Work on the first three testing phases is essentially complete as of May 24, 2013; however, the fourth phase remains work in progress, and, as such, users should not rely upon it until this documentation indicates it is (reasonably) complete.

