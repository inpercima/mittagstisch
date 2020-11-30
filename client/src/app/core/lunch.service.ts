import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Lunch } from './lunch';
import { environment } from 'src/environments/environment';

@Injectable()
export class LunchService {

  constructor(private http: HttpClient) { }

  public get(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(environment.api + '/today');
  }

  public getTomorrow(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(environment.api + '/tomorrow');
  }

  public getNextWeek(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(environment.api + '/next-week');
  }

}
