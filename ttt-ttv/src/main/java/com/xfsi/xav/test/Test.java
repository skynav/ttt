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

package com.xfsi.xav.test;

import com.xfsi.xav.util.Result;

public interface Test {

    /**
     * This method is called by result reporter to append test version to reult output.
     *
     * @return version of test
     */
    public String getVersion();

    /**
     * If false is returned, run() method will not be called.
     *
     * @param tm the manager object used for callbacks during the test
     * @param ti the test information that describes this test instance
     * @return true if test should run
     */
    public boolean isRunnable(TestManager tm, TestInfo ti) throws Exception;

    /**
     * This method is called by the validation framework whenever it wishes this test
     * to be executed.  the contract is that this method need not be
     * thread-safe, however it does require that the Test object be
     * capable of running the test multiple times.
     *
     * The contract also requires that the test must report a result to ResultManager
     * for each resource that was tested.
     * However, the result returned by this method must NOT be reported to ResultManager
     * by ths class, it will be reported by the caller.
     *
     * @param v the manager object used for callbacks during the test
     * @param ti the test information that describes this test instance
     * @return the test result
     */
    public Result run(TestManager v, TestInfo ti) throws Exception;

}
