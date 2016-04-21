/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 * Portions Copyright 2009 Extensible Formatting Systems, Inc (XFSI).
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.xfsi.xav.util;

public final class Result {

    public static final int FAIL_RESULT                         = 0;
    public static final int PASS_RESULT                         = 1;
    public static final int NO_RESULT                           = 2;
    public static final int NA_RESULT                           = 3;
    public static final int DEP_FAIL_RESULT                     = 4;

    public static final Result FAIL                             = new Result(FAIL_RESULT);     // Test failed
    public static final Result PASS                             = new Result(PASS_RESULT);     // Test passed
    public static final Result NO                               = new Result(NO_RESULT);       // Test not run
    public static final Result NA                               = new Result(NA_RESULT);       // No test found (resource test)
    public static final Result DEP_FAIL                         = new Result(DEP_FAIL_RESULT); // Test not run due to dependency failure

    private int type;

    private Result( int type ) {
        this.type = type;
    }

    public int getResult() { return type; }

    public String toString() {
        switch (type) {
        case FAIL_RESULT:
            return "failed";
        case PASS_RESULT:
            return "passed";
        case NO_RESULT:
            return "not run";
        case NA_RESULT:
            return "untested";
        case DEP_FAIL_RESULT:
            return "dependency failed";
        default:
            return "unknown";
        }
    }

    public boolean isFailure() {
        switch (type) {
        case FAIL_RESULT:
        case DEP_FAIL_RESULT:
            return true;
        default:
            return false;
        }
    }

}

