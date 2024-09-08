package com.mahua.juanju.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
	private Long pageSize;
	private Long pageNum;
}
