import { NgClass } from '@angular/common';
import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { LunchModel } from '../../models/lunch.model';
import { StatusBoxClassPipe } from '../pipes/status-box-class.pipe';
import { StatusTextClassPipe } from '../pipes/status-text-class.pipe';
import { StatusTextPipe } from '../pipes/status-text.pipe';
import { DayViewService } from './day-view.service';

@Component({
  selector: 'app-day-view',
  templateUrl: './day-view.html',
  imports: [NgClass, StatusTextPipe, StatusBoxClassPipe, StatusTextClassPipe],
})
export class DayView implements OnInit {
  readonly #route = inject(ActivatedRoute);
  readonly #dayViewService = inject(DayViewService);
  readonly #destroyRef = inject(DestroyRef);

  readonly day = this.#route.snapshot.routeConfig!.path! as 'today' | 'tomorrow';

  readonly lunches = signal<LunchModel[]>([]);
  readonly loading = signal<boolean>(false);

  ngOnInit(): void {
    this.loading.set(true);
    this.#dayViewService
      .get(this.day)
      .pipe(takeUntilDestroyed(this.#destroyRef))
      .subscribe((lunches) => {
        this.lunches.set(lunches);
        this.loading.set(false);
      });
  }
}
