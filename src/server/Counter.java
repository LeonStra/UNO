package server;

public class Counter {
    private int counter;

    public Counter(){
        counter = 0;
    }

    //Getter
    public int getCounter() {
        return counter;
    }

    //Setter
    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void addCounter(int increment){
        this.counter += increment;
    }
}