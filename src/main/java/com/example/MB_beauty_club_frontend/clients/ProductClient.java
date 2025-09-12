package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.ProductDTO;
import com.example.MB_beauty_club_frontend.enums.ProductCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "MB-products", url = "${backend.base-url}/products")
public interface ProductClient {

    @GetMapping
    List<ProductDTO> getAllProducts(
            @RequestParam(required = false) Boolean forSale,
            @RequestParam(required = false) ProductCategory category,
            @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping("/{id}")
    ProductDTO getById(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth);

    @PostMapping
    ProductDTO create(@RequestBody ProductDTO dto, @RequestHeader(value = "Authorization", required = false) String auth);

    @PutMapping("/{id}")
    ProductDTO update(@PathVariable Long id, @RequestBody ProductDTO dto, @RequestHeader(value = "Authorization", required = false) String auth);


    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth);

    @PostMapping("/{id}/toggle")
    ProductDTO toggleAvailability(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth);

}
