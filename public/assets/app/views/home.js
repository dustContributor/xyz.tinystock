App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    const hideAllBut = (uid, ids) => {
      for (const item of ids) {
        if (item !== uid) {
          $("#" + item).hide()
        }
      }
      $("#" + uid).show()
    }

    const itemOf = (url, title, uid) => {
      return {
        text: title,
        attr: {
          class: "ts-lv1",
          "data-uid": uid || kendo.guid(),
          "data-content-url": url
        }
      }
    }

    const firstItemUid = kendo.guid()

    const menu = els.mainMenu.kendoMenu({
      extra: new Set(),
      orientation: "vertical",
      dataSource: [
        itemOf(App.urls.content.customer, "Customers", firstItemUid),
        itemOf(App.urls.content.order, "Orders"),
        itemOf(App.urls.content.component, "Components"),
        itemOf(App.urls.content.test, "Test")
      ],
      select: e => {
        const url = e.item.getAttribute("data-content-url")
        const uid = e.item.getAttribute("data-uid")
        if (!url || !uid) {
          return
        }

        const extra = e.sender.options.extra;

        if (extra.has(uid)) {
          hideAllBut(uid, extra)
          return
        }

        $.get(url).then(html => {
          extra.add(uid)
          $("<div>")
            .hide()
            .attr("id", uid)
            .addClass("ts-section")
            .html(html)
            .appendTo(els.mainContainer)
          hideAllBut(uid, extra)
        });
      }
    }).getKendoMenu()
    // Simulate an item select event so it loads the first page.
    menu.trigger("select", {
      item: $("[data-uid='" + firstItemUid + "']")[0],
      sender: menu
    })
  }

  return {
    home: {
      render: render
    }
  }
}, "home", "render")
