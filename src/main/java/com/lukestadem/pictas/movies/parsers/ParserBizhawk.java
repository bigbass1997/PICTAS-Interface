package com.lukestadem.pictas.movies.parsers;

import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.inputs.InputN64;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ParserBizhawk extends Parser {
	
	private enum InputByteType {
		N64_BUTTONS_1, N64_BUTTONS_2
	}
	
	private Input[][] frames;
	
	private boolean isParsed;
	
	public ParserBizhawk(File file){
		super(file, Type.BK2);
		
		frames = null;
		
		isParsed = false;
	}
	
	@Override
	public Input[][] getFrames(){ //TODO: Add support for other consoles
		if(!isParsed){
			final List<String> lines = getFileLines("Input Log.txt");
			int logKeyIndex = 0; // LogKey doesn't always start on first line
			for(int i = 0; i < lines.size(); i++){
				logKeyIndex = i;
				if(lines.get(i).startsWith("LogKey")){
					break;
				}
			}
			final String logKey = lines.get(logKeyIndex).substring(8);
			final String[] logKeyParts = logKey.split("#");
			final int numControllers = logKeyParts.length - 1;
			
			int numFrames = 0;
			for(String line : lines){
				if(line.startsWith("|")){
					numFrames++;
				}
			}
			
			frames = new Input[numFrames][numControllers];
			for(int i = logKeyIndex + 1; i < lines.size(); i++){
				final String line = lines.get(i);
				if(line.startsWith("|")){
					final String[] inputLineParts = line.substring(1).split("\\|"); // startReset, controller 1, controller 2, etc.
					
					for(int j = 1; j < inputLineParts.length; j++){ // for each controller
						final String inputController = inputLineParts[j]; // controller specific inputs
						
						final String[] inputControllerParts = inputController.split(","); // split controller inputs using ',' to isolate analog values
						final byte xAxis = (byte) Integer.parseInt(inputControllerParts[0].trim());
						final byte yAxis = (byte) Integer.parseInt(inputControllerParts[1].trim());
						
						frames[i - (logKeyIndex + 1)][j - 1] = new InputN64(
								formatBinaryInputs(inputControllerParts[2], InputByteType.N64_BUTTONS_1),
								formatBinaryInputs(inputControllerParts[2], InputByteType.N64_BUTTONS_2),
								xAxis, yAxis);
						/*System.out.printf("i: %d, j: %d, %s %s %s %s\n",
								(i - (logKeyIndex + 1)), (j - 1),
								String.format("%8s", Integer.toBinaryString(frames[i - (logKeyIndex + 1)][j - 1].getBytes()[0] & 0xFF)).replace(' ', '0'),
								String.format("%8s", Integer.toBinaryString(frames[i - (logKeyIndex + 1)][j - 1].getBytes()[1] & 0xFF)).replace(' ', '0'),
								String.format("%8s", Integer.toBinaryString(frames[i - (logKeyIndex + 1)][j - 1].getBytes()[2] & 0xFF)).replace(' ', '0'),
								String.format("%8s", Integer.toBinaryString(frames[i - (logKeyIndex + 1)][j - 1].getBytes()[3] & 0xFF)).replace(' ', '0')
						);*/
					}
				}
			}
			
			isParsed = true;
		}
		
		return frames;
	}
	
	private byte formatBinaryInputs(String inputs, InputByteType type){
		final char[] s = inputs.toCharArray();
		byte b = 0;
		switch(type){
			case N64_BUTTONS_1: // A  B  Z  S dU dD dL dR
				b = byteBuilder(s[11], s[10], s[9], s[8], s[4], s[5], s[6], s[7]);
				break;
			case N64_BUTTONS_2: //RST _ LT RT cU cD cL cR
				b = byteBuilder('.', '.', s[16], s[17], s[12], s[13], s[15], s[14]);
				break;	
		}
		
		return b;
	}
	
	private byte byteBuilder(char c7, char c6, char c5, char c4, char c3, char c2, char c1, char c0){
		return byteBuilder(c7 != '.', c6 != '.', c5 != '.', c4 != '.', c3 != '.', c2 != '.', c1 != '.', c0 != '.');
	}
	
	private byte byteBuilder(boolean b7, boolean b6, boolean b5, boolean b4, boolean b3, boolean b2, boolean b1, boolean b0){
		byte b = 0;
		if(b7) b |= (1 << 7);
		if(b6) b |= (1 << 6);
		if(b5) b |= (1 << 5);
		if(b4) b |= (1 << 4);
		if(b3) b |= (1 << 3);
		if(b2) b |= (1 << 2);
		if(b1) b |= (1 << 1);
		if(b0) b |= (1);
		
		return b;
	}
}
