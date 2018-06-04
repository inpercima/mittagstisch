import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'mt-dash',
  templateUrl: './dash.component.html',
})
export class DashComponent implements OnInit {

  private loaded: boolean;

  constructor() { }

  ngOnInit(): void {
    this.loaded = true;
  }

}

