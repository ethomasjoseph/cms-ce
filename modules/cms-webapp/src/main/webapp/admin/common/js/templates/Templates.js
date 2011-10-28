if ( !Templates )
{
    var Templates = {};
}

Templates.common = {
    wizardPanelSteps:
        '<ul class="cms-wizard-navigation clearfix">' +
            '<tpl for=".">' +
                '<li class="{[ this.resolveClsName( xindex, xcount ) ]}">' +
                '<a wizardStep="{[xindex]}" href="#" class="step {[ this.resolveClsName( xindex, xcount ) ]}">{[xindex]}. {[  (values.stepTitle || values.title) ]}</a></li>' +
            '</tpl>' +
        '</ul>'

    ,userInfo:
        '<div>' +
            '<div class="cms-user-info clearfix">' +
                '<div class="cms-user-photo cms-left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' +
                '</div>' +
                '<div class="cms-left">' +
                    '<h3>{displayName}</h3>' +
                    '({qualifiedName})<br/>' +
                    '<a href="mailto:{email}:">{email}</a>' +
                '</div>' +
                '<div class="cms-clear"></div>' +
            '</div>' +
        '</div>'

};
