import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { Api, SearchResult } from '../../services/api';

@Component({
  selector: 'app-semantic-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './semantic-search.html',
  styleUrls: ['./semantic-search.scss'],
})
export class SemanticSearch {
 searchQuery: string = '';
 results: SearchResult[] = [];
 isLoading: boolean = false;
 hasSearched: boolean = false;

 constructor(private apiService: Api) {}

 performSearch(){
  if(!this.searchQuery.trim()){
    return;
  }
  this.isLoading = true;
  this.hasSearched = true;
  this.apiService.semanticSearch(this.searchQuery).subscribe({
    next: (data) => {
      this.results = data;
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error performing semantic search:', err);
      this.isLoading = false;
    }
  });
 }
}
