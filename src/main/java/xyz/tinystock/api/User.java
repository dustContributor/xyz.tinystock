package xyz.tinystock.api;

import static xyz.tinystock.database.tables.User.USER;
import static xyz.tinystock.utils.OpsDb.dslContext;
import static xyz.tinystock.utils.OpsHttp.badRequest;
import static xyz.tinystock.utils.OpsHttp.ok;

import org.apache.commons.lang3.StringUtils;
import org.jooby.Jooby;
import org.jooq.DSLContext;

import xyz.tinystock.utils.OpsAuth;

public final class User
{
	public static class Shared
	{
		public long id;
		public String name;

		public Shared ()
		{
			// Empty.
		}

		public Shared ( org.jooq.Record r )
		{
			id = r.get( USER.ID ).longValue();
			name = r.get( USER.NAME );
		}
	}

	public static final class Get extends Shared
	{
		// Empty.

		public Get ( org.jooq.Record r )
		{
			super( r );
		}
	}

	public static final Jooby register ( Jooby app )
	{
		app.path( "api/user", () ->
		{
			app.post( req ->
			{
				String name = req.param( "name" ).value();
				String pass = req.param( "pass" ).value();

				name = StringUtils.trim( name );
				pass = StringUtils.trim( pass );

				if ( StringUtils.isBlank( name )
						|| StringUtils.isBlank( pass )
						|| pass.length() != OpsAuth.EXPECTED_PASSWORD_LENGTH )
				{
					return badRequest();
				}

				try ( DSLContext db = dslContext( req ) )
				{
					final byte[] salt = OpsAuth.makeSalt();
					final byte[] saltedPass = OpsAuth.saltPass( pass, salt );

					final int mod = db.insertInto( USER, USER.NAME, USER.PASS, USER.SALT )
							.values( name, saltedPass, salt )
							.execute();

					return mod == 1 ? ok() : badRequest();
				}
			} );
		} );
		return app;
	}
}
