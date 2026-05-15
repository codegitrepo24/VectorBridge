package com.vectorbridge.ai;

public class VectorMath {

    /**
     * Calculates the Cosine Similarity between two vectors.
     * Returns a score between -1.0 and 1.0 (1.0 is a perfect match).
     */
    public static double cosineSimilarity(double[] vectorA, double[] vectorB){
        // 1. Safety check: Gemini vectors must both be exactly 768 dimensions
        if(vectorA.length != vectorB.length){
            throw new IllegalArgumentException("Vectors must be the exact same length to compare.");
        }

        double dotProduct = 0.0;
        double normA = 0.0; // Magnitude of A
        double normB = 0.0; // Magnitude of B

        // 2. The Core Math Loop
        for(int i=0;i<vectorA.length;i++){
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);

        }

        // 3. Prevent division by zero just in case an array is all 0s
        if(normA == 0.0 || normB == 0.0){
            return 0.0;
        }

        // 4. The Final Formula: Dot Product / (Magnitude A * Magnitude B)
        return dotProduct/(Math.sqrt(normA) * Math.sqrt(normB));

    }
}
