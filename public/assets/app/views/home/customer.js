App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    els.orderCreateContainer.hide()

    const showCreateOrder = pars.createOrder.show

    const dspars = {
      transport: App.utils.kendo.toTransport(App.urls.api.customer),
      schema: {
        model: {
          id: "id",
          fields: App.utils.kendo.toModelDefinition([
            ["id", "number"],
            ["name", "string"],
            ["lastName", "string"],
            ["address", "string"],
            ["email", "string"],
            ["phone", "string"]
          ])
        }
      }
    };
    const ds = new kendo.data.DataSource(dspars);

    els.customers.jsGrid({
      autoload: true,
      editing: true,
      inserting: true,
      width: "100%",
      // css: "",
      //headercss: "k-grid-header",
      controller: App.utils.jsGrid.toController(App.urls.api.customer),
      fields: App.utils.jsGrid.toFields([
        ["id", "number", "Id", {
          editing: false
        }],
        ["name", "text", "Name"],
        ["lastName", "text", "Last Name"],
        ["address", "text", "Address"],
        ["email", "text", "Email"],
        ["phone", "text", "Phone"],
        ["dummy", "", "", {
          editTemplate: $.noop,
          itemTemplate: function(value, item) {
            const grid = this._grid
            return $('<div title="New order">').kendoButton({
              icon: "track-changes-enable",
              click: e => {
                // Prefent default edit on click
                e.event.stopPropagation()
                // Find the selected customer and pass it to the next view
                showCreateOrder({
                  customer: grid.itemByRow(e.sender.wrapper.closest('tr'))
                })
              }
            })
          }
        }], {
          width: 48,
          type: "control"
        }
      ])
    })
  }

  return {
    customer: {
      render: render
    }
  }
}, "customer", "render")
//# sourceURL=/app/views/customer.js
