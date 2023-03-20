import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {CategoryformComponent} from 'src/app/modal/categoryform/categoryform.component';
import {MessageboxComponent} from 'src/app/modal/messagebox/messagebox.component';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Category} from '../../models/category.model';
import {Product} from '../../models/product.model';
import Swal from 'sweetalert2'
import {BarcodeComponent} from "../../modal/barcode/barcode.component";
import {Company} from "../../models/compnay.model";
import {MatTableExporterDirective} from "mat-table-exporter";

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["id", 'name', "price","quantity", "description", "category", "action"];
  categorycolumnsToDisplay = ["id", 'name', "action"];
  companycolumnsToDisplay = ["id", 'name', "action"];
  @ViewChild("exporter") exporter! : MatTableExporterDirective;
  dataSource: MatTableDataSource<Product> = null;
  categoriesdatasource: MatTableDataSource<Category> = null;
  companiesdatasource: MatTableDataSource<Company> = null;
  products: Product[] = [];
  categories: Category[] = [];
  companies: Company[] = [];
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

  ngOnInit() {
    this.getAllCategories();
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
            this.messagebox("Error deleting category, please make sure no products are in this category.");
          }
        );
      }
    })
  }

  addCategory(category: Category): any {
    this.productservice.addCategory(category).subscribe(
      data => {
        this.messagebox(data.message);
        this.getAllCategories()
      },
      err => {
        this.messagebox("Error adding category. make sure it does not already exist");
      }
    );
  }
  updateCategory(category: Category): any {
    this.productservice.updateCategory(category).subscribe(
      data => {
        var objIndex = this.categories.findIndex((obj => obj.id == category.id));
        this.categories[objIndex] = category
        this.categoriesdatasource = new MatTableDataSource(this.categories)

        this.messagebox(data.message);
      },
      err => {
        this.messagebox(err.message);

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
        this.messagebox("error getting categories");
      }
    );
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


  addCompany(company: Company): any {
    this.productservice.addCompany(company).subscribe(
      data => {
        this.messagebox(data.message);
        this.getAllCategories()
      },
      err => {
        this.messagebox("Error adding category. make sure it does not already exist");
      }
    );
  }
  updateCompany(company: Company): any {
    this.productservice.updateCompany(company).subscribe(
      data => {
        var objIndex = this.companies.findIndex((obj => obj.id == company.id));
        this.companies[objIndex] = company
        this.companiesdatasource = new MatTableDataSource(this.companies)

        this.messagebox(data.message);
      },
      err => {
        this.messagebox(err.message);

      }
    );
  }


}


