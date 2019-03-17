package com.lhk.test;

import java.util.Scanner;

public class Test2 {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String asStr = sc.nextLine();
        String[] asStrGroup = asStr.split("");
        int[] as = new int[asStrGroup.length];
        for (int i = 0; i < asStrGroup.length; i++) {
            as[i] = Integer.valueOf(asStrGroup[i]);
        }
        counter(as);

    }

    private static void counter(int[] answers) {
        int[] counter = new int[4];
        for (int i = 0; i < answers.length; i++) {
            switch (i + 1) {
                case 1:
                case 5:
                case 13:
                case 17:
                case 19:
                case 21:
                case 25:
                case 28:
                case 33:
                case 37:
                case 41:
                case 45:
                case 57:
                case 61:
                case 68:
                case 71:
                case 75:
                case 79:
                case 83:
                case 85:
                    if (answers[i] == 1) {
                        counter[1]++;
                    } else {

                    }
                    break;
                case 9:
                case 49:
                    if (answers[i] == 0) {
                        counter[1]++;
                    }
                    break;
                case 3:
                case 7:
                case 12:
                case 15:
                case 23:
                case 32:
                case 35:
                case 39:
                case 43:
                case 47:
                case 51:
                case 53:
                case 55:
                case 59:
                case 60:
                    if (answers[i] == 1) {
                        counter[0]++;
                    }
                    break;
                case 30:
                case 66:
                case 77:
                    if (answers[i] == 0) {
                        counter[0]++;
                    }
                    break;
                case 2:
                case 6:
                case 10:
                case 14:
                case 18:
                case 22:
                case 26:
                case 29:
                case 34:
                case 38:
                case 42:
                case 46:
                case 50:
                case 54:
                case 58:
                case 62:
                case 65:
                case 69:
                case 72:
                case 74:
                case 76:
                case 81:
                case 84:
                    if (answers[i] == 1) {
                        counter[2]++;
                    }
                    break;
                case 4:
                case 11:
                case 16:
                case 20:
                case 40:
                case 67:
                case 70:
                case 73:
                case 80:
                case 82:
                case 87:
                    if (answers[i] == 0) {
                        counter[3]++;
                    }
                    break;
                case 8:
                case 24:
                case 27:
                case 31:
                case 36:
                case 44:
                case 48:
                case 52:
                case 56:
                case 63:
                case 78:
                    if (answers[i] == 1) {
                        counter[3]++;
                    }
                    break;
            }
        }
        System.out.println("P(神经质)" + counter[0] + ",\t E(内外向)" + counter[1] + ",\t N(情绪稳定性)" + counter[2] + ", \t L(真实性)" + counter[3]);
    }
}

