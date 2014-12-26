# cap2tt

Lambda CAP to TtML Converter

## Building

In order to build **cap2tt**, run *ant* with one of the following targets:

`ant build`

Using the `build` target will (re)create the `cap2tt.jar` target under `bld/artifacts`.

`ant clean-build`

Using the `clean-build` target will first clean (delete) previously built classes and artifacts, and then perform a `build`.

The full set of (public) `ant` targets can be listed by using `ant -p`, which presently outputs the following:

<pre>
Main targets:

 build               Build All
 build-cap2tt        Build CAP2TT
 clean-build         Clean and build all targets.
 clean-cap2tt-build  Clean and build CAP2TT.
 clean-cap2tt-test   Clean, build, and test CAP2TT
 clean-test          Clean, build, and test all targets.
 run-samples         Run converter with sample input test files
 test                Run all test suites
 test-apps           Run CAP2TT application tests
 test-cap2tt         Run all CAP2TT test suites
 test-converter      Run CAP2TT conversion test suite

Default target: clean-test
</pre>

## Running

In order to run the convert, use the following (or an equivalent):

`java -cp bld/artifacts/cap2tt.jar:ext/lib/ttv.jar com.skynav.cap2tt.app.Converter input.cap`

Usage information can be obtained by using:

`java -cp bld/artifacts/cap2tt.jar:ext/lib/ttv.jar com.skynav.cap2tt.app.Converter --help`

At present, this will output the following:

<pre>
CAP To Timed Text (CAP2TT) [1.0.0dev] Copyright 2014 Skynav, Inc.
Usage: java -cp bld/artifacts/cap2tt.jar:ext/lib/ttv.jar com.skynav.cap2tt.app.Converter [options] URL*
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
    --external-duration DURATION        - specify root temporal extent duration for document processing context
    --external-extent EXTENT            - specify root container region extent for document processing context
    --external-frame-rate RATE          - specify frame rate for document processing context
    --help                              - show usage help
    --hide-resource-location            - hide resource location (default: show)
    --hide-resource-path                - hide resource path (default: show)
    --hide-warnings                     - hide warnings (but count them)
    --merge-styles                      - merge styles (default: don't merge)
    --metadata-creation                 - add creation metadata (default: don't add)
    --no-verbose                        - disable verbose output (resets verbosity level to 0)
    --no-warn-on TOKEN                  - disable warning specified by warning TOKEN, where multiple instances of this option may be specified
    --output-directory DIRECTORY        - specify path to directory where ISD output is to be written
    --output-encoding ENCODING          - specify character encoding of ISD output (default: UTF-8)
    --output-indent                     - indent ISD output (default: no indent)
    --quiet                             - don't show banner
    --reporter REPORTER                 - specify reporter, where REPORTER is null|text|xml (default: text)
    --reporter-file FILE                - specify path to file to which reporter output is to be written
    --reporter-file-append              - if reporter file already exists, then append output to it
    --reporter-file-encoding ENCODING   - specify character encoding of reporter output (default: utf-8)
    --show-repository                   - show source code repository information
    --show-resource-location            - show resource location (default: show)
    --show-resource-path                - show resource path (default: show)
    --show-warning-tokens               - show warning tokens (use with --verbose to show more details)
    --treat-warning-as-error            - treat warning as error (overrides --disable-warnings)
    --verbose                           - enable verbose output (may be specified multiple times to increase verbosity level)
    --warn-on TOKEN                     - enable warning specified by warning TOKEN, where multiple instances of this option may be specified
  Non-Option Arguments:
    URL                                 - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `cap2tt` attempts to resolve it against the current working directory.

## Testing

Junit 4 is used to perform tests on `cap2tt`. You should have previously installed the appropriate Junit 4 files in your `ant` runtime in order to use these features.

A number of test targets are listed above for invocation from `ant`. The `clean-test` target is useful in order to perform a clean build then run all tests.

In addition, the `run-samples` target will use the command line (not Junit) invocation path in order to run `cap2tt` on a number of sample input files.

## Notes

 * At present, `cap2tt` is being developed using the following versions of tools:

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

 * An (Helios) Eclipse workspace and `cap2tt` project are available under the `.eclipse` directory.

 * See the `dst` directory for archived source and binary releases.

## Issues

See [Open Issues](http://github.com/skynav/cap2tt/issues?state=open) for current known bugs, feature requests (enhancements), etc.

