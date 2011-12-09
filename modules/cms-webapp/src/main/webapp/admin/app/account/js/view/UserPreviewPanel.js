Ext.define( 'App.view.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',


    requires: ['App.view.UserPreviewToolbar', 'Common.WizardPanel'],

    dialogTitle: 'User Preview',

    autoWidth: true,

    tbar: {
        xtype: 'userPreviewToolbar'
    },

    cls: 'cms-user-preview-panel',
    layout: 'fit',
    width: undefined,


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
        var profileData = this.generateProfileData(this.data);
        console.log(profileData);
        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                defaults: {
                    border: 0
                },
                items: [
                    {
                        width: 100,
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data,
                        margin: 5
                    },
                    {
                        flex: 1,
                        cls: 'center',
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                height: 70,
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                xtype: 'wizardPanel',
                                showControls: false,
                                items: [
                                    {
                                        stepNumber: 1,
                                        stepTitle: "Activities"
                                    },
                                    {
                                        stepNumber: 2,
                                        stepTitle: "Profile",
                                        tpl: Templates.account.userPreviewProfile,
                                        data: profileData
                                    },
                                    {
                                        stepNumber: 3,
                                        stepTitle: "Places"
                                    },
                                    {
                                        stepNumber: 4,
                                        stepTitle: "Memberships"
                                    },
                                    {
                                        stepNumber: 5,
                                        stepTitle: "Advanced"
                                    }]
                            }]
                    },
                    {
                        width: 300,
                        margin: 5,
                        cls: 'east',
                        tpl: Templates.account.userPreviewCommonInfo,
                        data: this.data
                    }]
        }];
        this.callParent( arguments );
    },

    generateProfileData: function( userData )
    {
        var fieldSetEmpty = true;
        var profileData = [];
        Ext.Array.each(this.fieldSets, function( fieldSet){
            var fieldSetData = { title: fieldSet.title};
            fieldSetData.fields = [];
            Ext.Array.each(fieldSet.fields, function(field){
                var value = userData[field] ? userData[field] : userData.userInfo[field];
                var title = App.view.EditUserFormPanel.fieldLabels[field] ?
                        App.view.EditUserFormPanel.fieldLabels[field] : field
                if (value)
                {
                    Ext.Array.include(fieldSetData.fields, {title: title, value: value});
                    fieldSetEmpty = false;
                }
            });
            if (!fieldSetEmpty)
            {
                Ext.Array.include(profileData, fieldSetData);
                fieldSetEmpty = true;
            }
        });
        return profileData;
    }



} );