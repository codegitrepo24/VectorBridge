import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SearchResult{
  sourceId: number;
  sourceTable: string;
  textContent: string;
  similarityScore: number;
}
@Injectable({
  providedIn: 'root',
})
export class Api {
  constructor(private http: HttpClient){ }

  semanticSearch(query: string, limit: number = 3): Observable<SearchResult[]> {
    const body = { query: query, limit: limit };
    return this.http.post<SearchResult[]>('/vectors/search', body);
  }

  // askDatabase(question: string): Observable<any> {
  //   const body = { query: question };
  //   return this.http.post<any>('/api/ai/query', body);
  // }

  generateSql(question: string): Observable<{sql: string}> {
    const body = { query: question };
    return this.http.post<{sql: string}>('/api/ai/generate-sql', body);
  }

  executeSql(sqlQuery: string): Observable<any[]> {
    const body = { sql: sqlQuery };
    return this.http.post<any[]>('/api/ai/execute-sql', body);
  }
}
