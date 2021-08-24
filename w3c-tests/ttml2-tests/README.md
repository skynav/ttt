# TTML2 Test Suites

The TTML2 Test Suites consist of a validation test suite and a presentation test suite. The purpose of these test suites is to demonstrate adequate implementation experience of each (designated) feature of the [TTML2 Specification](https://www.w3.org/TR/ttml2/) as documented by the [TTML2 Implementation Report](https://www.w3.org/wiki/TimedText/TTML2ImplementationReport). An ancillary purpose is to promote ineroperability between implementations.

The tests found in these test suites focus on functionality and constraints thereof that derive directly from normative text in the [TTML2 Specification](https://www.w3.org/TR/ttml2/). Additional tests that go beyond the normative specification text are included here as well; however, though such tests are useful for wider interoperability testing, the results from their exercise are not reported in the [TTML2 Implementation Report](https://www.w3.org/wiki/TimedText/TTML2ImplementationReport). See also [Excluded Tests](#excluded-tests).

## Validation Test Suite

The validation test suite is found under the `validation` directory, and is divided into two parts: (1) tests for valid content (validity tests) and (2) tests for invalid content (invalidity tests). The result of each test can be characterized as PASS or FAIL.

A PASS result for a validity test occurs if the validator does not reject (report a validation error for) the content of the test. In contrast, a FAIL result for a validity test occurs if the validator rejects (reports a validation error for) the content of the test, in which case, such a result is deemed a _false negative_ result.

A PASS result for an invalidity test occcurs if the validator rejects (reports a validation error for) the content of the test. In contrast, a FAIL result for an invalidity test occurs if the validator does not reject (report a validation error for) the content of the test, in which case, such a result is deemed a _false positive_ result.

A validator is considered to **_strictly pass_** the test suite if it does not report a false negative for any validity test. A validator is considered to **_fully pass_** the test suite if it (1) strictly passes the test suite and (2) does not report a false positive for any invalidity test.

For the purpose of reporting an implementation of the validation function for a specific (designated) feature, the implementation must strictly pass all tests for that feature; however, it is not required to fully pass all tests for that feature since the TTML2 specification does not require a validator to detect all possibile invalidities, about which see [validate document](https://www.w3.org/TR/ttml2/#semantics-procedure-validate-document).

A mapping from (designated) features to specific tests is found in `validation/tests.json`, which, for each new TTML2 feature designator, lists validity and invalidity tests (by name), and optionally includes any of (1) a per-test exclusion flag if the test is intended to be excluded from exit criteria consideration and (2) an edition introduced (`since`) indicator. We refer to this mapping file as the *validation test manifest*.

## Presentation Test Suite

The presentation test suite is found under the `presentation` directory, and is divided into two parts: (1) tests for presenting valid content and (2) tests for presenting invalid content.

A mapping from (designated) features to specific tests is found in `presentation/tests.json`, which, for each new TTML2 feature designator, lists presentation tests (by name), and optionally includes a per-test exclusion flag if the test is intended to be excluded from exit criteria consideration. We refer to this mapping file as the *presentation test manifest*.

For tests having primarily visual presentation semantics, each presentation test is associated with a like named ZIP archive with the suffix `.expected.zip`, which contains the output of a particular reference implementation (TTPE). Each such _reference archive_ contains a manifest file and one or more image frames represented in some image format. In the present form of the reference archives, the image format is `image/svg+xml`. These image frames should **not** be construed as normative, but merely serve as a possible reference image for performing (human visual) comparisons of expected output.

## Excluded Tests

As noted above, the two (validation and presentation) test manifests employ an *exclusion* flag to denote that a test is excluded from exit criteria consideration. The tests marked as excluded (presently) consist of tests that may be useful for testing implementations, but are not necessarily derived from normative specification language, as well as tests for which there is some question as to whether they correctly represent normative specification requirements. In both cases, these tests may, but need not be exercised by implementations, and there is no expectation that the results from such tests will be reported by implementors.

## Pending Tests

Tests located in the `pending` directory are under consideration for future addition to the test suite. They are not considered part of the formal test suite at this time; furthermore, there is no (fixed) schedule for their addition (or not) to the test suite.

## Test Annotation Attributes

In addition to standard, TTML2 defined attributes, each test contains certain tool-specific annotation attributes in a namespace associated with the `ttva` prefix. These attributes are used by a certain reference implementation, [TTV](https://github.com/skynav/ttt/tree/master/ttt-ttv), to facilitate the testing process. Note that TTML2 content processing requires processors to prune (ignore) unrecognized namespace qualified attributes. See [Annotations](https://github.com/skynav/ttt/tree/master/ttt-ttv#annotations) for further information on their usage.
