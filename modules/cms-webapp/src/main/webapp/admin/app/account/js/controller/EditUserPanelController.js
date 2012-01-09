Ext.define( 'App.controller.EditUserPanelController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [
        'SelectUserStoreWindow'
    ],

    init: function()
    {
        this.control(
            {

                '*[action=saveUser]': {
                    click: this.saveUser
                },
                '*[action=toggleDisplayNameField]': {
                    click: this.toggleDisplayNameField
                },
                'addressPanel #iso-country' : {
                    select: this.countryChangeHandler
                },
                '*[action=deleteUser]': {
                    click: this.deleteUser
                },
                'editUserPanel textfield[name=prefix]': {
                    keyup: this.textFieldHandleEnterKey
                },
                'editUserPanel textfield[name=first-name]': {
                    keyup: this.textFieldHandleEnterKey
                },
                'editUserPanel textfield[name=middle-name]': {
                    keyup: this.textFieldHandleEnterKey
                },
                'editUserPanel textfield[name=last-name]': {
                    keyup: this.textFieldHandleEnterKey
                },
                'editUserPanel textfield[name=suffix]': {
                    keyup: this.textFieldHandleEnterKey
                },
                'editUserPanel textfield[name=label]': {
                    keyup: this.updateTabTitle
                },
                '*[action=deleteGroup]': {
                    click: this.leaveGroup
                },
                '*[action=closeUserForm]': {
                    click: this.closeUserForm
                },
                '*[action=addNewTab]': {
                    click: this.addNewTab
                },
                '*[action=initValue]': {
                    added: this.initValue
                }
            }
        );
    },

    deleteUser: function( button )
    {
        var deleteUserWindow = button.up( 'deleteAccountWindow' );

        Ext.Ajax.request( {
                              url: 'data/user/delete',
                              method: 'POST',
                              params: {userKey: deleteUserWindow.userKey},
                              success: function( response, opts )
                              {
                                  deleteUserWindow.close();
                                  Ext.Msg.alert( 'Info', 'Account(s) was(were) deleted' );
                              },
                              failure: function( response, opts )
                              {
                                  Ext.Msg.alert( 'Info', 'Account(s) was(were) not deleted' );
                              }
                          } );
    },

    countryChangeHandler: function( field, newValue, oldValue, options )
    {
        var region = field.up( 'addressPanel' ).down( '#iso-region' );
        if ( region )
        {
            region.clearValue();
            Ext.apply( region.store.proxy.extraParams, {
                'countryCode': field.getValue()
            } );

            region.store.load( {
                                   callback: function( records, operation, success )
                                   {
                                       region.setDisabled( !records || records.length == 0 );
                                   }
                               } );
        }
        return true;
    },

    textFieldHandleEnterKey: function( field, event )
    {
        var formPanel = field.up( 'editUserPanel' );
        var prefix = formPanel.down( '#prefix' ) ? Ext.String.trim( formPanel.down( '#prefix' ).getValue() ) : '';
        var firstName = formPanel.down( '#first-name' ) ? Ext.String.trim( formPanel.down( '#first-name' ).getValue() )
                : '';
        var middleName = formPanel.down( '#middle-name' )
                ? Ext.String.trim( formPanel.down( '#middle-name' ).getValue() ) : '';
        var lastName = formPanel.down( '#last-name' ) ? Ext.String.trim( formPanel.down( '#last-name' ).getValue() )
                : '';
        var suffix = formPanel.down( '#suffix' ) ? Ext.String.trim( formPanel.down( '#suffix' ).getValue() ) : '';
        var displayName = Ext.get( 'display-name' );
        if ( displayName )
        {
            var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
            displayName.dom.value = Ext.String.trim( displayNameValue.replace( /  /g, ' ' ) );
        }
    },

    updateTabTitle: function ( field, event )
    {
        var addressPanel = field.up( 'addressPanel' );
        addressPanel.setTitle( field.getValue() );
    },

    toggleDisplayNameField: function ( button, event )
    {
        var tabId = button.currentUser != '' ? button.currentUser.userStore + '-' + button.currentUser.name
                : 'new-user';
        var locked = 'icon-locked';
        var open = 'icon-unlocked';
        var displayNameField = this.getCmsTabPanel().down( '#' + tabId ).down( '#display-name' );
        if ( button.iconCls == locked )
        {
            button.setIconCls( open );
            displayNameField.setReadOnly( false );
        }
        else
        {
            button.setIconCls( locked );
            displayNameField.setReadOnly( true );
        }

    },

    leaveGroup: function( element, event )
    {
        var groupItem = element.up( 'groupDetailButton' );
        var groupPanel = element.up( '#groupPanel' );
        var userPanel = element.up( 'editUserPanel' );
        Ext.Ajax.request( {
                              url: 'data/group/leave',
                              method: 'POST',
                              params: {key: userPanel.currentUser.key, isUser: true, leave: [groupItem.key]},
                              success: function( response, opts )
                              {
                                  groupPanel.removeItem( groupItem );
                              },
                              failure: function( response, opts )
                              {
                                  Ext.Msg.alert( 'Info', 'Group wasn\'t removed' );
                              }
                          } );
    },

    saveUser: function( button )
    {
        var editUserForm = button.up( 'editUserPanel' );
        if ( editUserForm.getForm().isValid() )
        {
            var formValues = editUserForm.getValues();
            var userData = {
                username: formValues['username'],
                'display-name': Ext.get( 'display-name' ).dom.value,
                email: formValues['email'],
                key: editUserForm.userFields.key,
                userStore: editUserForm.userFields.userStore ? editUserForm.userFields.userStore
                        : editUserForm.defaultUserStoreName,
                userInfo: formValues
            };
            var tabPanel = editUserForm.down( 'addressContainer' );
            var tabs = tabPanel.query( 'form' );
            var addresses = [];
            for ( var index in tabs )
            {
                var address = tabs[index].getValues();
                Ext.Array.include( addresses, address );
            }
            userData.userInfo.addresses = addresses;

            Ext.Ajax.request( {
                                  url: 'data/user/update',
                                  method: 'POST',
                                  jsonData: userData,
                                  success: function( response, opts )
                                  {
                                      var serverResponse = Ext.JSON.decode( response.responseText );
                                      if ( !serverResponse.success )
                                      {
                                          Ext.Msg.alert( 'Error', serverResponse.error );
                                      }
                                      else
                                      {
                                          Ext.Msg.alert( 'Info', 'User was updated' );
                                      }
                                  },
                                  failure: function( response, opts )
                                  {
                                      Ext.Msg.alert( 'Error', 'Internal server error was occured' );
                                  }
                              } );
        }
        else
        {
            Ext.Msg.alert( 'Error', 'Some required fields are missing' );
        }
    },

    showEditUserForm: function( account, callback, index )
    {
        if ( !account )
        {
            var window = Ext.create( 'widget.selectUserStoreWindow', {} );
            window.show();
        }
        else
        {
            var tabPane = this.getCmsTabPanel();
            var me = this;
            if (account.type === 'user'){
                Ext.Ajax.request( {
                      url: 'data/user/userinfo',
                      method: 'GET',
                      params: {key: account.key},
                      success: function( response )
                      {
                          var jsonObj = Ext.JSON.decode( response.responseText );
                          var tab = {
                              xtype: 'userWizardPanel',
                              id: account.userStore + '-' + account.name,
                              title: account.displayName + ' (' + account.qualifiedName + ')',
                              iconCls: 'icon-edit-user',
                              closable: true,
                              userstore: jsonObj.userStore,
                              qUserName: account.name,
                              userFields: jsonObj,
                              hasPhoto: account.hasPhoto,
                              autoScroll: true
                          };
                          var tabCmp = tabPane.addTab( tab, index );
                          var wizardPanel = tabCmp.down('wizardPanel');

                          var data = me.userInfoToWizardData(jsonObj);
                          wizardPanel.addData( data );
                          tabCmp.updateHeader( {value: account.displayName, edited: true} );

                          if ( Ext.isFunction( callback ) ) {
                              callback();
                          }
                      }
                  } );
            }
            else
            {
                Ext.Ajax.request( {
                      url: 'data/account/groupinfo',
                      method: 'GET',
                      params: {key: account.key},
                      success: function( response )
                      {
                          var jsonObj = Ext.JSON.decode( response.responseText );
                          console.log(jsonObj.group);
                          var tab = {
                              title: jsonObj.group.displayName,
                              iconCls: 'icon-new-group',
                              xtype: 'groupWizardPanel',
                              modelData: jsonObj.group
                          };
                          me.getCmsTabPanel().addTab( tab, index );

                          if ( Ext.isFunction( callback ) ) {
                              callback();
                          }
                      }
                } );
            }
        }
    },

    userInfoToWizardData:function ( userData )
    {
        var data = {
            'userStore': userData.userStore,
            'key': userData.key,
            'email': userData.email,
            'username': userData.username,
            'display-name': userData['display-name'],
            'userInfo': userData.userInfo
        };
        return data;
    },

    closeUserForm: function( button )
    {
        var tabPane = this.getCmsTabPanel();
        tabPane.getActiveTab().close();
    },

    initValue: function( field )
    {
        var formField = field.up( 'userFormField' );
        field.valueNotFoundText = formField.fieldValue;
        field.setValue( formField.fieldValue );
    },

    addNewTab: function( button, event )
    {
        var tabPanel = button.up( 'addressContainer' );
        var closable = tabPanel.down( 'addressColumn' ).items.getCount() != 0;
        var newTab = this.getEditUserFormPanel().generateAddressPanel( tabPanel.sourceField, closable );
        newTab = tabPanel.down( 'addressColumn' ).add( newTab );
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getEditUserFormPanel: function()
    {
        return Ext.ComponentQuery.query( 'editUserFormPanel' )[0];
    },

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    }

} );
