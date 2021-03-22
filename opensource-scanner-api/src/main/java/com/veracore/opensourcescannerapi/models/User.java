package com.veracore.opensourcescannerapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Integer id;
    private String userName;
    private String email;
}
