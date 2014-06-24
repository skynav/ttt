<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="java.io.File" %>
<%@page import="com.skynav.ttv.app.TimedTextVerifier" %>
<%@page import="com.skynav.ttv.model.Model" %>
<%@page import="com.skynav.ttv.model.Models" %>
<%@page contentType="text/html" pageEncoding="utf-8" %>
<%!
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
    static void putVersionInfo(ServletContext context, JspWriter out) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("This service runs the ");
        sb.append("<a href=\""/*"*/);
        sb.append(TimedTextVerifier.getRepositoryURL());
        sb.append("\">"/*"*/);
        sb.append(TimedTextVerifier.getVersionTitle());
        sb.append("</a>");
        sb.append(", using ");
        sb.append(context.getServerInfo());
        sb.append(" with Java Runtime ");
        sb.append(System.getProperty("java.runtime.version"));
        sb.append('.');
        out.println(sb.toString());                                          
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <title>The W3C TTML Validation Service</title>
    <link rev="made" href="mailto:www-validator@w3.org" />
    <link rel="shortcut icon" href="http://www.w3.org/2008/site/images/favicon.ico" type="image/x-icon" />
    <link rev="start" href="./verify" title="Home Page" />
    <link rel="stylesheet" href="./style/base.css" type="text/css" />
    <script type="text/javascript" src="./scripts/combined.js"></script>
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
    <div id="frontforms">
      <ul id="tabset_tabs">
	<li><a href="#validate-by-uri"><span>Validate by</span> URI</a></li>
	<li><a href="#validate-by-upload"><span>Validate by</span> File Upload</a></li>
	<li><a href="#validate-by-input"><span>Validate by</span> Direct Input</a></li>
      </ul>
      <div id="fields">
        <fieldset id="validate-by-uri" class="tabset_content front"><legend class="tabset_label">Validate by URI</legend>
          <form method="get" action="check">
            <p class="instructions">
	      Validate a document online:     
            </p>
            <p>
	      <label title="Address of page to Validate" for="uri">Address:</label>
              <input type="text" name="uri" id="uri" size="45" />
            </p>
            <fieldset id="extra_opt_uri" class="moreoptions">
	      <legend class="toggletext"><a href="#validate_by_uri+with_options"><img id="toggleiconURI" class="toggleicon" src="./images/arrow-closed.png" alt="Show" /> More Options</a></legend>
	      <div class="options">
                <table>
                  <tr>
                    <th>
                      <label for="uri-encoding">Character Encoding</label>
                    </th>
                    <td>
                      <select id="uri-encoding" name="encoding">
                        <option value="(detect automatically)" selected="selected">(detect automatically)</option>
                        <option value="utf-8">utf-8</option>
                        <option value="utf-16">utf-16</option>
                        <option value="utf-32">utf-32</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <label for="uri-model">Model</label>
                    </th>
                    <td>
                      <select id="uri-model" name="model">
<%
      putModelOptions(out);
%>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td><input id="treatWarningAsError" name="treatWarningAsError" type="checkbox" value="1" /><label title="Treat Warning as Error" for="treatWarningAsError">Treat Warning as Error</label></td>
                    <td><input id="uri-verbose" name="verbose" type="checkbox" value="1" /><label title="Verbose Output" for="uri-verbose">Verbose Output</label></td>
                  </tr>
                </table>
              </div>
            </fieldset>
	    <p class="submit_button"><input type="submit" title="Submit for validation" value="Check" /></p>
            <input type="hidden" name="quiet" value="1" />
          </form>
        </fieldset>
        <fieldset id="validate-by-upload"  class="tabset_content front"><legend class="tabset_label">Validate by File Upload</legend>
          <form method="post" enctype="multipart/form-data" action="check">
            <p class="instructions">Upload a document for validation:</p>
            <p><label title="Choose a Local File to Upload and Validate" for="uploaded_file">File:</label>
              <input type="file" id="uploaded_file" name="uploaded_file" size="30" /></p>        
            <fieldset id="extra_opt_upload" class="moreoptions">
	      <legend class="toggletext"><a href="#validate_by_upload+with_options"><img class="toggleicon" src="./images/arrow-closed.png" alt="Show" /> More Options</a></legend>
	      <div class="options">
                <table>
                  <tr>
                    <th>
                      <label for="upload-encoding">Character Encoding</label>
                    </th>
                    <td>
                      <select id="upload-encoding" name="encoding">
                        <option value="(detect automatically)" selected="selected">(detect automatically)</option>
                        <option value="utf-8">UTF-8</option>
                        <option value="utf-16">UTF-16</option>
                        <option value="utf-32">UTF-32</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <label for="upload-model">Model</label>
                    </th>
                    <td>
                      <select id="upload-model" name="model">
<%
      putModelOptions(out);
%>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td><input id="treatWarningAsError" name="treatWarningAsError" type="checkbox" value="1" /><label title="Treat Warning as Error" for="treatWarningAsError">Treat Warning as Error</label></td>
                    <td><input id="upload-verbose" name="verbose" type="checkbox" value="1" /><label title="Verbose Output" for="upload-verbose">Verbose Output</label></td>
                  </tr>
                </table>
              </div>
            </fieldset><!-- invisible -->
	    <p class="submit_button"><input title="Submit for validation" type="submit" value="Check" /></p>
            <input type="hidden" name="quiet" value="1" />
          </form>
        </fieldset>
        <fieldset id="validate-by-input"  class="tabset_content front"><legend class="tabset_label">Validate by direct input</legend>
          <form method="post" enctype="multipart/form-data" action="check">
            <p class="instructions"><label title="Paste a complete (HTML) Document here" for="fragment">Enter the TTML content to validate</label>:<br />
              <textarea id="fragment" name="fragment" rows="12" cols="80"></textarea>
            </p>
            <fieldset id="extra_opt_direct" class="moreoptions">
              <legend class="toggletext"><a href="#validate_by_input+with_options"><img class="toggleicon" src="./images/arrow-closed.png" alt="Show" /> More Options</a></legend>
              <div class="options">
                <table>
                  <tr>
                    <th>
                      <label for="uri-model">Model</label>
                    </th>
                    <td>
                      <select id="uri-model" name="model">
<%
      putModelOptions(out);
%>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td>
                    <td><input id="treatWarningAsError" name="treatWarningAsError" type="checkbox" value="1" /><label title="Treat Warning as Error" for="treatWarningAsError">Treat Warning as Error</label></td>
                      <input id="direct-verbose" name="verbose" type="checkbox" value="1" /><label title="Verbose Output" for="direct-verbose">Verbose Output</label>
                    </td>
                  </tr>
                </table>
              </div>
            </fieldset>
            <p class="submit_button">
              <input title="Submit for validation" type="submit" value="Check" />
            </p>
            <input type="hidden" name="quiet" value="1" />
          </form>
        </fieldset>
      </div><!-- fields -->
    </div> <!-- frontforms -->
    <div class="intro">
      <p>
        This validator checks the syntactic and semantic validity of a Timed Text Markup Language (TTML) document according to one of the following
        specification models:
      </p>
      <ul id="models">
        <li><a href="http://www.w3.org/TR/2013/REC-ttml1-20130924/">TTML1 (2nd Edition)</a></li>
        <li><a href="https://www.smpte.org/sites/default/files/st2052-1-2010.pdf">SMPTE ST2052 2010</a></li>
        <li><a href="https://www.smpte.org/sites/default/files/ST2052-1-2013.pdf">SMPTE ST2052 2013</a></li>
      </ul>
    </div>
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
	and <a rel="Copyright" href="http://www.w3.org/Consortium/Legal/copyright-software">software licensing</a>
	rules apply. Your interactions with this site are in accordance
	with our <a href="http://www.w3.org/Consortium/Legal/privacy-statement#Public">public</a> and
	<a href="http://www.w3.org/Consortium/Legal/privacy-statement#Members">Member</a> privacy
	statements.
      </p>
    </div>
  </body>
</html>
