package xyz.tinystock.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooby.Registry;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.typesafe.config.Config;

public final class OpsDb
{
	static
	{
		try
		{
			// This shouldn't be needed but whatever.
			Class.forName( "com.mysql.jdbc.Driver" );
		}
		catch ( ClassNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}

	private OpsDb ()
	{
		// Empty.
	}

	public static final DSLContext dslContext ( Registry reg )
	{
		return dslContext( reg.require( Config.class ) );
	}

	public static final DSLContext dslContext ( Config cfg )
	{
		return DSL.using( cfg.getString( "app.dbConnection" ) );
	}

	public static final Connection newConnection ( Config cfg ) throws SQLException
	{
		return DriverManager.getConnection( cfg.getString( "app.dbConnection" ) );
	}
}
