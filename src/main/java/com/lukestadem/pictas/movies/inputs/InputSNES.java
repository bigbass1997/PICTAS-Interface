package com.lukestadem.pictas.movies.inputs;

public class InputSNES implements Input {
	
	public final byte[] bytes;
	
	public InputSNES(){
		bytes = new byte[2];
	}
	
	public InputSNES(byte buttons1, byte buttons2){
		bytes = new byte[]{buttons1, buttons2};
	}
	
	@Override
	public byte[] getBytes(){
		return bytes;
	}
}
