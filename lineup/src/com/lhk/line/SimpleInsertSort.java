package com.lhk.line;

public class SimpleInsertSort {
	public static int[] sort(int[] num, int n) {
		for (int i = 1; i < n; i++) {
			if (num[i] < num[i - 1]) {
				int x = num[i];
				num[i] = num[i - 1];
				int j = i - 1;
				// 如果当前j的值大于x，就把后面的，其实也就是x的位置和当前j的位置进行交换
				// 并且向前走一格
				while (j >= 0 && num[j] > x) {
					num[j + 1] = num[j];
					j--;
				}
				// 这里我们可以假设while循环只是成功了一次，那么其位置必然往前面移动了一格
				// 也就是到了-1，而num应该是0的位置是给x的，所以这里是j+1;
				num[j + 1] = x;
			}
		}
		return num;
	}
}
