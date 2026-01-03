import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LunchModel } from './lunch.model';

@Injectable({
  providedIn: 'root',
})
export class LunchService {
  private http = inject(HttpClient);

  public get(route: string): Observable<LunchModel[]> {
    return this.http.get<LunchModel[]>(environment.api + route);
  }
}
