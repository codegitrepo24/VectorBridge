import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../services/api';

@Component({
  selector: 'app-nl2sql',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nl2sql.html',
  styleUrl: './nl2sql.scss'
})
export class Nl2sql { // or Nl2sqlComponent depending on what you named it
  // Step 1 State
  question: string = '';
  isGenerating: boolean = false;
  
  // Step 2 State
  generatedSql: string | null = null;
  isExecuting: boolean = false;
  
  // Step 3 State
  aiResponse: any[] | null = null;
  
  // Global Error State
  errorMessage: string | null = null;

  constructor(private apiService: Api) {}

  // Action 1: Ask AI to generate SQL
  generateSql() {
    if (!this.question.trim()) return;

    this.isGenerating = true;
    this.errorMessage = null;
    this.generatedSql = null;
    this.aiResponse = null;

    this.apiService.generateSql(this.question).subscribe({
      next: (data: {sql: string}) => {
        this.generatedSql = data.sql;
        this.isGenerating = false;
      },
      error: (err: any) => {
        console.error('SQL Generation failed', err);
        this.errorMessage = 'Failed to generate SQL. Please check the server logs.';
        this.isGenerating = false;
      }
    });
  }

  // Action 2: User approves and runs the SQL
  runSql() {
    if (!this.generatedSql?.trim()) return;

    this.isExecuting = true;
    this.errorMessage = null;

    this.apiService.executeSql(this.generatedSql).subscribe({
      next: (data: any[]) => {
        this.aiResponse = data;
        this.isExecuting = false;
      },
      error: (err: any) => {
        console.error('SQL Execution failed', err);
        // Using optional chaining to safely extract the backend error message
        this.errorMessage = err.error?.error || 'Failed to execute query. Only SELECT statements are allowed.';
        this.isExecuting = false;
      }
    });
  }

  // Action 3: Reset the form
  startOver() {
    this.question = '';
    this.generatedSql = null;
    this.aiResponse = null;
    this.errorMessage = null;
  }

  getColumnHeaders(dataArray: any[]): string[] {
    if (!dataArray || dataArray.length === 0) return [];
    return Object.keys(dataArray[0]);
  }
}