import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { LunchModel } from '../../models/lunch.model';

@Injectable({
  providedIn: 'root',
})
export class DayViewService {
  private http = inject(HttpClient);

  public get(day: 'today' | 'tomorrow'): Observable<LunchModel[]> {
    return this.http.get<LunchModel[]>(environment.api + day);
  }
}
