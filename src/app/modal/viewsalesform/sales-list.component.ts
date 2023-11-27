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
import {Product} from "../../models/product.model";

@Component({
  selector: 'app-sales-list',
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.css']
})
export class SalesListComponent implements OnInit {


  sales: Sale[] = [];
  productslength = 0;
  grandTotal = 0;
  disabledBtn=false;
  ngAfterViewInit(): void {
  }

  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef | any;
  public show: boolean = true;
  public buttonName: any = 'Hide Product Returned';

  counter(i: number) {
    return new Array(i);
  }

  companies: Sale[];
  invoice: Invoice;
  errors: string = "";
  returnItemQuantity: number = 0;

  constructor(
    public dialogRef: MatDialogRef<SalesListComponent>,
    @Inject(MAT_DIALOG_DATA) public productSales: SaleOrders, private saleService: SaleService, private productService: ProductService, public dialog: MatDialog) {
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

  // validate(p: ProductSales){
  //   if(p.returnQuantity>p.quantity){
  //     p.returnQuantity=p.quantity;
  //   }
  // }

  calculateRetQty(p: ProductSales, product: Product) {
    if (Number(Number(p.userExtraQuantity)) > 0) {
      p.userTotalQuantity = Number(Number(p.userExtraQuantity)) + (Number(product.quantityItem) * Number(p.userQuantityBundle) || 0) || 0;
    } else {
      p.userTotalQuantity = (Number(product.quantityItem) * Number(p.userQuantityBundle) || 0) || 0;
    }
    if ( p.userTotalQuantity > this.productSales.totalQuantity) {
      p.userExtraQuantity = 0;
      p.userQuantityBundle = 0;
      p.userTotalQuantity = 0;
      this.disabledBtn=true;
    }else {
      this.disabledBtn=false;

    }

  }

  validateTotal(p: ProductSales, product: Product) {
    debugger
    if ((Number(p.userTotalQuantity)) > Number(p.totalQuantitySale)) {
      p.userExtraQuantity = 0;
      p.userQuantityBundle = 0;
      p.userTotalQuantity = 0;
    }
  }

  validateExt(p: ProductSales, product: Product) {
    debugger

    if (Number(p.userExtraQuantity) >= p.product.quantityItem) {
      p.userQuantityBundle = 0;
      p.userExtraQuantity = 0;
      p.userTotalQuantity = 0;
    }
  }

  validateBundle(p: ProductSales, product: Product) {
    if (Number(p.userQuantityBundle) > p.bundleSale || (Number(p.userTotalQuantity) + Number(this.productSales.totalQuantityReturn) || 0) > this.productSales.totalQuantity) {
      p.userQuantityBundle = 0;
      p.userExtraQuantity = 0;
      p.userTotalQuantity = 0;
    }

  }

  returnProductSale(): any {


// console.log("return qu");
//     console.log(this.productSales);
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
        this.saleService.returnProductSale(this.productSales).subscribe(
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

  // submitOrder(): any {
  //   this.saleService.addProduct(this.productSaleList._sales).subscribe(
  //     productSaleList => {
  //       this.swAlert(productSaleList.message, "Product Sale!");
  //       // this.exportAsExcelFile(this.productSaleList._sales,"receipt")
  //       return this.dialogRef.close();
  //     },
  //     err => {
  //       this.swAlert("System error occurred!", "Product Sale!");
  //     }
  //   );
  //
  //
  // }

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
    console.log("invoiceNo")
    console.log(this.productSales.invoiceNo);


    // let totalQuantity=0;
    // let grandTotal=0;
    // this.productSaleList._sales.forEach(value =>
    // {totalQuantity=Number(totalQuantity)+Number(value.quantity)
    //   grandTotal=grandTotal+(Number(value.price)*Number(value.quantity));
    // })
    // this.productSaleList.setTotalQuantity(totalQuantity);
    // this.productSaleList.setGrandTotal(grandTotal);
  }

  //
  // remove(product: Sale) {
  //   let index: number = this.productSaleList._sales.findIndex(a => a.productId === product.productId);
  //   if (index != -1) {
  //     this.productSaleList._totalQuantity=this.productSaleList._totalQuantity-product.quantity;
  //     this.productSaleList._grandTotal=this.productSaleList._grandTotal-(product.price*product.quantity);
  //     this.productSaleList._sales.splice(index, 1);
  //   }
  //   if (this.productSaleList._sales.length <= 0) {
  //     this.onNoClick();
  //   }
  //   console.log(this.productSaleList);
  //
  // }


  toggle() {
    this.show = !this.show;

    // Change the name of the button.
    if (this.show)
      this.buttonName = "Hide Product Returned";
    else
    this.buttonName = "Show Product Returned";
  }

}
