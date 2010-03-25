/*
 * MarcRecordReader.java
 *
 * Created on den 8 juni 2003, 16:44
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.io.*;

/**
 *
 * @author  Martin Malmsten
 */
public class MarcXmlRecordWriter implements MarcRecordWriter {
    Document doc = null;
    Transformer transformer = null;
    OutputStream out = null;
    String encoding = "utf-8";
    
    public MarcXmlRecordWriter(File f) throws IOException {
        this(new FileOutputStream(f));
    }
    
    public MarcXmlRecordWriter(File f, String encoding) throws IOException {
        this(new FileOutputStream(f), encoding);
    }
    
    public MarcXmlRecordWriter(OutputStream out) throws IOException {
        try {
            doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            System.err.println();
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            System.err.println();
        }
        
        this.out = out;
        writeHeader();
    }
    
    public MarcXmlRecordWriter(OutputStream out, String encoding) throws IOException {
        try {
            doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            System.err.println();
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            System.err.println();
        }
        
        this.out = out;
        this.encoding = encoding;
        
        writeHeader();
    }

    public void writeRecord(MarcRecord mr) throws IOException {
        DocumentFragment docFragment = DomSerializer.serialize(mr, doc);
        StringWriter sw = new StringWriter();        
        Source source = new DOMSource(docFragment);
        Result result = new StreamResult(sw);
        
        try {
            transformer.setOutputProperty("encoding", encoding);
            transformer.transform(source, result);            
        } catch (javax.xml.transform.TransformerException e) {
            System.err.println(e.getMessage());
        }
        
        out.write(("  " + sw.getBuffer() + "\n").getBytes(encoding));
        
        out.flush();
        
        return;
    }
    
    public void writeHeader() throws IOException {
        if (encoding.equalsIgnoreCase("UTF8") || encoding.equalsIgnoreCase("UTF-8")) {
            out.write(("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n").getBytes(encoding));
        } else if (encoding.equalsIgnoreCase("Latin1Strip") || encoding.equalsIgnoreCase("ISO-8859-1")) {
            out.write(("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n").getBytes(encoding));
        } else {
            out.write(("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n").getBytes(encoding));
        }

        out.write(("<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n").getBytes(encoding));
    }
    
    public void close() throws IOException {
        out.write("</collection>".getBytes(encoding));
    }
}
