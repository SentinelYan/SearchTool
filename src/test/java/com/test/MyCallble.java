package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MyCallble implements Callable<List<Integer>> {
    int startPos;
    int endPos;

    MyCallble(int s ,int e){
        this.startPos = s;
        this.endPos = e;
    }

    static boolean isPrime(int num){
        if (num == 1) return false;
        for (int i = 2; i <= (num/2) ; i++) {
            if (num % i ==0 ) return false;
        }
        return true;
    }
    public List call() throws Exception{
        List<Integer> list = new ArrayList<>();
        for (int i = startPos; i < endPos ; i++) {
            if (isPrime(i)){
                list.add(i);
            }
        }
        return list;
    }

}
