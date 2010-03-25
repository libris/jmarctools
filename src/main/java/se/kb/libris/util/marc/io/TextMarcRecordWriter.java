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
public class TextMarcRecordWriter implements MarcRecordWriter {
    String encoding = null;
    OutputStream out = null;
    
    public TextMarcRecordWriter(File f) throws IOException {
        this.out = new FileOutputStream(f);
    }
    
    public TextMarcRecordWriter(OutputStream out) {
        this.out = out;
    }
        
    public TextMarcRecordWriter(File f, String encoding) throws IOException {
        this.out = new FileOutputStream(f);
        this.encoding = encoding;
    }
    
    public TextMarcRecordWriter(OutputStream out, String encoding) {
        this.out = out;
        this.encoding = encoding;
    }
        
    public void writeRecord(MarcRecord mr) throws IOException {
        byte rec[] = null;
        
        if (encoding == null) {
            rec = (mr.toString() + "\n").getBytes();
        } else {
            rec = (mr.toString() + "\n").getBytes(encoding);
        }
        
        out.write(rec);
    }
    
    public void close() {        
    }
}
