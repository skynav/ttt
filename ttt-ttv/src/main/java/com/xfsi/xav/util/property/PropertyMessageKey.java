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

package com.xfsi.xav.util.property;

import com.xfsi.xav.util.Error;

public class PropertyMessageKey {

    public static final int EXPECTED_VALIDATOR_ID_LEN           = 3;
    public static final int EXPECTED_VALIDATOR_CODE_LEN         = 2;
    public static final int EXPECTED_SEVERITY_LEN               = 1;
    public static final int EXPECTED_ERROR_CODE_LEN             = 3;

    private String validatorId;
    private int validatorCode;
    private String severity;
    private int errorCode;
    private int errorCodeFraction;

    public PropertyMessageKey() {
        validatorId = null;
        validatorCode = -1;
        severity = null;
        errorCode = -1;
        errorCodeFraction = -1;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId( String id ) {
        validatorId = id;
    }

    public int getValidatorCode() {
        return validatorCode;
    }

    public void setValidatorCode( int code ) {
        validatorCode = code;
    }

    public Error.Severity getSeverity() {
        switch (severity.charAt(0)) {
        case 'T':
            return Error.Severity.TRACE;
        case 'I':
            return Error.Severity.INFO;
        case 'W':
            return Error.Severity.WARNING;
        case 'E':
            return Error.Severity.ERROR;
        case 'F':
            return Error.Severity.FATAL;
        case 'X':
            return Error.Severity.ERROR; // TBD: a separate code needed?
        case 'Y':
            return Error.Severity.FATAL;
        default:
            return Error.Severity.UNSPECIFIED;
        }
    }

    public char getSeverityChar() {
        return severity.charAt(0);
    }

    public void setSeverity( String s ) {
        severity = s;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode( int code ) {
        errorCode = code;
    }

    public int getErrorCodeFraction() {
        return errorCodeFraction;
    }

    public void setErrorCodeFraction( int code ) {
        errorCodeFraction = code;
    }

    public void checkValidity() throws MalformedKeyException {
        if (validatorId == null)
            throw new MalformedKeyException("undefined validator ID");
        else if ( validatorId.length() != EXPECTED_VALIDATOR_ID_LEN )
            throw new MalformedKeyException("expected validator ID length " + EXPECTED_VALIDATOR_ID_LEN + ", actual " + validatorId.length());
        if (severity == null)
            throw new MalformedKeyException("undefined error severity");
        else {
            if (severity.length() != 1)
                throw new MalformedKeyException("expected severity length " + EXPECTED_SEVERITY_LEN + ", actual " + severity.length());
            else {
                switch (severity.charAt(0)) {
                case 'T':
                case 'I':
                case 'W':
                case 'E':
                case 'F':
                case 'X':
                case 'Y':
                    break;
                default:
                    throw new MalformedKeyException("invalid severity " + severity.charAt(0));
                }
            }
        }
        if (validatorCode < 0)
            throw new MalformedKeyException("undefined validator code");
        else if (validatorCode > 1000)
            throw new MalformedKeyException("expected validator code [0-999], actual " + validatorCode);
        if (errorCode < 0)
            throw new MalformedKeyException("undefined error code");
    }

    static public class MalformedKeyException extends Exception {
        static final long serialVersionUID = 1L;
        public MalformedKeyException() {
        }
        public MalformedKeyException(String message) {
            super(message);
        }
    }
}
