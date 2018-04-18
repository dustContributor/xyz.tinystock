package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Stock.STOCK;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.jooby.Jooby;
import org.jooq.DSLContext;
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

	public static final Jooby register ( Jooby dest )
	{
		dest.use( "api/stock" )
				.get( req ->
				{
					final long id = req.param( "id" ).longValue( 0 );

					try ( DSLContext db = dslContext( req ) )
					{
						/*
						 * If an id was passed, add a where clause, otherwise fetch all
						 * rows.
						 */
						if ( id > 0 )
						{
							return db
									.fetchOne( STOCK, STOCK.ID_COMPONENT.eq( UInteger.valueOf( id ) ) )
									.map( Get::new );
						}
						return db.fetch( STOCK ).map( Get::new );
					}
				} )
				.put( req ->
				{
					final Put model = req.body().to( Put.class );
					final UInteger id = UInteger.valueOf( model.componentId );

					try ( DSLContext db = dslContext( req ) )
					{
						final int mod = db.update( STOCK )
								/*
								 * We update the stock based on differences, since doing an
								 * absolute update, ie, set QUANTITY 3, might lose changes if
								 * multiple clients are updating the same stock. Amounts in
								 * their browsers can be different.
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
		return dest;
	}
}
