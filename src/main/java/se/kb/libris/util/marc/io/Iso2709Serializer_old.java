/*
 * Iso2709Serializer.java
 *
 * Created on den 31 maj 2003, 17:10
 */

package se.kb.libris.util.marc.io;

import java.io.*;
import java.util.*;
import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class Iso2709Serializer_old {
    public static byte[] serialize(MarcRecord record) {
        Vector fields = new Vector();
        byte directory[] = new byte[12*record.getFields().size()], leader[] = record.getLeader().getBytes();
        int recSize = 0, nFields = 0;
                
        // controlfields
        Iterator iter = record.iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            nFields++;
            byte tmp[] = null;
            
            System.arraycopy(f.getTag().getBytes(), 0, directory, 12*(nFields-1), 3);
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                tmp = cf.getData().getBytes();
            } else {
                StringBuffer sb = new StringBuffer();
                Datafield df = (Datafield)f;

                sb.append(df.getIndicator(0));
                sb.append(df.getIndicator(1));

                Iterator sfiter = df.iterator();
                while (sfiter.hasNext()) {
                    Subfield sf = (Subfield)sfiter.next();

                    sb.append((char)MarcRecord.SUBFIELD_MARK);
                    sb.append(sf.getCode());
                    sb.append(sf.getData());
                }

                //System.out.println(sb);
                
                tmp = sb.toString().getBytes();
            }
            
            fields.add(tmp);
            recSize += tmp.length;            
        }
                
        // size is: leader(24) + directory(12*nFields) + END_OF_FIELD(1) + fields(nFields*(length+1)) + END_OF_RECORD(1)
        
        byte rec[] = new byte[24 + 12*nFields + 1 + recSize + nFields + 1];

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
        
        rec[rec.length - 1] = MarcRecord.END_OF_RECORD;
        
        return rec;
    }

    public static byte[] serialize(MarcRecord record, String encoding) throws UnsupportedEncodingException {
        Vector fields = new Vector();
        byte directory[] = new byte[12*record.getFields().size()], leader[] = record.getLeader().getBytes(encoding);
        int recSize = 0, nFields = 0;
                
        // controlfields
        Iterator iter = record.iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            nFields++;
            byte tmp[] = null;
            
            System.arraycopy(f.getTag().getBytes(encoding), 0, directory, 12*(nFields-1), 3);
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                tmp = cf.getData().getBytes(encoding);
            } else {
                StringBuffer sb = new StringBuffer();
                Datafield df = (Datafield)f;

                sb.append(df.getIndicator(0));
                sb.append(df.getIndicator(1));

                Iterator sfiter = df.iterator();
                while (sfiter.hasNext()) {
                    Subfield sf = (Subfield)sfiter.next();

                    sb.append((char)MarcRecord.SUBFIELD_MARK);
                    sb.append(sf.getCode());
                    sb.append(sf.getData());
                }

                //System.out.println(sb);
                
                tmp = sb.toString().getBytes(encoding);
            }
            
            fields.add(tmp);
            recSize += tmp.length;            
        }
                
        // size is: leader(24) + directory(12*nFields) + END_OF_FIELD(1) + fields(nFields*(length+1)) + END_OF_RECORD(1)
        
        byte rec[] = new byte[24 + 12*nFields + 1 + recSize + nFields + 1];

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
        
//        if (encoding.equals("UTF-8")) {
//            rec[9] = (byte)'a';
//        }
        
//        rec[10] = (byte)'2';
//        rec[11] = (byte)'2';
        
        rec[12] = (byte)((leader.length + 1 + nFields*12) / 10000 % 10 + '0');
        rec[13] = (byte)((leader.length + 1 + nFields*12) / 1000 % 10 + '0');
        rec[14] = (byte)((leader.length + 1 + nFields*12) / 100 % 10 + '0');
        rec[15] = (byte)((leader.length + 1 + nFields*12) / 10 % 10 + '0');
        rec[16] = (byte)((leader.length + 1 + nFields*12) % 10 + '0');
        
//        rec[20] = (byte)'4';
//        rec[21] = (byte)'5';
//        rec[22] = (byte)'0';
//        rec[23] = (byte)'0';

        rec[rec.length - 1] = MarcRecord.END_OF_RECORD;        
        
        return rec;
    }
}
