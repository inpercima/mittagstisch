import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { BistroModel } from '../../models/bistro.model';
import { LunchModel } from '../../models/lunch.model';
import { DayViewService } from './day-view.service';
import { DayView } from './day-view';

describe('DayView', () => {
  let component: DayView;
  let fixture: ComponentFixture<DayView>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DayView],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { routeConfig: { path: 'today' } } } },
        { provide: DayViewService, useValue: { get: (): Observable<LunchModel[]> => of([]) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DayView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the no-data note, status, and decorative plate for empty dishes', () => {
    component.lunches.set([createLunch('NO_DATA', [])]);
    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.grid > div');
    expect(card.textContent).toContain('Keine Daten');
    expect(card.textContent).toContain('Für diesen Tag liegen derzeit keine Speiseinformationen vor.');
    expect(card.querySelector('img[aria-hidden="true"][alt=""]')).toBeTruthy();
  });

  it('shows dish descriptions, prices, and the update date for populated cards', () => {
    component.lunches.set([
      createLunch('SUCCESS', [{ name: 'Linsensuppe mit Brot', price: '5,50 €' }]),
    ]);
    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.grid > div');
    expect(card.textContent).toContain('Aktuell');
    expect(card.textContent).toContain('Linsensuppe mit Brot');
    expect(card.textContent).toContain('5,50 €');
    expect(card.textContent).toContain('Aktualisiert am 28.10.2026');
    expect(card.textContent).not.toContain('keine Speiseinformationen');
  });
});

function createLunch(status: string, dishes: LunchModel['dishes']): LunchModel {
  const bistro: BistroModel = { name: 'Testkantine', url: 'https://example.com' };
  return { bistro, dishes, status, importDate: new Date('2026-10-28T12:00:00') };
}
