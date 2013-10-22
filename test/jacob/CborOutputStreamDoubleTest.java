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
public class CborOutputStreamDoubleTest extends CborOutputStreamTestBase<Double> {

    /**
     * Creates a new {@link CborOutputStreamDoubleTest} instance.
     */
    public CborOutputStreamDoubleTest(double input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding double-precision float {0}")
    public static Iterable<Object[]> getParameters() {
        // @formatter:off
        return Arrays.asList( //
            new Object[] { 0.0, new int[] { 0xfb, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 0
            new Object[] { -0.0, new int[] { 0xfb, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 1
            new Object[] { 1.1, new int[] { 0xfb, 0x3f, 0xf1, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a } }, // 2
            new Object[] { 1.0e+300, new int[] { 0xfb, 0x7e, 0x37, 0xe4, 0x3c, 0x88, 0x00, 0x75, 0x9c } }, // 3
            new Object[] { -4.1, new int[] { 0xfb, 0xc0, 0x10, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66 } }, // 4
            new Object[] { Double.POSITIVE_INFINITY, new int[] { 0xfb, 0x7f, 0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 5
            new Object[] { Double.NaN, new int[] { 0xfb, 0x7f, 0xf8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } }, // 6
            new Object[] { Double.NEGATIVE_INFINITY, new int[] { 0xfb, 0xff, 0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } } // 7
            );
        // @formatter:on
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeDouble(m_input);

        assertStreamContentsIsExpected();
    }
}
