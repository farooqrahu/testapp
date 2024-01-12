import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Product} from '../../models/product.model';
import {BarcodeComponent} from "../../modal/barcode/barcode.component";
import {ProductRequest} from "../../models/productrequest.model";
import Swal from "sweetalert2";

@Component({
    selector: 'app-producthistory',
    templateUrl: './producthistory.component.html',
    styleUrls: ['./producthistory.component.scss']
})
export class ProductHistoryComponent implements OnInit, AfterViewInit {
    columnsToDisplay = ["id", 'name', "price", "quantityItem", "quantityBundle", "extraQuantity", "quantity", "description", "category", "createdAt", "userName"];

    dataSource: MatTableDataSource<Product> = null;
    products: Product[] = [];
    productslength = 0;
    totalElements: number = 0;

    constructor(public productservice: ProductService, private token: TokenStorageService
        , public dialog: MatDialog
    ) {
    }

    ngAfterViewInit(): void {
    }

    @ViewChild(MatSort) sort: MatSort | any;
    @ViewChild(MatPaginator) paginator: MatPaginator | any;

    // @ViewChild(MatPaginator) set matPaginator(paginator: MatPaginator) {
    //     this.dataSource.paginator = paginator;
    // }

    @ViewChild('productsearch') productsearch: ElementRef | any;

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
        console.log(this.paginator.pageSize, this.paginator.getNumberOfPages())
        const productrequest = new ProductRequest(0, this.productsearch.nativeElement.value,
            this.productsearch.nativeElement.value, 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, this.productsearch.nativeElement.value, this.productsearch.nativeElement.value, false, 'name', 'asc', this.paginator.pageSize, 0)

        this.getProducts(productrequest);
    }

    ngOnInit() {
        if (!(this.token.isAdmin() || this.token.isDEO())) {
            this.token.signOut();
        }
        const productrequest = new ProductRequest(0, "",
            "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', 10, 0)
        this.getProducts(productrequest);
    }

    private getProducts(request) {
        this.productservice.findProductHistory(request)
            .subscribe(data => {
                    this.products = data['prodHisContent'];
                    this.totalElements = data['totalitems'];
                    this.dataSource = new MatTableDataSource(this.products);
                    this.dataSource.sort = this.sort;
                }
                , error => {
                    console.log(error.error.message);
                }
            );
    }

    // refreshproduct() {
    //
    //   this.productservice.getAllProductHistory().subscribe(
    //     data => {
    //       this.products = data.list;
    //       (this.products.length);
    //       this.productslength = data.totalitems;
    //       this.dataSource = new MatTableDataSource(this.products);
    //       setTimeout(() => {
    //         this.dataSource.sort = this.sort;
    //         this.dataSource.paginator = this.paginator;
    //       });
    //     },
    //     err => {
    //       (err);
    //     }
    //   );
    // }
    nextPage(event: PageEvent) {
        // const request = {};
        // request['page'] = ;
        // request['size'] = ;
        const productrequest = new ProductRequest(0, "",
            "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', event.pageSize, event.pageIndex)

        this.getProducts(productrequest);
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


