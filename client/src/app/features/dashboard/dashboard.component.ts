import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'mt-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {

  private loaded: boolean;

  constructor() { }

  ngOnInit(): void {
    this.loaded = true;
  }

}
