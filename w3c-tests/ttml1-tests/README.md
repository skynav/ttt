# TTML1 Test Suites

The TTML1 Test Suites consist of a validation test suite and a presentation test suite.

The tests found in these test suites focus on functionality and constraints thereof that derive directly from normative text in the [TTML1 Specification](https://www.w3.org/TR/ttml1/).

## Validation Test Suite

The validation test suite is found under the `validation` directory, and is divided into two parts: (1) tests for valid content (validity tests) and (2) tests for invalid content (invalidity tests). The result of each test can be characterized as PASS or FAIL.

A PASS result for a validity test occurs if the validator does not reject (report a validation error for) the content of the test. In contrast, a FAIL result for a validity test occurs if the validator rejects (reports a validation error for) the content of the test, in which case, such a result is deemed a _false negative_ result.

A PASS result for an invalidity test occcurs if the validator rejects (reports a validation error for) the content of the test. In contrast, a FAIL result for an invalidity test occurs if the validator does not reject (report a validation error for) the content of the test, in which case, such a result is deemed a _false positive_ result.

A validator is considered to **_strictly pass_** the test suite if it does not report a false negative for any validity test. A validator is considered to **_fully pass_** the test suite if it (1) strictly passes the test suite and (2) does not report a false positive for any invalidity test.

A mapping from (designated) features to specific tests is found in `validation/tests.json`, which, for each TTML1 feature designator, lists validity and invalidity tests (by name). We refer to this mapping file as the *validation test manifest*.

## Presentation Test Suite

The presentation test suite is found under the `presentation` directory, and is divided into two parts: (1) tests for presenting valid content and (2) tests for presenting invalid content.

A mapping from (designated) features to specific tests is found in `presentation/tests.json`, which, for each TTML1 feature designator, lists presentation tests (by name). We refer to this mapping file as the *presentation test manifest*.

For tests having primarily visual presentation semantics, each presentation test is associated with a like named ZIP archive with the suffix `.expected.zip`, which contains the output of a particular reference implementation (TTPE). Each such _reference archive_ contains a manifest file and one or more image frames represented in some image format. In the present form of the reference archives, the image format is `image/svg+xml`. These image frames should **not** be construed as normative, but merely serve as a possible reference image for performing (human visual) comparisons of expected output.

## Pending Tests

Tests located in the `pending` directory are under consideration for future addition to the test suite. They are not considered part of the formal test suite at this time; furthermore, there is no (fixed) schedule for their addition (or not) to the test suite.

## Test Annotation Attributes

In addition to standard, TTML1 defined attributes, each test contains certain tool-specific annotation attributes in a namespace associated with the `ttva` prefix. These attributes are used by a certain reference implementation, [TTV](https://github.com/skynav/ttt/tree/master/ttt-ttv), to facilitate the testing process. Note that TTML1 content processing requires processors to prune (ignore) unrecognized namespace qualified attributes. See [Annotations](https://github.com/skynav/ttt/tree/master/ttt-ttv#annotations) for further information on their usage.
