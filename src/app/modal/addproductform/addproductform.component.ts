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

  constructor(
    public dialogRef: MatDialogRef<AddProductFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private productservice: ProductService) {
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

  calculateQty(data: Product) {
    this.data.quantity = Number(this.data.extraQuantity) + (Number(this.data.quantityItem) * Number(this.data.quantityBundle));
  }

  calculateRetQty(data: Product) {
    this.data.userTotalQuantity = Number(this.data.userExtraQuantity) + (Number(this.data.quantityItem) * Number(this.data.userQuantityBundle)) || 0;
  }

  validate(data: Product): boolean {
    this.errors = "";
    if (this.data.userTotalQuantity <= 0)
      this.errors += "quantity must not be zero or negative. "
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
    if (this.data.userTotalQuantity > this.data.quantity || this.data.userTotalQuantity==0) {
      this.data.userTotalQuantity = 0;
    }
  }

  calcExt() {
    if (this.data.userExtraQuantity >= this.data.quantityItem || (this.data.userTotalQuantity>this.data.quantity)) {
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

  // addProductToList(data: Product) {
  //   this.submitOrder();
  //   // console.log(this.invoice);
  // }
  //
  // submitOrder() {
  //   if (this.invoice == undefined || this.invoice._sales.length <= 0) {
  //     Swal.fire(
  //       'Product Sale!',
  //       'List is empty!',
  //       'warning'
  //     )
  //   } else {
  //     Swal.fire(
  //       'Product Sale!',
  //       'List is empty!',
  //       'warning'
  //     )
  //
  //   }
  // }

}
