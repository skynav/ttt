<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="rpt" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rpt="http://skynav.com/ns/ttv/report">
  <xsl:output method="html" encoding="utf-8" indent="no"/>
  <xsl:template match="rpt:report">
    <h3 id="preparse_warnings">Notes and Potential Issues</h3>
    <p>The following notes and warnings highlight missing or conflicting information which caused the validator to perform some guesswork prior to validation,
       or other things affecting the output below. If the guess or fallback is incorrect, it could make validation results entirely incoherent. It is
      <em>highly recommended</em> to check these potential issues, and, if necessary, fix them and re-validate the document.
    </p>
    <ol id="warnings">
      <xsl:apply-templates/>
    </ol>
    <p class="backtop"><a href="#jumpbar">&#x2191; Top</a></p>
  </xsl:template>
  <xsl:template match="rpt:error"/>
  <xsl:template match="rpt:warning">
    <li class="msg_warn" id="W01">
      <p>
        <span class="err_type"><img src="images/info_icons/warning.png" alt="Warning" title="Warning" /></span>
        <xsl:apply-templates select="rpt:message"/>
      </p>
    </li>
  </xsl:template>
  <xsl:template match="rpt:info"/>
  <xsl:template match="rpt:debug"/>
  <xsl:template match="rpt:message">
    <xsl:apply-templates select="rpt:location"/>
    <xsl:apply-templates select="rpt:text"/>
  </xsl:template>
  <xsl:template match="rpt:location">
    <em>Line <xsl:value-of select=".//rpt:line"/>, Column <xsl:value-of select=".//rpt:column"/></em>:
  </xsl:template>
  <xsl:template match="rpt:url"/>
  <xsl:template match="rpt:line"/>
  <xsl:template match="rpt:column"/>
  <xsl:template match="rpt:text">
    <span class="msg"><xsl:value-of select="."/></span>
  </xsl:template>
  <xsl:template match="rpt:reference"/>
</xsl:stylesheet>
