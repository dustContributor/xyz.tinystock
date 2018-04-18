App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    els.orderDetail.jsGrid({
      autoload: true,
      editing: true,
      inserting: true,
      width: "100%",
      // css: "",
      //headercss: "k-grid-header",
      controller: App.utils.jsGrid.toController(App.urls.api.order),
      fields: App.utils.jsGrid.toFields([
        ["orderId", "number", "Order Id"],
        ["componentId", "number", "Component Id"],
        ["quantity", "number", "Quantity"],
        ["totalPrice", "number", "Total Price"],
        ["", "", "", {
          itemTemplate: (value, item) => {
            return $('<div>').kendoButton({
              icon: "zoom",
              click: e => {
                // Prefent default edit on click
                e.event.stopPropagation()
                // Ensure second effect is played after first is done.
                mainFade.reverse().then(() => {
                  detailFade.play()
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
          type: "control"
        }
      ])
    })
  }
  return {
    order: {
      detail: {
        render: render
      }
    }
  }
}, "order", "render")
//# sourceURL=/app/views/order/detail.js
