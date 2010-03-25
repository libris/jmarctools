/*
 * SubfieldImpl.java
 *
 * Created on den 18 augusti 2003, 20:45
 */

package se.kb.libris.util.marc.impl;

/**
 *
 * @author  Martin Malmsten
 */
public class SubfieldImpl implements se.kb.libris.util.marc.Subfield {
    char code;
    String data = null;
    
    /** Creates a new instance of SubfieldImpl */
    public SubfieldImpl(char code, String data) {
        this.code = code;
        this.data = data;
    }
    
    public char getCode() {
        return code;
    }
    
    public String getData() {
        return data;
    }
    
    public void setCode(char code) {
        this.code = code;
    }
    
    public void setData(String data) {
        this.data = data;
    }
}
