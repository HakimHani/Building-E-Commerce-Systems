import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { Router } from '@angular/router';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {
id
category
viewUrl
  constructor(private url: ActivatedRoute, private http: HttpClient, private router: Router) { 
    this.url.queryParams.subscribe(params =>
      {
        this.id = params['id'];
      });
      this.viewUrl = this.router.url;
  }

  ngOnInit() {
    let url = "http://localhost:8000/list?id=" + this.id;  
	  this.http.get(url).subscribe(data => {
      this.category = data
	  });
  }

  shipTo() {
    this.router.navigateByUrl('/shipto?viewUrl:'+ this.viewUrl)
  }

}
