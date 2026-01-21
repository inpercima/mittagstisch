import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DayView } from './day-view';

describe('DayView', () => {
  let component: DayView;
  let fixture: ComponentFixture<DayView>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DayView],
    }).compileComponents();

    fixture = TestBed.createComponent(DayView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
