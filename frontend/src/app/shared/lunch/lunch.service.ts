import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { Lunch } from './lunch';

@Injectable({
  providedIn: 'root'
})
export class LunchService {

  constructor(private http: HttpClient) { }

  public get(route: string): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(environment.api + route);
  }
}
