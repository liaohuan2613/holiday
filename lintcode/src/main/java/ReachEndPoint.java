/**
 * Q:
 * Can Reach The Endpoint
 * Given a map size of m*n, 1 means space, 0 means obstacle, 9 means the endpoint. You start at (0,0) and return whether you can reach the endpoint.
 * <p>
 * Example
 * Input:[[1,1,1],[1,1,1],[1,1,9]]
 * Output:true
 */

public class ReachEndPoint {

    public static void main(String[] args) {
        System.out.println(reachEndpoint(new int[][]{{1, 1, 1, 1, 1}, {1, 0, 0, 0, 0}, {1, 1, 1, 0, 9}}));
    }

    public static boolean reachEndpoint(int[][] map) {
        // Write your code here
        return stackStep(map, 0, 0, false);
    }

    public static boolean stackStep(int[][] map, int i, int j, boolean flag) {
        if (!flag) {
            if (map[i][j] != 9) {
                map[i][j] = 0;
                if (i + 1 < map.length && map[i + 1][j] != 0) {
                    flag = stackStep(map, i + 1, j, flag);
                }
                if (j + 1 < map[i].length && map[i][j + 1] != 0 && !flag) {
                    flag = stackStep(map, i, j + 1, flag);
                }
                if (i - 1 >= 0 && map[i - 1][j] != 0 && !flag) {
                    flag = stackStep(map, i - 1, j, flag);
                }
                if (j - 1 >= 0 && map[i][j - 1] != 0 && !flag) {
                    flag = stackStep(map, i, j - 1, flag);
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

}
