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

import java.io.InputStream;

// import com.xfsi.xav.util.MimeType;

/**
 * An object that will contain all the information pertaining to a
 * given XML described test.  This object is instantiated by the
 * validation engine and passed off to a Test object.
 */
public interface TestInfo {
    /**
     * an accessor to the name of the test
     *
     * @return the test name as returned from the XML description file
     */
    public String getName();

    /**
     * an accessor to the description of the test
     *
     * @return the test description as returned from the XML description file
     */
    public String getDescription();

    /**
     * an accessor to the arguments of the test
     *
     * @return the test arguments as returned from the XML description file
     */
    public String[] getArguments();

    /**
     * return the pathname of the resource to be validated
     *
     * @return the pathname of the resource to be validated
     */
    public String getResourceName();

    /**
     * set the pathname of the resource to be validated
     *
     * @param name the pathname of the resource to be validated
     */
    public void setResourceName( String name );

    /**
     * return a stream, initialized at zero, that contains the resource
     * to be validated
     *
     * @return an input stream pointing to the resource to be validated
     */
    public InputStream getResourceStream();

    /**
     * Set the stream that contains the resource to be validated
     *
     * @param stream an input stream pointing to the resource to be validated
     */
    public void setResourceStream( InputStream stream );

    /**
     * return the MIME type this test applies to
     *
     * @return the MIME type this test applies to
     */
    // public MimeType getMimeType();

    /**
     * set the MIME type of resource to be validated
     *
     * @param type the MIME type this test applies to
     */
    // public void setMimeType( MimeType type );

    /**
     * returns whether a resource hierarchy is extracted to disk
     *
     * @return is resource hierarchy extracted to disk
     */
    public boolean hasResourceHierarchy();

    /**
     * returns resource hierarchy pathname
     *
     * @return resource hierarchy pathname
     */
    public String getResourceHierarchyPathname();

    /**
     * returns resource hierarchy application subdirectory pathname
     *
     * @return resource hierarchy application subdirectory pathname
     */
    public String getResourceHierarchyAppPathname();

    /**
     * an accessor to test dependence
     *
     * @return the test name that this test depends upon
     */
    public String getDependsUpon();

    /**
     * an accessor to the vesion of the test
     *
     * @return the test version string
     */
    public String getVersion();
}
