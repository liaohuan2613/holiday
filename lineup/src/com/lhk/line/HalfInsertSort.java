package com.lhk.line;

public class HalfInsertSort {
	public static int[] sort(int[] num,int n) {
		for (int i = 1; i < n; i++) {
			int low = 0;
			int height = i;
			if (num[i] < num[i - 1]) {
				int x = num[i];
				num[i] = num[i - 1];

				// 查找一个比x大的数，这里是用二分查找法查找的
				// 这里是通过中间插入的方式，从而得出能够移动多少格
				while (low <= height) {
					int m = (low + height) / 2;
					if (x < num[m]) {
						height = m - 1;
					} else {
						low = m + 1;
					}
				}

				// 把从当前下标到找到的比x大的数的下标进行移动然后插入即可
				for (int j = i - 1; j >= height + 1; j--) {
					num[j + 1] = num[j];
				}
				// 到达目的位置后，将值赋值与它
				num[height + 1] = x;
			}
		}
		return num;
	}
}
