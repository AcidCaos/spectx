package com.sxlic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class SequentialProperties
extends Properties {
    private final Set<Object> keySet = new LinkedHashSet<Object>();

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(this.keySet);
    }

    @Override
    public synchronized Object put(Object object, Object object2) {
        this.keySet.add(object);
        return super.put(object, object2);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(this.keySet);
    }

    @Override
    public synchronized void load(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        this.load(inputStreamReader);
    }

    @Override
    public void store(OutputStream outputStream, String string) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        this.store(outputStreamWriter, string);
    }
    
    public void print() {
        Iterator<Object> keysIterator = this.keySet().iterator();
        System.out.println();
        while(keysIterator.hasNext()) {
            String key = (String) keysIterator.next();
            System.out.println(key + "=" + this.getProperty(key));
        }
        System.out.println();
    }
}
