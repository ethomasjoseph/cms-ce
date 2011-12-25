<?xml version="1.0" encoding="UTF-8"?>

    <!--

    How to use:

    1. Include this file in the XSL document

      <xsl:include href="common/codearea.xsl"/>

    2. Embed the following JavaScripts in the XSL document

      <script type="text/javascript" src="ace/src/ace.js" charset="utf-8">//</script>
      <script type="text/javascript" src="ace/src/mode-xml.js" charset="utf-8">//</script>
      <script type="text/javascript" src="javascript/codearea.js" charset="utf-8">//</script>

    3. Call the codearea template

      <xsl:call-template name="codearea">
        <xsl:with-param name="name" select="'module'"/>
        <xsl:with-param name="label" select="'Label'"/>
        <xsl:with-param name="selectnode" select="/path/to/content"/>
        <xsl:with-param name="mode" select="'xml'"/>
      </xsl:call-template>

        @param: name    - String    required    name of the textarea that is submitted to the server
        @param: label   - String    required    text label for the codearea
        @selectnode     - XPath     optional    X-path to the content that should initially populate the code area.
        @mode           - String    optional    Mode/syntax parser. Default is XML
        @readonly       - Boolean   optional    Read only


    Modes:

      To change mode (syntax parser), change the mode-{$mode} in the second javascript src and set mode to $mode in the template call.

      Supported modes are:

         java
         xml (default)
         html
         javascript
         css
         json
    -->


<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="codearea">
    <xsl:param name="name" select="''"/>
    <xsl:param name="label" select="''"/>
    <!-- TODO: Required should be boolean! -->
    <xsl:param name="required" select="''"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="width" select="'100%'"/>
    <xsl:param name="height" select="'500px'"/>
    <xsl:param name="mode" select="'xml'"/>
    <xsl:param name="readonly" select="false()"/>

    <xsl:variable name="idForTextArea" select="concat('cms_codeArea_textArea_',$name)" />
    <xsl:variable name="idForPreElement" select="concat('cms_codeArea_',$name)" />

    <xsl:if test="string-length($label) &gt; 0">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
      </xsl:call-template>
    </xsl:if>

    <td valign="top">
      <div class="codearea" style="position:relative; background-color:#fff; width: {$width}; height: {$height}; padding:0">

        <!-- Editor content store. This will be populated on form submit -->
        <textarea style="display: none">
          <xsl:attribute name="name">
            <xsl:value-of select="$name"/>
          </xsl:attribute>
          <xsl:attribute name="id">
            <xsl:value-of select="$idForTextArea"/>
          </xsl:attribute>
          <xsl:value-of select="$selectnode"/>
        </textarea>

        <!-- Editor display -->
        <pre id="{$idForPreElement}" style="position: absolute; margin: 0; right:0; width: {$width}; height: {$height};"><xsl:value-of select="$selectnode"/></pre>

        <script type="text/javascript">
          cms.CodeArea.create({
            id: '<xsl:value-of select="$name"/>',
            required: '<xsl:value-of select="$required"/>',
            mode: '<xsl:value-of select="$mode"/>',
            readonly: <xsl:value-of select="$readonly"/>,
          });

        </script>
      </div>
    </td>
  </xsl:template>
</xsl:stylesheet>