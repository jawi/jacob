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
public class CborOutputStreamFloatTest extends CborOutputStreamTestBase<Float> {

    /**
     * Creates a new {@link CborOutputStreamFloatTest} instance.
     */
    public CborOutputStreamFloatTest(float input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding single-precision float {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0.0f, new int[] { 0xfa, 0x00, 0x00, 0x00, 0x00 } }, // 0
            new Object[] { -0.0f, new int[] { 0xfa, 0x80, 0x00, 0x00, 0x00 } }, // 1
            new Object[] { 1.0f, new int[] { 0xfa, 0x3f, 0x80, 0x00, 0x00 } }, // 2
            new Object[] { 1.5f, new int[] { 0xfa, 0x3f, 0xc0, 0x00, 0x00 } }, // 3
            new Object[] { 65504.0f, new int[] { 0xfa, 0x47, 0x7f, 0xe0, 0x00 } }, // 4
            new Object[] { 100000.0f, new int[] { 0xfa, 0x47, 0xc3, 0x50, 0x00 } }, // 5
            new Object[] { 3.4028234663852886e+38f, new int[] { 0xfa, 0x7f, 0x7f, 0xff, 0xff } }, // 6
            new Object[] { Float.POSITIVE_INFINITY, new int[] { 0xfa, 0x7f, 0x80, 0x00, 0x00 } }, // 7
            new Object[] { Float.NaN, new int[] { 0xfa, 0x7f, 0xc0, 0x00, 0x00 } }, // 8
            new Object[] { Float.NEGATIVE_INFINITY, new int[] { 0xfa, 0xff, 0x80, 0x00, 0x00 } } // 9
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeFloat(m_input);

        assertStreamContentsIsExpected();
    }
}
