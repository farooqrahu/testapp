import {Category} from './../../models/category.model';
import {ProductService} from './../../_services/product.service';
import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Product} from 'src/app/models/product.model';
import {MatDialog} from '@angular/material/dialog';
import {MatSelect} from '@angular/material/select';
import {Company} from "../../models/compnay.model";
import {Sale} from "../../models/sale.model";
import {Invoice} from "../../models/invoice.model";
import Swal from "sweetalert2";
import {SaleformComponent} from "../saleform/saleform.component";

@Component({
  selector: 'app-addproductform',
  templateUrl: './addproductform.component.html',
  styleUrls: ['./addproductform.component.css']
})
export class AddProductFormComponent implements OnInit {
  categories: Category[];
  companies: Company[];
  errors: String;
  sales: Sale[] = [];
  // invoices: Invoice[] = [];
  sale: Sale;
  invoice: Invoice = new Invoice([], 0, 0);
  itemCount: number = 0;
  priceSelected: string;

  priceTypes = [
    'Retail',
    'Whole',
    'Price'
  ];
  public price:number;

  constructor(
    public dialogRef: MatDialogRef<AddProductFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private productservice: ProductService) {
    this.price=this.data.retailPrice;
  }

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

  calculateRetQty(data: Product) {
    this.data.userTotalQuantity = Number(this.data.userExtraQuantity) + (Number(this.data.quantityItem) * Number(this.data.userQuantityBundle)) || 0;
  }

  validate(data: Product): boolean {
    this.data.priceSelected=this.priceSelected;
    let totalPrice=0;
    if (this.data.priceSelected == 'Retail') {
      totalPrice += this.data.retailPrice*this.data.userTotalQuantity;
    } else if (this.data.priceSelected == 'Whole') {
      totalPrice += this.data.wholeSalePrice*this.data.userTotalQuantity;
    } else if (this.data.priceSelected == 'Price') {
      totalPrice += this.data.price*this.data.userTotalQuantity;
    }
    this.data.grandTotal=totalPrice;
    this.errors = "";
    if (this.data.userTotalQuantity <= 0)
      this.errors += "quantity must not be zero or negative. "
    return (this.errors == "")

  }

  save(data: any): any {
    return this.dialogRef.afterClosed()
  }

  ngOnInit() {
    this.priceSelected = 'Retail';
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
    if (this.data.userTotalQuantity > this.data.quantity || this.data.userTotalQuantity == 0) {
      this.data.userTotalQuantity = 0;
    }
  }

  calcExt() {
    if (this.data.userExtraQuantity >= this.data.quantityItem || (this.data.userTotalQuantity > this.data.quantity)) {
      this.data.userExtraQuantity = 0;
      this.data.userQuantityBundle = 0;
      this.data.userTotalQuantity = 0;
    }
  }

  validateBundle() {
    if (this.data.userQuantityBundle > this.data.quantityBundle) {
      this.data.userQuantityBundle = 0;
      this.data.userExtraQuantity = 0;
      this.data.userTotalQuantity = 0;
    }

  }
}
