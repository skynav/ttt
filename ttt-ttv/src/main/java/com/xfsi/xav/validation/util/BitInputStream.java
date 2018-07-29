/*
 * Copyright 2018 Skynav, Inc. All rights reserved.
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

package com.xfsi.xav.validation.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles bit input streams, to allow bit granularity reading.
 * Note: use aggregation instead of inheritance to assure that, with minimal code, byte
 * count is always accurate.
 */
public class BitInputStream {
    private int traceReadBitsCount = 0;
    private boolean isTracingEnabled = false;
    private DataInputStream inputStream;
    private static final int bitBufferSize = 32;
    private int readByteCount = 0;
    private Integer bitBuffer = null;
    private Integer nextBitBuffer = null;
    private int bitBufferIndex = 0;
    private int nextBitBufferIndex = 0;
    private boolean isEof = false;

    public BitInputStream(InputStream is) {
        this.inputStream = new DataInputStream(is);
    }

    public void setTracing(boolean enable) {
        this.isTracingEnabled = enable;
    }

    public int peekBits(int numBits) throws IOException {
        assert(numBits > 0 && numBits <= BitInputStream.bitBufferSize);
        int result = 0;
        for (int i = numBits - 1; i >= 0; i--)
            result |= peekNextBufferBit(numBits - (i + 1)) << i;
        return result;
    }

    public int readBits(int numBits) throws IOException {
        assert(numBits > 0 && numBits <= BitInputStream.bitBufferSize);
        int result = 0;
        for (int i = numBits - 1; i >= 0; i--)
            result |= readNextBufferBit() << i;
        if (isTracingEnabled) {
            Integer peek;
            try
                {
                    peek = this.peekBits(16);
                }
            catch (EOFException e)
                {
                    peek = null;
                }
            System.out.println(String.format("%1$d readBits(%2$d) = %3$d    %4$4x", traceReadBitsCount += numBits, numBits, result, peek));
        }
        return result;
    }

    public int getTotalBytesRead() {
        return this.readByteCount - (this.bitBufferIndex / 8) - (this.nextBitBufferIndex / 8);
    }

    public boolean isByteAligned() {
        return (this.bitBufferIndex % 8) == 0;
    }

    public boolean isEof() {
        return this.isEof;
    }

    private int peekNextBufferBit(int peekOffset) throws IOException {
        long peekBuffer;
        int peekBufferIndex = this.bitBufferIndex - peekOffset;
        if (peekBufferIndex > 0) {
            peekBuffer = this.bitBuffer;
        } else {
            if (nextBitBuffer == null)
                loadBuffers();
            peekBuffer = this.nextBitBuffer;
            peekBufferIndex = BitInputStream.bitBufferSize + peekBufferIndex;
        }
        peekBufferIndex--;
        int mask = 0x1 << peekBufferIndex;
        return (peekBuffer & mask) == 0 ? 0 : 1;
    }

    private int readNextBufferBit() throws IOException {
        if (this.bitBufferIndex == 0)
            loadBuffers();
        this.bitBufferIndex--;
        int mask = 0x1 << this.bitBufferIndex;
        return (bitBuffer & mask) == 0 ? 0 : 1;
    }
    
    private void loadBuffers() throws IOException {
        if (this.isEof) {
            if (this.nextBitBuffer != null) {
                this.bitBuffer = this.nextBitBuffer;
                this.bitBuffer >>>= (BitInputStream.bitBufferSize - this.nextBitBufferIndex);
                this.bitBufferIndex = this.nextBitBufferIndex;
                this.nextBitBuffer = null;
                return;
            }
            throw new EOFException();
        }
        if (nextBitBuffer == null) {
            fillNextBuffer();
            // Check if possible empty file
            if (this.isEof)
                throw new EOFException();
        }
        this.bitBuffer = this.nextBitBuffer;
        this.bitBufferIndex = this.nextBitBufferIndex;
        fillNextBuffer();       
    }

    private void fillNextBuffer() throws IOException {
        try {
            this.nextBitBuffer = 0;
            this.nextBitBufferIndex = 0;
            for (int i = BitInputStream.bitBufferSize - 8; i >= 0; i -= 8)
            {
                this.nextBitBuffer |= ((this.inputStream.readUnsignedByte() & 0xff) << i);
                this.nextBitBufferIndex += 8;
                this.readByteCount++;
            }
        } catch (EOFException e) {
            // Null out buffer if not bytes could be read
            if (this.nextBitBufferIndex == 0)
                this.nextBitBuffer = null;
            this.isEof = true;
        }
    }
}
