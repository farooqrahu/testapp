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
import jsPDF from 'jspdf';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
pdfMake.vfs = pdfFonts.pdfMake.vfs;
import htmlToPdfmake from 'html-to-pdfmake';
import {PosReceiptComponent} from "../posreciept/pos.receipt.component";
import {WarehousePosReceiptComponent} from "../warehouse-posreciept/warehouse-pos-receipt.component";
@Component({
  selector: 'app-sales-invoice',
  templateUrl: './sales-invoice.component.html',
  styleUrls: ['./sales-invoice.component.css']
})
export class SalesInvoiceComponent implements OnInit {


  sales: Sale[] = [];
  productslength = 0;
  grandTotal = 0;
  wareHouseProduct = false;

  ngAfterViewInit(): void {
  }

  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef | any;

  counter(i: number) {
    return new Array(i);
  }
  title = 'htmltopdf';

  @ViewChild('pdfTable') pdfTable: ElementRef;
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

  public async downloadAsPDF() {
    // document.getElementById("myimage").innerHTML="";
    // const doc = new jsPDF();
    debugger
    const pdfTable = this.pdfTable.nativeElement;

    var html = htmlToPdfmake(pdfTable.innerHTML);
    const documentDefinition = {   content: [html],
      margin: [20, 5, 0, 10],
      styles:{
        tableCenter: {
          alignment: 'center',
          absolutePosition: { x: 10, y: 35 },
        },
      rightme:
          {
            alignment: 'right'
          },'html-table':{alignment:'center'},'html-h1':{ alignment:'center'}},
      pageOrientation: 'portrait', pageMargins: [40,60,40,30],
      // footer: function (currentPage, pageCount) {return {table: { widths: [ "*"],body: [[{text: 'Page: ' + currentPage.toString() + ' of ' + pageCount, alignment: 'center'}]]},};},
    //
    };
    pdfMake.createPdf(documentDefinition).open();
  }

  getBase64ImageFromURL(url) {
    return new Promise((resolve, reject) => {
      var img = new Image();
      img.setAttribute("crossOrigin", "anonymous");

      img.onload = () => {
        var canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;

        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);

        var dataURL = canvas.toDataURL("image/png");

        resolve(dataURL);
      };

      img.onerror = error => {
        reject(error);
      };

      img.src = url;
    });
  }
  printPos(){

  }

  printPosDialog(saleOrders?: SaleOrders,wareHouseProduct?): void {
    debugger
    const result = saleOrders.productSales.filter((obj) => {
      return obj.product.wareHouseProduct === true;

    });
    if(wareHouseProduct){
      const dialogRef = this.dialog.open(WarehousePosReceiptComponent, {
        data: {
          id: saleOrders.id,
          customerName:saleOrders.customerName,
          mobileNumber:saleOrders.mobileNumber,
          invoiceNo:saleOrders.invoiceNo+" - WareHouse",
          grandTotal:saleOrders.grandTotal,
          totalQuantity:saleOrders.totalQuantity,
          createdAt: saleOrders.createdAt,
          productSales:result,
        }
      });
      dialogRef.afterClosed().subscribe(res => {
        // console.log(res);
        const dialogRef2 = this.dialog.open(PosReceiptComponent, {
          data: {
            id: saleOrders.id,
            customerName:saleOrders.customerName,
            mobileNumber:saleOrders.mobileNumber,
            invoiceNo:saleOrders.invoiceNo,
            grandTotal:saleOrders.grandTotal,
            totalQuantity:saleOrders.totalQuantity,
            createdAt: saleOrders.createdAt,
            productSales:saleOrders.productSales,
          }
        });
        setTimeout(() => { // Needed for large documents
          var divContents = document.getElementById("invoice-POS").innerHTML;
          var printWindow = window.open('', '', 'height=400,width=600');
          // printWindow.document.querySelector("*").forEach(e => e.style.display="none");
          printWindow.document.write('<html><head><title>Print DIV Content</title>');
          printWindow.document.write('</head><body >');
          printWindow.document.write(divContents);
          printWindow.document.write('</body></html>');
          printWindow.document.close();
          printWindow.print();
        }, 1000)

        dialogRef2.afterClosed().subscribe(res => {
          // console.log(res);
        });

      });
    }else{
      // if ( saleOrders === undefined)
        // saleOrders = new SaleOrders(0, "","","","",  0,0,0, null, null, null,null,null)
      const dialogRef = this.dialog.open(PosReceiptComponent, {
        data: {
          id: saleOrders.id,
          customerName:saleOrders.customerName,
          customerId:saleOrders.customerId,
          mobileNumber:saleOrders.mobileNumber,
          invoiceNo:saleOrders.invoiceNo,
          grandTotal:saleOrders.grandTotal,
          totalQuantity:saleOrders.totalQuantity,
          createdAt: saleOrders.createdAt,
          productSales:saleOrders.productSales,
        }
      });
      dialogRef.afterClosed().subscribe(res => {
        // console.log(res);
      });

    }
    // let printElement = document.getElementById("invoice-POS");
    // var printWindow = window.open('', 'PRINT');
    // printWindow.document.write(document.documentElement.innerHTML);
    setTimeout(() => { // Needed for large documents
      var divContents = document.getElementById("invoice-POS").innerHTML;
      var printWindow = window.open('', '', 'height=400,width=600');
      // printWindow.document.querySelector("*").forEach(e => e.style.display="none");
      printWindow.document.write('<html><head><title>Print DIV Content</title>');
      printWindow.document.write('</head><body >');
      printWindow.document.write(divContents);
      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }, 1000)
  }

  setWareHousePrint() {
    this.wareHouseProduct=true;
  }
}
