package com.lhk.line;

public class PosPopSort {
	public static int[] sort(int[] num, int n) {
		// 刚刚开始，所以交换的位置设置为最后的一位
		int flag = n - 1;
		while (flag > 0) {
			// 每一轮循环肯定是标志位置为0，直到没有交换了就结束了
			// 这个方法相对于flagPopSort的优势是它能够动态的把循环长度改变
			// 标记这一点其实是一样的
			int pos = 0;
			for (int i = 0; i < flag; i++) {
				if (num[i] > num[i + 1]) {
					int temp = num[i];
					num[i] = num[i + 1];
					num[i + 1] = temp;
					pos = i;
				}
			}
			flag = pos;
		}
		return num;
	}
}
