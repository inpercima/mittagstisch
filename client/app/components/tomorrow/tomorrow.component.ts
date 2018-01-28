import { Component, OnInit } from '@angular/core';

import { LunchService } from '../../services/lunch.service';

import { Lunch } from '../../models/lunch';

@Component({
  selector: 'mt-tomorrow',
  templateUrl: '../today/today.component.html',
})
export class TomorrowComponent implements OnInit {

  private panelOpenState: boolean = false;

  private lunch: Array<Lunch>;

  private loaded: boolean = false;

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
