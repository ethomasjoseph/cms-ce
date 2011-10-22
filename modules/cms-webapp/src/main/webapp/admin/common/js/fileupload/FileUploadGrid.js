Ext.define( 'Common.fileupload.FileUploadGrid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.fileUploadGrid',
    width: 300,
    height: 150,

    initComponent: function()
    {
        if (!window.plupload)
        {
            alert('ImageUploadButton requires Plupload!');
        }

        this.createStore();
        this.createToolbar();
        this.columns = [
            { header: 'Name',  dataIndex: 'fileName', flex: 2 },
            { header: 'Size',  dataIndex: 'fileSize', flex: 1 }
        ];

        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode: 'MULTI'
        });

        this.callParent( arguments );
    },

    createToolbar: function()
    {
        var grid = this;
        grid.tbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    xtype: 'button',
                    text: 'Browse...',
                    iconCls: 'icon-browse',
                    itemId: 'browseButton'
                },
                {
                    xtype: 'button',
                    text: 'Upload',
                    iconCls: 'icon-upload',
                    disabled: true,
                    itemId: 'uploadButton',
                    handler: function() {
                        Ext.Msg.alert('TODO', 'Upload');
                    }
                },
                {
                    xtype: 'button',
                    text: 'Remove',
                    iconCls: 'icon-remove',
                    disabled: true,
                    itemId: 'removeButton',
                    handler: function() {
                        grid.removeSelectedFiles(grid.getSelectionModel().getSelection());

                    }
                }
            ],
            listeners: {
                afterrender: {
                    fn: grid.initUploader,
                    scope: grid
                }
            }
        });

        this.on('selectionchange', function(model, selected, eOpts ) {
            var removeButton = grid.down('toolbar').down('button[itemId=removeButton]');
            removeButton.setDisabled(selected.length === 0);
        }, this);

        this.getStore().on('datachanged', function(store, eOpts) {
            var uploadButton = grid.down('toolbar').down('button[itemId=uploadButton]');
            uploadButton.setDisabled(store.data.items.length === 0);
        }, this, null);
    },

    initUploader: function()
    {
        var store = this.getStore();
        var browseButtonElementId = this.down('toolbar').down('button[itemId=browseButton]').getEl().id;

        this.uploader = new plupload.Uploader(
            {
                runtimes            : 'html5,flash,silverlight',
                multi_selection     : true,
                browse_button       : browseButtonElementId ,
                url                 : 'data/user/photo',
                multipart           : true,
                //drop_element      : buttonId,
                flash_swf_url       : 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url : 'common/js/fileupload/plupload/js/plupload.silverlight.xap'
            }
        );

        this.uploader.bind('FilesAdded', function(up, files)
        {
            var file = null;
            for (var i = 0; i < files.length; i++)
            {
                file = files[i];
                store.add({
                    'fileId': file.id,
                    'fileName': file.name,
                    'fileSize': file.size
                });
            }
        });

        this.uploader.bind('UploadProgress', function(up, file) {
        });

        this.uploader.bind('UploadComplete', function(up, files) {
        });

        this.uploader.bind('Init', function(up, params) {
        });

        this.uploader.init();
    },

    removeSelectedFiles: function(selected)
    {
        var store = this.getStore(), fileRecord = null;

        for (var i = 0; i < selected.length; i++)
        {
            fileRecord = selected[i];
            store.remove(fileRecord);

            this.removeFileFromUploaderQueue(fileRecord.data);
        }
    },

    removeFileFromUploaderQueue: function(recordData)
    {
        var uploaderFiles = this.uploader.files;
        for (var j = 0; j < uploaderFiles.length; j++ )
        {
            if ( uploaderFiles[j].id === recordData.fileId )
            {
                this.uploader.removeFile(uploaderFiles[j]);
            }
        }
    },

    createStore: function()
    {
       this.store = Ext.create('Ext.data.Store', {
            fields:['fileName', 'fileSize', 'fileId'],
            data:{'items':[
            ]},
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    root: 'items'
                }
            }
        });
    }
});
