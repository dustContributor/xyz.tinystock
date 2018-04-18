App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)
    // const dspars = {
    //   transport: App.utils.kendo.toApiTransport(App.urls.api.component),
    //   schema: {
    //     model: {
    //       id: "id",
    //       fields: App.utils.kendo.toModelDefinition([
    //         ["id", "number"],
    //         ["code", "string"],
    //         ["description", "string"]
    //       ])
    //     }
    //   }
    // };
    // const ds = new kendo.data.DataSource(dspars);

    els.container.jsGrid({
      autoload: true,
      editing: true,
      inserting: true,
      width: "100%",
      // css: "",
      //headercss: "k-grid-header",
      controller: App.utils.jsGrid.toController(App.urls.api.component),
      fields: App.utils.jsGrid.toFields([
        ["id", "number", "Id", {
          editing: false
        }],
        ["code", "text", "Code"],
        ["price", "number", "Price", {
          deferred: {
            insertEdit: element => {
              return $(element).kendoNumericTextBox()
            }
          },
          insertTemplate: function(item, value) {
            return this.insertControl = $("<input name='price' style='width: 100%;'>")
          },
          editTemplate: () => {
            return this.editControl = $("<input name='price' style='width: 100%;'>")
          }
        }],
        ["description", "text", "Description"], {
          width: 32,
          type: "control"
        }
      ])
    })
  }

  return {
    component: {
      render: render
    }
  }
}, "component", "render")
//# sourceURL=/app/views/component.js
