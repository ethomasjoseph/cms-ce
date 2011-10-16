Ext.define( 'Common.fileupload.SimpleImageUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.simpleImageUploadButton',
    width: 100,
    height: 100,

    tpl : new Ext.Template(
            '<div id="{id}" class="cms-image-upload-button-container">' +
                '<img src="resources/images/x-user-photo.png" class="cms-image-upload-button-image"/>' +
                '<div class="cms-image-upload-button-progress-bar-container">' +
                    '<div class="cms-image-upload-button-progress-bar"></div>' +
                '</div>' +
            '</div>'),

    onRender: function() {
        this.callParent(arguments); // call the superclass onRender method
        var id = Ext.id(null, 'image-uploader-');
        this.update({id: id});
        this.progressBarId = id;
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
                runtimes : 'html5',
                //runtimes : 'html5,flash,silverlight',
                multi_selection:false,
                // Attach listener to this button.
                browse_button : buttonId,
                url : 'data/user/photo',
                multipart: true,
                //flash_swf_url : 'common/js/fileupload/plupload/js/plupload.flash.swf',
                //silverlight_xap_url : 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
                filters : [
                    {title : 'Image files', extensions : 'jpg,gif,png'}
                ]
            }
        );

        uploader.bind('Init', function(up, params) {
        });

        uploader.bind('FilesAdded', function(up, files) {
            uploader.start();
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
        var pct = 0;
        var interval = setInterval(function() {
            progressBar.style.width = pct + '%';
            if (pct >= 100) {
                clearInterval(interval);
            }
            setTimeout(function() {
                me.resetProgressBar();
                me.updateImage();
            }, 1200);

            pct++;
        }, 5);
    },

    updateImage: function()
    {
        this.getImageElement().src = 'resources/images/x-user.png';
    },

    getImageElement: function()
    {
        return Ext.DomQuery.select('#'+ this.progressBarId + ' .cms-image-upload-button-image')[0];
    },

    showProgressBar: function()
    {
        this.getProgressBarContainerElement().style.visibility = 'visible';
    },

    resetProgressBar: function()
    {
        this.getProgressBarContainerElement().style.visibility = 'hidden';
        this.getProgressBarElement().style.width = '0';
    },

    getProgressBarContainerElement: function()
    {
        return Ext.DomQuery.select('#'+ this.progressBarId + ' .cms-image-upload-button-progress-bar-container')[0];
    },

    getProgressBarElement: function()
    {
        return Ext.DomQuery.select('#'+ this.progressBarId + ' .cms-image-upload-button-progress-bar')[0];
    }

} );
