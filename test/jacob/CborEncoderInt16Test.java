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
public class CborEncoderInt16Test extends CborEncoderTestBase<Integer> {

    /**
     * Creates a new {@link CborEncoderInt16Test} instance.
     */
    public CborEncoderInt16Test(int input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding 16-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x19, 0x00, 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x19, 0x00, 0x01 } }, // 1
            new Object[] { 100, new int[] { 0x19, 0x00, 0x64 } }, // 2
            new Object[] { 500, new int[] { 0x19, 0x01, 0xf4 } }, // 3
            new Object[] { 1000, new int[] { 0x19, 0x03, 0xe8 } }, // 4
            new Object[] { 65535, new int[] { 0x19, 0xff, 0xff } }, // 5
            new Object[] { 65536, new int[] { 0x19, 0x00, 0x00 } }, // 6
            new Object[] { -1, new int[] { 0x39, 0x00, 0x00, } }, // 7
            new Object[] { -100, new int[] { 0x39, 0x00, 0x63 } }, // 8
            new Object[] { -500, new int[] { 0x39, 0x01, 0xf3 } }, // 9
            new Object[] { -1000, new int[] { 0x39, 0x03, 0xe7 } }, // 10
            new Object[] { -65535, new int[] { 0x39, 0xff, 0xfe } }, // 11
            new Object[] { -65536, new int[] { 0x39, 0xff, 0xff } }, // 12
            new Object[] { -65537, new int[] { 0x39, 0x00, 0x00 } }, // 13
            new Object[] { Integer.MAX_VALUE, new int[] { 0x19, 0xff, 0xff } }, // 14
            new Object[] { Integer.MIN_VALUE, new int[] { 0x39, 0xff, 0xff } } // 15
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeInt16(m_input);

        assertStreamContentsIsExpected();
    }
}
