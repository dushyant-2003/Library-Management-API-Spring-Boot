package com.library.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private Status status;
	private String message;
	private T data;
	private Integer count;
	private Integer limit;
	private Integer totalPage;
	private Integer totalElements;

}
