Ext.define( 'Common.fileupload.PhotoUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.photoUploadButton',
    width: 132,
    height: 132,
    url: '',
    progressBarHeight: 8,

    // TODO: Move markup to template file
    tpl : new Ext.XTemplate(
            '<div id="{id}" class="cms-image-upload-button-container" style="width:{width - 2}px;height:{height - 2}px">' +
                '<img src="resources/images/x-user-photo.png" class="cms-image-upload-button-image" style="width:{width - 6}px;height:{height - 6}px"/>' +
                '<div class="cms-image-upload-button-progress-bar-container" style="width:{width - 12}px">' +
                    '<div class="cms-image-upload-button-progress-bar" style="height:{progressBarHeight}px"><!-- --></div>' +
                '</div>' +
                '<div id="{id}-over-border" style="visibility: hidden; position:absolute; top:0; left:0; width:{width - 4}px;height:{height - 4}px; border: 4px solid #dbeeff;"></div>' +
            '</div>'),

    initComponent: function()
    {
        if (!window.plupload)
        {
            alert('ImageUploadButton requires Plupload!');
        }
    },

    onRender: function()
    {
        this.callParent(arguments);

        var buttonElementId = Ext.id(null, 'image-upload-button-');
        var width = this.width;
        var height = this.height;
        var progressBarHeight = this.progressBarHeight;
        this.update(
            {
                id: buttonElementId,
                width: width,
                height: height,
                progressBarHeight: progressBarHeight
            }
        );

        this.buttonElementId = buttonElementId;
    },

    afterRender: function()
    {
        this.initUploader();
        this.addBodyMouseEventListeners();
    },

    initUploader: function()
    {
        var uploadButton = this;
        var buttonId = this.getId();

        var uploader = new plupload.Uploader(
            {
                runtimes                : 'html5,flash,silverlight',
                multi_selection         :false,
                browse_button           : buttonId,
                url                     : this.url,
                multipart               : true,
                drop_element            : buttonId,
                flash_swf_url           : 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url     : 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
                filters : [
                    {title : 'Image files', extensions : 'jpg,gif,png'}
                ]
            }
        );

        uploader.bind('Init', function(up, params) {
        });

        uploader.bind('FilesAdded', function(up, files) {
        });

        uploader.bind('QueueChanged', function(up ) {
            // TODO: Check files length. Only one should be allowed
            // TODO: Check file extension. Only jpeg,jpg,png,gif,tiff,bmp is allowed.
            up.start();
        });

        uploader.bind( 'UploadFile', function(up, file) {
            uploadButton.showProgressBar();
        });

        uploader.bind('UploadProgress', function(up, file) {
            uploadButton.updateProgressBar( file );
        });

        uploader.bind( 'FileUploaded', function( up, file, response ) {
            if ( response && response.status == 200 ) {
                var responseObj = Ext.decode(response.response);
                uploadButton.updateImage( responseObj.src );
            }
            uploadButton.hideProgressBar();
        });

        uploader.bind('UploadComplete', function(up, files) {

        });

        setTimeout(function() {
            uploader.init();
        }, 1);
    },

    updateImage: function( src )
    {
        this.getImageElement().src = src || 'resources/images/x-user.png';
    },

    showProgressBar: function()
    {
        this.getProgressBarContainerElement().style.opacity = 1;
        this.getProgressBarContainerElement().style.visibility = 'visible';
    },

    updateProgressBar: function( file )
    {
        var progressBar = this.getProgressBarElement();
        var percent = file.percent || 0;
        progressBar.style.width = percent + '%';
    },

    hideProgressBar: function()
    {
        this.getProgressBarElement().style.width = '0';
        this.getProgressBarContainerElement().style.visibility = 'hidden';
    },

    getImageElement: function()
    {
        return Ext.DomQuery.select('#'+ this.buttonElementId + ' .cms-image-upload-button-image')[0];
    },

    getProgressBarContainerElement: function()
    {
        return Ext.DomQuery.select('#'+ this.buttonElementId + ' .cms-image-upload-button-progress-bar-container')[0];
    },

    getProgressBarElement: function()
    {
        return Ext.DomQuery.select('#'+ this.buttonElementId + ' .cms-image-upload-button-progress-bar')[0];
    },

    addBodyMouseEventListeners: function()
    {
        var bodyElement = Ext.getBody();
        var dropTarget =  Ext.get(this.buttonElementId);
        var border = Ext.get(this.buttonElementId + '-over-border');

        function cancelEvent( event ) {
            if (event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }

        function showBorder() {
            //border.dom.style.visibility = 'visible';
        }
        function hideBorder() {
            //border.dom.style.visibility = 'hidden';
        }

        function highlightDropTarget() {
            dropTarget.addCls('cms-file-upload-drop-target');
            showBorder();
        }

        function removeHighlightFromDropTarget() {
            dropTarget.dom.className = dropTarget.dom.className.replace(/ cms-file-upload-drop-target/ , '');
            hideBorder();
        }

        dropTarget.on('mouseenter', function(event) {
            showBorder();
            cancelEvent(event);
        });
        dropTarget.on('mouseleave', function(event) {
            hideBorder();
            cancelEvent(event);
        });
        dropTarget.on('dragenter', function(event) {
            dropTarget.highlight('99BCE8');
            cancelEvent(event);
        });

        bodyElement.on('dragover', function(event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragenter', function(event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragleave', function(event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('drop', function(event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragend', function(event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
    }

});
