package com.lhk.line;

public class Start {
    public static void main(String[] args) {
        int[] num = {7, 6, 12, 31, 5, 9, 21, 10};
        HeapAdjustSort.sort(num, num.length);
        for (int i = 0; i < num.length; i++) {
            System.out.print(num[i] + "\t");
        }
    }
}
