package se.kb.libris.util.marc.io;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import se.kb.libris.util.marc.*;

public class Iso2709ToXml {
    public static void main(String[] args) throws Exception {
	String encoding = "UTF-8";

	if (args.length != 0) {
	    encoding = args[0];
	}

        StrictIso2709Reader reader = new StrictIso2709Reader(System.in);
        byte record[];
        Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("collection");
        int i = 0, start=10000, nRecords=10000;
        
        while ((record = reader.readIso2709()) != null) {
            MarcRecord mr = Iso2709Deserializer.deserialize(record, encoding);
            root.appendChild(DomSerializer.serialize(mr, doc));            
        }       
        
        doc.appendChild(root);

        Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        Result result = new StreamResult(System.out);
        Source source = new DOMSource(doc);
        transformer.transform(source, result);
    }   
}
