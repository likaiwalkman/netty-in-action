package com.victor.basics;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class StringBinaryConvertTest {

    public boolean validate(int num) {
        byte[] bytes = new byte[num];
        for (int i = 0; i < num; i++) {
            bytes[i] = (byte) i;
        }
        System.out.println(Arrays.toString(bytes));
        String  s      = new String(bytes);
        byte[]  sBytes = s.getBytes();
        boolean b      = sBytes.length == num;
        for (int i = 0; i < sBytes.length; i++) {
            boolean r = sBytes[i] == i;
            b &= r;
        }
        return b;

    }

    @Test
    public void test() {
        for (int i = 1; i <= 128; i++) {
            boolean fact = validate(i);
            Assert.assertTrue(fact);
        }
    }

}
