Ext.define( 'Common.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',

    cls: 'cmsmegamenu',
    bodyCls: 'cms-mega-menu',
    plain: true,
    showSeparator: false,
    styleHtmlContent: true,
    bodyPadding: 10,

    maxColumns: 5,

    requires: [ 'Common.MegaKeyNav'],

    loader: {
        url: 'mega-menu.data',
        autoLoad: true,
        renderer: function(loader, response, active) {
            var menu = loader.getTarget();
            var data = Ext.decode( response.responseText );
            if ( data && data.menu ) {

                for ( var i = 0; i < data.menu.items.length; i++ ) {

                    var section = data.menu.items[i];
                    var sectionItems = [];
                    var sectionMenu = [];

                    if(section.menu) {
                        for ( var j = 0; j < section.menu.items.length; j++ ) {
                            var item = section.menu.items[j];
                            sectionMenu.push({
                                xtype: 'menuitem',
                                cls: 'cms-mega-menu-item',
                                text: item.text,
                                iconCls: item.iconCls,
                                icon: item.icon,
                                handler: menu.onMegaMenuItemClick
                            });
                        }
                    }

                    if( section.text ) {
                        sectionItems.push({
                                xtype: 'container',
                                cls: 'cms-mega-menu-header',
                                html: '<h2>' + section.text + '</h2>'
                             });
                    }

                    if( sectionMenu.length > 0 ) {
                        sectionItems.push({
                                xtype: 'container',
                                cls: 'cms-mega-menu-section',
                                layout: {
                                    type: 'table',
                                    columns: menu.maxColumns,
                                    bindToOwnerCtComponent: true,
                                    autoSize: true
                                },
                                items: sectionMenu
                             });
                    }

                    menu.add(sectionItems);
                }

            }
            return true;
        }
    },

    initComponent: function() {

        this.callParent( arguments );

    },

    afterRender: function ( ct ) {
        var me = this;
        this.callParent( arguments );
        if( this.keyNav )
            this.keyNav.destroy();
        this.keyNav = new Ext.create( 'Common.MegaKeyNav', me );
    },

    getAllItems: function() {
        var result = new Ext.util.MixedCollection();
        for ( var i = 0; i < this.items.items.length; i++ ) {
            var container = this.items.items[i];
            result.addAll( container.items.items );
        }
        return result;
    },

    getItemAbove: function( item ) {

    },

    getItemBelow: function( item ) {
        var container = item.up( 'container' );
        var rowCount = Math.ceil( container.items.items.length / this.maxColumns );
        var columnCount = this.maxColumns;
        var currentIdx = container.items.indexOf( item );
        var currentRow = Math.floor( currentIdx / this.maxColumns );
        var currentColumn = currentIdx % this.maxColumns;
        if( rowCount > (currentRow + 1) ) {

        }
    },

    getItemLeft: function( item ) {

    },

    getItemRight: function( item ) {

    },

    getItemFromEvent: function( e ) {
        var firstLevelChild = this.getChildByElement( e.getTarget() );
        if ( firstLevelChild && firstLevelChild.getXTypes().indexOf( 'menuitem' ) < 0 ) {
             firstLevelChild = firstLevelChild.getChildByElement( e.getTarget() );
        }
        return firstLevelChild;
    },

    setActiveItem: function(item) {
        var me = this;

        if (item && (item != me.activeItem && item != me.focusedItem)) {
            me.deactivateActiveItem();
            if (me.canActivateItem(item)) {
                if (item.activate) {
                    item.activate();
                    if (item.activated) {
                        me.activeItem = item;
                        me.focusedItem = item;
                        me.focus();
                    }
                } else {
                    item.focus();
                    me.focusedItem = item;
                }
            }
            item.el.scrollIntoView(me.layout.getRenderTarget());
        }
    },

    onMegaMenuItemClick: function( item, event ) {
        console.log( 'click item ' + (item ? item.text : 'undefined'), item );
    }

} );
