import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Lunch } from './lunch';

@Injectable()
export class LunchService {

  constructor(private http: HttpClient) { }

  public get(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>('/today');
  }

  public getTomorrow(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>('/tomorrow');
  }

  public getNextWeek(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>('/next-week');
  }

}
