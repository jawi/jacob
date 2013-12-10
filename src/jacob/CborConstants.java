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

    /** The CBOR-encoded boolean <code>false</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int FALSE = 0x14;
    /** The CBOR-encoded boolean <code>true</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int TRUE = 0x15;
    /** The CBOR-encoded <code>null</code> value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int NULL = 0x16;
    /** The CBOR-encoded "undefined" value (encoded as "simple value": {@link #MT_SIMPLE}). */
    int UNDEFINED = 0x17;
    /** Denotes a half-precision float (two-byte IEEE 754, see {@link #MT_FLOAT}). */
    int HALF_PRECISION_FLOAT = 0x19;
    /** Denotes a single-precision float (four-byte IEEE 754, see {@link #MT_FLOAT}). */
    int SINGLE_PRECISION_FLOAT = 0x1a;
    /** Denotes a double-precision float (eight-byte IEEE 754, see {@link #MT_FLOAT}). */
    int DOUBLE_PRECISION_FLOAT = 0x1b;
    /** The CBOR-encoded "break" stop code for unlimited arrays/maps. */
    int BREAK = 0x1f;

    /** Semantic tag value describing date/time values in the standard format (UTF8 string, RFC3339). */
    int TAG_STANDARD_DATE_TIME = 0;
    /** Semantic tag value describing date/time values as Epoch timestamp (numeric, RFC3339). */
    int TAG_EPOCH_DATE_TIME = 1;
    /** Semantic tag value describing a positive big integer value (byte string). */
    int TAG_POSITIVE_BIGINT = 2;
    /** Semantic tag value describing a negative big integer value (byte string). */
    int TAG_NEGATIVE_BIGINT = 3;
    /** Semantic tag value describing a decimal fraction value (two-element array, base 10). */
    int TAG_DECIMAL_FRACTION = 4;
    /** Semantic tag value describing a big decimal value (two-element array, base 2). */
    int TAG_BIGDECIMAL = 5;
    /** Semantic tag value describing an expected conversion to base64url encoding. */
    int TAG_EXPECTED_BASE64_URL_ENCODED = 21;
    /** Semantic tag value describing an expected conversion to base64 encoding. */
    int TAG_EXPECTED_BASE64_ENCODED = 22;
    /** Semantic tag value describing an expected conversion to base16 encoding. */
    int TAG_EXPECTED_BASE16_ENCODED = 23;
    /** Semantic tag value describing an encoded CBOR data item (byte string). */
    int TAG_CBOR_ENCODED = 24;
    /** Semantic tag value describing an URL (UTF8 string). */
    int TAG_URI = 32;
    /** Semantic tag value describing a base64url encoded string (UTF8 string). */
    int TAG_BASE64_URL_ENCODED = 33;
    /** Semantic tag value describing a base64 encoded string (UTF8 string). */
    int TAG_BASE64_ENCODED = 34;
    /** Semantic tag value describing a regular expression string (UTF8 string, PCRE). */
    int TAG_REGEXP = 35;
    /** Semantic tag value describing a MIME message (UTF8 string, RFC2045). */
    int TAG_MIME_MESSAGE = 36;
    /** Semantic tag value describing CBOR content. */
    int TAG_CBOR_MARKER = 55799;
}
