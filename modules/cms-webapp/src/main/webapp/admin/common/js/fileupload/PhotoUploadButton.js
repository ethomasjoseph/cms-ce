Ext.define( 'Common.fileupload.PhotoUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.photoUploadButton',
    width: 132,
    height: 132,
    progressBarHeight: 8,

    tpl : new Ext.XTemplate(
            '<div id="{id}" class="cms-image-upload-button-container" style="width:{width}px;height:{height}px">' +
                '<img src="resources/images/x-user-photo.png" class="cms-image-upload-button-image" style="width:{width - 4}px;height:{height - 4}px"/>' +
                '<div class="cms-image-upload-button-progress-bar-container" style="width:{width - 12}px">' +
                    '<div class="cms-image-upload-button-progress-bar" style="height:{progressBarHeight}px"><!-- --></div>' +
                '</div>' +
            '</div>'),

    onRender: function() {
        this.callParent(arguments);
        var me = this;
        var id = Ext.id(null, 'image-upload-button-');
        var width = this.width;
        var height = this.height;
        var progressBarHeight = this.progressBarHeight;
        this.update(
            {
                id: id,
                width: width,
                height: height,
                progressBarHeight: progressBarHeight
            }
        );
        this.uploadButtonId = id;
    },

    afterRender: function() {
        var body = Ext.getBody();
        var buttonContainer =  Ext.get(this.uploadButtonId);

        function cancelEvent( event ) {
            if (event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }

        function addDragOverCls() {
            buttonContainer.addCls('cms-image-upload-button-container-dragover');
        }

        function removeDragOverCls() {
            buttonContainer.removeCls('cms-image-upload-button-container-dragover');
        }

        body.on('dragover', function(event) {
            addDragOverCls();
            cancelEvent(event);
        });
        body.on('dragenter', function(event) {
            addDragOverCls();
            cancelEvent(event);
        });
        body.on('dragleave', function(event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        body.on('drop', function(event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        body.on('dragend', function(event) {
            removeDragOverCls();
            cancelEvent(event);
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
                me.hideProgressBar();
            }

            percent++;
        }, 5);
    },

    postProcess: function()
    {

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

    hideProgressBar: function()
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
