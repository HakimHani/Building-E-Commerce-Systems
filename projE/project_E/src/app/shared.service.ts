import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class SharedService {
  public static orderDetails = {
    name : '',
    address1: '',
    address2: '',
    city : '',
    prov : '',
    zip : '',
    instructions : ''
  }

  constructor() { 
  }
  // updateOderDetails(name : string, address1: string, address2: string, city: string, prov: string, zip: string, instructions:string){
  public static updateOderDetails(form: any){
    form = JSON.parse(form);
    this.orderDetails.name = form.name;
    this.orderDetails.address1 = form.address1;
    this.orderDetails.address2 = form.address2;
    this.orderDetails.city = form.city;
    this.orderDetails.prov = form.prov;
    this.orderDetails.zip = form.zip;
    this.orderDetails.instructions = form.instructions;
  }

  public static getOrderDetails() {
    return JSON.stringify(this.orderDetails);
  }
} 