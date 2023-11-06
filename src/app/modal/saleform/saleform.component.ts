import {Sale} from '../../models/sale.model';
import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {MatSort} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";
import {SaleService} from "../../_services/sale.service";
import {ProductService} from "../../_services/product.service";
import Swal from "sweetalert2";
import {Invoice} from "../../models/invoice.model";
import * as printJS from "print-js";
import * as es6printJS from "print-js";
import * as XLSX from "xlsx";
import {CustomerModel} from "../../models/Customer.model";
import {Category} from "../../models/category.model";
import {FormControl} from "@angular/forms";
import {map, Observable, startWith} from "rxjs";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {MatOptionSelectionChange} from "@angular/material/core";

@Component({
  selector: 'app-saleform',
  templateUrl: './saleform.component.html',
  styleUrls: ['./saleform.component.css']
})
export class SaleformComponent implements OnInit {


  sales: Sale[] = [];
  productslength = 0;

  ngAfterViewInit(): void {
  }

  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef | any;

  counter(i: number) {
    return new Array(i);
  }

  companies: Sale[];
  customerList: CustomerModel[];
  invoice: Invoice;
  errors: String = "";

  customerId: String = "";
  customerName: String = "";
  isOldCustomer: boolean = false;
  mobileNumber: String = "";
  mobileNumberError: boolean;
  mobileNumberErrorText: String = "";
  customers: CustomerModel[];
  filteredOptions: Observable<CustomerModel[]>;
  myControl = new FormControl();

  constructor(
    public dialogRef: MatDialogRef<SaleformComponent>,
    @Inject(MAT_DIALOG_DATA) public productSaleList: Invoice, private saleService: SaleService, private productService: ProductService, public dialog: MatDialog) {
  }

  ngOnInit() {
    this.getAllCustomers();
  }

  private _filter(value: string): CustomerModel[] {
    const filterValue = value.toLowerCase();
    let data = this.customers.filter(option => option.name.toLowerCase().includes(filterValue));
    if (data.length > 0) {
      return data;
    } else {
      this.customerName = filterValue;
      this.mobileNumber=null;
      this.customerId=null;
      this.isOldCustomer=false;

    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getAllCustomers(): any {
    this.saleService.getAllCustomers().subscribe(
      data => {
        this.customers = data;
        this.filteredOptions = this.myControl.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filter(value))
          );

      },
      err => {
        (err);
      }
    );
  }

  setCustomer(event: MatOptionSelectionChange, customerModel: CustomerModel) {
    if(event.source["_selected"] === true){
      //your next set of action goes here
      this.customerId=""+customerModel.id
      this.mobileNumber=""+customerModel.mobileNumber
      this.isOldCustomer=true;
    }
  }
  printTest() {
    console.log({
      node_module: printJS,
      es6_module: es6printJS
    });
    es6printJS("receipt", "html");
  }

  validateForm():boolean {
    if((this.customerName==undefined || this.customerId==="")){
    return false;
    }
    // else
    // if(this.mobileNumber==undefined || this.mobileNumber===""){
    // return false;
    // }else if(this.mobileNumber.length<11){
    //   return false;
    // }

    return true;
  }

  submitOrder(): any {
    if (this.productSaleList._sales.length > 0) {
      let isValid = this.validateForm();
      if (isValid) {
        this.saleService.submitSaleOrder(this.productSaleList, this.customerId,this.customerName, this.mobileNumber).subscribe(
          productSaleList => {
            this.swAlert(productSaleList.message, "Product Sale!");
            // this.exportAsExcelFile(this.productSaleList._sales,"receipt")

            return this.dialogRef.close("clear");
          },
          err => {
            this.swAlert("System error occurred!", "Product Sale!");
          }
        );

      } else {
        Swal.fire(
          'Product Sale!',
          'Please enter customer detail!',
          'error')
      }
    }
  }

  toExportFileName(excelFileName: string): string {
    return `${excelFileName}_export_${new Date().getTime()}.xlsx`;
  }

  public exportAsExcelFile(json: any[], excelFileName: string): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
    const workbook: XLSX.WorkBook = {Sheets: {'data': worksheet}, SheetNames: ['data']};
    XLSX.writeFile(workbook, this.toExportFileName(excelFileName));
  }

  swAlert(body: string, title?: string) {
    Swal.fire(
      title,
      body,
      'success'
    )
  }


  // ngOnInit() {
  // let totalQuantity=0;
  // let grandTotal=0;
  // this.productSaleList._sales.forEach(value =>
  // {totalQuantity=Number(totalQuantity)+Number(value.quantity)
  //   grandTotal=grandTotal+(Number(value.price)*Number(value.quantity));
  // })
  // this.productSaleList.setTotalQuantity(totalQuantity);
  // this.productSaleList.setGrandTotal(grandTotal);
  // }


  remove(product: Sale) {
    let index: number = this.productSaleList._sales.findIndex(a => a.productId === product.productId);
    if (index != -1) {
      this.productSaleList._totalQuantity = this.productSaleList._totalQuantity - product.totalQuantitySale;
      this.productSaleList._grandTotal = this.productSaleList._grandTotal - (product.price * product.totalQuantitySale);
      this.productSaleList._sales.splice(index, 1);
    }
    if (this.productSaleList._sales.length <= 0) {
      this.onNoClick();
    }
    // console.log(this.productSaleList);

  }

  validateMobileNumber() {
    if (this.mobileNumber.length < 11) {
      this.mobileNumberErrorText = 'Invalid mobile Number!';
    }
  }


}
