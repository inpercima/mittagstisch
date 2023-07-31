import { Component, OnInit } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { NgIf } from '@angular/common';

@Component({
  selector: 'mt-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: true,
  imports: [NgIf, MatCardModule, MatProgressBarModule],
})
export class DashboardComponent implements OnInit {
  loaded = false;

  constructor() {}

  ngOnInit(): void {
    this.loaded = true;
  }
}
