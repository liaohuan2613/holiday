package com.lhk.line;

public class SimpleSelectSort {
	public static int[] sort(int[] num,int n) {
		for (int i = 0; i < n; i++) {
			int k = i;
			for (int j = i + 1; j < n; j++) {
				if (num[j] < num[k]) {
					k = j;
				}
			}
			if (k != i) {
				int temp = num[i];
				num[i] = num[k];
				num[k] = temp;
			}
		}
		return num;
	}
}
