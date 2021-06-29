package test;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        ArrayList<String> a = new ArrayList<>();
        a.add("hi");
        a.add("ho");
        System.out.println(a.contains("hi"));
        System.out.println(a.contains("ho"));
        System.out.println(a.contains("hi"));
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