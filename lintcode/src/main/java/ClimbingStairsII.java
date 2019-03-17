/**
 * Climbing Stairs II
 * A child is running up a staircase with n steps, and can hop either 1 step, 2 steps, or 3 steps at a time.
 * Implement a method to count how many possible ways the child can run up the stairs.
 * <p>
 * Example
 * n=3
 * 1+1+1=2+1=1+2=3=3
 * <p>
 * return 4
 */
public class ClimbingStairsII {
    public static int climbStairs2(int n) {
        // write your code here
        int[] sum = new int[n + 4];
        sum[0] = 1;
        sum[1] = 1;
        sum[2] = 2;
        sum[3] = 4;
        for (int i = 4; i <= n; i++) {
            sum[i] = sum[i - 1] * 2 - sum[i - 4];
        }
        return sum[n];
    }

    public static void main(String[] args) {
        System.out.println(climbStairs2(32));
    }
}
