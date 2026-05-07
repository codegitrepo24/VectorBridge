package com.vectorbridge.ai;

import java.util.List;
import java.util.Map;

public class QueryResult {
    private String naturalLanguageQuery;
    private String generatedSql;
    private List<Map<String, Object>> results;
    private int rowCount;
    private String error;

    // Constructors
    public QueryResult() {
    } // --> Why: Jackson requires a no-argument constructor to deserialize JSON into
      // a Java object. When Jackson reads JSON and maps it to a class, it first calls
      // the empty constructor, then sets fields using getters/setters.

    // Why: Used when everything worked correctly — Gemini generated SQL, SQL ran,
    // results came back.
    public QueryResult(String naturalLanguageQuery, String generatedSql, List<Map<String, Object>> results) {
        this.naturalLanguageQuery = naturalLanguageQuery;
        this.generatedSql = generatedSql;
        this.results = results;
        this.rowCount = results != null ? results.size() : 0;
    }

    // Why: Used when something failed — Gemini API call failed, generated SQL was
    // invalid, DB error occurred.
    public QueryResult(String naturalLanguageQuery, String error) {
        this.naturalLanguageQuery = naturalLanguageQuery;
        this.error = error;
    }

    public String getNaturalLanguageQuery() {
        return naturalLanguageQuery;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getError() {
        return error;
    }

}
