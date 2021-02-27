# ttpe

Timed Text Presentation Engine

## Building

In order to build **ttpe**, first perform the instructions at https://github.com/skynav/ttt, then run `mvn clean install` from this `ttt-ttpe` directory.
Next, change directory to the standalone JAR package, ``cd ../ttt-ttpe-all``, then run `mvn clean install`.

## Running

In order to run the presentation engine (from its standalone JAR), first change directory to the standalone JAR package, ``cd ../ttt-ttpe-all``,
then use the following (or equivalent):

`java -jar target/ttt-ttpe-all-7.1-SNAPSHOT.jar ttml.xml`

Usage information can be obtained by using:

`java -jar target/ttt-ttpe-all-7.1-SNAPSHOT.jar --help`

At present, this will output the following:

<pre>
Timed Text Presentation Engine (TTPE) [7.1-SNAPSHOT] Copyright 2014-19 Skynav, Inc.
Usage: java -jar ttpe.jar [options] URL*
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
    --default-background-color COLOR    - default background color (default: "[0.0,0.0,0.0,0.0]")
    --default-color COLOR               - default foreground color (default: [1.0,1.0,1.0,1.0]")
    --default-font-families FAMILIES    - default font families (default: "[Noto Sans]")
    --default-whitespace SPACE          - default xml space treatment ("default"|"preserve"; default: "default")
    --disable-warnings                  - disable warnings (both hide and don't count warnings)
    --expect-errors COUNT               - expect count errors or -1 meaning unspecified expectation (default: -1)
    --expect-warnings COUNT             - expect count warnings or -1 meaning unspecified expectation (default: -1)
    --extension-schema NS URL           - add schema for namespace NS at location URL to grammar pool (may be specified multiple times)
    --external-duration DURATION        - specify root temporal extent duration for document processing context
    --external-extent EXTENT            - specify root container region extent for document processing context
    --external-frame-rate RATE          - specify frame rate for document processing context
    --external-wallclock-begin TIME     - specify document wallclock begin time for document processing context
    --font FILE                         - specify font configuration file
    --font-directory DIRECTORY          - specify path to directory where font configuration files are located
    --force-encoding NAME               - force use of named character encoding, overriding default and resource specified encoding
    --force-model NAME                  - force use of named model, overriding default model and resource specified model
    --forced-display                    - enable forced display mode
    --help                              - show usage help
    --hide-resource-location            - hide resource location (default: show)
    --hide-resource-path                - hide resource path (default: show)
    --hide-warnings                     - hide warnings (but count them)
    --isd-output-clean                  - clean (remove) all files in output directory prior to writing ISD output
    --isd-output-directory DIRECTORY    - specify path to directory where ISD output is to be written
    --isd-output-encoding ENCODING      - specify character encoding of ISD output (default: UTF-8)
    --isd-output-indent                 - indent ISD output (default: no indent)
    --isd-output-pattern PATTERN        - specify ISD output file name pattern (default: 'isd00000')
    --layout NAME                       - specify layout name (default: basic)
    --line-breaker NAME                 - specify line breaker name (default: "uax14")
    --max-chars COUNT                   - maximum number of characters in canvas (default: no limit)
    --max-chars-per-line COUNT          - maximum number of characters in a line (default: no limit)
    --max-chars-per-region COUNT        - maximum number of characters in a region (default: no limit)
    --max-lines COUNT                   - maximum number of lines in canvas (default: no limit)
    --max-lines-per-region COUNT        - maximum number of lines in a region (default: no limit)
    --max-regions COUNT                 - maximum number of regions in canvas (default: no limit)
    --model NAME                        - specify model name (default: ttml1)
    --no-verbose                        - disable verbose output (resets verbosity level to 0)
    --no-warn-on TOKEN                  - disable warning specified by warning TOKEN, where multiple instances of this option may be specified
    --output-archive                    - combine output frames into frames archive file
    --output-archive-file NAME          - specify path of frames archive file
    --output-directory DIRECTORY        - specify path to directory where output is to be written
    --output-directory-retained DIRECTORY- specify path to directory where retained output is to be written, in which case only single input URI may be specified
    --output-encoding ENCODING          - specify character encoding of output (default: UTF-8)
    --output-format NAME                - specify output format name (default: xml)
    --output-indent                     - indent output (default: no indent)
    --output-manifest-format NAME       - specify output manifest format name (default: simple)
    --output-pattern PATTERN            - specify output file name pattern
    --output-pattern-isd PATTERN        - specify output ISD file name pattern
    --output-pattern-resource PATTERN   - specify output resource file name pattern
    --output-retain-frames              - retain individual frame files after archiving
    --output-retain-isd                 - retain ISD documents
    --output-retain-manifest            - retain manifest document
    --quiet                             - don't show banner
    --reporter REPORTER                 - specify reporter, where REPORTER is null|text|xml (default: text)
    --reporter-file FILE                - specify path to file to which reporter output is to be written
    --reporter-file-append              - if reporter file already exists, then append output to it
    --reporter-file-encoding ENCODING   - specify character encoding of reporter output (default: utf-8)
    --reporter-include-source           - include source context in report messages
    --retain-reporter                   - retain (rather than reset) reporter upon run completion
    --servlet                           - configure defaults for servlet operation
    --show-formats                      - show output formats
    --show-layouts                      - show built-in layouts
    --show-memory                       - show memory statistics
    --show-models                       - show built-in verification models (use with --verbose to show more details)
    --show-repository                   - show source code repository information
    --show-resource-location            - show resource location (default: show)
    --show-resource-path                - show resource path (default: show)
    --show-transformers                 - show built-in transformers (use with --verbose to show more details)
    --show-validator                    - show platform validator information
    --show-warning-tokens               - show warning tokens (use with --verbose to show more details)
    --svg-background COLOR              - paint background of specified color into root region (default: transparent)
    --svg-decorate-all                  - decorate regions, lines, glyphs
    --svg-decorate-glyphs               - decorate glyphs with bounding box
    --svg-decorate-line-baselines       - decorate line baselines
    --svg-decorate-line-bounds          - decorate line bounding boxes
    --svg-decorate-line-labels          - decorate line labels
    --svg-decorate-lines                - decorate line features (bounding baselines, boxes, labels)
    --svg-decorate-none                 - disble decorations on regions, lines, glyphs
    --svg-decorate-regions              - decorate regions with bounding box
    --svg-decoration COLOR              - paint decorations using specified color (default: color contrasting with specified background or black)
    --svg-mark-classes                  - mark area classes
    --transformer NAME                  - specify transformer name (default: isd)
    --treat-foreign-as TOKEN            - specify treatment for foreign namespace vocabulary, where TOKEN is error|warning|info|allow (default: warning)
    --treat-warning-as-error            - treat warning as error (overrides --disable-warnings)
    --until-phase PHASE                 - verify up to specified phase, where PHASE is none|resource|wellformedness|validity|semantics|all (default: all)
    --verbose                           - enable verbose output (may be specified multiple times to increase verbosity level)
    --verbose-level LEVEL               - enable verbose output at specified level (default: 0)
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

