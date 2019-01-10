package jtxt;

import jtxt.emulator.Location;
import jtxt.emulator.Region;
import jtxt.emulator.tui.Container;
import jtxt.emulator.tui.GridLayout;
import jtxt.emulator.tui.RootContainer;

class Page extends GlyphBuffer {
    protected PageSettings settings;
    
    /**
     * The container which manages all components within this page.
     */
    protected Container container;
    
    public static enum Size {
        SMALL(80, 35),
        MEDIUM(100, 45),
        LARGE(120, 50);
        
        int width,
            height;
        
        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    public static class PageSettings {
        protected final int width,
                            height;

        /**
         * The amount of padding around each of the edges of the page. The
         * bounds in which {@code Component}s can be placed is shrunk according
         * to these parameters.
         */
        protected int marginTop,
                      marginBottom,
                      marginRight,
                      marginLeft;
        
        public PageSettings(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public PageSettings margin(int marginTop, 
                                   int marginBottom,
                                   int marginLeft,
                                   int marginRight) {
            this.marginTop = marginTop;
            this.marginBottom = marginBottom;
            this.marginLeft = marginLeft;
            this.marginRight = marginRight;
            
            return this;
        }
        
        public PageSettings margin(int topBottom, int leftRight) {
            return margin(topBottom,
                          leftRight,
                          topBottom,
                          leftRight);
        }
        
        public PageSettings margin(int numChars) {
            return margin(numChars, numChars);
        }
        
        /**
         * Gets the amount of usable space from the top of the page to the
         * bottom of the page.
         * 
         * @return The height of this page.
         */
        public int getHeight() {
            return height - (marginTop + marginBottom);
        }
        
        /**
         * Gets the amount of usable space from the left of this page to the
         * right of this page.
         * 
         * @return The width of this page.
         */
        public int getWidth() {
            return width - (marginLeft + marginRight);
        }
    }
    
    public Page(PageSettings settings) {
        this.settings = settings;
        
        Region bounds = Region.fromLocation(new Location(settings.marginTop,
                                                         settings.marginLeft),
                                            settings.getWidth(),
                                            settings.getHeight());
        container = new RootContainer(bounds, new GridLayout(new int[] { }));
    }
    
    public Region getBounds() {
        return container.getBounds();
    }
}
