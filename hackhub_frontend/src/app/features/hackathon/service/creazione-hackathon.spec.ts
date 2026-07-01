import { TestBed } from '@angular/core/testing';

import { CreazioneHackathon } from './creazione-hackathon';

describe('CreazioneHackathon', () => {
  let service: CreazioneHackathon;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreazioneHackathon);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
