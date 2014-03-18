
package com.skynav.ttv.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.skynav.ttv.app.TimedTextVerifier;
import com.skynav.ttv.app.TimedTextVerifier.Results;

public class CheckerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY_NAME = "uploads";
    private static final String UPLOAD_FILE_PREFIX = "ttv";
    private static final String UPLOAD_FILE_SUFFIX = ".dat";
    private static final int UPLOAD_BUFFER_SIZE = 1<<16;
    private static final String REPORT_DIRECTORY_NAME = "reports";
    private static final String REPORT_FILE_PREFIX = "rpt";
    private static final String REPORT_FILE_SUFFIX = ".xml";

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
        String[] uris = (String[]) parameters.get("uri");
        if ((uris != null) && (uris.length > 0)) {
            URL url = null;
            try {
                url = new URI(uris[0]).toURL();
            } catch (URISyntaxException e) {
                state.setPreverifyException(e);
            } catch (MalformedURLException e) {
                state.setPreverifyException(e);
            } catch (IllegalArgumentException e) {
                state.setPreverifyException(e);
            }
            if (url != null) 
                processFileItem(state, new UrlItemStream(url));
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
                    if (!name.equals("fragment")) {
                        InputStream is = null;
                        try {
                            is = item.openStream();
                            state.addField(name, Streams.asString(is));
                        } finally {
                            StreamUtil.closeSafely(is);
                        }
                    } else {
                        String fragment = Streams.asString(item.openStream());
                        processFileItem(state, new FragmentItemStream(fragment));
                        state.fragment = fragment;
                    }
                } else {
                    processFileItem(state, item);
                }
            }
        } catch (FileUploadException e) {
            state.setPreverifyException(e);
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
                    state.uploadFileNameOriginal = item.getName();
                    if (state.uploadFileNameOriginal == null)
                        state.uploadFileNameOriginal = "unspecified";
                } finally {
                    StreamUtil.closeSafely(bos);
                    StreamUtil.closeSafely(bis);
                    StreamUtil.closeSafely(is);
                }
            } catch (IOException e) {
                state.setPreverifyException(e);
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

    private String createReporterFile(RequestState state) throws IOException {
        String reportFilePath = null;
        File reportDir = ensureReportDirectory();
        if (reportDir != null) {
            File reportFile = null;
            String prefix = REPORT_FILE_PREFIX;
            String suffix = REPORT_FILE_SUFFIX;
            reportFile = File.createTempFile(prefix, suffix, reportDir);
            if (reportFile != null) {
                reportFilePath = reportFile.getAbsolutePath();
                state.reportFile = reportFile;
            }
        }
        return reportFilePath;
    }

    private File ensureReportDirectory() {
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        File reportDir = new File(tempDir, REPORT_DIRECTORY_NAME);
        if (!reportDir.exists()) {
            synchronized (this) {
                if (!reportDir.exists()) {
                    if (!reportDir.mkdir())
                        reportDir = tempDir;
                }
            }
        }
        return reportDir;
    }

    private void processCheck(RequestState state) throws IOException {
        File upload = state.uploadFile;
        if ((upload != null) && upload.exists()) {
            TimedTextVerifier ttv = new TimedTextVerifier();
            List<String> args = new java.util.ArrayList<String>();
            if (state.getBooleanField("quiet"))
                args.add("-q");
            if (state.getBooleanField("verbose"))
                args.add("-v");
            if (state.getBooleanField("treatWarningAsError"))
                args.add("--treat-warning-as-error");
            args.add("--servlet");
            args.add("--reporter");
            args.add("xml");
            args.add("--reporter-file");
            args.add(createReporterFile(state));
            String urlString = upload.toURI().toString();
            args.add(urlString);
            ttv.run(args.toArray(new String[args.size()]));
            state.results = ttv.getResults(urlString);
        } else {
            state.results = new Results();
            if (state.preverifyException != null) {
                state.request.setAttribute("PreverifyExceptionMessage", state.preverifyException.getMessage());
            }
        }
    }

    private void processResponse(RequestState state) throws ServletException, IOException {
        HttpServletRequest request = state.request;
        HttpServletResponse response = state.response;
        if (state.results != null) {
            processReportWarnings(state);
            processReportErrors(state);
            request.setAttribute("Results", state.results);
            if (state.fragment != null)
                request.setAttribute("Fragment", state.fragment);
            else
                request.setAttribute("UploadFileNameOriginal", state.uploadFileNameOriginal);
        }
        request.getRequestDispatcher("/results.jsp").forward(request, response);
    }

    private void processReportErrors(RequestState state) throws ServletException, IOException {
        if (state.results != null) {
            HttpServletRequest request = state.request;
            try {
                String errors;
                if (state.results.errors > 0) {
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer t = tf.newTransformer(new StreamSource(getServletContext().getRealPath("/templates/errors.xsl")));
                    StringWriter sw = new StringWriter();
                    t.transform(new StreamSource(state.reportFile.getAbsolutePath()), new StreamResult(sw));
                    errors = sw.toString();
                } else
                    errors = null;
                request.setAttribute("Errors", errors);
            } catch (Throwable e) {
                state.setPreverifyException(e);
            }
        }
    }

    private void processReportWarnings(RequestState state) throws ServletException, IOException {
        if (state.results != null) {
            HttpServletRequest request = state.request;
            try {
                String warnings;
                if (state.results.warnings > 0) {
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer t = tf.newTransformer(new StreamSource(getServletContext().getRealPath("/templates/warnings.xsl")));
                    StringWriter sw = new StringWriter();
                    t.transform(new StreamSource(state.reportFile.getAbsolutePath()), new StreamResult(sw));
                    warnings = sw.toString();
                } else
                    warnings = null;
                request.setAttribute("Warnings", warnings);
            } catch (Throwable e) {
                state.setPreverifyException(e);
            }
        }
    }

    class RequestState {

        // request, response, and result output state
        HttpServletRequest request;
        HttpServletResponse response;
        // pre-verification state
        Throwable preverifyException;
        // fields state
        Map<String,List<String>> fields = new java.util.TreeMap<String,List<String>>();
        // upload state
        File uploadFile;
        int uploadFileLength;
        String uploadFileNameOriginal;
        String fragment;
        // report state
        File reportFile;
        // ttv state
        Results results;

        RequestState(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        void reset() {
            if (reportFile != null) {
                FileUtil.deleteSafely(reportFile);
                reportFile = null;
            }
            if (uploadFile != null) {
                FileUtil.deleteSafely(uploadFile);
                uploadFile = null;
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

        void setPreverifyException(Throwable e) {
            if (preverifyException == null)
                preverifyException = e;
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
