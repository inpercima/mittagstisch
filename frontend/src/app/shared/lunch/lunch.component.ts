import { Component, OnInit, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ActivatedRoute } from '@angular/router';
import { Lunch } from './lunch';
import { LunchService } from './lunch.service';

@Component({
  selector: 'mt-lunch',
  templateUrl: './lunch.component.html',
  styleUrls: ['./lunch.component.css'],
  imports: [MatCardModule, MatDividerModule, MatProgressBarModule],
})
export class LunchComponent implements OnInit {
  route = inject(ActivatedRoute);
  private lunchService = inject(LunchService);

  lunch: Lunch[] = [];
  path: string;

  loaded = false;

  constructor() {
    const route = this.route;

    this.path = route.snapshot.routeConfig!.path!;
  }

  ngOnInit(): void {
    this.lunchService.get(this.path).subscribe((lunch) => {
      this.lunch = lunch;
      this.loaded = true;
    });
  }
}
