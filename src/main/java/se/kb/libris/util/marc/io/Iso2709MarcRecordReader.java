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
public class Iso2709MarcRecordReader implements MarcRecordReader {
    Iso2709Reader isoReader = null;
    String encoding = null;
    
    public Iso2709MarcRecordReader(File f) throws IOException {
        isoReader = new StrictIso2709Reader(f);
    }
    
    public Iso2709MarcRecordReader(InputStream in) {
        isoReader = new StrictIso2709Reader(in);
    }

    public Iso2709MarcRecordReader(Iso2709Reader reader) {
        isoReader = reader;
    }

    public Iso2709MarcRecordReader(Iso2709Reader reader, String encoding) {
        isoReader = reader;
        this.encoding = encoding;
    }

    public Iso2709MarcRecordReader(File f, String encoding) throws IOException {
        isoReader = new StrictIso2709Reader(f);
        this.encoding = encoding;
    }
    
    public Iso2709MarcRecordReader(InputStream in, String encoding) {
        isoReader = new StrictIso2709Reader(in);
        this.encoding = encoding;
    }
    
    public MarcRecord readRecord() throws IOException {
        byte rec[] = isoReader.readIso2709();
        
        if (rec != null) {
            if (encoding == null) {
                return Iso2709Deserializer.deserialize(rec);
            } else {
                return Iso2709Deserializer.deserialize(rec, encoding);
            }
        } else {
            return null;
        }
    }

    public void close() {
        isoReader.close();
    }
}
