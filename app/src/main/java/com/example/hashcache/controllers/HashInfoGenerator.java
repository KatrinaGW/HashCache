package com.example.hashcache.controllers;

import com.example.hashcache.models.HashInfo;

public class HashInfoGenerator {

    public static class NameGenerator{

        //I could not figure out what the hashvalue is supposed to be yet
        protected static String generateName(Object hashValue){
            //generate the name from the hash information

            return null;
        }

    }

    public static class ImageGenerator{
        protected static String generateImage(Object hashValue){
            //generate the image from the hash information

            return null;
        }
    }

    public static class ScoreGenerator{
        protected static int generateScore(Object hashValue){
            //generate the score from the hash information

            return -1;
        }
    }

    public HashInfo createScannableCodeHashInfo(String codeContents){
        //somehow get hash value

        /** Uncomment once implementation finalized
        return new HashInfo(ImageGenerator.generateImage(hasValue),
                NameGenerator.generateName(hashValue), ScoreGenerator.generateScore(hashValue));
        */

        return null;
    }
}
