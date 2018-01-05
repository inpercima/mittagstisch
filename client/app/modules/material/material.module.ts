import { MatButtonModule } from '@angular/material';
import { MatCardModule } from '@angular/material';
import { MatExpansionModule } from '@angular/material';
import { MatIconModule } from '@angular/material';
import { MatInputModule } from '@angular/material';
import { MatProgressBarModule } from '@angular/material';
import { MatTabsModule } from '@angular/material';
import { MatToolbarModule } from '@angular/material';
import { NgModule } from '@angular/core';

@NgModule({
  exports: [
    MatButtonModule,
    MatCardModule,
    MatExpansionModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
    MatTabsModule,
    MatToolbarModule,
  ],
  imports: [
    MatButtonModule,
    MatCardModule,
    MatExpansionModule,
    MatInputModule,
    MatProgressBarModule,
    MatTabsModule,
    MatToolbarModule,
  ],
})

export class MaterialModule { }
