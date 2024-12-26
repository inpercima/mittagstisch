import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { Lunch } from './lunch';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LunchService {
  private http = inject(HttpClient);


  public get(route: string): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(environment.api + route);
  }
}
