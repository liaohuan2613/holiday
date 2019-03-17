/**
 * First Unique Character in a String
 * Find the first unique character in a given string. You can assume that there is at least one unique character in the string.
 * <p>
 * Example
 * For "abaccdeff", return 'b'.
 */
public class FirstUniqueCharacter {
    public char firstUniqChar(String str) {
        // Write your code here
        for (int i = 0; i < str.length(); i++) {
            boolean flag = true;
            for (int j = 0; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j) && i != j) {
                    flag = false;
                }
            }
            if (flag) {
                return str.charAt(i);
            }
        }
        return ' ';
    }
}
