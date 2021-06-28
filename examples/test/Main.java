package test;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        Main m = new Main();
        Sup s = new Sub();
        m.test(s);
    }

    public void test(Sup s){
        System.out.println("Sup");
    }

    public void test(Sub s){
        System.out.println("Sub");
    }
}

class Sup{}

class Sub extends Sup{}