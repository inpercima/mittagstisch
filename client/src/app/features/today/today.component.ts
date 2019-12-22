import { Component, OnInit } from '@angular/core';

import { LunchService } from 'src/app/core/lunch.service';
import { Lunch } from 'src/app/core/lunch';

@Component({
  selector: 'mt-today',
  templateUrl: './today.component.html',
  styleUrls: ['./today.component.css']
})
export class TodayComponent implements OnInit {

  private panelOpenState: boolean;

  private lunch: Array<Lunch>;

  private loaded: boolean;

  constructor(private lunchService: LunchService) {
    this.lunch = new Array<Lunch>();
  }

  ngOnInit(): void {
    this.lunchService.get().subscribe(lunch => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }

}
