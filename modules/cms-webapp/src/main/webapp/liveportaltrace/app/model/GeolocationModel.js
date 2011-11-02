Ext.define( 'LPT.model.GeolocationModel', {
    extend: 'Ext.data.Model',

    fields: [
        'ipAddress',
        'latitude',
        'longitude',
        'city',
        'country'
    ],

    idProperty: 'ipAddress'
} );
