/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static jacob.CborConstants.*;
import static jacob.CborType.*;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides an {@link OutputStream} capable of encoding data into CBOR format.
 */
public class CborOutputStream extends FilterOutputStream {
    /**
     * Creates a new {@link CborOutputStream} instance.
     * 
     * @param os the actual output stream to write the CBOR-encoded data to, cannot be <code>null</code>.
     */
    public CborOutputStream(OutputStream os) {
        super(os);
    }

    /**
     * Interprets a given float-value as a half-precision float value and
     * converts it to its raw integer form, as defined in IEEE 754.
     * <p>
     * Taken from: <a href="http://stackoverflow.com/a/6162687/229140">this Stack Overflow answer</a>.
     * </p>
     * 
     * @param fval the value to convert.
     * @return the raw integer representation of the given float value.
     */
    static int halfPrecisionToRawIntBits(float fval) {
        int fbits = Float.floatToIntBits(fval);
        int sign = (fbits >>> 16) & 0x8000;
        int val = (fbits & 0x7fffffff) + 0x1000;

        // might be or become NaN/Inf
        if (val >= 0x47800000) {
            if ((fbits & 0x7fffffff) >= 0x47800000) { // is or must become NaN/Inf
                if (val < 0x7f800000) {
                    // was value but too large, make it +/-Inf
                    return sign | 0x7c00;
                }
                return sign | 0x7c00 | (fbits & 0x007fffff) >>> 13; // keep NaN (and Inf) bits
            }
            return sign | 0x7bff; // unrounded not quite Inf
        }
        if (val >= 0x38800000) {
            // remains normalized value
            return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
        }
        if (val < 0x33000000) {
            // too small for subnormal
            return sign; // becomes +/-0
        }

        val = (fbits & 0x7fffffff) >>> 23;
        // add subnormal bit, round depending on cut off and div by 2^(1-(exp-127+15)) and >> 13 | exp=0
        return sign | ((fbits & 0x7fffff | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val);
    }

    /**
     * Writes an "indefinite" generic array value in canonical CBOR format.
     * <p>
     * Note that string elements are always encoded in UTF8 format.
     * </p>
     * 
     * @param value the array to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeArray(Iterable<?> value) throws IOException {
        writeArray((Iterator<?>) (value == null ? null : value.iterator()));
    }

    /**
     * Writes an "indefinite" generic array value in canonical CBOR format.
     * <p>
     * Note that string elements are always encoded in UTF8 format.
     * </p>
     * 
     * @param value the array to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeArray(Iterator<?> value) throws IOException {
        write(ARRAY.encode(BREAK));
        if (value != null) {
            while (value.hasNext()) {
                writeGenericItem(value.next());
            }
        }
        writeBreak();
    }

    /**
     * Writes a generic array value in canonical CBOR format.
     * <p>
     * Note that string elements are always encoded in UTF8 format.
     * </p>
     * 
     * @param value the array to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeArray(Object[] value) throws IOException {
        writeArray(value, (value == null) ? 0 : value.length);
    }

    /**
     * Writes the start of an indefinite-length array.
     * <p>
     * After calling this method, one is expected to write the given number of array elements, which can be of any type. No length checks are performed.<br/>
     * After all array elements are written, one should write a single break value to end the array, see {@link #writeBreak()}.
     * </p>
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeArrayStart() throws IOException {
        write(ARRAY.encode(BREAK));
    }

    /**
     * Writes the start of a finite-length array.
     * <p>
     * After calling this method, one is expected to write the given number of array elements, which can be of any type. No length checks are performed.
     * </p>
     * 
     * @param length the number of array elements to write, should &gt;= 0.
     * @throws IllegalArgumentException in case the given length was negative;
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeArrayStart(int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Invalid array-length!");
        }
        writeType(ARRAY, length);
    }

    /**
     * Writes a boolean value in canonical CBOR format.
     * 
     * @param value the boolean to write.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeBoolean(boolean value) throws IOException {
        write(FLOAT_SIMPLE.encode(value ? TRUE : FALSE));
    }

    /**
     * Writes a "break" stop-value in canonical CBOR format.
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeBreak() throws IOException {
        write(FLOAT_SIMPLE.encode(BREAK));
    }

    /**
     * Writes a byte string in canonical CBOR-format.
     * 
     * @param value the byte string to write, can be <code>null</code> in which case a byte-string of length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeByteString(byte[] bytes) throws IOException {
        writeString(BYTE_STRING, bytes);
    }

    /**
     * Writes a byte string in canonical CBOR-format.
     * <p>
     * Note that this method is <em>platform</em> specific, as the given string value will be encoded in a byte array
     * using the <em>platform</em> encoding! This means that the encoding must be standardized and known.
     * </p>
     * 
     * @param value the byte string to write, can be <code>null</code> in which case a byte-string of length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeByteString(String value) throws IOException {
        writeString(BYTE_STRING, value == null ? null : value.getBytes());
    }

    /**
     * Writes the start of an indefinite-length byte string.
     * <p>
     * After calling this method, one is expected to write the given number of string parts. No length checks are performed.<br/>
     * After all string parts are written, one should write a single break value to end the string, see {@link #writeBreak()}.
     * </p>
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeByteStringStart() throws IOException {
        write(BYTE_STRING.encode(BREAK));
    }

    /**
     * Writes a double-precision float value in canonical CBOR format.
     * 
     * @param value the value to write, values from {@link Double#MIN_VALUE} to {@link Double#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeDouble(double value) throws IOException {
        writeUInt64(FLOAT_SIMPLE.bitMask(), Double.doubleToRawLongBits(value));
    }

    /**
     * Writes a single-precision float value in canonical CBOR format.
     * 
     * @param value the value to write, values from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeFloat(float value) throws IOException {
        writeUInt32(FLOAT_SIMPLE.bitMask(), Float.floatToRawIntBits(value));
    }

    /**
     * Writes a half-precision float value in canonical CBOR format.
     * 
     * @param value the value to write, values from {@link Float#MIN_VALUE} to {@link Float#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeHalfPrecisionFloat(float value) throws IOException {
        writeUInt16(FLOAT_SIMPLE.bitMask(), halfPrecisionToRawIntBits(value));
    }

    /**
     * Writes a signed or unsigned integer value in canonical CBOR format, that is, tries to encode it in a little bytes as possible..
     * 
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeInt(long value) throws IOException {
        // extends the sign over all bits...
        long sign = value >> 63;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        value = (sign ^ value);

        writeUInt(mt, value);
    }

    /**
     * Writes a signed or unsigned 16-bit integer value in CBOR format.
     * 
     * @param value the value to write, values from <tt>[-65536..65535]</tt> are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeInt16(int value) throws IOException {
        // extends the sign over all bits...
        int sign = value >> 31;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        writeUInt16(mt, (sign ^ value) & 0xffff);
    }

    /**
     * Writes a signed or unsigned 32-bit integer value in CBOR format.
     * 
     * @param value the value to write, values in the range <tt>[-4294967296..4294967295]</tt> are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeInt32(long value) throws IOException {
        // extends the sign over all bits...
        long sign = value >> 63;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        writeUInt32(mt, (int) ((sign ^ value) & 0xffffffffL));
    }

    /**
     * Writes a signed or unsigned 64-bit integer value in CBOR format.
     * 
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeInt64(long value) throws IOException {
        // extends the sign over all bits...
        long sign = value >> 63;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        writeUInt64(mt, sign ^ value);
    }

    /**
     * Writes a signed or unsigned 8-bit integer value in CBOR format.
     * 
     * @param value the value to write, values in the range <tt>[-256..255]</tt> are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeInt8(int value) throws IOException {
        // extends the sign over all bits...
        int sign = value >> 31;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        writeUInt8(mt, (sign ^ value) & 0xff);
    }

    /**
     * Writes an "indefinite" generic array value in canonical CBOR format.
     * <p>
     * Note that string keys and/or values are encoded in UTF8 format.
     * </p>
     * 
     * @param value the map entries to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeMap(Iterable<Map.Entry<?, ?>> value) throws IOException {
        writeMap((Iterator<Map.Entry<?, ?>>) (value == null ? null : value.iterator()));
    }

    /**
     * Writes an "indefinite" generic array value in canonical CBOR format.
     * <p>
     * Note that string keys and/or values are encoded in UTF8 format.
     * </p>
     * 
     * @param value the map entries to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeMap(Iterator<Map.Entry<?, ?>> value) throws IOException {
        write(MAP.encode(BREAK));
        if (value != null) {
            while (value.hasNext()) {
                Map.Entry<?, ?> entry = value.next();
                writeGenericItem(entry.getKey());
                writeGenericItem(entry.getValue());
            }
        }
        writeBreak();
    }

    /**
     * Writes a generic array value in canonical CBOR format.
     * <p>
     * Note that string keys and/or values are encoded in UTF8 format.
     * </p>
     * 
     * @param value the map to write, can be <code>null</code> in which an array with length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeMap(Map<?, ?> value) throws IOException {
        int len = (value == null) ? 0 : value.size();
        writeType(MAP, len);
        if (value != null) {
            for (Map.Entry<?, ?> entry : value.entrySet()) {
                writeGenericItem(entry.getKey());
                writeGenericItem(entry.getValue());
            }
        }
    }

    /**
     * Writes the start of an indefinite-length map.
     * <p>
     * After calling this method, one is expected to write any number of map entries, as separate key and value. Keys and values can both be of any type. No length checks are performed.<br/>
     * After all map entries are written, one should write a single break value to end the map, see {@link #writeBreak()}.
     * </p>
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeMapStart() throws IOException {
        write(MAP.encode(BREAK));
    }

    /**
     * Writes the start of a finite-length map.
     * <p>
     * After calling this method, one is expected to write any number of map entries, as separate key and value. Keys and values can both be of any type. No length checks are performed.
     * </p>
     * 
     * @param length the number of map entries to write, should &gt;= 0.
     * @throws IllegalArgumentException in case the given length was negative;
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeMapStart(int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Invalid length of map!");
        }
        writeType(MAP, length);
    }

    /**
     * Writes a <code>null</code> value in canonical CBOR format.
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeNull() throws IOException {
        write(FLOAT_SIMPLE.encode(NULL));
    }

    /**
     * Writes a simple value, i.e., an "atom" or "constant" value in canonical CBOR format.
     * 
     * @param value the (unsigned byte) value to write, values from <tt>32</tt> to <tt>255</tt> are supported (though not enforced).
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeSimpleValue(byte simpleValue) throws IOException {
        // convert to unsigned value...
        int value = (simpleValue & 0xff);
        writeType(FLOAT_SIMPLE, value);
    }

    /**
     * Writes a signed or unsigned small (&lt;= 23) integer value in CBOR format.
     * 
     * @param value the value to write, values in the range <tt>[-24..23]</tt> are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeSmallInt(int value) throws IOException {
        // extends the sign over all bits...
        int sign = value >> 31;
        // in case value is negative, this bit should be set...
        int mt = (int) (sign & NEGATIVE_INTEGER.bitMask());
        // complement negative value...
        value = Math.min(0x17, (sign ^ value));

        write((int) (mt | value));
    }

    /**
     * Writes a semantic tag in canonical CBOR format.
     * 
     * @param tag the tag to write, should &gt;= 0;
     * @param data the tagged data to write, can be <code>null</code>.
     * @throws IllegalArgumentException in case the given tag was negative;
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeTag(int tag, Object data) throws IOException {
        if (tag < 0) {
            throw new IllegalArgumentException("Invalid tag specification, cannot be negative!");
        }
        writeType(TAG, tag);

        // XXX note sure whether this is the right place...
        switch (tag) {
            case 0:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                writeTextString(String.valueOf(data));
                break;
            case 2:
            case 3:
            case 23:
            case 24:
                writeByteString(String.valueOf(data));
                break;
            default:
                writeGenericItem(data);
                break;
        }
    }

    /**
     * Writes an UTF-8 string in canonical CBOR-format.
     * <p>
     * Note that this method is <em>platform</em> specific, as the given string value will be encoded in a byte array
     * using the <em>platform</em> encoding! This means that the encoding must be standardized and known.
     * </p>
     * 
     * @param value the UTF-8 string to write, can be <code>null</code> in which case an UTF-8 string of length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeTextString(String value) throws IOException {
        writeString(TEXT_STRING, value == null ? null : value.getBytes("UTF-8"));
    }

    /**
     * Writes the start of an indefinite-length UTF-8 string.
     * <p>
     * After calling this method, one is expected to write the given number of string parts. No length checks are performed.<br/>
     * After all string parts are written, one should write a single break value to end the string, see {@link #writeBreak()}.
     * </p>
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeTextStringStart() throws IOException {
        write(TEXT_STRING.encode(BREAK));
    }

    /**
     * Writes an "undefined" value in canonical CBOR format.
     * 
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    public void writeUndefined() throws IOException {
        write(FLOAT_SIMPLE.encode(UNDEFINED));
    }

    /**
     * Writes a generic array value in canonical CBOR format.
     * 
     * @param array the array to write, can be <code>null</code> in which an array with length <tt>0</tt> is written;
     * @param length the number of elements in the array, >= 0.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeArray(Object array, int length) throws IOException {
        writeType(ARRAY, length);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            writeGenericItem(item);
        }
    }

    /**
     * Writes any given item in CBOR-encoded format by introspecting its type.
     * 
     * @param item the item to write, can be <code>null</code> in which case a {@link CborConstants#NULL} value is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeGenericItem(Object item) throws IOException {
        if (item instanceof Number) {
            Number num = (Number) item;
            if (item instanceof Double) {
                writeDouble(num.doubleValue());
            } else if (item instanceof Float) {
                writeFloat(num.floatValue());
            } else {
                writeInt(num.longValue());
            }
        } else if (item instanceof String) {
            writeTextString((String) item);
        } else if (item instanceof Boolean) {
            writeBoolean((Boolean) item);
        } else if (item instanceof Map) {
            writeMap((Map<?, ?>) item);
        } else if (item != null) {
            Class<?> type = item.getClass();
            if (type.isArray()) {
                writeArray(item, Array.getLength(item));
            } else {
                // XXX Tags...
                throw new IOException("Unknown/unhandled component type: " + type);
            }
        } else {
            writeNull();
        }
    }

    /**
     * Writes a byte string in canonical CBOR-format.
     * 
     * @param majorType the major type of the string, should be either 0x40 or 0x60;
     * @param value the byte string to write, can be <code>null</code> in which case a byte-string of length <tt>0</tt> is written.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeString(CborType majorType, byte[] bytes) throws IOException {
        int len = (bytes == null) ? 0 : bytes.length;
        writeType(majorType, len);
        for (int i = 0; i < len; i++) {
            write(bytes[i]);
        }
    }

    /**
     * Encodes and writes the major type indicator with a given payload (length).
     * 
     * @param majorType the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeType(CborType majorType, long value) throws IOException {
        writeUInt(majorType.bitMask(), value);
    }

    /**
     * Encodes and writes an unsigned integer value, that is, tries to encode it in a little bytes as possible.
     * 
     * @param mt the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeUInt(int mt, long value) throws IOException {
        if (value < 0x18L) {
            write((int) (mt | value));
        } else if (value < 0x100L) {
            writeUInt8(mt, (int) value);
        } else if (value < 0x10000L) {
            writeUInt16(mt, (int) value);
        } else if (value < 0x100000000L) {
            writeUInt32(mt, (int) value);
        } else {
            writeUInt64(mt, value);
        }
    }

    /**
     * Encodes and writes an unsigned 16-bit integer value
     * 
     * @param mt the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeUInt16(int mt, int value) throws IOException {
        write(mt | TWO_BYTES);
        write(value >> 8);
        write(value & 0xFF);
    }

    /**
     * Encodes and writes an unsigned 32-bit integer value
     * 
     * @param mt the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeUInt32(int mt, int value) throws IOException {
        write(mt | FOUR_BYTES);
        write(value >> 24);
        write(value >> 16);
        write(value >> 8);
        write(value & 0xFF);
    }

    /**
     * Encodes and writes an unsigned 64-bit integer value
     * 
     * @param mt the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeUInt64(int mt, long value) throws IOException {
        write(mt | EIGHT_BYTES);
        write((int) (value >> 56));
        write((int) (value >> 48));
        write((int) (value >> 40));
        write((int) (value >> 32));
        write((int) (value >> 24));
        write((int) (value >> 16));
        write((int) (value >> 8));
        write((int) (value & 0xFF));
    }

    /**
     * Encodes and writes an unsigned 8-bit integer value
     * 
     * @param mt the major type of the value to write, denotes what semantics the written value has;
     * @param value the value to write, values from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE} are supported.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected void writeUInt8(int mt, int value) throws IOException {
        write(mt | ONE_BYTE);
        write(value & 0xFF);
    }
}
