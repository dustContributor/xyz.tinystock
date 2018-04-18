package xyz.tinystock.api;

import static xyz.tinystock.database.tables.User.USER;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.apache.commons.lang3.StringUtils;
import org.jooby.Jooby;
import org.jooq.DSLContext;

import xyz.tinystock.database.tables.records.UserRecord;
import xyz.tinystock.utils.OpsAuth;
import xyz.tinystock.utils.OpsHttp;

public final class Auth
{
	private static final byte[] DUMMY_SALT;
	private static final byte[] DUMMY_PASS;

	static
	{
		DUMMY_SALT = OpsAuth.makeSalt();
		DUMMY_PASS = OpsAuth.saltPass( new Object().toString(), DUMMY_SALT );
	}

	private static boolean validate ( UserRecord user, String password )
	{
		final byte[] salt = user != null ? user.getSalt() : DUMMY_SALT;
		final byte[] pass = user != null ? user.getPass() : DUMMY_PASS;
		return OpsAuth.validate( password, salt, pass );
	}

	public static final Jooby register ( Jooby dest )
	{
		dest.use( "api/auth" )
				.post( req ->
				{
					final String name = req.param( "name" ).value();
					final String pass = req.param( "pass" ).value();

					if ( StringUtils.isBlank( name )
							|| StringUtils.isBlank( pass ) )
					{
						return OpsHttp.badRequest();
					}
					
					final UserRecord user;

					try ( DSLContext db = dslContext( req ) )
					{
						user = db.fetchOne( USER, USER.NAME.eq( name ) );
					}

					final boolean isValid = validate( user, pass );
					/*
					 * If the user exists but the password is invalid, we return not found
					 * anyway, to avoid disclosing the user exists. Still, hacker could
					 * derive this from timing the non-existent query versus existing
					 * query, since one could return faster than the other. In this case
					 * we run validate regardless of the existence of the user, the
					 * timings ought to be similar enough to avoid disclosing anything.
					 */
					return user != null && isValid ? OpsHttp.ok() : OpsHttp.notFound();
				} );

		return dest;
	}
}
