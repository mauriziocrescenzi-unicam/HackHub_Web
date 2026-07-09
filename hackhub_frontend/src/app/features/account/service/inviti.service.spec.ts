import { TestBed } from '@angular/core/testing';
import { InvitiService } from './inviti.service';

describe('InvitiService', () => {
  let service: InvitiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InvitiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
