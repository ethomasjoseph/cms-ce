Ext.define('App.view.SummaryTreeGrid', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.summaryTreeGrid',
    layout: 'fit',
    initComponent: function() {
        this.store = Ext.create('Ext.data.TreeStore', {
            proxy: {
                type: 'ajax',
                url: 'summary.json',
            },
            reader: {
                type: 'json',
                root: 'children'
            },
            folderSort: false,
            autoLoad: false,
            remoteSort: false,
            sortOnLoad: true,

            fields:[
                {name: 'field', type: 'string'},
                {name: 'value', type: 'string'},
                {name: 'changeType', type: 'string'}
            ]
        });
        this.bodyCls = 'admin-summary-tree-grid';
        this.border = false;
        this.height = 400;
        this.useArrows = true;
        this.rootVisible = false;
        this.multiSelect = false;
        this.singleExpand = false;
        this.columns = [
            {
                xtype: 'treecolumn',
                text: 'Field',
                flex: 1,
                sortable: true,
                dataIndex: 'field'
            },
            {
                text: 'Value',
                flex: 5,
                dataIndex: 'value',
                sortable: true
            }
        ];

        this.viewConfig = {
            getRowClass: function(record, index) {
                return record.get('changeType');
            }
        };

        this.callParent(arguments);
    }

});
