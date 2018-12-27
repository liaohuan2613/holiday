package com.lhk.line;

public class SimpleSelectSort {
    public static int[] sort(int[] num, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (num[j] < num[i]) {
                    int temp = num[i];
                    num[i] = num[j];
                    num[j] = temp;
                }
            }

        }
        return num;
    }
}
