
/**
 * Q: Rectangle Overlap
 * Given two rectangles, find if the given two rectangles overlap or not.
 * <p>
 * Example
 * Given l1 = [0, 8], r1 = [8, 0], l2 = [6, 6], r2 = [10, 0], return true
 * <p>
 * Given l1 = [0, 8], r1 = [8, 0], l2 = [9, 6], r2 = [10, 0], return ``false`
 * <p>
 * Notice
 * l1: Top Left coordinate of first rectangle.
 * r1: Bottom Right coordinate of first rectangle.
 * l2: Top Left coordinate of second rectangle.
 * r2: Bottom Right coordinate of second rectangle.
 * <p>
 * l1 != r2 and l2 != r2
 */

class Point {
    int x;
    int y;

    Point() {
        x = 0;
        y = 0;
    }

    Point(int a, int b) {
        x = a;
        y = b;
    }
}

public class RectangleOverlap {
    public static boolean doOverlap(Point l1, Point r1, Point l2, Point r2) {
        // write your code here
        double top_bottom = (l1.y - r1.y) / 2.0;
        double left_right = (r1.x - l1.x) / 2.0;
        double centerPointY = r1.y + top_bottom;
        double centerPointX = l1.x + left_right;
        double top_bottom2 = (l2.y - r2.y) / 2.0;
        double left_right2 = (r2.x - l2.x) / 2.0;
        double centerPointY2 = r2.y + top_bottom2;
        double centerPointX2 = l2.x + left_right2;
        double centerYtoY2 = centerPointY2 >= centerPointY ? centerPointY2 - centerPointY : centerPointY - centerPointY2;
        double centerXtoX2 = centerPointX2 >= centerPointX ? centerPointX2 - centerPointX : centerPointX - centerPointX2;
        if ((top_bottom + top_bottom2) >= centerYtoY2 && (left_right + left_right2) >= centerXtoX2) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(doOverlap(new Point(0, 8), new Point(8, 0), new Point(6, 6), new Point(10, 0)));
    }

}
