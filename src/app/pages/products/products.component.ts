import {ProductRequest} from './../../models/productrequest.model';
import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {CategoryformComponent} from 'src/app/modal/categoryform/categoryform.component';
import {ProductformComponent} from 'src/app/modal/productform/productform.component';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Category} from '../../models/category.model';
import {Product} from '../../models/product.model';
import Swal from 'sweetalert2'
import {BarcodeComponent} from "../../modal/barcode/barcode.component";
import {Company} from "../../models/compnay.model";
import {CompanyformComponent} from "../../modal/companyform/companyform.component";
import {SelectionModel} from "@angular/cdk/collections";
import {MatTableExporterDirective} from "mat-table-exporter";

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["select", "id", 'name', "price", "wholeSalePrice", "quantityItem", "quantityBundle", "extraQuantity", "quantity", "description", "category", "company", "outOfStock", "createdAt", "action"];
  products: Product[] = [];
  productslength = 0;
  totalElements: number = 0;

  categoriesdatasource: MatTableDataSource<Category> = null;
  companiesdatasource: MatTableDataSource<Company> = null;
  categories: Category[] = [];
  companies: Company[] = [];

  constructor(public productservice: ProductService, private token: TokenStorageService
    , public dialog: MatDialog
              // ,@Inject(DOCUMENT) document:Document
  ) {
  }

  ngAfterViewInit(): void {
  }
  @ViewChild(MatSort) sort: MatSort | any;
  // @ViewChild(MatPaginator) paginator: MatPaginator | any;

  //declare your datasource like this
  dataSource: MatTableDataSource<Product> = new MatTableDataSource();
  @ViewChild(MatPaginator, { read: true }) paginator: MatPaginator;
  ngOnInit() {
    if (!(this.token.isAdmin() || this.token.isDEO())) {
      this.token.signOut();
    }
    // this.productsearch.nativeElement=""
    const productrequest = new ProductRequest(0, "",
      "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', 10, 0)
    this.getProducts(productrequest);

    this.getAllCompanies();
    this.getAllCategories();
  }
  @ViewChild("exporter") exporter!: MatTableExporterDirective;
  // dataSource: MatTableDataSource<Product> = null;
  selection = new SelectionModel<Product>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {

    if (this.dataSource) {
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: Product): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.id + 1}`;
  }


  @ViewChild('productsearch') productsearch: ElementRef | any;

  nextPage(event: PageEvent) {
    // const request = {};
    // request['page'] = ;
    // request['size'] = ;
    const productrequest = new ProductRequest(0, '',
      "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'asc', event.pageSize, event.pageIndex)

    this.getProducts(productrequest);
  }

  private getProducts(request) {
    this.productservice.findProduct(request)
      .subscribe(data => {
          this.products = data['prodContent'];
          this.totalElements = data['totalitems'];
          this.dataSource = new MatTableDataSource(this.products);
          this.dataSource.sort = this.sort;
        }
        , error => {
          console.log(error.error.message);
        }
      );
  }


  counter(i: number) {
    return new Array(i);
  }
  loadproductresults(): void {
    // const category: Category = new Category(0,this.productsearch.nativeElement.value);
    const productrequest = new ProductRequest(0, this.productsearch.nativeElement.value,
      this.productsearch.nativeElement.value, 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0,
      null,
      this.productsearch.nativeElement.value, false, 'name', 'asc', 1000000000, 0)
      this.getProducts(productrequest);
  }


  public doFilter = (value: string, type: String) => {
    switch (type) {
      case 'product':
        this.dataSource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'category':
        this.categoriesdatasource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'company':
        this.companiesdatasource.filter = value.trim().toLocaleLowerCase();
        break;

    }
  }

  openDialog(product?: Product): void {
    if (product === undefined)
      product = new Product(0, "", "", 0, 0, this.categories[0], this.companies[0], false, 0, 0, 0, 0, false, false, "", null, "", 0, 0, 0, 10, 0)
    const dialogRef = this.dialog.open(ProductformComponent, {
      width: '690px',
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
        quantity: product.quantity,
        enableTQ: product.enableTQ,
        wareHouseProduct: product.wareHouseProduct
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (JSON.stringify(product) != JSON.stringify(res)) {
        if (product.id != res.id)
          console.log("error")
        else {
          this.updateProduct(res);
          const productrequest = new ProductRequest(0, "",
            "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
          this.getProducts(productrequest)
        }
      }


    });
  }

  viewDialog(product?: Product): void {
    const dialogRef = this.dialog.open(BarcodeComponent, {
      width: '920px',
      data: {
        id: product.id, file: product.file
      }
    });
    dialogRef.afterClosed().subscribe(res => {
    });
  }

  opencategorydialog(category?: Category): void {
    if (category === undefined || category.name == null)
      category = new Category(0, "");
    const dialogRef = this.dialog.open(CategoryformComponent, {
      width: '350px',
      data: {
        id: category.id, name: category.name
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res === undefined)
        return;
      if (JSON.stringify(category) == JSON.stringify(res))
        return;
      else {
        if (category.id != res.id)
          return;
        else {
          if (category.id == 0)
            this.addCategory(res);
          else
            this.updateCategory(res);
          this.getAllCategories();
        }
      }
    });
  }

  @ViewChild('produtimage', {static: false})
  produtimage: HTMLImageElement;

  changeproductimage() {
    this.produtimage.src =
      '/assets/Productimages/default.jpg';
  }

  public onFileChanged(event, id, i) {
    this.updateProductImage(event, id, i);

  }

  public fileExists(id, number): boolean {
    var exists = false;
    this.productservice.fileExists(id, number).subscribe(
      data => {
        exists = data
      }
    )
    return exists;
  }

  updateProductImage(event, id, i): void {
    const image: FormData = new FormData();
    image.append('image', event.target.files[0]);
    // image.append('username', "mod");
    // image.append('password', "123456");
    image.append('id', id);
    image.append('number', i);
    this.productservice.updateProductImage(image).subscribe(
      data => {
        window.location.reload()
      },
      err => {
        this.messageboxError("cannot update image");
      }
    );

  }

  updateProduct(product: Product): any {
    this.productservice.updateProduct(product).subscribe(
      data => {
        var objIndex = this.products.findIndex((obj => obj.id == product.id));
        this.products[objIndex] = product
        this.dataSource = new MatTableDataSource(this.products);
        const productrequest = new ProductRequest(0, "",
          "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
        this.getProducts(productrequest);
        this.messageboxSuccess(data.message);

      },
      err => {
        this.messageboxError("error updating product");

      }
    );
  }

  deleteProduct(product: Product): any {
    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productservice.deleteProduct(product).subscribe(
          data => {
            Swal.fire(
              'Deleted!',
              'Your Product has been deleted.',
              'success'
            );
            const productrequest = new ProductRequest(0, "",
              "", 0, 0, 0, 0, 0, 0, false, false, 0, 0, 0, null, null, false, 'name', 'desc', 10, 0);
            this.getProducts(productrequest);
          },
          err => {
            this.messageboxError("error deleting product");
          }
        );
      }
    })


  }

  deleteCategory(category: Category): any {

    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productservice.deleteCategory(category).subscribe(
          data => {
            this.getAllCategories()
            Swal.fire(
              'Deleted!',
              'Your Product has been deleted.',
              'success'
            )
          },
          err => {
            this.messageboxError("error deleting category, please make sure no products are in this category.");
          }
        );
      }
    })
  }

  addCategory(category: Category): any {
    this.productservice.addCategory(category).subscribe(
      data => {
        this.messageboxSuccess(data.message);
        this.getAllCategories()
      },
      err => {
        this.messageboxError("error adding category. make sure it does not already exist");
      }
    );
  }

  updateCategory(category: Category): any {
    this.productservice.updateCategory(category).subscribe(
      data => {
        var objIndex = this.categories.findIndex((obj => obj.id == category.id));
        this.categories[objIndex] = category
        this.categoriesdatasource = new MatTableDataSource(this.categories)

        this.messageboxSuccess(data.message);
      },
      err => {
        this.messageboxError(err.message);

      }
    );
  }

  getAllCategories(): any {
    this.productservice.getAllCategories().subscribe(
      data => {
        this.categories = data.categories;
        this.categoriesdatasource = new MatTableDataSource(this.categories);
        setTimeout(() => {
          this.categoriesdatasource.sort = this.sort;
          this.categoriesdatasource.paginator = this.paginator;
        });

      },
      err => {
        this.messageboxError("error getting categories");
      }
    );
  }

  messageboxSuccess(body: string, title?: string) {
    if (title === undefined)
      title = "Notice"
    Swal.fire(
      title,
      body,
      'success'
    )
  }

  messageboxError(body: string, title?: string) {
    if (title === undefined)
      title = "Notice"
    Swal.fire(
      title,
      body,
      'error'
    )
  }


  opencompanydialog(company?: Company): void {
    if (company === undefined || company.name == null)
      company = new Category(0, "");
    const dialogRef = this.dialog.open(CompanyformComponent, {
      width: '350px',
      data: {
        id: company.id, name: company.name
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res === undefined)
        return;
      if (JSON.stringify(company) == JSON.stringify(res))
        return;
      else {
        if (company.id != res.id)
          return;
        else {
          if (company.id == 0)
            this.addCompany(res);
          else
            this.updateCompany(res);
          this.getAllCompanies();
        }
      }
    });
  }

  addCompany(company: Company): any {
    this.productservice.addCompany(company).subscribe(
      data => {
        this.messageboxSuccess(data.message);
        this.getAllCategories()
      },
      err => {
        this.messageboxError("error adding category. make sure it does not already exist");
      }
    );
  }

  updateCompany(company: Company): any {
    this.productservice.updateCompany(company).subscribe(
      data => {
        var objIndex = this.companies.findIndex((obj => obj.id == company.id));
        this.companies[objIndex] = company
        this.companiesdatasource = new MatTableDataSource(this.companies)

        this.messageboxSuccess(data.message);
      },
      err => {
        this.messageboxError(err.message);

      }
    );
  }

  getAllCompanies(): any {
    this.productservice.getAllCompanies().subscribe(
      data => {
        this.companies = data.companies;
        this.companiesdatasource = new MatTableDataSource(this.companies);
        setTimeout(() => {
          this.companiesdatasource.sort = this.sort;
          this.companiesdatasource.paginator = this.paginator;
        });

      },
      err => {
        this.messageboxError("error getting companies");
      }
    );
  }


  deleteCompany(company: Company): any {

    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productservice.deleteCompany(company).subscribe(
          data => {
            this.getAllCompanies()
            Swal.fire(
              'Deleted!',
              'Your Company has been deleted.',
              'success'
            )
          },
          err => {
            this.messageboxError("error deleting company, please make sure no products are in this company.");
          }
        );
      }
    })
  }

  // }


}


