/*
 * Controlfield.java
 *
 * Created on den 31 maj 2003, 16:03
 */

package se.kb.libris.util.marc;

/**
 *
 * @author  Martin Malmsten
 */
public interface Controlfield extends Field {
    public String getData();
    public void setData(String data);
    public char getChar(int idx);
    public void setChar(int idx, char c);
}
