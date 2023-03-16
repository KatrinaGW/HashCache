package com.example.hashcache.controllers.hashInfo;

public class HashExceptions {
    public static class AlreadyHasCodeEx extends Exception{
        public AlreadyHasCodeEx(String errorMsg){
            super(errorMsg);
        }
    }
}
