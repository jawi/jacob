/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import java.io.ByteArrayInputStream;

import org.junit.Assert;

/**
 * Base class for all {@link CborEncoder} test cases.
 */
public class CborInputStreamTestBase<T> {
    protected static final Exception NONE = null;

    protected final T m_expectedOutput;
    protected final CborInputStream m_stream;

    private final ByteArrayInputStream m_bais;

    public CborInputStreamTestBase(int[] encodedInput, T output) {
        m_expectedOutput = output;

        byte[] buf = new byte[encodedInput.length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) encodedInput[i];
        }

        m_bais = new ByteArrayInputStream(buf);
        m_stream = new CborInputStream(m_bais);
    }

    protected static void fail(Class<? extends Exception> expectedType) {
        Assert.fail("Exception " + expectedType.getSimpleName() + " expected!");
    }

    protected static void assertException(Class<? extends Exception> expectedType, Exception actual) {
        Assert.assertNotNull("Exception " + expectedType.getSimpleName() + " expected, got null!", actual);

        Class<? extends Exception> actualType = actual.getClass();
        Assert.assertTrue("Exception " + expectedType.getSimpleName() + " expected, got " + actualType.getSimpleName(),
            expectedType.isAssignableFrom(actualType));
    }
}
