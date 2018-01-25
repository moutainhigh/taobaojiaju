package com.xinshan.utils;

/**
 * Created by mxt on 17-5-4.
 */
public class Counter {
    private int n = 0;

    public int getN() {
        return n++;
    }

    public void reset(){
        n = 0;
    }
}
