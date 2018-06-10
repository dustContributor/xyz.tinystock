package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Component.COMPONENT;
import static xyz.tinystock.database.tables.Stock.STOCK;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.apache.commons.lang3.StringUtils;
import org.jooby.Jooby;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import xyz.tinystock.utils.OpsHttp;
import xyz.tinystock.utils.OpsString;

public final class Component
{
	private static class Shared
	{
		public String code;
		public String description;
		public long price;

		public Shared ()
		{
			// Empty.
		}

		public Shared ( org.jooq.Record r )
		{
			code = r.get( COMPONENT.CODE );
			description = r.get( COMPONENT.DESCRIPTION );
			price = r.get( COMPONENT.PRICE ).longValue();
		}
	}

	public static final class Get extends Shared
	{
		public long id;

		public Get ( org.jooq.Record r )
		{
			super( r );
			id = r.get( COMPONENT.ID ).longValue();
		}
	}

	public static final class Post extends Shared
	{
		public Post ()
		{
			// Default for de-serialization.
		}
	}

	public static final class Put extends Shared
	{
		public long id;

		public Put ()
		{
			// Default for de-serialization.
		}
	}

	public static final Jooby register ( Jooby app )
	{
		// TODO Delete? Logical delete if used in existing orders?
		app.path( "api/component", () ->
		{
			app.get( "/query", req ->
			{
				final String code = req.param( "code" ).value( "" );
				final long id = req.param( "id" ).longValue( 0 );

				try ( DSLContext db = dslContext( req ) )
				{
					Condition cond = DSL.trueCondition();
					if ( StringUtils.isNotEmpty( code ) )
					{
						cond = cond.and( DSL.upper( COMPONENT.CODE ).contains( OpsString.toUpper( code ) ) );
					}
					if ( id > 0 )
					{
						cond = cond.and( COMPONENT.ID.eq( UInteger.valueOf( id ) ) );
					}
					return db.selectFrom( COMPONENT )
							.where( cond )
							.fetch()
							.map( Get::new );
				}
			} );
			app.get( req ->
			{
				final long id = req.param( "id" ).longValue( 0 );

				try ( DSLContext db = dslContext( req ) )
				{
					return db
							.selectFrom( COMPONENT )
							/*
							 * If an id was passed, add a where clause, otherwise fetch all
							 * rows.
							 */
							.where( id > 0 ? COMPONENT.ID.eq( UInteger.valueOf( id ) ) : DSL.trueCondition() )
							.fetch()
							.map( Get::new );
				}
			} );
			app.put( req ->
			{
				final Put model = req.body().to( Put.class );
				final UInteger id = UInteger.valueOf( model.id );

				try ( DSLContext db = dslContext( req ) )
				{
					final int mod = db.update( COMPONENT )
							/*
							 * Can't use set(row,row) because MySQL doesn't supports row
							 * expressions.
							 */
							.set( COMPONENT.CODE, model.code )
							.set( COMPONENT.DESCRIPTION, model.description )
							.set( COMPONENT.PRICE, UInteger.valueOf( model.price ) )
							.where( COMPONENT.ID.eq( id ) )
							/*
							 * Can't use 'returning(fields)' here because it isn't supported
							 * in MySQL.
							 */
							.execute();
					if ( mod < 1 )
					{
						return OpsHttp.notFound();
					}

					// Convenience api for select * from TABLE where
					// CONDITION.
					return db.fetchOne( COMPONENT, COMPONENT.ID.eq( id ) )
							.map( Get::new );
				}
			} );
			app.post( req ->
			{
				final Post model = req.body().to( Post.class );

				try ( DSLContext db = dslContext( req ) )
				{
					return db.transactionResult( cfg ->
					{
						try ( DSLContext tr = DSL.using( cfg ) )
						{
							// Id is auto generated database-side, fetch
							// after insert.
							final Get result = tr
									.insertInto( COMPONENT, COMPONENT.CODE, COMPONENT.DESCRIPTION, COMPONENT.PRICE )
									.values( model.code, model.description, UInteger.valueOf( model.price ) )
									// Return field by field or all of them
									// together.
									.returning( COMPONENT.fields() )
									.fetchOne()
									.map( Get::new );
							// We create one stock row for each new
							// component.
							tr.insertInto( STOCK, STOCK.ID_COMPONENT, STOCK.QUANTITY )
									.values( UInteger.valueOf( result.id ), UInteger.valueOf( 0 ) )
									.execute();
							return result;
						}
					} );
				}
			} );
		} );
		return app;
	}
}
