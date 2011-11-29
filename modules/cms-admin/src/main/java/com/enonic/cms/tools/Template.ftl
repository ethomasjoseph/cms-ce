/**
 * This file is auto-generated. Do not modify.
 * Date: ${timestamp?string("yyyy-MM-dd HH:mm:ss zzz")}
**/

if ( !Templates )
{
    var Templates = {};
}

Templates.${templateNamespace} = {

<#list templateList as tpl>
    ${tpl.name}:
${tpl.text}<#if tpl_has_next>,</#if>
    
</#list>
};
