import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { Router } from '@angular/router';
import { SharedService } from '../shared.service'

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  providers: [SharedService]
})
export class CartComponent implements OnInit {
  itemParam
  cart
  viewUrl
  lastIndex
  id
  orderDetails
  constructor(private sharedService: SharedService, private url: ActivatedRoute, private http: HttpClient, private router: Router) { 
    this.url.queryParams.subscribe(params => {
        this.itemParam = params['item'];
        if (this.itemParam != '') { this.id = (JSON.parse(params['item']).id) }
    });
      this.viewUrl = this.router.url; 
  }
  
  ngOnInit(){
    let url = "http://localhost:8000/cart?item=" + this.itemParam;        
    this.http.get(url, {withCredentials: true}).subscribe(data => {
      this.cart = data
      this.orderDetails = SharedService.getOrderDetails() 
      this.orderDetails = JSON.parse(this.orderDetails)  
    });
  }

  changeQty(item, sign){
    if(sign == "-"){
      item.qty = -1
    }
    let url = "http://localhost:8000/cart?item=" + JSON.stringify(item);
    this.http.get(url, {withCredentials: true}).subscribe(data => {
      this.cart = data
    });
  }

  goTo() {
    // console.log(SharedService.getOrderDetails()) 
    if (this.orderDetails.name == "") this.router.navigateByUrl('/shipto')
    else this.router.navigateByUrl('/checkout')
    
    
  }

}
