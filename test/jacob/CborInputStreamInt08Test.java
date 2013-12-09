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
public class CborInputStreamInt08Test extends CborInputStreamTestBase<Integer> {
    private final Class<? extends Exception> m_exceptionClass;

    public CborInputStreamInt08Test(int output, int[] encodedInput, Class<? extends Exception> exceptionType) {
        super(encodedInput, output);

        m_exceptionClass = exceptionType;
    }

    @Parameters(name = "{index}: decoding 8-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x18, 0x00 }, NONE }, // 0
            new Object[] { 1, new int[] { 0x18, 0x01 }, NONE }, // 1
            new Object[] { 10, new int[] { 0x18, 0x0a }, NONE }, // 2
            new Object[] { 23, new int[] { 0x18, 0x17 }, NONE }, // 3
            new Object[] { 24, new int[] { 0x18, 0x18 }, NONE }, // 4
            new Object[] { 25, new int[] { 0x18, 0x19 }, NONE }, // 5
            new Object[] { 100, new int[] { 0x18, 0x64 }, NONE }, // 6
            new Object[] { 255, new int[] { 0x18, 0xff }, NONE }, // 7
            new Object[] { 255, new int[] { 0x19, 0x00, 0x01 }, IOException.class }, // 8
            new Object[] { -1, new int[] { 0x38, 0x00 }, NONE }, // 9
            new Object[] { -10, new int[] { 0x38, 0x09 }, NONE }, // 10
            new Object[] { -24, new int[] { 0x38, 0x17 }, NONE }, // 11
            new Object[] { -25, new int[] { 0x38, 0x18 }, NONE }, // 12
            new Object[] { -100, new int[] { 0x38, 0x63 }, NONE }, // 13
            new Object[] { -255, new int[] { 0x38, 0xfe }, NONE }, // 14
            new Object[] { -256, new int[] { 0x38, 0xff }, NONE }, // 15
            new Object[] { -256, new int[] { 0x39, 0x00, 0x00 }, IOException.class } // 16
            );
    }

    @Test
    public void test() throws IOException {
        if (m_exceptionClass == null) {
            assertEquals(m_expectedOutput.intValue(), m_stream.readInt8());
        } else {
            try {
                m_stream.readInt8();

                fail(m_exceptionClass);
            }
            catch (Exception e) {
                assertException(m_exceptionClass, e);
            }
        }
    }
}
