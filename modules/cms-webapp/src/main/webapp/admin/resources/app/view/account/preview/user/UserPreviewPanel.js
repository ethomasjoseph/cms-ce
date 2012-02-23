Ext.define( 'Admin.view.account.preview.user.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',
    requires: [
        'Admin.view.account.preview.user.UserPreviewToolbar',
        'Admin.view.WizardPanel'
    ],

    dialogTitle: 'User Preview',

    autoWidth: true,
    autoScroll: true,

    cls: 'cms-user-preview-panel',
    width: undefined,

    showToolbar: true,

    initComponent: function()
    {
        this.fieldSets = [
            {
                title: 'Name',
                fields: [ 'prefix', 'firstName', 'middleName',
                    'lastName', 'suffix', 'initials', 'nickName']
            },
            {
                title: 'Personal Information',
                fields: ['personalId', 'memberId', 'organization', 'birthday',
                    'gender', 'title', 'description', 'htmlEmail', 'homepage']
            },
            {
                title: 'Settings',
                fields: ['timezone', 'locale', 'country', 'globalPosition']
            },
            {
                title: 'Communication',
                fields: ['phone', 'mobile', 'fax']
            }
        ];
        var profileData = this.generateProfileData( this.data );

        if ( this.showToolbar && this.data.isEditable)
        {
            this.tbar = {
                xtype:'userPreviewToolbar'
            };
        }

        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'column',
                    columns: 3
                },
                autoHeight: true,
                defaults: {
                    border: 0
                },
                items: [
                    {
                        width: 100,
                        itemId: 'previewPhoto',
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data,
                        margin: 5
                    },
                    {
                        columnWidth: .99,
                        cls: 'center',
                        xtype: 'panel',
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                autoHeight: true,
                                itemId: 'previewHeader',
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                itemId: 'previewTabs',
                                xtype: 'tabpanel',
                                defaults: {
                                    border: false
                                },
                                items: [
                                    {
                                        title: "Activities",
                                        html: 'Activities'
                                    },
                                    {
                                        title: "Profile",
                                        itemId: 'profileTab',
                                        tpl: Templates.account.userPreviewProfile,
                                        data: profileData
                                    },
                                    {
                                        title: "Places",
                                        itemId: 'placesTab',
                                        tpl: Templates.account.userPreviewPlaces,
                                        data: this.data
                                    },
                                    {
                                        title: "Memberships",
                                        itemId: 'membershipsTab',
                                        tpl: Templates.account.userPreviewMemberships,
                                        data: this.data
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        width: 300,
                        margin: 5,
                        itemId: 'previewInfo',
                        cls: 'east',
                        tpl: Templates.account.userPreviewCommonInfo,
                        data: this.data
                    }
                ]
            }
        ];
        this.callParent( arguments );
    },

    generateProfileData: function( userData )
    {
        var fieldSetEmpty = true;
        var profileData = [];
        if ( userData ) {
            Ext.Array.each( this.fieldSets, function( fieldSet )
            {
                var fieldSetData = { title: fieldSet.title};
                fieldSetData.fields = [];
                Ext.Array.each( fieldSet.fields, function( field )
                {
                    var value = userData[field] || (userData.userInfo ? userData.userInfo[field] : undefined);
                    var title = Admin.view.account.EditUserFormPanel.fieldLabels[field]
                            ? Admin.view.account.EditUserFormPanel.fieldLabels[field] : field
                    if ( value )
                    {
                        Ext.Array.include( fieldSetData.fields, {title: title, value: value} );
                        fieldSetEmpty = false;
                    }
                } );
                if ( !fieldSetEmpty )
                {
                    Ext.Array.include( profileData, fieldSetData );
                    fieldSetEmpty = true;
                }
            } );
        }
        return profileData;
    },

    isFieldsEnabled: function( fieldNames, userstoreName ) {
        var userstores = Ext.data.StoreManager.lookup( 'Admin.store.account.UserstoreConfigStore' );
        var userstore = userstores.findRecord( 'name', userstoreName );
        if ( userstore && userstore.raw.userFields ) {
            var fieldTypes = [].concat( fieldNames );
            for ( var i = 0; i < userstore.raw.userFields.length; i++ ) {
                for ( var j = 0; j < fieldTypes.length; j++ ) {
                    if ( userstore.raw.userFields[ i ].type == fieldTypes[ j ] ) return true;
                }
            }
        }
        return false;
    },

    setData: function( data ) {
        if ( data ) {
            this.data = data;

            var tabs = this.down( '#previewTabs' );

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            var profileTab = this.down( '#profileTab' );
            var profileFields = [];
            for ( var i = 0; i < this.fieldSets.length; i++ ) {
                profileFields = profileFields.concat( this.fieldSets[ i ].fields );
            }
            if ( this.isFieldsEnabled( profileFields, data.userStore ) ) {
                profileTab.update( this.generateProfileData( data ) );
                this.setTabVisible( tabs, profileTab, true );
            } else {
                this.setTabVisible( tabs, profileTab, false );
            }

            var membershipsTab = this.down( '#membershipsTab' );
            membershipsTab.update( data );

            var placesTab = this.down( '#placesTab' );
            if ( this.isFieldsEnabled( 'address', data.userStore ) ) {
                placesTab.update( data );
                this.setTabVisible( tabs, placesTab, true );
            } else {
                this.setTabVisible( tabs, placesTab, false );
            }
        }
    },

    setTabVisible: function( tabPanel, tabItem, visible ) {
        tabItem.tab.setVisible( visible );
        // activate first tab if the item being hidden is active
        var tabLayout = tabPanel.getLayout();
        if( !visible && tabLayout.getActiveItem() == tabItem ) {
            tabLayout.setActiveItem( 0 );
        }
    }



} );