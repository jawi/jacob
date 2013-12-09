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
public class CborInputStreamInt16Test extends CborInputStreamTestBase<Integer> {
    private final Class<? extends Exception> m_exceptionClass;

    public CborInputStreamInt16Test(int output, int[] encodedInput, Class<? extends Exception> exceptionType) {
        super(encodedInput, output);

        m_exceptionClass = exceptionType;
    }

    @Parameters(name = "{index}: decoding 16-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x19, 0x00, 0x00 }, NONE }, // 0
            new Object[] { 1, new int[] { 0x19, 0x00, 0x01 }, NONE }, // 1
            new Object[] { 100, new int[] { 0x19, 0x00, 0x64 }, NONE }, // 2
            new Object[] { 500, new int[] { 0x19, 0x01, 0xf4 }, NONE }, // 3
            new Object[] { 1000, new int[] { 0x19, 0x03, 0xe8 }, NONE }, // 4
            new Object[] { 65535, new int[] { 0x19, 0xff, 0xff }, NONE }, // 5
            new Object[] { 65535, new int[] { 0x20, 0x00, 0x00, 0x00, 0x01 }, IOException.class }, // 6
            new Object[] { -1, new int[] { 0x39, 0x00, 0x00, }, NONE }, // 7
            new Object[] { -100, new int[] { 0x39, 0x00, 0x63 }, NONE }, // 8
            new Object[] { -500, new int[] { 0x39, 0x01, 0xf3 }, NONE }, // 9
            new Object[] { -1000, new int[] { 0x39, 0x03, 0xe7 }, NONE }, // 10
            new Object[] { -65535, new int[] { 0x39, 0xff, 0xfe }, NONE }, // 11
            new Object[] { -65536, new int[] { 0x39, 0xff, 0xff }, NONE }, // 12
            new Object[] { -65537, new int[] { 0x40, 0x00, 0x00, 0x00, 0x01 }, IOException.class }, // 13
            new Object[] { 0, new int[] { 0x00 }, IOException.class } // 14
            );
    }

    @Test
    public void test() throws IOException {
        if (m_exceptionClass == null) {
            assertEquals(m_expectedOutput.intValue(), m_stream.readInt16());
        } else {
            try {
                m_stream.readInt16();

                fail(m_exceptionClass);
            }
            catch (Exception e) {
                assertException(m_exceptionClass, e);
            }
        }
    }
}
