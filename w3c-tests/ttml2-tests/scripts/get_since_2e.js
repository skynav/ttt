// Map features to tests since 2e; built using node.js 15.4.0

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
var expectedFeatureCount = 138;

// miscellaneous globals
var ne = 0; // # of errors encountered

// banner
console.log('Get features and tests since 2e');

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

var since_2e = {};

const repo_prefix = "https://raw.githubusercontent.com/w3c/ttml2-tests/master/";
const validation_prefix = "validation/";
const presentation_prefix = "presentation/";
const valid_prefix = "valid/";
const invalid_prefix = "invalid/";
const suffix = ".xml";
const presentation_test_type = "Pres";
const validation_test_type = "Val";

// find validation features with tests since 2e
vf.forEach(function(vfe) {
    vt[vfe].valid.forEach(function(vfve) {
        if (vfve.since == "2e") {
            console.log('[I]: ' + vfe + ' ' + vfve.test);
            if (!since_2e[vfe])
                since_2e[vfe] = [];
            since_2e[vfe].push(
                [
                    vfve.test,
                    repo_prefix + validation_prefix + valid_prefix + vfve.test + suffix,
                    validation_test_type
                ]);
        };
    });
    vt[vfe].invalid.forEach(function(vfve) {
        if (vfve.since == "2e") {
            console.log('[I]: ' + vfe + ' ' + vfve.test);
            if (!since_2e[vfe])
                since_2e[vfe] = [];
            since_2e[vfe].push(
                [
                    vfve.test,
                    repo_prefix + validation_prefix + invalid_prefix + vfve.test + suffix,
                    validation_test_type
                ]);
        };
    });
});

// find presentation features with tests since 2e
pf.forEach(function(pfe) {
    pt[pfe].valid.forEach(function(pfve) {
        if (pfve.since == "2e") {
            console.log('[I]: ' + pfe + ' ' + pfve.test);
            if (!since_2e[pfe])
                since_2e[pfe] = [];
            since_2e[pfe].push(
                [
                    pfve.test,
                    repo_prefix + presentation_prefix + valid_prefix + pfve.test + suffix,
                    presentation_test_type
                ]);
        };
    });
    pt[pfe].invalid.forEach(function(pfve) {
        if (pfve.since == "2e") {
            console.log('[I]: ' + pfe + ' ' + pfve.test);
            if (!since_2e[pfe])
                since_2e[pfe] = [];
            since_2e[pfe].push(
                [
                    pfve.test,
                    repo_prefix + presentation_prefix + invalid_prefix + pfve.test + suffix,
                    presentation_test_type
                ]);
        };
    });
});

// output final results
console.log('[I]: JSON Results follow on next line:')
console.log(JSON.stringify(since_2e));

// output final results as MediaWiki table
console.log('[I]: MediaWiki Results follow:');
// table start
console.log('{| class="wikitable tl"');
console.log('|+ Table 1 - Table of features and related tests');
// first header row
console.log('!');
console.log('!');
console.log('!');
console.log('! Company 1');
console.log('! Company 2');
console.log('! Company 3');
console.log('! Company 4');
console.log('! Company 5');
// second header row
console.log('|-');
console.log('! Feature');
console.log('! Test');
console.log('! Test type');
console.log('! Impl 1');
console.log('! Impl 2');
console.log('! Impl 3');
console.log('! Impl 4');
console.log('! Impl 5');
for (const [key, value] of Object.entries(since_2e)) {
    for (var si in value) {
        // new row
        console.log('|-');
        // feature designator
        if (si == 0) {
            console.log('| [https://www.w3.org/TR/2020/CR-ttml2-20200128/#feature-' + key.split('#')[1] + ' ' + key + ']');
        } else {
            console.log('| ');
        }
        // test
        console.log('| [' + value[si][1] + ' ' + value[si][0] + ']');
        // test type
        console.log('| style="text-align:center;" | ' + value[si][2]);
        console.log('|')
        console.log('|')
        console.log('|')
        console.log('|')
        console.log('|')
    }
};
// table end
console.log('|}');