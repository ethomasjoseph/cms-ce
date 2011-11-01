if ( !Templates )
{
    var Templates = {};
}

Templates.main = {
    loggedInUserButtonPopup:
        '<div class="cms-logged-in-user-popup-left">' +
            '<img src="resources/images/x-user.png"/>' +
        '</div>' +
        '<div class="cms-logged-in-user-popup-right">' +
            '<h1>{displayName}</h1>' +
            '<p>{qualifiedName}</p>' +
            '<p>{email}</p>' +
            '<p>&nbsp;</p>' +
            '<p><a href="javascript:;">Edit Account</a></p>' +
            '<p><a href="javascript:;">Change Password</a></p>' +
            '<p class="cms-logged-in-user-popup-log-out" style="float:right"><a href="index.html">Log Out</a></p>' +
        '</div>',

    activityStream:
        '<tpl for=".">' +
            '<div class="cms-activity-stream-message">' +
                '<table border="0" cellspacing="0" cellpadding="0">' +
                    '<tr>' +
                        '<td valign="top" class="photo-container">' +
                            '<img class="photo" src="{photo}"/>' +
                        '</td>' +
                        '<td valign="top">' +
                            '<div class="display-name-location"><a href="javascript:;">' +
                            '<tpl if="birthday"><img src="app/main/images/cake.png" style="width:11px; height:8px" title="{displayName} has Birthday today"/> </tpl>' +
                            '{displayName}</a> via {location}</div>' +
                            '<div>{action}: ' +
                                '<tpl if="action == \'Said\'">{description}</tpl>' +
                                '<tpl if="action != \'Said\'"><a href="javascript:;">{description}</a></tpl>' +
                             '</div>' +
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
                            '<td style="text-align:right">' +
                                '<a href="javascript:;" class="more" style="visibility:hidden"><!-- --></a>' +
                            '</td>' +
                        '</tr>' +
                    '</table>' +
               '</div>' +
            '</div>' +
        '</tpl>',

    speakOutPanel:
        '<div>' +
            '<h1>What\'s happening?</h1>' +
            '<div id="activity-stream-speak-out-text-input"><!-- --></div>' +
            '<div class="clearfix">' +
                '<div class="clearfix">'+
                    '<div class="cms-left">'+
                        '<div id="activity-stream-speak-out-url-shortener-button-container"><!-- --></div>' +
                    '</div>' +
                    '<div class="cms-right">'+
                        '<div id="activity-stream-speak-out-letters-left-container" class="cms-left">140</div>' +
                        '<div id="activity-stream-speak-out-send-button-container" class="cms-left"><!-- --></div>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>',

    notifier:
        '<div class="cms-notifier clearfix">' +
            '<table border="0">'+
                '<tr>' +
                    '<td style="width: 42px" valign="top">' +
                        '<img src="app/main/images/feedback-ok.png" style="width:32px; height:32px"/>' +
                    '</td>' +
                    '<td valign="top">' +
                        '<h1>{messageTitle}</h1>' +
                        '<div class="message-text">{messageText}</div>' +
                    '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td colspan="2" style="text-align: right">' +
                        '<p><a class="notify-user-button" href="javascript:;">Notify User</a></p>' +
                    '</td>' +
                '</tr>'+
            '</table>'+
        '</div>'
};
