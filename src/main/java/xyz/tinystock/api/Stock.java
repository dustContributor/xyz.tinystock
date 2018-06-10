package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Stock.STOCK;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.jooby.Jooby;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import xyz.tinystock.utils.OpsHttp;

public final class Stock
{
	private static class Shared
	{
		public long componentId;

		public Shared ()
		{
			// Empty.
		}
	}

	public static final class Get extends Shared
	{
		public long quantity;

		public Get ( org.jooq.Record r )
		{
			componentId = r.get( STOCK.ID_COMPONENT ).longValue();
			quantity = r.get( STOCK.QUANTITY ).longValue();
		}
	}

	public static final class Put extends Shared
	{
		public long difference;

		public Put ()
		{
			// Default for de-serialization.
		}
	}

	public static final Jooby register ( Jooby app )
	{
		app.path( "api/stock", () ->
		{
			app.get( req ->
			{
				final long id = req.param( "id" ).longValue( 0 );
				// Don't even try to parse it if we got a proper ID first.
				final UInteger[] ids = id > 0 ? null
						: req.param( "ids" ).toList().stream()
								.map( v ->
								{
									try
									{
										return UInteger.valueOf( v );
									}
									catch ( Exception e )
									{
										return null;
									}
								} )
								.filter( v -> v != null )
								.toArray( UInteger[]::new );

				try ( DSLContext db = dslContext( req ) )
				{
					/*
					 * If an id was passed, add a where clause, otherwise fetch all rows.
					 */
					Condition cond = DSL.trueCondition();
					if ( ids != null )
					{
						cond = STOCK.ID_COMPONENT.in( ids );
					}
					if ( id > 0 )
					{
						cond = STOCK.ID_COMPONENT.eq( UInteger.valueOf( id ) );
					}
					return db.selectFrom( STOCK )
							.where( cond )
							.fetch()
							.map( Get::new );
				}
			} );
			app.put( req ->
			{
				final Put model = req.body().to( Put.class );
				final UInteger id = UInteger.valueOf( model.componentId );

				try ( DSLContext db = dslContext( req ) )
				{
					final int mod = db.update( STOCK )
							/*
							 * We update the stock based on differences, since doing an
							 * absolute update, ie, set QUANTITY 3, might lose changes if
							 * multiple clients are updating the same stock. Amounts in their
							 * browsers can be different.
							 */
							.set( STOCK.QUANTITY, STOCK.QUANTITY.add( Long.valueOf( model.difference ) ) )
							.where( STOCK.ID_COMPONENT.eq( id ) )
							.execute();

					if ( mod < 1 )
					{
						return OpsHttp.notFound();
					}

					// Convenience api for select * from TABLE where CONDITION.
					return db
							.fetchOne( STOCK, STOCK.ID_COMPONENT.eq( id ) )
							.map( Get::new );
				}
			} );
		} );
		return app;
	}
}
