package com.rahu.springjwt.dto;

import com.rahu.springjwt.models.Customer;
import com.rahu.springjwt.models.ProductReturn;
import com.rahu.springjwt.models.ProductReturnList;
import com.rahu.springjwt.models.ProductSaleList;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReturnDto {

  private Long id;
  private Long invoiceNo;
  private Double grandTotal;
  private Long totalQuantityReturn;
  private String detail;
  private List<ProductReturnList> productReturnList;

  public ProductReturnDto factoryProductReturn(ProductReturn productReturn){
//    List<ProductReturnList> productReturnLists=productReturn.getProductReturnList();
    return ProductReturnDto.builder().id(productReturn.getId()).totalQuantityReturn(productReturn.getProductReturnList().stream().mapToLong(ProductReturnList::getTotalQuantityReturn).sum()).productReturnList(productReturn.getProductReturnList()).build();
  }


}
