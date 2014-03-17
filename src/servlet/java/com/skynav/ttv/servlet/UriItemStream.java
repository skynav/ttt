
package com.skynav.ttv.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemHeaders;

public class UriItemStream implements FileItemStream {

    private String url;

    public UriItemStream(String url) {
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
        try {
            return new URI(url).toURL().openStream();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

}

