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
import {Category} from "../../models/category.model";

@Component({
  selector: 'app-sales',
  templateUrl: './sale.orders.component.html',
  styleUrls: ['./sale.orders.component.scss']
})
export class SaleOrdersComponent implements OnInit, AfterViewInit {
  companycolumnsToDisplay = ["id","customerName","mobileNumber", "invoiceNo","createdAt", "action"];
  // saleList =Sale[] = [];
  saleOrdersdatasource: MatTableDataSource<SaleOrders> = null;
  saleOrders: SaleOrders[] = [];
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
  @ViewChild(MatPaginator, { read: true }) paginator: MatPaginator;
  @ViewChild('productsearch') productsearch: ElementRef | any;

  counter(i: number) {
    return new Array(i);
  }

  nextPage(event: PageEvent) {
    // const request = {};
    // request['page'] = ;
    // request['size'] = ;
    const productrequest=this.getProductRequest('',event.pageSize,event.pageIndex);
    this.getAllOrders(productrequest);
  }

  getProductRequest( name:string,pageSize:number,pageNumber:number) {
    const productrequest = new ProductRequest( 0, name,
      name, 0,0,0,0,0,0, 0,false,false,
      0,0,0,null, null, false, 'name', 'asc', pageSize, pageNumber);
  return productrequest;
  }

  private getAllOrders(request) {
    this.saleservice.getAllOrders(request)
      .subscribe(data => {
          this.saleOrders = data['saleOrders'];
          this.totalElements = data['totalitems'];
          this.saleOrdersdatasource = new MatTableDataSource(this.saleOrders);
          this.saleOrdersdatasource.sort = this.sort;
        }
        , error => {
          console.log(error.error.message);
        }
      );
  }
  loadproductresults(): void {
    const category: Category = new Category(0,'');
    const productrequest=this.getProductRequest(this.productsearch.nativeElement.value,100000000,0);
    this.getAllOrders(productrequest);
  }

  ngOnInit() {
    // const productrequest = new ProductRequest(0, "",
    //   "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', 10, 0)
    const productrequest=this.getProductRequest('',10,0);

    this.getAllOrders(productrequest);
  }

  public doFilter = (value: string, type: string) => {
    switch (type) {
      case 'product':
        this.saleOrdersdatasource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'saleOrders':
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
    // console.log(saleOrders)

    // if (saleOrders === undefined)
    //   saleOrders = new SaleOrders(0, "","","",  0,0,0,null, null, null,null,null)

    this.saleservice.findReturnOrdersByInvoiceNo(saleOrders.invoiceNo).subscribe(
      data => {
        console.log(data);
        // this.saleOrders = data.productOrderInvoiceDtos
        // console.log("this is te");
        // this.productslength = data.totalitems;
        // this.saleOrdersdatasource = new MatTableDataSource(this.saleOrders);
        // console.log(this.saleOrders);
        const dialogRef = this.dialog.open(SalesListComponent, {
          width: '1150px', height: '630px',
          data: {
            id: saleOrders.id,
            customerName:saleOrders.customerName,
            mobileNumber:saleOrders.mobileNumber,
            invoiceNo:saleOrders.invoiceNo,
            totalQuantity:saleOrders.totalQuantity,
            grandTotal:saleOrders.grandTotal,
            createdAt: saleOrders.createdAt,
            productSales:saleOrders.productSales,
            productReturnList:data.productReturnList,
            totalQuantityReturn:data.totalQuantityReturn,

          }
        });
        dialogRef.afterClosed().subscribe(res => {
          // console.log(res);
          // const productrequest = new ProductRequest(0, "",
          //   "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
          const productrequest=this.getProductRequest('',10,0);
          this.getAllOrders(productrequest)
        });
      },
      err => {
        (err);
      }
    );


  }
  printDialog(saleOrders?: SaleOrders): void {
    // if (saleOrders === undefined)
      // saleOrders = new SaleOrders(0, "","","","",  0,0,0, null, null, null,null,null)
    const dialogRef = this.dialog.open(SalesInvoiceComponent, {
      width: '1150px', height: '630px',
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
      // const productrequest = new ProductRequest(0, "",
      //   "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
      const productrequest=this.getProductRequest('',10,0);
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
        // const productrequest = new ProductRequest(0, "",
        //   "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
        const productrequest=this.getProductRequest('',10,0);
        this.getAllOrders(productrequest)
      });

    }
  }
}


