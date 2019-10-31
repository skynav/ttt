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

package com.xfsi.xav.validation.audio.mpeg;

/**
 * Maintains state during MPEG audio validation
 */
class State {
    static enum Mode {
        STEREO,
        JOINT_STEREO,
        DUAL_CHANNEL,
        SINGLE_CHANNEL,
    }
    static enum Layer {
        RESERVED,       // must be 0 (spec bits 00)
        LAYER_I,        // must be 1 (spec bits 11)
        LAYER_II,       // must be 2 (spec bits 10)
        LAYER_III,      // must be 3 (spec bits 01)
    }
    Boolean protectionBit = false;
    Boolean paddingBit = false;
    Boolean algorithmBit = false;
    Integer frameCount = 0;
    Integer layer3FrameCount = 0;
    Integer mainDataBegin = 0;
    Integer bitRateIndex = 0;
    Integer bitRate = 0;
    Integer samplingFrequency = 0;
    Mode mode = null;
    Layer layer = null;

    int getNumberOfChannels() {
        return this.mode == Mode.SINGLE_CHANNEL ? 1 : 2;
    }

    int getBytesToNextFrameBeginning() {
        int pad = this.paddingBit ? 1 : 0;
        if (this.layer == State.Layer.LAYER_I)
            return ((12 * this.bitRate/this.samplingFrequency) + pad) * 4;
        return ((this.layer == State.Layer.LAYER_III && !this.algorithmBit ? 72 : 144) * this.bitRate/this.samplingFrequency) + pad;
    }

    void setAddedRedundancy(boolean bit) {
        this.protectionBit = bit;
    }

    boolean hasAddedRedundancy() {
        return this.protectionBit;
    }

    void setPaddingBit(boolean bit) {
        this.paddingBit = bit;
    }

    boolean isPaddingBit() {
        return this.paddingBit;
    }

    void setAlgorithmBit(boolean bit) {
        this.algorithmBit = bit;
    }

    boolean isAlgorithmBit() {
        return this.algorithmBit;
    }

    void incrementFrameCount() {
        this.frameCount++;
    }

    Integer getFrameCount() {
        return this.frameCount;
    }

    Integer getLayer3FrameCount() {
        return this.layer3FrameCount;
    }

    void setMainDataBegin(int mainDataBegin) {
        this.mainDataBegin = mainDataBegin;
    }

    Integer getMainDataBegin() {
        return this.mainDataBegin;
    }

    void setBitRateIndex(int bitRateIndex) {
        this.bitRateIndex = bitRateIndex;
    }

    Integer getBitRateIndex() {
        return this.bitRateIndex;
    }

    void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    Integer getBitRate() {
        return this.bitRate;
    }

    void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    Integer getSamplingFrequency() {
        return this.samplingFrequency;
    }

    void setMode(Mode mode) {
        this.mode = mode;
    }

    Mode getMode() {
        return this.mode;
    }

    void setLayer(Layer layer) {
        this.layer = layer;
        switch (this.layer) {
        case LAYER_III:
            this.layer3FrameCount++;
            break;
        case LAYER_I:
        case LAYER_II:
            break;
        case RESERVED:
            // TBD: generate error/warning?
            break;
        }
    }

    Layer getLayer() {
        return this.layer;
    }
}
