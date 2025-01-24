package com.telegram.hunter.utils;

import org.bitcoinj.core.Base58;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolanaUtil {
	
	/**
	 * analyze solana address
	 * @param text
	 */
	public static String analyzeSolanaAddress(String text) {
		String solanaAddressPattern = "[1-9A-HJ-NP-Za-km-z]{32,44}";
		Pattern pattern = Pattern.compile(solanaAddressPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String potentialAddress = matcher.group();
			if (isValidSolanaAddress(potentialAddress)) {
				return potentialAddress;
			}
		}
		return null;
	}
	
	/**
	 * Valid solana address
	 * @param address
	 * @return
	 */
	public static boolean isValidSolanaAddress(String address) {
		try {
			byte[] decoded = Base58.decode(address);
			return decoded.length == 32;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
}
