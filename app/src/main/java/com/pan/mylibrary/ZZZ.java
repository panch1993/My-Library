package com.pan.mylibrary;

import java.nio.ByteBuffer;

/**
 * @author Pan
 * @DATE 2019/10/10.
 * @TIME 22:26.
 */
public class ZZZ {
    public static void main(String[] args) {
        long curr = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(32767);

        buf.putLong(0,curr);
        System.out.print(curr);
        String cu = curr + "";
        System.out.print("\n1 getLong = "+(buf.getLong(0)&0xffffffff));
        System.out.print("\n2 getLong = "+(buf.getLong(0)&0xffffffffL));

        for (int i = 0; i < 100; i++) {

        }
    }
}
