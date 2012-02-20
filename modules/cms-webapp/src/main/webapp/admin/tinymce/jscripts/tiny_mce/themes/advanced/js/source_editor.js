tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

var aceInstance;

function onLoadInit()
{
    tinyMCEPopup.resizeToInnerSize();

    document.getElementById('htmlSource').innerHTML = formatContent(tinyMCEPopup.editor.getContent());

    aceInstance = ace.edit( 'htmlSource' );
    var XmlMode = require( "ace/mode/xml" ).Mode;
    aceInstance.getSession().setMode( new XmlMode() );
}

function formatContent( content )
{
    var c = content;

    // P
    c = c.replace(/(<p(?:\s+[^>]*)?>)/gim, '$1\n\t');
    c = c.replace(/<\/p>/gim, '\n</p>');

    // H1-6
    c = c.replace(/(<h[1-6].*?>)/gim, '$1\n\t');
    c = c.replace(/(<\/h[1-6].*?>)/gim, '\n$1');
    c = c.replace(/>/gim, '&gt;');
    c = c.replace(/</gim, '&lt;');

    return c;
}

function saveContent()
{
    tinyMCEPopup.editor.setContent(aceInstance.getSession().getValue());
    tinyMCEPopup.close();
}