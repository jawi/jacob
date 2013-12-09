/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;

/**
 * Base class for all {@link CborEncoder} test cases.
 */
public class CborEncoderTestBase<T> {
    protected final T m_input;
    protected final CborEncoder m_stream;

    private final int[] m_encodedOutput;
    private final ByteArrayOutputStream m_baos;

    public CborEncoderTestBase(T input, int[] encodedOutput) {
        m_input = input;
        m_encodedOutput = encodedOutput;

        m_baos = new ByteArrayOutputStream();
        m_stream = new CborEncoder(m_baos);
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
                m_stream.writeDouble(num.doubleValue());
            } else if (item instanceof Float) {
                m_stream.writeFloat(num.floatValue());
            } else {
                m_stream.writeInt(num.longValue());
            }
        } else if (item instanceof String) {
            m_stream.writeTextString((String) item);
        } else if (item instanceof Boolean) {
            m_stream.writeBoolean((Boolean) item);
        } else if (item instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) item;
            m_stream.writeMapStart(map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                writeGenericItem(entry.getKey());
                writeGenericItem(entry.getValue());
            }
        } else if (item != null) {
            Class<?> type = item.getClass();
            if (type.isArray()) {
                int len = Array.getLength(item);
                m_stream.writeArrayStart(len);
                for (int i = 0; i < len; i++) {
                    writeGenericItem(Array.get(item, i));
                }
            } else {
                throw new IOException("Unknown/unhandled component type: " + type);
            }
        } else {
            m_stream.writeNull();
        }
    }

    protected void assertStreamContentsIsExpected() {
        assertStreamContents(m_encodedOutput, m_baos.toByteArray());
    }

    protected static void assertStreamContents(int[] expected, byte[] input) {
        if (input.length != expected.length) {
            fail(String.format("Not enough bytes! Expected at least %d but only got %d bytes.", expected.length,
                input.length));
        }

        for (int i = 0; i < expected.length; i++) {
            if (input[i] != (byte) expected[i]) {
                fail(String.format("Byte at index %d, expected 0x%02x but was 0x%02x", i, expected[i], input[i]));
            }
        }
    }
}
