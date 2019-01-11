package jtxt;

import java.io.File;

public class Document {
    /**
     * The pages that make up this document. Each page 
     */
    protected Page[] pages;
    
    /**
     * The underlying file that this {@code Document} represents on the client
     * computer.
     */
    protected File file;
    
    public Document(File file) {
        this.file = file;
    }
    
    
}
