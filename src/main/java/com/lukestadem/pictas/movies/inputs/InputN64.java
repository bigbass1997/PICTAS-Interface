package com.lukestadem.pictas.movies.inputs;

public class InputN64 implements Input {
	
	public final int BUTTONS1 = 0;
	public final int BUTTONS2 = 1;
	public final int X_AXIS = 2;
	public final int Y_AXIS = 3;
	
	public final byte[] bytes;
	
	public InputN64(){
		bytes = new byte[4];
	}
	
	public InputN64(byte buttons1, byte buttons2, byte xAxis, byte yAxis){
		bytes = new byte[]{buttons1, buttons2, xAxis, yAxis};
	}
	
	@Override
	public byte[] getBytes(){
		return bytes;
	}
}
