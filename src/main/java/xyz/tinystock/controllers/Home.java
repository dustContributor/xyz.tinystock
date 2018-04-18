package xyz.tinystock.controllers;

import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import xyz.tinystock.utils.HtmlUtil;

@Path( "/home" )
public final class Home extends Base
{

	public static final Jooby register ( Jooby dest )
	{
		dest.use( "/", req ->
		{
			return Results.html( "views/home" ).put( "html", HtmlUtil.of( req ) );
		} );
		dest.use( "/order", req ->
		{
			return Results.html( "views/home/order" ).put( "html", HtmlUtil.of( req ) );
		} );
		dest.use( "/customer", req ->
		{
			return Results.html( "views/home/customer" ).put( "html", HtmlUtil.of( req ) );
		} );
		dest.use( "/component", req ->
		{
			return Results.html( "views/home/component" ).put( "html", HtmlUtil.of( req ) );
		} );
		dest.use( "/test", req ->
		{
			return Results.html( "views/home/test" ).put( "html", HtmlUtil.of( req ) );
		} );
		dest.use( Home.class );
		return dest;
	}

	@Inject
	public Home ( Config cfg )
	{
		super( cfg );
	}
}
