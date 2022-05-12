package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.dto.ProductDto;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.*;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
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

  public ResponseEntity<?> findProduct(ProductRequest productRequest) {
    if (productRequest.getCategory() != null && productRequest.getName() != null) {
      return findByNameContainingAndCategory(productRequest);
    } else {
      if (productRequest.getName() != null) {
        return findByNameContaining(productRequest);
      } else {
        return findAllProducts(productRequest);
      }
    }
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

  public ResponseEntity<?> findAllProducts(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductResponse(productRepository.findAll().stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
    return ResponseEntity.ok(new ProductResponse(productRepository.findAll(paging).stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
  }

  public ResponseEntity<?> findByNameContaining(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductResponse(productRepository.findByNameContaining(productRequest.getName()).stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
    else
      return ResponseEntity
        .ok(new ProductResponse(productRepository.findByNameContaining(productRequest.getName(), paging).stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
  }

  public ResponseEntity<?> findByNameContainingAndCategory(ProductRequest productRequest) {
    Pageable paging = checkPaging(productRequest);
    if (paging == null)
      return ResponseEntity.ok(new ProductResponse(
        productRepository.findByNameContainingAndCategoryName(productRequest.getName(), productRequest.getCategory().getName()).stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
    else
      return ResponseEntity.ok(new ProductResponse(productRepository
        .findByNameContainingAndCategory(productRequest.getName(), productRequest.getCategory(), paging).stream().map(ProductDto::factoryProduct).collect(Collectors.toList())));
  }

  public ResponseEntity<?> addProduct(ProductRequest productRequest) throws IOException {
    userDetailsServiceImpl.checkAdmin();
    Optional<Category> category = categoryRepository.findById(productRequest.getCategory().getId());
    if (category.isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Category does not exist"));
    Optional<Company> company = companyRepository.findById(productRequest.getCompany().getId());
    if (company.isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Company does not exist"));
    if (productRequest.getName() == null || "".equals(productRequest.getName()))
      return ResponseEntity.badRequest().body(new MessageResponse("Error: name must not be empty"));
    if (productRequest.getPrice() == null || productRequest.getPrice() <= 0)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: price must not be empty, zero or negative"));
    Product product = new Product(productRequest);

//    Document document = new Document(new Rectangle(PageSize.A4));
    String fileName = product.getName().toLowerCase(Locale.ROOT).trim() + ".png";
//    String fileLocation = new File("static\\images").getAbsolutePath() + "\\" + fileName;
//    FileOutputStream fos = new FileOutputStream(fileLocation);
//    PdfWriter writer = PdfWriter.getInstance(document, fos);
//    document.open();
//    document.add(new Paragraph("Khalil Cloth Shop"));
//
//    Barcode128 code128 = new Barcode128();
//    code128.setGenerateChecksum(true);
//    code128.setCode("RS: 140.00");
//    code128.setBarHeight(20);
//    code128.setBaseline(10);
//    document.add(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
//    document.close();
//
//
//    BarcodeGenerator generator = new BarcodeGenerator(EncodeTypes.CODE_128, "Aspose.BarCode");
//// set barcode's caption
//    generator.getParameters().getCaptionAbove().setText("The caption above.");
//    generator.getParameters().getCaptionAbove().setVisible(true);
//    generator.getParameters().getCaptionBelow().setText("The caption below.");
//    generator.getParameters().getCaptionBelow().setVisible(true);
//// set image resolution
//    generator.getParameters().setResolution(200);
//// generate barcode
//    generator.save(fileName);


//    FileDB fileDB = new FileDB(fileName,"pdf",);

    productRepository.save(product);
    return ResponseEntity.ok(new MessageResponse("product added successfully!"));
  }

  public ResponseEntity<?> updateProduct(ProductRequest productRequest) throws IOException {
    userDetailsServiceImpl.checkAdmin();

    Optional<Category> category = categoryRepository.findById(productRequest.getCategory().getId());
    if (category.isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Category does not exist"));
    Optional<Company> company = companyRepository.findById(productRequest.getCompany().getId());
    if (company.isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Company does not exist"));
    if (productRequest.getName() == null || Objects.equals(productRequest.getName(), ""))
      return ResponseEntity.badRequest().body(new MessageResponse("Error: name must not be empty"));
    if (productRequest.getPrice() == null || productRequest.getPrice() <= 0)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: price must not be empty, zero or negative"));
    Product product = new Product(productRequest);
// ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//    Document document = new Document(new Rectangle(new RectangleReadOnly(400.0F, 240.0F)));
    String fileName = product.getName().toLowerCase(Locale.ROOT).trim() + ".png";
    File file = new File(".\\barcode");
    if(!file.isDirectory()){
      file.mkdir();
    }
    String fileLocation=file.getAbsolutePath() + "\\" + fileName; ;
    //    FileOutputStream fos = new FileOutputStream(fileLocation);
//    PdfWriter writer = PdfWriter.getInstance(document, fos);
//    PdfWriter.getInstance(document, byteArrayOutputStream);
//    document.open();
//    document.add(new Paragraph(barcodeLabel));
//    Barcode128 code128 = new Barcode128();
//    code128.setGenerateChecksum(true);
//    code128.setCode("RS: "+ new  DecimalFormat("#.##").format(product.getPrice()));
//    code128.setBarHeight(20);
//    code128.setBaseline(20);
//    code128.setGuardBars(true);
//    code128.setBarHeight(80f); // great! but what about width???
//    code128.setX(2f);
//
//    document.add(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
//    document.close();

//
//    Code128 code1281=new Code128();
//    EAN13Bean barcodeGenerator = new EAN13Bean();
//    BitmapCanvasProvider canvas =
//      new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//    barcodeGenerator.generateBarcode(canvas, "125555555322");
//    CanvasProvider canvas128 =new CanvasProvider();
//    barcodeGenerator.generateBarcode(canvas, "125555555322");
//
//    code1281.generateBarcode(canvas128);
//
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    ImageIO.write(canvas.getBufferedImage(), "jpg", baos);
//    byte[] bytes = baos.toByteArray();

    Code128Bean code128 = new Code128Bean();
    code128.setHeight(15f);
    code128.setModuleWidth(0.4);
    code128.setQuietZone(10);
    code128.doQuietZone(true);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
    code128.generateBarcode(canvas, "RS: "+ new  DecimalFormat("#.##").format(product.getPrice()));
    canvas.finish();

//write to png file
    FileOutputStream fos = new FileOutputStream(fileLocation);
    fos.write(baos.toByteArray());
    fos.flush();
    fos.close();

//write to pdf
//    Image png = Image.getInstance(baos.toByteArray());
//    png.setAbsolutePosition(400, 685);
//    png.scalePercent(25);

//    Document document = new Document(new Rectangle(595, 842));
//    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("barcodes.pdf"));
//    document.open();
//    document.add(png);
//    document.close();
//
//    writer.close();

    FileDB fileDB = new FileDB(fileName,"image",baos.toByteArray());

//    BarcodeGenerator generator = new BarcodeGenerator(EncodeTypes.CODE_128, "Khalil Cloth House");
//// set barcode's caption
//    generator.getParameters().getCaptionAbove().setText("Rs: 140.00");
//    generator.getParameters().getCaptionAbove().setVisible(true);
//// set image resolution
//    generator.getParameters().setResolution(200);
//// generate barcode
//    generator.save(fileName);
    product.setFiles(fileDB);
    productRepository.save(product);
    return ResponseEntity.ok(new MessageResponse("product updated successfully!"));
  }

  public ResponseEntity<?> deleteProduct(ProductRequest productRequest) {
    userDetailsServiceImpl.checkAdmin();

    productRepository.deleteById(productRequest.getId());
    return ResponseEntity.ok(new MessageResponse("product deleted successfully"));
  }

  public ResponseEntity<?> saveProductImage(long id, MultipartFile image, int number) {
    if (number < 0 || number > 4)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: product can only have 5 images"));
    Product product = getProduct(id);
    try {
      String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
      FileDB fileDB = new FileDB(fileName, image.getContentType(), image.getBytes());
      product.setImages(true);
      product.setFiles(fileDB);
      fileDBRepository.save(fileDB);
      productRepository.save(product);

      return ResponseEntity.ok(new MessageResponse("product image updated successfully"));
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(new MessageResponse("Error: image could not be saved"));
    }
  }

  private Product getProduct(long id) {

    Optional<Product> product = productRepository.findById(id);
    checkProduct(product);
    return product.get();
  }

  private void checkProduct(Optional<Product> product) {
    if (product.isEmpty())
      productDoesNotExist();
  }

  private ResponseEntity<?> productDoesNotExist() {
    return ResponseEntity.badRequest().body(new MessageResponse("Error: product does not exist"));
  }

  public ResponseEntity<?> updateCategory(@Valid CategoryRequest categoryRequest) {
    userDetailsServiceImpl.checkAdmin();
    Category category = categoryRepository.findById(categoryRequest.getId()).get();
    category.setName(categoryRequest.getName());
    categoryRepository.save(category);
    return ResponseEntity.ok(new MessageResponse("Category updated successfully!"));
  }

  public ResponseEntity<?> deleteCategory(@Valid CategoryRequest categoryRequest) {
    userDetailsServiceImpl.checkAdmin();
    categoryRepository.deleteById(categoryRequest.getId());
    return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
  }

  public ResponseEntity<?> addCategory(@Valid CategoryRequest categoryRequest) {
    userDetailsServiceImpl.checkAdmin();
    Category category = new Category(categoryRequest.getName());
    categoryRepository.save(category);
    return ResponseEntity.ok(new MessageResponse("Category added successfully!"));
  }

  public ResponseEntity<?> getAllCategories(CategoryRequest categoryRequest) {
    userDetailsServiceImpl.checkAdmin();

    List<Category> categorylist = categoryRepository.findAll();
    return ResponseEntity.ok(new CategoryResponse(categorylist));
  }
  public ResponseEntity<?> getAllCompanies(CompanyRequest compCategoryRequest) {
    userDetailsServiceImpl.checkAdmin();
    List<Company> companyList = companyRepository.findAll();
    return ResponseEntity.ok(new CompanyResponse(companyList));
  }


  public ResponseEntity<?> updateCompany(@Valid CompanyRequest companyRequest) {
    userDetailsServiceImpl.checkAdmin();
    Company category = companyRepository.findById(companyRequest.getId()).get();
    category.setName(companyRequest.getName());
    companyRepository.save(category);
    return ResponseEntity.ok(new MessageResponse("Company Add/Update successfully!"));
  }

  public ResponseEntity<?> deleteCompany(@Valid CompanyRequest companyRequest) {
    userDetailsServiceImpl.checkAdmin();
    companyRepository.deleteById(companyRequest.getId());
    return ResponseEntity.ok(new MessageResponse("Company deleted successfully!"));
  }

  public ResponseEntity<?> addCompany(@Valid CompanyRequest companyRequest) {
    userDetailsServiceImpl.checkAdmin();
    Company company = new Company(companyRequest.getName());
    companyRepository.save(company);
    return ResponseEntity.ok(new MessageResponse("Company Add successfully!"));
  }

  public ResponseEntity<?> getAllCompany(CompanyRequest companyRequest) {
    userDetailsServiceImpl.checkAdmin();

    List<Company> categorylist = companyRepository.findAll();
    return ResponseEntity.ok(new CompanyResponse(categorylist));
  }

  public ResponseEntity<?> addToCart(CartRequest cartRequest) {
    userDetailsServiceImpl.checkAdminOrConcernedUser(cartRequest.getUserid());

    // if (cartRequest.getQuantity() <= 0)
    // return ResponseEntity.badRequest().body(new MessageResponse("Error: quantity
    // must not bigger than 1"));
    if (cartRequest.getProductid() <= 0)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: product not valid"));
    if (cartRequest.getUserid() == null || cartRequest.getUserid() <= 0)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: cart's user is not valid"));
    User user = null;
    Optional<User> ouser = userRepository.findById(cartRequest.getUserid());
    if (ouser.isEmpty()) {
      userDetailsServiceImpl.userNotFound();
    } else
      user = ouser.get();
    ShoppingCart shoppingcart;
    if (user.getShoppingcart() == null) {
      shoppingcart = new ShoppingCart(user);
      shoppingcartRepository.save(shoppingcart);
      user.setShoppingcart(shoppingcart);
      userRepository.save(user);
    }

    Product product = productRepository.findById(cartRequest.getProductid()).get();

    if (product == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: product does not exist"));

    CartItem cartitem = null;
    Optional<CartItem> ocartitem = cartItemRepository.findByShoppingcartAndProduct(user.getShoppingcart(), product);
    if (ocartitem.isEmpty()) {
      cartitem = new CartItem(product, cartRequest.getQuantity());
      cartitem.setShoppingcart(user.getShoppingcart());
      cartItemRepository.save(cartitem);
    } else
      cartitem = ocartitem.get();
    shoppingcart = user.getShoppingcart();
    shoppingcart.addProduct(cartitem);
    shoppingcartRepository.save(shoppingcart);
    return ResponseEntity.ok(new MessageResponse("product added to cart"));
  }

  public ResponseEntity<?> removeCartItem(@Valid CartRequest cartRequest) {
    userDetailsServiceImpl.checkAdminOrConcernedUser(
      cartRequest.getUserid());
    User user = userRepository.findByUsernameIgnoreCase(cartRequest.getUsername()).get();
    ShoppingCart shoppingCart = user.getShoppingcart();
    Product product = getProduct(cartRequest.getProductid());

    CartItem cartItem = cartItemRepository.findByShoppingcartAndProduct(shoppingCart, product).get();
    shoppingCart.removeProduct(cartItem);
    shoppingcartRepository.save(shoppingCart);
    cartItemRepository.delete(cartItem);
    return ResponseEntity.ok(new MessageResponse("Item removed"));

  }

  public ResponseEntity<?> getShoppingcart(CartRequest cartRequest) {
    userDetailsServiceImpl.checkAdminOrConcernedUser(
      cartRequest.getUserid());
    if (cartRequest.getUserid() == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: cart's user is not valid"));
    User user = userRepository.findById(cartRequest.getUserid()).get();
    if (user == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user does not exist"));
    ShoppingCart shoppingcart = user.getShoppingcart();
    if (shoppingcart == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user has no cart"));
    if (shoppingcart.getCartItems().isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: empty cart"));
    return ResponseEntity.ok(new ShoppingCartResponse(shoppingcart));
  }

  public ResponseEntity<?> getAllShoppingCarts(CartRequest cartRequest) {
    userDetailsServiceImpl.checkAdmin();
    List<ShoppingCart> shoppingcart = shoppingcartRepository.findByCartItemsIsNotEmpty();
    if (shoppingcart == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: no carts"));
    return ResponseEntity.ok(new ShoppingCartResponse(shoppingcart));
  }

  public ResponseEntity<?> setShippingDate(@Valid ShoppingCartRequest shoppingcartRequest) {
    userDetailsServiceImpl.checkAdmin();
    if (shoppingcartRequest.getUserid() == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: cart's user is not valid"));
    User user = userRepository.findById(shoppingcartRequest.getUserid()).get();
    if (user == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user does not exist"));
    ShoppingCart shoppingcart = shoppingcartRepository.findByUser(user).get();
    if (shoppingcart == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user has no cart"));
    if (shoppingcart.getCartItems().isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: empty cart"));
    if (shoppingcart.getShippingdate() != null)
      if (shoppingcartRequest.getShippingdate().before(shoppingcart.getShippingdate()))
        return ResponseEntity.badRequest().body(new MessageResponse("Error: invalid date"));
    if (shoppingcartRequest.getShippingdate() == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: invalid date"));
    shoppingcart.setShippingdate(shoppingcartRequest.getShippingdate());
    shoppingcartRepository.save(shoppingcart);
    //send email
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setSubject("Your order has been shipped!");
    mailMessage.setFrom("mrissaoussama@gmail.com");
    mailMessage.setText("hello " + user.getUsername() + ", your order has been shipped");
    emailSenderService.sendEmail(mailMessage);
    return ResponseEntity.ok(new ShoppingCartResponse(shoppingcart));
  }

  public ResponseEntity<?> setCompletedDate(@Valid ShoppingCartRequest shoppingcartRequest) {
    userDetailsServiceImpl.checkAdmin();
    if (shoppingcartRequest.getUserid() == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: cart's user is not valid"));
    User user = userRepository.findById(shoppingcartRequest.getUserid()).get();
    if (user == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user does not exist"));
    ShoppingCart shoppingcart = shoppingcartRepository.findByUser(user).get();
    if (shoppingcart == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: user has no cart"));
    if (shoppingcart.getCartItems().isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: empty cart"));
    if (shoppingcart.getCompleteddate() != null)
      if (shoppingcartRequest.getCompleteddate().before(shoppingcart.getCompleteddate()))
        return ResponseEntity.badRequest().body(new MessageResponse("Error: invalid date"));
    if (shoppingcartRequest.getCompleteddate() == null)
      return ResponseEntity.badRequest().body(new MessageResponse("Error: invalid date"));
    shoppingcart.setCompleteddate(shoppingcartRequest.getCompleteddate());
    shoppingcartRepository.save(shoppingcart);
    //send email
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setSubject("Your order has been completed!");
    mailMessage.setFrom("mrissaoussama@gmail.com");
    mailMessage.setText("hello " + user.getUsername() + ", your order has been completed");
    emailSenderService.sendEmail(mailMessage);
    return ResponseEntity.ok(new ShoppingCartResponse(shoppingcart));
  }

  public ResponseEntity<?> updateShoppingCartItems(@Valid CartItemRequest cartItemRequest) {
    userDetailsServiceImpl.checkAdminOrConcernedUser(cartItemRequest.getUserid());

    if (cartItemRequest.getCartItems().isEmpty())
      return ResponseEntity.badRequest().body(new MessageResponse("Error: empty cart"));
    User user = userRepository.findById(cartItemRequest.getUserid()).get();

    ShoppingCart shoppingcart = user.getShoppingcart();
    shoppingcart.updateProduct(cartItemRequest.getCartItems());
    shoppingcartRepository.save(shoppingcart);
    return ResponseEntity.ok(new MessageResponse("Cart updated"));

  }

}
