/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

/**
 * Represents the various major types in CBOR.
 * <p>
 * The major type is encoded in the upper three bits of each initial byte.
 * </p>
 */
public enum CborType {
    /** Major type 0: unsigned integers. */
    UNSIGNED_INTEGER,
    /** Major type 1: negative integers. */
    NEGATIVE_INTEGER,
    /** Major type 2: byte string. */
    BYTE_STRING,
    /** Major type 3: text/UTF8 string. */
    TEXT_STRING,
    /** Major type 4: array of items. */
    ARRAY,
    /** Major type 5: map of pairs. */
    MAP,
    /** Major type 6: semantic tags. */
    TAG,
    /** Major type 7: floating point, simple data types. */
    FLOAT_SIMPLE;

    /**
     * Decodes a given initial byte to a {@link CborType} value by looking at the three most-significant bits.
     * 
     * @param i the input byte (8-bit) to decode into a {@link CborType} instance.
     * @return a {@link CborType} instance, never <code>null</code>.
     */
    public static CborType decode(int i) {
        return values()[i >>> 5];
    }

    /**
     * Encodes this {@link CborType} into a byte-value with additional information as the lower 5 bits.
     * 
     * @param additionalInfo the lower 5 bits to encode.
     * @return the encoded byte value, 0..255.
     */
    public int encode(int additionalInfo) {
        return bitMask() | (additionalInfo & 0x1f);
    }

    /**
     * @return the bit mask value of this type.
     */
    public int bitMask() {
        return (ordinal() << 5);
    }

    /**
     * @param i the input byte (8-bit) whose value should be equal to the ordinal value of this major type.
     * @return <code>true</code> if the given input value matches this major type's ordinal value, <code>false</code> otherwise.
     */
    public boolean isSameOrdinal(int i) {
        return ordinal() == ((i >> 5) & 0x1f);
    }
}
