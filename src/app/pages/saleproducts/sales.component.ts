import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MessageboxComponent} from 'src/app/modal/messagebox/messagebox.component';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Product} from '../../models/product.model';
import Swal from 'sweetalert2'
import {SaleformComponent} from "../../modal/saleform/saleform.component";
import {Sale} from "../../models/sale.model";
import {ProductRequest} from "../../models/productrequest.model";
import {Invoice} from "../../models/invoice.model";
import {AddProductFormComponent} from "../../modal/addproductform/addproductform.component";
import {Category} from "../../models/category.model";

@Component({
  selector: 'app-sales',
  templateUrl: './sales.component.html',
  styleUrls: ['./sales.component.scss']
})
export class SalesComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["id", 'name', "price", "quantity", "description", "category",'createdAt', "action"];
  companycolumnsToDisplay = ["id", 'name', "category", "company", "quantity",'createdAt', "action"];
  // saleList =Sale[] = [];
  dataSource: MatTableDataSource<Product> = null;
  salesdatasource: MatTableDataSource<Sale> = null;
  products: Product[] = [];
  totalElements: number = 0;

  sales: Sale[] = [];
  // invoices: Invoice[] = [];
  sale: Sale;
  invoice: Invoice = new Invoice([], 0, 0);
  productslength = 0;
  itemCount: number = 0;

  constructor(public productservice: ProductService, private token: TokenStorageService
    , public dialog: MatDialog
              // ,@Inject(DOCUMENT) document:Document
  ) {
  }

  ngAfterViewInit(): void {
  }

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
    const productrequest = new ProductRequest( 0, "",
      "", 0,0,0,0,0, 0,false,false,
      0,0,0,null, null, false, 'name', 'asc', event.pageSize, event.pageIndex)
    this.findProductOutOfStock(productrequest);
  }

  loadproductresults(): void {
    // const category: Category = new Category(0,this.productsearch.nativeElement.value);
    const productrequest = new ProductRequest(0, this.productsearch.nativeElement.value,
      null, 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0,
      null,
      null, false, 'name', 'desc', 10, 0)
    this.findProductOutOfStock(productrequest);
  }



  ngOnInit() {
    const productrequest = new ProductRequest(0, "",
      "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', 10, 0);

    this.findProductOutOfStock(productrequest);
  }

  findProductOutOfStock(productrequest) {

    this.productservice.findProductOutOfStock(productrequest).subscribe(
      data => {
        this.products = data['prodContent'];
        this.totalElements = data['totalitems'];
        this.dataSource = new MatTableDataSource(this.products);
        this.dataSource.sort = this.sort;
      },
      err => {
        (err);
      }
    );
  }

  public doFilter = (value: string, type: string) => {
    switch (type) {
      case 'product':
        this.dataSource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'category':
        this.salesdatasource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'sale':
        this.salesdatasource.filter = value.trim().toLocaleLowerCase();
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
    )  }


  openDialog(product?: Product): void {
    if (product === undefined)
      product = new Product(0, "", "", 0,0, null, null, false, 0, 0, 0, 0, false,false,"",null,"",0,0,0,10,0)
    const dialogRef = this.dialog.open(SaleformComponent, {
      width: '55px',
      data: {
        id: product.id,
        name: product.name,
        description: product.description,
        price: product.price,
        wholeSalePrice: product.wholeSalePrice,
        category: product.category,
        company: product.company,
        quantityItem: product.quantityItem,
        quantityBundle: product.quantityBundle,
        extraQuantity: product.extraQuantity
        ,
        quantity: product.quantity
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (JSON.stringify(product) != JSON.stringify(res)) {
        if (product.id != res.id)
          console.log("error")
        else {
          // this.updateProduct(res)
          // this.findProductOutOfStock()
        }
      }


    });
  }

  add(product?: Product): void {
    const dialogRef = this.dialog.open(AddProductFormComponent, {
      width: '880px',
      data: {
        id: product.id, name: product.name, description: product.description,
        price: product.price, category: product.category,company: product.company,quantityItem:product.quantityItem,quantityBundle:product.quantityBundle,extraQuantity:product.extraQuantity
        ,quantity:product.quantity,enableTQ:product.enableTQ,userTotalQuantity:0,userExtraQuantity:0,userQuantityBundle:0
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (JSON.stringify(product) != JSON.stringify(res)) {
        if (product.id != res.id){
          console.log("error")
          return;
        }
        else {
          //send customer and item detail to submit view invoice
          this.sale = new Sale(null, res.name,"","", res.id, res.price,res.userTotalQuantity, res.userQuantityBundle,res.userExtraQuantity,res.userTotalQuantity);
          let qu = 0;
          let tu = 0;
          let found = [];
          if (this.invoice != null && this.invoice._sales.length > 0) {
            found = this.invoice._sales.filter(value => {
              if (value.productId == res.id) {
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
            if (this.invoice) {
              if (this.invoice._totalQuantity > 0) {
                qu = this.invoice._totalQuantity
              }
              if (this.invoice._grandTotal > 0) {
                tu = this.invoice._grandTotal
              }
            }
            this.invoice._sales = this.sales;
            this.invoice._totalQuantity = Number(qu) + Number(res.userTotalQuantity);
            this.invoice._grandTotal = tu + (Number(res.price) * Number(res.userTotalQuantity));
            // this.invoice = new Invoice(this.sales,  ,);
            this.itemCount++;
            this.view();
          }

        }
      }
    });


  }



  view() {
    if (this.sales.length >0) {
      const dialogRef = this.dialog.open(SaleformComponent, {
        width: '1120px', height: '600px',
        data: this.invoice
      });
      dialogRef.afterClosed().subscribe(res => {
        if (res == 'clear') {
          this.clear();
        }
      });
    }else{
      this.clear();
    }
  }

  clear() {
    this.invoice = new Invoice([], 0, 0);
    this.sales = [];
    const productrequest = new ProductRequest(0, "",
      "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
    this.findProductOutOfStock(productrequest);
    this.itemCount = 0;
  }

}


