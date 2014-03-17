<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="com.skynav.ttv.app.TimedTextVerifier" %>
<%@page import="com.skynav.ttv.app.TimedTextVerifier.Results" %>
<%@page import="com.skynav.ttv.model.Model" %>
<%@page import="com.skynav.ttv.model.Models" %>
<%@page contentType="text/html" pageEncoding="utf-8" %>
<%!
    static void putResultHeaderMatter(Results results, String uploadFileNameOriginal, JspWriter out) throws java.io.IOException {
        String titleLabel;
        String iconData;
        if (results.errors > 0) {
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
            sb.append(uploadFileNameOriginal);
        }
        sb.append(" - W3C TTML Validation Service");
        sb.append("</title>\n");
        sb.append("<link rel=\"icon\" href=\""/*"*/);
        sb.append(iconData);
        sb.append("\" type=\"image/png\" />\n"/*"*/);
        out.println(sb.toString());
    }
    static void putResultJumpBar(Results results, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        if ((results.warnings > 0) || (results.errors > 0)) {
            sb.append("<ul class=\"navbar\" id=\"jumpbar\">\n");
            sb.append("<li><strong>Jump To:</strong></li>\n");
            if (results.warnings > 0)
              sb.append("<li><a href=\"#preparse_warnings\">Notes and Potential Issues</a></li>\n");
            if (results.errors > 0)
              sb.append("<li><a title=\"Result of Validation\" href=\"#result\">Validation Output</a></li>\n");
            sb.append("</ul>\n");
        }
        out.println(sb.toString());
    }
    static void putResultHeading(Results results, JspWriter out) throws java.io.IOException {
        String headingClass;
        String headingText;
        if (results.errors > 0) {
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
        sb.append(headingText);
        sb.append("</h2>");
        out.println(sb.toString());
    }
    static void putResultCounts(Results results, JspWriter out) throws java.io.IOException {
        String countsClass;
        String countsText;
        int errors = results.errors;
        int warnings = results.warnings;
        if (errors > 0) {
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
        } else if (warnings > 0) {
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
        StringBuffer sb = new StringBuffer();
        sb.append("<td colspan=\"2\" class=\""/*"*/);
        sb.append(countsClass);
        sb.append("\">"/*"*/);                                                 
        sb.append(countsText);
        sb.append('.');
        sb.append("</td>\n");
        out.println(sb.toString());
    }
    static void putResultEncoding(Results results, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        String encoding = results.encodingName;
        if ((encoding != null) && (encoding.length() > 0))
            sb.append(encoding.toLowerCase());
        out.println(sb.toString());
    }
    static void putResultModel(Results results, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        String model = results.modelName;
        if ((model != null) && (model.length() > 0))
            sb.append(model);
        out.println(sb.toString());
    }
    static void putResults(String errors, String warnings, JspWriter out) throws java.io.IOException {
        if ((warnings == null) && (errors == null)) {
        } else {
            if (warnings != null)
                out.println(warnings);
            if (errors != null)
                out.println(errors);
        }
    }
    static void putModelOptions(JspWriter out) throws java.io.IOException {
        String defaultModelName = Models.getDefaultModelName();
        StringBuffer sb = new StringBuffer();
        for (String name : Models.getModelNames()) {
            sb.setLength(0);
            sb.append("<option");
            sb.append(" value=\""/*"*/);
            sb.append(name);
            sb.append('\"'/*"*/);
            if (name.equals(defaultModelName))
                sb.append(" selected=\"selected\"");
            sb.append('>');                                              
            sb.append(name.toLowerCase());
            sb.append("</option>");
            out.println(sb.toString());
        }
    }
    static void putVersionInfo(JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("This service runs the ");
        sb.append("<a href=\""/*"*/);
        sb.append(TimedTextVerifier.getRepositoryURL());
        sb.append("\">"/*"*/);
        sb.append(TimedTextVerifier.getVersionTitle());
        sb.append("</a>.");
        out.println(sb.toString());                                          
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<%
      putResultHeaderMatter((Results)request.getAttribute("Results"), (String)request.getAttribute("UploadFileNameOriginal"), out);
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
      putResultJumpBar((Results)request.getAttribute("Results"),out);
      putResultHeading((Results)request.getAttribute("Results"),out);
%>
      <form id="form" method="post" enctype="multipart/form-data" action="check">
        <table class="header">
          <tr>
            <th>Result:</th>
<%
      putResultCounts((Results)request.getAttribute("Results"),out);
%>
          </tr>
          <tr>
            <th><label title="Choose a Local File to Upload and Validate" for="uploaded_file">File</label>:</th>
            <td colspan="2">
              <input type="file" id="uploaded_file" name="uploaded_file" size="30" />
              <p class="revalidate_instructions">Use the file selection box above if you wish to re-validate the uploaded file ttml.xml</p>
            </td>
          </tr>
          <tr>
            <th><label title="Character Encoding" for="charset">Encoding</label>:</th>
            <td>
<%
      putResultEncoding((Results)request.getAttribute("Results"),out);
%>
            </td>
            <td>
              <select name="charset" id="charset">
                <option value="(detect automatically)" selected="selected">(detect automatically)</option>
                <option value="utf-8">utf-8</option>
                <option value="utf-16">utf-16</option>
                <option value="utf-32">utf-32</option>
              </select>
            </td>
          </tr>
          <tr>
            <th>
              <label for="model">Model</label>
            </th>
            <td>
<%
      putResultModel((Results)request.getAttribute("Results"),out);
%>
            </td>
            <td>
              <select id="model" name="model">
<%
      putModelOptions(out);
%>
              </select>
            </td>
          </tr>
          <tr>
            <th>Root Element:</th>
            <td colspan="2">tt</td>
          </tr>
        </table>
        <fieldset id="revalidate_opts">
          <legend>Options</legend>
          <table class="header">
            <tr>
              <td><input id="treatWarningAsError" name="treatWarningAsError" type="checkbox" value="1" /><label title="Treat Warning as Error" for="treatWarningAsError">Treat Warning as Error</label></td>
              <td><input id="verbose" name="verbose" type="checkbox" value="1" /><label title="Verbose Output" for="verbose">Verbose Output</label></td>
            </tr>
          </table>
          <div id="revalidate_button" class="submit_button">
            <input type="hidden" value="W3C_TTML_Validator/1.0 http://validator.w3.org/services" id="user-agent" name="user-agent" />
            <input type="submit" value="Revalidate" title="Validate this document again" />
          </div>
        </fieldset>
      </form>
<%
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
      putVersionInfo(out);
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
