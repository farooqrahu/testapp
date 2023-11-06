package com.rahu.springjwt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productreturns")
public class ProductReturn {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Nullable
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Customer customer;
  private Long invoiceNo;
  private Double grandTotal;
  private Long grandTotalQtReturn;
  private String detail;
  @OneToMany(mappedBy = "productReturn",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonBackReference
  private List<ProductReturnList> productReturnList;


}
