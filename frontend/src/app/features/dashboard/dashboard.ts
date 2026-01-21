import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Routes } from '@angular/router';
import { routeConfig } from '../../app.routes';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  imports: [RouterLinkActive, RouterLink, RouterOutlet],
})
export class Dashboard {
  public routes: Routes = routeConfig.find((r) => r.children)?.children ?? [{ path: '', data: { label: '' } }];
}
