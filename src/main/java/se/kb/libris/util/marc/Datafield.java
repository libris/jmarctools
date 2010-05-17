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
    public List<? extends Subfield> getSubfields();
    public List<? extends Subfield> getSubfields(String regexp);
    public Iterator<? extends Subfield> iterator();
    public Iterator<? extends Subfield> iterator(String regexp);
    public ListIterator<? extends Subfield> listIterator();
    public ListIterator<? extends Subfield> listIterator(String regexp);
}
