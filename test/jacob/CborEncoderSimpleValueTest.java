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
public class CborEncoderSimpleValueTest extends CborEncoderTestBase<Integer> {
    /**
     * Creates a new {@link CborEncoderSimpleValueTest} instance.
     */
    public CborEncoderSimpleValueTest(int input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding simple value {0}")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList( //
            new Object[] { 0, new int[] { 0xe0 } }, // 0
            new Object[] { 16, new int[] { 0xf0 } }, // 1
            new Object[] { 24, new int[] { 0xf8, 0x18 } }, // 2
            new Object[] { 255, new int[] { 0xf8, 0xff } }, // 3
            new Object[] { 20, new int[] { 0xf4 } }, // 4 == 'false'
            new Object[] { 21, new int[] { 0xf5 } }, // 5 == 'true'
            new Object[] { 22, new int[] { 0xf6 } }, // 6 == 'null'
            new Object[] { 23, new int[] { 0xf7 } } // 7 == 'undefined'
            );
    }

    @Test
    public void testEncodeInput() throws IOException {
        m_stream.writeSimpleValue(m_input.byteValue());

        assertStreamContentsIsExpected();
    }
}
