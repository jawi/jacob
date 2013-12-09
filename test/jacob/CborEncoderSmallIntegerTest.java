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
public class CborEncoderSmallIntegerTest extends CborEncoderTestBase<Integer> {

    /**
     * Creates a new {@link CborEncoderSmallIntegerTest} instance.
     */
    public CborEncoderSmallIntegerTest(int input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding small integer {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0x00 } }, // 0
            new Object[] { 1, new int[] { 0x01 } }, // 1
            new Object[] { 10, new int[] { 0x0a } }, // 2
            new Object[] { 23, new int[] { 0x17 } }, // 3
            new Object[] { 24, new int[] { 0x17 } }, // 4
            new Object[] { 25, new int[] { 0x17 } }, // 5
            new Object[] { -1, new int[] { 0x20 } }, // 6
            new Object[] { -10, new int[] { 0x29 } }, // 7
            new Object[] { -24, new int[] { 0x37 } }, // 8
            new Object[] { -25, new int[] { 0x37 } }, // 9
            new Object[] { -100, new int[] { 0x37 } }, // 10
            new Object[] { Integer.MAX_VALUE, new int[] { 0x17 } }, // 11
            new Object[] { Integer.MIN_VALUE, new int[] { 0x37 } } // 12
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeSmallInt(m_input);

        assertStreamContentsIsExpected();
    }
}
