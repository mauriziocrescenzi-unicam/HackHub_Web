import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaHackathonComponent } from './lista-hackathon.component';

describe('ListaHackathonComponent', () => {
  let component: ListaHackathonComponent;
  let fixture: ComponentFixture<ListaHackathonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaHackathonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaHackathonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
