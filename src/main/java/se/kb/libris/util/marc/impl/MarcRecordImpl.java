package se.kb.libris.util.marc.impl;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import se.kb.libris.util.marc.*;

public class MarcRecordImpl implements MarcRecord {
    public Map<String, String> properties = null;
    char leader[] = new char[24];
    List fields = new LinkedList();
    
    /** Creates a new instance of MarcRecordImpl */
    public MarcRecordImpl() {
    }
    
    public void addField(Field f) {
        fields.add(f);
    }
    
    public void addField(Field f, Comparator c) {
        if (fields.isEmpty()) {
            fields.add(f);
        } else {
            ListIterator i = fields.listIterator();
            
            while (i.hasNext()) {
                Field f2 = (Field)i.next();
                int n = c.compare(f, f2);
                
                if (n < 0) {
                    i.previous();
                    i.add(f);
                    
                    return;
                }
            }
            
            fields.add(f);
        }
    }
    
    public List getFields() {
        return fields;
    }
    
    public Iterator iterator() {
        return fields.iterator();
    }
    
    public ListIterator listIterator() {
        return fields.listIterator();
    }
    
    public String getLeader() {
        return new String(leader);
    }
    
    public char getLeader(int idx) {
        return leader[idx];
    }
    
    public void setLeader(String leader) {
        this.leader = leader.toCharArray();
    }
    
    public void setLeader(int i, char c) {
        leader[i] = c;
    }
    
    public Controlfield createControlfield(String tag, String data) {
        return new ControlfieldImpl(tag, data);
        
    }
    
    public Datafield createDatafield(String tag) {
        return new DatafieldImpl(tag);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("leader: " + getLeader() + "\n");
        
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (f instanceof Controlfield) {
                Controlfield cf = (Controlfield)f;
                sb.append(cf.getTag() + " " + cf.getData() + "#\n");
            } else {
                Datafield df = (Datafield)f;
                
                sb.append(df.getTag() + " '" + df.getIndicator(0) + "' '" + df.getIndicator(1) + "'");
                
                Iterator sfiter = df.iterator();
                while (sfiter.hasNext()) {
                    Subfield sf = (Subfield)sfiter.next();
                    sb.append("$" + sf.getCode() + sf.getData().replace('\r', ' ').replace('\n', ' '));
                }
                
                sb.append("#\n");
            }
        }
        
        return sb.toString();
    }
    
    public int getType() {
        char c = getLeader(6);
        if (c == 'z') {
            return AUTHORITY;
        } else if (c == 'u' ||
                   c == 'v' ||
                   c == 'x' ||
                   c == 'y') {
            return HOLDINGS;
        } else if (c == 'a' ||
                   c == 'c' ||
                   c == 'd' ||
                   c == 'e' ||
                   c == 'f' ||
                   c == 'g' ||
                   c == 'i' ||
                   c == 'j' ||
                   c == 'k' ||
                   c == 'm' ||
                   c == 'o' ||
                   c == 'p' ||
                   c == 'r' ||
                   c == 't' ) {
            return BIBLIOGRAPHIC;
        } else if (c == 'w') {
            return CLASSIFICATION;
        } else if (c == 'q') {
            return COMMUNITY;
        }
        
        return UNKNOWN;
    }
    
    public List getFields(String regexp) {
        LinkedList list = new LinkedList();
        
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (Pattern.matches(regexp, f.getTag())) {
                list.add(f);
            }
        }
        
        return list;
    }
    
    public List getFields(Pattern pattern) {
        LinkedList list = new LinkedList();
        
        Iterator iter = iterator();
        while (iter.hasNext()) {
            Field f = (Field)iter.next();
            
            if (pattern.matcher(f.getTag()).find()) {
                list.add(f);
            }
        }
            
        return list;
    }
    
    public Iterator iterator(String regexp) {
        return getFields(regexp).iterator();
    }
    
    public Iterator iterator(Pattern pattern) {
        return getFields(pattern).iterator();
    }
    
    public ListIterator listIterator(String regexp) {
        return getFields(regexp).listIterator();
    }
    
    public ListIterator listIterator(Pattern pattern) {
        return getFields().listIterator();
    }
    
    public void setProperty(String property, String value) {
        if (properties == null) properties = new HashMap<String, String>();
        
        properties.put(property, value);
    }
    
    public String getProperty(String property) {
        if (properties == null) return null;
        else return properties.get(property);
    }
    
    public String getProperty(String property, String def) {
        if (properties == null) return def;
        else if (properties.containsKey(property)) return properties.get(property);
        else return def;
    }
    
    public List<Datafield> grep(String str) {
        // example 100a,b,c,w=g;h
        Map<String, Set<String>> filter = new HashMap<String, Set<String>>();
        String tag = str.substring(0,3), subfields[]=str.substring(3).split(",");
        Set<String> set = new HashSet<String>();

        for (String subfield: subfields) {
            Set<String> hs = new HashSet<String>();

            if (subfield.length() > 1 && subfield.charAt(1) == '=') {
                for (String value: subfield.substring(2).split(";")) {
                    hs.add(value);
                }

                filter.put(tag + subfield.charAt(0), hs);
            } else {
                hs.add("*");
                filter.put(tag + subfield.charAt(0), hs);
            }


            set.add(tag + subfield.charAt(0));
        }

        List<Datafield> ret = new LinkedList<Datafield>();
        Iterator fiter = iterator(tag);

        while (fiter.hasNext()) {
            Datafield df = (Datafield)fiter.next();
            Set<String> set2 = new HashSet<String>(set);

            Iterator sfiter = df.iterator();

            while (sfiter.hasNext()) {
                Subfield sf = (Subfield)sfiter.next();
                Set<String> set3 = filter.get(df.getTag() + sf.getCode());

                if (set3 != null && (set3.contains("*") || set3.contains(sf.getData())))
                    set2.remove(df.getTag() + sf.getCode());
            }

            if (set2.size() == 0)
                ret.add(df);
        }

        return ret;
    }
}
