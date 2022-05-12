import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MessageboxComponent} from 'src/app/modal/messagebox/messagebox.component';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Product} from '../../models/product.model';
import Swal from 'sweetalert2'
import {SaleformComponent} from "../../modal/saleform/saleform.component";
import {Sale} from "../../models/sale.model";
import {ProductRequest} from "../../models/productrequest.model";
import {Invoice} from "../../models/invoice.model";
import {SaleService} from "../../_services/sale.service";
import {SaleOrders} from "../../models/sale.orders.model";
import {SalesListComponent} from "../../modal/viewsalesform/sales-list.component";
import {SalesInvoiceComponent} from "../../modal/saleinvoice/sales-invoice.component";

@Component({
  selector: 'app-sales',
  templateUrl: './sale.orders.component.html',
  styleUrls: ['./sale.orders.component.scss']
})
export class SaleOrdersComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["id","invoiceNo","createdAt",  "action"];
  companycolumnsToDisplay = ["id", "invoiceNo","createdAt", "action"];
  // saleList =Sale[] = [];
  saleOrdersdatasource: MatTableDataSource<SaleOrders> = null;
  saleOrders: SaleOrders[] = [];
  sales: Sale[] = [];
  // invoices: Invoice[] = [];
  sale: Sale;
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

  loadproductresults(): void {
    this.paginator.page.subscribe(() => {
        const productrequest = new ProductRequest(0, this.productsearch.nativeElement.value,
          this.productsearch.nativeElement.value, 0, 0, 0, 0, 0, null, null, false, 'name', 'asc', this.paginator.pageSize, this.paginator.getNumberOfPages())
        this.saleservice.findProduct(productrequest).subscribe(
          data => {
            this.saleOrders = data.productOrderInvoiceDtos;
            console.log(this.saleOrders);
            this.productslength = data.totalitems;
            setTimeout(() => {
              this.saleOrdersdatasource = new MatTableDataSource(this.saleOrders);
              this.saleOrdersdatasource.sort = this.sort;
              this.saleOrdersdatasource.paginator = this.paginator;
            });
          },
          err => {
            (err);
          }
        );


      }
    )
  }

  ngOnInit() {
    this.refreshproduct();
  }

  refreshproduct() {

    this.saleservice.getAllOrders().subscribe(
      data => {
        console.log(data);
        this.saleOrders = data.productOrderInvoiceDtos
          console.log("this is te");
        this.productslength = data.totalitems;
        this.saleOrdersdatasource = new MatTableDataSource(this.saleOrders);
        console.log(this.saleOrders);
        setTimeout(() => {
          this.saleOrdersdatasource.sort = this.sort;
          this.saleOrdersdatasource.paginator = this.paginator;
        });
      },
      err => {
        (err);
      }
    );
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
    const dialogRef = this.dialog.open(MessageboxComponent, {
      width: '350px',
      data: {
        title: title, body: body
      }
    });
  }


  openDialog(saleOrders?: SaleOrders): void {
    if (saleOrders === undefined)
      saleOrders = new SaleOrders(0, "",  0,0,0, null, null, null,null,null)
    const dialogRef = this.dialog.open(SalesListComponent, {
      width: '1120px', height: '600px',
      data: {
        id: saleOrders.id,
        invoiceNo:saleOrders.invoiceNo,
        grandTotal:saleOrders.grandTotal,
        createdAt: saleOrders.createdAt,
        productSales:saleOrders.productSales,
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      // console.log(res);
          this.refreshproduct()
    });
  }
  printDialog(saleOrders?: SaleOrders): void {
    if (saleOrders === undefined)
      saleOrders = new SaleOrders(0, "",  0,0,0, null, null, null,null,null)
    const dialogRef = this.dialog.open(SalesInvoiceComponent, {
      width: '1120px', height: '600px',
      data: {
        id: saleOrders.id,
        invoiceNo:saleOrders.invoiceNo,
        grandTotal:saleOrders.grandTotal,
        totalQuantity:saleOrders.totalQuantity,
        createdAt: saleOrders.createdAt,
        productSales:saleOrders.productSales,
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      // console.log(res);
          this.refreshproduct()
    });
  }
  add(product?: Product): void {
    Swal.fire({
      title: "Product Sale!",
      text: "Enter item quantity",
      input: 'number',
      showCancelButton: true
    }).then((result) => {
      let qu=0;
      let tu=0;

      if (!isNaN(result.value)) {
        if (product.quantity >= parseInt(result.value)) {
          this.sale = new Sale(null, product.name, product.id, product.price, result.value);
          let found = [];
          if (this.invoice!=null && this.invoice._sales.length>0) {
            found = this.invoice._sales.filter(value => {
              if (value.productId == product.id) {
                Swal.fire(
                  'Product Sale!',
                  'Product already added!.',
                  'error'
                )
                return value.productId;
              }
            });
          }
          if (found.length == 0) {
            this.sales.push(this.sale);
            Swal.fire(
              'Product Sale!',
              'Product added in the list!.',
              'success'
            )
            if(this.invoice){
              if(this.invoice._totalQuantity>0) {
                qu = this.invoice._totalQuantity
              }
              if(this.invoice._grandTotal>0) {
                tu=this.invoice._grandTotal
              }
            }
            this.invoice._sales=this.sales;
            this.invoice._totalQuantity=Number(qu)+Number(result.value);
            this.invoice._grandTotal= tu+(Number(product.price)*Number(result.value));
            // this.invoice = new Invoice(this.sales,  ,);
          }
        } else {
          Swal.fire(
            'Product Sale!',
            'Product quantity invalid!',
            'error'
          )
        }
      }

      // this.invoice.getSales(this.sale);
      // this.invoice = new Invoice(this.sales,  Number(qu)+Number(result.value), tu+(Number(product.price)*Number(result.value)));
    console.log(this.invoice)
    });



  }

  // addSale(sale: Sale): any {
  //   this.saleservice.addSale(sale).subscribe(
  //     data => {
  //       this.messagebox(data.message);
  //       this.getAllsales()
  //     },
  //     err => {
  //       this.messagebox("Error adding category. make sure it does not already exist");
  //     }
  //   );
  // }
  // updateSale(sale: Sale): any {
  //   this.saleservice.updateSale(sale).subscribe(
  //     data => {
  //       var objIndex = this.sales.findIndex((obj => obj.id == sale.id));
  //       this.sales[objIndex] = sale
  //       this.salesdatasource = new MatTableDataSource(this.sales)
  //
  //       this.messagebox(data.message);
  //     },
  //     err => {
  //       this.messagebox(err.message);
  //
  //     }
  //   );
  // }


  deleteSale(sale: Sale): any {

    // Swal.fire({
    //   title: 'Are you sure?',
    //   text: "You won't be able to revert this!",
    //   icon: 'warning',
    //   showCancelButton: true,
    //   confirmButtonColor: '#3085d6',
    //   cancelButtonColor: '#d33',
    //   confirmButtonText: 'Yes, delete it!'
    // }).then((result) => {
    //   if (result.isConfirmed) {
    //     this.saleservice.deleteSale(sale).subscribe(
    //       data => {
    //         this.getAllCompanies()
    //         Swal.fire(
    //           'Deleted!',
    //           'Your Sale has been deleted.',
    //           'success'
    //         )
    //       },
    //       err => {
    //         this.messagebox("Error deleting sale, please make sure no products are in this sale.");
    //       }
    //     );
    //   }
    // })
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
        this.refreshproduct();
      });

    }
  }
}

