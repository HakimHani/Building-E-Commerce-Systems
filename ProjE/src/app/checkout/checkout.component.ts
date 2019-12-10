import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {SharedService} from '../shared.service'
import { HttpClient } from "@angular/common/http";
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  orderDetails;
  cartDetails;
  totalPrice
  constructor(private sharedService: SharedService, private http: HttpClient, private router: Router) { }

  ngOnInit() {
    let url = "http://localhost:8000/cart?item=";
    this.http.get(url, {withCredentials: true}).subscribe((data) => {
      this.cartDetails = Object.values(data)
    });
    // this.orderDetails = JSON.parse(this.sharedService.getOrderDetails());
    this.orderDetails = JSON.parse(SharedService.getOrderDetails());
    let url1 = "http://localhost:8000/cartprice";
    this.http.get(url1, {withCredentials: true}).subscribe((data2) => {
      this.totalPrice = (data2);
      console.log(JSON.stringify(data2))
    })

    

  }
  
  

}
