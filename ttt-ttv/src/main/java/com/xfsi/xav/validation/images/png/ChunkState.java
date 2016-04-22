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

package com.xfsi.xav.validation.images.png;

final class ChunkState
{
    final static class StateType {
        static final byte mustExist             = 1;
        static final byte mayExist              = 2;
        static final byte mustNotExist          = 3;
        static final byte mhpMustExist          = 4;
    }

    private final byte[] type;
    private final boolean multipleAllowed;
    private byte state;
    private int count;
    private byte[] data;

    ChunkState(byte[] type, byte state, boolean multipleAllowed) {
        this.type = type;
        this.state = state;
        this.multipleAllowed = multipleAllowed;
        this.count = 0;
        this.data = null;
    }

    byte[] getType() {
        return type;
    }

    boolean mustExist() {
        return state == StateType.mustExist;
    }

    boolean mhpMustExist() {
        return state == StateType.mhpMustExist;
    }

    boolean mayExist() {
        return state == StateType.mayExist;
    }

    boolean mustNotExist() {
        return state == StateType.mustNotExist;
    }

    boolean getMultipleAllowed() {
        return multipleAllowed;
    }

    int getCount() {
        return count;
    }

    void incrementCount() {
        count++;
    }

    void setStateType(byte s) {
        state = s;
    }

    void setData(byte[] d) {
        data = d;
    }

    byte[] getData() {
        return data;
    }

}
