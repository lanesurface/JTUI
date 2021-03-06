package jtxt.emulator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads properties of an initialization (ini) file. Initialization files are
 * used to store runtime properties of the program, such as fonts, text-size,
 * emulator dimensions, and so on. See the documentation for details about the
 * specifics for creating an initialization file for the terminal and 
 * associated utilities. You can learn more about the initialization file
 * format <a href="https://en.wikipedia.org/wiki/INI_file">here</a>.
 */
public class InitializationReader {
    /**
     * A map of key-value pairs defined in the initialization file. The 
     * properties of this map must retain the order in which they are defined
     * within the initialization file. This is to aid in the iteration of these
     * values.
     */
    private Map<String, String> properties;
    
    public InitializationReader(String fileName) {
        properties = new HashMap<>();
        
        try {
            Path path = Paths.get(fileName);
            Files.readAllLines(path)
                 .forEach(this::parse);
        }
        catch (IOException ie) {
            System.err.println("Could not find the file " + fileName);
            return;
        }
    }
    
    private void parse(String line) {
        switch (line.charAt(0)) {
        case ';':
            // Discard lines starting with ";" as it denotes a comment.
            return;
        case '[':
            break;
        default:
            int assignmentIndex = line.indexOf("=");
            String key = line.substring(0, assignmentIndex),
                   value = line.substring(assignmentIndex+1);
            properties.put(key, value);
        }
    }
    
    /**
     * Fetches the property for the given key in this initialization file, and
     * returns the raw string that was read from the file. If this property
     * cannot be found... what happens?
     * 
     * @param key A key in the initialization file.
     * 
     * @return The value which the given key represents.
     */
    public String getRawValue(String key) {
        return properties.get(key);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public int getValueAsInt(String key) {
        String value = properties.get(key);
        return Integer.parseInt(value);
    }
    
    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * For a section in the initialization file, this method returns a map of
     * all key=value pairs defined within that section.
     * 
     * @param section The name of the section in the initialization file to 
     *                construct this mapping for.
     * 
     * @return A map of key-value pairs defined within the given section in
     *         this initialization file.
     */
    public Map<String, String> getSectionProperties(String section) {
        
        
        return null;
    }
}
