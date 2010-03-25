/*
 * MarcXmlToIso2709.java
 *
 * Created on den 1 september 2003, 23:46
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import se.kb.libris.util.marc.*;
/**
 *
 * @author  marma
 */
public class ConversionTest {
    public static void main(String[] args) throws Exception {
        Iso2709Reader reader = new Iso2709Reader(System.in);
        byte record[];
        Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("collection");
        
        
        for (int i=0;i<256;i++) {
            System.err.println(String.valueOf(i) + ": " + ((byte)i));
        }
        
        while ((record = reader.readIso2709()) != null) {
            MarcRecord mr = Iso2709Deserializer.deserialize(record, "VRLIN");
            System.err.println("bibid: " + ((Controlfield)mr.getFields("001").iterator().next()).getData());
            byte record2[] = Iso2709Serializer.serialize(mr, "VRLIN");
            //byte record3[] = Iso2709Serializer.serialize(mr, "UTF-8");
            
            
            if (record.length != record2.length) {
                System.err.println("different length: " + ((Controlfield)mr.getFields("001").iterator().next()).getData() + " in=" + record.length + ", out=" + record2.length);
                continue;
            }
            
            for (int i=0;i<record.length;i++) {
                if (record[i] != record2[i]) {
                    System.err.println("warning: mismatch " + ((Controlfield)mr.getFields("001").iterator().next()).getData());
                    
                    System.err.print("  ");
                    for (int j=i-2;j<i+2;j++) {
                        System.err.print(String.valueOf(record[j]) + " ");
                    }
                    System.err.println();

                    System.err.print("  ");
                    for (int j=i-2;j<i+2;j++) {
                        System.err.print(String.valueOf(record2[j]) + " ");
                    }
                    System.err.println();                    
                }
            }
        }
    }
}
