/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for decoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborInputStreamSmallIntegerTest extends CborInputStreamTestBase<Integer> {
    private final Class<? extends Exception> m_exceptionClass;

    public CborInputStreamSmallIntegerTest(int output, int[] encodedInput, Class<? extends Exception> exceptionType) {
        super(encodedInput, output);

        m_exceptionClass = exceptionType;
    }

    @Parameters(name = "{index}: decoding small integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x00 }, NONE }, // 0
            new Object[] { 1, new int[] { 0x01 }, NONE }, // 1
            new Object[] { 10, new int[] { 0x0a }, NONE }, // 2
            new Object[] { 23, new int[] { 0x17 }, NONE }, // 3
            new Object[] { 23, new int[] { 0x18, 0x01 }, IOException.class }, // 4
            new Object[] { -1, new int[] { 0x20 }, NONE }, // 5
            new Object[] { -10, new int[] { 0x29 }, NONE }, // 6
            new Object[] { -24, new int[] { 0x37 }, NONE }, // 7
            new Object[] { -25, new int[] { 0x38, 0x01 }, IOException.class } // 8
            );
    }

    @Test
    public void test() throws IOException {
        if (m_exceptionClass == null) {
            assertEquals(m_expectedOutput.intValue(), m_stream.readSmallInt());
        } else {
            try {
                m_stream.readSmallInt();

                fail(m_exceptionClass);
            }
            catch (Exception e) {
                assertException(m_exceptionClass, e);
            }
        }
    }
}
