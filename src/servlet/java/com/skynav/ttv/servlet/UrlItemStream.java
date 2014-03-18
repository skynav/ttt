
package com.skynav.ttv.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemHeaders;

public class UrlItemStream implements FileItemStream {

    private URL url;

    public UrlItemStream(URL url) {
        this.url = url;
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
        return "url";
    }

    public String getName() {
        return null;
    }

    public boolean isFormField() {
        return false;
    }

    public InputStream openStream() throws IOException {
        return url.openStream();
    }

}

