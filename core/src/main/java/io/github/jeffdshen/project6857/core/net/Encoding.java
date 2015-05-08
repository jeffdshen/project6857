package io.github.jeffdshen.project6857.core.net;

/**
 * Created by jdshen on 5/7/15.
 */
public class Encoding {
    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            int x = aByte & 0xFF;
            builder.append(HEX_ARRAY[x >>> 4]);
            builder.append(HEX_ARRAY[x & 0x0F]);
        }
        return builder.toString();
    }
}
