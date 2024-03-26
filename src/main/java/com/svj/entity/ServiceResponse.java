package com.svj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.http.HttpResponse;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse<T> {
    private HttpResponse status;
    private T response;
    private List<ErrorDTO> errors;
}
