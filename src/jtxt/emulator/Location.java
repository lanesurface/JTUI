/* 
 * Copyright 2018 Lane W. Surface 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jtxt.emulator;

/**
 * A place where text can be inserted within the buffer.
 */
public class Location {
    public int line,
               position;
    
    public Location(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    public Location(Location loc) {
        this(loc.line, loc.position);
    }
    
    public void setLocation(int line, int position) {
        this.line = line;
        this.position = position;
    }
    
    @Override
    public String toString() {
        return "Location: [line=" + line + "," + " position=" + position + "]";
    }
}
