/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2014 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

import static jacob.CborConstants.BREAK;
import static jacob.CborConstants.TYPE_FLOAT_SIMPLE;
import static jacob.CborType.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test cases for {@link CborType}.
 */
public class CborTypeTest {

    @Test
    public void testValueOf() {
        CborType type = valueOf(0xff);
        assertEquals(TYPE_FLOAT_SIMPLE, type.getMajorType());
        assertEquals(BREAK, type.getAdditionalInfo());
    }

    @Test
    public void testIsEqualTypeInt() {
        CborType type = valueOf(0xff);

        assertTrue(type.isEqualType(0xe0));
        assertTrue(type.isEqualType(0xff));
        assertFalse(type.isEqualType(0x01));
    }
}
