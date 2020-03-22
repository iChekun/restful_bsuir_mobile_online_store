package by.bsuir.controller;

import by.bsuir.service.ProductService;
import by.bsuir.service.dto.PageWrapper;
import by.bsuir.service.dto.Paging;
import by.bsuir.service.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static by.bsuir.controller.ControllerHelper.checkBindingResultAndThrowExceptionIfInvalid;

@Controller
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> add(@RequestBody @Valid ProductDto productDto,
                                          BindingResult result) {
        checkBindingResultAndThrowExceptionIfInvalid(result);
        ProductDto savedGiftCertificateDto = productService.save(productDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedGiftCertificateDto.getId()).toUri());
        return new ResponseEntity<>(savedGiftCertificateDto, httpHeaders, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable @Positive(message = "Id must be positive!") Long id,
                                             @RequestBody @Valid ProductDto productDto,
                                             BindingResult result) {
        checkBindingResultAndThrowExceptionIfInvalid(result);
        productDto.setId(id);

        return new ResponseEntity<>(productService.update(productDto),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @Positive(message = "Id must be positive!") Long id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable @Positive(message = "Id must be positive!") Long id) {
        return new ResponseEntity<>(
                productService.findById(id),
                HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<PageWrapper<ProductDto>> findAll(@RequestParam(defaultValue = "10", value = "size")
                                                           @Positive(message = "Id must be positive!") Integer size,
                                                           @RequestParam(defaultValue = "0", value = "page") Integer page,

                                                           @RequestParam(value = "brands", required = false) List<String> brands,
                                                           @RequestParam(value = "price", required = false) Double price,
                                                           @RequestParam(value = "productName", required = false, defaultValue = "") String productName,

                                                           @RequestParam(value = "sortBy", required = false, defaultValue = "price") String sortBy,//dateOfCreation
                                                           @RequestParam(value = "sortType", required = false, defaultValue = "ASC") String sortType) {


        Paging paging = new Paging(size, page);
        return new ResponseEntity<>(
                productService.findAll(
                        paging,
                        brands,
                        price,
                        productName,
                        sortBy,
                        sortType),
                HttpStatus.OK);
    }

}