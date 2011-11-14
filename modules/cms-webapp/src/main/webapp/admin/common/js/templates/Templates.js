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
            '<div>' + '<div class="cms-user-info clearfix">' + '<div class="cms-user-photo cms-left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' + '</div>' +
                    '<div class="cms-left">' + '<h2>{displayName}</h2>' + '({qualifiedName})<br/>' +
                    '<a href="mailto:{email}:">{email}</a>' + '</div>' + '</div>' + '</div>'
    ,groupList:
            '<div class="clearfix">' +
                '<div class="cms-left">' +
                    '<span class="icon-group cms-list-item"></span></div>' + '<div class="cms-left"><span>{name} ({userStore})</span>' +
                '</div>' +
            '</div>'

};
