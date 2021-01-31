package com.lukestadem.pictas.movies.parsers;

import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.inputs.InputN64;

import java.io.File;

public class ParserMupen64 extends Parser {
	
	private Input[][] frames;
	
	private boolean isParsed;
	
	public ParserMupen64(File file){
		super(file, Type.M64);
		
		frames = null;
		
		isParsed = false;
	}
	
	@Override
	public Input[][] getFrames(){
		if(!isParsed){
			final byte[] bytes = getFileBytes();
			final int numControllers = getNumControllers(bytes);
			final int headerVersion = getHeaderVersion(bytes);
			final int frameOffset;
			if(headerVersion <= 2){
				frameOffset = 0x200;
			} else {
				frameOffset = 0x400;
			}
			final int numFrames = (bytes.length - frameOffset) / (numControllers * 4);
			
			frames = new Input[numFrames][numControllers];
			for(int i = 0; i < numFrames; i++){
				final int o = frameOffset + (i * numControllers * 4);
				for(int j = 0; j < numControllers; j++){
					final int offset = o + (j * 4);
					
					frames[i][j] = new InputN64(bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3]);
				}
			}
			
			
			isParsed = true;
		}
		
		return frames;
	}
	
	private int getNumControllers(byte[] bytes){
		return bytes[0x15];
	}
	
	private int getHeaderVersion(byte[] bytes){
		return (((bytes[0x07]       ) << 24) |
                ((bytes[0x06] & 0xff) << 16) |
                ((bytes[0x05] & 0xff) <<  8) |
                ((bytes[0x04] & 0xff)      ));
	}
	
	private int getNumFrames(byte[] bytes){
		return (((bytes[0x0F]       ) << 24) |
                ((bytes[0x0E] & 0xff) << 16) |
                ((bytes[0x0D] & 0xff) <<  8) |
                ((bytes[0x0C] & 0xff)      ));
	}
}
