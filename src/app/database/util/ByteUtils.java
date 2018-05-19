package app.database.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.primitives.Ints;

import app.database.DBContract;
import app.database.DBTransaction;
import app.log.OUT;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;

public class ByteUtils {
	private static final byte PREFIXINT = (byte) 0b00000001;
	private static final byte PREFIXSTRING = (byte) 0b00010000;
	public static final byte PREFIXSTRINGARRAY = (byte) 0b00100000;
	private static final byte PREFIXBOOLEAN = (byte) 0b01000000;
	private static final byte PREFIXLONG = (byte) 0b00000011;

	private static final byte PREFIXBYTE = (byte) 0b00000010;
	private static final byte PREFIXBYTEARRAY = (byte) 0b00000100;
	private static final byte PREFIXBIGINT = (byte) 0b00001000;

	public static final byte PREFIXTRANSACTIONS = (byte) 0b00001111;
	public static final byte PREFIXCONTRACTS = (byte) 0b00011111;

	private static final int LENGTHTO = 5;
	private static final int LENGTHFROM = 1;

	@SuppressWarnings("unchecked")
	public static byte[] createParcel(Object[] contents) {
		Object o;
		byte[] objectBytes = new byte[] {};
		byte prefix = (byte) 0;
		byte[] length = new byte[] {};
		byte[] newParcel = new byte[] {};
		for (int i = 0; i < contents.length; i++) {
			o = contents[i];
			objectBytes = new byte[] {};
			if (o instanceof Integer) {
				objectBytes = intoToBytes((int) o);
				prefix = PREFIXINT;
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof Byte) {
				objectBytes = new byte[] { (byte) o };
				prefix = PREFIXBYTE;
				length = getLengthInByte(1);
			} else if (o instanceof byte[]) {
				objectBytes = (byte[]) o;
				prefix = PREFIXBYTEARRAY;
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof BigInteger) {
				objectBytes = bigIntegerToBytes((BigInteger) o);
				prefix = PREFIXBIGINT;
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof String) {
				objectBytes = stringToBytes((String) o);
				prefix = PREFIXSTRING;
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof String[]) {
				prefix = PREFIXSTRINGARRAY;
				String[] strArray = (String[]) o;
				for (int j = 0; j < strArray.length; j++) {
					byte[] str = stringToBytes(strArray[j]);
					byte[] strlength = getLengthInByte(str.length);
					objectBytes = concatenateBytes(objectBytes, strlength);
					objectBytes = concatenateBytes(objectBytes, str);
				}
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof Long) {
				objectBytes = longToBytes((Long) o);
				prefix = PREFIXLONG;
				length = Ints.toByteArray(objectBytes.length);
			} else if (o instanceof Boolean) {
				objectBytes = booleanToBytes((boolean) o);
				prefix = PREFIXBOOLEAN;
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof ArrayList<?>) {
				prefix = PREFIXTRANSACTIONS;
				ArrayList<Transaction> strArray = (ArrayList<Transaction>) o;
				for (int j = 0; j < strArray.size(); j++) {
					byte[] str = (strArray.get(j).writeToDB());
					byte strlength = (byte) str.length;
					objectBytes = concatenateBytes(objectBytes, new byte[] { strlength });
					objectBytes = concatenateBytes(objectBytes, str);
				}
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof Transaction[]) {
				prefix = PREFIXTRANSACTIONS;
				Transaction[] strArray = (Transaction[]) o;
				for (int j = 0; j < strArray.length; j++) {
					byte[] str = (strArray[j].writeToDB());
					byte[] strlength = getLengthInByte(str.length);
					objectBytes = concatenateBytes(objectBytes, strlength);
					objectBytes = concatenateBytes(objectBytes, str);
				}
				length = getLengthInByte(objectBytes.length);
			} else if (o instanceof SmartContract[]) {
				prefix = PREFIXCONTRACTS;
				SmartContract[] strArray = (SmartContract[]) o;
				for (int j = 0; j < strArray.length; j++) {
					byte[] str = (strArray[j].writeToDB());
					byte[] strlength = getLengthInByte(str.length);
					objectBytes = concatenateBytes(objectBytes, strlength);
					objectBytes = concatenateBytes(objectBytes, str);
				}
				length = getLengthInByte(objectBytes.length);
			}
			newParcel = addToParcel(newParcel, prefix, length, objectBytes);
		}
		return newParcel;
	}

	private static byte[] getLengthInByte(int length) {
		byte[] bytearray = Ints.toByteArray(length);
		return bytearray;
	}

	private static byte[] addToParcel(byte[] parcel, byte prefix, byte[] length, byte[] data) {
		parcel = concatenateBytes(parcel, new byte[] { prefix });
		parcel = concatenateBytes(parcel, length);
		parcel = concatenateBytes(parcel, data);
		return parcel;
	}

	public static boolean bytesToBoolean(byte[] data) {

		boolean converted = false;
		if (data[0] == PREFIXBOOLEAN) {
			int length = getLengthNextByteObject(data);
			byte[] val = new byte[length];
			int x = 0;
			for (int i = LENGTHTO; i < (LENGTHTO + length); i++, x++) {
				val[x] = data[i];
			}
			converted = val[0] != 0;
			// data = removeReaded(2 + length, data);
		}
		return converted;

		// boolean vOut = data[0]!=0; //convert first Byte
		// return vOut;
	}

	private static byte[] booleanToBytes(boolean bool) {
		byte[] bytes = new byte[] { (byte) (bool ? 1 : 0) };
		return bytes;
	}

	private static byte[] intoToBytes(int o) {
		return ByteBuffer.allocate(4).putInt(o).array();
	}

	private static byte[] bigIntegerToBytes(BigInteger o) {
		return ((BigInteger) o).toByteArray();
	}

	private static byte[] stringToBytes(String o) {
		return o.getBytes();
	}

	private static byte[] concatenateBytes(byte[] objectBytes, byte[] bs) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(objectBytes);
			outputStream.write(bs);
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
		byte c[] = outputStream.toByteArray();
		return c;
	}

	public static BigInteger bytesToBigInteger(byte[] data) {
		BigInteger converted = new BigInteger("0");
		if (data[0] == PREFIXBIGINT || data[0] == PREFIXINT) {
			int length = getLengthNextByteObject(data);
			byte[] val = new byte[length];
			int x = 0;
			for (int i = LENGTHTO; i < (LENGTHTO + length); i++, x++) {
				val[x] = data[i];
			}
			try {
				converted = new BigInteger(val);

			} catch (Exception e) {
				OUT.ERROR("", e);
			}
		}
		return converted;
	}

	public static byte[] removeReaded(int length, byte[] parcel) {
		return Arrays.copyOfRange(parcel, length, parcel.length);
	}

	public static String bytesToString(byte[] data) {
		String converted = null;
		if (data[0] == PREFIXSTRING) {
			int length = getLengthNextByteObject(data);
			byte[] val = new byte[length];
			int x = 0;
			for (int i = LENGTHTO; i < (LENGTHTO + length); i++, x++) {
				val[x] = data[i];
			}
			try {
				converted = new String(val, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				OUT.ERROR("", e);
			}
		}
		return converted;
	}

	private static byte[] longToBytes(long x) {
		byte[] result = new byte[8];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte) (x & 0xFF);
			x >>= 8;
		}
		return result;
	}

	public static long bytesToLong(byte[] bytes) {
		long result = 0;
		if (bytes[0] == PREFIXLONG) {
			int length = getLengthNextByteObject(bytes);
			byte[] val = new byte[length];
			int x = 0;
			for (int i = LENGTHTO; i < (LENGTHTO + length); i++, x++) {
				val[x] = bytes[i];
				result <<= 8;
				result |= (bytes[i] & 0xFF);
			}
		}
		return result;
	}

	public static int getLengthNextByteObject(byte[] arr) {
		int length = new BigInteger(getBytesRawFromTo(LENGTHFROM, LENGTHTO, arr)).intValue();
		length = (length & 0xff);
		return length;
	}

	public static byte[] getBytesRaw(int length, byte[] parcel) {
		return Arrays.copyOfRange(parcel, LENGTHTO, length);
	}

	public static byte[] getBytesRawFromTo(int from, int to, byte[] parcel) {
		return Arrays.copyOfRange(parcel, from, to);
	}

	public static int bytesToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public static Transaction bytesToTransaction(byte[] bytes) {
		Transaction tx = DBTransaction.getTransaction(bytes);
		return tx;
	}

	public static SmartContract bytesToContract(byte[] bytes) {
		SmartContract tx = DBContract.getContract(bytes);
		return tx;
	}

}
