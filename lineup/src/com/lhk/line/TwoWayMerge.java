package com.lhk.line;

/**
 * 归并排序
 */
public class TwoWayMerge {

	public static int[] sort(int[] num, int n) {
		num = mergeSort(num, 0, n - 1);
		return num;
	}

	// 这个方法时用于实现归并的效果，将数组分成多份进行递归，也因此会消耗空间
	public static int[] mergeSort(int[] num, int left, int right) {
		int i;
		if (left < right) {
			i = (left + right) / 2;
			num = mergeSort(num, left, i);
			num = mergeSort(num, i + 1, right);
			num = merge(num, left, right);
		}
		return num;
	}

	// 这个是主要效果的实现方法
	public static int[] merge(int[] num, int left, int right) {
		// 开启两个起点，一个是最开始的点，也就是左边起点
		int begin1 = left;
		int mid = (left + right) / 2;
		// 另一个是中点后面的节点
		int begin2 = mid + 1;
		// k值用于记录b的下标的
		int k = 0;
		int newArrayLen = right - left + 1;
		int[] b = new int[newArrayLen];
		while (begin1 <= mid && begin2 <= right) {
			if (num[begin1] < num[begin2]) {
				// 从小到大开始记录
				// ++在后，先传值再++
				b[k++] = num[begin1++];
			} else {
				b[k++] = num[begin2++];
			}
		}
		// 当循环结束，说明两边的比较基本完成了，从而能够确定一边的最值（可能最大值也可能是最小值）
		while (begin1 <= mid) {
			b[k++] = num[begin1++];
		}
		while (begin2 <= right) {
			b[k++] = num[begin2++];
		}
		// 将b中的值全数赋值给num原数组
		copyArray(b, num, newArrayLen, left);
		return num;
	}

	public static int[] copyArray(int[] source, int[] num, int len, int first) {
		int j = first;
		for (int i = 0; i < len; i++) {
			num[j] = source[i];
			j++;
		}
		return num;
	}

}
