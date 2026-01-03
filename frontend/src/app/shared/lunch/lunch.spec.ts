import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Lunch } from './lunch';

describe('Lunch', () => {
  let component: Lunch;
  let fixture: ComponentFixture<Lunch>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Lunch],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              routeConfig: {
                path: 'dashboard',
              },
            },
          },
        },
      ],
    });
    fixture = TestBed.createComponent(Lunch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
