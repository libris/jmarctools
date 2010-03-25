package se.kb.libris.util.marc.io;

import java.util.*;
import se.kb.libris.util.marc.*;
import se.kb.libris.util.marc.impl.MarcRecordImpl;

public class MarcGrep {
    public static List<Datafield> grep(MarcRecord record, String str) {
        // example 100a,b,c,w=g;h
        Map<String, Set<String>> filter = new HashMap<String, Set<String>>();
        String tag = str.substring(0,3), subfields[]=str.substring(3).split(",");
        Set<String> set = new HashSet<String>();

        for (String subfield: subfields) {
            System.out.println(subfield);
            Set<String> hs = new HashSet<String>();

            if (subfield.length() > 1 && subfield.charAt(1) == '=') {
                for (String value: subfield.substring(2).split(";")) {
                    System.out.println(value);
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
        Iterator fiter = record.iterator(tag);
        
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

            if (set2.size() == 0) {
                ret.add(df);
            }
        }
        
        return ret;
    }

    public static void main(String args[]) {
        MarcRecord mr = new MarcRecordImpl();
        mr.addField(mr.createControlfield("001", "bla bla bla"));
        mr.addField(mr.createDatafield("550").addSubfield('a', "abc").addSubfield('w', "g"));
        mr.addField(mr.createDatafield("550").addSubfield('a', "def").addSubfield('w', "h"));

        System.out.println(mr.grep("550a,w=g;h"));
    }
}
