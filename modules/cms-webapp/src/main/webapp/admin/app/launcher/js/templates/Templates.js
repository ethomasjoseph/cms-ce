if ( !Templates )
{
    var Templates = {};
}

Templates.launcher = {
    loggedInUserButtonPopup:
        '<div class="cms-logged-in-user-popup-left">' +
            '<img src="resources/images/x-user.png"/>' +
        '</div>' +
        '<div class="cms-logged-in-user-popup-right">' +
            '<h1>{displayName}</h1>' +
            '<p>{qualifiedName}</p>' +
            '<p>{email}</p>' +
            '<p>&nbsp;</p>' +
            '<p>Edit Account</p>' +
            '<p>Change Password</p>' +
            '<p>&nbsp;</p>' +
            '<p class="cms-logged-in-user-popup-log-out"><a href="index.html">Log Out</a></p>' +
        '</div>',

    activityStreamItem:
        '<div class="cms-activity-stream-message">' +
            '<table border="0" cellspacing="0" cellpadding="0">' +
                '<tr>' +
                    '<td valign="top" class="photo-container">' +
                        '<img class="photo" src="{photo}"/>' +
                    '</td>' +
                    '<td valign="top">' +
                        '<div class="display-name-location"><a href="javascript:;">' +
                        '<tpl if="birthday"><img src="resources/images/cake.png" title="Birthday"/> </tpl>' +
                        '{displayName}</a> via {location}</div>' +
                        '<div>{action}: <a href="javascript:;">{description}</a></div>' +
                    '</td>' +
                '</tr>' +
            '</table>' +
            '<div class="date-links clearfix" style="clear:both">' +
                '<table border="0" cellspacing="0" cellpadding="0">' +
                    '<tr>' +
                        '<td>' +
                            '<span class="pretty-date">{prettyDate}</span>' +
                        '</td>' +
                        '<td>' +
                            '<a href="javascript:;" class="favorite" style="visibility:hidden">Favorite</a>' +
                        '</td>' +
                        '<td>' +
                            '<a href="javascript:;" class="comment" style="visibility:hidden">Comment</a>' +
                        '</td>' +
                        '<td>' +
                            '<a href="javascript:;" class="more" style="visibility:hidden"><!-- --></a>' +
                        '</td>' +
                    '</tr>' +
                '</table>' +
           '</div>' +
        '</div>',

    speakOutPanel:
        '<div>' +
            '<h1>What\'s happening?</h1>' +
            '<div id="activity-stream-speak-out-text-input"></div>' +
            '<div class="clearfix">' +
                '<!--div id="activity-stream-speak-out-url-shortener">[°]</div-->' +
                '<!--div id="activity-stream-speak-out-letters-left">120</div-->' +
                '<div id="activity-stream-speak-out-send-button"></div>' +
            '</div>' +
        '</div>'
};
