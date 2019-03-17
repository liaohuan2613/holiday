
/**
 * Q:
 * <p>
 * Given an array, find two numbers in the array and their sum is closest to the target value but does not exceed the target value, return their sum.
 * <p>
 * Example
 * Input:target = 15
 * array = [1,3,5,11,7]
 * Output:14
 * Notice
 * if there is no result meet the requirement, return -1.
 */

public class ClosestTargetValue {

    public static void main(String[] args) {
        closestTargetValue(-10, new int[]{-12, -5, 14, 5, 0, -16});
    }

    /**
     * @param target: the target
     * @param array:  an array
     * @return: the closest value
     */
    public static int closestTargetValue(int target, int[] array) {
        // Write your code here
        int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE;
        if (array.length >= 2) {
            for (int num : array) {
                if (num < first) {
                    second = first;
                    first = num;
                } else if (num < second) {
                    second = num;
                }
            }
            if (first + second > target) {
                return -1;
            } else {
                int oldSum = -Integer.MAX_VALUE;
                for (int i = 0; i < array.length; i++) {
                    for (int j = i + 1; j < array.length; j++) {
                        int sum = array[i] + array[j];
                        if (sum <= target && oldSum < sum) {
                            oldSum = sum;
                            System.out.println(oldSum + " : " + array[i] + "," + array[j]);
                        }
                    }
                }
                return oldSum;
            }
        }
        return -1;
    }
}
