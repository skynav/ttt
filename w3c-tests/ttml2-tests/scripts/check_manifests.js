// Check Manifest Files; built using node.js 10.10.0

// deps
var fs = require('fs');

// helper functions
function fail(e) {
    process.exit(1);
}

function pass() {
    process.exit(0);
}

// miscellaneous constants
var expectedFeatureCount        = 138;

// miscellaneous globals
var ne = 0;                     // # of errors encountered

// banner
console.log('Check Test Manifests 1.0');

// read and parse validation test manifest
var vt, fv;
try {
  vt = JSON.parse(fs.readFileSync('validation/tests.json', 'utf8'));
  vf = Object.keys(vt).sort();
} catch (e) {
    console.log('[E]:' + 'read or parse failed for validation test manifest: ' + e.message);
    fail(e);
}

// read and parse presentation test manifest
var pt, pf;
try {
  pt = JSON.parse(fs.readFileSync('presentation/tests.json', 'utf8'));
  pf = Object.keys(pt).sort();
} catch (e) {
    console.log('[E]:' + 'read or parse failed for presentation test manifest: ' + e.message);
    fail(e);
}


// check that feature counts are correct
var nvf = vf.length;
if (nvf != expectedFeatureCount) {
    console.log('[E]:' + 'expected ' + expectedFeatureCount + ' validation features, but ' + nvf + ' features specified');
    ne++;
}
var npf = pf.length;
if (npf != expectedFeatureCount) {
    console.log('[E]:' + 'expected ' + expectedFeatureCount + ' presentation features, but ' + npf + ' features specified');
    ne++;
}

// find missing validation features
for (var fvi in vf) {
    var fv = vf[fvi];
    if (!(fv in pt)) {
        console.log('[E]:' + 'validation feature ' + fv + ' is missing from presentation features');
        ne++;
    }
}

// find missing presentation features
for (var fpi in pf) {
    var fp = pf[fpi];
    if (!(fp in vt)) {
        console.log('[E]:' + 'presentation feature ' + fp + ' missing from validation features');
        ne++;
    }
}

// output final results
if (ne > 0) {
    console.log('[I]: FAIL, with ' + ne + ' errors');
    fail();
} else {
    console.log('[I]: PASS');
    pass();
}
