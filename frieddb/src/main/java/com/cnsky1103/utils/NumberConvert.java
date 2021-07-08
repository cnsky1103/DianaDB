package com.cnsky1103.utils;

public class NumberConvert {
    public static int bytes2int(byte b1, byte b2, byte b3, byte b4) {
        return (((0xff & b1) << 24) | ((0xff & b2) << 16) | ((0xff & b3) << 8) | (0xff & b4));
    }

    public static int bytes2int(byte[] b) {
        return bytes2int(b[0], b[1], b[2], b[3]);
    }
}
