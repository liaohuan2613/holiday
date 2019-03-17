/**
 * Second Max of Array
 * Find the second max number in a given array.
 * <p>
 * Example
 * Given [1, 3, 2, 4], return 3.
 * <p>
 * Given [1, 2], return 1.
 * <p>
 * Notice
 * You can assume the array contains at least two numbers.
 */

public class SecondMaxOfArray {
    public static int secondMax(int[] nums) {
        if (nums.length > 1) {
            int secondMaxNum = Integer.MIN_VALUE;
            int maxNum = Integer.MIN_VALUE;
            for (int num : nums) {
                if (num > secondMaxNum) {
                    if (num > maxNum) {
                        secondMaxNum = maxNum;
                        maxNum = num;
                    } else {
                        secondMaxNum = num;
                    }
                }
            }
            return secondMaxNum;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        return 0;
    }
}
