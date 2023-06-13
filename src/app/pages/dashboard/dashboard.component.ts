import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js';

// core components
import {
  chartOptions,
  parseOptions,
  chartExample1,
  chartExample2
} from "../../variables/charts";
import {MatTableDataSource} from "@angular/material/table";
import {ProductService} from "../../_services/product.service";
import {TokenStorageService} from "../../_services/token-storage.service";
import {MatDialog} from "@angular/material/dialog";
import {DashboardService} from "../../_services/dashboard.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public datasets: any;
  public data: any;
  public salesChart;
  public clicked: boolean = true;
  public clicked1: boolean = false;
  public totalSales=0;
  public totalAmount=0;
  public grandSales=0;
  public grandAmount=0;
  public params;
  public currentDate=new Date();
  constructor(public dashboardService: DashboardService, private token: TokenStorageService
    , public dialog: MatDialog
              // ,@Inject(DOCUMENT) document:Document
  ) { }
  ngOnInit() {
    this.getTotalSales();

    this.datasets = [
      [0, 20, 10, 30, 15, 40, 20, 60, 60],
      [0, 20, 5, 25, 10, 30, 15, 40, 40]
    ];
    this.data = this.datasets[0];


    var chartOrders = document.getElementById('chart-orders');

    parseOptions(Chart, chartOptions());


    var ordersChart = new Chart(chartOrders, {
      type: 'bar',
      options: chartExample2.options,
      data: chartExample2.data
    });

    var chartSales = document.getElementById('chart-sales');

    this.salesChart = new Chart(chartSales, {
      type: 'line',
      options: chartExample1.options,
      data: chartExample1.data
    });
  }
  getTotalSales() {

    this.dashboardService.getTotalSales().subscribe(
      data => {
        debugger
        // console.log("dashboardService")
        // console.log(data)
          this.params =  JSON.parse(data);
        this.totalSales=this.params.totalSales;
        this.totalAmount=this.params.totalAmount;
        this.grandSales=this.params.grandSales;
        this.grandAmount=this.params.grandAmount;
      },
      err => {
        (err);
      }
    );
  }

  public updateOptions() {
    this.salesChart.data.datasets[0].data = this.data;
    this.salesChart.update();
  }

}
