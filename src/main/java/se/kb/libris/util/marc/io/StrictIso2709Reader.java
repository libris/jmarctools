package se.kb.libris.util.marc.io;

import java.io.*;

public class StrictIso2709Reader extends Iso2709Reader {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    
    public StrictIso2709Reader(File f) throws IOException {
        super(f);
    }
    
    public StrictIso2709Reader(InputStream in) {
        super(in);
    }
    
    @Override
    public byte[] readIso2709() throws IOException {
        int i;
        byte b, last = 0;
        out.reset();

        while ((i = in.read()) != -1) {
            b = (byte)i;

            if (b == '\0') b = '0';
            if (b != '\r' && b != '\n') out.write(b);

            if (b == 0x1D && last == 0x1E) {
                return out.toByteArray();
            }

            last = b;
        }
        
        return null;
    }
    
    public static void main(String args[]) throws Exception {
        StrictIso2709Reader reader = new StrictIso2709Reader(System.in);
        byte b[] = null;
        
        while ((b = reader.readIso2709()) != null) {
            System.out.print(new String(b));
        }
    }
}
