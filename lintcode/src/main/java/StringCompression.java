/**
 * String Compression
 * Implement a method to perform basic string compression using the counts of repeated characters. For example, the string aabcccccaaa would become a2b1c5a3.
 * <p>
 * If the "compressed" string would not become smaller than the original string, your method should return the original string.
 * <p>
 * You can assume the string has only upper and lower case letters (a-z).
 * <p>
 * Example
 * str=aabcccccaaa return a2b1c5a3
 * str=aabbcc return aabbcc
 * str=aaaa return a4
 */


public class StringCompression {
    public static String compress(String originalString) {
        // write your code here
        String result = "";
        int len = originalString.length();
        for (int i = 0; i < len; i++) {
            char temp = originalString.charAt(i);
            int tempInt = 1;
            while (i + 1 < len && temp == originalString.charAt(i + 1)) {
                i++;
                tempInt++;
            }
            result += temp;
            result += tempInt;

        }
        if (originalString.length() > result.length()) {
            return result;
        }
        return originalString;
    }
}