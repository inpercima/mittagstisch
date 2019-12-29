import { Component, OnInit } from '@angular/core';

import { LunchService } from 'src/app/core/lunch.service';
import { Lunch } from 'src/app/core/lunch';

@Component({
  selector: 'mt-next-week',
  templateUrl: '../../shared/lunch-entry/lunch-entry.component.html',
})
export class NextWeekComponent implements OnInit {

  private panelOpenState: boolean;

  private lunch: Array<Lunch>;

  private loaded: boolean;

  constructor(private lunchService: LunchService) {
    this.lunch = new Array<Lunch>();
  }

  ngOnInit(): void {
    this.lunchService.getNextWeek().subscribe(lunch => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }

}
