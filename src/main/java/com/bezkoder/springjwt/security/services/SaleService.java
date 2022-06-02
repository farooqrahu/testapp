package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.dto.ProductOrderInvoiceDto;
import com.bezkoder.springjwt.models.Product;
import com.bezkoder.springjwt.models.ProductOrder;
import com.bezkoder.springjwt.models.ProductReturn;
import com.bezkoder.springjwt.models.ProductSale;
import com.bezkoder.springjwt.payload.request.ProductRequest;
import com.bezkoder.springjwt.payload.request.SaleRequestList;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.payload.response.ProductResponse;
import com.bezkoder.springjwt.repository.*;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
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
        if (product.isPresent()) {
          if (!product.get().isEnableTQ()) {
            if (saleRequest.getQuantity() <= product.get().getExtraQuantity()) {
              product.get().setExtraQuantity(product.get().getExtraQuantity() - saleRequest.getQuantity());
            } else {
              double val = Double.valueOf(saleRequest.getQuantity()) / Double.valueOf(product.get().getQuantityItem());
              String saleBundle = new DecimalFormat("0.00").format(val);
              if ((val - (long) val) == 0) {
                product.get().setQuantityBundle(product.get().getQuantityBundle() - (long) val);
              } else {
                String[] bundleValues = saleBundle.split("\\.");
                long bundles = Long.parseLong(bundleValues[0]);
                long extra = Long.parseLong(bundleValues[1]);
                if (extra > product.get().getExtraQuantity()) {
                  bundles++;
                  double extraRemaining = product.get().getQuantityItem() - extra;
                  double totalExtra = product.get().getExtraQuantity() + extraRemaining;
                  if (product.get().getExtraQuantity() > 0) {
                    product.get().setExtraQuantity((long) totalExtra - product.get().getExtraQuantity());
                  } else {
                    product.get().setExtraQuantity((long) totalExtra);
                  }
                } else {
                  if (product.get().getExtraQuantity() > 0) {
                    product.get().setExtraQuantity(product.get().getExtraQuantity() - extra);
                  } else {
                    product.get().setExtraQuantity(extra);
                  }
                }
                product.get().setQuantityBundle(product.get().getQuantityBundle() - (long) bundles);
              }
            }
          }
          product.get().setQuantity(product.get().getQuantity() - saleRequest.getQuantity());
          grandTotal.set(grandTotal.get() + (saleRequest.getQuantity() * product.get().getPrice()));
          productRepository.save(product.get());
          saleRepository.save(ProductSale.builder().id(0L).quantity(saleRequest.getQuantity()).product(product.get()).productOrder(productOrder).build());
        }
      });
      productOrder.setGrandTotal(grandTotal.get());
      productOrderRepository.save(productOrder);
    }
    return ResponseEntity.ok(new MessageResponse("Sale order submitted successfully!"));
  }

  //  private static double[] separateFractional(double d) {
//    BigDecimal bd = new BigDecimal(d);
//    return new double[] { bd.intValue(),
//      bd.remainder(BigDecimal.ONE).doubleValue() };
//  }
  public ResponseEntity<?> findOrders(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductResponse(productOrderRepository.findAllByReturnedIsFalse().stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).collect(Collectors.toList()), ""));
    return ResponseEntity.ok(new ProductResponse(productOrderRepository.findAllByReturnedIsFalse(paging).stream().map(ProductOrderInvoiceDto::factoryProductOrderInvoice).collect(Collectors.toList()), ""));


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
    AtomicReference<Float> grandTotal = new AtomicReference<>(0f);
    if (productOrder.isPresent()) {
      productReturnRequest.getData().forEach(returnRequest -> {
        Optional<ProductSale> productSale = saleRepository.findById(returnRequest.getId());
        if (productSale.isPresent()) {
          Product product = productSale.get().getProduct();
          if (returnRequest.getReturnQuantity() > 0) {
            if (!Objects.requireNonNull(product).isEnableTQ()) {
              if (returnRequest.getReturnQuantity() <= returnRequest.getProduct().getExtraQuantity()) {
                Objects.requireNonNull(product).setExtraQuantity(returnRequest.getProduct().getExtraQuantity() + returnRequest.getReturnQuantity());
              } else {
                double val = Double.valueOf(returnRequest.getQuantity()) / Double.valueOf(product.getQuantityItem());
                String saleBundle = new DecimalFormat("0.00").format(val);
                if ((val - (long) val) == 0) {
                  product.setQuantityBundle(product.getQuantityBundle() + (long) val);
                } else {
                  String[] bundleValues = saleBundle.split("\\.");
                  long bundles = Long.parseLong(bundleValues[0]);
                  long extra = Long.parseLong(bundleValues[1]);
                  if (extra == product.getQuantityItem()) {
                    bundles++;
                    double extraRemaining = product.getQuantityItem() + extra;
                    double totalExtra = product.getExtraQuantity() + extraRemaining;
                    if (product.getExtraQuantity() > 0) {
                      product.setExtraQuantity((long) totalExtra + returnRequest.getProduct().getExtraQuantity());
                    } else {
                      product.setExtraQuantity((long) totalExtra);
                    }
                  } else {
                    product.setExtraQuantity(returnRequest.getProduct().getExtraQuantity() + extra);
                  }
                  product.setQuantityBundle(returnRequest.getProduct().getQuantityBundle() + bundles);
                }
              }
            }

            Objects.requireNonNull(product).setQuantity(Objects.requireNonNull(product).getQuantity() + returnRequest.getReturnQuantity());
            productRepository.save(Objects.requireNonNull(product));

            productSale.get().setQuantity(productSale.get().getQuantity() - returnRequest.getReturnQuantity());
            saleRepository.save(productSale.get());
            ProductReturn productReturn = ProductReturn.builder().product(product).productOrder(productOrder.get()).id(0L).quantity(returnRequest.getReturnQuantity()).build();
            returnRepository.save(productReturn);
            grandTotal.set(grandTotal.get() + (returnRequest.getReturnQuantity() * Objects.requireNonNull(product).getPrice()));
          }
        }
      });
//      productOrder.get().setReturned(Boolean.TRUE);
      productOrder.get().setGrandTotal(productOrder.get().getGrandTotal() - grandTotal.get());
      productOrderRepository.save(productOrder.get());
    }
    return ResponseEntity.ok(new MessageResponse("Ok"));

  }
}
