
package com.skynav.ttv.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemHeaders;

public class FragmentItemStream implements FileItemStream {

    private String fragment;

    public FragmentItemStream(String fragment) {
        this.fragment = fragment;
    }

    public FileItemHeaders getHeaders() {
        return null;
    }

    public void setHeaders(FileItemHeaders headers) {
    }

    public String getContentType() {
        return null;
    }

    public String getFieldName() {
        return "fragment";
    }

    public String getName() {
        return null;
    }

    public boolean isFormField() {
        return false;
    }

    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(fragment.getBytes("UTF-8"));
    }

}

