import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, HostBinding, TemplateRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Routes } from '@angular/router';
import { Title } from '@angular/platform-browser';

import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { FeaturesRoutingModule } from './features/features-routing.module';

@Component({
  selector: 'mt-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  public appname: string;

  public routes: Routes;
  /**
   * Adds the custom theme to the app root.
   * For overlays the OverlayContainer like in the constructor is used.
   * For dialogs the panelClass of the configuration must added manually like
   *
   * const dialogConfig = new MatDialogConfig();
   * dialogConfig.panelClass = `${environment.theme}-theme`;
   */
  @HostBinding('class') class = `${environment.theme}-theme`;

  public constructor(private dialog: MatDialog, private titleService: Title, public overlayContainer: OverlayContainer) {
    this.appname = environment.appname;
    this.routes = AppRoutingModule.ROUTES.concat(FeaturesRoutingModule.ROUTES);
    this.titleService.setTitle(this.appname);
    this.overlayContainer.getContainerElement().classList.add(`${environment.theme}-theme`);
  }

  openDialog(ref: TemplateRef<any>): void {
    this.dialog.open(ref, {
      maxWidth: "800px",
    });
  }
}
