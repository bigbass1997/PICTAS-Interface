package com.lukestadem.pictas.movies.parsers;

import com.lukestadem.pictas.ByteUtil;
import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.inputs.InputNES;

import java.io.File;
import java.util.List;

public class ParserFceux extends Parser {
	
	private Input[][] frames;
	
	private boolean isParsed;
	
	public ParserFceux(File file){
		super(file, Type.FM2);
		
		frames = null;
		
		isParsed = false;
	}
	
	@Override
	public Input[][] getFrames(){
		//TODO: Add support for when only port 2 is used but not port 1
		//TODO: Add support for console resets
		//TODO: Add support for zapper
		
		if(!isParsed){
			final List<String> lines = getFileLines();
			
			int numControllers = 0;
			int inputStartIndex = 0;
			for(int i = 0; i < lines.size(); i++){
				final String line = lines.get(i);
				inputStartIndex = i;
				if(line.startsWith("|")){
					break;
				} else if(line.equalsIgnoreCase("port0 1")){
					numControllers += 1;
				} else if(line.equalsIgnoreCase("port1 1")){
					numControllers += 1;
				}
			}
			
			int numFrames = 0;
			for(String line : lines){
				if(line.startsWith("|")){
					numFrames++;
				}
			}
			
			System.out.println(numFrames + ", " + numControllers);
			
			frames = new Input[numFrames][numControllers];
			for(int i = inputStartIndex; i < lines.size(); i++){
				final String line = lines.get(i);
				if(line.startsWith("|")){
					final String[] inputLineParts = line.substring(1).split("\\|"); // startReset, controller 1, controller 2, etc.
					
					for(int j = 1; j < inputLineParts.length; j++){ // for each controller
						final String inputController = inputLineParts[j]; // controller specific inputs
						
						frames[i - (inputStartIndex)][j - 1] = new InputNES(formatBinaryInputs(inputController));
					}
				}
			}
			
			isParsed = true;
		}
		
		return frames;
	}
	
	private byte formatBinaryInputs(String inputs){
		final char[] s = inputs.toCharArray();
						  // A     B     Se    St    up    down  left  right
		byte b = byteBuilder(s[7], s[6], s[5], s[4], s[3], s[2], s[1], s[0]);
		
		b ^= ByteUtil.iByte(0xFF); // invert bits, 0 is considered "pressed" and it would waste cycles if the pictas had to manipulate this
		
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
