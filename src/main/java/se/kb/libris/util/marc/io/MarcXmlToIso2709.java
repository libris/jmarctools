/*
 * MarcXmlToIso2709.java
 *
 * Created on den 1 september 2003, 23:46
 */

package se.kb.libris.util.marc.io;

import java.util.*;
import se.kb.libris.util.marc.*;
/**
 *
 * @author  marma
 */
public class MarcXmlToIso2709 {
    public static void main(String[] args) throws Exception {
        if (args.length != 3 && args.length != 2) {
            System.err.println("usage: java se.kb.libris.util.marc.io.MarcXmlToIso2709 <record-tag> <output-encoding> [namespace]");
            System.exit(1);
        }

        MarcXmlRecordReader reader = null;
        
        if (args.length == 2) {
            reader = new MarcXmlRecordReader(System.in, args[0]);
        } else {
            reader = new MarcXmlRecordReader(System.in, args[0], args[2]);
        }
        
        MarcRecord record;
        while ((record = reader.readRecord()) != null) {            
            System.out.write(Iso2709Serializer.serialize(record, args[1]));
            System.err.println(record);
        }
    }   
}
