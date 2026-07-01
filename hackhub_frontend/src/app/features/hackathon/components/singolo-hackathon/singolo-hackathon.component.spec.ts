import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingoloHackathonComponent } from './singolo-hackathon.component';

describe('SingoloHackathonComponent', () => {
  let component: SingoloHackathonComponent;
  let fixture: ComponentFixture<SingoloHackathonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingoloHackathonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SingoloHackathonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
