package test;

public class Test2 {
    public static void main(String[] args) {
        System.out.println("123124");
        test("1");
    }

    public static void test(String... a) {
        System.out.println(1);
    }

    public static void test(String a) {
        System.out.println(2);
    }
}
