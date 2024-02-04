import {Pipe} from "@angular/core";

@Pipe({
  name: 'somepipe',
})
export class SaleListReturn {

  transform(objects: any[]): any[] {
    if(objects) {
      return objects.filter(object => {
        return object.data.isReturn === true;
      });
    }
  }

}
