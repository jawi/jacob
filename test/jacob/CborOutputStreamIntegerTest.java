/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for encoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborOutputStreamIntegerTest extends CborOutputStreamTestBase<Long> {

    /**
     * Creates a new {@link CborOutputStreamIntegerTest} instance.
     */
    public CborOutputStreamIntegerTest(long input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x01 } }, // 1
            new Object[] { 10, new int[] { 0x0a } }, // 2
            new Object[] { 23, new int[] { 0x17 } }, // 3
            new Object[] { 24, new int[] { 0x18, 0x18 } }, // 4
            new Object[] { 25, new int[] { 0x18, 0x19 } }, // 5
            new Object[] { 100, new int[] { 0x18, 0x64 } }, // 6
            new Object[] { 500, new int[] { 0x19, 0x01, 0xf4 } }, // 7
            new Object[] { 1000, new int[] { 0x19, 0x03, 0xe8 } }, // 8
            new Object[] { 1000000, new int[] { 0x1a, 0x00, 0x0f, 0x42, 0x40 } }, // 9
            new Object[] { 1000000000000L, new int[] { 0x1b, 0x00, 0x00, 0x00, 0xe8, 0xd4, 0xa5, 0x10, 0x00 } }, // 10
            new Object[] { -1, new int[] { 0x20 } }, // 11
            new Object[] { -10, new int[] { 0x29 } }, // 12
            new Object[] { -24, new int[] { 0x37 } }, // 13
            new Object[] { -25, new int[] { 0x38, 0x18 } }, // 14
            new Object[] { -100, new int[] { 0x38, 0x63 } }, // 15
            new Object[] { -500, new int[] { 0x39, 0x01, 0xf3 } }, // 16
            new Object[] { -1000, new int[] { 0x39, 0x03, 0xe7 } }, // 17
            new Object[] { -1000000, new int[] { 0x3a, 0x00, 0x0f, 0x42, 0x3f } }, // 18
            new Object[] { -1000000000000L, new int[] { 0x3b, 0x00, 0x00, 0x00, 0xe8, 0xd4, 0xa5, 0x0f, 0xff } }, // 19
            new Object[] { Long.MAX_VALUE, new int[] { 0x1b, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff } }, // 20
            new Object[] { Long.MIN_VALUE, new int[] { 0x3b, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff } } // 21
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeInt(m_input);

        assertStreamContentsIsExpected();
    }
}
