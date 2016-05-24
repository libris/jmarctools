package se.kb.libris.util.marc.io;

import java.io.*;
import java.lang.Thread.State;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import se.kb.libris.util.marc.*;

public class BufferedMarcRecordReader implements MarcRecordReader {
    private  static int DEFAULT_BUFFER_SIZE = 10;
    
    LinkedBlockingQueue buffer = null;
    Thread readerThread = null;
    Exception exception = null;
    boolean closed = false, done = false;

    public BufferedMarcRecordReader(MarcRecordReader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    public BufferedMarcRecordReader(final MarcRecordReader reader, int bufferSize) {
        this.buffer = new LinkedBlockingQueue(bufferSize);
        
        readerThread = (new Thread() {
           @Override
           public void run() {
                try {
                    for (MarcRecord mr = reader.readRecord(); mr != null; mr = reader.readRecord())
                        addRecord(mr);
                    
                    done = true;
                } catch (IOException e) {
                    exception = e;
                } catch (InterruptedException e) {
                    exception = new IOException("Reader closed prematurely");
               } finally {
                    // add end-of-stream token if possible/needed
                    buffer.offer(new NullToken());
                    reader.close();
                }
           }
        });
    }

    @Override
    public synchronized MarcRecord readRecord() throws IOException {
        if (readerThread.getState().name().equals("NEW")) readerThread.start();
        
        Object ret = null;
        
        // order and checking for isEmpty every time is important!
        if (done && buffer.isEmpty()) return null;
        if (closed && buffer.isEmpty()) throw new IOException("Reader closed prematurely");
        if (exception != null && buffer.isEmpty()) {
            if (exception instanceof IOException) throw (IOException)exception;
            else throw new IOException(exception);
        }
        
        try {
            ret = buffer.take();
            
            // null token only used for breaking blocking call to take()
            if (ret instanceof NullToken) return readRecord();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        return (MarcRecord)ret;
    }

    private void addRecord(MarcRecord record) throws InterruptedException {
        buffer.put(record);
    }

    @Override
    public void close() {
        closed = true;
        // break blocking call to put(...) if needed
        readerThread.interrupt();
    }
    
    public State getReaderThreadState() {
        return readerThread.getState();
    }
    
    public int getBufferSize() {
        return buffer.size();
    }
    
    private class NullToken {
    }
}
