import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Lunch } from './lunch';
import { RequestService } from './request.service';

@Injectable()
export class LunchService {

  constructor(private http: HttpClient, private requestService: RequestService) { }

  public get(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(this.requestService.url('/today'));
  }

  public getTomorrow(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(this.requestService.url('/tomorrow'));
  }

  public getNextWeek(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>(this.requestService.url('/next-week'));
  }

}
