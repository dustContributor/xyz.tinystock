package xyz.tinystock.utils;

import org.jooby.Result;
import org.jooby.Status;

public final class OpsHttp
{
	private OpsHttp ()
	{
		// Empty.
	}

	private static final Result OK = new Result().status( Status.OK );
	private static final Result BAD_REQUEST = new Result().status( Status.BAD_REQUEST );
	private static final Result FORBIDDEN = new Result().status( Status.FORBIDDEN );
	private static final Result NOT_FOUND = new Result().status( Status.NOT_FOUND );

	public static final Result ok ()
	{
		return OK;
	}

	public static final Result badRequest ()
	{
		return BAD_REQUEST;
	}

	public static final Result forbidden ()
	{
		return FORBIDDEN;
	}

	public static final Result notFound ()
	{
		return NOT_FOUND;
	}
}
