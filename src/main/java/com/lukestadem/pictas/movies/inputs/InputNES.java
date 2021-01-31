package com.lukestadem.pictas.movies.inputs;

public class InputNES implements Input {
	
	public final byte[] bytes;
	
	public InputNES(){
		bytes = new byte[1];
	}
	
	public InputNES(byte buttons){
		bytes = new byte[]{buttons};
	}
	
	@Override
	public byte[] getBytes(){
		return bytes;
	}
}
