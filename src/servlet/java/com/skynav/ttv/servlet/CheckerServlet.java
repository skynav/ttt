
package com.skynav.ttv.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.skynav.ttv.app.TimedTextVerifier;

public class CheckerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY_NAME = "uploads";
    private static final String UPLOAD_FILE_PREFIX = "ttv";
    private static final String UPLOAD_FILE_SUFFIX = ".dat";
    private static final int UPLOAD_BUFFER_SIZE = 1<<16;
    private static final String OUTPUT_STREAM_ENCODING = "UTF-8";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doCheck(new RequestState(request, response));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doCheck(new RequestState(request, response));
    }

    private void doCheck(RequestState state) throws ServletException, IOException {
        try {
            if (!ServletFileUpload.isMultipartContent(state.request))
                processQueryRequest(state);
            else
                processNonQueryRequest(state);
            processCheck(state);
            processResponse(state);
        } finally {
            state.reset();
        }
    }

    private void processQueryRequest(RequestState state) throws ServletException, IOException {
        assert !ServletFileUpload.isMultipartContent(state.request);
        Map<String,String[]> parameters = state.request.getParameterMap();
        for (String name : parameters.keySet()) {
            for (String value : parameters.get(name)) {
                state.addField(name, value);
            }
        }
    }

    private void processNonQueryRequest(RequestState state) throws ServletException, IOException {
        assert ServletFileUpload.isMultipartContent(state.request);
        ServletFileUpload upload = new ServletFileUpload();
        try {
            for (FileItemIterator iter = upload.getItemIterator(state.request); iter.hasNext(); ) {
                FileItemStream item = iter.next();
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    InputStream is = null;
                    try {
                        is = item.openStream();
                        state.addField(item.getFieldName(), Streams.asString(is));
                    } finally {
                        StreamUtil.closeSafely(is);
                    }
                } else {
                    processFileItem(state, item);
                }
            }
        } catch (FileUploadException e) {
        }
    }

    private void processFileItem(RequestState state, FileItemStream item) throws ServletException, IOException {
        File uploadDir = ensureUploadDirectory();
        if (uploadDir != null) {
            File uploadFile = null;
            try {
                String prefix = UPLOAD_FILE_PREFIX;
                String suffix = UPLOAD_FILE_SUFFIX;
                uploadFile = File.createTempFile(prefix, suffix, uploadDir);
                InputStream is = null;
                InputStream bis = null;
                OutputStream bos = null;
                try {
                    is = item.openStream();
                    bis = new BufferedInputStream(is, UPLOAD_BUFFER_SIZE);
                    bos = new BufferedOutputStream(new FileOutputStream(uploadFile), UPLOAD_BUFFER_SIZE);
                    byte[] buffer = new byte[UPLOAD_BUFFER_SIZE];
                    int fileLength = 0;
                    for (int nb = 0; (nb = bis.read(buffer)) >= 0;) {
                        if (nb > 0) {
                            bos.write(buffer, 0, nb);
                            fileLength += nb;
                        }
                    }
                    state.uploadFile = uploadFile;
                    state.uploadFileLength = fileLength;
                } finally {
                    StreamUtil.closeSafely(bos);
                    StreamUtil.closeSafely(bis);
                    StreamUtil.closeSafely(is);
                }
            } catch (IOException e) {
            }
        }
    }

    private File ensureUploadDirectory() {
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        File uploadDir = new File(tempDir, UPLOAD_DIRECTORY_NAME);
        if (!uploadDir.exists()) {
            synchronized (this) {
                if (!uploadDir.exists()) {
                    if (!uploadDir.mkdir())
                        uploadDir = tempDir;
                }
            }
        }
        return uploadDir;
    }

    private void processCheck(RequestState state) {
        File upload = state.uploadFile;
        if ((upload != null) && upload.exists()) {
            TimedTextVerifier ttv = new TimedTextVerifier();
            List<String> args = new java.util.ArrayList<String>();
            if (state.getBooleanField("quiet"))
                args.add("-q");
            if (state.getBooleanField("verbose"))
                args.add("-v");
            args.add("--servlet");
            String urlString = upload.toURI().toString();
            args.add(urlString);
            ttv.getReporter().setOutput(state.getOutput());
            ttv.run(args.toArray(new String[args.size()]));
            state.resultCode = ttv.getResultCode(urlString);
            state.resultFlags = ttv.getResultFlags(urlString);
            state.checkComplete = true;
        }
    }

    private void processResponse(RequestState state) throws ServletException, IOException {
        state.response.setContentType("text/plain");
        state.response.setCharacterEncoding(OUTPUT_STREAM_ENCODING);
        PrintWriter out = state.response.getWriter();
        StringBuffer sb = new StringBuffer();
        for (String name : state.fields.keySet()) {
            for (String value : state.fields.get(name)) {
                sb.setLength(0);
                sb.append(name);
                sb.append(':');
                sb.append(value);
                out.println(sb.toString());
            }
        }
        if (state.uploadFile != null) {
            out.println("upload_uri: " + state.uploadFile.toURI());
            out.println("upload_length: " + state.uploadFileLength);
        }
        if (state.checkComplete) {
            int resultCode = state.resultCode;
            int resultFlags = state.resultFlags;
            if (resultCode == TimedTextVerifier.RV_PASS) {
                if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) != 0)
                    fail(state, "unexpected success with expected error(s) match.");
                else if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_UNEXPECTED) != 0)
                    fail(state, "unexpected success with unexpected warning(s).");
                else if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0)
                    fail(state, "unexpected success with expected warning(s) mismatch.");
                else
                    pass(state, "passed as expected");
            } else if (resultCode == TimedTextVerifier.RV_FAIL) {
                if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_UNEXPECTED) != 0)
                    fail(state, "unexpected failure with unexpected error(s).");
                else if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0)
                    fail(state, "unexpected failure with expected error(s) mismatch.");
                else
                    pass(state, "failed as expected");
            } else
                fail(state, "unexpected result code " + resultCode + ".");
        }
        if (state.outputStreamBuffer.size() > 0) {
            out.println("output_byte_length: " + state.outputStreamBuffer.size());
            out.println("output:");
            out.print(state.outputStreamBuffer.toString(OUTPUT_STREAM_ENCODING));
        }
    }

    private void pass(RequestState state, String message) throws IOException {
        PrintWriter out = state.response.getWriter();
        out.println("response: pass: " + message);
    }

    private void fail(RequestState state, String message) throws IOException {
        PrintWriter out = state.response.getWriter();
        out.println("response: fail: " + message);
    }

    class RequestState {

        // request, response, and result output state
        HttpServletRequest request;
        HttpServletResponse response;
        // buffered output stream state
        ByteArrayOutputStream outputStreamBuffer;
        PrintWriter output;
        // fields state
        Map<String,List<String>> fields = new java.util.TreeMap<String,List<String>>();
        // upload state
        File uploadFile;
        int uploadFileLength;
        // ttv state
        int resultCode;
        int resultFlags;
        boolean checkComplete;

        RequestState(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
            this.outputStreamBuffer = new ByteArrayOutputStream();
            this.output = createOutput(this.outputStreamBuffer);
        }

        PrintWriter getOutput() {
            return output;
        }

        void reset() {
            if (uploadFile != null) {
                FileUtil.deleteSafely(uploadFile);
                uploadFile = null;
            }
            if (output != null) {
                output.close();
                output = null;
            }
            if (outputStreamBuffer != null) {
                StreamUtil.closeSafely(outputStreamBuffer);
                outputStreamBuffer = null;
            }
        }

        void addField(String name, String value) {
            if (!fields.containsKey(name))
                fields.put(name, new java.util.ArrayList<String>());
            fields.get(name).add(value);
        }

        boolean getBooleanField(String name) {
            if (!fields.containsKey(name))
                return false;
            else {
                List<String> values = fields.get(name);
                if (values.isEmpty())
                    return false;
                else
                    return values.get(0).equals("1");
            }
        }

        private PrintWriter createOutput(OutputStream outputStream) {
            try {
                return new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, OUTPUT_STREAM_ENCODING)));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

    }

    static class StreamUtil {
        static void closeSafely(InputStream stream) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) { }
            }
        }
        static void closeSafely(OutputStream stream) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) { }
            }
        }
    }

    static class FileUtil {
        static void deleteSafely(File file) {
            if (file != null) {
                if (!file.delete())
                    file.deleteOnExit();
            }
        }
    }

}
