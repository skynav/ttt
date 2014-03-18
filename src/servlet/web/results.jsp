<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="javax.xml.namespace.QName" %>
<%@page import="com.skynav.ttv.app.TimedTextVerifier" %>
<%@page import="com.skynav.ttv.app.TimedTextVerifier.Results" %>
<%@page import="com.skynav.ttv.model.Model" %>
<%@page import="com.skynav.ttv.model.Models" %>
<%@page import="com.skynav.xml.helpers.XML" %>
<%@page contentType="text/html" pageEncoding="utf-8" %>
<%!
    static void putResultHeaderMatter(Results results, String uploadFileNameOriginal, JspWriter out) throws java.io.IOException {
        String titleLabel;
        String iconData;
        if ((results == null) || (results.errors > 0)) {
            titleLabel = "[Invalid]";
            iconData = "data:image/png,%89PNG%0D%0A%1A%0A%00%00%00%0DIHDR%00%00%00%10%00%00%00%10%08%02%00%00%00%90%91h6%00%00%00%19IDAT(%91c%BCd%AB%C2%40%0A%60%22I%F5%A8%86Q%0DCJ%03%00%DE%B5%01S%07%88%8FG%00%00%00%00IEND%AEB%60%82";
        } else {
            titleLabel = "[Valid]";
            iconData = "data:image/png,%89PNG%0D%0A%1A%0A%00%00%00%0DIHDR%00%00%00%10%00%00%00%10%08%02%00%00%00%90%91h6%00%00%00%19IDAT(%91c%0C%DD%10%C5%40%0A%60%22I%F5%A8%86Q%0DCJ%03%00dy%01%7F%0C%9F0%7D%00%00%00%00IEND%AEB%60%82";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<title>");
        sb.append(titleLabel);
        sb.append(" Markup Validation");
        if (uploadFileNameOriginal != null) {
            sb.append(" of ");
            sb.append(XML.escapeMarkup(uploadFileNameOriginal));
        }
        sb.append(" - W3C TTML Validation Service");
        sb.append("</title>\n");
        sb.append("<link rel=\"icon\" href=\""/*"*/);
        sb.append(iconData);
        sb.append("\" type=\"image/png\" />\n"/*"*/);
        out.println(sb.toString());
    }
    static void putResultJumpBar(HttpServletRequest request, Results results, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        if (results != null) {
            if ((results.warnings > 0) || (results.errors > 0)) {
                sb.append("<ul class=\"navbar\" id=\"jumpbar\">\n");
                sb.append("<li><strong>Jump To:</strong></li>\n");
                if (results.warnings > 0)
                  sb.append("<li><a href=\"#preparse_warnings\">Notes and Potential Issues</a></li>\n");
                if (results.errors > 0)
                  sb.append("<li><a title=\"Result of Validation\" href=\"#result\">Validation Output</a></li>\n");
                sb.append("</ul>\n");
            }
        }
        out.println(sb.toString());
    }
    static void putResultHeading(HttpServletRequest request, Results results, JspWriter out) throws java.io.IOException {
        String headingClass;
        String headingText;
        if (results == null) {
            headingClass = "invalid";
            headingText = "No results!";
        } else if (request.getAttribute("PreverifyExceptionMessage") != null) {
            headingClass = "invalid";
            headingText = "Unable to validate!";
        } else if (results.errors > 0) {
            headingClass = "invalid";
            headingText = "Errors found!";
        } else {
            headingClass = "valid";
            headingText = "This document was succcessfully checked as " + results.modelName.toUpperCase() + "!";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<h2 id=\"results\" class=\""/*"*/);
        sb.append(headingClass);
        sb.append("\">"/*"*/);                                                 
        sb.append(XML.escapeMarkup(headingText));
        sb.append("</h2>");
        out.println(sb.toString());
    }
    static void putRevalidateForm(HttpServletRequest request, Results results, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        putRevalidateFormStart(request, sb);
        sb.append("<table class=\"header\">\n");
        putResultCounts(request, results, sb);
        putResultSource(request, sb);
        putResultEncoding(results, sb);
        putResultModel(results, sb);
        putResultRoot(results, sb);
        sb.append("</table>\n");
        putRevalidateOptions(results, sb);
        putRevalidateFormEnd(sb);
        out.print(sb.toString());
    }
    static void putRevalidateFormStart(HttpServletRequest request, StringBuffer sb) {
        if (request.getParameter("uri") != null) {
            sb.append("<form id=\"form\" method=\"get\" action=\"check\">\n");
        } else if (request.getAttribute("Fragment") != null) {
            sb.append("<form id=\"form\" method=\"post\" enctype=\"multipart/form-data\" action=\"check\">\n");
        } else if (request.getAttribute("UploadFileNameOriginal") != null) {
            sb.append("<form id=\"form\" method=\"post\" enctype=\"multipart/form-data\" action=\"check\">\n");
        } else
            sb.append("<form id=\"form\">\n");
    }
    static void putRevalidateFormEnd(StringBuffer sb) {
        sb.append("</form>\n");
    }
    static void putResultCounts(HttpServletRequest request, Results results, StringBuffer sb) {
        String countsClass;
        String countsText;
        if (results == null) {
            countsClass = "invalid";
            countsText = "Failed, unable to validate.";
        } else if (request.getAttribute("PreverifyExceptionMessage") != null) {
            countsClass = "invalid";
            countsText = "Unable to validate: " + request.getAttribute("PreverifyExceptionMessage");
        } else if (results.errors > 0) {
            int errors = results.errors;
            int warnings = results.warnings;
            countsClass = "invalid";
            StringBuffer sbText = new StringBuffer();
            sbText.append("Failed, with ");
            sbText.append(errors);
            sbText.append(" error");
            if (errors > 1)
                sbText.append('s');
            if (warnings > 0) {
                sbText.append(", and ");
                sbText.append(warnings);
                sbText.append(" warning");
                if (warnings > 1)
                    sbText.append('s');
            }
            countsText = sbText.toString();
        } else if (results.warnings > 0) {
            int warnings = results.warnings;
            countsClass = "valid";
            StringBuffer sbText = new StringBuffer();
            sbText.append("Passed, with ");
            sbText.append(warnings);
            sbText.append(" warning");
            if (warnings > 1)
                sbText.append('s');
            countsText = sbText.toString();
        } else {
            countsClass = "valid";
            countsText = "Passed";
        }
        sb.append("<tr>\n");
        sb.append("<th>Result :</th>\n");
        sb.append("<td colspan=\"2\" class=\""/*"*/);
        sb.append(XML.escapeMarkup(countsClass));
        sb.append("\">"/*"*/);
        sb.append(XML.escapeMarkup(countsText));
        sb.append('.');
        sb.append("</td>\n");
        sb.append("</tr>\n");
    }
    static void putResultSource(HttpServletRequest request, StringBuffer sb) {
        sb.append("<tr>\n");
        if (request.getParameter("uri") != null) {
            String uri = request.getParameter("uri");
            sb.append("<th><label title=\"Address of Page to Validate\" for=\"uri\">Address</label>:</th>\n");
            sb.append("<td colspan=\"2\"><input type=\"text\" id=\"uri\" name=\"uri\" size=\"80\" value=\""/*"*/);
            sb.append(XML.escapeMarkup(uri));
            sb.append("\" /></td>\n"/*"*/);
        } else if (request.getAttribute("Fragment") != null) {
            String fragment = (String) request.getAttribute("Fragment");
            sb.append("<th><label title=\"Uploaded source of your Document\" for=\"fragment\">Source</label>:</th>\n");
            sb.append("<td colspan=\"2\"><textarea id=\"fragment\" name=\"fragment\" rows=\"12\" cols=\"80\">");
            sb.append(XML.escapeMarkup(fragment));
            sb.append("</textarea>\n");
            sb.append("</td>");
        } else if (request.getAttribute("UploadFileNameOriginal") != null) {
            String uploadFileName = (String) request.getAttribute("UploadFileNameOriginal");
            sb.append("<th><label title=\"Choose a Local File to Upload and Validate\" for=\"uploaded_file\">File</label>:</th>\n");
            sb.append("<td colspan=\"2\">\n");
            sb.append("<input type=\"file\" id=\"uploaded_file\" name=\"uploaded_file\" size=\"30\" />\n");
            sb.append("<p class=\"revalidate_instructions\">Use the file selection box above if you wish to re-validate the uploaded file ");
            sb.append(XML.escapeMarkup(uploadFileName));
            sb.append(".</p>\n");    
            sb.append("</td>\n");
        }
        sb.append("</tr>\n");
    }
    static void putResultEncoding(Results results, StringBuffer sb) {
        if (results != null) {
            String encoding = results.encodingName;
            if (encoding == null)
                encoding = "unknown";
            sb.append("<tr>\n");
            sb.append("<th><label title=\"Character Encoding\" for=\"charset\">Encoding</label>:</th>\n");
            sb.append("<td>");
            sb.append(XML.escapeMarkup(encoding.toLowerCase()));
            sb.append("</td>\n");
            sb.append("<td>\n");
            sb.append("<select name=\"charset\" id=\"charset\">\n");
            sb.append("<option value=\"(detect automatically)\">(detect automatically)</option>\n");
            sb.append("<option value=\"utf-8\">utf-8</option>\n");
            sb.append("<option value=\"utf-16\">utf-16</option>\n");
            sb.append("<option value=\"utf-32\">utf-32</option>\n");
            sb.append("</select>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
        }
    }
    static void putResultModel(Results results, StringBuffer sb) {
        if (results != null) {
            String model = results.modelName;
            if (model == null)
                model = "unknown";
            sb.append("<tr>\n");
            sb.append("<th><label title=\"Model\" for=\"model\">Model</label>:</th>\n");
            sb.append("<td>");
            sb.append(XML.escapeMarkup(model.toLowerCase()));
            sb.append("</td>\n");
            sb.append("<td>\n");
            sb.append("<select name=\"model\" id=\"model\">\n");
            putModelOptions(sb);
            sb.append("</select>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
        }
    }
    static void putModelOptions(StringBuffer sb) {
        String defaultModelName = Models.getDefaultModelName();
        for (String name : Models.getModelNames()) {
            StringBuffer sbOption = new StringBuffer();
            sbOption.append("<option");
            sbOption.append(" value=\""/*"*/);
            sbOption.append(XML.escapeMarkup(name));
            sbOption.append('\"'/*"*/);
            if (name.equals(defaultModelName))
                sbOption.append(" selected=\"selected\"");
            sbOption.append('>');                                              
            sbOption.append(XML.escapeMarkup(name.toLowerCase()));
            sbOption.append("</option>\n");
            sb.append(sbOption.toString());
        }
    }
    static void putResultRoot(Results results, StringBuffer sb) {
        if (results != null) {
            QName root = results.root;
            if (root != null) {
                sb.append("<tr>\n");
                sb.append("<th>Root Element :</th>\n");
                sb.append("<td colspan=\"2\">");
                sb.append(XML.escapeMarkup(root.getLocalPart()));
                sb.append("</td>\n");
                sb.append("</tr>\n");
                sb.append("<tr>\n");
                sb.append("<th>Root Element Namespace :</th>\n");
                sb.append("<td colspan=\"2\">");
                sb.append(XML.escapeMarkup(root.getNamespaceURI()));
                sb.append("</td>\n");
                sb.append("</tr>\n");
            }
        }
    }
    static void putRevalidateOptions(Results results, StringBuffer sb) {
        sb.append("<fieldset id=\"revalidate_opts\">\n");
        sb.append("<legend>Options</legend>\n");
        sb.append("<table class=\"header\">\n");
        sb.append("<tr>\n");
        sb.append("<td><input id=\"treatWarningAsError\" name=\"treatWarningAsError\" type=\"checkbox\" value=\"1\" /><label title=\"Treat Warning as Error\" for=\"treatWarningAsError\">Treat Warning as Error</label></td>\n");
        sb.append("<td><input id=\"verbose\" name=\"verbose\" type=\"checkbox\" value=\"1\" /><label title=\"Verbose Output\" for=\"verbose\">Verbose Output</label></td>\n");
        sb.append("</tr>\n");
        sb.append("</table>\n");
        sb.append("<div id=\"revalidate_button\" class=\"submit_button\">\n");
        sb.append("<input type=\"hidden\" value=\"W3C_TTML_Validator/1.0 http://validator.w3.org/services\" id=\"user-agent\" name=\"user-agent\" />\n");
        sb.append("<input type=\"submit\" value=\"Revalidate\" title=\"Validate this document again\" />\n");
        sb.append("</div>\n");
        sb.append("</fieldset>\n");
    }
    static void putResults(String errors, String warnings, JspWriter out) throws java.io.IOException {
        if ((warnings == null) && (errors == null)) {
            // [TBD] print congratulations info
        } else {
            if (warnings != null)
                out.println(warnings);
            if (errors != null)
                out.println(errors);
        }
    }
    static void putVersionInfo(ServletContext context, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("This service runs the ");
        sb.append("<a href=\""/*"*/);
        sb.append(XML.escapeMarkup(TimedTextVerifier.getRepositoryURL()));
        sb.append("\">"/*"*/);
        sb.append(XML.escapeMarkup(TimedTextVerifier.getVersionTitle()));
        sb.append("</a>");
        sb.append(", using ");
        sb.append(XML.escapeMarkup(context.getServerInfo()));
        sb.append(" with Java Runtime ");
        sb.append(XML.escapeMarkup(System.getProperty("java.runtime.version")));
        sb.append('.');
        out.println(sb.toString());                                          
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<%
      Results results = (Results) request.getAttribute("Results");
      putResultHeaderMatter(results, (String) request.getAttribute("UploadFileNameOriginal"), out);
%>
    <link rev="made" href="mailto:www-validator@w3.org" />
    <link rev="start" href="./verify" title="Home Page" />
    <link rel="stylesheet" href="./style/base.css" type="text/css" />
    <link rel="stylesheet" href="./style/results.css" type="text/css" />
    <meta name="keywords" content="TTML, Timed Text Markup Language, Validation, W3C TTML Validation Service, TTV, ST2052" />
    <meta name="description" content="W3C's easy-to-use TTML validation service." />
  </head>
  <body>
    <div id="banner">
      <h1 id="title">
        <a href="http://www.w3.org/"><img alt="W3C" width="110" height="61" id="logo" src="./images/w3c.png" /></a>
 	<a href="./verify"><span>TTML Validation Service</span></a>
      </h1>
      <p id="tagline">Check the syntax and semantics of Timed Text Markup Language (TTML) documents.</p>
    </div>
    <div id="results_container">
<%
      putResultJumpBar(request, results, out);
      putResultHeading(request, results, out);
      putRevalidateForm(request, results, out);
      putResults((String)request.getAttribute("Errors"), (String)request.getAttribute("Warnings"), out);
%>
    </div><!-- results_container-->
    <div id="w3c-include" lang="en" dir="ltr">
      <div class="w3c-include" id="w3c-include-validator-donation">
        <p>
          <a href="http://www.netflix.com" title="Netflix is a supporter of the W3C TTML Validator" class="w3c-include-validator-donation-img">
            <img src="./images/netflix-logo.png" alt="Netflix logo" class="w3c-include-sponsor-img" height="64" />
          </a>
        </p>
        <p>
          <span>The W3C TTML Validator was developed with assistance from Netflix, and supported by community donations.</span>
          <span><a href="http://www.w3.org/QA/Tools/Donate">Donate</a> and help us build better tools for a better Web.</span>
        </p>
      </div>
    </div>
    <div id="footer">
      <p id="activity_logos">
        <a href="http://www.w3.org/Status" title="W3C's Open Source, bringing you free Web quality tools and more"><img src="http://www.w3.org/Icons/WWW/w3c_home_nb" alt="W3C" width="72" height="47" /><img src="./images/opensource-55x48.png" alt="Open-Source" title="We are building certified Open Source/Free Software. - see www.opensource.org" width="55" height="48" /></a>
      </p>
      <p id="support_logo">
        <a href="http://www.w3.org/QA/Tools/Donate">
          <img src="http://www.w3.org/QA/Tools/I_heart_validator" alt="I heart Validator logo" title=" Validators Donation Program" width="80" height="15" />
        </a>
      </p>
      <p id="version_info">
<%
      putVersionInfo(application, out);
%>
      </p>
      <p class="copyright">
        <a rel="Copyright" href="http://www.w3.org/Consortium/Legal/ipr-notice#Copyright">Copyright</a> &copy; 1994-2014
        <a href="http://www.w3.org/"><acronym title="World Wide Web Consortium">W3C</acronym></a>&reg;
        (<a href="http://www.csail.mit.edu/"><acronym title="Massachusetts Institute of Technology">MIT</acronym></a>,
        <a href="http://www.ercim.eu/"><acronym title="European Research Consortium for Informatics and Mathematics">ERCIM</acronym></a>,
        <a href="http://www.keio.ac.jp/">Keio</a>, <a href="http://ev.buaa.edu.cn/">Beihang</a>),
        All Rights Reserved.
        W3C <a href="http://www.w3.org/Consortium/Legal/ipr-notice#Legal_Disclaimer">liability</a>,
        <a href="http://www.w3.org/Consortium/Legal/ipr-notice#W3C_Trademarks">trademark</a>,
        <a rel="Copyright" href="http://www.w3.org/Consortium/Legal/copyright-documents">document use</a>
        and <a rel="Copyright" href="http://www.w3.org/Consortium/Legal/copyright-software">software licensing</a> rules apply. Your interactions with this site are
        in accordance with our <a href="http://www.w3.org/Consortium/Legal/privacy-statement#Public">public</a> and
        <a href="http://www.w3.org/Consortium/Legal/privacy-statement#Members">Member</a> privacy statements.
      </p>
    </div><!-- footer -->
  </body>
</html>
