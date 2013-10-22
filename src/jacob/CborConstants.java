/*
 * JACOB - CBOR implementation in Java.
 * 
 * (C) Copyright - 2013 - J.W. Janssen <j.w.janssen@lxtreme.nl>
 *
 * Licensed under Apache License v2.0.
 */
package jacob;

/**
 * Constant values used by the CBOR format.
 */
public interface CborConstants {
    /** Denotes a one-byte value (uint8). */
    int ONE_BYTE = 0x18;
    /** Denotes a two-byte value (uint16). */
    int TWO_BYTES = 0x19;
    /** Denotes a four-byte value (uint32). */
    int FOUR_BYTES = 0x1a;
    /** Denotes a eight-byte value (uint64). */
    int EIGHT_BYTES = 0x1b;

    /** Denotes a boolean <code>false</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int FALSE = 0x14;
    /** Denotes a boolean <code>true</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int TRUE = 0x15;
    /** Denotes a <code>null</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int NULL = 0x16;
    /** Denotes an undefined value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int UNDEFINED = 0x17;
    /** Denotes a half-precision float (two-byte IEEE 754, see {@link #MT_FLOAT}). */
    int HALF_PRECISION_FLOAT = 0x19;
    /** Denotes a single-precision float (four-byte IEEE 754, see {@link #MT_FLOAT}). */
    int SINGLE_PRECISION_FLOAT = 0x1a;
    /** Denotes a double-precision float (eight-byte IEEE 754, see {@link #MT_FLOAT}). */
    int DOUBLE_PRECISION_FLOAT = 0x1b;
    /** Denotes a "break" stop code for unlimited arrays/maps. */
    int BREAK = 0x1f;
}
