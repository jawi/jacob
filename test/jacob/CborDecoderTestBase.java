/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static jacob.CborConstants.*;
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
        // Peek at the next type...
        CborType type = m_stream.peekType();
        
        int mt = type.getMajorType();

        if (mt == TYPE_UNSIGNED_INTEGER || mt == TYPE_NEGATIVE_INTEGER) {
            return m_stream.readInt();
        } else if (mt == TYPE_BYTE_STRING) {
            return m_stream.readByteString();
        } else if (mt == TYPE_TEXT_STRING) {
            return m_stream.readTextString();
        } else if (mt == TYPE_ARRAY) {
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
        } else if (mt == TYPE_MAP) {
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
        } else if (mt == TYPE_TAG) {
            return m_stream.readTag();
        } else if (mt == TYPE_FLOAT_SIMPLE) {
            int subtype = type.getAdditionalInfo();
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
