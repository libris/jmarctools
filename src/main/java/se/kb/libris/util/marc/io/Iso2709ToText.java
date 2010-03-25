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
public class Iso2709ToText {
    public static void main(String[] args) throws Exception {
        StrictIso2709Reader reader = new StrictIso2709Reader(System.in);
        byte record[];
        
        while ((record = reader.readIso2709()) != null) {
            MarcRecord mr = Iso2709Deserializer.deserialize(record);
            //System.out.write(Iso2709Serializer.serialize(record, args[1]));
            System.out.println(mr);
        }
    }   
}
