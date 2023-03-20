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
  customerName: String = "";
  isOldCustomer: boolean = false;
  mobileNumber: String = "03";
  mobileNumberError: boolean;
  mobileNumberErrorText: String = "";

  constructor(
    public dialogRef: MatDialogRef<SaleformComponent>,
    @Inject(MAT_DIALOG_DATA) public productSaleList: Invoice, private saleService: SaleService, private productService: ProductService, public dialog: MatDialog) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  printTest() {
    console.log({
      node_module: printJS,
      es6_module: es6printJS
    });
    es6printJS("receipt", "html");
  }

  validateForm():boolean {
    if((this.customerName==undefined || this.customerName==="") && this.isOldCustomer){
    return false;
    }else
    if(this.mobileNumber==undefined || this.mobileNumber===""){
    return false;
    }else if(this.mobileNumber.length<11){
      return false;
    }

    return true;
  }

  submitOrder(): any {
    if (this.productSaleList._sales.length > 0) {
    let isValid=this.validateForm();
      if(isValid){
        this.saleService.submitSaleOrder(this.productSaleList,this.customerName,this.mobileNumber).subscribe(
          productSaleList => {
            this.swAlert(productSaleList.message, "Product Sale!");
            // this.exportAsExcelFile(this.productSaleList._sales,"receipt")

            return this.dialogRef.close("clear");
          },
          err => {
            this.swAlert("System error occurred!", "Product Sale!");
          }
        );

      }else{
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


  ngOnInit() {
    // let totalQuantity=0;
    // let grandTotal=0;
    // this.productSaleList._sales.forEach(value =>
    // {totalQuantity=Number(totalQuantity)+Number(value.quantity)
    //   grandTotal=grandTotal+(Number(value.price)*Number(value.quantity));
    // })
    // this.productSaleList.setTotalQuantity(totalQuantity);
    // this.productSaleList.setGrandTotal(grandTotal);
  }


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
    if(this.mobileNumber.length<11){
      this.mobileNumberErrorText='Invalid mobile Number!';
    }else{
      this.saleService.findCustomerByMobileNumber(this.mobileNumber).subscribe(
        customer => {
          this.customerName=customer.name;
          this.isOldCustomer=true;
        },
        err => {
          if(err.status==400)
            // this.swAlert("Customer not found!", "Customer");
          this.customerName="";
          this.isOldCustomer=false;
        }
      );
    }
  }
}
