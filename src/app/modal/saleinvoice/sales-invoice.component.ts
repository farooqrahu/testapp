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
import {SaleOrders} from "../../models/sale.orders.model";
import {ProductSales} from "../../models/product.sale.model";

@Component({
  selector: 'app-sales-invoice',
  templateUrl: './sales-invoice.component.html',
  styleUrls: ['./sales-invoice.component.css']
})
export class SalesInvoiceComponent implements OnInit {


  sales: Sale[] = [];
  productslength = 0;
  grandTotal = 0;

  ngAfterViewInit(): void {
  }

  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef | any;

  counter(i: number) {
    return new Array(i);
  }

  companies: Sale[];
  invoice: Invoice;
  errors: string = "";
  returnItemQuantity: number = 0;

  constructor(
    public dialogRef: MatDialogRef<SalesInvoiceComponent>,
    @Inject(MAT_DIALOG_DATA) public productOrderList: SaleOrders, private saleService: SaleService, private productService: ProductService, public dialog: MatDialog) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  printTest() {
    console.log({
      node_module: printJS,
      es6_module: es6printJS
    });
    es6printJS("print", "html");
  }

  validate(p: ProductSales) {
    // if (p.returnQuantity > p.quantity || p.returnQuantity == 0) {
    //   p.returnQuantity = p.quantity;
    // }
  }

  returnProductSale(): any {
    console.log("return qu");
    console.log(this.productOrderList);
    Swal.fire({
      title: 'Are you sure?',
      text: "You want to return this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, Return it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.saleService.returnProductSale(this.productOrderList).subscribe(
          productSaleList => {
            this.swAlert("Product Returned Successfully", "Product Sale!");
            // this.exportAsExcelFile(this.productSaleList._sales,"receipt")
            this.onNoClick();
          },
          err => {
            this.swAlert("System error occurred!", "Product Sale!");
          }
        );
      }
    })


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
  }
}
