package xyz.tinystock.utils;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class OpsAuth
{
	/**
	 * SecureRandom instances for each thread, since it seems to be expensive to
	 * create.
	 */
	private static final ThreadLocal<SecureRandom> RAND = ThreadLocal.withInitial( SecureRandom::new );

	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final int SALT_BYTE_SIZE = 24;
	private static final int HASH_BYTE_SIZE = 24;
	private static final int HASH_BIT_SIZE = HASH_BYTE_SIZE * 8;
	private static final int PBKDF2_ITERATIONS = 64000;

	public static final int EXPECTED_PASSWORD_LENGTH = 40;

	private OpsAuth ()
	{
		// Empty.
	}

	public static final boolean validate ( String password, byte[] salt, byte[] reference )
	{
		return constantEquals( saltPass( password, salt ), reference );
	}

	public static final byte[] makeSalt ()
	{
		byte[] salt = new byte[SALT_BYTE_SIZE];
		RAND.get().nextBytes( salt );
		return salt;
	}

	public static final byte[] saltPass ( String password, byte[] salt )
	{
		return hash( password.toCharArray(), salt );
	}

	private static final byte[] hash ( char[] password, byte[] salt )
	{
		try
		{
			final PBEKeySpec spec = new PBEKeySpec( password, salt, PBKDF2_ITERATIONS, HASH_BIT_SIZE );
			final SecretKeyFactory skf = SecretKeyFactory.getInstance( PBKDF2_ALGORITHM );
			return skf.generateSecret( spec ).getEncoded();
		}
		catch ( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	private static final boolean constantEquals ( final byte[] a, final byte[] b )
	{
		int diff = a.length ^ b.length;
		for ( int i = 0; i < a.length && i < b.length; i++ )
		{
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	private static final byte[] hexToBytes ( final String hex )
	{
		final int len = hex.length();

		if ( (len & 1) != 0 )
		{
			throw new IllegalArgumentException( "length must be even" );
		}

		byte[] dest = new byte[len / 2];

		for ( int c = 0, b = 0; c < len; c += 2, ++b )
		{
			// Two characters, one hex 8 bit value.
			final int d1 = Character.digit( hex.charAt( c ), 16 );
			final int d2 = Character.digit( hex.charAt( c + 1 ), 16 );
			dest[b] = (byte) ((d1 | (d2 << 4)) & 0xFF);
		}

		return dest;
	}
}
