Ext.define( 'Common.fileupload.PhotoUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.photoUploadButton',
    width: 132,
    height: 132,

    tpl : new Ext.Template(
            '<div id="{id}" class="cms-image-upload-button-container">' +
                '<img src="resources/images/x-user-photo.png" class="cms-image-upload-button-image"/>' +
                '<div class="cms-image-upload-button-progress-bar-container">' +
                    '<div class="cms-image-upload-button-progress-bar"><!-- --></div>' +
                '</div>' +
            '</div>'),

    onRender: function() {
        var me = this;
        this.callParent(arguments);
        var id = Ext.id(null, 'image-upload-button-');
        this.update({id: id});
        this.uploadButtonId = id;
    },

    afterRender: function() {
        var body = Ext.getBody();
        var buttonContainer =  Ext.get(this.uploadButtonId);

        body.on('dragover', function(event) {
            buttonContainer.addCls('cms-image-upload-button-container-dragover');
        });

        body.on('dragleave', function(event) {
            buttonContainer.removeCls('cms-image-upload-button-container-dragover');
        });

        body.on('drop', function(event) {
            buttonContainer.removeCls('cms-image-upload-button-container-dragover');
        });

        body.on('dragend', function(event) {
            buttonContainer.removeCls('cms-image-upload-button-container-dragover');
        });

    },

    initComponent: function()
    {
        if (!window.plupload)
        {
            alert('ImageUploadButton requires Plupload!');
        }

        var me = this;

        var buttonId = this.getId();

        var uploader = new plupload.Uploader(
            {
                runtimes : 'html5,flash,silverlight',
                multi_selection:false,
                browse_button : buttonId,
                url : 'data/user/photo',
                multipart: true,
                drop_element: buttonId,
                flash_swf_url : 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url : 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
                filters : [
                    {title : 'Image files', extensions : 'jpg,gif,png'}
                ]
            }
        );

        uploader.bind('Init', function(up, params) {
        });

        uploader.bind('FilesAdded', function(up, files) {
            // uploader.start();
            me.fakeUpload();
        });

        uploader.bind('UploadProgress', function(up, file) {
        });

        uploader.bind('UploadComplete', function(up, files) {
        });

        setTimeout( function() {
            uploader.init();
        }, 1);

        this.callParent( arguments );
    },

    fakeUpload: function()
    {
        var me = this;
        var progressBar = this.getProgressBarElement();
        me.showProgressBar();
        var percent = 0;
        var interval = setInterval(function() {
            progressBar.style.width = percent + '%';
            if (percent >= 100) {
                clearInterval(interval);
                me.updateImage();
                me.fadeOutProgressBar();
            }

            percent++;
        }, 5);
    },

    updateImage: function()
    {
        this.getImageElement().src = 'resources/images/x-user.png';
    },

    showProgressBar: function()
    {
        this.getProgressBarContainerElement().style.opacity = 1;
        this.getProgressBarContainerElement().style.visibility = 'visible';
    },

    fadeOutProgressBar: function()
    {
        this.getProgressBarElement().style.width = '0';
        this.getProgressBarContainerElement().style.visibility = 'hidden';
    },

    getImageElement: function()
    {
        return Ext.DomQuery.select('#'+ this.uploadButtonId + ' .cms-image-upload-button-image')[0];
    },

    getProgressBarContainerElement: function()
    {
        return Ext.DomQuery.select('#'+ this.uploadButtonId + ' .cms-image-upload-button-progress-bar-container')[0];
    },

    getProgressBarElement: function()
    {
        return Ext.DomQuery.select('#'+ this.uploadButtonId + ' .cms-image-upload-button-progress-bar')[0];
    }

});
