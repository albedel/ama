package com.telegram.hunter.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ContractUtil {
	
	/**
	 * analyze ca
	 * @param text
	 */
	public static String analyzeContractAddress(String text) {

		String evmAddressPattern = "0x[a-fA-F0-9]{40}";
		Pattern pattern = Pattern.compile(evmAddressPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String potentialAddress = matcher.group();
			if (isValidEvmAddress(potentialAddress)) {
				return potentialAddress;
			}
		}
		return null;
	}
	
	/**
	 * valid evm address
	 * @param address
	 * @return
	 */
	public static boolean isValidEvmAddress(String address) {
		return address.equalsIgnoreCase(toChecksumAddress(address));
	}

	private static String toChecksumAddress(String address) {
		address = address.toLowerCase().replace("0x", "");
		String hash = sha3(address);
		if (StringUtils.isBlank(hash)) {
			return null;
		}
		
		StringBuilder result = new StringBuilder("0x");
		for (int i = 0; i < address.length(); i++) {
			if (Integer.parseInt(String.valueOf(hash.charAt(i)), 16) >= 8) {
				result.append(Character.toUpperCase(address.charAt(i)));
			} else {
				result.append(address.charAt(i));
			}
		}
		return result.toString();
	}

	private static String sha3(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes());
			
			StringBuilder result = new StringBuilder();
			for (byte b : hash) {
				result.append(String.format("%02x", b));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
}
