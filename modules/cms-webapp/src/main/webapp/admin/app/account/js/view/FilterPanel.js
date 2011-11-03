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
                    itemId: 'filterButton',
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
                    xtype: 'checkboxgroup',
                    itemId: 'accountTypeOptions',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'type',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'cms-cursor-clickable',
                        width: 170
                    },

                    items: [
                        {
                            itemId: 'searchFilterUsers',
                            boxLabel: 'Users',
                            inputValue: 'users',
                            checked: false
                        },
                        {
                            itemId: 'searchFilterGroups',
                            boxLabel: 'Groups',
                            inputValue: 'groups',
                            checked: false
                        }
                    ]
                },
                {
                    xtype: 'label',
                    text: '',
                    height: 10
                },
                {
                    xtype: 'label',
                    text: 'Userstore',
                    cls: 'facet-header'
                },
                {
                    xtype: 'checkboxgroup',
                    itemId: 'userstoreOptions',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'userStoreKey',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'cms-cursor-clickable',
                        width: 170
                    },

                    items: [
                    ]
                },
                {
                    xtype: 'label',
                    text: '',
                    height: 10
                },
                {
                    xtype: 'label',
                    text: 'Organization',
                    cls: 'facet-header'
                },
                {
                    xtype: 'checkboxgroup',
                    itemId: 'organizationOptions',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'organizations',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        overCls: 'cms-cursor-clickable',
                        width: 170
                    },

                    items: [
                    ]
                }
            ]
        };

 		Ext.apply(this, filter);
        Ext.tip.QuickTipManager.init();

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
            } else if (facet.name === 'organization') {
                this.showOrganizationFacets(facet);
            }
        }
    },

    removeAllOrgCheckboxes: function() {
        var organizationCheckGroup = this.query( '#organizationOptions' )[0];

        var cbList = [];
        Ext.Array.each(organizationCheckGroup.items.items, function(cb) {
            if (cb) cbList.push(cb);
        });
        for (var i = 0; i < cbList.length; i++) {
            cbList[i].destroy();
        }
    },

    showOrganizationFacets: function(facet) {
        var MAX_ORG_FACET_ITEMS = 10;
        var MAX_ORG_LABEL_CHARS = 20;
        var organizationCheckGroup = this.query( '#organizationOptions' )[0];
        var checked = organizationCheckGroup.getValue();
        var selectedCheck = {};

        Ext.Object.each(checked, function(key, val) {
            selectedCheck[val] = true;
        });

        this.removeAllOrgCheckboxes();

        var terms = facet.terms;
        var itemId, checkbox, label, tooltip;
        var orgList = [];
        for (var organization in terms) {
            orgList.push({name: organization, hits: terms[organization]});
        }

        orgList.sort(function (o1, o2) {
            if ((selectedCheck[o1.name]) && (selectedCheck[o2.name])) {
                return o2.hits - o1.hits;
            } else if (selectedCheck[o1.name]) {
                return -1;
            } else if (selectedCheck[o2.name]) {
                return 1;
            }
            return o2.hits - o1.hits;
        });

        var total = 0;
        var checksToShow = 0;
        var checkSelected;
        Ext.Array.each(orgList, function(org) {
            total++;
            if (total <= MAX_ORG_FACET_ITEMS) {
                checkSelected = selectedCheck[org.name];
                if (checkSelected || (org.hits > 0)) {
                    itemId = org.name + '_org_checkbox';
                    label = Ext.String.ellipsis(org.name, MAX_ORG_LABEL_CHARS) + ' ('+org.hits+')';
                    var cb = new Ext.form.Checkbox( { itemId: itemId, boxLabel: label, inputValue: org.name, checked: checkSelected, checkedCls: 'x-form-cb-checked facet-selected'} );
                    checkbox = organizationCheckGroup.add(cb);

                    if (org.name.length > MAX_ORG_LABEL_CHARS) {
                        tooltip = org.name + ' ('+org.hits+')';
                           Ext.tip.QuickTipManager.register({
                            target: cb.id,
                            text: tooltip
                        });
                    }

                    checksToShow++;
                }
            } else if (org.hits > 0) {
                checksToShow++;
            }
        });

        if (checksToShow > MAX_ORG_FACET_ITEMS) {
            var text = '... ' + (checksToShow - MAX_ORG_FACET_ITEMS) + ' more';
            var moreLabel = {html: '<a href="javascript:;" class="showMoreOrg">'+text+'</a>', border: false};
            organizationCheckGroup.add(moreLabel);
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
                checkbox.setVisible(checkbox.getValue() || count > 0);
                userstore = (userstore === '_Global')? 'Global' : userstore;
                checkbox.el.down('label').update(userstore + ' (' + count + ')');
            }
        }
    },

    showUserTypeFacets: function(facet) {
        var userCount = facet.terms.user;
        var groupCount = facet.terms.group;

        var usersButton = Ext.ComponentQuery.query( '*[itemId=searchFilterUsers]' )[0];
        usersButton.el.down('label').update('Users (' + userCount + ')');
        usersButton.setVisible(usersButton.getValue() || userCount > 0);

        var groupsButton = Ext.ComponentQuery.query( '*[itemId=searchFilterGroups]' )[0];
        groupsButton.el.down('label').update('Groups (' + groupCount + ')');
        groupsButton.setVisible(groupsButton.getValue() || groupCount > 0);
    },

    setUserStores: function(userstores) {
        var userstoreRadioGroup = Ext.ComponentQuery.query( '*[itemId=userstoreOptions]' )[0];
        userstoreRadioGroup.removeAll();

        // global userstore (global groups, built-in users)
        userstoreRadioGroup.add({ itemId: '_Global_checkbox', boxLabel: 'Global', inputValue: '_Global', checked: false });
        
        for (var i = 0; i < userstores.length; i++) {
            var userstore = userstores[i];
            var itemId = this.userstoreCheckboxId(userstore);
            userstoreRadioGroup.add({ itemId: itemId, boxLabel: userstore, inputValue: userstore, checked: false });
        }
    },

    userstoreCheckboxId: function(userstoreName) {
        return userstoreName + '_checkbox';
    },

    clearFilter: function() {
        this.setTitle( 'Filter' );

        var userstoreCheckboxes = Ext.ComponentQuery.query( '[itemId=userstoreOptions] * , [itemId=accountTypeOptions] *, [itemId=organizationOptions] *' );
        Ext.Array.each(userstoreCheckboxes, function(checkbox) {
            checkbox.suspendEvents();
            checkbox.setValue(false);
            checkbox.show();
            checkbox.resumeEvents();
        });

        var filterTextField = this.query('#filter')[0];
        filterTextField.suspendEvents();
        filterTextField.reset();
        filterTextField.resumeEvents();

        var filterButton = this.query('#filterButton')[0];
        filterButton.fireEvent('click');
    },

    updateTitle: function()
    {
        var title = "Filter   (<a href='javascript:;' class='clearSelection'>Clear filter</a>)";
        this.setTitle( title );

        var clearSel = this.header.el.down( 'a.clearSelection' );
        if ( clearSel )
        {
            var filterPanel = this;
            clearSel.on( "click", function() {
                filterPanel.clearFilter();
            }, this );
        }
    }

});
