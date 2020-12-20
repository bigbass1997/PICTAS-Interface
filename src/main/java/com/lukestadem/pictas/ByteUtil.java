package com.lukestadem.pictas;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ByteUtil {
	
	public static String byteToString(byte b){
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((b & 0xF), 16);
		
		return (new String(hexDigits)).toUpperCase();
	}
	
	public static String bytesToString(byte... bytes){
		String s = "";
		for(byte b : bytes){
			s += byteToString(b) + ", ";
		}
		return s.substring(0, s.length() - 2);
	}
	
	public static boolean equalsByte(int i, byte b){
		return b == iByte(i);
	}
	
	public static byte iByte(int i){
		return (byte) i;
	}
	
	public static byte[] iByte(int... ints){
		final byte[] bytes = new byte[ints.length];
		for(int i = 0; i < ints.length; i++){
			bytes[i] = (byte) ints[i];
		}
		return bytes;
	}
	
	public static char n64ToAscii(byte b){
		switch(b){
			case 0x00:
				return '_';
			case 0x0F:
				return ' ';
			case 0x34:
				return '!';
			case 0x35:
				return '\"';
			case 0x36:
				return '#';
			case 0x37:
				return '`';
			case 0x38:
				return '*';
			case 0x39:
				return '+';
			case 0x3A:
				return ',';
			case 0x3B:
				return '-';
			case 0x3C:
				return '.';
			case 0x3D:
				return '/';
			case 0x3E:
				return ':';
			case 0x3F:
				return '=';
			case 0x40:
				return '?';
			case 0x41:
				return '@';
		}
		
		if(b >= 0x10 && b <= 0x19){
			return (char) ('0' + (b - 0x10));
		}
		
		if(b >= 0x1A && b <= 0x33){
			return (char) ('A' + (b - 0x1A));
		}
		
		return ' ';
	}
	
	public static String n64ToAscii(byte... bytes){
		String s = "";
		
		for(int i = 0; i < bytes.length; i++){
			s += n64ToAscii(bytes[i]);
			if((i + 1) % 16 == 0){
				s += "\n";
			}
		}
		
		return s.trim();
	}
	
	public static boolean writeToFile(String filename, Byte[] bytes){
		if(filename == null || filename.isEmpty()){
			filename = "bytemanager-defaultname";
		}
		filename = filename.trim();
		if(!filename.endsWith(".bin")){
			filename += ".bin";
		}
		
		final byte[] arr = ArrayUtils.toPrimitive(bytes);
		
		try {
			final File file = new File(filename);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			
			final FileOutputStream fos = new FileOutputStream(file);
			fos.write(arr);
			fos.close();
			System.out.println("File written!");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
