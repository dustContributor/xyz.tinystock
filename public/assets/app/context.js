window.App = $.extend(true, window.App || {}, (() => {
  const views = {
    define: (definition, ...args) => {
      if (!_.isFunction(definition)) {
        throw "Definition must be a function!";
      }
      if (App.utils.existsIn(App.views, args)) {
        return;
      }
      return $.extend(true, App.views, definition());
    }
  }

  const contentTypeJson = 'application/json;charset=UTF-8'
  const contentTypeForm = 'application/x-www-form-urlencoded;charset=UTF-8'

  const types = {
    define: (name, items) => {
      const byId = {}
      const byName = {}
      for (const item of items) {
        byId[item.id] = item
        byName[item.name] = item
      }
      items.byId = byId
      items.byName = byName
      App.types[name] = items
    },
    fetch: (name, url) => {
      if (!name || !url) {
        throw "name and url are needed!"
      }
      return $.get(url).success(data => {
        App.types.define(name, data)
      })
    }
  }

  const formatDate = (str, format) => {
    const value = _.isDate(str) ? str : !str ? new Date() : kendo.parseDate(str);
    return kendo.toString(value, format);
  };

  const utils = {
    ajax: {
      contentTypes: {
        json: contentTypeJson,
        form: contentTypeForm
      }
    },
    formats: {
      date: "dd/MM/yyyy",
      timeStamp: "HH:mm:ss",
      full: "yyyyMMddHHmmss"
    },
    selectors: {
      fromIds: selectors => {
        // We will be catching all properties ending with "Id"
        const suffix = "Id"
        const dest = {}
        // Will skip if selectors are empty or null.
        for (const [name, id] of Object.entries(selectors || dest)) {
          if (_.isObject(id)) {
            // Value is an object, try to select its properties.
            dest[name] = App.utils.selectors.fromIds(id)
            continue
          }
          if (!_.isString(id) || !name.endsWith(suffix)) {
            // Value is a function or some other string.
            continue
          }
          const element = $("#" + id)
          if (element.length < 1) {
            // Value is as expected, but no such element exists.
            continue
          }
          // Store and name it properly without the suffix.
          const elementName = name.substring(0, name.length - suffix.length)
          dest[elementName] = element
        }
        return dest
      }
    },
    jsGrid: {
      toController: (url, extra) => {
        const req = (method, extra) => {
          const ajaxOpts = {
            method: method,
            url: url
          }
          // Specific request issue function
          const request = (extra && _.isFunction(extra.req)) ? extra.req : o => $.ajax(o)
          // We issue JSON by default, and only disallow it
          const isJson = !extra || extra.isJson !== false
          if (isJson) {
            ajaxOpts.contentType = contentTypeJson
          }
          if (extra && extra.ajax) {
            _.extend(ajaxOpts, extra.ajax)
          }
          let extraData = null;
          if (extra && extra.data) {
            if (_.isFunction(extra.data)) {
              extraData = extra.data
            } else {
              const data = extra.data
              extraData = () => (data)
            }
          }
          return data => {
            if (extraData) {
              data = _.extend({}, data, extraData(data))
            }
            ajaxOpts.data = isJson ? JSON.stringify(data) : data
            return request(ajaxOpts)
          }
        }

        return {
          loadData: req("GET", extra ? extra.read : null),
          insertItem: req("POST", extra ? extra.create : null),
          updateItem: req("PUT", extra ? extra.update : null),
          deleteItem: req("DELETE", extra ? extra.destroy : null)
        }
      },
      toFields: (fields) => {
        const dest = []
        for (const field of fields) {
          // By default we assign as-is
          let res = field
          if (_.isString(field)) {
            // Name only
            res = {
              name: field
            }
          } else if (_.isArray(field)) {
            // Decompose array
            res = {
              name: field[0],
              type: field[1],
              title: field[2]
            }
            if (_.isObject(field[3])) {
              // Fourth item are overrides
              _.extend(res, field[3])
            }
          }
          dest.push(res)
        }
        return dest
      }
    },
    kendo: {
      format: {
        date: str => {
          return formatDate(str, App.utils.formats.date);
        },
        dateTime: str => {
          const formats = App.utils.formats;
          return formatDate(str, formats.date + " " + formats.timeStamp);
        },
        timeStamp: str => {
          return formatDate(str, App.utils.formats.timeStamp);
        },
        full: str => {
          return formatDate(str, App.utils.formats.full);
        }
      },
      toTransport: (rootUrl, overrides) => {
        const on = m => {
          return {
            url: rootUrl,
            method: m
          }
        };
        // Api operations are based on HTTP methods.
        const methods = {
          read: on("GET"),
          create: on("POST"),
          update: on("PUT"),
          destroy: on("DELETE")
        };
        return _.isObject(overrides) ?
          $.extend(true, methods, overrides) : methods;
      },
      toModelDefinition: fields => {
        return _.indexBy(_.map(fields, fieldDef => {
          const dest = {
            name: fieldDef,
            editable: false
          };
          if (_.isString(fieldDef)) {
            // Simplest case, non editable, passed name.
            return dest;
          }
          if (_.isArray(fieldDef)) {
            // Mid case, first is name, second is type.
            dest.name = fieldDef[0];
            dest.type = fieldDef[1];
            if (fieldDef.length > 2) {
              // Third can be field's default value.
              dest.defaultValue = fieldDef[2];
              // In which case it's editable.
              dest.editable = true;
            }
            if (fieldDef.length > 3) {
              // Fourth can be the validators.
              dest.validation = fieldDef[3];
            }
            return dest;
          }
          // Object passed, assume non-editable by default.
          return _.extend({
            editable: false,
          }, fieldDef);
        }), "name");
      },
      typeEditorAppender: (types, overrides) => {
        const options = {
          dataValueField: "id",
          dataTextField: "publicName",
          dataSource: {
            data: types
          }
        };
        // Based on kendo examples of custom editors.
        return (container, pars) => {
          return $('<input name="' + pars.field + '"/>')
            .appendTo(container)
            .kendoDropDownList(_.isObject(overrides) ?
              $.extend(true, options, overrides) : options);
        };
      }
    },
    existsIn: (src, ...args) => {
      for (let i = 0; i < args.length; ++i) {
        if (!src || !src.hasOwnProperty(args[i])) {
          return false;
        }
        src = src[args[i]];
      }
      return true;
    },
    uuidv4: () => {
      // https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
      return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16))
    }
  }

  return {
    types: types,
    views: views,
    utils: utils
  }
})())
