if ( !Templates )
{
    var Templates = {};
}

Templates.cache = {
    detailPanelHeader:
        '<div class="cms-cache-info">' +
          '<h1>{name} <span><tpl if="implementationName != null">({implementationName})</tpl></span></h1>' +
        '</div>'
};
