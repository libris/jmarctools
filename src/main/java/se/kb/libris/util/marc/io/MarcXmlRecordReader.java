package se.kb.libris.util.marc.io;

import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import se.kb.libris.util.marc.*;

public class MarcXmlRecordReader implements MarcRecordReader {
    private static String DEFAULT_PATH = "/collection/record";
    //private static String DEFAULT_NAMESPACE = "http://www.loc.gov/MARC21/slim";
    private static String DEFAULT_NAMESPACE = null;
    
    String namespace = null, path = null;
    MarcRecordBuilder recordBuilder = MarcRecordBuilderFactory.newBuilder();
    boolean closed = false, done = false;

    XMLEventReader eventReader = null;
    String state = "";
    IOException exception = null;
    
    public MarcXmlRecordReader(InputStream in) throws IOException {
        this(in, DEFAULT_PATH, DEFAULT_NAMESPACE);
    }

    public MarcXmlRecordReader(InputStream in, String path) throws IOException {
        this(in, path, DEFAULT_NAMESPACE);
    }

    public MarcXmlRecordReader(InputStream in, String path, String namespace) throws IOException {
        this.path = path;
        this.namespace = namespace;
        
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            eventReader = factory.createXMLEventReader(in);
        } catch (XMLStreamException e) {
            exception = new IOException(e);
            throw exception;
        }
    }

    @Override
    public synchronized MarcRecord readRecord() throws IOException {
        if (closed) throw new IOException("Reader closed prematurely");
        if (exception != null) throw exception;
        if (done) return null;

        StringBuilder text = new StringBuilder();
        MarcRecord currentRecord = null;
        Controlfield currentControlField = null;
        Datafield currentDataField = null;
        Subfield currentSubField = null;
        boolean skip = true;
        
        try {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement startElement = event.asStartElement();
                    String localName = startElement.getName().getLocalPart();
                    boolean ns = namespace == null || startElement.getName().getNamespaceURI().equals(namespace);
                    state += "/" + localName;
                    
                    //System.err.println(startElement.getName().getNamespaceURI() + " - " + startElement.getName().getPrefix() + " - " + startElement.getName().getLocalPart());
                    
                    if (state.startsWith(path) && ns) {
                        // store attributes without respecitve prefix, but with correct namespace
                        Map<String, String> attributes = new HashMap<String, String>();
                        Iterator i = startElement.getAttributes();
                        while (i.hasNext()) {
                            Attribute a = (Attribute)i.next();

                            if (namespace == null || a.getName().getNamespaceURI().equals("") || namespace.equals(a.getName().getNamespaceURI())) {
                                //System.out.println(a.getName().getNamespaceURI() + " - " + a.getName().getPrefix() + " - " + a.getName().getLocalPart() + " = " + a.getValue());
                                attributes.put(a.getName().getLocalPart(), a.getValue());
                            }
                        }
                                                
                        if (localName.equals("record")) {
                            currentRecord = recordBuilder.createMarcRecord();
                        } else if (localName.equals("controlfield")) {
                            currentControlField = recordBuilder.createControlfield(attributes.get("tag"), "");
                        } else if (localName.equals("datafield")) {
                            String ind1 = attributes.get("ind1"), ind2 = attributes.get("ind2");
                            currentDataField = recordBuilder.createDatafield(attributes.get("tag"));

                            if (!ind1.equals("")) {
                                currentDataField.setIndicator(0, ind1.charAt(0));
                            }

                            if (!ind2.equals("")) {
                                currentDataField.setIndicator(1, ind2.charAt(0));
                            }
                        } else if (localName.equals("subfield")) {
                            String code = attributes.get("code");
                            currentSubField = recordBuilder.createSubfield(code.charAt(0), "");
                        }
                        
                        text.setLength(0);
                    }
                } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
                    EndElement endElement = event.asEndElement();
                    String localName = endElement.getName().getLocalPart();
                    boolean ns = namespace == null || endElement.getName().getNamespaceURI().equals(namespace);

                    if (state.startsWith(path) && ns) {
                        if (localName.equals("record")) {
                            return currentRecord;
                        } else if (localName.equals("leader")) {
                            currentRecord.setLeader(text.toString());
                        } else if (localName.equals("controlfield")) {
                            currentControlField.setData(text.toString());
                            currentRecord.addField(currentControlField);
                        } else if (localName.equals("datafield")) {
                            currentRecord.addField(currentDataField);
                        } else if (localName.equals("subfield")) {
                            currentSubField.setData(text.toString().replace('\r', ' ').replace('\n', ' '));
                            currentDataField.addSubfield(currentSubField);
                        }
                    }
                    
                    state = state.substring(0, state.lastIndexOf('/'));
                } else if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                    if (state.startsWith(path)) {
                        text.append(event.asCharacters());
                    }
                }
            }
            
            done = true;
        } catch (Exception e) {
            exception = new IOException(e);
            throw exception;
        }
        
        return null;
    }
    
    @Override
    public void close() {
        closed = true;
        try { eventReader.close(); } catch (XMLStreamException ex) { }
    }

    public static MarcRecord fromXml(String str) throws IOException {
        try {
            byte bytes[] = str.getBytes("UTF-8");
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            MarcXmlRecordReader reader = new MarcXmlRecordReader(bin);

            return reader.readRecord();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
