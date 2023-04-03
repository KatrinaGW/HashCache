package com.example.hashcache.controllers.hashInfo;

/**
 * Specific exceptions to throw when generating the hash
 */
public class HashExceptions {
    /**
     * Exception thrown when the user tries to scan a code they've already scanned
     */
    public static class AlreadyHasCodeEx extends Exception{
        public AlreadyHasCodeEx(String errorMsg){
            super(errorMsg);
        }
    }
}
