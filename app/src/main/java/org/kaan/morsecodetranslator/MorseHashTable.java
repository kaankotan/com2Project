package org.kaan.morsecodetranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orhan on 04.12.2017.
 */

public class MorseHashTable {
    public static Map<String, String> morseDictionary = new HashMap<>();

    public static void createHashTable() {
        morseDictionary.put("sl", "A");
        morseDictionary.put("lsss", "B");
        morseDictionary.put("lsls", "C");
        morseDictionary.put("lsss", "D");
        morseDictionary.put("s", "E");
        morseDictionary.put("ssls", "F");
        morseDictionary.put("lls", "G");
        morseDictionary.put("ssss", "H");
        morseDictionary.put("ss", "I");
        morseDictionary.put("slll", "J");
        morseDictionary.put("lsl", "K");
        morseDictionary.put("slss", "L");
        morseDictionary.put("ll", "M");
        morseDictionary.put("ls", "N");
        morseDictionary.put("lll", "O");
        morseDictionary.put("slls", "P");
        morseDictionary.put("llsl", "Q");
        morseDictionary.put("sls", "R");
        morseDictionary.put("sss", "S");
        morseDictionary.put("l", "T");
        morseDictionary.put("ssl", "U");
        morseDictionary.put("sssl", "V");
        morseDictionary.put("sll", "W");
        morseDictionary.put("lssl", "X");
        morseDictionary.put("lsll", "Y");
        morseDictionary.put("llss", "Z");
    }
}
