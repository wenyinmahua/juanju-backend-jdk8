package com.mahua.juanju.constant;

public enum TeamStatusEnum {

	PUBLIC(0,"公共"),
	PRIVATE(1,"私有"),
	SECRET(2,"加密")
	;

	public static TeamStatusEnum getEnumByValue(Integer value){
		if (value == null){
			return PUBLIC;
		}
		TeamStatusEnum [] values = TeamStatusEnum.values();
		for (TeamStatusEnum teamStatusEnum : values){
			if (teamStatusEnum.getValue() == value){
				return teamStatusEnum;
			}
		}
		return null;
	}

	private int value;
	private String text;

	TeamStatusEnum(int value, String text) {
		this.value = value;
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public String getText() {
		return text;
	}
}
