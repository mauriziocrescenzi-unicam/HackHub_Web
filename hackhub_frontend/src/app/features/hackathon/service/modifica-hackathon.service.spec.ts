import { TestBed } from '@angular/core/testing';

import { ModificaHackathonService } from './modifica-hackathon.service';

describe('ModificaHackathon', () => {
  let service: ModificaHackathonService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModificaHackathonService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
