import { Component, OnInit } from '@angular/core';

import { LunchService } from '../../services/lunch.service';

import { Lunch } from '../../models/lunch';

@Component({
  selector: 'mt-next-week',
  templateUrl: './next-week.component.html',
})
export class NextWeekComponent implements OnInit {

  private panelOpenState: boolean = false;

  private lunch: Array<Lunch>;

  private loaded: boolean = false;

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
