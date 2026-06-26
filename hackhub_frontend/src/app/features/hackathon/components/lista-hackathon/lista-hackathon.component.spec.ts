import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaHackathon } from './lista-hackathon';

describe('ListaHackathon', () => {
  let component: ListaHackathon;
  let fixture: ComponentFixture<ListaHackathon>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaHackathon],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaHackathon);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
