/*
 * DomSerializer.java
 *
 * Created on den 31 maj 2003, 17:25
 */

package se.kb.libris.util.marc.io;

import java.util.*;
import org.w3c.dom.*;
import se.kb.libris.util.marc.*;

/**
 *
 * @author  Martin Malmsten
 */
public class DomSerializer {
    public static DocumentFragment serialize(MarcRecord record, Document doc) {
        DocumentFragment ret = doc.createDocumentFragment();
        //Element rec = doc.createElement("record");
        Element rec = doc.createElementNS("http://www.loc.gov/MARC21/slim", "record");
        rec.setAttribute("xmlns", "http://www.loc.gov/MARC21/slim");
        //rec.setPrefix("marc");        
        
        // leader
        //Element leader = doc.createElement("leader");
        Element leader = doc.createElementNS("http://www.loc.gov/MARC21/slim", "leader");
        //leader.setPrefix("marc");
        leader.appendChild(doc.createTextNode(record.getLeader()));
        
        if (record.getLeader(6) == 'z') {
            rec.setAttribute("type", "Authority");
        } else if (record.getLeader(6) == 'u' ||
                   record.getLeader(6) == 'v' ||
                   record.getLeader(6) == 'x' ||
                   record.getLeader(6) == 'y') {
            rec.setAttribute("type", "Holdings");
        } else if (record.getLeader(6) == 'a' ||
                   record.getLeader(6) == 'c' ||
                   record.getLeader(6) == 'd' ||
                   record.getLeader(6) == 'e' ||
                   record.getLeader(6) == 'f' ||
                   record.getLeader(6) == 'g' ||
                   record.getLeader(6) == 'i' ||
                   record.getLeader(6) == 'j' ||
                   record.getLeader(6) == 'k' ||
                   record.getLeader(6) == 'm' ||
                   record.getLeader(6) == 'o' ||
                   record.getLeader(6) == 'p' ||
                   record.getLeader(6) == 'r' ||
                   record.getLeader(6) == 't' ) {
            rec.setAttribute("type", "Bibliographic");
        } else if (record.getLeader(6) == 'w') {
            rec.setAttribute("type", "Classification");
        } else if (record.getLeader(6) == 'q') {
            rec.setAttribute("type", "Community");
        }
        
        rec.appendChild(leader);
        
        Iterator iter = record.iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                //Element cfield = doc.createElement("controlfield");
                Element cfield = doc.createElementNS("http://www.loc.gov/MARC21/slim", "controlfield");
                //cfield.setPrefix("marc");
                cfield.setAttribute("tag", cf.getTag());
                cfield.appendChild(doc.createTextNode(cf.getData()));
                rec.appendChild(cfield);
            } else {
                Datafield df = (Datafield)f;
                //Element dfield = doc.createElement("datafield");
                Element dfield = doc.createElementNS("http://www.loc.gov/MARC21/slim", "datafield");
                //dfield.setPrefix("marc");
                dfield.setAttribute("tag", df.getTag());
                dfield.setAttribute("ind1", String.valueOf(df.getIndicator(0)));
                dfield.setAttribute("ind2", String.valueOf(df.getIndicator(1)));
            
                Iterator sfiter = df.iterator();
                while (sfiter.hasNext()) {
                    Subfield sf = (Subfield)sfiter.next();
                    //Element sfield = doc.createElement("subfield");
                    Element sfield = doc.createElementNS("http://www.loc.gov/MARC21/slim", "subfield");
                    //sfield.setPrefix("marc");
                    sfield.setAttribute("code", String.valueOf(sf.getCode()));
                    char x[] = sf.getData().toCharArray();
                    
                    for (int i=0;i<x.length;i++) {
                        if (x[i] < 0x0020) {
                            x[i] = '?';
                        }
                    }
                    
                    sfield.appendChild(doc.createTextNode(new String(x)));
                    dfield.appendChild(sfield);
                }

                rec.appendChild(dfield);
            }
        }
        
        ret.appendChild(rec);
        
        return ret;
    }

    public static StringBuffer serialize(MarcRecord mr) {
        return serialize(mr, new StringBuffer());
    }
    
    public static StringBuffer serialize(MarcRecord mr, StringBuffer sb) {
        String type = "";
        
        if (mr.getLeader(6) == 'z') {
            type =  "Authority";
        } else if (mr.getLeader(6) == 'u' ||
                   mr.getLeader(6) == 'v' ||
                   mr.getLeader(6) == 'x' ||
                   mr.getLeader(6) == 'y') {
            type = "Holdings";
        } else if (mr.getLeader(6) == 'a' ||
                   mr.getLeader(6) == 'c' ||
                   mr.getLeader(6) == 'd' ||
                   mr.getLeader(6) == 'e' ||
                   mr.getLeader(6) == 'f' ||
                   mr.getLeader(6) == 'g' ||
                   mr.getLeader(6) == 'i' ||
                   mr.getLeader(6) == 'j' ||
                   mr.getLeader(6) == 'k' ||
                   mr.getLeader(6) == 'm' ||
                   mr.getLeader(6) == 'o' ||
                   mr.getLeader(6) == 'p' ||
                   mr.getLeader(6) == 'r' ||
                   mr.getLeader(6) == 't' ) {
            type = "Bibliographic";
        } else if (mr.getLeader(6) == 'w') {
            type = "Classification";
        } else if (mr.getLeader(6) == 'q') {
            type = "Community";
        }
        
        sb.append("<record type=\"" + type + "\" xmlns=\"http://www.loc.gov/MARC21/slim\">");
        sb.append("<leader>" + mr.getLeader() + "</leader>");
        for (Field f: (List<Field>)mr.getFields()) {
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                
                sb.append("<controlfield tag=\"" + cf.getTag() + "\">");
                printXml(sb, cf.getData());
                sb.append("</controlfield>");
            } else if (f instanceof Datafield) {
                Datafield df = (Datafield)f;

                sb.append("<datafield tag=\"" + df.getTag() + "\" ind1=\"" + df.getIndicator(0) + "\" ind2=\"" + df.getIndicator(1) + "\">");

                for (Subfield sf: (List<Subfield>)df.getSubfields()) {
                    if (Character.isLetterOrDigit(sf.getCode())) {
                        sb.append("<subfield code=\"" + sf.getCode() + "\">");
                        printXml(sb, sf.getData());
                        sb.append("</subfield>");
                    }
                }
                
                sb.append("</datafield>");
            }
        }
            
        sb.append("</record>");

        for (int i=0;i<sb.length();i++)
            if (sb.charAt(i) < 0x09 || (sb.charAt(i) > 0x0D && sb.charAt(i) < 0x1F))
                sb.setCharAt(i, '?');

        return sb;
    }

    private static void printXml(StringBuffer sb, CharSequence data) {
        for (int i=0;i<data.length();i++) {
            char c = data.charAt(i);

            if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\'') {
                sb.append("&apos;");
            } else if (c == '"') {
                sb.append("&quot;");
            } else if (c == 0x09 || c == 0x0A || c == 0x0D || (c >= 0x20 && c <= 0xd7ff) || (c <= 0x10000 && c <= 0x10ffff)){
                sb.append(c);
            } else {
                sb.append('?');
            }
        }
    }    
}
