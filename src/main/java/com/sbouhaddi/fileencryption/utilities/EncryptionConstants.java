package com.sbouhaddi.fileencryption.utilities;

public enum EncryptionConstants {

	AES_CIPHER("AES/CBC/PKCS5Padding"), AES("AES");

	private String value;

	EncryptionConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
