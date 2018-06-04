package com.lhk.line;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 桶排序（基数排序）
 * maxRadix，表示需要一个数的位数
 */
public class RadixSort {
	public static int[] sort(int[] num, int n, int maxRadix) {
		// k用于记录循环的次数，和maxRadix相关联
		// m表示每次循环的被除数，用于确定比较的是个位，十位还是百位等，当然也可以是小数的
		// lsp表示当前的key值，因为比较的不同，所以会产生相应的key，这个值如果使用得当也可以缩小比较范围
		int k, m, lsp;
		// reset是对num数组的数据重组，在这里或者这样的使用方式并不算好，只是我个人整出的一个东西
		int reset = 0;
		k = 0;
		m = 1;
		while (k < maxRadix) {
			reset = 0;
			// 缓存空间，用于存储每次比较的值的结果
			Map<Integer, List<Integer>> map = new LinkedHashMap<>();
			for (int i = 0; i < n; i++) {
				// 记录每个位数的值，数字的话则是0到9等
				List<Integer> list;
				if (num[i] < m) {
					lsp = 0;
				} else {
					lsp = (num[i] / m) % 10;
				}
				if (map.get(lsp) == null) {
					list = new ArrayList<>();
				} else {
					list = map.get(lsp);
				}
				list.add(num[i]);
				map.put(lsp, list);
			}
			// 将比较之后的结果重新放入到num里面去，当然这个只是我个人的做法
			// 其实也可以直接拿它去进行第二次循环，这个地方还是可以进行优化的
			for (int i = 0; i < 10; i++) {
				if (map.get(i) != null) {
					List<Integer> list = map.get(i);
					for (int j = 0; j < list.size(); j++) {
						num[reset] = list.get(j);
						reset++;
					}
				}
			}
			// 增进位数，从比较个位变成比较十位，然后再到百位
			m = m * 10;
			// 记录循环次数加1
			k++;
		}
		return num;
	}
}
