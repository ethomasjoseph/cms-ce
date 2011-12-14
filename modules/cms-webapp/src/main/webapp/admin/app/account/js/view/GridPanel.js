Ext.define( 'App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.accountGrid',

    requires: ['Common.PersistentGridSelectionPlugin', 'Common.SlidingPagerPlugin'],
    plugins: ['persistentGridSelection'],
    layout: 'fit',
    multiSelect: true,
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'AccountStore',

    initComponent: function()
    {
        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                flex: 1
            },
            {
                text: 'Local Name',
                dataIndex: 'name',
                sortable: true
            },
            {
                text: 'User Store',
                dataIndex: 'userStore',
                sortable: true
            },
            {
                text: 'Last Modified',
                //xtype: 'datecolumn',
                //format: 'Y-m-d h:m',
                dataIndex: 'lastModified',
                renderer: this.prettyDateRenderer,
                sortable: true
            }
        ];

        this.tbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            plugins: ['slidingPagerPlugin']
        },

                this.viewConfig = {
                    trackOver : true,
                    stripeRows: true
                };

        this.selModel = Ext.create( 'Ext.selection.CheckboxModel', {
        } );

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        return Ext.String.format( Templates.account.gridPanelNameRenderer, record.data.key, value, record.data.name,
                                  record.data.userStore );
    },

    prettyDateRenderer: function( value, p, record )
    {
        try
        {
            if ( parent && Ext.isFunction( parent.humane_date ) )
            {
                return parent.humane_date( value );
            }
            else
            {
                return value;
            }
        }
        catch( e )
        {
            return value;
        }
    }
} );
