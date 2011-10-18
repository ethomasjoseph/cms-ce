if ( !Templates )
{
    var Templates = {};
}

Templates.contenttype = {
    detailPanelInfo:
        '<div class="detail-info">' +
            '<h3>{name}</h3>' +
            '<dl>' +
                '<dt>Key</dt><dd>{key}</dd>' +
                '<dt>Last Modified</dt><dd>{timestamp:this.formatDate}</dd>' +
            '</dl>' +
        '</div>'
};
