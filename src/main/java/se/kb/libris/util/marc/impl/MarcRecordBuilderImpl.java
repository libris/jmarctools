/*
 * MarcRecordBuilderImpl.java
 *
 * Created on den 31 maj 2003, 21:03
 */

package se.kb.libris.util.marc.impl;

import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class MarcRecordBuilderImpl implements se.kb.libris.util.marc.MarcRecordBuilder {
    
    /** Creates a new instance of MarcRecordBuilderImpl */
    public MarcRecordBuilderImpl() {
        se.kb.libris.util.marc.MarcRecordBuilderFactory.registerBuilder(this);
    }
    
    public MarcRecord createMarcRecord() {
        return new MarcRecordImpl();
    }
    
    public Controlfield createControlfield(String tag, String data) {
        return new ControlfieldImpl(tag, data);
    }
    
    public Datafield createDatafield(String tag) {
        return new DatafieldImpl(tag);
    }
    
    public Subfield createSubfield(char code, String data) {
        return new SubfieldImpl(code, data);
    }
    
}
