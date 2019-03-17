/**
 * Q: Log Sorting
 * Give a log, consisting of List< String >, each element representing one line of logs. Each line of log information is separated by a space. The first is the ID of the log, followed by the log content.
 * There are two ways to make content:
 * <p>
 * All consist of letters and spaces.
 * All consist of numbers and spaces.
 * Now that the logs are sorted, it is required that component 1 be sorted in order of content lexicography and placed at the top, and component 2 should be placed at the bottom and output in the order of input. (Note that the space also belongs to the content, and when the lexicographic order of the composition method 1 is equal, sort according to the dictionary order of log ID., and the guarantee ID is not repeated)
 * Example
 * Given
 * <p>
 * [
 * "zo4 4 7",
 * "a100 Act zoo",
 * "a1 9 2 3 1",
 * "g9 act car"
 * ]
 * , return
 * <p>
 * [
 * "a100 Act zoo",
 * "g9 act car",
 * "zo4 4 7",
 * "a1 9 2 3 1"
 * ]
 * Explanation:
 * "Act zoo" < "act car", So the output is as above.
 * Given
 * <p>
 * [
 * "zo4 4 7",
 * "a100 Actzoo",
 * "a100 Act zoo",
 * "Tac Bctzoo",
 * "Tab Bctzoo",
 * "g9 act car"
 * ]
 * , return
 * <p>
 * [
 * "a100 Act zoo",
 * "a100 Actzoo",
 * "Tab Bctzoo",
 * "Tac Bctzoo",
 * "g9 act car",
 * "zo4 4 7"
 * ]
 * Explanation:
 * Because "Bctzoo" == "Bctzoo", the comparison "Tab" and "Tac" have "Tab" < Tac ", so" Tab Bctzoo "< Tac Bctzoo".
 * Because ' '<'z', so "A100 Act zoo" < A100 Actzoo".
 * Notice
 * The total number of logs entered was n, and n < = 10000.
 * <p>
 * The length of each line is mi, and mi < = 100.
 */

public class LogSorting {
    public static void main(String[] args) {
        for (String str : logSort(new String[]{"zo4 4 7", "a1 9 2 3 1", "g9 act car", "a100 Act zoo", "a1 5 2 3 1",})) {
            System.out.println(str);
        }
    }

    /**
     * @param logs: the logs
     * @return: the log after sorting
     */
    public static String[] logSort(String[] logs) {
        // Write your code here
        int count = 0;
        for (int i = logs.length - 1; i >= 0; i--) {
            String firstContentStr = logs[i].substring(logs[i].indexOf(' ') + 1, logs[i].length());
            char firstIndexChar = firstContentStr.charAt(0);
            if (firstIndexChar <= '9' && '0' <= firstIndexChar) {
                String temp = logs[i];
                logs[i] = logs[logs.length - 1 - count];
                logs[logs.length - 1 - count] = temp;
                count++;
            }
        }
        for (int i = 0; i < logs.length - count; i++) {
            for (int j = i + 1; j < logs.length - count; j++) {
                String firstIdStr = logs[i].substring(0, logs[i].indexOf(' '));
                String firstContentStr = logs[i].substring(logs[i].indexOf(' ') + 1, logs[i].length());
                String secondIdStr = logs[j].substring(0, logs[j].indexOf(' '));
                String secondContentStr = logs[j].substring(logs[j].indexOf(' ') + 1, logs[j].length());
                if (firstContentStr.equals(secondContentStr)) {
                    if (!needSort(firstIdStr, secondIdStr)) {
                        continue;
                    }
                } else if (!needSort(firstContentStr, secondContentStr)) {
                    continue;
                }
                String temp = logs[i];
                logs[i] = logs[j];
                logs[j] = temp;
            }
        }

        return logs;
    }

    private static boolean needSort(String firstStr, String secondStr) {
        boolean flag = false;
        int firstLen = firstStr.length();
        int secondLen = secondStr.length();
        int len = firstLen < secondLen ? firstLen : secondLen;
        for (int k = 0; k < len; k++) {
            char a = firstStr.charAt(k);
            char b = secondStr.charAt(k);
            if (a > b) {
                flag = true;
                break;
            } else if (a < b) {
                break;
            }
            if (k == len - 1 && firstLen > secondLen) {
                flag = true;
            }
        }
        return flag;
    }
}
