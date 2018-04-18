package xyz.tinystock.utils;

import java.util.Locale;

public final class OpsString
{
	private OpsString ()
	{
		// Empty.
	}

	public static final String toUpper ( String value )
	{
		return value.toUpperCase( Locale.ROOT );
	}

	public static final String toLower ( String value )
	{
		return value.toLowerCase( Locale.ROOT );
	}
}
