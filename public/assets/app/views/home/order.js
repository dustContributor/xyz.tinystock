App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    const fadeMs = 500
    const mainFade = kendo.fx(els.container).fadeIn(fadeMs)
    const nextFade = kendo.fx(els.orderCreateContainer).fadeIn(fadeMs)

    els.orders.jsGrid({
      autoload: true,
      editing: true,
      filtering: false,
      width: "100%",
      // css: "",
      //headercss: "k-grid-header",
      controller: App.utils.jsGrid.toController(App.urls.api.order),
      fields: App.utils.jsGrid.toFields([
        ["id", "number", "Id", {
          editing: false
        }],
        ["customerId", "number", "Customer Id", {
          editing: false
        }],
        ["stateId", "number", "State Id", {
          itemTemplate: (value, item) => {
            return App.types.orderStates.byId[value].publicName
          }
        }],
        ["", "", "", {
          itemTemplate: (value, item) => {
            return $('<div>').kendoButton({
              icon: "zoom",
              click: e => {
                // Prefent default edit on click
                e.event.stopPropagation()
                // Ensure second effect is played after first is done.
                mainFade.reverse().then(() => {
                  nextFade.play()
                })
                // $('<div>ASDASD</div>')
                // .appendTo($(document.body))
                // .kendoWindow({
                //   width: 100
                // })
                // .getKendoWindow()
                // .center()
                // .open()
              }
            })
          }
        }], {
          width: 32,
          type: "control",
          modeSwitchButton: false,
          headerTemplate: function() {
            return $("<button>")
              .attr("type", "button")
              .addClass("jsgrid-button jsgrid-mode-button jsgrid-insert-mode-button")
              .on("click", function() {
                // Ensure second effect is played after first is done.
                mainFade.reverse().then(() => {
                  nextFade.play()
                })
              })
          }
        }
      ])
    })
  }

  return {
    order: {
      render: render
    }
  }
}, "order", "render")
//# sourceURL=/app/views/order.js
