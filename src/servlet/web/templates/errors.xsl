<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="rpt" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rpt="http://skynav.com/ns/ttv/report">
  <xsl:output method="html" encoding="utf-8" indent="no"/>
  <xsl:template match="rpt:report">
    <div id="result">
      <h3 class="invalid">Validation Output: <xsl:value-of select="count(//rpt:error)"/></h3>
      <ol id="error_loop">
        <xsl:apply-templates/>
      </ol>
      <p class="backtop"><a href="#jumpbar">&#x2191; Top</a></p>
    </div>
  </xsl:template>
  <xsl:template match="rpt:error">
    <li class="msg_err">
      <span class="err_type"><img src="images/info_icons/error.png" alt="Error" title="Error" /></span>
      <xsl:apply-templates select="rpt:message"/>
      <!--
      <pre><code class="input">...<strong title="Position where error was detected.">here</strong>...</code></pre>
      <p class="helpwanted">
        <a href="feedback.html?uri=;errmsg_id=libxml2-201#errormsg" title="Suggest improvements on this error message through our feedback channels">&#x2709;</a>
      </p>
      -->
    </li>
  </xsl:template>
  <xsl:template match="rpt:warning"/>
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
