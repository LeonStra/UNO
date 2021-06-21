import java.util.Collections;
import java.util.LinkedList;

public class Liked {

    public static void main(String[] args){
        LinkedList<String> l = new LinkedList();
        l.add("1");
        l.add("2");
        l.add("3");
        l.add("4");
        l.add("5");
        Collections.shuffle(l);

        System.out.println(l);
        String e = l.pollFirst();
        l.addLast(e);
        System.out.println(l);
    }
}