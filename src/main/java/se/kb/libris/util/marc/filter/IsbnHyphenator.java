package se.kb.libris.util.marc.filter;

import java.io.*;
import java.util.*;
import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.io.*;

public class IsbnHyphenator {
    static HashMap countries = null;
    
    public static final MarcRecord filter(MarcRecord rec) {
        MarcRecord ret = doFilter(rec);
                
        return ret;
    }
        
    public static MarcRecord doFilter(MarcRecord rec) {
        if (countries == null) {
            try {
                countries = new HashMap();

                BufferedReader reader = new BufferedReader(new InputStreamReader(Class.class.getResourceAsStream("/se/kb/libris/util/marc/filter/isbn.ranges.txt")));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    if (line.equals("") || line.charAt(0) == '#') {
                        continue;
                    }

                    String country = line.substring(0, line.indexOf('-')), start = line.substring(line.indexOf('-')+1, line.indexOf('.')), stop = line.substring(line.indexOf('.')+1);

                    Vector v = (Vector)countries.get(country);

                    if (v == null) {
                        v = new Vector();
                        countries.put(country, v);
                    }

                    v.add(start);
                    v.add(stop);

                    //System.out.println(country + " " + start + " " + stop);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        
        Iterator iter = rec.getFields("020").iterator();
        while (iter.hasNext()) {
            Iterator siter = ((Datafield)iter.next()).iterator("a");
            
            while (siter.hasNext()) {
                Subfield sf = (Subfield)siter.next();
                
                String data = sf.getData(), isbn = "";
                
                for (int i=0;i<data.length();i++) {
                    char c = data.charAt(i);
                    
                    if (Character.isDigit(c) || c == 'x' || c == 'X') isbn += c;
                    
                    if (isbn.length() == 10) {
                        sf.setData(hyphenate(isbn) + data.substring(i+1));
                        break;
                    }
                }                
            }
        }
        
        return rec;
    }    
    
    public static char checksum(String isbn) {
        int n = 0, weight = 1;
        
        for (int i=0;i<isbn.length()-1;i++) {
            if (isbn.charAt(i) == '-') {
                continue;
            }
            
            n += Integer.parseInt("" + isbn.charAt(i))*weight++;
        }
        
        n = n % 11;
        
        return (n==10)? 'X' : (char)(n + '0');
    }
    
    public static String scrub(String data, String scrub) {
        StringBuffer sb = new StringBuffer();
        
        for (int i=0;i<data.length();i++) {
            char c = data.charAt(i);
            
            if (scrub.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
    public static String purge(String data, String remain) {
        StringBuffer sb = new StringBuffer();
        
        for (int i=0;i<data.length();i++) {
            char c = data.charAt(i);
            
            if (remain.indexOf(c) != -1) {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }        
    
    public static String hyphenate(String isbn) {
        String isbn2 = scrub(isbn, "-");
        String country = null;
        
        // 1) find country
        for (int i=1;i<isbn2.length();i++) {
            if (countries.get(isbn2.substring(0,i)) != null) {
                country = isbn2.substring(0,i);
            }
        }
        
        if (country == null) {
            return isbn;
        }
        
        // 2) find publishers number
        String pprefix = "", num = "", tmp = isbn2.substring(country.length(), isbn2.length()-1);
        //System.err.println(tmp);
        
        Vector v = (Vector)countries.get(country);
        for (int i=0;i<v.size();i += 2) {
            int length = ((String)v.get(i)).length(), start = Integer.parseInt((String)v.get(i)), stop = Integer.parseInt((String)v.get(i+1)), n = Integer.parseInt(tmp.substring(0,length));
            
            if (n >= start && n <= stop) {
                pprefix = tmp.substring(0, length);
                break;
            }
        }

        num = tmp.substring(pprefix.length(), tmp.length());
        
        return country + "-" + pprefix + "-" + num + "-" + checksum(isbn2);
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
            if (outEncoding == null) {
                writer = new TextMarcRecordWriter(out);
            } else {
                writer = new TextMarcRecordWriter(out, outEncoding);
            }            
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
