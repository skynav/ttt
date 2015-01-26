# ttpe

Timed Text Presentation Engine

## Building

In order to build **ttpe**, run *ant* with one of the following targets:

`ant build`

Using the `build` target will (re)create the `ttpe.jar` target under `bld/artifacts`.

`ant clean-build`

Using the `clean-build` target will first clean (delete) previously built classes and artifacts, and then perform a `build`.

The full set of (public) `ant` targets can be listed by using `ant -p`, which presently outputs the following:

<pre>
Main targets:

 build               Build All
 build-ttpe          Build TTPE
 clean-build         Clean and build all targets.
 clean-ttpe-build    Clean and build TTPE.
 clean-ttpe-test     Clean, build, and test TTPE
 clean-test          Clean, build, and test all targets.
 run-samples         Run converter with sample input test files
 test                Run all test suites
 test-apps           Run TTPE application tests
 test-ttpe           Run all TTPE test suites
 test-converter      Run TTPE conversion test suite

Default target: clean-test
</pre>

## Running

In order to run the presentation engine, use the following (or an equivalent):

`java -jar bld/artifacts/ttpe.jar input.xml`

Usage information can be obtained by using:

`java -jar bld/artifcats/ttpe.jar --help`

At present, this will output the following:

<pre>
Timed Text Presentation Engine (TTPE) [1.0.0dev] Copyright 2013-15 Skynav, Inc.
Usage: java -jar ttpe.jar [options] URL*
  Short Options:
    -?                                  - see --help
    -d                                  - see --debug
    -q                                  - see --quiet
    -v                                  - see --verbose
  Long Options:
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
    --font FILE                         - specify font configuration file
    --font-directory DIRECTORY          - specify path to directory where font configuration files are located
    --force-encoding NAME               - force use of named character encoding, overriding default and resource specified encoding
    --force-model NAME                  - force use of named model, overriding default model and resource specified model
    --help                              - show usage help
    --hide-resource-location            - hide resource location (default: show)
    --hide-resource-path                - hide resource path (default: show)
    --hide-warnings                     - hide warnings (but count them)
    --layout NAME                       - specify layout name (default: basic)
    --model NAME                        - specify model name (default: ttml1)
    --no-verbose                        - disable verbose output (resets verbosity level to 0)
    --no-warn-on TOKEN                  - disable warning specified by warning TOKEN, where multiple instances of this option may be specified
    --output-clean                      - clean (remove) all files matching output pattern in output directory prior to writing output
    --output-directory DIRECTORY        - specify path to directory where output is to be written
    --output-encoding ENCODING          - specify character encoding of output (default: UTF-8)
    --output-indent                     - indent output (default: no indent)
    --output-pattern PATTERN            - specify output file name pattern
    --quiet                             - don't show banner
    --renderer NAME                     - specify renderer name (default: xml)
    --reporter REPORTER                 - specify reporter, where REPORTER is null|text|xml (default: text)
    --reporter-file FILE                - specify path to file to which reporter output is to be written
    --reporter-file-append              - if reporter file already exists, then append output to it
    --reporter-file-encoding ENCODING   - specify character encoding of reporter output (default: utf-8)
    --reporter-include-source           - include source context in report messages
    --servlet                           - configure defaults for servlet operation
    --show-layouts                      - show built-in layouts (use with --verbose to show more details)
    --show-models                       - show built-in verification models (use with --verbose to show more details)
    --show-renderers                    - show built-in renderers (use with --verbose to show more details)
    --show-repository                   - show source code repository information
    --show-resource-location            - show resource location (default: show)
    --show-resource-path                - show resource path (default: show)
    --show-validator                    - show platform validator information
    --show-warning-tokens               - show warning tokens (use with --verbose to show more details)
    --treat-foreign-as TOKEN            - specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: warning)
    --treat-warning-as-error            - treat warning as error (overrides --disable-warnings)
    --until-phase PHASE                 - verify up to and including specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: all)
    --verbose                           - enable verbose output (may be specified multiple times to increase verbosity level)
    --warn-on TOKEN                     - enable warning specified by warning TOKEN, where multiple instances of this option may be specified
  Non-Option Arguments:
    URL                                 - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `ttpe` attempts to resolve it against the current working directory.

## Testing

Junit 4 is used to perform tests on `ttpe`. You should have previously installed the appropriate Junit 4 files in your `ant` runtime in order to use these features.

A number of test targets are listed above for invocation from `ant`. The `clean-test` target is useful in order to perform a clean build then run all tests.

In addition, the `run-samples` target will use the command line (not Junit) invocation path in order to run `ttpe` on a number of sample input files.

## Notes

 * At present, `ttpe` is being developed using the following versions of tools:

<pre>
    $ java -version
    java version "1.6.0_65"
    Java(TM) SE Runtime Environment (build 1.6.0_65-b14-466.1-11M4716)
    Java HotSpot(TM) 64-Bit Server VM (build 20.65-b04-466.1, mixed mode)

    $ ant -version
    Apache Ant(TM) version 1.9.3 compiled on December 23 2013

    $ java -cp /usr/share/java/junit.jar junit.runner.Version
    4.12
</pre>

 * An (Helios) Eclipse workspace and `ttpe` project are available under the `.eclipse` directory.

 * See the `dst` directory for archived source and binary releases.

## Issues

See [Open Issues](http://github.com/skynav/ttpe/issues?state=open) for current known bugs, feature requests (enhancements), etc.

