
package com.skynav.ttv.servlet;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemHeaders;

public class FragmentItemStream implements FileItemStream {

    private FileItemStream item;

    public FragmentItemStream(FileItemStream item) {
        assert item != null;
        assert item.getFieldName().equals("fragment");
        this.item = item;
    }

    public FileItemHeaders getHeaders() {
        return null;
    }

    public void setHeaders(FileItemHeaders headers) {
    }

    public String getContentType() {
        return item.getContentType();
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
        return item.openStream();
    }

}

