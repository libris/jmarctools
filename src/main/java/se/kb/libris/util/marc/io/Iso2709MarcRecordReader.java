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
    boolean trustDirectory = false;
    
    public Iso2709MarcRecordReader(File f) throws IOException {
        isoReader = new StrictIso2709Reader(f);
    }

    public Iso2709MarcRecordReader(File f, boolean trustLength, boolean trustDirectory) throws IOException {
        isoReader = trustLength? new Iso2709Reader(f):new StrictIso2709Reader(f);
        this.trustDirectory = trustDirectory;
    }

    public Iso2709MarcRecordReader(InputStream in) {
        isoReader = new StrictIso2709Reader(in);
    }

    public Iso2709MarcRecordReader(InputStream in, boolean trustLength, boolean trustDirectory) {
        isoReader = trustLength? new Iso2709Reader(in):new StrictIso2709Reader(in);
        this.trustDirectory = trustDirectory;
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

    public Iso2709MarcRecordReader(File f, boolean trustLength, boolean trustDirectory, String encoding) throws IOException {
        isoReader = trustLength? new Iso2709Reader(f):new StrictIso2709Reader(f);
        this.encoding = encoding;
        this.trustDirectory = trustDirectory;
    }

    public Iso2709MarcRecordReader(InputStream in, boolean trustLength, boolean trustDirectory, String encoding) {
        isoReader = trustLength? new Iso2709Reader(in):new StrictIso2709Reader(in);
        this.encoding = encoding;
        this.trustDirectory = trustDirectory;
    }

    public Iso2709MarcRecordReader(File f, boolean trustLength, boolean trustDirectory, boolean discardBroken, String encoding) throws IOException {
        isoReader = trustLength? new Iso2709Reader(f):new StrictIso2709Reader(f, discardBroken);
        this.encoding = encoding;
        this.trustDirectory = trustDirectory;
    }

    public Iso2709MarcRecordReader(InputStream in, boolean trustLength, boolean trustDirectory, boolean discardBroken, String encoding) {
        isoReader = trustLength? new Iso2709Reader(in):new StrictIso2709Reader(in, discardBroken);
        this.encoding = encoding;
        this.trustDirectory = trustDirectory;
    }

    @Override
    public MarcRecord readRecord() throws IOException {
        byte rec[] = isoReader.readIso2709();
        
        if (rec != null) {
            if (encoding == null) {
                return Iso2709Deserializer.deserialize(rec, trustDirectory);
            } else {
                return Iso2709Deserializer.deserialize(rec, encoding, trustDirectory);
            }
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        isoReader.close();
    }
}
