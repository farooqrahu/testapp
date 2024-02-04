import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MessageboxComponent} from 'src/app/modal/messagebox/messagebox.component';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import Swal from 'sweetalert2'
import {SaleformComponent} from "../../modal/saleform/saleform.component";
import {Sale} from "../../models/sale.model";
import {ProductRequest} from "../../models/productrequest.model";
import {Invoice} from "../../models/invoice.model";
import {SaleService} from "../../_services/sale.service";
import {SaleOrders} from "../../models/sale.orders.model";
import {SalesListComponent} from "../../modal/viewsalesform/sales-list.component";
import {SalesInvoiceComponent} from "../../modal/saleinvoice/sales-invoice.component";
import {Product} from "../../models/product.model";
import {Reports} from "../../models/report.model";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit, AfterViewInit {
  companycolumnsToDisplay = ["id","product.name","bundleSale","extraSale","totalQuantitySale","createdAt"];
  // saleList =Sale[] = [];
  saleOrdersdatasource: MatTableDataSource<SaleOrders> = null;
  saleOrders: SaleOrders[] = [];
  dataSource: MatTableDataSource<Reports> = new MatTableDataSource();
  reports: Reports[] = [];
  sales: Sale[] = [];
  totalElements: number = 0;
  // invoices: Invoice[] = [];
   invoice: Invoice=new Invoice([],0,0);
  productslength = 0;

  constructor(public saleservice: SaleService, private token: TokenStorageService
    , public dialog: MatDialog
              // ,@Inject(DOCUMENT) document:Document
  ) {
  }

  ngAfterViewInit(): void {}

  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef | any;

  counter(i: number) {
    return new Array(i);
  }
  nextPage(event: PageEvent) {
    // const request = {};
    // request['page'] = ;
    // request['size'] = ;
    const productrequest = new ProductRequest( 0, "",
      "", 0,0,0,0,0, 0,false,false,
      0,0,0,null, null, false, 'name', 'asc', event.pageSize, event.pageIndex)
    this.getAllOrders(productrequest);
  }

  private getAllOrders(request) {
    this.saleservice.getProductReport(request)
      .subscribe(data => {
          this.reports = data['prodContent'];
          this.totalElements = data['totalitems'];
          this.dataSource = new MatTableDataSource(this.reports);
          this.dataSource.sort = this.sort;

        }
        , error => {
          console.log(error.error.message);
        }
      );
  }

  loadproductresults(): void {
    // const category: Category = new Category(0,'');
    const productrequest = new ProductRequest(0, this.productsearch.nativeElement.value,
      this.productsearch.nativeElement.value, 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0,
      null,
      this.productsearch.nativeElement.value, false, 'name', 'asc', 100000000, 0)

    this.getAllOrders(productrequest);
  }

  ngOnInit() {
    const productrequest = new ProductRequest(0, "",
      "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', 10, 0)
    this.getAllOrders(productrequest);
  }

  public doFilter = (value: string, type: string) => {
    switch (type) {
      case 'product':
        this.saleOrdersdatasource.filter = value.trim().toLocaleLowerCase();
        break;

    }
  }

  messagebox(body: string, title?: string) {
    if (title === undefined)
      title = "Notice"
    Swal.fire(
      title,
      body,
      'success'
    )
  }


  openDialog(saleOrders?: SaleOrders): void {
    console.log(saleOrders)
    // if (saleOrders === undefined)
    //   saleOrders = new SaleOrders(0, "","","",  0,0,0,null, null, null,null,null)
    const dialogRef = this.dialog.open(SalesListComponent, {
      width: '1220px', height: '600px',
      data: {
        id: saleOrders.id,
        customerName:saleOrders.customerName,
        mobileNumber:saleOrders.mobileNumber,
        invoiceNo:saleOrders.invoiceNo,
        grandTotal:saleOrders.grandTotal,
        createdAt: saleOrders.createdAt,
        productSales:saleOrders.productSales,
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      // console.log(res);
      const productrequest = new ProductRequest(0, "",
        "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
      this.getAllOrders(productrequest)

    });
  }
  printDialog(saleOrders?: SaleOrders): void {
    // if (saleOrders === undefined)
    //   saleOrders = new SaleOrders(0, "","","","",  0,0,0, null, null, null,null,null)
    const dialogRef = this.dialog.open(SalesInvoiceComponent, {
      width: '1120px', height: '600px',
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
      const productrequest = new ProductRequest(0, "",
        "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
      this.getAllOrders(productrequest)

    });
  }


  submitOrder() {
    if (this.invoice==undefined || this.invoice._sales.length <= 0) {
      Swal.fire(
        'Product Sale!',
        'List is empty!',
        'warning'
      )
    } else {
      const dialogRef = this.dialog.open(SaleformComponent, {
        width: '1120px', height: '600px',
        data: this.invoice
      });
      dialogRef.afterClosed().subscribe(res => {
       this.invoice=new Invoice([],0,0);
       this.sales=[];
        const productrequest = new ProductRequest(0, "",
          "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
        this.getAllOrders(productrequest)

      });

    }
  }
}


