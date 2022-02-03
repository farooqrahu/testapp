import { Category } from './../../models/category.model';
import { ProductService } from './../../_services/product.service';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from 'src/app/models/product.model';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
@Component({
  selector: 'app-barcode',
  templateUrl: './barcode.component.html',
  styleUrls: ['./barcode.component.css']
})
export class BarcodeComponent implements OnInit {

  categories: Category[];
  errors: String = "";
  constructor(
    public dialogRef: MatDialogRef<BarcodeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private productservice: ProductService) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
  onClick(): void {
   window.print();
  }
  validate(data: any): boolean {
    this.errors = ""
    return (this.errors == "")
  }
  save(data: any): any {
    return this.dialogRef.afterClosed()
  }

  ngOnInit() {

  }

}
