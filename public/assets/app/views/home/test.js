App.views.define(() => {
  const render = pars => {
    const els = App.utils.selectors.fromIds(pars)

    $("#blah").kendoAutoComplete({
      dataTextField: "code",
      dataValueField: "code",
      filter: "contains",
      minLength: 3,
      dataSource: {
        serverFiltering: true,
        transport: {
          read: {
            url: App.urls.api.component.query,
            data: v => {
              // Just fetch the value directly, we need nothing else.
              return {
                code: v.filter.filters[0].value
              }
            }
          }
        }
      }
    })
  }
  return {
    test: {
      render: render
    }
  }
}, "test", "render")
//# sourceURL=/app/views/test.js
