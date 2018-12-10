package com.lhk.line;

public class HeapAdjustSort {
	public static int[] sort(int[] num, int n) {
		num = createHeap(num, n);
		for (int i = n - 1; i > 0; i--) {
			// 将上一个堆中的最小值也就是顶点的值和最后的一个值进行交换
			// 并且起到打乱堆的效果，并重新找到新堆的最小值，当然是除了最后一个节点的
			int x = num[0];
			num[0] = num[i];
			num[i] = x;
			num = heapAdjust(num, 0, i);
		}
		return num;
	}

	// 构建堆排列方式是父节点一定比子节点小，因此顶点一定是最小的值
	private static int[] createHeap(int[] num, int n) {
		// 除以2是为了恰好在最后的一度子节点的父节点处
		for (int i = (n - 1) / 2; i >= 0; i--) {
			num = heapAdjust(num, i, n);
		}
		return num;
	}

	private static int[] heapAdjust(int[] num, int s, int n) {
		// 记录父节点的大小
		int rc = num[s];
		// 还原子节点的数据并且和所有子节点中较小的进行比较
		for (int i = 2 * s + 1; i < n; i = i * 2 + 1) {
			// 判断两个子节点中谁更小
			if (i + 1 <= n - 1 && num[i] > num[i + 1]) {
				i++;
			}
			// 子节点和父节点进行比较，如果父节点比子节点小，则说明堆合理，不需要交换
			// 所以跳出循环
			if (rc <= num[i]) {
				break;
			}
			// 如果到了这里说明父节点比子节点大，则和子节点中最小的进行交换
			num[s] = num[i];
			s = i;
		}
		// 到了最后当然是把最下面的数取开始的节点的值
		num[s] = rc;
		return num;
	}
}
