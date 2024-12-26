import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'mt-dashboard',
  templateUrl: './dashboard.component.html',
  imports: [MatCardModule, MatProgressBarModule],
})
export class DashboardComponent implements OnInit {
  loaded = false;

  ngOnInit(): void {
    this.loaded = true;
  }
}
