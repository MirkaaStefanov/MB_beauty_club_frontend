package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.config.FeignClientConfiguration;
import com.example.MB_beauty_club_frontend.dtos.OrderDTO;
import com.example.MB_beauty_club_frontend.dtos.OrderProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "MB-orders", url = "${backend.base-url}/orders", configuration = FeignClientConfiguration.class)
public interface OrderClient {

    @GetMapping
    List<OrderDTO> getAllOrders(@RequestHeader("Authorization") String auth);

    @GetMapping("/orderProducts")
    List<OrderProductDTO> allOrderProducts(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    OrderDTO getOrderById(@PathVariable UUID id, @RequestHeader("Authorization") String auth);

    @GetMapping("/{id}/products")
    List<OrderProductDTO> findOrderProductsForOrder(@PathVariable UUID id, @RequestHeader("Authorization") String auth);

    @PostMapping
    void createOrder(@RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    OrderDTO updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    void deleteOrder(@PathVariable UUID id, @RequestHeader("Authorization") String auth);

    @GetMapping("/last")
    OrderDTO findFirstByOrderByIdDesc(@RequestHeader("Authorization") String auth);

    @GetMapping("/my-orders")
    List<OrderDTO> getAllOrdersForAuthenticatedUser(@RequestHeader("Authorization") String auth);


}
