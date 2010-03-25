package se.kb.libris.util.marc.filter;

import java.io.*;

import se.kb.libris.util.marc.MarcRecord;
import se.kb.libris.util.marc.io.*;

abstract public class MarcFilter {
    public static final MarcRecord filter(MarcRecord rec) {
        MarcRecord ret = doFilter(rec);
                
        return ret;
    }
        
    public static MarcRecord doFilter(MarcRecord rec) {
        return rec;
    }
    
    public static void main(String args[]) throws Exception {
        if (args.length == 1 && args[0].equals("-h")) {
            System.out.println("usage: java <filterclass> [options]");
            System.out.println("  options are:");
            System.out.println("    -inFile=<filename>, default = System.in");
            System.out.println("    -outFile=<filename>, default = System.out");
            System.out.println("    -inType=[ISO2709|XML], default = ISO2709");
            System.out.println("    -outType=[ISO2709|XML|TEXT], default = ISO2709");
            System.out.println("    -inEncoding=<encoding> default = system specific");
            System.out.println("    -outEncoding=<encoding>, default = system specific");
            System.out.println("    -recordTag=<tag>, default = '/collection/record'");
            System.out.println("    -recordNamespace=<name>");
            System.out.println();
            
            return;
        }
        
        InputStream in = System.in;
        OutputStream out = System.out;
        String inEncoding = null, outEncoding = null, inType = "ISO2709", outType = "ISO2709", recordTag = "/collection/record", namespace = null;
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
            }
        }
        
        if (inType.equals("ISO2709")) {
            if (inEncoding == null) {
                reader = new Iso2709MarcRecordReader(in);
            } else {
                reader = new Iso2709MarcRecordReader(in, inEncoding);
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
            record = filter(record);
            
            if (record != null) {
                nPassed++;
                writer.writeRecord(record);
            } else {
                nFiltered++;
            }            
        }
        
        writer.close();
        
        System.err.println("info: passed=" + nPassed + ", filtered=" + nFiltered);
        
        out.flush();
    }
}
