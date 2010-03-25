/*
 * Field.java
 *
 * Created on den 31 maj 2003, 21:14
 */

package se.kb.libris.util.marc.impl;

import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class FieldImpl implements Field {
    String tag = null;
    
    public FieldImpl(String tag) {
        this.tag = tag;
    }
    
    public String getTag() {
        return tag;
    }
}
