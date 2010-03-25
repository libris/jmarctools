/*
 * ControlFieldImpl.java
 *
 * Created on den 31 maj 2003, 21:11
 */

package se.kb.libris.util.marc.impl;

import java.io.*;
import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class ControlfieldImpl extends FieldImpl implements Controlfield {
    char data[] = null;
    
    public ControlfieldImpl(String tag, String data) {
        super(tag);
        this.data = data.toCharArray();
    }

    public ControlfieldImpl(String tag, byte data[], int pos1, int pos2) {
        super(tag);
        this.tag = tag;
        this.data = new String(data, pos1, (pos2-pos1+1)).toCharArray();
    }

    public ControlfieldImpl(String tag, byte data[], int pos1, int pos2, String encoding) throws UnsupportedEncodingException {
        super(tag);
        this.tag = tag;
        this.data = new String(data, pos1, (pos2-pos1+1), encoding).toCharArray();
    }


    public String getTag() {
        return tag;
    }
    
    public String getData() {
        return new String(data);
    }
    
    public void setData(String data) {
        this.data = data.toCharArray();
    }
    
    public void setChar(int idx, char c) {
        data[idx] = c;
    }    
    
    public char getChar(int idx) {
        return data[idx];
    }
}
