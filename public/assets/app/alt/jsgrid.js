(() => {
  /* We add a convenience getJsGrid method to all jQuery objects, like Kendo's. */
  $.fn['getJsGrid'] = function() {
    return this.data('JSGrid')
  }
  /*
   * This is to add a counterpart to 'rowByItem'
   */
  jsGrid.Grid.prototype.itemByRow = function(element) {
    return element ? (element.jquery ? element : $(element)).data('JSGridItem') : null
  }
  /*
   * This here is to allow to specify Kendo buttons for various of the jsGrid's actions.
   */
  const _createGridButton = jsGrid.ControlField.prototype._createGridButton
  jsGrid.ControlField.prototype._createGridButton = function(cls, tooltip, clickHandler) {
    const grid = this._grid;
    if (_.isObject(cls)) {
      const opts = _.extend({
        attr: {
          title: tooltip
        },
        click: function(e) {
          clickHandler(grid, e.event, e)
        }
      }, cls)
      return $('<div title="' + (opts.title || '') + '" >').kendoButton(opts)
    }
    return _createGridButton.call(this, cls, tooltip, clickHandler)
  }
  const kendoButtonsByName = {}

  const defineKendoButton = (name, title, icon) => {
    kendoButtonsByName[name] = {
      icon: icon || name,
      title: title
    }
  }

  defineKendoButton('edit', 'Edit')
  defineKendoButton('delete', 'Delete')
  defineKendoButton('search', 'Search')
  defineKendoButton('insert', 'Add', 'add')
  defineKendoButton('update', 'Update', 'check')
  defineKendoButton('cancelEdit', 'Cancel', 'cancel')

  for (const field of Object.keys(jsGrid.ControlField.prototype)) {
    const i = field.indexOf('ButtonClass')
    if (i < 0) {
      continue
    }
    const replace = kendoButtonsByName[field.substring(0, i)]
    if (replace) {
      jsGrid.ControlField.prototype[field] = replace
    }
  }

  /*
   * This is to override the default row templates where the widgets are added
   * to the DOM after being initialized, whereas for some Kendo widgets (ie, AutoComplete),
   * we need the element to be added first to the DOM and then initialized.
   */
  const default_renderGrid = jsGrid.Grid.prototype._renderGrid
  const default_editRow = jsGrid.Grid.prototype._editRow
  const default_clearInsert = jsGrid.Grid.prototype.clearInsert

  jsGrid.Grid.prototype._renderGrid = function() {
    // Call default jsGrid's rendering
    default_renderGrid.call(this);
    // Now call deferred initializers
    for (const field of this.fields) {
      if (!_.isObject(field.deferred)) {
        continue
      }
      const handler = field.deferred.insert || field.deferred.insertEdit;
      if (!_.isFunction(handler)) {
        continue
      }
      const selector = '.jsgrid-insert-row [name=' + field.name + ']';
      handler(this._container[0].querySelector(selector), field)
    }
  }

  const initDeferredFields = (fields, row, type) => {
    for (const field of fields) {
      if (!_.isObject(field.deferred)) {
        continue
      }
      const handler = field.deferred.edit || field.deferred.insert || field.deferred.insertEdit;
      if (!_.isFunction(handler)) {
        continue
      }
      const selector = '[name=' + field.name + ']';
      handler(row.querySelector(selector), field)
    }
  }

  jsGrid.Grid.prototype._editRow = function($row) {
    // Call default jsGrid's edit row rendering
    default_editRow.call(this, $row)
    /*
     * jsGrid hides the row and inserts an editing mode
     * row right after, we need that one.
     */
    const editRow = $row.prev()[0]
    // Now call deferred initializers
    initDeferredFields(this.fields, editRow)
  }
  /*
   * JsGrid re-creates the insert row after an insert, so we need to
   * re-create Kendo any kendo widget present.
   */
  jsGrid.Grid.prototype.clearInsert = function() {
    default_clearInsert.call(this)
    // Now call deferred initializers
    initDeferredFields(this.fields, this._insertRow[0])
  }
})()
