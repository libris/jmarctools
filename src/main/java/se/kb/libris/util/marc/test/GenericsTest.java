package se.kb.libris.util.marc.test;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GenericsTest {
    public static void main(String args[]) {
        List<String> list = new LinkedList<String>();
        ((ListIterator<String>)list.listIterator()).add("bla");

        List<A> list_a = new LinkedList<A>();
        list_a.add(new B());
    }
}
