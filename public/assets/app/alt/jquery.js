(() => {
  /*
   * By default jQuery uses 'application/x-www-form-urlencoded; charset=UTF-8',
   * as content type. We override with 'application/json; charset=UTF-8' since
   * Jooby likes it better when receiving complex objects as request parameters.
   */
  // $.ajaxSetup({
  //   contentType: 'application/json;charset=UTF-8'
  // })
  /*
   * We catch all internal server errors, and *if* they return HTML,
   * which is probably a Whoops error page, we just show it. Useful for
   * debugging and we really don't care for it to be handled "gracefully"
   * since these errors should never occur anyway.
   */
  $(document).ajaxError((event, jqxhr, settings, thrownError) => {
    if (jqxhr.status != 500) {
      // Some other error we don't care about.
      return
    }
    const responseType = jqxhr.getResponseHeader('Content-Type')
    if (!responseType || !responseType.startsWith('text/html')) {
      // We expect to receive HTML to show, ignore anything else.
      return
    }
    // Replace everything with response.
    document.open();
    document.write(jqxhr.responseText);
    document.close();
  })
})()
