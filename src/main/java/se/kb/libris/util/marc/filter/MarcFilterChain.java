package se.kb.libris.util.marc.filter;

import java.util.*;
import se.kb.libris.util.marc.*;

public class MarcFilterChain {
    Vector filters = new Vector();
    
    public MarcFilterChain() {
    }
    
    public void addFilter(MarcFilter filter) {
        filters.add(filter);
    }

    public se.kb.libris.util.marc.MarcRecord doFilter(se.kb.libris.util.marc.MarcRecord rec) {
        MarcRecord record = rec;
        
        for (int i=0;i<filters.size();i++) {
            MarcFilter filter = (MarcFilter)filters.get(i);
            record = filter.doFilter(record);
            
            if (record == null) {
                return null;
            } 
        }
        
        return record;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for (int i=0;i<filters.size();i++) {
            sb.append(filters.get(i) + "\n");        
        }
        
        return sb.toString();
    }
}
