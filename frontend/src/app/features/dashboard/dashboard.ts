import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Routes } from '@angular/router';
import { routeConfig } from '../../app.routes';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  imports: [RouterLinkActive, RouterLink, RouterOutlet, DatePipe],
})
export class Dashboard {
  protected routes: Routes = routeConfig.find((r) => r.children)?.children ?? [{ path: '', data: { label: '' } }];
  protected today = new Date();
  protected tomorrow = new Date(this.today.getTime() + 1 * 24 * 60 * 60 * 1000);
}
