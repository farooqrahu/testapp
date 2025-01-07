package com.rahu.springjwt.security.services;

import com.rahu.springjwt.dto.AccountTypes;
import com.rahu.springjwt.dto.DashboardDto;
import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.dto.ProductReturnDto;
import com.rahu.springjwt.models.*;
import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.request.SaleRequestList;
import com.rahu.springjwt.payload.response.MessageResponse;
import com.rahu.springjwt.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
  private final ProductSaleRepository productSaleRepository;
  private final ProductReturnListRepository productReturnListRepository;
  private final ProductOrderRepository productOrderRepository;
  private final ProductReturnRepository productReturnRepository;
  private final CustomerRepository customerRepository;
  private final GeneralLedgerRepository generalLedgerRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public SaleService(ProductSaleRepository productSaleRepository, ProductReturnListRepository productReturnListRepository, ProductOrderRepository productOrderRepository, ProductReturnRepository productReturnRepository, CustomerRepository customerRepository, GeneralLedgerRepository generalLedgerRepository, AccountRepository accountRepository) {
    this.productSaleRepository = productSaleRepository;
    this.productReturnListRepository = productReturnListRepository;
    this.productOrderRepository = productOrderRepository;
    this.productReturnRepository = productReturnRepository;
    this.customerRepository = customerRepository;
    this.generalLedgerRepository = generalLedgerRepository;
    this.accountRepository = accountRepository;
  }


  public ResponseEntity<?> submitSaleOrder(@Valid SaleRequestList productRequest) {
    long invoiceNumber = saveProductOrder(productRequest);
    if (invoiceNumber > 0) {
      return ResponseEntity.ok(findOrderById(invoiceNumber));
    } else {
      return ResponseEntity.ok(new MessageResponse(""));

    }
//    ;

  }


  public Long saveProductOrder(SaleRequestList productRequest) {
    userDetailsServiceImpl.checkAdmin();
    Long invoiceNumber = 0L;
    if (!productRequest.getData().isEmpty()) {
      Long invoiceNo = productOrderRepository.findMaxInvoiceNo();
      invoiceNumber = invoiceNo == null ? 1 : invoiceNo + 1;
      ProductOrder productOrder = productOrderRepository.save(ProductOrder.builder().id(0L).invoiceNo(invoiceNumber).build());
      productRequest.getData().forEach(saleRequest -> {
        Optional<Product> product = productRepository.findById(saleRequest.getProductId());
        long totalQuantity = 0;
        if (product.isPresent()) {
          totalQuantity = saleRequest.getUserTotalQuantity();
          if (!product.get().isEnableTQ()) {
            if (saleRequest.getUserTotalQuantity() <= product.get().getExtraQuantity()) {
              product.get().setExtraQuantity(zeroIfNull(product.get().getExtraQuantity()) - zeroIfNull(saleRequest.getUserTotalQuantity()));
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
          productSaleRepository.save(ProductSaleList.builder().id(0L).priceSelected(saleRequest.getPriceSelected()).totalQuantitySale(totalQuantity).bundleSale(saleRequest.getUserQuantityBundle()).extraSale(saleRequest.getUserExtraQuantity()).product(product.get()).productOrder(productOrder).build());
        }
      });
      productOrder.setGrandTotal(productRequest.getGrandTotal());
      Long customerId = null;
      if (productRequest.getCustomerId() != null && productRequest.getCustomerId() > 0) {
        customerId = productRequest.getCustomerId();
      }
      Optional<Customer> customerFound = Optional.empty();
      if (customerId != null) {
        customerFound = customerRepository.findById(customerId);
      }
      if (customerFound.isPresent()) {
        productOrder.setCustomer(customerFound.get());
      } else {

        Optional<Customer> customerFoundByMsisdn = customerRepository.findByMobileNumber(productRequest.getMobileNumber());
        if (customerFoundByMsisdn.isPresent()) {
          productOrder.setCustomer(customerFoundByMsisdn.get());
        } else {
          Customer customer = Customer.builder().customerCode(productRequest.getCustomerName().toUpperCase(Locale.ROOT).substring(0, 2) + "" + productOrder.getInvoiceNo()).mobileNumber(productRequest.getMobileNumber()).name(productRequest.getCustomerName()).address(productRequest.getAddress()).build();
          productOrder.setCustomer(customer);
        }
      }
      Optional<Account> account;

      productOrderRepository.save(productOrder);
      account = accountRepository.findByName(AccountTypes.AccountsEnum.CASH.getDescription());
      GeneralLedger ledgerEntry = new GeneralLedger();
      ledgerEntry.setDescription("Cash Account");
      account.ifPresent(ledgerEntry::setAccount);
      ledgerEntry.setDebit(productRequest.getAmountReceived());
      ledgerEntry.setProductOrder(productOrder);
      ledgerEntry.setCustomer(productOrder.getCustomer());
      Double finalLedgerEntry1 = ledgerEntry.getDebit();
      account.ifPresent(account1 -> account1.addBalance(BigDecimal.valueOf(finalLedgerEntry1)));
      updateGeneralLedger(account, ledgerEntry);

      account = accountRepository.findByName(AccountTypes.AccountsEnum.SALES_REVENUE.getDescription());
      ledgerEntry = new GeneralLedger();
      ledgerEntry.setDescription("Sale Revenue");
      account.ifPresent(ledgerEntry::setAccount);
      ledgerEntry.setCredit(productRequest.getGrandTotal());
      ledgerEntry.setProductOrder(productOrder);
      ledgerEntry.setCustomer(productOrder.getCustomer());
      Double finalLedgerEntry2 = ledgerEntry.getCredit();
      account.ifPresent(account1 -> account1.addBalance(BigDecimal.valueOf(finalLedgerEntry2)));
      updateGeneralLedger(account, ledgerEntry);

      if (productRequest.getAmountReceived() < productRequest.getGrandTotal()) {
        account = accountRepository.findByName(AccountTypes.AccountsEnum.ACCOUNTS_RECEIVABLE.getDescription());
        ledgerEntry = new GeneralLedger();
        ledgerEntry.setDescription("Accounts Receivable");
        account.ifPresent(ledgerEntry::setAccount);
        ledgerEntry.setDebit(productRequest.getGrandTotal() - productRequest.getAmountReceived());
        ledgerEntry.setProductOrder(productOrder);
        ledgerEntry.setCustomer(productOrder.getCustomer());
        Double finalLedgerEntry = ledgerEntry.getDebit();
        account.ifPresent(account1 -> account1.addBalance(BigDecimal.valueOf(finalLedgerEntry)));
        updateGeneralLedger(account, ledgerEntry);
      }


    }
    return invoiceNumber;
  }

  private void updateGeneralLedger(Optional<Account> account, GeneralLedger ledgerEntry) {
    generalLedgerRepository.save(ledgerEntry);
    account.ifPresent(accountRepository::save);
  }


  public ResponseEntity<?> findOrders(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    Page<ProductOrder> productOrderPage = productOrderRepository.findAllByReturnedIsFalse(paging);
    return ResponseEntity.ok(new ProductOrderInvoiceDto(productOrderPage));

  }

  public ProductOrderInvoiceDto findOrderById(Long id) {
    List<ProductOrder> productOrderPage = productOrderRepository.findByIdReturnedIsFalse(id);
    productOrderPage.forEach(productOrder -> {
      productOrder.setProductSaleLists(productSaleRepository.findAllByProductOrderId(productOrder.getId()));
    });
    return new ProductOrderInvoiceDto(productOrderPage);

  }


  public ResponseEntity<?> findReturnOrdersByInvoiceNo(ProductRequest productRequest) {
    Optional<ProductReturn> productReturn = productReturnRepository.findByInvoiceNo(productRequest.getInvoiceNo());
    if (productReturn.isPresent()) {
      ProductReturnDto productReturnDto = new ProductReturnDto().factoryProductReturn(productReturn.get());
      return ResponseEntity.ok(productReturnDto);
    } else
      return ResponseEntity.ok(HttpEntity.EMPTY);
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

  @Transactional
  public ResponseEntity<?> returnProductSale(@Valid SaleRequestList productReturnRequest) {
    userDetailsServiceImpl.checkAdmin();
    Optional<ProductOrder> productOrder = productOrderRepository.findById(productReturnRequest.getId());
    if (productOrder.isPresent()) {
      Optional<ProductReturn> productReturn = productReturnRepository.findByInvoiceNo(productOrder.get().getInvoiceNo());
      ProductReturn productReturnFound = productReturn.orElseGet(() -> ProductReturn.builder().id(0L).invoiceNo(productOrder.get().getInvoiceNo()).customer(productOrder.get().getCustomer()).grandTotalQtReturn(productReturnRequest.getGrandTotalQtReturn()).build());
      ProductReturn productReturnSaved = productReturnRepository.save(productReturnFound);

      AtomicReference<Double> grandTotalAmount = new AtomicReference<>(0.0);
      AtomicReference<Double> grandTotalBundleWiseAmount = new AtomicReference<>(0.0);
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
                if (returnRequest.getUserTotalQuantity() > 0) {
                  ProductReturnList productReturnList = ProductReturnList.builder().id(0L).product(productSold.get().getProduct().getId()).productName(productSold.get().getProduct().getName()).totalQuantityReturn(returnRequest.getUserTotalQuantity()).productReturn(productReturnSaved).build();
                  productReturnListRepository.save(productReturnList);
                }
                if (returnRequest.getUserTotalQuantity() > 0) {
                  if (productSold.get().getPriceSelected().equals("Retail")) {
                    grandTotalAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getRetailPrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  } else if (productSold.get().getPriceSelected().equals("Whole")) {
                    grandTotalAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getWholeSalePrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  } else {
                    grandTotalAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getPrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  }
                }
              } else {
                long bundleReturn = returnRequest.getUserQuantityBundle();
                long extraReturn = returnRequest.getUserExtraQuantity();
                if ((extraReturn + product.getExtraQuantity()) == Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()) {
                  if ((extraReturn + product.getExtraQuantity()) == Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()) {
                    product.setQuantityBundle(product.getQuantityBundle() + 1);
                    product.setExtraQuantity((extraReturn + product.getExtraQuantity() - Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()));
                  } else {
                    product.setQuantityBundle(product.getQuantityBundle() + 1);
                    product.setExtraQuantity(0L);
                  }
                } else if ((extraReturn + product.getExtraQuantity()) > Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()) {
                  product.setQuantityBundle(product.getQuantityBundle() + 1);
                  product.setExtraQuantity((extraReturn + product.getExtraQuantity() - Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()));
                } else {
                  product.setExtraQuantity(product.getExtraQuantity() + extraReturn);
                }
                product.setQuantityBundle(product.getQuantityBundle() + bundleReturn);
                product.setQuantity(zeroIfNull(product.getQuantity()) + returnRequest.getUserTotalQuantity());

                if (product.getQuantity() > 0) { // check each product for stock
                  product.setOutOfStock(Boolean.FALSE);
                }
                productRepository.save(Objects.requireNonNull(product));
//                27-11-2023
                if (bundleReturn > 0 && extraReturn > 0) {
                  if (bundleReturn == productSold.get().getBundleSale() && extraReturn == productSold.get().getExtraSale()) {
                    productSold.get().setBundleSale(0L);
                    productSold.get().setExtraSale(0L);
                  } else {
                    if (extraReturn <= productSold.get().getExtraSale()) {
                      if (productSold.get().getBundleSale() > 0) {
                        productSold.get().setBundleSale(productSold.get().getBundleSale() - bundleReturn);
                      }
                      productSold.get().setExtraSale(productSold.get().getExtraSale() - extraReturn);
                    } else if (extraReturn > productSold.get().getExtraSale() && (extraReturn + productSold.get().getExtraSale()) < Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()) {
                      productSold.get().setBundleSale(productSold.get().getBundleSale() - 1);
                      productSold.get().setExtraSale(((extraReturn + productSold.get().getExtraSale()) - Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()));
                    } else {
                      if ((extraReturn + productSold.get().getExtraSale()) > Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()) {
                        if (productSold.get().getBundleSale() > 0) {
                          productSold.get().setBundleSale(productSold.get().getBundleSale() - 1);
                          productSold.get().setBundleSale(productSold.get().getBundleSale() - bundleReturn);
                        }
                        productSold.get().setExtraSale(Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem() + productSold.get().getExtraSale() - extraReturn);
                      }
                    }
                  }

                } else if (bundleReturn > 0) {
                  if (bundleReturn == productSold.get().getBundleSale()) {
                    productSold.get().setBundleSale(0L);
                  } else {
                    if (productSold.get().getBundleSale() > 0) {
                      productSold.get().setBundleSale(productSold.get().getBundleSale() - bundleReturn);
                    } else {
                      productSold.get().setBundleSale(0L);
                    }
                  }
                } else if (extraReturn > 0) {
                  if (extraReturn == productSold.get().getExtraSale()) {
                    productSold.get().setExtraSale(0L);
                  } else {
                    if (productSold.get().getExtraSale() > 0) {
                      if (extraReturn > productSold.get().getExtraSale()) {
                        productSold.get().setBundleSale(productSold.get().getBundleSale() - 1);
                        long totalExt = (extraReturn + productSold.get().getExtraSale());
                        if (totalExt >= productSold.get().getProduct().getQuantityItem()) {
                          productSold.get().setExtraSale((totalExt - Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem()));
                        } else {
                          long rettt = Objects.requireNonNull(productSold.get().getProduct()).getQuantityItem() - (extraReturn - productSold.get().getExtraSale());
                          productSold.get().setExtraSale(rettt);
                        }
                      } else {
                        productSold.get().setExtraSale(productSold.get().getExtraSale() - extraReturn);
                      }
                    } else {
                      productSold.get().setExtraSale(extraReturn);

                    }
                  }
                }

                productSold.get().setTotalQuantitySale(zeroIfNull(productSold.get().getTotalQuantitySale()) - returnRequest.getUserTotalQuantity());
                if (returnRequest.getUserTotalQuantity() > 0) {
                  ProductReturnList productReturnList = ProductReturnList.builder().id(0L).
                    product(productSold.get().getProduct().getId()).
                    productName(productSold.get().getProduct().getName()).
                    bundleReturn(bundleReturn).
                    extraReturn(extraReturn).
                    totalQuantityReturn(returnRequest.getUserTotalQuantity()).
                    productReturn(productReturnSaved).build();
                  productReturnListRepository.save(productReturnList);
                }
                if (returnRequest.getUserTotalQuantity() > 0) {
                  if (productSold.get().getPriceSelected().equals("Retail")) {
                    grandTotalBundleWiseAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getRetailPrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  } else if (productSold.get().getPriceSelected().equals("Whole")) {
                    grandTotalBundleWiseAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getWholeSalePrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  } else {
                    grandTotalBundleWiseAmount.updateAndGet(v -> v + Optional.of(Objects.requireNonNull(productSold.get().getProduct()).getPrice() * returnRequest.getUserTotalQuantity()).orElse(0.0));
                  }
                }
              }
            }
          }
        }


      });
      productReturnSaved.setGrandTotal(productOrder.get().getGrandTotal() - (grandTotalAmount.get() + grandTotalBundleWiseAmount.get()));
      productReturnSaved.setGrandTotalQtReturn(productReturnRequest.getGrandTotalQtReturn());
      productReturnRepository.save(productReturnSaved);

      productOrder.get().setGrandTotal(productReturnSaved.getGrandTotal());
      productOrderRepository.save(productOrder.get());

      GeneralLedger ledgerEntry = new GeneralLedger();
      ledgerEntry.setDescription("Return");
      ledgerEntry.setDebit(productOrder.get().getGrandTotal());
      ledgerEntry.setProductOrder(productOrder.get());
      ledgerEntry.setCustomer(productOrder.get().getCustomer());
      generalLedgerRepository.save(ledgerEntry);


    }
    return ResponseEntity.ok(new MessageResponse("Ok"));

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
    LocalDate dateTimeFrom = LocalDate.now();
    LocalDate dateTimeTo = LocalDate.now().plusDays(1);
    //default time zone
    ZoneId defaultZoneId = ZoneId.systemDefault();

    List<ProductOrder> list = productOrderRepository.findAllByNotReturned(Date.from(dateTimeFrom.atStartOfDay(defaultZoneId).toInstant()), Date.from(dateTimeTo.atStartOfDay(defaultZoneId).toInstant()));
    DashboardDto dashboardDto = new DashboardDto();
    List<Customer> all = customerRepository.findAll();
    AtomicReference<Double> todayAmount = new AtomicReference<>(0.0);
    AtomicReference<Double> todayReturnAmount = new AtomicReference<>(0.0);
    AtomicReference<Long> todaySaleCount = new AtomicReference<>(0L);
    AtomicReference<Long> todayReturnCount = new AtomicReference<>(0L);
    list.forEach(productOrder -> {
      Double totalAmount = productOrder.getGrandTotal() == null ? 0 : productOrder.getGrandTotal();
      if (!productOrder.isReturned()) {
        todayAmount.updateAndGet(v -> v + totalAmount);
        todaySaleCount.updateAndGet(v -> v + 1);
      } else {
        todayReturnAmount.updateAndGet(v -> v + totalAmount);
        todayReturnCount.updateAndGet(v -> v + 1);
      }
    });
    dashboardDto.setTodaySaleCount(todaySaleCount.get());
    dashboardDto.setTodayAmount(todayAmount.get());
    dashboardDto.setTotalCustomers((long) all.size());

    dashboardDto.setTodayReturnCount(todayReturnCount.get());
    dashboardDto.setTodayReturnAmount(todayReturnAmount.get());

    List<ProductOrder> list2 = productOrderRepository.findAllByNotReturned();
    AtomicReference<Double> grandAmount = new AtomicReference<>(0.0);
    list2.forEach(productOrder -> {
      Double grandTotal = productOrder.getGrandTotal() == null ? 0 : productOrder.getGrandTotal();
      grandAmount.updateAndGet(v -> v + grandTotal);
    });
    dashboardDto.setTotalSaleCount((long) list2.size());
    dashboardDto.setTotalAmount(grandAmount.get());

    return dashboardDto;
  }
}
