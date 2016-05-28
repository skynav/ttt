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

package com.xfsi.xav.validation.images.jpeg;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Handles JPEG input stream. Tracks total bytes read and allows putting back of read data.
 */
class JpegInputStream {
        private int readByteCount = 0;
        private final DataInputStream inputStream;
        private LinkedList<Byte> putBack = new LinkedList<Byte>();

        JpegInputStream(InputStream is)
        {
                this.inputStream = new DataInputStream(is);
        }

        byte readByte() throws EOFException, IOException
        {
                if (this.putBack.size() == 0)
                {
                        byte b = this.inputStream.readByte();
                        this.readByteCount++;
                        return b;
                }
                return this.putBack.remove();
        }

        short readShort() throws EOFException, IOException
        {
                if (this.putBack.size() == 0)
                {
                        short s = this.inputStream.readShort();
                        this.readByteCount += 2;
                        return s;
                }
                short msb = readByte();
                short lsb = readByte();
                return (short) ((msb << 8) | (lsb & 0xff));
        }

        int readInt() throws EOFException, IOException
        {
                if (this.putBack.size() == 0)
                {
                        int i = this.inputStream.readInt();
                        this.readByteCount += 4;
                        return i;
                }
                int mss = readShort();
                int lss = readShort();
                return (mss << 16) | (lss & 0xffff);
        }

        void skipBytes(int count) throws EOFException, IOException
        {
                // DataInputStream skipBytes() in jpegInputStream does not throw EOFException() if EOF reached,
                // which we are interested in, so read the bytes to skip them which WILL generate an EOFException().
                for (int i = 0; i < count; i++)
                        readByte();
        }

        void putBack(byte b)
        {
                this.putBack.add(b);
        }

        int getTotalBytesRead()
        {
                return this.readByteCount;
        }
}
