package xyz.tinystock;

import org.jooby.Jooby;
import org.jooby.jade.Jade;
import org.jooby.json.Jackson;
import org.jooby.whoops.Whoops;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * @author jooby generator
 */
public class App extends Jooby
{
	static
	{
		/*
		 * Log executed queries.
		 */
		System.getProperties().setProperty( "org.jooq.settings", "conf/jooq.xml" );
	}

	{
		/* Jackson for sending/receiving JSON */
		use( new Jackson()
				.doWith( m ->
				{
					// Use ISO date formats to handle java.sql.Date and friends.
					m.setDateFormat( new StdDateFormat() );
					// Add JDK8 module to handle Optional fields.
					m.registerModule( new Jdk8Module() );
					/*
					 * By default Jackson throws an exception if you pass a property
					 * unaccounted for. This disables it.
					 */
					m.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
				} ) );
		/*
		 * Whoops for fancy error messages if the server blew up serving a request.
		 */
		use( new Whoops() );
		/* Html templating engine */
		use( new Jade() );
		/* Public assets, js, css, etc. */
		assets( "/assets/**" );
		/* Each view is managed by a .js file. */
		assets( "/views/**.js" );
		/* Manually register all controllers. */
		xyz.tinystock.controllers.Home.register( this );
		/* Manually register all apis. */
		xyz.tinystock.api.Component.register( this );
		xyz.tinystock.api.Customer.register( this );
		xyz.tinystock.api.Order.register( this );
		xyz.tinystock.api.OrderDetail.register( this );
		xyz.tinystock.api.Stock.register( this );
		xyz.tinystock.api.User.register( this );
	}
	//
	// @Override
	// public void stop ()
	// {
	// System.out.println( "stop: " + this );
	// super.stop();
	// }
	//
	// @Override
	// public Jooby onStop ( CheckedConsumer<Registry> callback )
	// {
	// System.out.println( "onStop_CheckedConsumer: " + this );
	// return super.onStop( callback );
	// }
	//
	// @Override
	// public Jooby onStop ( CheckedRunnable callback )
	// {
	// System.out.println( "onStop_CheckedRunnable: " + this );
	// return super.onStop( callback );
	// }

	public static void main ( final String[] args )
	{
		run( App::new, args );
	}

}
