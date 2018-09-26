package xyz.tinystock.api;

import static xyz.tinystock.database.tables.Order.ORDER;
import static xyz.tinystock.utils.OpsDb.dslContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.jooby.Jooby;
import org.jooby.Request;
import org.jooq.DSLContext;
import org.jooq.UpdateSetStep;
import org.jooq.UpdateWhereStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import xyz.tinystock.database.tables.records.OrderRecord;
import xyz.tinystock.model.OrderState;
import xyz.tinystock.utils.OpsHttp;

public final class Order
{
	static final class Get
	{
		public long id;
		public int stateId;
		public Timestamp createdDate;
		public String complaint;
		public long customerId;
		public Timestamp deliveredDate;
		public String comment;

		public Get ( org.jooq.Record r )
		{
			id = r.get( ORDER.ID ).longValue();
			stateId = r.get( ORDER.ID_STATE ).intValue();
			createdDate = r.get( ORDER.CREATED_DATE );
			complaint = r.get( ORDER.COMPLAINT );
			customerId = r.get( ORDER.ID_CUSTOMER ).longValue();
			deliveredDate = r.get( ORDER.DELIVERED_DATE );
			comment = r.get( ORDER.COMMENT );
		}
	}

	static final class Post
	{
		public long customerId;

		public Post ()
		{
			// For de-serialization.
		}
	}

	static final class Put
	{
		public long id;
		/*
		 * Use Optional here to differentiate between setting to null and not
		 * updating at all.
		 */
		public OptionalInt stateId;
		public Optional<Timestamp> deliveredDate;
		public Optional<String> comment;
		public Optional<String> complaint;

		public Put ()
		{
			// For de-serialization.
		}
	}

	@JsonIgnoreProperties( ignoreUnknown = true )
	static final class Delete
	{
		public long id;
	}

	private static List<Get> getById ( Request req, final long id, final long customerId )
	{
		try ( DSLContext db = dslContext( req ) )
		{
			return db
					.selectFrom( ORDER )
					// If id is valid, try fetch one by id, otherwise return all.
					.where( id > 0 ? ORDER.ID.eq( UInteger.valueOf( id ) ) : DSL.trueCondition() )
					.and( customerId > 0 ? ORDER.ID_CUSTOMER.eq( UInteger.valueOf( customerId ) ) : DSL.trueCondition() )
					.fetch()
					.map( Get::new );
		}
	}

	/*
	 * UpdateSetStep is an AutoCloseable, that we needn't to close, suppress the
	 * warning.
	 */
	@SuppressWarnings( "resource" )
	public static final Jooby register ( Jooby app )
	{
		app.path( "api/order", () ->
		{
			app.get( req ->
			{
				var id = req.param( "id" ).longValue( 0 );
				var customerId = req.param( "customerId" ).longValue( 0 );
				return getById( req, id, customerId );
			} );
			app.delete( req ->
			{
				final Delete model = req.body().to( Delete.class );

				if ( model.id < 1 )
				{
					return OpsHttp.badRequest();
				}

				try ( DSLContext db = dslContext( req ) )
				{
					final int mod = db.deleteFrom( ORDER )
							.where( ORDER.ID.eq( UInteger.valueOf( model.id ) ) )
							.execute();
					if ( mod < 1 )
					{
						return OpsHttp.notFound();
					}
				}
				return OpsHttp.ok();
			} );
			app.post( req ->
			{
				final Post model = req.body().to( Post.class );

				try ( DSLContext db = dslContext( req ) )
				{
					return db.insertInto( ORDER, ORDER.ID_CUSTOMER,
							ORDER.ID_STATE,
							ORDER.CREATED_DATE )
							.values(
									UInteger.valueOf( model.customerId ),
									OrderState.CREATED.id,
									Timestamp.from( Instant.now() ) )
							.returning()
							.fetchOne()
							.map( Get::new );
				}
			} );
			app.put( req ->
			{
				final Put model = req.body().to( Put.class );

				try ( DSLContext db = dslContext( req ) )
				{
					UpdateSetStep<OrderRecord> upd = db.update( ORDER );

					final OrderState state = OrderState.valueOf( model.stateId.orElse( 0 ) );
					// TODO Restore stock on cancelled order.
					if ( state != OrderState.UNDEFINED )
					{
						upd = upd.set( ORDER.ID_STATE, state.id );
					}
					/*
					 * TODO Validate marking a delivered order as any other state than
					 * delivered?
					 */
					if ( model.deliveredDate.isPresent() )
					{
						upd = upd.set( ORDER.DELIVERED_DATE, model.deliveredDate.get() );
					}

					if ( model.comment.isPresent() )
					{
						upd = upd.set( ORDER.COMMENT, model.comment.get() );
					}

					if ( model.complaint.isPresent() )
					{
						upd = upd.set( ORDER.COMPLAINT, model.complaint.get() );
					}

					// This cast will fail if no field was supplied for the update.
					@SuppressWarnings( "unchecked" )
					final int mod = ((UpdateWhereStep<OrderRecord>) upd)
							.where( ORDER.ID.eq( UInteger.valueOf( model.id ) ) )
							.execute();

					if ( mod < 1 )
					{
						return OpsHttp.notFound();
					}
				}
				// Fetch updated record and return.
				return getById( req, model.id, 0 );
			} );
			app.get( "/states", ( req ) ->
			{
				return OrderState.serializableValues();
			} );
		} );
		return app;
	}
}
