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
public class CborOutputStreamHalfPrecisionTest extends CborOutputStreamTestBase<Float> {

    /**
     * Creates a new {@link CborOutputStreamHalfPrecisionTest} instance.
     */
    public CborOutputStreamHalfPrecisionTest(float input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding half-precision float {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( // half-precision (is upcasted to double)...
            new Object[] { 0.0f, new int[] { 0xf9, 0x00, 0x00 } }, // 0
            new Object[] { -0.0f, new int[] { 0xf9, 0x80, 0x00 } }, // 1
            new Object[] { 1.5f, new int[] { 0xf9, 0x3e, 0x00 } }, // 2
            new Object[] { 65504.0f, new int[] { 0xf9, 0x7b, 0xff } }, // 3
            new Object[] { 5.960464477539063e-8f, new int[] { 0xf9, 0x00, 0x01 } }, // 4
            new Object[] { 0.00006103515625f, new int[] { 0xf9, 0x04, 0x00 } }, // 5
            new Object[] { -4.0f, new int[] { 0xf9, 0xc4, 0x00 } }, // 6
            new Object[] { Float.POSITIVE_INFINITY, new int[] { 0xf9, 0x7c, 0x00 } }, // 7
            new Object[] { Float.NaN, new int[] { 0xf9, 0x7e, 0x00 } }, // 8
            new Object[] { Float.NEGATIVE_INFINITY, new int[] { 0xf9, 0xfc, 0x00 } } // 9
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeHalfPrecisionFloat(m_input);

        assertStreamContentsIsExpected();
    }
}
