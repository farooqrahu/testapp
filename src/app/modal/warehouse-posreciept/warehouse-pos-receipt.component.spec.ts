import { ComponentFixture, TestBed } from '@angular/core/testing';
import {WarehousePosReceiptComponent} from "./warehouse-pos-receipt.component";


describe('WarehousePosReceiptComponent', () => {
  let component: WarehousePosReceiptComponent;
  let fixture: ComponentFixture<WarehousePosReceiptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WarehousePosReceiptComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehousePosReceiptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
