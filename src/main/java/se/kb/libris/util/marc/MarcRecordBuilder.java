/*
 * MarcRecordBuilder.java
 *
 * Created on den 31 maj 2003, 20:52
 */

package se.kb.libris.util.marc;

/**
 *
 * @author  Martin Malmsten
 */
public interface MarcRecordBuilder {
    public MarcRecord createMarcRecord(); 
    public Controlfield createControlfield(String tag, String data);
    public Datafield createDatafield(String tag);
    public Subfield createSubfield(char code, String data);
}
