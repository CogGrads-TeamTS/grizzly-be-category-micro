package com.ts.grizzlybe.Client;

import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
@FeignClient(name = "product-service", decode404 = true)
public interface ProductClient {
    @GetMapping("categories/count/{categoryId}")
    ResponseEntity<Long> productCount(@PathVariable("categoryId") long categoryId);
}
