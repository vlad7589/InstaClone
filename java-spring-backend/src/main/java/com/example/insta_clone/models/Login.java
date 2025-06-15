package com.example.insta_clone.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {

    @JsonProperty("identifier")
    private String identifier; // Holds phoneNumber, email, or username
    private String password;
}
