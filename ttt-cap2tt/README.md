# cap2tt

Lambda CAP to TTML Converter

## Building

In order to build **cap2tt**, first perform the instructions at https://github.com/skynav/ttt, then run `mvn clean install` from this `ttt-cap2tt` directory.
Next, change directory to the standalone JAR package, ``cd ../ttt-cap2tt-all``, then run `mvn clean install`.

## Running

In order to run the presentation engine (from its standalone JAR), first change directory to the standalone JAR package, ``cd ../ttt-cap2tt-all``,
then use the following (or equivalent):

`java -jar target/ttt-cap2tt-all-7.1.jar ttml.xml`

Usage information can be obtained by using:

`java -jar target/ttt-cap2tt-all-7.1.jar --help`

At present, this will output the following:

<pre>
Lambda CAP To Timed Text (CAP2TT) [7.1] Copyright 2014-21 Skynav, Inc.
Usage: java -jar cap2tt.jar [options] URL*
  Short Options:
    -?                                  - see --help
    -d                                  - see --debug
    -q                                  - see --quiet
    -v                                  - see --verbose
  Long Options:
    --add-creation-metadata [BOOLEAN]   - add creation metadata (default: see configuration)
    --allow-modified-utf8               - allow use of modififed utf-8
    --config FILE                       - specify path to configuration file
    --debug                             - enable debug output (may be specified multiple times to increase debug level)
    --debug-exceptions                  - enable stack traces on exceptions (implies --debug)
    --debug-level LEVEL                 - enable debug output at specified level (default: 0)
    --default-alignment ALIGNMENT       - specify default alignment (default: "中央")
    --default-kerning KERNING           - specify default kerning (default: "1")
    --default-language LANGUAGE         - specify default language (default: "")
    --default-placement PLACEMENT       - specify default placement (default: "横下")
    --default-region ID                 - specify identifier of default region (default: undefined)
    --default-shear SHEAR               - specify default shear (default: "3")
    --default-typeface TYPEFACE         - specify default typeface (default: "default")
    --default-whitespace SPACE          - specify default xml space treatment ("default"|"preserve"; default: "default")
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
    --merge-styles [BOOLEAN]            - merge styles (default: see configuration)
    --model NAME                        - specify model name (default: ttml2)
    --no-verbose                        - disable verbose output (resets verbosity level to 0)
    --no-warn-on TOKEN                  - disable warning specified by warning TOKEN, where multiple instances of this option may be specified
    --output-directory DIRECTORY        - specify path to directory where TTML output is to be written; ignored if --output-file is specified
    --output-disable [BOOLEAN]          - disable output (default: false)
    --output-encoding ENCODING          - specify character encoding of TTML output (default: UTF-8)
    --output-file FILE                  - specify path to TTML output file, in which case only single input URI may be specified
    --output-indent [BOOLEAN]           - indent TTML output (default: false)
    --output-pattern PATTERN            - specify TTML output file name pattern
    --quiet                             - don't show banner
    --reporter REPORTER                 - specify reporter, where REPORTER is null|text|xml (default: text)
    --reporter-file FILE                - specify path to file to which reporter output is to be written
    --reporter-file-append              - if reporter file already exists, then append output to it
    --reporter-file-encoding ENCODING   - specify character encoding of reporter output (default: utf-8)
    --retain-document [BOOLEAN]         - retain document in results object (default: false)
    --shear-map SHEARS                  - specify shear map (default: "0.0 6.345103 11.33775 16.78842 21.99875 27.97058")
    --show-models                       - show supported output models
    --show-repository                   - show source code repository information
    --show-resource-location            - show resource location (default: show)
    --show-resource-path                - show resource path (default: show)
    --show-warning-tokens               - show warning tokens (use with --verbose to show more details)
    --style-id-pattern PATTERN          - specify style identifier format pattern (default: s{0})
    --style-id-sequence-start NUMBER    - specify style identifier sequence starting value, must be non-negative (default: 0)
    --treat-warning-as-error            - treat warning as error (overrides --disable-warnings)
    --verbose                           - enable verbose output (may be specified multiple times to increase verbosity level)
    --warn-on TOKEN                     - enable warning specified by warning TOKEN, where multiple instances of this option may be specified
  Non-Option Arguments:
    URL                                 - an absolute or relative URL; if relative, resolved against current working directory
</pre>

As a convenience, if a URL argument takes a relative form, then `cap2tt` attempts to resolve it against the current working directory.

## Testing

Junit 4 is used to perform tests on `cap2tt` from *maven*. All tests are performed by using `mvn test`. See the `target/surefire-reports` directory for detailed test output.

## Notes

 * At present, `cap2tt` is being developed using the following versions of tools:

<pre>
    $ java -version
    java version "1.8.0_231"
    Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
    Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)

    $ mvn -version
    Apache Maven 3.8.1 (05c21c65bdfed0f71a2f2ada8b84da59348c4c5d)
    Maven home: /opt/local/share/java/maven3
    Java version: 1.8.0_231, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/jre
    Default locale: en_US, platform encoding: UTF-8
    OS name: "mac os x", version: "10.16", arch: "x86_64", family: "mac"
</pre>

## Issues

See [Open Issues](http://github.com/skynav/cap2tt/issues?state=open) for current known bugs, feature requests (enhancements), etc.

