import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProductFormComponent } from './addproductform.component';

describe('AddProductformComponent', () => {
  let component: AddProductFormComponent;
  let fixture: ComponentFixture<AddProductFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddProductFormComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
