package se.kb.libris.util.marc.io;

import java.io.*;

public class StrictIso2709Reader extends Iso2709Reader {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    boolean discard_broken = false;
    
    public StrictIso2709Reader(File f) throws IOException {
        super(f);
    }

    public StrictIso2709Reader(InputStream in) {
        super(in);
    }

    public StrictIso2709Reader(File f, boolean discard_broken) throws IOException {
        super(f);
    }

    public StrictIso2709Reader(InputStream in, boolean discard_broken) {
        super(in);
        this.discard_broken = discard_broken;
    }

    @Override
    public byte[] readIso2709() throws IOException {
        byte ret[] = readIso2709_1();

        if (ret == null) return null;

        int length = Integer.parseInt(new String(ret, 0, 5));

        while (ret != null && length != ret.length) {
            //System.err.println("warning: reported length (" + length + ") differs from real length (" + out.size() + ")");

            if (discard_broken) {
                ret = readIso2709_1();
                length = Integer.parseInt(new String(ret, 0, 5));
            } else {
                break;
            }
        }

        return ret;
    }

    public byte[] readIso2709_1() throws IOException {
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
