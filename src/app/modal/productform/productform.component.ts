import { Category } from './../../models/category.model';
import { ProductService } from './../../_services/product.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from 'src/app/models/product.model';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import {Company} from "../../models/compnay.model";

@Component({
  selector: 'app-productform',
  templateUrl: './productform.component.html',
  styleUrls: ['./productform.component.css']
})
export class ProductformComponent implements OnInit {
  categories: Category[];
  companies: Company[];
  errors: String;
  constructor(
    public dialogRef: MatDialogRef<ProductformComponent>,
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
    debugger;
    let ext = Number(this.data.extraQuantity) || 0;
    let qtyItem = Number(this.data.quantityItem) || 0;
    let bundle = Number(this.data.quantityBundle) || 0;
    if(qtyItem<=0 && bundle<=0){
     this.resetFields();
    }else if(qtyItem>0 && ext>=qtyItem){
      this.resetFields();
    }else if(qtyItem<=0 && (bundle>0 || ext>0) ){
      this.resetFields();
    }else {
      this.data.quantity=ext+(qtyItem*bundle);
    }

  }
  validate(data: Product): boolean {
    // data = data.price.replace(/,/g, '.')
    this.errors = "";
    if (this.data.price <= 0)
      this.errors += "Price must not be zero or negative. "
    if (this.data.name == null || this.data.name == "")
      this.errors += "product name must not be empty\n"
    let qtyItem = Number(data.quantityItem) || 0;
    let bundle = Number(this.data.quantityBundle) || 0;
    if(qtyItem>10000000){
      this.errors+= "qtyItem can not exceed 50\n"
    }
    if(bundle>10000){
      this.errors+= "Bundle can not exceed 50\n"
    }
    if(data.enableTQ){
      let total = Number(this.data.quantity) || 0;
      if(total>10000000){
        this.errors+= "Total quantity can not exceed 10000\n"
      }
    }
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
    console.log(this.data.wareHouseProduct)
    this.data.quantity = 0;
    this.data.extraQuantity = 0;
    this.data.quantityItem = 0;
    this.data.quantityBundle = 0;
  }

  private resetFields() {
    this.data.extraQuantity=0;
    this.data.quantity=0
    this.data.quantityBundle=0;
    this.data.quantityItem=0;
  }

}
