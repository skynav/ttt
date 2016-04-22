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

package com.xfsi.xav.validation.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.xfsi.xav.test.Test;
import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;

import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Error.Category;
import com.xfsi.xav.util.Error.ContentType;
import com.xfsi.xav.util.Error.Reference;
import com.xfsi.xav.util.Error.TestType;
import com.xfsi.xav.util.property.PropertyMessageKey;
import com.xfsi.xav.util.property.PropertyMessageKeyTokenizer;

public abstract class AbstractValidator implements Test  {

    private static final String PROPERTY_FILE_NAME = "properties.xml";
    private static final String MSG_PREFIX = "msg.";
    private static final String INTERNAL_ERROR_ID = "XAV_INTERNAL: ";
    private static final String TKN01F001 = "malformed message key ";
    private static final String TKN01F002 = "null message key ";

    protected Properties properties;
    protected boolean propertiesInitialized;
    protected String propertyFile;
    private boolean valid;
    private String lastError;
    private PropertyMessageKeyTokenizer keyTokenizer;

    public AbstractValidator() {
        this.propertyFile = PROPERTY_FILE_NAME;
    }

    public AbstractValidator(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public boolean isRunnable(TestManager tm, TestInfo ti) {
        return true;
    }

    protected void initState(TestManager tm, TestInfo ti) throws Exception {
        loadProperties();
    }

    private void loadProperties() throws Exception {
        if (!propertiesInitialized) {
            InputStream is = null;
            valid = false;
            lastError = null;
            propertiesInitialized = true;
            try {
                URL url = getClass().getResource(propertyFile);
                if (url != null) {
                    if ((is = url.openStream()) != null) {
                        valid = true;
                        properties = new Properties();
                        properties.loadFromXML(is);
                        try {
                            Enumeration e = properties.propertyNames();
                            for (; e.hasMoreElements();) {
                                String propName = (String) e.nextElement();
                                if (propName.startsWith(MSG_PREFIX))
                                    getMsgKey(propName);
                            }
                        } catch (PropertyMessageKey.MalformedKeyException e) {
                            valid = false;
                            lastError = e.toString();
                            throw e;
                        }
                    }
                }
            } catch (InvalidPropertiesFormatException e) {
                valid = false;
                lastError = e.toString();
                throw e;
            } catch (IOException e) {
                valid = false;
                lastError = e.toString();
                throw e;
            } finally {
                closeSafely(is);
            }
        }
    }

    private String getClassDirectory() {
        String p = getClass().getName().replace( '.', '/' );
        return p.substring( 0, p.lastIndexOf( '/' ) + 1 );
    }

    private void closeSafely(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    private PropertyMessageKey getMsgKey(String key)
        throws PropertyMessageKey.MalformedKeyException {
        if (key != null) {
            if (key.startsWith(MSG_PREFIX))
                return parseKey(key.substring(MSG_PREFIX.length()));
            throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", invalid prefix, msg. expected");
        }
        throw new PropertyMessageKey.MalformedKeyException(TKN01F002);
    }

    protected String msg(String key, Object... formatArguments) {
        if (properties != null) {
            try {
                parseKey(key);
                String msg = properties.getProperty(MSG_PREFIX + key);
                if (msg != null && msg.length() > 0)
                    return MessageFormat.format(msg, formatArguments);
            } catch (PropertyMessageKey.MalformedKeyException e) {
                return INTERNAL_ERROR_ID + e.toString();
            }
        }
        return INTERNAL_ERROR_ID + "missing message: " + key;
    }

    protected String msgFormatterNV(String key, Object... formatArguments) {
        if (properties != null) {
            String msg = properties.getProperty(MSG_PREFIX + key);
            if (msg != null && msg.length() > 0)
                return String.format(msg, formatArguments);
        }
        return INTERNAL_ERROR_ID + "missing message: " + key;
    }

    protected PropertyMessageKey parseKey(String key) throws PropertyMessageKey.MalformedKeyException {
        if (keyTokenizer == null)
            keyTokenizer = new PropertyMessageKeyTokenizer();
        return parseKey(keyTokenizer, key);
    }

    protected PropertyMessageKey parseKey(PropertyMessageKeyTokenizer kt, String key) throws PropertyMessageKey.MalformedKeyException {
        PropertyMessageKey mk = new PropertyMessageKey();
        StringReader r = null;
        try {
            r = new StringReader(key);
            if (kt == null)
                kt = new PropertyMessageKeyTokenizer();
            else
                kt.reset();
            kt.setReader(r);
            int tokenCount = 0;
            int token = kt.nextToken();
            while (token != StreamTokenizer.TT_EOF) {
                ++tokenCount;
                switch (token) {
                case PropertyMessageKeyTokenizer.TT_NUMBER:
                    if (tokenCount == 2) {
                        mk.setValidatorCode(kt.nval);
                        if (kt.tokenLen != PropertyMessageKey.EXPECTED_VALIDATOR_CODE_LEN)
                            throw new PropertyMessageKey.MalformedKeyException(TKN01F001 +
                                key + ", expected validator code length " + PropertyMessageKey.EXPECTED_VALIDATOR_CODE_LEN + ", actual " + kt.tokenLen);
                    } else if (tokenCount == 4) {
                        mk.setErrorCode(kt.nval);
                        if (kt.tokenLen != PropertyMessageKey.EXPECTED_ERROR_CODE_LEN)
                            throw new PropertyMessageKey.MalformedKeyException(TKN01F001 +
                                key + ", expected error code length " + PropertyMessageKey.EXPECTED_ERROR_CODE_LEN + ", actual " + kt.tokenLen);
                    } else if (tokenCount == 6)
                        mk.setErrorCodeFraction(kt.nval);
                    else
                        throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", unexpected number token: " + kt.nval);
                    break;
                case PropertyMessageKeyTokenizer.TT_WORD:
                    if (tokenCount == 1)
                        mk.setValidatorId(kt.sval);
                    else if (tokenCount == 3)
                        mk.setSeverity(kt.sval);
                    else
                        throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", unexpected string token: " + kt.sval);
                    break;
                case PropertyMessageKeyTokenizer.TT_FRACTION:
                    if (tokenCount != 5)
                        throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", unexpected character: " + (char) kt.ttype);
                    break;
                default:
                    throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", unexpected character: " + (char) kt.ttype);
                }
                token = kt.nextToken();
            }
        } catch (IOException e) {
            throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", unexpected exception: " + e);
        } finally {
            if (r != null)
                r.close();
        }
        try {
            mk.checkValidity();
        } catch (PropertyMessageKey.MalformedKeyException e) {
            throw new PropertyMessageKey.MalformedKeyException(TKN01F001 + key + ", " + e);
        }
        return mk;
    }

    protected Error error(TestManager tm, TestType type, Category category, ContentType contentType, Reference reference, String subReference, String message, String messageKey) {
        Error.Severity severity = Error.Severity.UNSPECIFIED;
        try {
            PropertyMessageKey mk = getMsgKey(MSG_PREFIX + messageKey);
            category = verifyCategory(mk, category);
            severity = mk.getSeverity();
            if (isResultFiltered(tm, messageKey, severity))
                return null;
        } catch (PropertyMessageKey.MalformedKeyException e) {
            message = INTERNAL_ERROR_ID + e.toString();
        }
        return errorInternal(tm, type, category, contentType, reference, subReference, message, messageKey, severity);
    }

    private Category verifyCategory(PropertyMessageKey mk, Category category) {
        char sc = mk.getSeverityChar();
        if (sc == 'Y' || sc == 'X')
            return Error.Category.INTERNAL;
        return category;
    }

    private Error errorInternal(TestManager tm, TestType type, Category category,
        ContentType contentType, Reference reference, String subReference, String message, String messageKey, Error.Severity severity) {
        return new Error(messageKey, type, category, severity, contentType, reference, message, subReference);
    }

    protected boolean isResultFiltered(TestManager tm, String messageKey, Error.Severity severity) {
        if (tm != null) {
            if (tm.isFilteredResultSeverity(severity))
                return true;
            if (tm.isFilteredResultKey(messageKey))
                return true;
        }
        return false;
    }

}
