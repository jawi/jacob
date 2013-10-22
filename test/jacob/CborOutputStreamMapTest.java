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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases for encoding data in the CBOR format.
 */
@RunWith(Parameterized.class)
public class CborOutputStreamMapTest extends CborOutputStreamTestBase<Object> {
    /**
     * Creates a new {@link CborOutputStreamMapTest} instance.
     */
    public CborOutputStreamMapTest(Object input, int[] encodedOutput) {
        super(input, encodedOutput);
    }

    @Parameters(name = "{index}: encoding map \"{0}\"")
    public static Iterable<Object[]> getParameters() {
        Map<Integer, Integer> map1 = new LinkedHashMap<>();
        map1.put(1, 2);
        map1.put(3, 4);

        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("a", 1);
        map2.put("b", new int[] { 2, 3 });

        Map<String, Object> map3 = new LinkedHashMap<>();
        map3.put("a", "A");
        map3.put("b", "B");
        map3.put("c", "C");
        map3.put("d", "D");
        map3.put("e", "E");

        Iterable<Map.Entry<String, Object>> map4 = map2.entrySet();

        List<IntMapEntry> map5 = Arrays.asList(new IntMapEntry(1, 2), new IntMapEntry(3, 4));

        Iterable<Map.Entry<Integer, Number>> map6 = new HashMap<Integer, Number>() {
            {
                put(1, 1.0f);
                put(2, 1.1);
            }
        }.entrySet();

        // @formatter:off
        return Arrays.asList( //
            new Object[] { null, new int[] { 0xa0 } }, // 0
            new Object[] { Collections.emptyMap(), new int[] { 0xa0 } }, // 1
            new Object[] { map1, new int[] { 0xa2, 0x01, 0x02, 0x03, 0x04 } }, // 2
            new Object[] { map2, new int[] { 0xa2, 0x61, 0x61, 0x01, 0x61, 0x62, 0x82, 0x02, 0x03 } }, // 3
            new Object[] { map3, new int[] { 0xa5, 0x61, 0x61, 0x61, 0x41, 0x61, 0x62, 0x61, 0x42, 0x61, 0x63, 0x61, 0x43, 0x61, 0x64, 0x61, 0x44, 0x61, 0x65, 0x61, 0x45 } }, // 4
            new Object[] { map4, new int[] { 0xbf, 0x61, 0x61, 0x01, 0x61, 0x62, 0x82, 0x02, 0x03, 0xff } }, // 5
            new Object[] { map5, new int[] { 0xbf, 0x01, 0x02, 0x03, 0x04, 0xff } }, // 6
            new Object[] { map6, new int[] { 0xbf, 0x01, 0xfa, 0x3f, 0x80, 0x00, 0x00, 0x02, 0xfb, 0x3f, 0xf1, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a, 0xff } } // 7
            );
        // @formatter:on
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEncodeInput() throws IOException {
        if (m_input == null || m_input instanceof Map<?, ?>) {
            m_stream.writeMap((Map<?, ?>) m_input);
        } else if (m_input instanceof Iterable<?>) {
            m_stream.writeMap((Iterable<Map.Entry<?, ?>>) m_input);
        } else {
            m_stream.writeMap((Iterator<Map.Entry<?, ?>>) m_input);
        }

        assertStreamContentsIsExpected();
    }

    static class IntMapEntry implements Map.Entry<Integer, Integer> {
        final Integer m_key;
        final Integer m_value;

        public IntMapEntry(int key, int value) {
            m_key = key;
            m_value = value;
        }

        @Override
        public Integer getKey() {
            return m_key;
        }

        @Override
        public Integer getValue() {
            return m_value;
        }

        @Override
        public Integer setValue(Integer value) {
            throw new UnsupportedOperationException();
        }
    }
}
