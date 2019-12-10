import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http'; 
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { CatalogComponent } from './catalog/catalog.component';
import { CategoryComponent } from './category/category.component';
import { ProductComponent } from './product/product.component';
import { CartComponent } from './cart/cart.component';
import { ShipToComponent } from './ship-to/ship-to.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { FormsModule } from '@angular/forms';
import { EndComponent } from './end/end.component';


const urlMap: Routes =
[
	{ path: '', redirectTo: '/catalog', pathMatch: "full" },
	// { path: 'app', component: AppComponent },
  { path: 'category', component: CategoryComponent},
  { path: 'catalog', component: CatalogComponent },
  { path: 'product', component: ProductComponent },
  { path: 'cart', component: CartComponent },
  { path: 'shipto', component: ShipToComponent},
  { path: 'checkout', component: CheckoutComponent},
  { path: 'end', component: EndComponent },
 

];

@NgModule({
  declarations: [
    AppComponent,
    CatalogComponent,
    CategoryComponent,
    ProductComponent,
    CartComponent,
    ShipToComponent,
    CheckoutComponent,
    EndComponent
    
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(urlMap)
  ],
  providers: [],
  bootstrap: [AppComponent]
})


export class AppModule { }
