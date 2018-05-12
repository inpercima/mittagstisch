import { Component, OnInit } from '@angular/core';

import { LunchService } from '../../core/lunch.service';

import { Lunch } from '../../core/lunch';

@Component({
  selector: 'mt-tomorrow',
  templateUrl: '../today/today.component.html',
})
export class TomorrowComponent implements OnInit {

  private panelOpenState: boolean;

  private lunch: Array<Lunch>;

  private loaded: boolean;

  constructor(private lunchService: LunchService) {
    this.lunch = new Array<Lunch>();
  }

  ngOnInit(): void {
    this.lunchService.getTomorrow().subscribe(lunch => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }

}
