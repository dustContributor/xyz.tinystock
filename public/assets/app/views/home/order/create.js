'use strict';

App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    const fadeMs = 500
    const mainFade = kendo.fx(els.container).fadeIn(fadeMs)
    const nextFade = kendo.fx(els.nextContainer).fadeIn(fadeMs)

    const model = {};

    $('<button>').kendoButton({
      icon: "check",
      click: () => {
        mainFade.reverse().then(() => {
          nextFade.play()
        })
      }
    }).appendTo(els.toolbar)

    $('<button>').kendoButton({
      icon: "cancel",
      click: () => {
        if (window.confirm("Do you also want to delete the order?")) {
          $.ajax({
            method: "DELETE",
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

    const componentsData = {
      data: () => ({
        orderId: model.order.id
      })
    }

    const components = els.components.jsGrid({
      autoload: false,
      editing: true,
      inserting: true,
      width: "100%",
      // css: "",
      //headercss: "k-grid-header",
      controller: App.utils.jsGrid.toController(App.urls.api.orderDetail, {
        read: _.extend({
          isJson: false
        }, componentsData),
        create: componentsData,
        update: componentsData,
        destroy: componentsData
      }),
      fields: App.utils.jsGrid.toFields([
        ["orderId", "number", "", {
          visible: false
        }],
        ["componentId", "number", "Component", {
          deferred: {
            insert: element => {
              $(element).kendoComboBox({
                dataTextField: "code",
                dataValueField: "id",
                filter: "contains",
                minLength: 3,
                template: "#=code# - #=price/100#$ - #=description#",
                dataSource: {
                  serverFiltering: true,
                  transport: {
                    read: {
                      url: App.urls.api.component.query,
                      data: v => {
                        // Just fetch the value directly, we need nothing else.
                        return {
                          code: v.filter.filters.length < 1 ? "" : v.filter.filters[0].value
                        }
                      }
                    }
                  }
                }
              })
            }
          },
          // itemTemplate: (item, value) => {},
          /* This has to be a function, otherwise 'this' doesn't gets bound. */
          insertTemplate: function(item, value) {
            return this.insertControl = $("<input name='componentId' style='width: 100%;'>")
          }
        }],
        ["quantity", "number", "Quantity", {
          deferred: {
            insertEdit: element => {
              return $(element).kendoNumericTextBox({
                min: 1,
                value: 1,
                format: "n0"
              })
            }
          },
          insertTemplate: function(item, value) {
            return this.insertControl = $("<input name='quantity' style='width: 100%;'>")
          },
          editTemplate: () => {
            return $("<input name='quantity' style='width: 100%;'>")
          }
        }], {
          type: "control",
          width: 16
        }
      ])
    }).data("JSGrid")

    const show = m => {
      // While we fade out the owner, we issue a create on the order.
      $.when($.ajax({
          url: App.urls.api.order,
          method: "POST",
          contentType: App.utils.ajax.contentTypes.json,
          data: JSON.stringify({
            customerId: m.customer.id
          })
        }), nextFade.reverse())
        .then(data => {
          model.order = data[0]
          components.loadData()
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
}, "order", "create", "render")
//# sourceURL=/app/views/order/create.js
