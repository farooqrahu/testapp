package com.rahu.springjwt;

import com.rahu.springjwt.models.ERole;
import com.rahu.springjwt.models.Role;
import com.rahu.springjwt.models.User;
import com.rahu.springjwt.repository.CategoryRepository;
import com.rahu.springjwt.repository.ProductRepository;
import com.rahu.springjwt.repository.RoleRepository;
import com.rahu.springjwt.repository.UserRepository;
import com.rahu.springjwt.security.services.ProductService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@Configuration
@EnableScheduling
public class SpringBootSecurityJwtApplication {
  @Autowired
  PasswordEncoder encoder;
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String DEFAULT_INCLUDE_PATTERN = "/api.*";

  public static void main(String[] args) {
    SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
  }

  @Bean
  public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*"); // this allows all origin
    config.addAllowedHeader("*"); // this allows all headers
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Bean
  CommandLineRunner init(RoleRepository RoleRepository, UserRepository userRepository,
                         ProductRepository productRepository, CategoryRepository categoryRepository, ProductService productService) {
    return args -> {

      if(userRepository.findByEmailIgnoreCase("farahu2008@gmail.com").isEmpty()){


      Role r1 = new Role(ERole.ROLE_ADMIN);
      Role r2 = new Role(ERole.ROLE_MODERATOR);
      Role r3 = new Role(ERole.ROLE_USER);

      RoleRepository.save(r1);
      RoleRepository.save(r2);
      RoleRepository.save(r3);
      Set<Role> r = new HashSet<Role>();
      r.add(r1);
      r.add(r2);
      r.add(r3);
      User user = new User("admin", "farahu2008@gmail.com", encoder.encode("Admin@123"), r);
      user.setName("Administrator");
      user.setStatus("Activated");
      userRepository.save(user);
      }

//      RoleRepository.findAll().forEach(System.out::println);
//      userRepository.findAll().forEach(System.out::println);
//      Category category = new Category("tech");
//      Category category1 = new Category("pc");
//
//      categoryRepository.save(category);
//      categoryRepository.save(category1);

//      Product product = new Product("ps5", "nice", category, 1F);
//      Product product1 = new Product("a", "nice", category, 5F);
//      Product product2 = new Product("z", "nice", category1, 7F);
//
//      Product product3 = new Product("y", "hi", category, 8F);
//      productRepository.save(product);
//      productRepository.save(product1);
//      productRepository.save(product2);
//      productRepository.save(product3);
//      CartRequest cartRequest=new CartRequest("mod","123456",1L,1,5);
//
//      productService.addToCart(cartRequest);


    };

  };
  /**
   * the following method adds the API info to the swagger api documentation
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("Test - App")
      .description("This api document serves as collection")
      .termsOfServiceUrl("http://javainuse.com").license("JavaInUse License")
      .licenseUrl("farahu2008@gmail.com").version("1.0").build();
  }
  /**
   * the following method makes all the APIs under the CMS package available on
   * swagger
   *
   * @return
   */
  @Bean
  public Docket apis() {
    return new Docket(DocumentationType.SWAGGER_2).securitySchemes(Lists.newArrayList(apiKey()))
      .securityContexts(Lists.newArrayList(securityContext())).apiInfo(apiInfo()).select()
      .apis(RequestHandlerSelectors.basePackage("com.rahu.springjwt")).build();
  }
  /**
   * the following configuration adds the use of authorization key the APIs in swagger
   */
  private ApiKey apiKey() {
    return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth())
      .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN)).build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
  }


}
