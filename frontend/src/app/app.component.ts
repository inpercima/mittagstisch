import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, HostBinding, TemplateRef, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Routes } from '@angular/router';

import { environment } from '../environments/environment';
import { AppRoutingPipe } from './app-routing.pipe';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';

import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ROUTES } from './app.routes';
import { NgFor } from '@angular/common';

@Component({
  selector: 'mt-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [
    NgFor,
    MatButtonModule,
    MatDialogModule,
    MatTabsModule,
    MatToolbarModule,
    RouterLinkActive,
    RouterLink,
    RouterOutlet,
    AppRoutingPipe,
  ],
})
export class AppComponent {
  private dialog = inject(MatDialog);
  private titleService = inject(Title);
  overlayContainer = inject(OverlayContainer);

  public appname: string;

  public routes: Routes;
  // Adds the custom theme to the app root.
  @HostBinding('class') class = `${environment.theme}-theme`;

  public constructor() {
    this.appname = environment.appname;
    this.routes = ROUTES;
    this.titleService.setTitle(this.appname);
    // Adds the custom theme to dialogs.
    this.overlayContainer.getContainerElement().classList.add(`${environment.theme}-theme`);
  }

  openDialog(ref: TemplateRef<Element>): void {
    this.dialog.open(ref, {
      maxWidth: '800px',
    });
  }
}
