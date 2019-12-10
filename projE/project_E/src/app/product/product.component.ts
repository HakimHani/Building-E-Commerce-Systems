import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { ItemObj } from '../itemObj.model';
import { Router } from '@angular/router';


@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent implements OnInit {
  id
  product
  productId
  catId
  productName
  productDescription
  productQty
  productCost
  productMSRP
  productVenId
  itemObj
  itemString
  viewUrl
  constructor(private url: ActivatedRoute, private http: HttpClient, private router: Router) { 
    this.url.queryParams.subscribe(params =>
      {
        this.id = params['id'];
      });
      this.viewUrl = this.router.url;  
  }

  ngOnInit() {
    let url = "http://localhost:8000/product?id=" + this.id;   
	  this.http.get(url).subscribe(data => {
      this.product = data[0]
      this.productId = data[0].id
      this.catId = data[0].catId
      this.productName = data[0].name
      this. productDescription = data[0].descripttion
      this.productQty = data[0].qty
      this.productCost = data[0].cost
      this.productMSRP = data[0].msrp
      this.productVenId = data[0].venId
      this.itemObj = {
        id : this.product.id,
        price : this.product.cost,
        qty : this.product.qty
      }
      this.itemString = JSON.stringify(this.itemObj)
	  });
  }

  shipTo() {
    this.router.navigateByUrl('/shipto?viewUrl='+ this.viewUrl)
  }
  
  
  
}
