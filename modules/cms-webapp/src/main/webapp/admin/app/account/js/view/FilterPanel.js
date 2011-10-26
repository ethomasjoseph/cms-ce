Ext.define('App.view.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accountFilter',
    cls: 'facet-navigation',

    title: 'Filter',
    split: true,
    collapsible: true,

    initComponent: function() {
        var search = {
            xtype: 'fieldcontainer',
            layout: 'hbox',

            items: [
                {
                    xtype: 'textfield',
                    enableKeyEvents: true,
                    bubbleEvents: ['specialkey'],
                    id: 'filter',
                    name: 'filter',
                    flex: 1
                },
                {
                    xtype: 'button',
                    iconCls: 'icon-find',
                    action: 'search',
                    margins: '0 0 0 5'
                }
            ]
        };

        var filter = {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: true,
            bodyPadding: 10,

            defaults: {
                margins: '0 0 0 0'
            },

            items: [
                search,
                {
                    xtype: 'label',
                    text: 'Type',
                    cls: 'facet-header'
                },

                {
                    xtype: 'radiogroup',
                    itemId: 'typeRadios',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'type',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        width: 170
                    },

                    items: [
                        { itemId: 'allFilterCheckbox', boxLabel: 'All', inputValue: 'all', checked: true },
                        { itemId: 'usersFilterCheckbox', boxLabel: 'Users', inputValue: 'users'},
                        { itemId: 'groupsFilterCheckbox', boxLabel: 'Groups', inputValue: 'groups'}
                    ]
                },
                {
                    xtype: 'label',
                    text: 'Userstore',
                    cls: 'facet-header'
                },
                {
                    xtype: 'checkboxgroup',
                    itemId: 'userstoreRadios',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'userStoreKey',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        width: 170
                    },

                    items: [
                    ]
                }
            ]
        };

 		Ext.apply(this, filter);

        this.callParent(arguments);
    },

    showFacets: function(facets) {
        var facet;
        for (var i = 0; i < facets.length; i++) {
            facet = facets[i];
            if (facet.name === 'userstore') {
                this.showUserstoreFacets(facet);
            } else if (facet.name === 'type') {
                this.showUserTypeFacets(facet);
            }
        }
    },

    showUserstoreFacets: function(facet) {
        var terms = facet.terms;
        var itemId, checkbox, count;
        for (var userstore in terms) {
            itemId = this.userstoreCheckboxId(userstore);
            checkbox = Ext.ComponentQuery.query( '*[itemId='+itemId+']' );
            if (checkbox.length > 0) {
                checkbox = checkbox[0];
                count = terms[userstore];
                userstore = (userstore === '_Global')? 'Global' : userstore;
                checkbox.el.down('label').update(userstore + ' (' + count + ')');
            }
        }
    },

    showUserTypeFacets: function(facet) {
        var userCount = facet.terms.user;
        var groupCount = facet.terms.group;
        var usersRadioButton = Ext.ComponentQuery.query( '*[itemId=usersFilterCheckbox]' )[0];
        var groupsRadioButton = Ext.ComponentQuery.query( '*[itemId=groupsFilterCheckbox]' )[0];
        var allRadioButton = Ext.ComponentQuery.query( '*[itemId=allFilterCheckbox]' )[0];
        usersRadioButton.el.down('label').update('Users (' + userCount + ')');
        groupsRadioButton.el.down('label').update('Groups (' + groupCount + ')');
        allRadioButton.el.down('label').update('All (' + (userCount+groupCount) + ')');
    },

    setUserStores: function(userstores) {
        var userstoreRadioGroup = Ext.ComponentQuery.query( '*[itemId=userstoreRadios]' )[0];
        userstoreRadioGroup.removeAll();

        // global userstore (global groups, built-in users)
        userstoreRadioGroup.add({ itemId: '_Global_checkbox', boxLabel: 'Global', inputValue: '_Global', checked: true });
        
        for (var i = 0; i < userstores.length; i++) {
            var userstore = userstores[i];
            var itemId = this.userstoreCheckboxId(userstore);
            userstoreRadioGroup.add({ itemId: itemId, boxLabel: userstore, inputValue: userstore, checked: true });
        }
    },

    userstoreCheckboxId: function(userstoreName) {
        return userstoreName + '_checkbox';
    }

});
