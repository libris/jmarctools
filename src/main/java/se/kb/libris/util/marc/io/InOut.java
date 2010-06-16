package se.kb.libris.util.marc.io;

import java.io.*;

import se.kb.libris.util.marc.MarcRecord;
import se.kb.libris.util.marc.io.*;

abstract public class InOut {
    public static void main(String args[]) throws Exception {
        if (args.length == 1 && args[0].equals("-h")) {
            System.out.println("usage: java se.kb.libris.util.marc.io.InOut [options]");
            System.out.println("  options are:");
            System.out.println("    -inFile=<filename>, default = System.in");
            System.out.println("    -outFile=<filename>, default = System.out");
            System.out.println("    -inType=[ISO2709|XML], default = ISO2709");
            System.out.println("    -outType=[ISO2709|XML|TEXT], default = ISO2709");
            System.out.println("    -inEncoding=<encoding> default = system specific");
            System.out.println("    -outEncoding=<encoding>, default = UTF-8");
            System.out.println("    -recordTag=<tag>, default = '/collection/record'");
            System.out.println("    -recordNamespace=<name>");
            System.out.println("    -trustLength=[true|false]");
            System.out.println("    -trustDirectory=[true|false]");
            System.out.println();
            
            return;
        }
        
        InputStream in = System.in;
        OutputStream out = System.out;
        String inEncoding = null, outEncoding = "UTF-8", inType = "ISO2709", outType = "ISO2709", recordTag = "/collection/record", namespace = null;
        boolean trust_length = false, trust_directory = false;
        MarcRecordReader reader = null;
        MarcRecordWriter writer = null;
        
        for (int i=0;i<args.length;i++) {
            String arg = args[i], name = arg.substring(1, arg.indexOf('=')), value = arg.substring(arg.indexOf('=')+1);
            
            if (name.equals("inFile")) {
                in = new FileInputStream(value);
            } else if (name.equals("outFile")) {
                out = new FileOutputStream(value);
            } else if (name.equals("inType")) { 
                inType = value;
            } else if (name.equals("outType")) {
                outType = value;
            } else if (name.equals("inEncoding")) {
                inEncoding = value;
            } else if (name.equals("outEncoding")) {
                outEncoding = value;
            } else if (name.equals("recordTag")) {
                recordTag = value;
            } else if (name.equals("namespace")) {
                namespace = value;
            } else if (name.equals("trustLength")) {
                trust_length = Boolean.valueOf(value);
            } else if (name.equals("trustDirectory")) {
                trust_directory = Boolean.valueOf(value);
            }
        }
        
        if (inType.equals("ISO2709")) {
            if (inEncoding == null) {
                reader = new Iso2709MarcRecordReader(in, trust_length, trust_directory);
            } else {
                reader = new Iso2709MarcRecordReader(in, trust_length, trust_directory, inEncoding);
            }
        } else if (inType.equals("XML")) {
            if (namespace == null) {
                reader = new MarcXmlRecordReader(in, recordTag);
            } else {
                reader = new MarcXmlRecordReader(in, recordTag, namespace);
            }
        }
        
        if (outType.equals("ISO2709")) {
            if (outEncoding == null) {
                writer = new Iso2709MarcRecordWriter(out);
            } else {
                writer = new Iso2709MarcRecordWriter(out, outEncoding);
            }
        } else if (outType.equals("XML")) {
            if (outEncoding == null) {
                writer = new MarcXmlRecordWriter(out);
            } else {
                writer = new MarcXmlRecordWriter(out, outEncoding);
            }
        } else if (outType.equals("TEXT")) {
            writer = new TextMarcRecordWriter(out);
        }
        
        MarcRecord record = null;
        
        int nPassed = 0, nFiltered = 0;
        while ((record = reader.readRecord()) != null) {
            if (outEncoding != null) {
                if (outEncoding.equalsIgnoreCase("MARC8") || outEncoding.equalsIgnoreCase("VRLIN")) {
                    record.setLeader(9, 'a');
                } if (outEncoding.equalsIgnoreCase("UTF8") || outEncoding.equalsIgnoreCase("UTF-8")) {                    
                    record.setLeader(9, ' ');
                } if (outEncoding.equalsIgnoreCase("ISO-8859-1") || outEncoding.equalsIgnoreCase("ISO8859-1") || outEncoding.equalsIgnoreCase("Latin1Strip")) {
                    record.setLeader(9, '1');                    
                }
            }

            writer.writeRecord(record);
        }
        
        writer.close();
        
        out.flush();
    }
}
