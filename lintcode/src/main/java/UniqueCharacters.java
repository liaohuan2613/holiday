/**
 * Unique Characters
 * Implement an algorithm to determine if a string has all unique characters.
 * <p>
 * Example
 * Given "abc", return true.
 * <p>
 * Given "aab", return false.
 * <p>
 * Challenge
 * What if you can not use additional data structures?
 */

public class UniqueCharacters {
    public static boolean isUnique(String str) {
        for (int i = 0; i < str.length(); i++) {
            for (int j = i + 1; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }
}
