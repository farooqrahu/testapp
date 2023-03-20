import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MessageboxComponent} from 'src/app/modal/messagebox/messagebox.component';
import {ProductService} from 'src/app/_services/product.service';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {Category} from '../../models/category.model';
import {Product} from '../../models/product.model';
import Swal from 'sweetalert2'
import {Company} from "../../models/compnay.model";
import {CompanyformComponent} from "../../modal/companyform/companyform.component";
import {MatTableExporterDirective} from "mat-table-exporter";

@Component({
  selector: 'app-companies',
  templateUrl: './companies.component.html',
  styleUrls: ['./companies.component.scss']
})
export class CompaniesComponent implements OnInit, AfterViewInit {
  columnsToDisplay = ["id", 'name', "price","quantity", "description", "category", "action"];
  categorycolumnsToDisplay = ["id", 'name', "action"];
  companycolumnsToDisplay = ["id", 'name', "action"];

  dataSource: MatTableDataSource<Product> = null;
  @ViewChild("exporter") exporter! : MatTableExporterDirective;
  companiesdatasource: MatTableDataSource<Company> = null;
  products: Product[] = [];
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
    this.getAllCompanies();
  }

  public doFilter = (value: string, type: String) => {
    switch (type) {
      case 'product':
        this.dataSource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'category':
        this.companiesdatasource.filter = value.trim().toLocaleLowerCase();
        break;
      case 'company':
        this.companiesdatasource.filter = value.trim().toLocaleLowerCase();
        break;

    }
  }
  getAllcompanies(): any {
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
        this.messagebox("error getting companies");
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
        this.messagebox(data.message);
        this.getAllcompanies()
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
        this.messagebox("error getting companies");
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
            this.messagebox("Error deleting company, please make sure no products are in this company.");
          }
        );
      }
    })
  }


}


