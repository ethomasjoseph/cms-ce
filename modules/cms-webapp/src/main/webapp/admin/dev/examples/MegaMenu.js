Ext.define( 'Common.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',
    cls: 'cms-mega-menu',
    styleHtmlContent: true,

     /*
    items: [{
        xtype: 'container',
        itemId: 'menuView',
        cls: 'cms-mega-menu-view',
        autoHeight: true,
        maxWidth: 300,
        layout: {
            type: 'auto',
            bindToOwnerCtComponent: true
        },
        listeners: {
            afterrender: function( view ) {
                console.log( 'view afterrender' );
                Ext.defer( function() {
                    var h = view.getEl().down( 'ul' ).getHeight();
                    console.log('h=' +h);
                    view.up( 'menu' ).setHeight( h );
                }, 10 );
            }
        },
        styleHtmlContent: true,

        tpl: new Ext.XTemplate(

            '<ul><tpl for="menu.items"><li class="cms-mega-menu-section">',
                '<tpl if="text">',
                    '<h2>{text}</h2>',
                '</tpl>',
                '<tpl if="menu">',
                    '<ul><tpl for="menu.items">',
                        '<li class="cms-mega-menu-item">',
                            '<a class="x-menu-item-link {[ !(values.icon || values.iconCls) ? \"no-icon\" : \"\" ]}" href="#" hidefocus="true" unselectable="on">',
                                '<tpl if="icon || iconCls">',
                                    '<img src="{icon}" class="x-menu-item-icon {iconCls}">',
                                '</tpl>',
                                '<span class="x-menu-item-text">{text}</span>',
                            '</a>',
                        '</li>',
                    '</tpl></ul>',
                    '<div style="height: 0; line-height: 0; clear: both;"/>',
                '</tpl>',
            '</li></tpl></ul>'

        )
    }],
    */

    listeners: {
       click: {
            fn: function(menu, item, e, eOpts ) {
                console.log('item', item);
            }
        }
    },

    loader: {
        url: 'mega-menu.data',
        autoLoad: true,
        renderer: function(loader, response, active) {
            var menu = loader.getTarget();
            var data = Ext.decode( response.responseText );

            for ( var i = 0; i < data.menu.items.length; i++ ) {

                var section = data.menu.items[i];
                var sectionItems = [];

                var sectionChildren = [];
                if (section.menu) {
                    for ( var j = 0; j < section.menu.items.length; j++ ) {
                        var item = section.menu.items[j];
                        sectionChildren.push({
                            xtype: 'menuitem',
                            text: item.text,
                            iconCls: item.iconCls,
                            icon: item.icon
                        });
                    }
                }

                if( section.text ) {
                    sectionItems.push({
                            xtype: 'container',
                            html: '<h2>' + section.text + '</h2>'
                         });
                }
                if( sectionChildren.length > 0 ) {
                    sectionItems.push({
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                bindToOwnerCtComponent: true,
                                autoSize: true
                            },
                            items: sectionChildren
                         });
                }

                menu.add(sectionItems);
            }
            //menu.down( '#menuView' ).update( data );
            console.log( 'updated menu' );
            return true;
        }
    }


/*     initComponent: function() {

         this.callParent( arguments );

         var view = this.down( '#menuView' );
         var menu = this;

     }*/



} );
