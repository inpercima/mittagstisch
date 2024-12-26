import { DOCUMENT } from '@angular/common';
import { Component, TemplateRef, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Title } from '@angular/platform-browser';
import { RouterLink, RouterLinkActive, RouterOutlet, Routes } from '@angular/router';
import { environment } from '../environments/environment';
import { AppRoutingPipe } from './app-routing.pipe';
import { ROUTES } from './app.routes';

@Component({
  selector: 'mt-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [MatButtonModule, MatDialogModule, MatTabsModule, MatToolbarModule, RouterLinkActive, RouterLink, RouterOutlet, AppRoutingPipe],
})
export class AppComponent {
  private dialog = inject(MatDialog);
  private titleService = inject(Title);
  private document = inject<Document>(DOCUMENT);

  public appname: string;
  public routes: Routes;

  public constructor() {
    this.appname = environment.appname;
    this.routes = ROUTES;
    this.titleService.setTitle(this.appname);
    this.document.body.classList.add(`${environment.theme}-theme`);
  }

  openDialog(ref: TemplateRef<Element>): void {
    this.dialog.open(ref, {
      maxWidth: '800px',
    });
  }
}
