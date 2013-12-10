/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static jacob.CborConstants.BREAK;
import static jacob.CborConstants.DOUBLE_PRECISION_FLOAT;
import static jacob.CborConstants.FALSE;
import static jacob.CborConstants.HALF_PRECISION_FLOAT;
import static jacob.CborConstants.NULL;
import static jacob.CborConstants.ONE_BYTE;
import static jacob.CborConstants.SINGLE_PRECISION_FLOAT;
import static jacob.CborConstants.TRUE;
import static jacob.CborConstants.UNDEFINED;
import static jacob.CborType.ARRAY;
import static jacob.CborType.BYTE_STRING;
import static jacob.CborType.FLOAT_SIMPLE;
import static jacob.CborType.MAP;
import static jacob.CborType.NEGATIVE_INTEGER;
import static jacob.CborType.TAG;
import static jacob.CborType.TEXT_STRING;
import static jacob.CborType.UNSIGNED_INTEGER;
import static jacob.CborType.decode;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Base class for all {@link CborEncoder} test cases.
 */
public class CborDecoderTestBase<T> {
    protected static final Exception NONE = null;

    @Rule
    public ExpectedException m_exception;

    protected final T m_expectedOutput;
    protected final CborDecoder m_stream;

    private final PushbackInputStream m_is;
    private final ByteArrayInputStream m_bais;

    protected CborDecoderTestBase(int[] encodedInput, T output) {
        this(encodedInput, output, null);
    }

    protected CborDecoderTestBase(int[] encodedInput, T output, Class<? extends Exception> exceptionClass) {
        m_expectedOutput = output;

        byte[] buf = new byte[encodedInput.length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) encodedInput[i];
        }

        m_bais = new ByteArrayInputStream(buf);
        m_is = new PushbackInputStream(m_bais);
        m_stream = new CborDecoder(m_is);
        m_exception = ExpectedException.none();

        if (exceptionClass != null) {
            m_exception.expect(exceptionClass);
        }
    }

    protected void assertElementEquals(Object expected, Object actual) {
        if (expected instanceof Object[]) {
            Object[] expectedArray = (Object[]) expected;
            List<?> actualArray = (List<?>) actual;

            for (int i = 0; i < expectedArray.length; i++) {
                assertEquals(expectedArray[i], actualArray.get(i));
            }
        } else {
            assertEquals(expected, actual);
        }
    }

    /**
     * Reads any given item in CBOR-encoded format by introspecting its type.
     * 
     * @return the read item, can be <code>null</code> in case a {@link CborConstants#NULL} value is found.
     * @throws IOException in case of I/O problems writing the CBOR-encoded value to the underlying output stream.
     */
    protected Object readGenericItem() throws IOException {
        // Peek at the next byte...
        int ib = m_is.read();
        m_is.unread(ib);

        CborType mt = decode(ib);

        if (mt == UNSIGNED_INTEGER || mt == NEGATIVE_INTEGER) {
            return m_stream.readInt();
        } else if (mt == BYTE_STRING) {
            return m_stream.readByteString();
        } else if (mt == TEXT_STRING) {
            return m_stream.readTextString();
        } else if (mt == ARRAY) {
            long len = m_stream.readArrayLength();

            List<Object> result = new ArrayList<>();
            for (int i = 0; len < 0 || i < len; i++) {
                Object item = readGenericItem();
                if (len < 0 && (item == null)) {
                    // break read...
                    break;
                }
                result.add(item);
            }
            return result;
        } else if (mt == MAP) {
            long len = m_stream.readMapLength();

            Map<Object, Object> result = new HashMap<>();
            for (long i = 0; len < 0 || i < len; i++) {
                Object key = readGenericItem();
                if (len < 0 && (key == null)) {
                    // break read...
                    break;
                }
                Object value = readGenericItem();
                result.put(key, value);
            }
            return result;
        } else if (mt == TAG) {
            return m_stream.readTag();
        } else if (mt == FLOAT_SIMPLE) {
            int subtype = ib & 0x1f;
            if (subtype < ONE_BYTE) {
                if (subtype == FALSE || subtype == TRUE) {
                    return m_stream.readBoolean();
                } else if (subtype == NULL) {
                    return m_stream.readNull();
                } else if (subtype == UNDEFINED) {
                    return m_stream.readUndefined();
                }
            } else if (subtype == ONE_BYTE) {
                return m_stream.readSimpleValue();
            } else if (subtype == HALF_PRECISION_FLOAT) {
                return m_stream.readHalfPrecisionFloat();
            } else if (subtype == SINGLE_PRECISION_FLOAT) {
                return m_stream.readFloat();
            } else if (subtype == DOUBLE_PRECISION_FLOAT) {
                return m_stream.readDouble();
            } else if (subtype == BREAK) {
                return m_stream.readBreak();
            }
        }

        Assert.fail("Unexpected type: " + mt + "!");
        return null; // to keep compiler happy...
    }
}
