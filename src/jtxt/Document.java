package jtxt;

import java.io.File;

import jtxt.emulator.tui.Layout;

public class Document {
    /**
     * The pages that make up this document. Each page 
     */
    protected Page[] pages;
    
    protected int pageIndex;
    
    /**
     * The underlying file that this {@code Document} represents on the client
     * computer.
     */
    protected File file;
    
    public Document(File file,
                    Layout rootLayout,
                    Page.PageSettings pageSettings) {
        this.file = file;
    }
}
