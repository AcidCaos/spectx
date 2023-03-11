package com.sxlic;

public class Base64 {
    public static String encode(byte[] byArray) {
        if (byArray == null) {
            return null;
        }
        return java.util.Base64.getEncoder().encodeToString(byArray);
    }

    public static String encode(byte[] byArray, int n, int n2) {
        if (byArray == null) {
            return null;
        }
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, n, byArray2, 0, n2);
        return java.util.Base64.getEncoder().encodeToString(byArray2);
    }

    public static byte[] decode(String string) {
        if (string == null) {
            return null;
        }
        return java.util.Base64.getDecoder().decode(string);
    }

    public static byte[] decode(byte[] byArray) {
        if (byArray == null) {
            return null;
        }
        return java.util.Base64.getDecoder().decode(byArray);
    }
}