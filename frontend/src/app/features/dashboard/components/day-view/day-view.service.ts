import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { LunchModel } from '../../models/lunch.model';

@Service()
export class DayViewService {
  private http = inject(HttpClient);

  get(day: 'today' | 'tomorrow'): Observable<LunchModel[]> {
    return this.http.get<LunchModel[]>(environment.api + day);
  }
}
