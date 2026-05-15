package com.vectorbridge.ai;

// Implementing Comparable lets us use this easily with a PriorityQueue!
public class SearchResult implements Comparable<SearchResult> {
    public int sourceId;
    public String sourceTable;
    public String textContent;
    public double similarityScore;

    public SearchResult(int sourceId, String sourceTable, String textContent, double similarityScore){
        this.sourceId = sourceId;
        this.sourceTable = sourceTable;
        this.textContent = textContent;
        this.similarityScore = similarityScore;

    }

    // We sort backwards (highest score first) because we want the best matches at the top
    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.similarityScore, this.similarityScore);
    }
    
}
