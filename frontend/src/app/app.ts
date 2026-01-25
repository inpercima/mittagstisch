import { Component, inject } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterOutlet, Routes } from '@angular/router';
import { environment } from '../environments/environment';
import { routeConfig } from './app.routes';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [RouterOutlet],
})
export class App {
  readonly #titleService = inject(Title);

  appname: string;
  routes: Routes;

  constructor() {
    this.appname = environment.appname;
    this.routes = routeConfig;
    this.#titleService.setTitle(this.appname);
  }
}
