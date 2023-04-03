package com.example.hashcache.controllers.hashInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Generates a random name for a scannableCode
 */
public class NameGenerator {
    // Singleton variable for names
    private static ArrayList<String> NAMES;

    /**
     * Generates the hashes name
     * @param hashValue a long integer associated with the hashcode generated
     * @return the generated name as a string
     */
    public static String generateName(long hashValue){
        //generate the name from the hash information
        return generateFunnyName(hashValue).concat(generateRealName(hashValue));
    }
    /**
     * Uses bits 0-11 from the bitmap to generate a funny name
     * @param bitmap An integer storing information to generate unique funny name
     * @return The monsters funny name
     */
    private static String generateFunnyName(long bitmap) {
        String[] firstPrefix = {"Go", "Ta", "Blo", "Fi"};
        String[] firstSuffix = {"rpus", "trox", "bulon", "mp"};
        String[] secondPrefix = {"Cro", "Aa", "Xe", "Di"};
        String[] secondSuffix = {"rg", "bol", "vex", "zzle"};
        //String[] endings = {"First", "Second", "Third", "Fourth"};
        String[] endings = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Ruler"};

        // Add the first and second name to the funny name
        String funnyName = firstPrefix[getBits(bitmap,0,2)] + firstSuffix[getBits(bitmap,2,2)];
        funnyName += " " + secondPrefix[getBits(bitmap,4,2)] + secondSuffix[getBits(bitmap,6,2)];
        funnyName += " the " + endings[getBits(bitmap, 8, 3)];

        return funnyName;
    }

    /**
     * Generates the real name by getting an index from a file associate to the values of the 12-21 bits
     * @param bitmap An integer storing info to generate real name
     * @return The monsters reals name
     */
    private static String generateRealName(long bitmap) {
        int idx = getBits(bitmap, 12, 10);
        return " " + NAMES.get(idx);
    }

    /**
     * Reads from a csv file the first 1024 names then stores them in a ArrayList
     */
    public static void getNames(InputStream names_csv) {
        String temp;
        Scanner scan = new Scanner(names_csv);
        NAMES = new ArrayList<String>();

        // Set the delim
        scan.useDelimiter("\n");
        scan.next();
        for (int i = 0; i < 1024 && scan.hasNext(); i++) {
            temp = scan.next().split(",")[1];
            NAMES.add(temp.substring(1, temp.length() - 1));
        }
        // Close the file scanner
        scan.close();

    }

    /** Gets the desired bits from num1
     * @param num Number to get bits from
     * @param start Index of starting bit
     * @param n Number of bits
     * @return The value of the n bits starting from start
     */
    private static int getBits(long num, int start, int n) {
        return (int)((num >> start) & ((1 << n) - 1));
    }
}
