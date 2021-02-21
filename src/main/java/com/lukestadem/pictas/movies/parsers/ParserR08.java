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
					final int offset = o + j;
					
					if(offset < bytes.length){
						frames[i][j] = new InputNES(ByteUtil.iByte(bytes[offset] ^ ByteUtil.iByte(0xFF)));
					} else {
						frames[i][j] = new InputNES(ByteUtil.iByte(0xFF)); // if TAS length is odd, fill in missing input with 0xFF
					}
				}
			}
			
			
			isParsed = true;
		}
		
		return frames;
	}
}
