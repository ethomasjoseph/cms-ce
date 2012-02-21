var summaryData =
{
    expanded: true,
    children: [
        {
            field: "Display Name",
            value: "Thomas Sigdestad",
            changeType: 'added',
            leaf: true
        },
        {
            field:"1. Profile",
            expanded:true,
            children: [
                {
                    field:"First Name",
                    value:"Thomas",
                    changeType: 'added',
                    leaf:true
                },
                {
                    field:"Middle Name",
                    changeType: 'removed',
                    leaf:true
                },
                {
                    field:"Last Name",
                    value:"Sigdestad",
                    changeType: 'modified',
                    leaf:true
                },
                {
                    field:"Organization",
                    value:"Enonic",
                    changeType: 'modified',
                    leaf:true
                }
            ]
        },
        {
            field: "2. User",
            expanded: true,
            children: [
                {
                    field: "Password",
                    value: "********",
                    changeType: 'modified',
                    leaf: true
                }
            ]
        },
        {
            field: "3. Places",
            expanded: true,
            children: [
                {
                    field: "Address",
                    expanded: true,
                    changeType: 'modified',
                    children: [
                        {
                            field: "Label",
                            value: "Home",
                            leaf: true
                        }
                    ]
                },
                {
                    field: "Address",
                    expanded: true,
                    changeType: 'modified',
                    children: [
                        {
                            field: "Region",
                            value: "",
                            changeType: 'removed',
                            leaf: true
                        }
                    ]
                }

            ]
        },
        {
            field: "4. Memberships",
            expanded: true,
            children: [
                {
                    field: "Member Of",
                    value: "3 Accounts (2 added, 1 removed)",
                    changeType: 'modified',
                    leaf: true
                }
            ]
        }
    ]
};