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
public class CborEncoderInt64Test extends CborEncoderTestBase<Long> {

    /**
     * Creates a new {@link CborEncoderInt64Test} instance.
     */
    public CborEncoderInt64Test(long input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding 64-bit integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 } }, // 1
            new Object[] { 1000000, new int[] { 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0f, 0x42, 0x40 } }, // 2
            new Object[] { 1000000000000L, new int[] { 0x1b, 0x00, 0x00, 0x00, 0xe8, 0xd4, 0xa5, 0x10, 0x00 } }, // 3
            new Object[] { -1, new int[] { 0x3b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 4
            new Object[] { -1000000, new int[] { 0x3b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0f, 0x42, 0x3f } }, // 5
            new Object[] { -1000000000000L, new int[] { 0x3b, 0x00, 0x00, 0x00, 0xe8, 0xd4, 0xa5, 0x0f, 0xff } }, // 6
            new Object[] { Long.MAX_VALUE, new int[] { 0x1b, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff } }, // 7
            new Object[] { Long.MIN_VALUE, new int[] { 0x3b, 0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff } } // 8
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeInt64(m_input);

        assertStreamContentsIsExpected();
    }
}
