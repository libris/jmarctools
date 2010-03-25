/*
 * Subfield.java
 *
 * Created on den 31 maj 2003, 16:00
 */

package se.kb.libris.util.marc;

/**
 *
 * @author  Martin Malmsten
 */
public interface Subfield {
    public char getCode();
    public void setCode(char code);
    public String getData();
    public void setData(String data);
}
