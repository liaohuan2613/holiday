package com.lhk.line;

public class ShellSort {
	public static int[] sort(int[] num,int n) {
		int dk;
		int temp;
		// 先折半，把整个数组折半
		for (dk = n / 2; dk > 0; dk /= 2) {
			// 这里是从中间往前面走，所以恰好能够把所有的数都走过一次
			for (int i = dk; i < n; i++) {
				// 记录第一个的值，用于后面进行交换的
				temp = num[i];
				int j;
				// 每次循环都和相同间隔的进行比较
				for (j = i; j >= dk; j -= dk) {
					if (temp < num[j - dk]) {
						num[j] = num[j - dk];
					} else {
						break;
					}
				}
				// 将最后的结果交换就能得出答案了
				num[j] = temp;
			}
		}
		return num;
	}
}
