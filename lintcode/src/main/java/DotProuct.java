/**
 * Q:
 * Dot Product
 * Given two array, find their dot product.(Wikipedia)
 * <p>
 * Example
 * Input:A = [1,1,1]
 * B = [2,2,2]
 * Output:6
 * Notice
 * if there is no dot product, return -1.
 */

public class DotProuct {

    public static void main(String[] args) {
        dotProduct(new int[]{1, 2, 3}, new int[]{1, 2, 3});
    }

    public static int dotProduct(int[] A, int[] B) {
        // Write your code here
        int sum = 0;
        if (A.length == B.length && A.length != 0) {
            for (int i = 0; i < A.length; i++) {
                sum += A[i] * B[i];
            }
        } else {
            return -1;
        }
        return sum;
    }
}
