package com.pan.mylibrary;

import android.view.View;

import java.lang.reflect.Constructor;

/**
 * @author Pan
 * @DATE 2019/10/10.
 * @TIME 22:26.
 */
public class ZZZ {
    public static void main(String[] args) {
        Test lp = new Test(-1,-1);

        Class<? extends Test> aClass = lp.getClass();

        try {
            Constructor<? extends Test> constructor = aClass.getConstructor(Integer.class, Integer.class);
            Test layoutParams = constructor.newInstance(-1, -1);

            System.out.println("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        View[] test = new View[5];

        for (int i = test.length - 1; i >= 0; i--) {

        }
        for (int i = 1; i < 100; i+=2){

        }
    }

    static class Test{
        private int s1,s2;

        public Test(int s1, int s2) {
            this.s1 = s1;
            this.s2 = s2;
        }


    }
}
