/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for decoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborInputStreamArrayTest extends CborInputStreamTestBase<Object> {

    public CborInputStreamArrayTest(Object output, int[] encodedInput) {
        super(encodedInput, output);
    }

    @Parameters(name = "{index}: decoding array \"{0}\"")
    public static Iterable<Object[]> getParameters() {
        // @formatter:off
        return Arrays.asList( //
            new Object[] { new Object[0], new int[] { 0x80 } }, // 0
            new Object[] { new Object[] { 1L, 2L, 3L }, new int[] { 0x83, 0x01, 0x02, 0x03 } }, // 1
            new Object[] { new Object[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L }, new int[] { 0x98, 0x19, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x18, 0x18, 0x19 } }, // 2
            new Object[] { new Object[] { 1L, new Object[] { 2L, 3L }, new Object[] { 4L, 5L } } , new int[] { 0x83, 0x01, 0x82, 0x02, 0x03, 0x82, 0x04, 0x05 } }, // 3
            new Object[] { new Object[] { "a", Collections.singletonMap("b", "c") }, new int[] { 0x82, 0x61, 0x61, 0xa1, 0x61, 0x62, 0x61, 0x63 } }, // 4
            new Object[] { new Object[] { "\1", 1L, "\2", null, false }, new int[] { 0x85, 0x61, 0x01, 0x01, 0x61, 0x02, 0xf6, 0xf4 } } // 5
//            new Object[] { Arrays.asList(1, 2, 3, 4, 5), new int[] { 0x9f, 0x01, 0x02, 0x03, 0x04, 0x05, 0xFF } } // 6
            );
        // @formatter:on
    }

    @Test
    public void test() throws IOException {
        if (m_expectedOutput instanceof Object[]) {
            Object[] read = m_stream.readArray();
            Object[] expected = (Object[]) m_expectedOutput;

            assertEquals(expected.length, read.length);
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] instanceof Object[]) {
                    assertArrayEquals((Object[]) expected[i], (Object[]) read[i]);
                } else {
                    assertEquals("Item at index " + i + " mismatch", expected[i], read[i]);
                }
            }
        } else {
            fail("Unsupported output!");
        }
    }
}
