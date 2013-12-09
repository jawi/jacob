/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 */
package jacob;

import static jacob.CborConstants.*;
import static jacob.CborType.*;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides an {@link InputStream} capable of handling CBOR encoded data.
 */
public class CborInputStream extends FilterInputStream {

    /**
     * Creates a new {@link CborInputStream} instance.
     * 
     * @param is the actual input stream to read the CBOR-encoded data from, cannot be <code>null</code>.
     */
    public CborInputStream(InputStream is) {
        super(new PushbackInputStream(is));
    }

    private static void fail(String msg, Object... args) throws IOException {
        throw new IOException(String.format(msg, args));
    }

    /**
     * Reads an array value in CBOR format.
     * 
     * @return the read array, never <code>null</code>.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public Object[] readArray() throws IOException {
        int ib = read();

        expectMajorType(ib, ARRAY);

        long len = readUInt(ib & 0x1f, true /* unlimitedAllowed */);
        if (len < 0) {
            fail("Unlimited length arrays not supported by readArray()!");
        }
        if (len > Integer.MAX_VALUE) {
            fail("Array length too long!");
        }

        Object[] result = new Object[(int) len];
        for (int i = 0; i < result.length; i++) {
            result[i] = readGenericItem();
        }
        return result;
    }

    /**
     * Reads a boolean value in CBOR format.
     * 
     * @return the read boolean.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public boolean readBoolean() throws IOException {
        int ib = read();

        expectMajorType(ib, FLOAT_SIMPLE);

        int b = (ib & 0x1f);
        if (b != FALSE && b != TRUE) {
            fail("Unexpected boolean value: %d!", b);
        }

        return b == TRUE;
    }

    /**
     * Reads a "break"/stop value in CBOR format.
     * 
     * @return always <code>null</code>.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public Object readBreak() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(BREAK));

        return null;
    }

    /**
     * Reads a byte string value in CBOR format.
     * 
     * @return the read byte string, never <code>null</code>. In case the encoded string has a length of <tt>0</tt>, an empty string is returned.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public byte[] readByteString() throws IOException {
        int ib = read();

        expectMajorType(ib, BYTE_STRING);

        long len = readUInt(ib & 0x1f, true /* unlimitedAllowed */);
        if (len < 0) {
            fail("Unlimited length strings not supported by readString()!");
        }
        if (len > Integer.MAX_VALUE) {
            fail("String length too long!");
        }

        return readFully(new byte[(int) len]);
    }

    /**
     * Reads a double-precision float value in CBOR format.
     * 
     * @return the read double value, values from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public double readDouble() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(DOUBLE_PRECISION_FLOAT));

        return Double.longBitsToDouble(readUInt64());
    }

    /**
     * Reads a single-precision float value in CBOR format.
     * 
     * @return the read float value, values from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public float readFloat() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(SINGLE_PRECISION_FLOAT));

        return Float.intBitsToFloat((int) readUInt32());
    }

    /**
     * Reads a half-precision float value in CBOR format.
     * 
     * @return the read half-precision float value, values from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public double readHalfPrecisionFloat() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(HALF_PRECISION_FLOAT));

        int half = readUInt16();
        int exp = (half >> 10) & 0x1f;
        int mant = half & 0x3ff;

        double val;
        if (exp == 0) {
            val = mant * Math.pow(2, -24);
        } else if (exp != 31) {
            val = (mant + 1024) * Math.pow(2, exp - 25);
        } else if (mant != 0) {
            val = Double.NaN;
        } else {
            val = Double.POSITIVE_INFINITY;
        }

        return ((half & 0x8000) == 0) ? val : -val;
    }

    /**
     * Reads a signed or unsigned integer value in CBOR format.
     * 
     * @return the read integer value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public long readInt() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        long ui = -majorType.ordinal() >> 63;
        // in case of negative integers does a ones complement
        return ui ^ readUInt(ib & 0x1f);
    }

    /**
     * Reads a signed or unsigned 16-bit integer value in CBOR format.
     * 
     * @read the small integer value, values from <tt>[-65536..65535]</tt> are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying output stream.
     */
    public int readInt16() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }
        int len = ib & 0x1f;
        if (len != TWO_BYTES) {
            fail("Expected two-byte integer value!");
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        int ui = -majorType.ordinal() >> 31;

        // in case of negative integers does a ones complement
        return ui ^ readUInt16();
    }

    /**
     * Reads a signed or unsigned 32-bit integer value in CBOR format.
     * 
     * @read the small integer value, values in the range <tt>[-4294967296..4294967295]</tt> are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying output stream.
     */
    public long readInt32() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }
        int len = ib & 0x1f;
        if (len != FOUR_BYTES) {
            fail("Expected four-byte integer value!");
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        long ui = -majorType.ordinal() >> 63;

        // in case of negative integers does a ones complement
        return ui ^ readUInt32();
    }

    /**
     * Reads a signed or unsigned 64-bit integer value in CBOR format.
     * 
     * @read the small integer value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying output stream.
     */
    public long readInt64() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }
        int len = ib & 0x1f;
        if (len != EIGHT_BYTES) {
            fail("Expected eight-byte integer value!");
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        long ui = -majorType.ordinal() >> 63;

        // in case of negative integers does a ones complement
        return ui ^ readUInt64();
    }

    /**
     * Reads a signed or unsigned 8-bit integer value in CBOR format.
     * 
     * @read the small integer value, values in the range <tt>[-256..255]</tt> are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying output stream.
     */
    public int readInt8() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }
        int len = ib & 0x1f;
        if (len != ONE_BYTE) {
            fail("Expected one-byte integer value!");
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        int ui = -majorType.ordinal() >> 31;

        // in case of negative integers does a ones complement
        return ui ^ readUInt8();
    }

    /**
     * Reads a map of key-value pairs in CBOR format.
     * 
     * @return the read map, never <code>null</code>.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public Map<?, ?> readMap() throws IOException {
        int ib = read();

        expectMajorType(ib, MAP);

        long len = readUInt(ib & 0x1f, true /* unlimitedAllowed */);
        if (len < 0) {
            fail("Unlimited length maps not supported by readMap()!");
        }

        Map<Object, Object> result = new HashMap<>();
        while (len-- > 0) {
            result.put(readGenericItem(), readGenericItem());
        }
        return result;
    }

    /**
     * Reads a <code>null</code>-value in CBOR format.
     * 
     * @return always <code>null</code>.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public Object readNull() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(NULL));

        return null;
    }

    /**
     * Reads a single byte value in CBOR format.
     * 
     * @return the read byte value.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public byte readSimpleValue() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(ONE_BYTE));

        return (byte) readUInt8();
    }

    /**
     * Reads a signed or unsigned small (&lt;= 23) integer value in CBOR format.
     * 
     * @read the small integer value, values in the range <tt>[-24..23]</tt> are supported.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying output stream.
     */
    public int readSmallInt() throws IOException {
        int ib = read();

        CborType majorType = decode(ib);
        if ((majorType != UNSIGNED_INTEGER) && (majorType != NEGATIVE_INTEGER)) {
            fail("Unexpected type: %s, expected type %s or %s!", majorType, UNSIGNED_INTEGER, NEGATIVE_INTEGER);
        }

        // in case of negative integers, extends the sign to all bits; otherwise zero...
        int ui = -majorType.ordinal() >> 31;
        int len = ib & 0x1f;
        if (len >= ONE_BYTE) {
            fail("Expected small integer value, but got a multi-byte value!");
        }

        // in case of negative integers does a ones complement
        return ui ^ len;
    }

    /**
     * Reads a semantic tag value in CBOR format.
     * 
     * @return the read tag value.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public long readTag() throws IOException {
        int ib = read();

        expectMajorType(ib, TAG);

        return readUInt(ib & 0x1f, false /* unlimitedAllowed */);
    }

    /**
     * Reads an UTF-8 encoded string value in CBOR format.
     * 
     * @return the read UTF-8 encoded string, never <code>null</code>. In case the encoded string has a length of <tt>0</tt>, an empty string is returned.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public String readTextString() throws IOException {
        int ib = read();

        expectMajorType(ib, TEXT_STRING);

        long len = readUInt(ib & 0x1f, true /* unlimitedAllowed */);
        if (len < 0) {
            fail("Unlimited length strings not supported by readUTF8String()!");
        }
        if (len > Integer.MAX_VALUE) {
            fail("String length too long!");
        }

        return new String(readFully(new byte[(int) len]), "UTF-8");
    }

    /**
     * Reads an undefined value in CBOR format.
     * 
     * @return always <code>null</code>.
     * @throws IOException in case of I/O problems reading the CBOR-encoded value from the underlying input stream.
     */
    public Object readUndefined() throws IOException {
        int ib = read();

        expectMajorTypeExact(ib, FLOAT_SIMPLE.encode(UNDEFINED));

        return null;
    }

    protected void expectMajorType(int input, CborType majorType) throws IOException {
        CborType mt = decode(input);
        if (mt != majorType) {
            fail("Unexpected type: %s, expected type %s!", mt, majorType);
        }
    }

    protected void expectMajorTypeExact(int type, int majorType) throws IOException {
        if (type != majorType) {
            fail("Unexpected type: %d, expected type %d!", type, majorType);
        }
    }

    /**
     * Reads any given item in CBOR-encoded format by introspecting its type.
     * 
     * @return the read item, can be <code>null</code> in case a {@link CborConstants#NULL} value is found.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected Object readGenericItem() throws IOException {
        int ib = peek();

        CborType mt = decode(ib);
        if (mt == UNSIGNED_INTEGER || mt == NEGATIVE_INTEGER) {
            return readInt();
        } else if (mt == BYTE_STRING) {
            return readByteString();
        } else if (mt == TEXT_STRING) {
            return readTextString();
        } else if (mt == ARRAY) {
            return readArray();
        } else if (mt == MAP) {
            return readMap();
        } else if (mt == TAG) {
            return null; // XXX
        } else if (mt == FLOAT_SIMPLE) {
            int subtype = ib & 0x1f;
            if (subtype < ONE_BYTE) {
                if (subtype == FALSE || subtype == TRUE) {
                    return readBoolean();
                } else if (subtype == NULL) {
                    return readNull();
                } else if (subtype == UNDEFINED) {
                    return readUndefined();
                }
            } else if (subtype == ONE_BYTE) {
                return readSimpleValue();
            } else if (subtype == HALF_PRECISION_FLOAT) {
                return readHalfPrecisionFloat();
            } else if (subtype == SINGLE_PRECISION_FLOAT) {
                return readFloat();
            } else if (subtype == DOUBLE_PRECISION_FLOAT) {
                return readDouble();
            } else if (subtype == BREAK) {
                return readBreak();
            }

            fail("Unexpected subtype: %d!", subtype);
        }

        fail("Unexpected type: %d!", mt);
        return null; // to keep compiler happy...
    }

    /**
     * Reads an unsigned integer with a given length-indicator.
     * 
     * @param length the length indicator to use.
     * @return the read unsigned integer, as long value.
     * @throws IOException in case of I/O problems reading the unsigned integer from the underlying input stream.
     * @see #readUInt(int, boolean)
     */
    protected long readUInt(int length) throws IOException {
        return readUInt(length, false /* unlimitedAllowed */);
    }

    /**
     * Reads an unsigned integer with a given length-indicator.
     * 
     * @param length the length indicator to use;
     * @param unlimitedAllowed <code>true</code> if a length of {@link CborConstants#BREAK} is allowed, <code>false</code> otherwise.
     * @return the read unsigned integer, as long value. Returns <tt>-1L</tt> if the given length was {@link CborConstants#BREAK}.
     * @throws IOException in case of I/O problems reading the unsigned integer from the underlying input stream.
     */
    protected long readUInt(int length, boolean unlimitedAllowed) throws IOException {
        if (length < ONE_BYTE) {
            return length;
        } else if (length == ONE_BYTE) {
            int result = readUInt8();
            return result;
        } else if (length == TWO_BYTES) {
            int result = readUInt16();
            return result;
        } else if (length == FOUR_BYTES) {
            long result = readUInt32();
            return result;
        } else if (length == EIGHT_BYTES) {
            long result = readUInt64();
            return result;
        } else if (unlimitedAllowed && length == BREAK) {
            return -1L;
        }

        fail("Not well-formed CBOR integer found, unexpected length: %d!", length);
        return -1L; // never reached...
    }

    /**
     * Reads an unsigned 16-bit integer value
     * 
     * @return value the read value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected int readUInt16() throws IOException {
        byte[] buf = readFully(new byte[2]);
        return (buf[0] & 0xFF) << 8 | (buf[1] & 0xFF);
    }

    /**
     * Reads an unsigned 32-bit integer value
     * 
     * @return value the read value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected long readUInt32() throws IOException {
        byte[] buf = readFully(new byte[4]);
        return ((buf[0] & 0xFF) << 24 | (buf[1] & 0xFF) << 16 | (buf[2] & 0xFF) << 8 | (buf[3] & 0xFF)) & 0xffffffffL;
    }

    /**
     * Reads an unsigned 64-bit integer value
     * 
     * @return value the read value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected long readUInt64() throws IOException {
        byte[] buf = readFully(new byte[8]);
        return (buf[0] & 0xFFL) << 56 | (buf[1] & 0xFFL) << 48 | (buf[2] & 0xFFL) << 40 | (buf[3] & 0xFFL) << 32 | //
            (buf[4] & 0xFFL) << 24 | (buf[5] & 0xFFL) << 16 | (buf[6] & 0xFFL) << 8 | (buf[7] & 0xFFL);
    }

    /**
     * Reads an unsigned 8-bit integer value
     * 
     * @return value the read value, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected int readUInt8() throws IOException {
        return read() & 0xff;
    }

    /**
     * Peeks at the next byte in the underlying input stream.
     * <p>
     * Note that this does not consume the next byte! This means that after a call to this method,
     * a call to {@link #read()} should be made to consume it!
     * </p>
     * 
     * @return the peeked result, or <tt>-1</tt> if the end-of-stream is reached.
     * @throws IOException in case of I/O problems reading from the underlying input stream.
     */
    private int peek() throws IOException {
        int ib = read();
        ((PushbackInputStream) this.in).unread(ib);
        return ib;
    }

    private byte[] readFully(byte[] buf) throws IOException {
        int len = buf.length;
        int n = 0, off = 0;
        while (n < len) {
            int count = read(buf, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
        return buf;
    }
}
