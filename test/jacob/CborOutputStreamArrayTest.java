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
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for encoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborOutputStreamArrayTest extends CborOutputStreamTestBase<Object> {
    /**
     * Creates a new {@link CborOutputStreamArrayTest} instance.
     */
    public CborOutputStreamArrayTest(Object input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding array \"{0}\"")
    public static Iterable<Object[]> getParameters() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

        // @formatter:off
        return Arrays.asList( //
            new Object[] { null, new int[] { 0x80 } }, // 0
            new Object[] { new Object[0], new int[] { 0x80 } }, // 1
            new Object[] { new Object[] { 1, 2, 3 }, new int[] { 0x83, 0x01, 0x02, 0x03 } }, // 2
            new Object[] { new Object[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 }, new int[] { 0x98, 0x19, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x18, 0x18, 0x19 } }, // 3
            new Object[] { new Object[] { 1, new int[] { 2, 3 }, new int[] { 4, 5 } } , new int[] { 0x83, 0x01, 0x82, 0x02, 0x03, 0x82, 0x04, 0x05 } }, // 4
            new Object[] { new Object[] { "a", Collections.singletonMap("b", "c") }, new int[] { 0x82, 0x61, 0x61, 0xa1, 0x61, 0x62, 0x61, 0x63 } }, // 5
            new Object[] { new Object[] { "\1", 1, "\2", null, false }, new int[] { 0x85, 0x61, 0x01, 0x01, 0x61, 0x02, 0xf6, 0xf4 } }, // 6
            new Object[] { list, new int[] { 0x9f, 0x01, 0x02, 0x03, 0x04, 0x05, 0xFF } } // 7
            );
        // @formatter:on
    }

    @Test
    public void testEncodeInput() throws IOException {
        encodeInputData();

        assertStreamContentsIsExpected();
    }

    private void encodeInputData() throws IOException {
        if (m_input == null) {
            m_stream.writeArrayStart(0);
        } else if (m_input.getClass().isArray()) {
            int length = ((Object[]) m_input).length;
            m_stream.writeArrayStart(length);
            for (Object i : (Object[]) m_input) {
                writeGenericItem(i);
            }
        } else {
            m_stream.writeArrayStart();
            for (Object i : (Iterable<?>) m_input) {
                writeGenericItem(i);
            }
            m_stream.writeBreak();
        }
    }
}
