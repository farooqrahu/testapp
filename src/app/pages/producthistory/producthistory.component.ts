import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Category} from '../../models/category.model';
import {Product} from '../../models/product.model';
import {BarcodeComponent}  from "../../modal/barcode/barcode.component";
import {Company} from "../../models/compnay.model";
import {MessageboxComponent} from "../../modal/messagebox/messagebox.component";
import {ProductRequest} from "../../models/productrequest.model";
import Swal from "sweetalert2";

@Component({
  selector: 'app-producthistory',
  templateUrl: './producthistory.component.html',
  styleUrls: ['./producthistory.component.scss']
})
export class ProductHistoryComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["id", 'name', "price","quantityItem","quantityBundle","extraQuantity","quantity", "description", "category","createdAt","userName"];

  dataSource: MatTableDataSource<Product> = null;
  products: Product[] = [];
  productslength = 0;
  constructor(public productservice: ProductService, private token: TokenStorageService
    , public dialog: MatDialog
    // ,@Inject(DOCUMENT) document:Document
  ) { }
  ngAfterViewInit(): void {
  }
  @ViewChild(MatSort) sort: MatSort | any;
  @ViewChild(MatPaginator) paginator: MatPaginator | any;
  @ViewChild('productsearch') productsearch: ElementRef  | any;
  counter(i: number) {
    return new Array(i);
  }


  public doFilter = (value: string, type: String) => {
    switch (type) {
      case 'product':
        this.dataSource.filter = value.trim().toLocaleLowerCase();
        break;
    }
  }

  viewDialog(product?: Product): void {
    const dialogRef = this.dialog.open(BarcodeComponent, {
      width: '820px',
      data: {
        id: product.id, file: product.file
      }
    });
    dialogRef.afterClosed().subscribe(res => {
    });
  }

  loadproductresults(): void {
    this.paginator.page.subscribe(() => {
        const productrequest = new ProductRequest( 0, this.productsearch.nativeElement.value,
          this.productsearch.nativeElement.value,0, 0,0,0,0, 0,false,false,0,0,0,null, null, false, 'name', 'asc', this.paginator.pageSize, this.paginator.getNumberOfPages())
        this.productservice.findProductHistory(productrequest).subscribe(
          data => {
            this.products = data.list;
            // (this.products);
            this.productslength = data.totalitems;
            setTimeout(() => {
              this.dataSource = new MatTableDataSource(this.products);
              this.dataSource.sort = this.sort;
              this.dataSource.paginator = this.paginator;
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
    if (!(this.token.isAdmin() || this.token.isDEO())) {
      this.token.signOut();
    }
    this.refreshproduct();
  }

  refreshproduct() {

    this.productservice.getAllProductHistory().subscribe(
      data => {
        this.products = data.list;
        (this.products.length);
        this.productslength = data.totalitems;
        this.dataSource = new MatTableDataSource(this.products);
        setTimeout(() => {
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
        });
      },
      err => {
        (err);
      }
    );
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


}


