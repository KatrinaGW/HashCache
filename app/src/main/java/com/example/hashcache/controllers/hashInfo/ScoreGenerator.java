package com.example.hashcache.controllers.hashInfo;

import static com.example.hashcache.controllers.hashInfo.Constants.consecutiveOnesProbs;
import static com.example.hashcache.controllers.hashInfo.Constants.totalOnesProbs;
import static com.example.hashcache.controllers.hashInfo.Constants.MAX_SCORE;

public class ScoreGenerator {
    public static long generateScore(byte[] byteArray) throws Exception {
            if (byteArray.length != 8) {
                return -1;
            }
            boolean firstIter = true;
            int largestSequence1 = 1;
            int lastBit = byteArray[0] & 1;
            int consecOnesLargestCount = largestSequence1;
            int totalOnes = 0;
            for (int i = 0; i < byteArray.length; i++) {
                for (int b = 0; b < 8; b++) {
                    if (firstIter) {
                        firstIter = false;
                        continue;
                    }
                    int bit = (byteArray[i] >> b) & 1;
                    totalOnes += bit;
                    if (bit == lastBit && bit == 1) {
                        consecOnesLargestCount += 1;
                        if (consecOnesLargestCount > largestSequence1) {
                            largestSequence1 = consecOnesLargestCount;
                        }
                    } else {
                        consecOnesLargestCount = 1;
                    }
                    lastBit = bit;
                }
            }

            double maxScore = 100000.0;
            double prob = consecutiveOnesProbs[largestSequence1] * totalOnesProbs[totalOnes];
            double maxScoreReciprocal = Math.pow(maxScore, -1);
            long score = Math.round(1.0 / (prob + maxScoreReciprocal));
            return score;
        }

    }
