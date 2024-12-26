import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Lunch } from './lunch';

@Injectable({
  providedIn: 'root',
})
export class LunchService {
  private http = inject(HttpClient);

  public get(route: string): Observable<Lunch[]> {
    return this.http.get<Lunch[]>(environment.api + route);
  }
}
