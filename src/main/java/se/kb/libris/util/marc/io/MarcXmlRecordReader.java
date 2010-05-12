package se.kb.libris.util.marc.io;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.*;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.xml.sax.SAXException;
import se.kb.libris.util.marc.*;

public class MarcXmlRecordReader implements MarcRecordReader {
    LinkedList records = null;
    SAXParser parser = null;
    InputStream in = null;
    String namespace = null, start = "/record";
    MarcRecordBuilder recordBuilder = MarcRecordBuilderFactory.newBuilder();
    boolean available = false;
    public static boolean debug = false;
    Thread readerThread = null;
    Exception exception = null;
    
    public MarcXmlRecordReader(InputStream _in) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
        in = _in;
        init();
    }
    
    public MarcXmlRecordReader(InputStream _in, String _start) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
        in = _in;
        start = _start;
        init();
    }
    
    public MarcXmlRecordReader(InputStream _in, String _start, String _namespace) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
        in = _in;
        start = _start;
        namespace = _namespace;
        init();
    }

    public static MarcRecord fromXml(String str) throws IOException {
        MarcRecord mr = null;

        try {
            byte bytes[] = str.getBytes("UTF-8");
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            MarcXmlRecordReader reader = new MarcXmlRecordReader(bin);
            mr = reader.readRecord();
            reader.close();

            return mr;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MarcXmlRecordReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MarcXmlRecordReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MarcXmlRecordReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mr;
    }

    public void init() {
        records = new LinkedList();
        final MarcXmlRecordReader reader = this;

        readerThread = (new Thread() {
            public void run() {
                try {
                    if (debug) System.err.println(this.toString() + " starting");
                    parser.parse(in, new MarcXmlHandler(records, reader, start, namespace));
                    if (debug) System.err.println(this.toString() + " done");
                } catch (Exception e) {
                    if (debug) System.err.println(e.getMessage());
                    if (debug) e.printStackTrace(System.err);
                    exception = e;
                } finally {
                    reader.addRecord(null);
                }
            } 
        });
        
        readerThread.start();
    }
    
    public synchronized MarcRecord readRecord() throws IOException {        
        MarcRecord ret = null;
        
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        
        if (exception != null) {
            throw new IOException(exception.getMessage());
        }
        
        available = false;
        
        if (!records.isEmpty()) {
            ret = (MarcRecord)records.removeFirst();
        }
        
        notifyAll();

        if (ret == null) {
            if (debug) System.err.println(Thread.currentThread().toString() + " returning null");
        } else {
            if (debug) System.err.println(Thread.currentThread().toString() + " returning record");
        }

        return ret;
    }
    
    public synchronized void addRecord(MarcRecord record) {
        if (record == null) {
            if (debug) System.err.println(Thread.currentThread().toString() + " adding null");
        } else {
            if (debug) System.err.println(Thread.currentThread().toString() + " adding record");
        }
        
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        
        if (record != null) {
            records.add(record);
        }
        
        available = true;
        notifyAll();
    }
    
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
        }
        
        readerThread.interrupt();
    }
    
    protected class MarcXmlHandler extends org.xml.sax.helpers.DefaultHandler {
        MarcXmlRecordReader reader = null;
        LinkedList list = null;
        String state = "", start = null, namespace = null;;
        StringBuffer text = new StringBuffer();
        MarcRecord currentRecord = null;
        Controlfield currentControlField = null;
        Datafield currentDataField = null;
        Subfield currentSubField = null;
        
        public MarcXmlHandler(LinkedList l, MarcXmlRecordReader _reader, String _start, String _namespace) {
            reader = _reader;
            list = l;
            namespace = _namespace;
            start = _start;
            
            if (namespace == null) {
                namespace = "";
            } else {
                namespace += ":";
            }
        }
        
        public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException {
            if (debug) System.err.println("end uri: " + uri + ", localName: " + localName + ", qName: " + qName + ", state " + state + ", start " + start);
            if (state.startsWith(start)) {
                if (qName.equals(namespace + "record")) {
                    if (debug) System.err.println(Thread.currentThread().toString() + " adding record");
                    reader.addRecord(currentRecord);                    
                } else if (qName.equals(namespace + "leader")) {
                    currentRecord.setLeader(text.toString());
                } else if (qName.equals(namespace + "controlfield")) {
                    currentControlField.setData(text.toString());
                    currentRecord.addField(currentControlField);
                } else if (qName.equals(namespace + "datafield")) {
                    currentRecord.addField(currentDataField);
                } else if (qName.equals(namespace + "subfield")) {
                    currentSubField.setData(text.toString().replace('\r', ' ').replace('\n', ' '));
                    currentDataField.addSubfield(currentSubField);
                }
            }
            
            text.setLength(0);
            
            state = state.substring(0, state.lastIndexOf('/'));
        }
        
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
            if (debug) System.err.println("uri: " + uri + ", localName: " + localName + ", qName: " + qName);
            
            state += "/" + qName;
            
            if (debug) System.err.println("state: " + state + ", start: " + start);
            
            if (state.startsWith(start)) {
                if (qName.equals(namespace + "record")) {
                    currentRecord = recordBuilder.createMarcRecord();
                } else if (qName.equals(namespace + "controlfield")) {
                    currentControlField = recordBuilder.createControlfield(attributes.getValue("tag"), "");
                } else if (qName.equals(namespace + "datafield")) {
                    String ind1 = attributes.getValue("ind1"), ind2 = attributes.getValue("ind2");
                    currentDataField = recordBuilder.createDatafield(attributes.getValue("tag"));
                    
                    if (!ind1.equals("")) {
                        currentDataField.setIndicator(0, ind1.charAt(0));
                    }
                    
                    if (!ind2.equals("")) {
                        currentDataField.setIndicator(1, ind2.charAt(0));
                    }
                } else if (qName.equals(namespace + "subfield")) {
                    String code = attributes.getValue("code");
                    currentSubField = recordBuilder.createSubfield(code.charAt(0), "");
                }
                
                text.setLength(0);
            }
        }
        
        public void characters(char[] values, int start, int length) throws org.xml.sax.SAXException {
            text.append(values, start, length);
        }
    }
}
