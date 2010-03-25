package se.kb.libris.util.marc.io;

import java.io.*;

public class MarcSplit {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: java [java-options] se.kb.libris.util.marc.io.MarcSplit <n> <out file>");
            System.exit(1);
        }
        
        FileOutputStream out = null;
        StrictIso2709Reader reader = new StrictIso2709Reader(System.in);
        byte record[] = null;
        int n = Integer.parseInt(args[0]), i = 0;
        
        
        while ((record = reader.readIso2709()) != null) {
            if (i++ % n == 0) {
                if (out != null) {
                    out.close();
                } 
                
                out = new FileOutputStream(args[1] + "." + (i / n));
            }
            
            out.write(record);
        }
    }    
}
