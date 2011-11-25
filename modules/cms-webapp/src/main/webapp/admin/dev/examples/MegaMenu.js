Ext.define( 'Common.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',
    plain: true,
    padding: 10,
    bodyCls: 'cms-mega-menu',
    loader: {
        url: 'mega-menu.data',
        params: {
            userId: 1
        },
        tpl: '{id} - {text}',
        renderer: function(loader, response, active) {
            alert( response );
            var text = response.responseText;
            loader.getTarget().update('The response is ' + text);
            return true;
        }
    }



} );
