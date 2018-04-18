package xyz.tinystock.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public final class OpsArray
{
	private OpsArray ()
	{
		// Empty.
	}

	@SafeVarargs
	public static final <T> T[] of ( T... values )
	{
		return values;
	}

	@SafeVarargs
	public static final <T> Stream<T> stream ( T... values )
	{
		return Arrays.stream( values );
	}
}
