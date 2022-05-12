import { ComponentFixture, TestBed } from '@angular/core/testing';
import {SaleformComponent} from "./saleform.component";


describe('SaleformComponent', () => {
  let component: SaleformComponent;
  let fixture: ComponentFixture<SaleformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SaleformComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SaleformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
