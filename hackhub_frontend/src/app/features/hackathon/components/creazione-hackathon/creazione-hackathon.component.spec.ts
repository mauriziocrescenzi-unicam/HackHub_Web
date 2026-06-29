import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreazioneHackathonComponent } from './creazione-hackathon.component';

describe('CreazioneHackathonComponent', () => {
  let component: CreazioneHackathonComponent;
  let fixture: ComponentFixture<CreazioneHackathonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreazioneHackathonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreazioneHackathonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
