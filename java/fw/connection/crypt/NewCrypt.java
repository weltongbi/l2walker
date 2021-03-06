package fw.connection.crypt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;

import fw.util.BlowfishEngine;

public class NewCrypt {
	private static final byte[] STATIC_BLOWFISH_KEY =
	{
		(byte) 0x6b, (byte) 0x60, (byte) 0xcb, (byte) 0x5b,
		(byte) 0x82, (byte) 0xce, (byte) 0x90, (byte) 0xb1,
		(byte) 0xcc, (byte) 0x2b, (byte) 0x6c, (byte) 0x55,
		(byte) 0x6c, (byte) 0x6c, (byte) 0x6c, (byte) 0x6c
	};
	protected static Logger _log = Logger.getLogger(NewCrypt.class.getName());
	BlowfishEngine _crypt;
	BlowfishEngine _decrypt;

	/**
	 * @param blowfishKey
	 */
	public NewCrypt(byte[] blowfishKey) {
		_crypt = new BlowfishEngine();
		_crypt.init(true, blowfishKey);
		_decrypt = new BlowfishEngine();
		_decrypt.init(false, blowfishKey);
	}
	
	
	/**
	 * @param useStaticKey
	 */
	public NewCrypt(boolean staticKey) {
		_crypt = new BlowfishEngine();
		_crypt.init(true, STATIC_BLOWFISH_KEY);
		_decrypt = new BlowfishEngine();
		_decrypt.init(false, STATIC_BLOWFISH_KEY);
	}

	public NewCrypt(String key) {
		this(key.getBytes());
	}

	public static boolean verifyChecksum(byte[] raw) {
		return NewCrypt.verifyChecksum(raw, 0, raw.length);
	}

	public static boolean verifyChecksum(byte[] raw, final int offset,
			final int size) {
		// check if size is multiple of 4 and if there is more then only the
		// checksum
		if ((size & 3) != 0 || size <= 4) {
			return false;
		}

		long chksum = 0;
		int count = size - 4;
		long check = -1;
		int i;

		for (i = offset; i < count; i += 4) {
			check = raw[i] & 0xff;
			check |= raw[i + 1] << 8 & 0xff00;
			check |= raw[i + 2] << 0x10 & 0xff0000;
			check |= raw[i + 3] << 0x18 & 0xff000000;

			chksum ^= check;
		}

		check = raw[i] & 0xff;
		check |= raw[i + 1] << 8 & 0xff00;
		check |= raw[i + 2] << 0x10 & 0xff0000;
		check |= raw[i + 3] << 0x18 & 0xff000000;

		return check == chksum;
	}

	public static void appendChecksum(byte[] raw) {
		NewCrypt.appendChecksum(raw, 0, raw.length);
	}

//	public static void appendChecksum(byte[] raw, final int offset,
//			final int size) {
//		long chksum = 0;
//		int count = size - 4;
//		long ecx;
//		int i;
//
//		for (i = offset; i < count; i += 4) {
//			ecx = raw[i] & 0xff;
//			ecx |= raw[i + 1] << 8 & 0xff00;
//			ecx |= raw[i + 2] << 0x10 & 0xff0000;
//			ecx |= raw[i + 3] << 0x18 & 0xff000000;
//
//			chksum ^= ecx;
//		}
//
//		ecx = raw[i] & 0xff;
//		ecx |= raw[i + 1] << 8 & 0xff00;
//		ecx |= raw[i + 2] << 0x10 & 0xff0000;
//		ecx |= raw[i + 3] << 0x18 & 0xff000000;
//
//		raw[i] = (byte) (chksum & 0xff);
//		raw[i + 1] = (byte) (chksum >> 0x08 & 0xff);
//		raw[i + 2] = (byte) (chksum >> 0x10 & 0xff);
//		raw[i + 3] = (byte) (chksum >> 0x18 & 0xff);
//	}
    public static void appendChecksum(byte[] raw, final int offset, final int size)
	{
		long chksum = 0;
		int count = size - 8;
		long ecx;
		int i;

		for (i = offset; i<count; i+=4)
		{
			ecx = raw[i] &0xff;
			ecx |= raw[i+1] << 8 &0xff00;
			ecx |= raw[i+2] << 0x10 &0xff0000;
			ecx |= raw[i+3] << 0x18 &0xff000000;

			chksum ^= ecx;
		}

		ecx = raw[i] &0xff;
		ecx |= raw[i+1] << 8 &0xff00;
		ecx |= raw[i+2] << 0x10 &0xff0000;
		ecx |= raw[i+3] << 0x18 &0xff000000;

		raw[i] = (byte) (chksum &0xff);
		raw[i+1] = (byte) (chksum >>0x08 &0xff);
		raw[i+2] = (byte) (chksum >>0x10 &0xff);
		raw[i+3] = (byte) (chksum >>0x18 &0xff);	
	}

	/**
	 * Packet is first XOR encoded with <code>key</code> Then, the last 4 bytes
	 * are overwritten with the the XOR "key". Thus this assume that there is
	 * enough room for the key to fit without overwriting data.
	 * 
	 * @param raw
	 *            The raw bytes to be encrypted
	 * @param key
	 *            The 4 bytes (int) XOR key
	 */
	public static void encXORPass(byte[] raw, int key) {
		NewCrypt.encXORPass(raw, 0, raw.length, key);
	}

	/**
	 * Packet is first XOR encoded with <code>key</code> Then, the last 4 bytes
	 * are overwritten with the the XOR "key". Thus this assume that there is
	 * enough room for the key to fit without overwriting data.
	 * 
	 * @param raw
	 *            The raw bytes to be encrypted
	 * @param offset
	 *            The begining of the data to be encrypted
	 * @param size
	 *            Length of the data to be encrypted
	 * @param key
	 *            The 4 bytes (int) XOR key
	 */
	public static void encXORPass(byte[] raw, final int offset, final int size,
			int key) {
		int stop = size - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key; // Initial xor key

		while (pos < stop) {
			edx = (raw[pos] & 0xFF);
			edx |= (raw[pos + 1] & 0xFF) << 8;
			edx |= (raw[pos + 2] & 0xFF) << 16;
			edx |= (raw[pos + 3] & 0xFF) << 24;

			ecx += edx;

			edx ^= ecx;

			raw[pos++] = (byte) (edx & 0xFF);
			raw[pos++] = (byte) (edx >> 8 & 0xFF);
			raw[pos++] = (byte) (edx >> 16 & 0xFF);
			raw[pos++] = (byte) (edx >> 24 & 0xFF);
		}

		raw[pos++] = (byte) (ecx & 0xFF);
		raw[pos++] = (byte) (ecx >> 8 & 0xFF);
		raw[pos++] = (byte) (ecx >> 16 & 0xFF);
		raw[pos++] = (byte) (ecx >> 24 & 0xFF);
	}

	
	public static void decXORPass(byte raw[]) {
		decXORPass(raw, 0, raw.length);
	}

	public static void decXORPass(byte raw[], int offset, int size) {
		int pos = size - 1 - 4;
		int ecx = 0;
		ecx = (raw[pos--] & 0xff) << 24;
		ecx |= (raw[pos--] & 0xff) << 16;
		ecx |= (raw[pos--] & 0xff) << 8;
		ecx |= (raw[pos--] & 0xff);
		while (pos > 4) {
			int edx = (raw[pos] & 0xff) << 24;
			edx |= (raw[pos - 1] & 0xff) << 16;
			edx |= (raw[pos - 2] & 0xff) << 8;
			edx |= (raw[pos - 3] & 0xff);
			edx ^= ecx;
			ecx -= edx;
			raw[pos--] = (byte) (edx >> 24 & 0xff);
			raw[pos--] = (byte) (edx >> 16 & 0xff);
			raw[pos--] = (byte) (edx >> 8 & 0xff);
			raw[pos--] = (byte) (edx & 0xff);
		}
	}

	public byte[] decrypt(byte[] raw) throws IOException {
		byte[] result = new byte[raw.length];
		int count = raw.length / 8;

		for (int i = 0; i < count; i++) {
			_decrypt.processBlock(raw, i * 8, result, i * 8);
		}

		return result;
	}

	public void decrypt(byte[] raw, final int offset, final int size)
			throws IOException {
		byte[] result = new byte[size];
		int count = size / 8;

		for (int i = 0; i < count; i++) {
			_decrypt.processBlock(raw, offset + i * 8, result, i * 8);
		}

		System.arraycopy(result, 0, raw, offset, size);
	}

	public byte[] crypt(byte[] raw) throws IOException {
		int count = raw.length / 8;
		byte[] result = new byte[raw.length];

		for (int i = 0; i < count; i++) {
			_crypt.processBlock(raw, i * 8, result, i * 8);
		}

		return result;
	}

	public void crypt(byte[] raw, final int offset, final int size)
			throws IOException {
		int count = size / 8;
		byte[] result = new byte[size];

		for (int i = 0; i < count; i++) {
			_crypt.processBlock(raw, offset + i * 8, result, i * 8);
		}

		System.arraycopy(result, 0, raw, offset, size);
	}

    public static BigInteger descrambleModulus(byte[] scrambledMod) {
        if (scrambledMod.length != 0x80)
            return null;
        // step 1 : xor last $40 bytes with first $40 bytes
        for (int i = 0; i < 0x40; i++) {
            scrambledMod[0x40 + i] ^= scrambledMod[i];
        }
        // step 2 : xor bytes $0d-$10 with bytes $34-$38
        for (int i = 0; i <= 3; i++) {
            scrambledMod[0x0d + i] ^= scrambledMod[0x34 + i];
        }
        // step 3 : xor first $40 bytes with last $40 bytes
        for (int i = 0; i < 0x40; i++) {
            scrambledMod[i] ^= scrambledMod[0x40 + i];
        }
        // step 4 : $4d-$50 <-> $00-$04
        byte tmp = 0;
        for (int i = 0; i <= 3; i++) {
            tmp = scrambledMod[0x00 + i];
            scrambledMod[0x00 + i] = scrambledMod[0x4d + i];
            scrambledMod[0x4d + i] = tmp;
        }
        byte[] result = new byte[129];
        System.arraycopy(scrambledMod, 0, result, 1, 128);
        BigInteger _modulus = new BigInteger(result);
        return _modulus;
    }

}
