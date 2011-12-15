Ext.define( 'Common.view.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',

    requires: [ 'Common.MegaKeyNav'],

    bodyCls: 'cms-mega-menu',
    plain: true,
    showSeparator: false,
    styleHtmlContent: true,

    maxColumns: 4,
    tableAttrs: undefined,
    trAttrs: undefined,
    tdAttrs: undefined,
    url: undefined,

    loader: {
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
                            sectionMenu.push(Ext.apply({
                                xtype: 'menuitem',
                                cls: 'cms-mega-menu-item medium'
                            }, item));
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
                                    tableAttrs: menu.tableAttrs,
                                    trAttrs: menu.trAttrs,
                                    tdAttrs: menu.tdAttrs,
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
        Ext.apply( this.loader, {
            url: this.url
        } );

        this.callParent( arguments );

    },

    afterRender: function ( ct ) {
        var me = this;
        this.callParent( arguments );
        if( this.keyNav )
            this.keyNav.destroy();
        this.keyNav = new Ext.create( 'Common.MegaKeyNav', me );
        this.createItemMap();
    },

    getAllItems: function() {
        var result = new Ext.util.MixedCollection();
        for ( var i = 0; i < this.items.items.length; i++ ) {
            var container = this.items.items[i];
            result.addAll( container.items.items );
        }
        return result;
    },

    createItemMap: function() {
        var result = new Array();
        var line = 0;
        for ( var i = 0; i < this.items.items.length; i++ ) {
            var container = this.items.items[i];
            if( container.items.length > 0 ) {
                var col = 0;
                result[line] = new Array();
                for ( var j = 0; j < container.items.items.length; j++ ) {
                    var obj = container.items.items[j];
                    if( col >= this.maxColumns ) {
                        line++;
                        col = 0;
                        result[line] = new Array();
                    }
                    result[line][col] = obj;
                    col++;
                }
                line++;
            }
        }
        this.itemMap = result;
    },

    getItemPosition: function( item ) {
        for ( var i = 0; i < this.itemMap.length; i++ ) {
            for ( var j = 0; j < this.itemMap[i].length; j++ ) {
                if( this.itemMap[i][j] == item )
                    return [j, i];
            }
        }
    },

    getItemAbove: function( item ) {
        return this.getItemNear( item, -1, 0 );
    },

    getItemBelow: function( item ) {
        return this.getItemNear( item, 1, 0 );
    },

    getItemLeft: function( item ) {
        return this.getItemNear( item, 0, -1 );
    },

    getItemRight: function( item ) {
        return this.getItemNear( item, 0, 1 );
    },

    getItemNear: function( item, ver, hor ) {
        var xy = this.getItemPosition( item );
        var x,y;
        if ( xy ) {
            x = xy[0] + hor;
            y = xy[1] + ver;
        } else {
            // set the first item selected by default
            x = 0;
            y = 0;
        }

        // handle y edges
        var yLength = this.itemMap.length;
        if ( y < 0 ) {
            y += yLength;
        } else if ( y >= yLength ) {
            y -= yLength;
        }
        // handle x edges, should be done aftery because we need to know the row first
        var xLength = this.itemMap[y].length;
        if ( x < 0 ) {
            x += xLength;
        } else if ( x >= xLength ) {
            if( !hor || hor == 0 ) {
                // came from the row with more items, so set to the last
                x = xLength - 1;
            } else {
                // went out of right horizontal limit, so start over
                x -= xLength;
            }
        }

        return this.itemMap[y][x];
    },

    getItemFromEvent: function( e ) {
        var firstLevelChild = this.getChildByElement( e.getTarget() );
        if ( firstLevelChild && firstLevelChild.getXTypes().indexOf( 'menuitem' ) < 0 ) {
             firstLevelChild = firstLevelChild.getChildByElement( e.getTarget() );
        }
        return firstLevelChild;
    }

} );
