import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  imports: [MatCardModule, MatProgressBarModule],
})
export class Dashboard implements OnInit {
  loaded = false;

  ngOnInit(): void {
    this.loaded = true;
  }
}
