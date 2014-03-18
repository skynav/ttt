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
      <xsl:apply-templates/>
      <!--
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
    <xsl:apply-templates select="rpt:source"/>
  </xsl:template>
  <xsl:template match="rpt:location">
    <em>Line <xsl:value-of select=".//rpt:row"/>, Column <xsl:value-of select=".//rpt:col"/></em>:
  </xsl:template>
  <xsl:template match="rpt:url"/>
  <xsl:template match="rpt:row"/>
  <xsl:template match="rpt:col"/>
  <xsl:template match="rpt:text">
    <span class="msg"><xsl:value-of select="."/></span>
  </xsl:template>
  <xsl:template match="rpt:reference"/>
  <xsl:template match="rpt:source">
    <table class="source">
      <tr>
        <th class="line">Line</th>
        <th class="source">Source</th>
      </tr>
      <xsl:apply-templates select="rpt:line"/>
    </table>
  </xsl:template>
  <xsl:template match="rpt:line">
    <tr>
      <td class="line"><xsl:value-of select="./@row"/></td>
      <td class="source"><code class="input"><xsl:apply-templates/></code></td>
    </tr>
  </xsl:template>
  <xsl:template match="rpt:unmarked"><xsl:value-of select="."/></xsl:template>
  <xsl:template match="rpt:marked"><strong title="Position where error was detected."><xsl:value-of select="."/></strong></xsl:template>
</xsl:stylesheet>
