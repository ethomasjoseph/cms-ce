Ext.define( 'App.view.preview.user.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',
    requires: [
        'App.view.preview.user.UserPreviewToolbar',
        'Common.WizardPanel'
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
                fields: [ 'prefix', 'first-name', 'middle-name',
                    'last-name', 'suffix', 'initials', 'nick-name']
            },
            {
                title: 'Personal Information',
                fields: ['personal-id', 'member-id', 'organization', 'birthday',
                    'gender', 'title', 'description', 'html-email', 'homepage']
            },
            {
                title: 'Settings',
                fields: ['timezone', 'locale', 'country', 'global-position']
            },
            {
                title: 'Communication',
                fields: ['phone', 'mobile', 'fax']
            }
        ];
        var profileData = this.generateProfileData( this.data );

        if ( this.showToolbar )
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
                        columnWidth: 1,
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
                                        html: 'Places'
                                    },
                                    {
                                        title: "Memberships",
                                        html: 'Memberships'
                                    },
                                    {
                                        title: "Advanced",
                                        html: 'Advanced'
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
                    var title = App.view.EditUserFormPanel.fieldLabels[field]
                            ? App.view.EditUserFormPanel.fieldLabels[field] : field
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

    setData: function( data ) {
        if ( data ) {
            this.data = data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            var profileTab = this.down( '#profileTab' );
            profileTab.update( this.generateProfileData( data ) );
        }
    }



} );