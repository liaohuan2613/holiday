import java.util.Scanner;

/**
 * @author LHK
 *
 * Q: Given two string S and T, determine if S can be changed to T by deleting some letters (including 0 letter)
 */

public class EasyCode {

    public static void main(String[] args) {
        String[] ss = {"", ""};
        String[] as = {null, null};
        String[] cs = {null, ""};
        isContains(ss[0], ss[1]);
        isContains(as[0], as[1]);
        isContains(cs[0], cs[1]);

    }

    public static void isContains(String S, String T) {
        if (T == null) {
            System.out.println(true);
        } else if (S == null) {
            System.out.println(false);
        } else {
            System.out.println(S.contains(T));
        }
    }

}
