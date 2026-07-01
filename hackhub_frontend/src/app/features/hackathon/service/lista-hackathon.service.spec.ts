import { TestBed } from '@angular/core/testing';

import { ListaHackathonService } from './lista-hackathon.service';

describe('ListaHackathonService', () => {
  let service: ListaHackathonService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListaHackathonService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
