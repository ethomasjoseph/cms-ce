Ext.define('Common.MegaKeyNav', {
    extend: 'Ext.util.KeyNav',

    requires: ['Ext.FocusManager'],

    constructor: function(menu) {
        var me = this;

        me.menu = menu;
        me.callParent([menu.el, {
            down: me.down,
            enter: me.enter,
            esc: me.escape,
            left: me.left,
            right: me.right,
            space: me.enter,
            tab: me.tab,
            up: me.up
        }]);
    },

    down: function(e) {
        var me = this,
            fi = me.menu.focusedItem;

        if (fi && e.getKey() == Ext.EventObject.DOWN && me.isWhitelisted(fi)) {
            return true;
        }
        var ni = me.menu.getItemBelow( fi );
        if (me.menu.canActivateItem( ni )) {
            me.menu.setActiveItem( ni );
        }
    },

    enter: function(e) {
        var menu = this.menu,
            focused = menu.focusedItem;

        if (menu.activeItem) {
            menu.onClick(e);
        } else if (focused && focused.isFormField) {
            // prevent stopEvent being called
            return true;
        }
    },

    escape: function(e) {
        Ext.menu.Manager.hideAll();
    },

    focusNextItem: function(step) {
        var menu = this.menu,
            items = menu.getAllItems(),
            focusedItem = menu.focusedItem,
            startIdx = focusedItem ? items.indexOf(focusedItem) : -1,
            idx = startIdx + step;

        while (idx != startIdx) {
            if (idx < 0) {
                idx = items.length - 1;
            } else if (idx >= items.length) {
                idx = 0;
            }

            var item = items.getAt(idx);
            if (menu.canActivateItem(item)) {
                menu.setActiveItem(item);
                break;
            }
            idx += step;
        }
    },

    isWhitelisted: function(item) {
        return Ext.FocusManager.isWhitelisted(item);
    },

    left: function(e) {
        var menu = this.menu,
            fi = menu.focusedItem,
            ai = menu.activeItem;

        if (fi && this.isWhitelisted(fi)) {
            return true;
        }

        var ni = menu.getItemLeft( fi );
        if (menu.canActivateItem( ni )) {
            menu.setActiveItem( ni );
        }
    },

    right: function(e) {
        var menu = this.menu,
            fi = menu.focusedItem,
            ai = menu.activeItem,
            am;

        if (fi && this.isWhitelisted(fi)) {
            return true;
        }

        var ni = menu.getItemRight( fi );
        if (menu.canActivateItem( ni )) {
            menu.setActiveItem( ni );
        }
    },

    tab: function(e) {
        var me = this;

        if (e.shiftKey) {
            me.up(e);
        } else {
            me.down(e);
        }
    },

    up: function(e) {
        var me = this,
            fi = me.menu.focusedItem;

        if (fi && e.getKey() == Ext.EventObject.UP && me.isWhitelisted(fi)) {
            return true;
        }
        var ni = me.menu.getItemAbove( fi );
        if (me.menu.canActivateItem( ni )) {
            me.menu.setActiveItem( ni );
        }
    }
});