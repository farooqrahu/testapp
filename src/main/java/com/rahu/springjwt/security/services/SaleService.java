package com.rahu.springjwt.security.services;

import com.rahu.springjwt.dto.DashboardDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.models.*;
import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.request.SaleRequest;
import com.rahu.springjwt.payload.request.SaleRequestList;
import com.rahu.springjwt.payload.response.MessageResponse;
import com.rahu.springjwt.payload.response.ProductResponse;
import com.rahu.springjwt.repository.*;
import com.rahu.springjwt.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SaleService {
  private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

  @Autowired
  ProductRepository productRepository;
  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  CompanyRepository companyRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ShoppingCartRepository shoppingcartRepository;
  @Autowired
  CartItemRepository cartItemRepository;
  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;
  @Autowired
  EmailSenderService emailSenderService;
  @Autowired
  private FileDBRepository fileDBRepository;
  @Value("${barcode.label}")
  private String barcodeLabel;
  @Autowired
  private ProductSaleRepository productSaleRepository;
  @Autowired
  private ProductReturnRepository productReturnRepository;
  @Autowired
  private ProductOrderRepository productOrderRepository;
  @Autowired
  private ReturnRepository returnRepository;
  @Autowired
  private CustomerRepository customerRepository;

  public ResponseEntity<?> submitSaleOrder(@Valid SaleRequestList productRequest) {
    userDetailsServiceImpl.checkAdmin();
    if (!productRequest.getData().isEmpty()) {
      Long invoiceNo = productOrderRepository.findMaxInvoiceNo();
      ProductOrder productOrder = productOrderRepository.save(ProductOrder.builder().id(0L).invoiceNo(invoiceNo == null ? 1 : invoiceNo + 1).build());
      AtomicReference<Float> grandTotal = new AtomicReference<>(0f);
      productRequest.getData().forEach(saleRequest -> {
        Optional<Product> product = productRepository.findById(saleRequest.getProductId());
        long totalQuantity = 0;
        if (product.isPresent()) {
          totalQuantity = saleRequest.getUserTotalQuantity();
          if (!product.get().isEnableTQ()) {
            if (saleRequest.getUserTotalQuantity() <= product.get().getExtraQuantity()) {
              product.get().setExtraQuantity(zeroIfNull(product.get().getExtraQuantity()) - zeroIfNull(saleRequest.getQuantity()));
            } else {
              long bundles = saleRequest.getUserQuantityBundle();
              long extra = saleRequest.getUserExtraQuantity();
              if (bundles > 0 && extra <= 0) {
                product.get().setQuantityBundle(product.get().getQuantityBundle() - bundles);
                totalQuantity = (bundles * product.get().getQuantityItem());
              } else {
                if (extra > product.get().getExtraQuantity()) {
                  bundles++;
                  long totalExtras = product.get().getExtraQuantity() + product.get().getQuantityItem();
                  if (totalExtras >= product.get().getQuantityItem()) {
                    long remaining = totalExtras - saleRequest.getUserExtraQuantity();
                    product.get().setExtraQuantity(remaining);
                  } else if (totalExtras < product.get().getExtraQuantity()) {
                    long remaining = product.get().getQuantityItem() - totalExtras;
                    product.get().setExtraQuantity(remaining);
                    product.get().setQuantityBundle(product.get().getQuantityBundle() - 1);
                  } else {
                    product.get().setExtraQuantity(product.get().getExtraQuantity() - saleRequest.getUserExtraQuantity());
                  }
                } else {
                  if (product.get().getExtraQuantity() > 0) {
                    product.get().setExtraQuantity(product.get().getExtraQuantity() - extra);
                  } else {
                    product.get().setExtraQuantity(extra);
                  }
                }
                product.get().setQuantityBundle(product.get().getQuantityBundle() - bundles);
              }
            }
          }
          product.get().setQuantity(product.get().getQuantity() - totalQuantity);
          if (product.get().getQuantity() <= 0) {
            product.get().setOutOfStock(Boolean.TRUE);
          }
          productRepository.save(product.get());
          productSaleRepository.save(ProductSaleList.builder().id(0L).totalQuantitySale(totalQuantity).bundleSale(saleRequest.getUserQuantityBundle()).extraSale(saleRequest.getUserExtraQuantity()).product(product.get()).productOrder(productOrder).build());
        }
      });
      productOrder.setGrandTotal(productRequest.getGrandTotal());
      Optional<Customer> customerFound = customerRepository.findByMobileNumber(productRequest.getMobileNumber());
      if (customerFound.isPresent()) {
        productOrder.setCustomer(customerFound.get());
      } else {
        Customer customer = Customer.builder().mobileNumber(productRequest.getMobileNumber()).name(productRequest.getCustomerName()).address(productRequest.getAddress()).build();
        productOrder.setCustomer(customer);
      }
      productOrderRepository.save(productOrder);
    }
    return ResponseEntity.ok(new

      MessageResponse("Sale order submitted successfully!"));
  }

  //  private static double[] separateFractional(double d) {
//    BigDecimal bd = new BigDecimal(d);
//    return new double[] { bd.intValue(),
//      bd.remainder(BigDecimal.ONE).doubleValue() };
//  }
  public ResponseEntity<?> findOrders(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null) {
      List<ProductOrderInvoiceDto> list = productOrderRepository.findAllByReturnedIsFalse().stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).filter(Objects::nonNull).collect(Collectors.toList());
//      list.sort(Comparator.comparing(ProductOrderInvoiceDto::getCreatedAt).reversed());
      return ResponseEntity.ok(new ProductResponse(list, ""));
    } else {
      List<ProductOrderInvoiceDto> list = productOrderRepository.findAllByReturnedIsFalse(paging).stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).filter(Objects::nonNull).collect(Collectors.toList());
//      list.sort(Comparator.comparing(ProductOrderInvoiceDto::getCreatedAt).reversed());
      return ResponseEntity.ok(new ProductResponse(list, ""));
    }

  }

  public Pageable checkPaging(ProductRequest productRequest) {
    if (productRequest.getPagesize() > 0 && productRequest.getPagenumber() >= 0) {
      if (Objects.equals(productRequest.getSortdirection(), "desc")) {
        return PageRequest.of(productRequest.getPagenumber(), productRequest.getPagesize(),
          Sort.by(productRequest.getSort()).descending());
      } else {
        return PageRequest.of(productRequest.getPagenumber(), productRequest.getPagesize(),
          Sort.by(productRequest.getSort()).ascending());
      }
    }
    return null;
  }

  public ResponseEntity<?> returnProductSale(@Valid SaleRequestList productReturnRequest) {
    userDetailsServiceImpl.checkAdmin();
    Optional<ProductOrder> productOrder = productOrderRepository.findById(productReturnRequest.getId());
    if (productOrder.isPresent()) {
      Optional<ProductReturn> productReturn = returnRepository.findByInvoiceNo(productOrder.get().getInvoiceNo());
      ProductReturn productReturnFound = productReturn.orElseGet(() -> ProductReturn.builder().id(0L).invoiceNo(productOrder.get().getInvoiceNo()).customer(productOrder.get().getCustomer()).grandTotalQtReturn(productReturnRequest.getGrandTotalQtReturn()).build());
      productReturnFound.setGrandTotal(productReturnRequest.getGrandTotal());
      ProductReturn productReturnSaved = returnRepository.save(productReturnFound);
      AtomicLong quantitySold = new AtomicLong();
      AtomicLong quantityReturned = new AtomicLong();

      productReturnRequest.getData().forEach(returnRequest -> {
        Optional<ProductSaleList> productSold = productSaleRepository.findById(returnRequest.getId());
        if (productSold.isPresent() && Objects.nonNull(productSold.get().getProduct())) {
          if (Objects.nonNull(productSold.get().getProduct())) {
            Product product = productSold.get().getProduct();
            if (!Objects.isNull(product)) {
              if (product.isEnableTQ()) {
                long totalQuantitySold = productSold.get().getTotalQuantitySale();
                long userTotalQuantity = returnRequest.getUserTotalQuantity();
                if (userTotalQuantity > 0 && userTotalQuantity <= productSold.get().getTotalQuantitySale()) {
                  long addReturnProduct = Objects.requireNonNull(product).getQuantity() + userTotalQuantity;
                  product.setQuantity(addReturnProduct);
                  if (product.getQuantity() > 0) { // check each product for stock
                    product.setOutOfStock(Boolean.FALSE);
                  } else {
                    product.setOutOfStock(Boolean.TRUE);
                  }
                  productRepository.save(Objects.requireNonNull(product));
                  productSold.get().setTotalQuantitySale(totalQuantitySold - userTotalQuantity);
                  productSaleRepository.save(productSold.get());
                }
                ProductReturnList productReturnList = ProductReturnList.builder().id(0L).product(productSold.get().getProduct()).totalQuantityReturn(returnRequest.getUserTotalQuantity()).productReturn(productReturnSaved).build();
                productReturnRepository.save(productReturnList);
              } else {
                long bundleReturn = returnRequest.getUserQuantityBundle();
                long extraReturn = returnRequest.getUserExtraQuantity();
                if ((extraReturn + product.getExtraQuantity()) == productSold.get().getProduct().getQuantityItem()) {
                  product.setQuantityBundle(product.getQuantityBundle() + 1);
                  product.setExtraQuantity(0L);
                } else if ((extraReturn + product.getExtraQuantity()) > productSold.get().getProduct().getQuantityItem()) {
                  product.setQuantityBundle(product.getQuantityBundle() + 1);
                  product.setExtraQuantity((extraReturn + product.getExtraQuantity() - productSold.get().getProduct().getQuantityItem()));
                } else {
                  product.setExtraQuantity(product.getExtraQuantity() + extraReturn);
                }
                product.setQuantityBundle(product.getQuantityBundle() + bundleReturn);
                product.setQuantity(zeroIfNull(product.getQuantity()) + returnRequest.getUserTotalQuantity());

                if (product.getQuantity() > 0) { // check each product for stock
                  product.setOutOfStock(Boolean.FALSE);
                }
                productRepository.save(Objects.requireNonNull(product));

                long bundleReturned = returnRequest.getUserQuantityBundle();
                long extraReturned = returnRequest.getUserExtraQuantity();
                if (productSold.get().getExtraSale() <= 0) {
                  if (extraReturned > 0) {
                    productSold.get().setExtraSale(extraReturned);
                  }
                } else if (extraReturned <= productSold.get().getExtraSale()) {
                  productSold.get().setExtraSale(productSold.get().getExtraSale() - extraReturned);
                } else {
                  productSold.get().setExtraSale(productSold.get().getExtraSale() + productSold.get().getProduct().getQuantityItem() - extraReturned);
                  productSold.get().setBundleSale(productSold.get().getBundleSale() - 1);

                  System.out.println("new cond");
                }
                productSold.get().setBundleSale(productSold.get().getBundleSale() - bundleReturned);
                productSold.get().setTotalQuantitySale(zeroIfNull(productSold.get().getTotalQuantitySale()) - returnRequest.getUserTotalQuantity());

                productSaleRepository.save(productSold.get());
                ProductReturnList productReturnList = ProductReturnList.builder().id(0L).product(productSold.get().getProduct()).bundleReturn(bundleReturned).extraReturn(extraReturn).totalQuantityReturn(returnRequest.getUserTotalQuantity()).productReturn(productReturnSaved).build();
                productReturnRepository.save(productReturnList);

              }
            }
          }
        }
        quantitySold.set(productSold.get().getTotalQuantitySale());
      });
      if (quantitySold.get() <= 0) {
        productOrder.get().setReturned(Boolean.TRUE);
      }
      productOrder.get().setGrandTotal(productReturnRequest.getGrandTotal());
      productOrderRepository.save(productOrder.get());

    }
    return ResponseEntity.ok(new MessageResponse("Ok"));

  }

  private void calculateBundleWise(SaleRequest returnRequest, Product product, Optional<ProductSaleList> productSale, long totalQuantity) {/*

    if (returnRequest.getUserTotalQuantity() <= product.getExtraQuantity()) {//returning quantity lese then extra quantity
      long totalEx = returnRequest.getUserTotalQuantity() + product.getExtraQuantity();
      if (totalEx > product.getQuantityItem()) {
        long totalExtras = totalEx - product.getQuantityItem();
        if (totalExtras < product.getExtraQuantity()) {
          product.setExtraQuantity(totalExtras);
          productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + returnRequest.getUserExtraQuantity());
          product.setQuantityBundle(product.getQuantityBundle() + 1);
        }
      } else {
        product.setExtraQuantity(product.getExtraQuantity() + returnRequest.getUserTotalQuantity());
        productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + returnRequest.getUserExtraQuantity());
      }
    } else {///with bundle and/or extra
      long bundles = returnRequest.getUserQuantityBundle();
      long extra = returnRequest.getUserExtraQuantity();
      if (bundles > 0 && (extra > 0 && extra < product.getQuantityItem()) && extra < product.getExtraQuantity()) {
        long totalEx = extra + product.getExtraQuantity();
        if (totalEx > product.getQuantityItem()) {
          long totalExtras = totalEx - product.getQuantityItem();
          if (totalExtras < product.getExtraQuantity()) {
            bundles++;
            product.setExtraQuantity(totalExtras);
            productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + returnRequest.getUserExtraQuantity());
            product.setQuantityBundle(product.getQuantityBundle() + bundles);
          }
        } else {
          product.setExtraQuantity(product.getExtraQuantity() + extra);
          productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + extra);
          product.setQuantityBundle(product.getQuantityBundle() + bundles);
          productSale.get().setBundleReturn(zeroIfNull(bundles));
        }
      } else if (bundles > 0 && (extra > 0 && extra < product.getQuantityItem()) && extra > product.getExtraQuantity()) {
        product.setQuantityBundle(product.getQuantityBundle() + bundles);
        productSale.get().setBundleReturn(zeroIfNull(bundles));
        long totalEx = extra + product.getExtraQuantity();
        if (totalEx > product.getQuantityItem()) {
          long totalExtras = totalEx - product.getQuantityItem();
          if (totalExtras < product.getExtraQuantity()) {
            product.setExtraQuantity(totalExtras);
            productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + totalExtras);
            product.setQuantityBundle(product.getQuantityBundle() + bundles);
          }
        } else {
          if (product.getExtraQuantity() > 0) {
            product.setExtraQuantity(product.getExtraQuantity() + totalEx);
          } else {
            product.setExtraQuantity(totalEx);
          }
          productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + totalEx);
        }
      } else if (bundles > 0 && extra <= 0) {
        product.setQuantityBundle(product.getQuantityBundle() + bundles);
        productSale.get().setBundleReturn(zeroIfNull(bundles));
        totalQuantity = (bundles * product.getQuantityItem());
      } else if (extra > 0 && bundles <= 0) {
        long totalEx = returnRequest.getUserTotalQuantity() + product.getExtraQuantity();
        if (totalEx > product.getQuantityItem()) {
          long totalExtras = totalEx - product.getQuantityItem();
          if (totalExtras < product.getExtraQuantity()) {
            product.setExtraQuantity(totalExtras);
            productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + returnRequest.getUserExtraQuantity());
            product.setQuantityBundle(product.getQuantityBundle() + 1);
          }
        } else {
          product.setExtraQuantity(product.getExtraQuantity() + extra);
          productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + extra);
        }
      } else {
        product.setQuantityBundle(product.getQuantityBundle() + bundles);
        productSale.get().setBundleReturn(zeroIfNull(bundles));
        long totalEx = returnRequest.getUserTotalQuantity() + product.getExtraQuantity();
        if (totalEx > product.getQuantityItem()) {
          long totalExtras = totalEx - product.getQuantityItem();
          if (totalExtras < product.getExtraQuantity()) {
            product.setExtraQuantity(totalExtras);
            productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + returnRequest.getUserExtraQuantity());
            product.setQuantityBundle(product.getQuantityBundle() + 1);
          }
        } else {
          product.setExtraQuantity(product.getExtraQuantity() + extra);
          productSale.get().setExtraReturn(zeroIfNull(productSale.get().getExtraReturn()) + extra);
        }

      }

    }*/
  }

  public long zeroIfNull(Long bonus) {
    return Optional.ofNullable(bonus).orElse(0L);
  }


  public ResponseEntity<?> findCustomerByMobileNumber(SaleRequestList productRequest) {
    Optional<Customer> customerFound = customerRepository.findByMobileNumber(productRequest.getMobileNumber());
    if (customerFound.isPresent()) {
      return ResponseEntity.ok(customerFound);
    }
    return ResponseEntity.badRequest().body(new MessageResponse("Error: Customer not found!"));
  }

  public DashboardDto totalSales() {
//    String strDate=Utility.formatDate(new Date(),"yyyy-MM-dd");
//    LocalDateTime dateTimeFrom=Utility.parseStringToLocalDateTime(strDate);
//    LocalDateTime dateTimeTo=Utility.parseStringToLocalDateTime(strDate);
    LocalDate dateTimeFrom=LocalDate.now();
    LocalDate dateTimeTo=LocalDate.now().plusDays(1);
    //default time zone
    ZoneId defaultZoneId = ZoneId.systemDefault();

    List<ProductOrder> list = productOrderRepository.findAllByNotReturned(Date.from(dateTimeFrom.atStartOfDay(defaultZoneId).toInstant()),Date.from(dateTimeTo.atStartOfDay(defaultZoneId).toInstant()));
    DashboardDto dashboardDto = new DashboardDto();
    AtomicReference<Double> todayAmount = new AtomicReference<>(0.0);
    AtomicReference<Double> todayReturnAmount = new AtomicReference<>(0.0);
    AtomicReference<Long> todaySaleCount = new AtomicReference<>(0L);
    AtomicReference<Long> todayReturnCount = new AtomicReference<>(0L);
    list.forEach(productOrder -> {
      Double totalAmount = productOrder.getGrandTotal() == null ? 0 : productOrder.getGrandTotal();
      if(!productOrder.isReturned()){
        todayAmount.updateAndGet(v -> v + totalAmount);
        todaySaleCount.updateAndGet(v -> v +1);
      }else{
        todayReturnAmount.updateAndGet(v -> v + totalAmount);
        todayReturnCount.updateAndGet(v -> v +1);
      }
    });
    dashboardDto.setTodaySaleCount(todaySaleCount.get());
    dashboardDto.setTodayAmount(todayAmount.get());

    dashboardDto.setTodayReturnCount(todayReturnCount.get());
    dashboardDto.setTodayReturnAmount(todayReturnAmount.get());

    List<ProductOrder> list2 = productOrderRepository.findAllByNotReturned();
    AtomicReference<Double> grandAmount = new AtomicReference<>(0.0);
    list2.forEach(productOrder -> {
      Double grandTotal = productOrder.getGrandTotal() == null ? 0 : productOrder.getGrandTotal();
      grandAmount.updateAndGet(v -> v + grandTotal);
    });
    dashboardDto.setTotalSaleCount((long)list2.size());
    dashboardDto.setTotalAmount(grandAmount.get());

    return dashboardDto;
  }
}
