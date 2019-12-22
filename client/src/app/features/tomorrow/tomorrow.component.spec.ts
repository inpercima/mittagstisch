import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TomorrowComponent } from './tomorrow.component';

describe('TomorrowComponent', () => {
  let component: TomorrowComponent;
  let fixture: ComponentFixture<TomorrowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TomorrowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TomorrowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
