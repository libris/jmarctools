package se.kb.libris.util.marc.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarcXmlTestInputStream extends InputStream {
    int max = -1, i = 0, n = 0, closeAfter = -1;
    boolean closed = false, done = false;
    byte record[], current[];
            
    public MarcXmlTestInputStream() {
        this(-1);
    }
    
    public MarcXmlTestInputStream(int max) {
        this(max, -1);
    }
    
    public MarcXmlTestInputStream(int max, int closeAfter) {
        try {
            current = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n".getBytes("UTF-8");
            record = ("<record>\n" +
                    "  <leader>00000cam a2200000 ar4500</leader>\n" +
                    "  <controlfield tag=\"001\">123</controlfield>\n" +
                    "  <datafield tag=\"100\" ind1=\" \" ind2=\" \">\n" +
                    "    <subfield code=\"a\">Strindberg, August,</subfield>\n" +
                    "    <subfield code=\"d\">1849-1912</subfield>\n" +
                    "  </datafield>\n" +
                    "  <datafield tag=\"245\" ind1=\" \" ind2=\" \">\n" +
                    "    <subfield code=\"a\">Röda rummet</subfield>\n" +
                    "  </datafield>\n" +
                    "</record>\n").getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MarcXmlTestInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
                   
        this.max = max;
        this.closeAfter = closeAfter;
    }
    
    @Override
    public int read() throws IOException {
        if (done) return -1;
        if (closed) throw new IOException("Stream closed prematurely");

        if (i != current.length) {
            return current[i++];
        } else {
            if (n == max) {
                current = "</collection>\n".getBytes("UTF-8");
            } else if (n == max + 1) {
                done = true;
            } else {
                current = record;
            }

            if (n++ >= closeAfter && closeAfter != -1) close();

            i = 0;
            return read();
        }
    }
    
    @Override
    public void close() throws IOException {
        closed = true;
    }
}
