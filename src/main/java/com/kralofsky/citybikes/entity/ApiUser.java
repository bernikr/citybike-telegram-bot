package com.kralofsky.citybikes.entity;

import com.kralofsky.citybikes.citybikeAPI.util.Session;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class ApiUser implements Serializable {
    final String username;
    final String password;
    Session session;
    String fullName;
}
