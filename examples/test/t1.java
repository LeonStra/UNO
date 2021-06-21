package test;

public class t1 {
    public t2 t;

    public t1(t2 t){
        this.t = t;
    }

    public void m(){
        t.test("T");
    }
}
