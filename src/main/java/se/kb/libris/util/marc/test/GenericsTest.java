/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kb.libris.util.marc.test;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author marma
 */
public class GenericsTest {
    public static void main(String args[]) {
        List<? extends String> list = new LinkedList<String>();
        //list.listIterator().add("bla");
    }
}
