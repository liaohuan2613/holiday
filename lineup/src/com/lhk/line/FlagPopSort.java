package com.lhk.line;

public class FlagPopSort {
	public static int[] sort(int[] num, int n) {
		for (int i = 0; i < n - 1; i++) {
			// 在关键的地方加入一个标记，也就是当这个标记没有改变的时候
			// 说明已经不需要交换了，所以直接通过了
			boolean flag = false;
			for (int j = 0; j < n - 1 - i; j++) {
				if (num[j] > num[j + 1]) {
					int temp = num[j];
					num[j] = num[j + 1];
					num[j + 1] = temp;
					flag = true;
				}
			}
			if (!flag) {
				break;
			}
		}
		return num;
	}
}
