package com.lhk.line;

public class DoubleSelectSort {
	public static int[] sort(int[] num, int n) {
		int min, max, temp;
		for (int i = 0; i <= n / 2; i++) {
			min = i;
			max = n - 1 - i;
			boolean flag = true;// 用于防止两个元素选中了对方
			// 因为我们max是等于n-1-i，为了避免后面会出现重用的情况，所以我们优先从min的位置开始循环

			// 并且这里不能和简单的排序一样，从i+1开始，因为相对于max来说是看不到i的位置的

			// 而相对于min来说是看不到n-i的位置的，所以起点和终点需要比简单的多两格
			for (int j = i; j < n - i; j++) {
				// 如果j比i小，说明会出现头尾进行交换，所以就不能进入其中
				// 那么就需要直接i和j的值进行交换即可
				if (num[j] > num[max] && num[j] > num[i]) {
					flag = false;
					max = j;
					continue;
				}
				if (num[j] < num[min]) {
					min = j;
					if (flag) {
						max = min;
					}
				}
			}
			temp = num[i];
			num[i] = num[min];
			num[min] = temp;
			temp = num[n - 1 - i];
			num[n - 1 - i] = num[max];
			num[max] = temp;
		}
		return num;
	}
}
