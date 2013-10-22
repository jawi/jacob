/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import java.io.ByteArrayInputStream;

/**
 * Base class for all {@link CborOutputStream} test cases.
 */
public class CborInputStreamTestBase<T> {
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
}
