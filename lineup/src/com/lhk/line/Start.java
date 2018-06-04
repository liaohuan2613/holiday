package com.lhk.line;

public class Start {
	public static void main(String[] args) {
		int[] num = { 7, 6, 12, 31, 5, 9, 21, 10 };
		RadixSort.sort(num, num.length,2);
		for (int i = 0; i < num.length; i++) {
			System.out.print(num[i] + "\t");
		}
	}
}
