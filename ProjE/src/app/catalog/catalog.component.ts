import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Router } from '@angular/router';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit {
  catalog
  viewUrl
  constructor(private http: HttpClient, private router: Router) { 
    this.viewUrl = this.router.url;
  }
    
  ngOnInit() {
    let url = "http://localhost:8000/catalog";
      this.http.get(url).subscribe(data => {
      this.catalog = data
	  });
  }

  shipTo() {
    this.router.navigateByUrl('/shipto?'+ this.viewUrl)
  }

}

