import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificaHackathonComponent } from './modifica-hackathon.component';

describe('ModificaHackathonComponent', () => {
  let component: ModificaHackathonComponent;
  let fixture: ComponentFixture<ModificaHackathonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModificaHackathonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ModificaHackathonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
