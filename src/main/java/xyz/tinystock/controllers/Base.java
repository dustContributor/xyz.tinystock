package xyz.tinystock.controllers;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.Path;

import com.google.common.reflect.ClassPath;
import com.typesafe.config.Config;

public abstract class Base
{
	private static final ConcurrentHashMap<RouteKey, String> VIEW_ROUTES = new ConcurrentHashMap<>();

	public static final String VIEWS_ROOT_FOLDER = "views";

	protected final Config cfg;

	protected Base ( Config cfg )
	{
		super();
		this.cfg = cfg;
	}

	public final View view ( final String view )
	{
		return Results.html( VIEW_ROUTES.computeIfAbsent( RouteKey.of( getClass(), view ), k ->
		{
			final Path path = k.owner.getAnnotation( Path.class );
			return VIEWS_ROOT_FOLDER
					+ (path == null ? "" : path.value()[0])
					+ (StringUtils.isBlank( k.route ) ? "" : ("/" + k.route));
		} ) );
	}

	private final static class RouteKey
	{
		public final Class<?> owner;
		public final String route;
		private final int hash;

		private RouteKey ( Class<?> owner, String route )
		{
			super();
			this.owner = Objects.requireNonNull( owner );
			// This one can be null.
			this.route = route;
			int hash = 17;
			hash = 31 * hash + owner.hashCode();
			hash = 31 * hash + Objects.hashCode( route );
			this.hash = hash;
		}

		public static RouteKey of ( Class<?> owner, String route )
		{
			return new RouteKey( owner, route );
		}

		@Override
		public int hashCode ()
		{
			return hash;
		}

		@Override
		public boolean equals ( Object obj )
		{
			if ( !(obj instanceof RouteKey) )
			{
				return false;
			}
			final RouteKey k = (RouteKey) obj;
			return hash == k.hash && owner.equals( k.owner ) && Objects.equals( route, k.route );
		}
	}

	public static final void registerAll ( final Jooby dest )
	{
		final Class<?> type = MethodHandles.lookup().lookupClass();
		final String name = type.getName();
		try
		{
			ClassPath
					.from( type.getClassLoader() )
					.getTopLevelClasses( name.substring( 0, name.lastIndexOf( '.' ) ) )
					.stream()
					.map( c -> c.load() )
					.filter( c -> !Modifier.isAbstract( c.getModifiers() ) && Base.class.isAssignableFrom( c ) )
					.forEach( c ->
					{
						System.out.println( "Loading controller " + c.getSimpleName() );
						dest.use( c );
					} );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
}
