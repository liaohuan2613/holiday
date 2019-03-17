/**
 * Q: Window Sum
 * Given an array of n integers, and a moving window(size k), move the window at each iteration from the start of the array, find the sum of the element inside the window at each moving.
 * <p>
 * Example
 * For array [1,2,7,8,5], moving window size k = 3.
 * 1 + 2 + 7 = 10
 * 2 + 7 + 8 = 17
 * 7 + 8 + 5 = 20
 * return [10,17,20]
 */

public class WindowSum {
    public static int[] winSum(int[] nums, int k) {
        if (k == 0) {
            k = 1;
        }
        int[] returnNums = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length - k + 1; i++) {
            int sum = 0;
            for (int j = 0; j < k; j++) {
                sum += nums[i + j];
            }
            returnNums[i] = sum;
        }
        return returnNums;
    }
}
