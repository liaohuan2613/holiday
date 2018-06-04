package com.lhk.line;

public class QuickSort {
	public static int[] sort(int[] num, int n) {
		return done(num, 0, n - 1);
	}

	public static int[] done(int[] num, int low, int height) {
		int i, j, t, m;
		// 首先确定开始的位置和结束的位置是不是同一个位置
		if (low < height) {
			i = low;
			j = height;
			t = num[low];
			// 通过while进行循环，然后通过i和j分别表示开始和结束的两个点
			while (i < j) {
				// 对当前所在的结尾的数据和所选中的数进行比较，如果大于则继续往前面移动
				// 所以这里是选出一个在当前范围内最小的一个数，放到i的位置去
				while (i < j && num[j] > t)
					j--;
				// 对结束后的数据进行互换，当然还是要确定不是头结点的，否则会出现混乱
				if (i < j) {
					m = num[i];
					num[i] = num[j];
					num[j] = m;
					i++;// i++目的是为了避免和原本的交换点出现差异
				}
				// 再从头开始进行循环，如果满足条件则往后走
				// 这里则是选择一个最大的数放到后面去
				while (i < j && num[i] <= t)
					i++;
				// 将满足条件的数据进行交换，直到所有的数据都交换完毕
				if (i < j) {
					m = num[j];
					num[j] = num[i];
					num[i] = m;
					j--;
				}
				// 由此又确定了一个尾数了，也就是说在相对的区间里，我们已经把第一个较小的数和第一个较大的数确定了
				// 但是不一定是最小的数和最大的数，因此需要继续
			}
			// 这个数是完全确定了位置的，因为它从前面以及从后面都是确定在这个点的
			num[i] = t;
			num = done(num, 0, i - 1);
			num = done(num, i + 1, height);
		}
		return num;
	}
}
