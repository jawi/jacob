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

/**
 * Base class for all {@link CborOutputStream} test cases.
 */
public class CborOutputStreamTestBase<T> {
    protected final T m_input;
    protected final CborOutputStream m_stream;

    private final int[] m_encodedOutput;
    private final ByteArrayOutputStream m_baos;

    public CborOutputStreamTestBase(T input, int[] encodedOutput) {
        m_input = input;
        m_encodedOutput = encodedOutput;

        m_baos = new ByteArrayOutputStream();
        m_stream = new CborOutputStream(m_baos);
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
