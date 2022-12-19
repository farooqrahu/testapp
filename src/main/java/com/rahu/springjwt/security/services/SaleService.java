package com.rahu.springjwt.security.services;

import com.rahu.springjwt.dto.ProductOrderInvoiceDto;
import com.rahu.springjwt.models.Product;
import com.rahu.springjwt.models.ProductOrder;
import com.rahu.springjwt.models.ProductReturn;
import com.rahu.springjwt.models.ProductSale;
import com.rahu.springjwt.payload.request.ProductRequest;
import com.rahu.springjwt.payload.request.SaleRequest;
import com.rahu.springjwt.payload.request.SaleRequestList;
import com.rahu.springjwt.payload.response.MessageResponse;
import com.rahu.springjwt.payload.response.ProductResponse;
import com.rahu.springjwt.repository.*;
import com.rahu.springjwt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SaleService {
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
  private SaleRepository saleRepository;
  @Autowired
  private ProductOrderRepository productOrderRepository;
  @Autowired
  private ReturnRepository returnRepository;

  public ResponseEntity<?> submitSaleOrder(@Valid SaleRequestList productRequest) {
    userDetailsServiceImpl.checkAdmin();
    if (!productRequest.getData().isEmpty()) {
      ProductOrder productOrder = productOrderRepository.save(ProductOrder.builder().id(0L).invoiceNo(System.currentTimeMillis()).build());
      AtomicReference<Float> grandTotal = new AtomicReference<>(0f);
      productRequest.getData().forEach(saleRequest -> {
        Optional<Product> product = productRepository.findById(saleRequest.getProductId());
        long totalQuantity = 0;
        if (product.isPresent()) {
          totalQuantity = saleRequest.getUserTotalQuantity();
          if (!product.get().isEnableTQ()) {
            if (saleRequest.getUserTotalQuantity() <= product.get().getExtraQuantity()) {
              product.get().setExtraQuantity(product.get().getExtraQuantity() - saleRequest.getQuantity());
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
//          grandTotal.set(grandTotal.get() + (saleRequest.getQuantity() * product.get().getPrice()));
          productRepository.save(product.get());
          saleRepository.save(ProductSale.builder().id(0L).quantity(totalQuantity).bundle(saleRequest.getUserQuantityBundle()).extra(saleRequest.getUserExtraQuantity()).product(product.get()).productOrder(productOrder).build());
        }
      });
      productOrder.setGrandTotal(productRequest.getGrandTotal());
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
    if (paging == null)
      return ResponseEntity.ok(new ProductResponse(productOrderRepository.findAllByReturnedIsFalse().stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).filter(Objects::nonNull).collect(Collectors.toList()), ""));
    return ResponseEntity.ok(new ProductResponse(productOrderRepository.findAllByReturnedIsFalse(paging).stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).filter(Objects::nonNull).collect(Collectors.toList()), ""));
  }

  public Pageable checkPaging(ProductRequest productRequest) {
    if (productRequest.getPagesize() > 0 && productRequest.getPagenumber() >= 0) {
      if (productRequest.getSortdirection() == "desc") {
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
      productReturnRequest.getData().forEach(returnRequest -> {
        Optional<ProductSale> productSale = saleRepository.findById(returnRequest.getId());
        if (productSale.isPresent()) {
          if (Objects.nonNull(productSale.get().getProduct())) {
            Product product = productSale.get().getProduct();
            long totalQuantity = returnRequest.getUserTotalQuantity();
            if (totalQuantity > 0 && totalQuantity<=productSale.get().getQuantity()) {
              if (!Objects.isNull(product) && !product.isEnableTQ()) {
                long bundles = returnRequest.getUserQuantityBundle();
                long extra = returnRequest.getUserExtraQuantity();
                if (bundles <= 0 && extra <= 0 && returnRequest.getUserTotalQuantity() > 0) {
                  long totalEx = returnRequest.getUserTotalQuantity() + product.getExtraQuantity();
                  if (totalEx > product.getQuantityItem()) {
                    long totalExtras = totalEx - product.getQuantityItem();
                    if (totalExtras < product.getExtraQuantity()) {
                      product.setExtraQuantity(totalExtras);
                      product.setQuantityBundle(product.getQuantityBundle() + 1);
                      productSale.get().setBundleReturn(zeroIfNull(productSale.get().getBundleReturn()) + 1);
                    }
                  } else {
                    product.setExtraQuantity(product.getExtraQuantity() + returnRequest.getUserTotalQuantity());
                  }
                } else {
                  calculateBundleWise(returnRequest, product, productSale, totalQuantity);
                }
              }

              long retProd=Objects.requireNonNull(product).getQuantity() + totalQuantity;
              if(retProd<=productSale.get().getQuantity())
              Objects.requireNonNull(product).setQuantity(retProd);
              long pro=zeroIfNull(productSale.get().getQuantityReturn()) + totalQuantity;
              if(pro<=productSale.get().getQuantity())
              productSale.get().setQuantityReturn(pro);
              if (product.getQuantity() > 0) { // check each product for stock
                product.setOutOfStock(Boolean.FALSE);
              } else {
                product.setOutOfStock(Boolean.TRUE);
              }
              productRepository.save(Objects.requireNonNull(product));
              if (productSale.get().getQuantityReturn() >= productSale.get().getQuantity()) {
                productSale.get().setReturned(Boolean.TRUE);
              }
              saleRepository.save(productSale.get());
              ProductReturn productReturn = ProductReturn.builder().product(product).productOrder(productOrder.get()).id(0L).quantity(totalQuantity).build();
              returnRepository.save(productReturn);
            }
          }
        }
      });

//      Long totalReturn = productReturnRequest.getData().stream().mapToLong(SaleRequest::getUserTotalQuantity).sum();
//      Long totalSold = productReturnRequest.getData().stream().mapToLong(SaleRequest::getQuantity).sum();
//      if (totalReturn.equals(totalSold)) {
//        productOrder.get().setReturned(Boolean.TRUE);
//      }
      productOrder.get().setGrandTotal(productReturnRequest.getGrandTotal());
      productOrderRepository.save(productOrder.get());
    }
    return ResponseEntity.ok(new MessageResponse("Ok"));

  }

  private void calculateBundleWise(SaleRequest returnRequest, Product product, Optional<ProductSale> productSale, long totalQuantity) {

    if (returnRequest.getUserTotalQuantity() <= product.getExtraQuantity()) {
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
          productSale.get().setBundleReturn(zeroIfNull(productSale.get().getBundleReturn()) + returnRequest.getUserQuantityBundle());
        }
      } else if (bundles > 0 && (extra > 0 && extra < product.getQuantityItem()) && extra > product.getExtraQuantity()) {
        product.setQuantityBundle(product.getQuantityBundle() + bundles);
        productSale.get().setBundleReturn(zeroIfNull(productSale.get().getBundleReturn()) + returnRequest.getUserQuantityBundle());
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
        productSale.get().setBundleReturn(zeroIfNull(productSale.get().getBundleReturn()) + returnRequest.getUserQuantityBundle());
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
      }

    }
  }

  public long zeroIfNull(Long bonus) {
    return Optional.ofNullable(bonus).orElse(0L);
  }

}
