package com.uetty.jreview.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 冒泡排序
 * @author Vince
 */
public class BubboSort {

	private static <T> void swap(List<T> list, int i, int j) {
		T cache = list.get(j);
		list.set(j, list.get(i));
		list.set(i, cache);
	}
	
	public static <T> void sort(List<T> list, Comparator<T> ctor) {
		for (int i = list.size() - 1; i > 0; i--) {
			boolean changed = false;
			for (int j = 0; j < i; j++) {
				if (ctor.compare(list.get(j), list.get(j + 1)) > 0) {
					swap(list, j, j + 1);
					changed = true;
				}
			}
			if (!changed) break;
		}
	}
	
	public static void main(String[] args) {
		Comparator<Integer> ctor = Integer::compare;
		
		// 少量数据，多次排序
		Integer[] arr = new Integer[] {
				28, 44, 1, 34, 32, 55,
				33, 44, 83, 35, 36,
				100, 23, 155, 11, 33,
				110, 9, 22, 51, 44,
				53, 13, 1200, 200, 10110,
				29, 100, 33, 55, 82,
				34, 24, 500, 84, 11, 633,
				722, 211, 62, 5, 99,
				22, 59, 73, 22, 103,
				20, 55, 11, 66, 805
			};
		int count = 1_00;
		
		long start = System.nanoTime();
		List<Integer> list = null;
		for (int i = 0; i < count; i++) {
			list = Arrays.asList(arr);
			sort(list, ctor);
		}
		long pass = System.nanoTime() - start;
		
		list.forEach(item -> {
			System.out.print(item + " ");
		});
		
		System.out.println();
		System.out.println(pass);
		
		// 大量数据，单次排序
		list = new ArrayList<>();
		for (int i = 0; i < 20_000; i++) {
			list.add((int)(Math.random() * 1000000));
		}
		start = System.currentTimeMillis();
		sort(list, ctor);
		pass = System.currentTimeMillis() - start;
		System.out.println();
		System.out.println(pass);
	}
}
