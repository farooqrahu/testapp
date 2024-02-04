import { Company } from '../../models/compnay.model';
import { ProductService } from '../../_services/product.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from 'src/app/models/product.model';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
@Component({
  selector: 'app-companyform',
  templateUrl: './companyform.component.html',
  styleUrls: ['./companyform.component.css']
})
export class CompanyformComponent implements OnInit {

  companies: Company[];
  errors: String = "";
  constructor(
    public dialogRef: MatDialogRef<CompanyformComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private productservice: ProductService) { }
  getAllCompanies(): any {
    this.productservice.getAllCompanies().subscribe(
      data => {
        this.companies = data.companies;
      },
      err => {
        (err);
      }
    );
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  validate(data: any): boolean {
    this.errors = ""

    if (this.data.name == null || this.data.name == "")
      this.errors += "name must not be empty\n"
    return (this.errors == "")
  }
  save(data: any): any {
    return this.dialogRef.afterClosed()
  }

  ngOnInit() {
    this.getAllCompanies()();
    if (this.data.name == "")
      this.data.name = ""


  }

}
