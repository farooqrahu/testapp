import { Category } from './../../models/category.model';
import { ProductService } from './../../_services/product.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from 'src/app/models/product.model';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import {Company} from "../../models/compnay.model";

@Component({
  selector: 'app-addproductform',
  templateUrl: './addproductform.component.html',
  styleUrls: ['./addproductform.component.css']
})
export class AddProductFormComponent implements OnInit {
  categories: Category[];
  companies: Company[];
  errors: String;
  constructor(
    public dialogRef: MatDialogRef<AddProductFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private productservice: ProductService) { }
  getAllCategories(): any {
    this.productservice.getAllCategories().subscribe(
      data => {
        this.categories = data.categories;

      },
      err => {
        (err);
      }
    );
  }
  getAllCompanies(): any {
    this.productservice.getAllCompanies().subscribe(
      data => {
        this.companies = data.companies;

      },
      err => {
        (err);
      }
    );
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

  calculateQty(data: Product){
    this.data.quantity=Number(this.data.extraQuantity)+(Number(this.data.quantityItem)*Number(this.data.quantityBundle));
  }
  calculateRetQty(data: Product){
    this.data.returnTotalQuantity=Number(this.data.returnExtraQuantity)+(Number(this.data.quantityItem)*Number(this.data.returnQuantityBundle)) || 0;
  }
  validate(data: Product): boolean {
    // data = data.price.replace(/,/g, '.')
    this.errors = "";
    if (this.data.price <= 0)
      this.errors += "price must not be zero or negative. "
    if (this.data.name == null || this.data.name == "")
      this.errors += "name must not be empty\n"
    return (this.errors == "")
  }
  save(data: any): any {
    return this.dialogRef.afterClosed()
  }

  ngOnInit() {
    this.getAllCategories();
    this.getAllCompanies();

  }

  clearQty(data: Product) {
    this.data.quantity = 0;
    this.data.extraQuantity = 0;
    this.data.quantityItem = 0;
    this.data.quantityBundle = 0;
  }

  validateTotal() {
    if (this.data.returnTotalQuantity>this.data.quantity){
      this.data.returnTotalQuantity=0;
    }
  }

  calcExt() {
    if (this.data.returnExtraQuantity>this.data.quantityItem){
      this.data.returnExtraQuantity=0;
      this.data.returnTotalQuantity=0;
    }
  }
}
