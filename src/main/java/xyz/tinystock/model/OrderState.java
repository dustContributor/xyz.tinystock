package xyz.tinystock.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.types.UInteger;

public enum OrderState
{
	UNDEFINED( 0, "Undefined" ),
	CREATED( 1, "Created" ),
	CANCELLED( 2, "Cancelled" ),
	PAID( 3, "Paid" ),
	IN_DELIVERY( 4, "In Delivery" ),
	DELIVERED( 5, "Delivered" );

	public final UInteger id;
	public final int value;
	public final String publicName;

	private static final Map<Integer, OrderState> BY_ID = Arrays.stream( values() )
			.collect( Collectors.toMap( k -> Integer.valueOf( k.value ), v -> v ) );

	private OrderState ( int value, String publicName )
	{
		this.value = value;
		this.id = UInteger.valueOf( value );
		this.publicName = publicName;
	}

	public static final OrderState valueOf ( int value )
	{
		return BY_ID.getOrDefault( Integer.valueOf( value ), UNDEFINED );
	}

	/** For internal use only, do not modify. */
	private static final OrderState[] VALUES = OrderState.values();

	public static final Object[] serializableValues ()
	{
		/*
		 * We abuse anonymous classes since the serializer on the other end will
		 * reflectively retrieve the public getters as properties anyway.
		 */
		return Arrays.stream( VALUES )
				.map( v -> new Object()
				{
					public int getId ()
					{
						return v.value;
					}

					public String getName ()
					{
						return v.toString();
					}

					public String getPublicName ()
					{
						return v.publicName;
					}
				} )
				.toArray( Object[]::new );
	}
}
