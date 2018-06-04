package com.lhk.line;

public class ChQuickSort {
	// 总体来说这个是一个综合排序
	// k区间值，这个值是可以改变的，只要大于0即可
	public static int[] sort(int[] num, int n,int k) {
		num = sortImprove(num, 0, n, k);

		// 利用简单插入排序对基本有序序列进行排序，时间复杂度基本上是n
		for (int i = 1; i < n; i++) {
			int temp = num[i];
			int j = i - 1;
			while (j >= 0 && temp < num[j]) {
				num[j + 1] = num[j];
				j = j - 1;
			}
			num[j + 1] = temp;
		}
		return num;
	}

	public static int[] sortImprove(int[] num, int low, int height, int k) {
		// 改进版本的第一个地方，这里并非严格要求进行排序的，所以不需要每一个数都走到
		// k值就是用于分开区间的
		if (height - low > k) {
			int pivot = partition(num, low, height);
			sortImprove(num, low, pivot - 1, k);
			sortImprove(num, pivot + 1, height, k);
		}
		return num;
	}

	public static int partition(int num[], int low, int height) {
		// 基本上就是快速排序的格式，如果把low和height变成i和j就能完成成为快速排序的形式了
		int privotKey = num[low];
		while (low < height) {
			while (low < height && num[height] > privotKey) {
				height--;
			}
			if (low < height) {
				int temp = num[height];
				num[height] = num[low];
				num[low] = temp;
				low++;
			}
			while (low < height && num[low] < privotKey) {
				low++;
			}
			if (low < height) {
				int temp = num[low];
				num[low] = num[height];
				num[height] = temp;
				height--;
			}
		}
		// 由于这个是改进版，快速排序的目的只是生成基本有序表
		// 所以其实不用这句话也是可以的，因为后面的简单插入排序是可以完善它的
		num[low] = privotKey;
		return low;
	}
}
