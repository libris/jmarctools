/*
 * Iso2709Validator.java
 *
 * Created on den 2 september 2003, 13:17
 */

package se.kb.libris.util.marc.io;

/**
 *
 * @author  marma
 */
public class Iso2709Validator {
    static boolean debug = true;

    static boolean validate(byte[] b) {        
        // 0 check input
        if (b.length < 24 + 1) {
            if (debug) System.err.println("error: record to short to be a marc-record");
            
            return false;
        }
        
        // 1 check length
        try {
            int length = Integer.parseInt(new String(b, 0,5));
        } catch (NumberFormatException e) {
            if (debug) System.err.println("error: first five bytes ('" + new String(b, 0, 5) + "') is not a number");
            return false;
        }
        
        int baseAddr = Integer.parseInt(new String(b, 12, 5)), nFields = (baseAddr - 24) / 12;

        // 2 check base addresss
        if (b[baseAddr-1] != se.kb.libris.util.marc.MarcRecord.END_OF_FIELD) {
            if (debug) System.err.println("error: ");
            return false;
        }
        
        // 3 check fields
        
        // 4 check end of record
        if (b[b.length - 1] != se.kb.libris.util.marc.MarcRecord.END_OF_RECORD) {
            if (debug) System.err.println("error: record does not end with END_OF_RECORD");
            return false;
        }
        
        return true;
    }
    
    public static void main(String args[]) throws Exception {
        Iso2709Reader reader = new Iso2709Reader(System.in);
        
        byte record[];
        
        while ((record = reader.readIso2709()) != null) {
            if (!validate(record)) System.exit(1);
        }        
    }
}
