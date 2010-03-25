/*
 * Iso2709MarcRecordReader.java
 *
 * Created on den 8 juni 2003, 17:13
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class Iso2709MarcRecordWriter implements MarcRecordWriter {
    String encoding = null;
    OutputStream out = null;
    
    public Iso2709MarcRecordWriter(File f) throws IOException {
        this.out = new FileOutputStream(f);
    }
    
    public Iso2709MarcRecordWriter(OutputStream out) {
        this.out = out;
    }
        
    public Iso2709MarcRecordWriter(File f, String encoding) throws IOException {
        this.out = new FileOutputStream(f);
        this.encoding = encoding;
    }
    
    public Iso2709MarcRecordWriter(OutputStream out, String encoding) {
        this.out = out;
        this.encoding = encoding;
    }
        
    public void writeRecord(MarcRecord mr) throws IOException {
        byte rec[] = null;
        
        if (encoding == null) {
            rec = Iso2709Serializer.serialize(mr);
        } else {
            if (encoding.equalsIgnoreCase("UTF8") || encoding.equalsIgnoreCase("UTF-8")) {
                mr.setLeader(9, 'a');
            } else if (encoding.equalsIgnoreCase("ISO-8859-1")) {
                mr.setLeader(9, '1');
            } else if (encoding.equalsIgnoreCase("MARC8")) {
                mr.setLeader(9, ' ');
            } else if (encoding.equalsIgnoreCase("VRLIN")) {
                mr.setLeader(9, ' ');
            }
            
            rec = Iso2709Serializer.serialize(mr, encoding);
        }
        
        out.write(rec);
    }
    
    public void close() {        
    }    
}
