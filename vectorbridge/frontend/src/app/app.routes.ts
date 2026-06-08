import { Routes } from '@angular/router';
import { SemanticSearch } from './components/semantic-search/semantic-search';
import { Nl2sql } from './components/nl2sql/nl2sql';

export const routes: Routes = [
  { path: 'search', component: SemanticSearch },
  { path: 'ask', component: Nl2sql },
  { path: '', redirectTo: '/search', pathMatch: 'full' }
];
