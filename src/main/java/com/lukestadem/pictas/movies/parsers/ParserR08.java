package com.lukestadem.pictas.movies.parsers;

import com.lukestadem.pictas.ByteUtil;
import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.inputs.InputNES;

import java.io.File;

public class ParserR08 extends Parser {
	
	private Input[][] frames;
	
	private boolean isParsed;
	
	private final int movieControllers;
	
	public ParserR08(File file, int movieControllers){
		super(file, Type.R08);
		
		this.movieControllers = movieControllers;
		
		frames = null;
		
		isParsed = false;
	}
	
	@Override
	public Input[][] getFrames(){
		if(!isParsed){
			final byte[] bytes = getFileBytes();
			final int numFrames = (bytes.length) / (2);
			
			frames = new Input[numFrames][movieControllers];
			for(int i = 0; i < numFrames; i++){
				final int o = (i * 2);
				for(int j = 0; j < movieControllers; j++){
					final int offset = o + (j * 2);
					
					frames[i][j] = new InputNES(ByteUtil.iByte(bytes[offset] ^ ByteUtil.iByte(0xFF))); // TODO dynamically support 2 controllers
				}
			}
			
			
			isParsed = true;
		}
		
		return frames;
	}
}
