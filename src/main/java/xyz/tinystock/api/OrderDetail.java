package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Component.COMPONENT;
import static xyz.tinystock.database.tables.OrderDetail.ORDER_DETAIL;
import static xyz.tinystock.database.tables.Stock.STOCK;
import static xyz.tinystock.utils.OpsDb.dslContext;

import org.jooby.Jooby;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public final class OrderDetail
{
	private static class Shared
	{
		public long orderId;
		public long componentId;
		public long quantity;

		public Shared ()
		{
			// Empty.
		}
	}

	static final class Get extends Shared
	{
		public long totalPrice;

		public Get ( org.jooq.Record r )
		{
			orderId = r.get( ORDER_DETAIL.ID_ORDER ).longValue();
			componentId = r.get( ORDER_DETAIL.ID_COMPONENT ).longValue();
			quantity = r.get( ORDER_DETAIL.QUANTITY ).longValue();
			totalPrice = r.get( ORDER_DETAIL.TOTAL_PRICE ).longValue();
		}
	}

	static final class Post extends Shared
	{
		public Post ()
		{
			// Default for de-serialization.
		}
	}

	static final class GetParams
	{
		public long orderId;
	}

	public static final Jooby register ( Jooby app )
	{
		// TODO delete for orders that haven't been delivered yet?
		app.use( "api/orderdetail" )
				.get( req ->
				{
					final long id = req.param( "orderId" ).longValue( 0 );

					try ( DSLContext db = dslContext( req ) )
					{
						/*
						 * If an id was passed, add a where clause, otherwise fetch all
						 * rows.
						 */
						if ( id > 0 )
						{
							return db
									.fetch( ORDER_DETAIL, ORDER_DETAIL.ID_ORDER.eq( UInteger.valueOf( id ) ) )
									.map( Get::new );
						}
						return db.fetch( ORDER_DETAIL ).map( Get::new );
					}
				} )
				.post( req ->
				{
					final Post model = req.body().to( Post.class );
					final UInteger componentId = UInteger.valueOf( model.componentId );

					try ( DSLContext db = dslContext( req ) )
					{
						return db.transactionResult( cfg ->
						{
							try ( DSLContext tr = DSL.using( cfg ) )
							{
								// Update the stock subtracting what was ordered.
								tr.update( STOCK )
										.set( STOCK.QUANTITY, STOCK.QUANTITY.sub( Long.valueOf( model.quantity ) ) )
										.where( STOCK.ID_COMPONENT.eq( componentId ) )
										.execute();

								return tr.insertInto( ORDER_DETAIL,
										ORDER_DETAIL.ID_ORDER,
										ORDER_DETAIL.ID_COMPONENT,
										ORDER_DETAIL.QUANTITY,
										ORDER_DETAIL.TOTAL_PRICE )
										.select(
												tr.select(
														DSL.inline( UInteger.valueOf( model.orderId ) ),
														COMPONENT.ID,
														DSL.inline( UInteger.valueOf( model.quantity ) ),
														/* Compute total price in the database directly */
														COMPONENT.PRICE.mul( Long.valueOf( model.quantity ) ) )
														.from( COMPONENT )
														.where( COMPONENT.ID.eq( componentId ) ) )
										// Return field by field or all of them together.
										.returning( ORDER_DETAIL.fields() )
										.fetch()
										.map( Get::new );
							}
						} );
					}
				} );
		return app;
	}
}
