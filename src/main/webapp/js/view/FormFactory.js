//function FormFactory() {

/**
 * Builds a form panel for Mine filters
 * @param id to specify the id of this formpanel instance
 * @param serviceUrl the service url for submit
 */
function buildMineFilterForm(id, loadUrl, submitUrl, serviceUrl, successFunction, preSubmitFunction) {
    var mineNamesStore = new Ext.data.Store({
        baseParams: {serviceUrl: serviceUrl},
        proxy: new Ext.data.HttpProxy({url: '/getMineNames.do'}),
        reader: new Ext.data.JsonReader({
            root:'data'
        }, [{name:'mineDisplayName', mapping:'mineDisplayName'}])
    });

    var thePanel = new Ext.FormPanel({
        //region: "center",
        //collapsible: true,
        //title: "Filter Properties",
        url: loadUrl,
        id: id,
        border: false,
        autoScroll:true,
        hideMode:'offsets',
        width: '100%',
        buttonAlign: 'right',
        labelAlign: 'right',
        //labelWidth: 60,

        items: [{
            xtype:'fieldset',
            title: 'Mine Filter Properties',

            autoHeight:true,
            items :[new Ext.form.ComboBox({
                anchor: '100%',
                autoWidth: true,
                name: 'mineName',
                displayField:'mineDisplayName',
                editable: true,
                fieldLabel: 'Mine Name',
                forceSelection: true,
                listWidth: 300,            // 'auto' does not work in IE6
                mode: 'remote',
                selectOnFocus: true,
                store: mineNamesStore,
                triggerAction: 'all',
                typeAhead: true,
                valueField:'mineDisplayName',
                xtype: 'combo'
            })
            ]
        }],
        buttons: [{
            text: 'Show Me >>',
            handler: function() {
                preSubmitFunction();
                thePanel.getForm().submit({
                    url:submitUrl,
                    waitMsg:'Running query...',
                    params: {serviceUrl: serviceUrl},
                    success: successFunction,
                    failure: function(form, action) {
                        Ext.MessageBox.show({
                            title: 'Filter Failed',
                            msg: action.result.msg,
                            buttons: Ext.MessageBox.OK,
                            animEl: 'mb9',
                            icon: Ext.MessageBox.ERROR
                        });
                    }
                });
            }
        }]
    });
    return thePanel;
}
;

/**
 * Builds a form panel for Mining Activity filters
 * @param id to specify the id of this formpanel instance
 * @param serviceUrl the service url for submit
 */
function buildMiningActivityFilterForm(id, loadUrl, submitUrl, serviceUrl, successFunction, preSubmitFunction) {
    var mineNamesStore = new Ext.data.Store({
        baseParams: {serviceUrl: serviceUrl},
        proxy: new Ext.data.HttpProxy({url: '/getMineNames.do'}),
        reader: new Ext.data.JsonReader({
            root:'data'
        }, [{name:'mineDisplayName', mapping:'mineDisplayName'}])
    });

    var thePanel = new Ext.FormPanel({
        //region: "center",
        //collapsible: true,
        //title: "Filter Properties",
        url: loadUrl,
        id: id,
        border: false,
        autoScroll:true,
        hideMode:'offsets',
        width: '100%',
        buttonAlign: 'right',
        labelAlign: 'right',
        labelWidth: 140,

        items: [{
            xtype:'fieldset',
            title: 'Mining Activity Filter Properties',

            autoHeight:true,

            defaultType: 'datefield',

            items :[new Ext.form.ComboBox({
                anchor: '100%',
                fieldLabel: 'Mine Name',
                name: 'mineName',
                typeAhead: true,
                forceSelection: true,
                mode: 'remote',
                triggerAction: 'all',
                selectOnFocus: true,
                editable: true,
                xtype: 'combo',
                store: mineNamesStore,
                displayField:'mineDisplayName',
                valueField:'mineDisplayName'
            }),{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Produced Material Name',
                name: 'producedMaterial'
            },{
                anchor: '100%',
                fieldLabel: 'Mining Activity Start Date',
                name: 'startDate',
                format: "d/M/Y",
                value: ''
            }, {
                anchor: '100%',
                fieldLabel: 'Mining Activity End Date',
                name: 'endDate',
                format: "d/M/Y",
                value: ''
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Min. Ore Processed',
                name: 'oreProcessed'
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Grade',
                name: 'cutOffGrade'
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Min. Production Amount',
                name: 'production'
            }]
        }],
        buttons: [{
            text: 'Show Me >>',
            handler: function() {
                preSubmitFunction();
                thePanel.getForm().submit({
                    url:submitUrl,
                    waitMsg:'Running query...',
                    params: {serviceUrl: serviceUrl},
                    success: successFunction,
                    failure: function(form, action) {
                        Ext.MessageBox.show({
                            title: 'Filter Failed',
                            msg: action.result.msg,
                            buttons: Ext.MessageBox.OK,
                            animEl: 'mb9',
                            icon: Ext.MessageBox.ERROR
                        });
                    }
                });
            }
        }]
    });
    return thePanel;
}
;

/**
 * Builds a form panel for Mineral Occurrence filters
 * @param id to specify the id of this formpanel instance
 * @param serviceUrl the service url for submit
 */
function buildMineralOccurrenceFilterForm(id, loadUrl, submitUrl, serviceUrl, successFunction, preSubmitFunction) {
    var unitOfMeasureStore = new Ext.data.SimpleStore({
        fields: ['unitCode', 'unitDescription'],
        data: [
            ['CRT', 'Carats'],
            ['CUB M/HA', 'Cubic Metres/Hectare'],
            ['CUB M', 'Cubic Metres'],
            ['TONNE', 'Tonnes'],
            ['TONNE/M', 'Tonnes/M'],
            ['TONNE/100M', 'Tonne/100 metres'],
            ['GM/TONNE', 'Grams/Tonne'],
            ['KG/TONNE', 'Kilograms/Tonne'],
            ['MILL TONNE', 'Million Tonnes'],
            ['GM', 'Grams'],
            ['KG', 'Kilograms'],
            ['M', 'Metres'],
            ['%', 'Percentage'],
            ['UKN', 'Unknown'],
            ['SQ M', 'Square Metres'],
            ['MA', 'Million Years'],
            ['NOUNIT', 'Dimensionless Numeric Value'],
            ['PPM', 'Parts Per Million'],
            ['PPB', 'Parts Per Billion'],
            ['MM', 'Millimetres'],
            ['UM', 'Microns'],
            ['GCOUNT', 'Grain Count'],
            ['HA', 'Hectare'],
            ['MESH', 'Generic screen mesh designator'],
            ['SI', 'SI units magnetic susceptibility'],
            ['GM/CC', 'Grams per cubic centimetre'],
        ]        
    });

    var thePanel = new Ext.FormPanel({
        //region: "center",
        //collapsible: true,
        //title: "Filter Properties",
        url: loadUrl,
        id: id,
        border: false,
        autoScroll:true,
        hideMode:'offsets',
        width: '100%',
        buttonAlign: 'right',
        labelAlign: 'right',
        labelWidth: 140,

        items: [{
            xtype:'fieldset',
            title: 'Mineral Occurrence Filter Properties',
            autoHeight:true,
            anchor: '100%',


            //defaultType: 'textfield',

            items :[{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Commodity Name',
                name: 'commodityName'
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Commodity Group',
                name: 'commodityGroup'
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Measure Type',
                name: 'measureType'
            },{
                anchor: '100%',
                xtype: 'textfield',
                fieldLabel: 'Min. Ore Amount',
                name: 'minOreAmount'
            },  new Ext.form.ComboBox({
                tpl: '<tpl for="."><div ext:qtip="{unitCode}. {unitDescription}" class="x-combo-list-item">{unitCode}. {unitDescription}</div></tpl>',
                anchor: '100%',
                name: 'minOreAmountUOM',
                fieldLabel: 'Min. Ore Amount Unit',
                emptyText:'Select a Unit Of Measure...',
                forceSelection: true,
                mode: 'local',
                //selectOnFocus: true,
                store: unitOfMeasureStore,
                triggerAction: 'all',
                typeAhead: true,
                displayField:'unitCode',
                valueField:'unitCode'
            })
                ,{
                    anchor: '100%',
                    xtype: 'textfield',
                    fieldLabel: 'Min. Commodity Amount',
                    name: 'minCommodityAmount'
                },{
                    anchor: '100%',
                    xtype: 'textfield',
                    fieldLabel: 'Cut Off Grade',
                    name: 'minCutOffGrade'
                }]
        }]
        ,buttons: [{
            text: 'Show Me >>',
            handler: function() {
                preSubmitFunction();
                thePanel.getForm().submit({
                    url:submitUrl,
                    waitMsg:'Running query...',
                    params: {serviceUrl: serviceUrl},
                    success: successFunction,
                    failure: function(form, action) {
                        Ext.MessageBox.show({
                            title: 'Filter Failed',
                            msg: action.result.msg,
                            buttons: Ext.MessageBox.OK,
                            animEl: 'mb9',
                            icon: Ext.MessageBox.ERROR
                        });
                    }
                });
            }
        }]
    });
    return thePanel;
}
;

//}