/*
 * Datafield.java
 *
 * Created on den 31 maj 2003, 15:55
 */

package se.kb.libris.util.marc;

import java.util.*;

/**
 *
 * @author  Martin Malmsten
 */
public interface Datafield extends Field{
    public char getIndicator(int idx);
    public Datafield setIndicator(int idx, char c);
    public void addSubfield(Subfield f);
    public Datafield addSubfield(char code, String data);
    public Subfield createSubfield(char code, String data);
    public List<Subfield> getSubfields();
    public List<Subfield> getSubfields(String regexp);
    public Iterator<Subfield> iterator();
    public Iterator<Subfield> iterator(String regexp);
    public ListIterator<Subfield> listIterator();
    public ListIterator<Subfield> listIterator(String regexp);
}
