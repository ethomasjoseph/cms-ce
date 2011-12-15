if ( !Templates )
{
    var Templates = {};
}

Templates.account = {
    userstoreRadioButton:
            '<tpl for=".">' + '<div class="cms-userstore clearfix">' +
                    '<div class="cms-left" style="margin: 24px 5px 0 0">' +
                    '<input type="radio" name="userstore" value="{key}">' + '</div>' +
                    '<div class="cms-userstore-block cms-left">' +
                    '<div class="cms-left" style="padding-right: 15px;"><img width="48" height="48" src="resources/icons/48x48/userstore.png"/></div>' +
                    '<div class="cms-left">' + '<h2>{name}</h2>' + '<p>(usersstores\\\\{name})</p>' + '</div>' +
                    '</div>' + '</div>' + '</tpl>'

    ,gridPanelNameRenderer:
            '<div style="float:left"><img src="{0}" class="cms-grid-thumbnail"></div>' +
                    '<div style="float:left; padding: 1px 0 0 5px"><div class="cms-grid-title">{1}</div>' +
                    '<div class="cms-grid-description">{3}\\{2}</div>'

    ,editUserPanelHeader:
            '<div class="cms-edit-form-header clearfix">' + '<div class="left">' +
                    '<img alt="User" src="data/user/photo?key={key}"/></div>' + '<div class="right">' +
                    '<h1><input id="display-name" type="text" value="{displayName}" readonly="true" class="cms-display-name {[edited ? "cms-edited-field" : ""]}"/></h1><a href="javascript:;" class="edit-button"></a>' +
                    '<p>{qualifiedName}</p></div></div>'

    ,newUserPanelHeader:
            '<div class="cms-wizard-header-container">' + '<div class="cms-wizard-header clearfix">' +
                    '<input type="text" value="{value}" readonly="true" id="cms-display-name" class="cms-display-name"/>' +
                    '</div>' + '<div class="clearfix user-store-user-name">' + '<tpl if="isNewUser">New User:</tpl>' +
                    '<tpl if="isNewUser == false">User:</tpl>' +
                    '<span><tpl if="userstoreName">{userstoreName}\\\\</tpl></span><span>{qUserName}</span></div>' +
                    '</div>'

    ,noUserSelected:
            '<div>No user selected</div>'

    ,selectedUserLarge:
            '<tpl for="users">' +
                    '<div id="selected-item-box-{key}" class="cms-selected-item-box large x-btn-default-large clearfix">' +
                    '<div class="left">' + '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' + '</div>' +
                    '<div class="center">' + '<h2>{displayName}</h2>' + '<p>{userStore}\\\\{name}</p>' + '</div>' +
                    '<div class="right">' +
                    ' <a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                    '</div>' + '</div>' + '</tpl>'

    ,selectedUserSmall:
            '<tpl for="users">' +
                    '<div id="selected-item-box-{key}" class="cms-selected-item-box small x-btn-default-small clearfix">' +
                    '<div class="cms-selected-item-box left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' + '</div>' +
                    '<div class="cms-selected-item-box center">' + '<h2>{displayName}</h2>' + '</div>' +
                    '<div class="cms-selected-item-box right">' +
                    '<a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                    '</div>' + '</div>' + '</tpl>'

    ,userPreview:
            '<div id="cms-user-preview" class="clearfix cms-user-preview"><div class="west cms-left">' +
                    '<div class="photo-placeholder">' +
                    '<tpl if="hasPhoto"><img src="data/user/photo?key={key}" alt="{displayName}"/></tpl>' +
                    '<tpl if="!hasPhoto"><img src="resources/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '</div>' +
                    '</div><div class="center"><div class="container">' +
                    '<h1>{displayName}</h1><div><span>{userStore}\\\\{name}</span>' +
                    '<span class="email">&nbsp;{email}</span></div></div>' +
                    '<div class="navigation"><ul><li>Activities</li>' +
                    '<li class="active">Profile</li><li>Places</li>' +
                    '<li>Memberships</li><li>Advanced</li></ul></div><div><table><thead>' +
                    '<tr><th colspan="2">Name</th></tr></thead><tbody>' +
                    '<tr><td class="label">Prefix:</td><td>Mr</td></tr><tr>' +
                    '<td class="label">First Name:</td><td>John</td></tr><tr>' +
                    '<td class="label">Middle Name:</td><td>Enok</td></tr><tr>' +
                    '<td class="label">Last Name:</td><td>Vollestad</td></tr>' +
                    '<tr><td class="label">Suffix:</td><td>Jr</td></tr></tbody>' +
                    '</table></div><div><table><thead><tr>' + '<th colspan="2">Details</th></tr></thead><tbody><tr>' +
                    '<td class="label">Organization:</td><td>Norsk Regnesentral</td></tr>' +
                    '<tr><td class="label">Homepage:</td><td><a href="http://www.nr.no">http://www.nr.no</a></td>' +
                    '</tr><tr><td class="label">Gender:</td><td>Male</td>' +
                    '</tr></tbody></table></div><div><table><thead><tr>' +
                    '<th colspan="2">Communication</th></tr></thead><tbody><tr>' +
                    '<td class="label">Mobile:</td><td>12345678</td></tr><tr>' +
                    '<td class="label">Phone:</td><td>01234567</td></tr><tr>' +
                    '<td class="label">Fax:</td><td>98765432</td>' +
                    '</tr></tbody></table></div></div><div class="east"><div><table><thead><tr>' +
                    '<th>Roles</th></tr></thead><tbody><tr><td>Enterprise Administrator</td></tr>' +
                    '<tr><td>Expert</td></tr></tbody></table></div><div><table><thead>' +
                    '<tr><th colspan="2">Settings</th></tr></thead><tbody><tr>' +
                    '<td class="label">Locale:</td><td>Norwegian (no)</td></tr><tr>' +
                    '<td class="label">Country:</td><td>Norway (NO)</td></tr><tr>' +
                    '<td class="label">TimeZone:</td><td>GMT+1</td></tr></tbody></table>' +
                    '</div><div><table><thead><tr><th colspan="2">Statistics</th></tr></thead>' +
                    '<tbody><tr><td class="label">Last login:</td><td>2011-10-25 23:05</td>' +
                    '</tr><tr><td class="label">Created:</td><td>2011-10-24 08:59</td></tr><tr>' +
                    '<td class="label">Owner of:</td><td>394</td></tr></tbody></table></div>' +
                    '<div><table><thead><tr><th>Last position</th></tr></thead></table></div></div></div>'

    ,userPreviewProfile:
            '<div>' + '<tpl for=".">' + '<fieldset class="x-fieldset x-fieldset-default">' +
                    '<legend class="x-fieldset-header x-fieldset-header-default">' +
                    '<div class="x-component x-fieldset-header-text x-component-default">{title}</div>' + '</legend>' +
                    '<table><tbody>' + '<tpl for="fields">' +
                    '<tr><td class="label">{title}</td><td>{value}</td></tr>' + '</tpl>' +
                    '</tbody></table></fieldset>' + '</tpl></div>'

    ,userPreviewPhoto:
            '<div class="west cms-left">' +
                    '<div class="photo-placeholder">' +
                    '<tpl if="hasPhoto"><img src="data/user/photo?key={key}" alt="{displayName}"/></tpl>' +
                    '<tpl if="!hasPhoto"><img src="resources/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '</div></div>'

    ,userPreviewHeader:
            '<div class="container">' + '<h1>{displayName}</h1><div><span>{userStore}\\\\{name}</span>' +
                    '<span class="email">&nbsp;{email}</span></div></div>'

    ,previewMemberships:
            '<div class="container">' + '<ul>' + '<tpl for="members">' +
                    '<li class="{[values.type === \"user\" ? "user-el" : "group-el"]}">{displayName}</li>' + '</tpl></ul></div>'

    ,userPreviewCommonInfo:
            '<div><table><thead><tr>' + '<th>Roles</th></tr></thead><tbody>' + '<tpl for="groups">' +
                    '<tr><td>{name}</td></tr>' + '</tpl>' + '</tbody></table></div><div><table><thead>' +
                    '<tr><th colspan="2">Settings</th></tr></thead><tbody><tr>' +
                    '<td class="label">Locale:</td><td>{locale}</td></tr><tr>' +
                    '<td class="label">Country:</td><td>{country}</td></tr><tr>' +
                    '<td class="label">TimeZone:</td><td>{timezone}</td></tr></tbody></table>' +
                    '</div><div><table><thead><tr><th colspan="2">Statistics</th></tr></thead>' +
                    '<tbody><tr><td class="label">Last login:</td><td>{lastLogged}</td>' +
                    '</tr><tr><td class="label">Created:</td><td>{created}</td></tr><tr>' +
                    '<td class="label">Owner of:</td><td>394</td></tr></tbody></table></div>' +
                    '<div><table><thead><tr><th>Last position</th></tr></thead></table></div>'

    ,deleteManyUsers:
            '<div class="cms-delete-user-confirmation-message">' +
                    '<div class="icon-question-mark-32 cms-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                    '<div class="cms-left" style="margin-top:5px">' +
                    'Are you sure you want to delete the selected {selectionLength} items?' + '</div>' + '</div>'



};
