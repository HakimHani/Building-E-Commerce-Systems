import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { ActivatedRoute, Router } from "@angular/router";
import { NgForm } from '@angular/forms';
import { SharedService } from '../shared.service';

@Component({
  selector: 'app-ship-to',
  templateUrl: './ship-to.component.html',
  styleUrls: ['./ship-to.component.css']
})
export class ShipToComponent implements OnInit {
  @ViewChild('f', {static: false}) form: NgForm
  viewUrl
  orderDetails
  name
  address1
  address2
  city
  prov
  zip
  instructions
  
  constructor(private sharedService: SharedService, private http: HttpClient, private url: ActivatedRoute, private router: Router) { 
    this.url.queryParams.subscribe(params => {
      this.viewUrl = params['viewUrl'];
    });
  }

  ngOnInit() {
    // alert("The view is of type: " + (this.viewUrl)) 
    this.orderDetails = SharedService.getOrderDetails() 
    this.orderDetails = JSON.parse(this.orderDetails) 
    this.name = this.orderDetails.name;
    this.address1 = this.orderDetails.address1;
    this.address2 = this.orderDetails.address2;
    this.city = this.orderDetails.city;
    this.prov = this.orderDetails.prov;
    this.zip = this.orderDetails.zip;
    this.instructions = this.orderDetails.instructions;     
  }

  oncheckOut(){
    // this.orderDetails.name = this.form.value.name;
    // this.orderDetails.address1 = this.form.value.address1;
    // this.orderDetails.address2 = this.form.value.address2;
    // this.orderDetails.city = this.form.value.city;
    // this.orderDetails.prov = this.form.value.prov;
    // this.orderDetails.zip = this.form.value.zip;
    // this.orderDetails.instructions = this.form.value.instructions;
    // this.parent = this.orderDetails; 
    // this.sharedService.updateOderDetails(JSON.stringify(this.form.value));
    SharedService.updateOderDetails(JSON.stringify(this.form.value));
    this.router.navigateByUrl('/checkout')

  }

  // onSubmit(form: NgForm){
  //   console.log(this.form)
  // }
}
