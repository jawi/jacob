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
public class CborEncoderTagTest extends CborEncoderTestBase<Long> {
    private final Object m_data;

    /**
     * Creates a new {@link CborEncoderTagTest} instance.
     */
    public CborEncoderTagTest(long type, Object data, int[] encodedOutput) {
        super(type, encodedOutput);

        m_data = data;
    }

    @Parameters(name = "{index}: encoding tag {0},{1}")
    public static Iterable<Object[]> getParameters() {
        // @formatter:off
        return Arrays.asList( //
            new Object[] { 0, "2013-03-21T20:04:00Z", new int[] { 0xc0, 0x74, 0x32, 0x30, 0x31, 0x33, 0x2d, 0x30, 0x33, 0x2d, 0x32, 0x31, 0x54, 0x32, 0x30, 0x3a, 0x30, 0x34, 0x3a, 0x30, 0x30, 0x5a } }, // 0
            new Object[] { 1, 1363896240, new int[] { 0xc1, 0x1a, 0x51, 0x4b, 0x67, 0xb0 } }, // 1
            new Object[] { 1, 1363896240.5, new int[] { 0xc1, 0xfb, 0x41, 0xd4, 0x52, 0xd9, 0xec, 0x20, 0x00, 0x00 } }, // 2
            new Object[] { 23, "\1\2\3\4", new int[] { 0xd7, 0x44, 0x01, 0x02, 0x03, 0x04 } }, // 3
            new Object[] { 24, "dIETF", new int[] { 0xd8, 0x18, 0x45, 0x64, 0x49, 0x45, 0x54, 0x46 } }, // 4
            new Object[] { 32, "http://www.example.com", new int[] { 0xd8, 0x20, 0x76, 0x68, 0x74, 0x74, 0x70, 0x3a, 0x2f, 0x2f, 0x77, 0x77, 0x77, 0x2e, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, 0x2e, 0x63, 0x6f, 0x6d } } // 5
            );
        // @formatter:on
    }

    @Test
    public void testEncodeInput() throws IOException {
        encodeInputData();

        assertStreamContentsIsExpected();
    }

    private void encodeInputData() throws IOException {
        int tag = m_input.intValue();

        m_stream.writeTag(tag);
        switch (tag) {
            case 0:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                m_stream.writeTextString(String.valueOf(m_data));
                break;
            case 2:
            case 3:
            case 23:
            case 24:
                m_stream.writeByteString(String.valueOf(m_data));
                break;
            default:
                writeGenericItem(m_data);
                break;
        }
    }
}
