/*
 * Iso2709Serializer.java
 *
 * Created on den 31 maj 2003, 17:10
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.*;
import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.MarcRecordImpl;

/**
 *
 * @author  Martin Malmsten
 */
public class Iso2709Serializer {
    private static final int MAX_FIELD_LEN = 9999;
    private static final int MAX_RECORD_LEN = 99999;
    public static boolean debug = false;
    
    public static byte[] serialize(MarcRecord record) {
        Vector fields = new Vector();
        byte directory[] = new byte[12*record.getFields().size()], leader[] = record.getLeader().getBytes();
        int recSize = 0, nFields = 0;
                
        // controlfields
        Iterator iter = record.iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            nFields++;
            byte[] tmp;
            
            System.arraycopy(f.getTag().getBytes(), 0, directory, 12*(nFields-1), 3);
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                tmp = cf.getData().getBytes();
            } else {
                tmp = serialize((Datafield) f);
            }
            
            fields.add(tmp);
            recSize += tmp.length;
        }
                
        // size is: leader(24) + directory(12*nFields) + END_OF_FIELD(1) + fields(nFields*(length+1)) + END_OF_RECORD(1)
        int size = 24 + 12*nFields + 1 + recSize + nFields + 1;
        if (size > MAX_RECORD_LEN) {
            return (serialize(dropLongestField(record)));
        }
        
        byte[] rec = new byte[size];

        System.arraycopy(leader, 0, rec, 0, leader.length);
        System.arraycopy(directory, 0, rec, leader.length, directory.length);
        rec[leader.length + directory.length] = MarcRecord.END_OF_FIELD;
        
        int n = 24 + nFields*12 + 1;
        for (int i=0;i<fields.size();i++) {
            byte tmp[] = (byte[])fields.get(i);
            System.arraycopy(tmp, 0, rec, n, tmp.length);
            rec[n + tmp.length] = MarcRecord.END_OF_FIELD;            
            
            rec[24 + 12*i + 3] = (byte)((tmp.length + 1) / 1000 % 10 + '0');
            rec[24 + 12*i + 4] = (byte)((tmp.length + 1) / 100 % 10 + '0');
            rec[24 + 12*i + 5] = (byte)((tmp.length + 1) / 10 % 10 + '0');
            rec[24 + 12*i + 6] = (byte)((tmp.length + 1) % 10 + '0');

            rec[24 + 12*i + 7] = (byte)((n - 24 - 1 - directory.length)/ 10000 % 10 + '0');
            rec[24 + 12*i + 8] = (byte)((n - 24 - 1 - directory.length)/ 1000 % 10 + '0');
            rec[24 + 12*i + 9] = (byte)((n - 24 - 1 - directory.length)/ 100 % 10 + '0');
            rec[24 + 12*i + 10] = (byte)((n - 24 - 1 - directory.length)/ 10 % 10 + '0');
            rec[24 + 12*i + 11] = (byte)((n - 24 - 1 - directory.length) % 10 + '0');
            
            n += tmp.length + 1;
        }
        
        rec[0] = (byte)(rec.length / 10000 % 10 + '0');
        rec[1] = (byte)(rec.length / 1000 % 10 + '0');
        rec[2] = (byte)(rec.length / 100 % 10 + '0');
        rec[3] = (byte)(rec.length / 10 % 10 + '0');
        rec[4] = (byte)(rec.length % 10 + '0');
        
        rec[12] = (byte)((leader.length + 1 + nFields*12) / 10000 % 10 + '0');
        rec[13] = (byte)((leader.length + 1 + nFields*12) / 1000 % 10 + '0');
        rec[14] = (byte)((leader.length + 1 + nFields*12) / 100 % 10 + '0');
        rec[15] = (byte)((leader.length + 1 + nFields*12) / 10 % 10 + '0');
        rec[16] = (byte)((leader.length + 1 + nFields*12) % 10 + '0');

        rec[20] = (byte)'4';
        rec[21] = (byte)'5';
        rec[22] = (byte)'0';
        rec[23] = (byte)'0';

        rec[rec.length - 1] = MarcRecord.END_OF_RECORD;
        
        return rec;
    }
    
    public static byte[] serialize(MarcRecord record, String encoding) throws UnsupportedEncodingException {
        Vector fields = new Vector();
        byte directory[] = new byte[12*record.getFields().size()], leader[] = record.getLeader().getBytes("ISO8859-1");
        int recSize = 0, nFields = 0;
                
        // controlfields
        Iterator iter = record.iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            nFields++;
            byte[] tmp;
            
            System.arraycopy(f.getTag().getBytes("ISO8859-1"), 0, directory, 12*(nFields-1), 3);

            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                tmp = cf.getData().getBytes(encoding);
            } else {
                tmp = serialize((Datafield) f, encoding);
            }
            
            fields.add(tmp);
            recSize += tmp.length;
        }
                
        // size is: leader(24) + directory(12*nFields) + END_OF_FIELD(1) + fields(nFields*(length+1)) + END_OF_RECORD(1)
        int size = 24 + 12*nFields + 1 + recSize + nFields + 1;
        if (size > MAX_RECORD_LEN) {
            return (serialize(dropLongestField(record)));
        }

        byte[] rec = new byte[size];

        System.arraycopy(leader, 0, rec, 0, leader.length);
        System.arraycopy(directory, 0, rec, leader.length, directory.length);
        rec[leader.length + directory.length] = MarcRecord.END_OF_FIELD;
        
        int n = 24 + nFields*12 + 1;
        for (int i=0;i<fields.size();i++) {
            byte tmp[] = (byte[])fields.get(i);
            System.arraycopy(tmp, 0, rec, n, tmp.length);
            rec[n + tmp.length] = MarcRecord.END_OF_FIELD;            
            
            rec[24 + 12*i + 3] = (byte)((tmp.length + 1) / 1000 % 10 + '0');
            rec[24 + 12*i + 4] = (byte)((tmp.length + 1) / 100 % 10 + '0');
            rec[24 + 12*i + 5] = (byte)((tmp.length + 1) / 10 % 10 + '0');
            rec[24 + 12*i + 6] = (byte)((tmp.length + 1) % 10 + '0');

            rec[24 + 12*i + 7] = (byte)((n - 24 - 1 - directory.length)/ 10000 % 10 + '0');
            rec[24 + 12*i + 8] = (byte)((n - 24 - 1 - directory.length)/ 1000 % 10 + '0');
            rec[24 + 12*i + 9] = (byte)((n - 24 - 1 - directory.length)/ 100 % 10 + '0');
            rec[24 + 12*i + 10] = (byte)((n - 24 - 1 - directory.length)/ 10 % 10 + '0');
            rec[24 + 12*i + 11] = (byte)((n - 24 - 1 - directory.length) % 10 + '0');
            
            n += tmp.length + 1;
        }
        
        rec[0] = (byte)(rec.length / 10000 % 10 + '0');
        rec[1] = (byte)(rec.length / 1000 % 10 + '0');
        rec[2] = (byte)(rec.length / 100 % 10 + '0');
        rec[3] = (byte)(rec.length / 10 % 10 + '0');
        rec[4] = (byte)(rec.length % 10 + '0');
        
        if (encoding.equalsIgnoreCase("UTF-8")) {
            rec[9] = (byte)'a';
        } else if (encoding.equalsIgnoreCase("ISO-8859-1") || encoding.equalsIgnoreCase("ISO8859-1")) {
            rec[9] = (byte)'1';
        }
        
        rec[10] = (byte)'2';
        rec[11] = (byte)'2';
        
        rec[12] = (byte)((leader.length + 1 + nFields*12) / 10000 % 10 + '0');
        rec[13] = (byte)((leader.length + 1 + nFields*12) / 1000 % 10 + '0');
        rec[14] = (byte)((leader.length + 1 + nFields*12) / 100 % 10 + '0');
        rec[15] = (byte)((leader.length + 1 + nFields*12) / 10 % 10 + '0');
        rec[16] = (byte)((leader.length + 1 + nFields*12) % 10 + '0');
        
        rec[20] = (byte)'4';
        rec[21] = (byte)'5';
        rec[22] = (byte)'0';
        rec[23] = (byte)'0';

        rec[rec.length - 1] = MarcRecord.END_OF_RECORD;        
        
        return rec;
    }
    
    private static byte[] serialize(Datafield df) {
        try {
            return serialize(df, null);
        } catch (UnsupportedEncodingException cannotHappen) {
            // Never throws unless passed an encoding
            throw new RuntimeException(cannotHappen);
        }
    }
    
    private static byte[] serialize(Datafield df, String encoding) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        sb.append(df.getIndicator(0));
        sb.append(df.getIndicator(1));

        Iterator<Subfield> sfiter = df.iterator();
        while (sfiter.hasNext()) {
            Subfield sf = sfiter.next();

            sb.append((char)MarcRecord.SUBFIELD_MARK);
            sb.append(sf.getCode());
            sb.append(sf.getData());
        }
        
        byte[] bytes = str2bytes(sb.toString(), encoding);

        if (bytes.length > MAX_FIELD_LEN) {
            // (Recursively) cut data of the longest subfield to fit within maximum field length  
            Subfield longest = findLongestSubfield(df);

            String data = longest.getData();
            byte[] b = str2bytes(data, encoding);
            int newLen = Math.max(0, b.length - (bytes.length - MAX_FIELD_LEN + 1));
            longest.setData(bytes2str(Arrays.copyOf(b, newLen), encoding));
            bytes = serialize(df, encoding);
            longest.setData(data);
        }

        return bytes;
    }
    
    private static byte[] str2bytes(String s, String encoding) throws UnsupportedEncodingException {
        return encoding != null ? s.getBytes(encoding) : s.getBytes();
    }

    private static String bytes2str(byte[] b, String encoding) throws UnsupportedEncodingException {
        try {
            Charset charset = encoding != null ? Charset.forName(encoding) : Charset.defaultCharset();
            CharsetDecoder decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
            CharBuffer c = decoder.decode(ByteBuffer.wrap(b));
            return new String(c.array(), 0, c.length());
        } catch (CharacterCodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }
    
    private static Subfield findLongestSubfield(Datafield df) {
        int maxLen = -1;
        Subfield longest = null;
        Iterator<Subfield> sfiter = df.iterator();
        while (sfiter.hasNext()) {
            Subfield sf = sfiter.next();
            if (sf.getData() != null && sf.getData().length() > maxLen) {
                maxLen = sf.getData().length();
                longest = sf;
            }
        }
        return longest;
    }

    private static MarcRecord dropLongestField(MarcRecord record) {
        MarcRecordImpl copy = new MarcRecordImpl();
        copy.setLeader(record.getLeader());

        Iterator<Field> i = record.iterator();
        int maxLen = -1;
        Datafield longest = null;
        while (i.hasNext()) {
            Field f = i.next();
            if (f instanceof Datafield) {
                int len = serialize((Datafield) f).length;
                if (len > maxLen) {
                    maxLen = len;
                    longest = (Datafield) f;
                }
            } 
        }

        i = record.iterator();
        while (i.hasNext()) {
            Field f = i.next();
            if (f != longest) {
                copy.addField(f);
            }
        }
        return copy;
    }
}
