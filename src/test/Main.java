package test;

import jtxt.emulator.Configuration;
import jtxt.emulator.Location;
import jtxt.emulator.Terminal;

public class Main {
    public static void main(String[] args) {
        Configuration config = new Configuration("Terminal",
                                                 80, 20,
                                                 "Consolas", 12);
        Terminal term = new Terminal(config);
        
        term.putLine("This text demonstrates the wrapping features of the " +
                     "terminal. Text can be easily wrapped based on " +
                     "location and a boundary.", 
                     new Location(0, 20), 
                     60);
        
        term.putLine("Hello, world!");
        
        // Is there a printf like function?
        // Use String.format(...) and pass as the argument to putLine(...)
        
        int age = Integer.parseInt(term.requestInput("Age:"));
        term.cursor.setLocation(10, 0);
        String msg = String.format("Your look %d.", age + 10);
        term.putLine(msg);
    }
}
