/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for encoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborEncoderStringTest extends CborEncoderTestBase<String> {
    private static final boolean PLATFORM_ENCODING = false;
    private static final boolean UTF8_ENCODING = true;

    private final boolean m_utf8;

    /**
     * Creates a new {@link CborEncoderStringTest} instance.
     */
    public CborEncoderStringTest(String input, int[] encodedOutput, boolean utf8)
        throws UnsupportedEncodingException {
        super(input, encodedOutput);
        m_utf8 = utf8;
    }

    @Parameters(name = "{index}: encoding string \"{0}\"")
    public static Iterable<Object[]> getParameters() {
        // @formatter:off
        return Arrays.asList( //
            new Object[] { null, new int[] { 0x40 }, PLATFORM_ENCODING }, // 0
            new Object[] { "", new int[] { 0x40 }, PLATFORM_ENCODING }, // 1
            new Object[] { "\1\2\3\4", new int[] { 0x44, 0x01, 0x02, 0x03, 0x04 }, PLATFORM_ENCODING }, // 2
            new Object[] { "\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2", new int[] { 0x57, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02 }, PLATFORM_ENCODING }, // 3
            new Object[] { "\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3", new int[] { 0x58, 0x18, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03 }, PLATFORM_ENCODING }, // 4
            new Object[] { null, new int[] { 0x60 }, UTF8_ENCODING }, // 5
            new Object[] { "", new int[] { 0x60 }, UTF8_ENCODING }, // 6
            new Object[] { "a", new int[] { 0x61, 0x61 }, UTF8_ENCODING }, // 7
            new Object[] { "IETF", new int[] { 0x64, 0x49, 0x45, 0x54, 0x46 }, UTF8_ENCODING }, // 8
            new Object[] { "\"\\", new int[] { 0x62, 0x22, 0x5c }, UTF8_ENCODING }, // 9
            new Object[] { "\u00fc", new int[] { 0x62, 0xc3, 0xbc }, UTF8_ENCODING }, // 10
            new Object[] { "\u6c34", new int[] { 0x63, 0xe6, 0xb0, 0xb4 }, UTF8_ENCODING }, // 11
            new Object[] { "\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2", new int[] { 0x77, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02 }, UTF8_ENCODING }, // 12
            new Object[] { "\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3\4\5\6\7\1\2\3", new int[] { 0x78, 0x18, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x01, 0x02, 0x03 }, UTF8_ENCODING } // 13
            );
        // @formatter:on
    }

    @Test
    public void testEncodeInput() throws IOException {
        if (m_utf8) {
            m_stream.writeTextString(m_input);
        } else {
            m_stream.writeByteString(m_input == null ? null : m_input.getBytes());
        }

        assertStreamContentsIsExpected();
    }
}
