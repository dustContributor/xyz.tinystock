'use strict';

App.views.define(() => {
    const render = pars => {
      const els = App.utils.selectors.fromIds(pars)
      const dragComponentTemplate = kendo.template(els.templates.dragComponent.html())
      const fadeMs = 500
      const mainFade = kendo.fx(els.container).fadeIn(fadeMs)
      const nextFade = kendo.fx(els.nextContainer).fadeIn(fadeMs)

      const model = {};
      //
      // const dataSource = new kendo.data.DataSource({
      //   transport: App.utils.kendo.toTransport(App.urls.api.orderDetail),
      //   schema: {
      //     model: {
      //       id: 'componentId',
      //       fields: App.utils.kendo.toModelDefinition(['componentId', 'orderId', ['quantity', 'number', 1],
      //         ['quantity', 'number', 0]
      //       ])
      //     }
      //   }
      // })

      const split = els.split.kendoSplitter({
        orientation: 'vertical',
        panes: [{}, {}]
      }).getKendoSplitter()

      $('<button>').kendoButton({
        icon: 'check',
        click: () => {
          mainFade.reverse().then(() => {
            nextFade.play()
          })
        }
      }).appendTo(els.toolbar)

      $('<button>').kendoButton({
        icon: 'cancel',
        click: () => {
          if (window.confirm('Do you also want to delete the order?')) {
            $.ajax({
              method: 'DELETE',
              data: JSON.stringify({
                id: model.order.id
              })
            })
          }
          // Dont leave the object lingering
          model.order = null
          // Go back to the previous view
          mainFade.reverse().then(() => {
            nextFade.play()
          })
        }
      }).appendTo(els.toolbar)

      const detailData = {
        data: () => ({
          orderId: model.order.id
        })
      }

      const detail = els.detail.jsGrid({
        autoload: false,
        editing: true,
        inserting: true,
        width: '100%',
        controller: App.utils.jsGrid.toController(App.urls.api.orderDetail, {
          read: _.extend({
            isJson: false
          }, detailData),
          create: detailData,
          update: detailData,
          destroy: detailData
        }),
        fields: App.utils.jsGrid.toFields([
          ['orderId', 'number', '', {
            visible: false
          }],
          ['componentId', 'number', 'Component', {
            deferred: {
              insertEdit: element => {
                $(element).kendoComboBox({
                  dataTextField: 'code',
                  dataValueField: 'id',
                  filter: 'contains',
                  minLength: 3,
                  template: '#=code# - #=price/100#$ - #=description#',
                  dataSource: {
                    serverFiltering: true,
                    transport: {
                      read: {
                        url: App.urls.api.component.query,
                        data: v => {
                          // Just fetch the value directly, we need nothing else.
                          return {
                            code: v.filter.filters.length < 1 ? '' : v.filter.filters[0].value
                          }
                        }
                      }
                    }
                  }
                })
              }
            },
            itemTemplate: (item, value) => {
              debugger;
            },
            /* This has to be a function, otherwise 'this' doesn't gets bound. */
            insertTemplate: function(item, value) {
              return this.insertControl = this.editTemplate()
            },
            editTemplate: (a, b, c) => ($('<input name="componentId" style="width: 100%;">'))
          }],
          ['quantity', 'number', 'Quantity', {
            deferred: {
              insertEdit: el => {
                return $(el).kendoNumericTextBox({
                  min: 1,
                  value: 1,
                  format: 'n0'
                })
              }
            },
            insertTemplate: function(item, value) {
              return this.insertControl = this.editTemplate()
            },
            editTemplate: () => ($('<input name="quantity" style="width: 100%;">'))
          }], {
            type: 'control',
            width: 16
          }
        ])
      }).getJsGrid()

      const emptyFilter = function() {
        return this.filterControl = $('<input style="display: none;">')
      }

      const components = els.components.jsGrid({
        autoload: false,
        editing: false,
        inserting: false,
        filtering: true,
        sorting: true,
        width: '100%',
        // css: '',
        //headercss: 'k-grid-header',
        controller: App.utils.jsGrid.toController(App.urls.api.component.query, {
          read: {
            isJson: false,
            req: o => (
              $.ajax(o).then(items => (
                $.ajax({
                  method: 'GET',
                  traditional: true,
                  url: App.urls.api.stock,
                  data: {
                    ids: _.map(items, v => (v.id))
                  }
                }).then(stocks => {
                  const stocksById = _.indexBy(stocks, 'componentId')
                  for (let i = items.length; i-- > 0;) {
                    const item = items[i]
                    const stock = stocksById[item.id]
                    item.quantity = stock ? stock.quantity : 0
                  }
                  return items
                })
              ))
            )
          }
        }),
        fields: App.utils.jsGrid.toFields([
          ['id', 'number', 'Id', {
            editing: false
          }],
          ['code', 'text', 'Code'],
          ['price', 'number', 'Price', {
            filterTemplate: emptyFilter
          }],
          ['quantity', 'number', 'Available', {
            filterTemplate: emptyFilter
          }],
          ['description', 'text', 'Description', {
            filterTemplate: emptyFilter
          }]
        ])
      }).getJsGrid()

      const drag = els.components.kendoDraggable({
        /*
         * Start the drag at the cell level, otherwise
         * the 'hint' element appears far off the pointer.
         * Filtering by jsgrid body so not to grab the
         * headers too.
         */
        filter: '.jsgrid-grid-body td',
        group: 'createOrderComponentAdd',
        hint: e => {
          const item = components.itemByRow(e.parent('tr'))
          return $(dragComponentTemplate(item))
        }
      }).getKendoDraggable()

      const addComponentModel = kendo.observable({
        quantity: 0
      })

      kendo.bind(els.confirmAddComponent, addComponentModel)

      const confirmAddComponent = els.confirmAddComponent.kendoWindow({
        title: 'How many?',
        modal: true,
        resizable: false
      }).getKendoWindow()

      const drop = els.detail.kendoDropTarget({
        group: drag.options.group,
        drop: e => {
          const item = components.itemByRow(e.draggable.currentTarget.parent('tr'))
          if(item.quantity < 1) {
            alert('There is no stock of this component!')
            return
          }
          const input = confirmAddComponent.element.find('[name=quantity]').getKendoNumericTextBox()
          // Initial quantity is always the minimum.
          addComponentModel.set('quantity', input.min())
          // Maximum quantity is whatever stock is left for the item.
          input.max(item.quantity)
          // Center the window and ask the user how many of the items they want.
          confirmAddComponent.center().open()
        }
      }).getKendoDropTarget()

      const resizeSplit = split.resize.bind(split)

      const show = m => {
        // While we fade out the owner, we issue a create on the order.
        $.when($.ajax({
            url: App.urls.api.order,
            method: 'POST',
            contentType: App.utils.ajax.contentTypes.json,
            data: JSON.stringify({
              customerId: m.customer.id
            })
          }), nextFade.reverse())
          .then(data => {
            model.order = data[0]
            // We hit resize on the splitter after each load otherwise it doesn't shows correctly.
            $.when(detail.loadData(), components.loadData()).then(resizeSplit)
            mainFade.play()
          })
      }

      return {
        show: show
      }
    }
    return {
      order: {
        create: {
          render: render
        }
      }
    }
  },
  'order', 'create', 'render')
//# sourceURL=/app/views/order/create.js
