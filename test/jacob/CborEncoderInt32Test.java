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
public class CborEncoderInt32Test extends CborEncoderTestBase<Long> {

    /**
     * Creates a new {@link CborEncoderInt32Test} instance.
     */
    public CborEncoderInt32Test(long input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding 32-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x1a, 0x00, 0x00, 0x00, 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x1a, 0x00, 0x00, 0x00, 0x01 } }, // 1
            new Object[] { 1000, new int[] { 0x1a, 0x00, 0x00, 0x03, 0xe8 } }, // 2
            new Object[] { 1000000, new int[] { 0x1a, 0x00, 0x0f, 0x42, 0x40 } }, // 3
            new Object[] { 4294967295L, new int[] { 0x1a, 0xff, 0xff, 0xff, 0xff } }, // 4
            new Object[] { 4294967296L, new int[] { 0x1a, 0x00, 0x00, 0x00, 0x00 } }, // 5
            new Object[] { -1, new int[] { 0x3a, 0x00, 0x00, 0x00, 0x00 } }, // 6
            new Object[] { -1000, new int[] { 0x3a, 0x00, 0x00, 0x03, 0xe7 } }, // 7
            new Object[] { -1000000, new int[] { 0x3a, 0x00, 0x0f, 0x42, 0x3f } }, // 8
            new Object[] { -4294967295L, new int[] { 0x3a, 0xff, 0xff, 0xff, 0xfe } }, // 9
            new Object[] { -4294967296L, new int[] { 0x3a, 0xff, 0xff, 0xff, 0xff } }, // 10
            new Object[] { -4294967297L, new int[] { 0x3a, 0x00, 0x00, 0x00, 0x00 } }, // 11
            new Object[] { Long.MAX_VALUE, new int[] { 0x1a, 0xff, 0xff, 0xff, 0xff } }, // 12
            new Object[] { Long.MIN_VALUE, new int[] { 0x3a, 0xff, 0xff, 0xff, 0xff } } // 13
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeInt32(m_input);

        assertStreamContentsIsExpected();
    }
}
