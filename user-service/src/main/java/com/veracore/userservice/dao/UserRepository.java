package com.veracore.userservice.dao;

import com.veracore.userservice.models.PaidUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<PaidUser, Integer> {
    boolean existsPaidUserByUserName(String userName);
}
