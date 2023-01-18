package com.rahu.springjwt.security.services;

import com.rahu.springjwt.models.Product;
import com.rahu.springjwt.models.ProductSaleList;
import com.rahu.springjwt.payload.request.SaleRequest;
import com.rahu.springjwt.payload.request.SaleRequestList;
import com.rahu.springjwt.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.Optional;

public class OldReturnLogic {

  public ResponseEntity<?> returnProductSale(@Valid SaleRequestList productReturnRequest) {
   /* userDetailsServiceImpl.checkAdmin();
    Optional<ProductOrder> productOrder = productOrderRepository.findById(productReturnRequest.getId());
    if (productOrder.isPresent()) {
      AtomicLong quantitySold= new AtomicLong();
      AtomicLong quantityReturned= new AtomicLong();
      productReturnRequest.getData().forEach(returnRequest -> {
        Optional<ProductSaleList> productSale = saleRepository.findById(returnRequest.getId());
        if (productSale.isPresent()) {
          if (Objects.nonNull(productSale.get().getProduct())) {
            Product product = productSale.get().getProduct();
            long totalQuantity = returnRequest.getUserTotalQuantity();
            if (totalQuantity > 0 && totalQuantity<=productSale.get().getTotalQuantitySale()) {
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
              quantitySold.set(productSale.get().getTotalQuantitySale());
              quantityReturned.set(zeroIfNull(productSale.get().getTotalQuantityReturn())+returnRequest.getUserTotalQuantity());
              long retProd=Objects.requireNonNull(product).getQuantity() + totalQuantity;
//              if(retProd<=productSale.get().getQuantity() || retProd<=product.getQuantity())
                product.setQuantity(retProd);
              long pro=zeroIfNull(productSale.get().getTotalQuantityReturn()) + totalQuantity;
              if(pro<=productSale.get().getTotalQuantitySale())
              productSale.get().setTotalQuantityReturn(pro);
              if (product.getQuantity() > 0) { // check each product for stock
                product.setOutOfStock(Boolean.FALSE);
              } else {
                product.setOutOfStock(Boolean.TRUE);
              }
              productRepository.save(Objects.requireNonNull(product));
              if (productSale.get().getTotalQuantityReturn() >= productSale.get().getTotalQuantitySale()) {
                productSale.get().setReturned(Boolean.TRUE);
              }
              saleRepository.save(productSale.get());
              ProductReturn productReturn = ProductReturn.builder().product(product).productOrder(productOrder.get()).id(0L).totalQuantityReturn(totalQuantity).build();
              returnRepository.save(productReturn);
            }
          }
        }
      });

      if (quantitySold.get()==quantityReturned.get()) {
        productOrder.get().setReturned(Boolean.TRUE);
      }
   *//*   long totalReturn = productReturnRequest.getData().stream().mapToLong(SaleRequest::getUserTotalQuantity).sum();
      long totalSold = productReturnRequest.getData().stream().mapToLong(SaleRequest::getQuantity).sum();
      if (totalReturn==totalSold) {
        productOrder.get().setReturned(Boolean.TRUE);
      }*//*
      productOrder.get().setGrandTotal(productReturnRequest.getGrandTotal());
      productOrderRepository.save(productOrder.get());
    }*/
    return ResponseEntity.ok(new MessageResponse("Ok"));

  }

  private void calculateBundleWise(SaleRequest returnRequest, Product product, Optional<ProductSaleList> productSale, long totalQuantity) {
/*
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
      }else{
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
}
