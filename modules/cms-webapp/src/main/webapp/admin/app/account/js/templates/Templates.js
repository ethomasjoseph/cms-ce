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

    ,userWizardHeader:
            '<div class="cms-wizard-header">' +
                '<input type="text" value="{displayName}" readonly="true" class="cms-display-name"/>' +
            '</div>' +
            '<div class="cms-wizard-userstore">' +
                '<label>{[ values.isNewUser ? "New User" : "User" ]}: </label>' +
                '<span>{[ values.userstoreName ? (values.userstoreName + "&#92;") : "" ]}</span>' +
                '<span>{qUserName}</span>' +
            '</div>'

    ,groupWizardHeader:
            '<div class="cms-wizard-header">' +
                '<h1 class="cms-display-name cms-edited-field">{displayName}</h1>' +
            '</div>'

    ,noUserSelected:
            '<div>No user selected</div>'

    ,selectedUserLarge:
            '<tpl for="users">' +
                    '<div id="selected-item-box-{key}" class="cms-selected-item-box large x-btn-default-large clearfix">' +
                    '<div class="left">' +
                    '<tpl if="hasPhoto"><img alt="User" src="data/user/photo?key={key}&thumb=true" alt="{displayName}"/></tpl>' +
                    '<tpl if="(!hasPhoto) && type===\'user\'"><img alt="User" src="resources/images/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'group\'"><img src="resources/icons/256x256/group.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'role\'"><img src="resources/icons/256x256/masks.png" alt="{displayName}"/></tpl>' +
                    '</div>' +
                    '<div class="center">' + '<h2>{displayName}</h2>' + '<p>{userStore}\\\\{name}</p>' + '</div>' +
                    '<div class="right">' +
                    ' <a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                    '</div>' + '</div>' + '</tpl>'

    ,selectedUserSmall:
            '<tpl for="users">' +
                    '<div id="selected-item-box-{key}" class="cms-selected-item-box small x-btn-default-small clearfix">' +
                    '<div class="cms-selected-item-box left">' +
                    '<tpl if="hasPhoto"><img alt="User" src="data/user/photo?key={key}&thumb=true" alt="{displayName}"/></tpl>' +
                    '<tpl if="(!hasPhoto) && type===\'user\'"><img alt="User" src="resources/images/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'group\'"><img alt="Group" src="resources/icons/256x256/group.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'role\'"><img alt="Group" src="resources/icons/256x256/masks.png" alt="{displayName}"/></tpl>' +
                    '</div>' +
                    '<div class="cms-selected-item-box center">' + '<h2>{displayName}</h2>' + '</div>' +
                    '<div class="cms-selected-item-box right">' +
                    '<a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                    '</div>' + '</div>' + '</tpl>'

    ,userPreview:
            '<div id="cms-user-preview" class="clearfix cms-user-preview"><div class="west cms-left">' +
                    '<div class="photo-placeholder">' +
                    '<tpl if="hasPhoto"><img src="data/user/photo?key={key}" alt="{displayName}"/></tpl>' +
                    '<tpl if="(!hasPhoto) && type===\'user\'"><img src="resources/images/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'group\'"><img src="resources/icons/256x256/group.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'role\'"><img src="resources/icons/256x256/masks.png" alt="{displayName}"/></tpl>' +
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
                    '<tpl if="(!hasPhoto) && type===\'user\' && (!builtIn)"><img src="resources/images/icons/256x256/dummy-user.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'group\'"><img src="resources/icons/256x256/group.png" alt="{displayName}"/></tpl>' +
                    '<tpl if="type===\'role\' || builtIn===true"><img src="resources/icons/256x256/masks.png" alt="{displayName}"/></tpl>' +
                    '</div></div>'

    ,userPreviewHeader:
            '<div class="container">' + '<h1>{displayName}</h1><div><span>{userStore}\\\\{name}</span>' +
                    '<span class="email">&nbsp;{email}</span></div></div>'

    ,groupPreviewMemberships:
            '<tpl for="members">' +
                '<div class="clearfix cms-member-preview-el x-boxselect-item">' +
                    '<div class="cms-left">' +
                        '<span class="{[values.type==="user" && !values.builtIn ? "icon-user" : ' +
                        'values.type==="role" || values.builtIn ? "icon-role" : "icon-group"]} cms-list-item"></span></div>' +
                        '<div class="cms-left"><span><tpl if="type==\'user\'"> {displayName} ({qualifiedName})</tpl>' +
                        '<tpl if="type!=\'user\'">{name} ({userStore})</tpl></span>' +
                    '</div>' +
                '</div><br>' +
            '</tpl>'

    ,userPreviewMemberships:

            '<tpl if="(groups == null || groups.length == 0) && (indirectGroups == null || indirectGroups.length == 0)">' +
                '<h2 class="nodata">No data</h2>' +
            '</tpl>' +

            '<tpl if="groups != null && groups.length &gt; 0">' +
                '<fieldset class="x-fieldset x-fieldset-default cms-groups-boxselect">' +
                    '<legend class="x-fieldset-header x-fieldset-header-default">' +
                        '<div class="x-component x-fieldset-header-text x-component-default">Member Of</div>' +
                    '</legend>' +
                    '<table><tbody>' +
                        '<tpl for="groups">' +
                            '<tr><td>' +
                                '<li class="x-boxselect-item cms-{type}-item">' +
                                    '<div class="x-boxselect-item-text">{qualifiedName}</div>' +
                                '</li>' +
                            '</td></tr>' +
                        '</tpl>' +
                    '</tbody></table>' +
                '</fieldset>' +
            '</tpl>' +

            '<tpl if="indirectGroups != null && indirectGroups.length &gt; 0">' +
                '<fieldset class="x-fieldset x-fieldset-default cms-groups-boxselect">' +
                    '<legend class="x-fieldset-header x-fieldset-header-default">' +
                        '<div class="x-component x-fieldset-header-text x-component-default">Indirect Member Of</div>' +
                    '</legend>' +
                    '<table><tbody>' +
                        '<tpl for="indirectGroups">' +
                            '<tr><td>' +
                                '<li class="x-boxselect-item cms-{type}-item">' +
                                    '<div class="x-boxselect-item-text">{qualifiedName}</div>' +
                                '</li>' +
                            '</td></tr>' +
                        '</tpl>' +
                    '</tbody></table>' +
                '</fieldset>' +
            '</tpl>'

    ,userPreviewPlaces:
            '<tpl if="userInfo == null || userInfo.addresses == null || userInfo.addresses.length == 0">' +
                '<h2 class="nodata">No data</h2>' +
            '</tpl>' +

            '<tpl if="userInfo != null && userInfo.addresses != null && userInfo.addresses.length &gt; 0">' +
                '<fieldset class="x-fieldset x-fieldset-default cms-addresses-container">' +
                    '<legend class="x-fieldset-header x-fieldset-header-default">' +
                        '<div class="x-component x-fieldset-header-text x-component-default">Addresses</div>' +
                    '</legend>' +
                    '<tpl for="userInfo.addresses">' +
                        '<div class="address">' +
                            '<tpl if="label != null">' +
                                '<h3 class="x-fieldset-header-text">{label}</h3>' +
                            '</tpl>' +
                            '<div class="body">' +
                                '<table><tbody>' +
                                    '<tr><td class="label">Street:</td><td>{street}</td></tr>' +
                                    '<tr><td class="label">Postal Code:</td><td>{postalCode}</td></tr>' +
                                    '<tr><td class="label">Postal Address:</td><td>{postalAddress}</td></tr>' +
                                    '<tr><td class="label">Country:</td><td>{country}</td></tr>' +
                                    '<tr><td class="label">Region:</td><td>{region}</td></tr>' +
                                '</tbody></table>' +
                            '</div>' +
                        '</div>' +
                    '</tpl>' +
                '</fieldset>' +
            '</tpl>'

    ,userPreviewCommonInfo:
            '<div class="container">' +
                '<table>' +
                    '<thead>' +
                        '<tr><th>Login Info</th></tr>' +
                    '</thead>' +
                    '<tbody>' +
                        '<tr><td class="label">User Name:</td><td>{username}</td></tr>' +
                        '<tr><td class="label">E-mail:</td><td>{email}</td></tr>' +
                    '</tbody>' +
                '</table>' +
            '</div>' +

            '<div class="container cms-groups-boxselect">' +
                '<table>' +
                    '<thead>' +
                        '<tr><th>Roles</th></tr>' +
                    '</thead>' +
                    '<tbody>' +
                        '<tpl for="groups">' +
                            '<tr><td>' +
                                '<li class="x-boxselect-item cms-{type}-item">' +
                                    '<div class="x-boxselect-item-text">{qualifiedName}</div>' +
                                '</li>' +
                            '</td></tr>' +
                        '</tpl>' +
                    '</tbody>' +
                '</table>' +
            '</div>' +

            '<div class="container">' +
                '<table>' +
                    '<thead>' +
                        '<tr><th colspan="2">Settings</th></tr>' +
                    '</thead>' +
                    '<tbody>' +
                        '<tr><td class="label">Locale:</td><td>{locale}</td></tr>' +
                        '<tr><td class="label">Country:</td><td>{country}</td></tr>' +
                        '<tr><td class="label">TimeZone:</td><td>{timezone}</td></tr>' +
                    '</tbody>' +
                '</table>' +
            '</div>' +

            '<div class="container">' +
                '<table>' +
                    '<thead>' +
                        '<tr><th colspan="2">Statistics</th></tr>' +
                    '</thead>' +
                    '<tbody>' +
                        '<tr><td class="label">Last login:</td><td>{lastLogged}</td></tr>' +
                        '<tr><td class="label">Created:</td><td>{created}</td></tr>' +
                        '<tr><td class="label">Owner of:</td><td>394</td></tr>' +
                    '</tbody>' +
                '</table>' +
            '</div>'

    ,groupPreviewCommonInfo:
            '<tpl if="type===\'role\'">' +
                '<div class="container"><table><thead>' +
                '<tr><th >Description</th></tr></thead><tbody><tr>' +
                '<td>{staticDesc}</td></tr></tbody>' +
            '</table></div></tpl>' +
            '<tpl if="type===\'group\'">' +
            '<div class="container"><table><thead>' +
                '<tr><th colspan="2">Properties</th></tr></thead><tbody><tr>' +
                '<td class="label">Public:</td><td>{[values.public ? "yes" : "no"]}</td></tr><tr>' +
                '<td class="label">Description:</td><td>{description}</td></tr></tbody>' +
            '</table></div></tpl>' +
            '<div class="container"><table><thead>' +
                '<tr><th colspan="2">Statistics</th></tr></thead><tbody><tr>' +
                '<td class="label">Member count:</td><td>{members.length}</td></tr><tr>' +
                '<td class="label">Last updated:</td><td>{lastModified}</td></tr></tbody>' +
            '</table></div>'

    ,deleteManyUsers:
            '<div class="cms-delete-user-confirmation-message">' +
                    '<div class="icon-question-mark-32 cms-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                    '<div class="cms-left" style="margin-top:5px">' +
                    'Are you sure you want to delete the selected {selectionLength} items?' + '</div>' + '</div>'

    ,shortValidationResult:
            '<tpl if="(valid)"><img src="resources/icons/16x16/check.png"/></tpl>'

    ,passwordStatus:
            '<div class="passwordStatus" style="color: {color};">{text}</div>'



};
