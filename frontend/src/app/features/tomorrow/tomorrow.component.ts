import { Component, OnInit } from '@angular/core';

import { LunchService } from 'src/app/core/lunch.service';
import { Lunch } from 'src/app/core/lunch';

@Component({
  selector: 'mt-tomorrow',
  templateUrl: '../../shared/lunch-entry/lunch-entry.component.html',
})
export class TomorrowComponent implements OnInit {

  lunch: Lunch[];

  loaded = false;

  constructor(private lunchService: LunchService) {
    this.lunch = [];
  }

  ngOnInit(): void {
    this.lunchService.getTomorrow().subscribe(lunch => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }

}
