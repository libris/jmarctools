/*
 * Iso2709Reader.java
 *
 * Created on den 8 juni 2003, 16:46
 */

package se.kb.libris.util.marc.io;

import java.io.*;

/**
 *
 * @author  Martin Malmsten
 */
public class Iso2709Reader {
    InputStream in = null;
    
    public Iso2709Reader(File f) throws IOException {
        in = new FileInputStream(f);
    }
    
    public Iso2709Reader(InputStream in) {
        this.in = in;
    }
    
    public byte[] readIso2709() throws IOException {
        byte tmp[] = new byte[5];
        int n = in.read(tmp);
        
        if (n != -1) {
            int recLength = Integer.parseInt(new String(tmp));
            
            byte rec[] = new byte[recLength];
            System.arraycopy(tmp, 0, rec, 0, 5);
            in.read(rec, 5, recLength-5);
            
            return rec;
        } else {
            return null;
        }
    }
    
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
        }
    }
}
