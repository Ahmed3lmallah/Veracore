package com.veracore.opensourcescannerapi.utils.feign;

import com.veracore.opensourcescannerapi.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UsersClient {

    @GetMapping(value = "/paidUsers/{id}")
    User getUser(int id);

    @GetMapping(value = "/paidUsers/search/existsPaidUserByUserName")
    boolean isExistingUser(@RequestParam String userName);

}
