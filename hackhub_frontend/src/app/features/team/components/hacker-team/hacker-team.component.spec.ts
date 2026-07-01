import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HackerTeamComponent } from './hacker-team.component';

describe('HackerTeamComponent', () => {
  let component: HackerTeamComponent;
  let fixture: ComponentFixture<HackerTeamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HackerTeamComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HackerTeamComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
