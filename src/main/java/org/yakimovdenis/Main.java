package org.yakimovdenis;

public class Main {
    public static void main(String[] args) {
        Greeter greeter = new Greeter(new Clock());
        greeter.calculateAndWrite(args);
    }
}
