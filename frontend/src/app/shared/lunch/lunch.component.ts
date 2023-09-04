import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ActivatedRoute } from '@angular/router';
import { Lunch } from './lunch';
import { LunchService } from './lunch.service';

@Component({
  selector: 'mt-lunch',
  standalone: true,
  templateUrl: './lunch.component.html',
  styleUrls: ['./lunch.component.css'],
  imports: [MatCardModule, MatDividerModule, MatProgressBarModule, NgFor, NgIf],
})
export class LunchComponent implements OnInit {
  lunch: Lunch[] = [];
  path: string;

  loaded = false;

  constructor(public route: ActivatedRoute, private lunchService: LunchService) {
    this.path = route.snapshot.routeConfig!.path!;
  }

  ngOnInit(): void {
    this.lunchService.get(this.path).subscribe((lunch) => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }
}
