package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Customer.CUSTOMER;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.jooby.Jooby;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import xyz.tinystock.utils.OpsHttp;

public final class Customer
{
	private static class Shared
	{
		public String name;
		public String lastName;
		public String address;
		public String phone;
		public String email;

		public Shared ()
		{
			// Empty.
		}

		public Shared ( org.jooq.Record r )
		{
			name = r.get( CUSTOMER.NAME );
			lastName = r.get( CUSTOMER.LAST_NAME );
			address = r.get( CUSTOMER.ADDRESS );
			phone = r.get( CUSTOMER.PHONE );
			email = r.get( CUSTOMER.EMAIL );
		}
	}

	public static class Get extends Shared
	{
		public long id;

		public Get ( Record r )
		{
			super( r );
			id = r.get( CUSTOMER.ID ).longValue();
		}
	}

	public static class Put extends Shared
	{
		public long id;

		public Put ()
		{
			// Default for de-serialization.
		}
	}

	public static class Post extends Shared
	{
		public Post ()
		{
			// Default for de-serialization.
		}
		
		
	}

	public static class Delete
	{
		public long id;
	}

	public static final Jooby register ( Jooby app )
	{
		app.use( "api/customer" )
				.get( req ->
				{
					final long id = req.param( "id" ).longValue( 0 );

					try ( DSLContext db = dslContext( req ) )
					{
						return db
								.selectFrom( CUSTOMER )
								/*
								 * If an id was passed, add a where clause, otherwise fetch all
								 * rows.
								 */
								.where( id > 0 ? CUSTOMER.ID.eq( UInteger.valueOf( id ) ) : DSL.trueCondition() )
								.fetch()
								.map( Get::new );
					}
				} )
				.post( req ->
				{
					final Post model = req.body( Post.class );

					try ( DSLContext db = dslContext( req ) )
					{
						// Id is auto generated database-side, fetch after insert.
						return db.insertInto( CUSTOMER,
								CUSTOMER.NAME,
								CUSTOMER.LAST_NAME,
								CUSTOMER.ADDRESS,
								CUSTOMER.PHONE,
								CUSTOMER.EMAIL )
								.values( model.name, model.lastName, model.address, model.phone, model.email )
								// Return field by field or all of them together.
								.returning( CUSTOMER.fields() )
								.fetchOne()
								.map( Get::new );
					}
				} )
				.put( req ->
				{
					final Put model = req.body( Put.class );
					final UInteger id = UInteger.valueOf( model.id );

					try ( DSLContext db = dslContext( req ) )
					{
						final int mod = db.update( CUSTOMER )
								/*
								 * Can't use set(row,row) because MySQL doesn't supports row
								 * expressions.
								 */
								.set( CUSTOMER.NAME, model.name )
								.set( CUSTOMER.LAST_NAME, model.lastName )
								.set( CUSTOMER.ADDRESS, model.address )
								.set( CUSTOMER.PHONE, model.phone )
								.set( CUSTOMER.EMAIL, model.email )
								.where( CUSTOMER.ID.eq( id ) )
								/*
								 * Can't use 'returning(fields)' here because it isn't supported
								 * in MySQL.
								 */
								.execute();
						if ( mod < 1 )
						{
							return OpsHttp.notFound();
						}
						// Fetch it back and return.
						return db.selectFrom( CUSTOMER )
								.where( CUSTOMER.ID.eq( id ) )
								.fetchOne()
								.map( Get::new );
					}
				} )
				.delete( req ->
				{
					final Delete model = req.body( Delete.class );

					try ( DSLContext db = dslContext( req ) )
					{
						final int mod = db.delete( CUSTOMER )
								.where( CUSTOMER.ID.eq( UInteger.valueOf( model.id ) ) )
								.execute();

						return mod < 1 ? OpsHttp.notFound() : OpsHttp.ok();
					}
				} );
		return app;
	}
}
