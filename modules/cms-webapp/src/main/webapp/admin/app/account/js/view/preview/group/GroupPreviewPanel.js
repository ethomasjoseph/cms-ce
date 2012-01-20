Ext.define( 'App.view.preview.group.GroupPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupPreviewPanel',

    requires: [
        'App.view.preview.group.GroupPreviewToolbar',
        'Common.WizardPanel'
    ],

    autoWidth: true,
    autoScroll: true,

    cls: 'cms-user-preview-panel',
    width: undefined,

    showToolbar: true,

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'column',
                    columns: 3
                },
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
                                height: 70,
                                itemId: 'previewHeader',
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                cls: 'center',
                                xtype: 'tabpanel',
                                items: [
                                    {
                                        title: "Memberships",
                                        itemId: 'membershipsTab',
                                        tpl: Templates.account.previewMemberships,
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
                        tpl: Templates.account.groupPreviewCommonInfo,
                        data: this.data
                    }
                ]
            }
        ];

        if ( this.showToolbar && this.data.isEditable)
        {
            this.tbar = {
                xtype:'groupPreviewToolbar',
                editable: this.data.isEditable
            };
        }

        this.callParent( arguments );
    },


    setData: function( data ) {
        if ( data ) {
            this.data = data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#membershipsTab' );
            previewInfo.update( data );
        }
    }

} );