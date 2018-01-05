import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Lunch } from '../models/lunch';

@Injectable()
export class LunchService {

  constructor(private http: HttpClient) { }

  public get(): Observable<Array<Lunch>> {
    return this.http.get<Array<Lunch>>('/today');
  }

}
