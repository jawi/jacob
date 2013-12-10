/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for decoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborDecoderMapTest extends CborDecoderTestBase<Map<?, ?>> {

    public CborDecoderMapTest(Map<?, ?> output, int[] encodedInput) {
        super(encodedInput, output);
    }

    @Parameters(name = "{index}: decoding map \"{0}\"")
    public static Iterable<Object[]> getParameters() {
        Map<Number, Number> map1 = new LinkedHashMap<>();
        map1.put(1L, 2L);
        map1.put(3L, 4L);

        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("a", 1L);
        map2.put("b", new Object[] { 2L, 3L });

        Map<String, Object> map3 = new LinkedHashMap<>();
        map3.put("a", "A");
        map3.put("b", "B");
        map3.put("c", "C");
        map3.put("d", "D");
        map3.put("e", "E");

        Map<Number, Number> map4 = new LinkedHashMap<>();
        map4.put(1L, 1.0f);
        map4.put(2L, 1.1);

        // @formatter:off
        return Arrays.asList( //
            new Object[] { Collections.emptyMap(), new int[] { 0xa0 } }, // 0
            new Object[] { map1, new int[] { 0xa2, 0x01, 0x02, 0x03, 0x04 } }, // 1
            new Object[] { map2, new int[] { 0xa2, 0x61, 0x61, 0x01, 0x61, 0x62, 0x82, 0x02, 0x03 } }, // 2
            new Object[] { map3, new int[] { 0xa5, 0x61, 0x61, 0x61, 0x41, 0x61, 0x62, 0x61, 0x42, 0x61, 0x63, 0x61, 0x43, 0x61, 0x64, 0x61, 0x44, 0x61, 0x65, 0x61, 0x45 } }, // 3
            new Object[] { map2, new int[] { 0xbf, 0x61, 0x61, 0x01, 0x61, 0x62, 0x82, 0x02, 0x03, 0xff } }, // 4
            new Object[] { map1, new int[] { 0xbf, 0x01, 0x02, 0x03, 0x04, 0xff } }, // 5
            new Object[] { map4, new int[] { 0xbf, 0x01, 0xfa, 0x3f, 0x80, 0x00, 0x00, 0x02, 0xfb, 0x3f, 0xf1, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a, 0xff } } // 6
            );
        // @formatter:on
    }

    @Test
    public void test() throws IOException {
        // In case of an exception, a @Rule will be applied...
        assertMapEquals(m_expectedOutput, (Map<?, ?>) readGenericItem());
    }

    private void assertMapEquals(Map<?, ?> expected, Map<?, ?> actual) {
        assertEquals(expected.size(), actual.size());

        for (Map.Entry<?, ?> entry : expected.entrySet()) {
            Object expectedValue = entry.getValue();
            Object actualValue = actual.get(entry.getKey());

            assertElementEquals(expectedValue, actualValue);
        }
    }
}
