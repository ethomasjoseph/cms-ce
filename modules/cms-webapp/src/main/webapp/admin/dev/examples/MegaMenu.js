Ext.define( 'Common.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',
    width: 600,
    height: 300,
    plain: true,
    margin: '0 0 10 0',
    bodyCls: 'cms-mega-menu',
    floating: true,

    items: new Ext.Component(
        {
            style: {
                width: '100%'
            },

            // TODO: For testing purposes at the moment. We may use a DataView here.
            tpl: Ext.create('Ext.XTemplate', '' +
                    '<table>' +
                    '<tr>' +
                    '<th colspan="50">' +
                    'Header' +
                    '</th>' +
                    '</tr>' +
                    '<tr>' +
                    '<td><a class="first-item" href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '</tr>' +
                    '<tr>' +
                    '<th colspan="50">' +
                    'Header' +
                    '</th>' +
                    '</tr>' +
                    '<tr>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '</tr>' +
                    '<tr>' +
                    '<th colspan="50">' +
                    'Header' +
                    '</th>' +
                    '</tr>' +
                    '<tr>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '<td><a href="">Menu Item</a></td>' +
                    '</tr>' +
                    '</table>'

            ),

            initComponent: function() {
                var me = this;

                me.update({ });

                Ext.EventManager.addListener(document, 'keydown', function(event, target, o) {

                }, me);

                me.callParent( arguments );
            }
        }
    )
} );
