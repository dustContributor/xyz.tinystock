package xyz.tinystock.utils;

import java.util.Objects;

import org.jooby.Request;

import com.typesafe.config.Config;

public final class HtmlUtil
{
	private final Request req;
	private final Config cfg;

	private HtmlUtil ( Request req, Config cfg )
	{
		this.req = req;
		this.cfg = cfg;
	}

	public static final HtmlUtil of ( Request req, Config cfg )
	{
		return new HtmlUtil( Objects.requireNonNull( req ), Objects.requireNonNull( cfg ) );
	}

	public static final HtmlUtil of ( Request req )
	{
		return of( Objects.requireNonNull( req ), req.require( Config.class ) );
	}

	public final String url ( String url )
	{
		return fullUrl( Objects.requireNonNull( url ) );
	}

	public final String api ( String url )
	{
		return fullUrl( "api/" + Objects.requireNonNull( url ) );
	}

	public final String view ( String url )
	{
		return fullUrl( "assets/app/views/" + Objects.requireNonNull( url ) );
	}

	public final String content ( String url )
	{
		return fullUrl( Objects.requireNonNull( url ) );
	}

	private final String fullUrl ( String url )
	{
		final String host = req.header( "Host" ).value();
		final String protocol = req.secure() ? "https://" : "http://";
		final String appName = cfg.hasPath( "app.urlName" ) ? cfg.getString( "app.urlName" ) + '/' : "";
		return protocol + host + '/' + appName + url;
	}
}