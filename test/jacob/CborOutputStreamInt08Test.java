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
public class CborOutputStreamInt08Test extends CborOutputStreamTestBase<Integer> {

    /**
     * Creates a new {@link CborOutputStreamInt08Test} instance.
     */
    public CborOutputStreamInt08Test(int input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding 8-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x18, 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x18, 0x01 } }, // 1
            new Object[] { 10, new int[] { 0x18, 0x0a } }, // 2
            new Object[] { 23, new int[] { 0x18, 0x17 } }, // 3
            new Object[] { 24, new int[] { 0x18, 0x18 } }, // 4
            new Object[] { 25, new int[] { 0x18, 0x19 } }, // 5
            new Object[] { 100, new int[] { 0x18, 0x64 } }, // 6
            new Object[] { 255, new int[] { 0x18, 0xff } }, // 7
            new Object[] { 256, new int[] { 0x18, 0x00 } }, // 8
            new Object[] { 500, new int[] { 0x18, 0xf4 } }, // 9
            new Object[] { -1, new int[] { 0x38, 0x00 } }, // 10
            new Object[] { -10, new int[] { 0x38, 0x09 } }, // 11
            new Object[] { -24, new int[] { 0x38, 0x17 } }, // 12
            new Object[] { -25, new int[] { 0x38, 0x18 } }, // 13
            new Object[] { -100, new int[] { 0x38, 0x63 } }, // 14
            new Object[] { -255, new int[] { 0x38, 0xfe } }, // 15
            new Object[] { -256, new int[] { 0x38, 0xff } }, // 16
            new Object[] { -500, new int[] { 0x38, 0xf3 } }, // 17
            new Object[] { Integer.MAX_VALUE, new int[] { 0x18, 0xff } }, // 18
            new Object[] { Integer.MIN_VALUE, new int[] { 0x38, 0xff } } // 19
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeInt8(m_input);

        assertStreamContentsIsExpected();
    }
}
