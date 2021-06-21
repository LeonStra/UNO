package test;

public class t2 {
    public t1 t;

    public t2(){
        this.t = new t1(this);
    }

    public void test(String s){System.out.println(s);}
}