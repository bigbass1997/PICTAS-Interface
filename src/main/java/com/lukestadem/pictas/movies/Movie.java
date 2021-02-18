package com.lukestadem.pictas.movies;

import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.parsers.Parser;
import com.lukestadem.pictas.movies.parsers.ParserBizhawk;
import com.lukestadem.pictas.movies.parsers.ParserFceux;
import com.lukestadem.pictas.movies.parsers.ParserMupen64;
import com.lukestadem.pictas.movies.parsers.ParserR08;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Movie {
	
	private static final Logger log = LoggerFactory.getLogger(Movie.class);
	
	private final Input[][] frames;
	private int frameIndex;
	
	public Movie(File file){
		final Parser parser;
		if(file.getName().endsWith(".m64")){
			parser = new ParserMupen64(file);
		} else if(file.getName().endsWith(".bk2")) {
			parser = new ParserBizhawk(file);
		} else if(file.getName().endsWith(".fm2")) {
			parser = new ParserFceux(file);
		} else if(file.getName().endsWith(".r08")) {
			parser = new ParserR08(file, 1);
		} else {
			parser = null;
		}
		
		if(parser == null){
			log.error("Movie file format is not supported! " + file.getPath());
			frames = new Input[0][0];
		} else {
			frames = parser.getFrames();
		}
		
		frameIndex = -1;
	}
	
	/**
	 * @return next frame of inputs, or null if no frames remain
	 */
	public Input[] nextFrame(){
		frameIndex++;
		
		if(frameIndex >= frames.length){
			return null;
		}
		
		return frames[frameIndex];
	}
	
	public boolean hasNextFrame(){
		if(nextFrame() != null){
			frameIndex--;
			return true;
		}
		frameIndex--;
		return false;
		//return frameIndex < frames.length;
	}
	
	/**
	 * @return length of movie in frames (ignores number of controllers)
	 */
	public int getNumFrames(){
		return frames.length;
	}
	
	public int getNumBytes(){
		return frames.length * frames[0].length * frames[0][0].getBytes().length;
	}
	
	public void export(int offset){
		List<Byte> list = new ArrayList<>();
		for(int i = 0; i < offset; i++){
			list.add((byte) 0);
		}
		
		for(int i = 0; i < frames.length; i++){
			for(int j = 0; j < frames[i].length; j++){
				//System.out.printf("i: %d, j: %d, %b\n", i, j, frames[i][j] == null);
				for(byte b : frames[i][j].getBytes()){
					list.add(b);
				}
			}
		}
		byte[] arr = ArrayUtils.toPrimitive(list.toArray(new Byte[0]));
		
		try {
			Files.write(new File("parsed-output.bin").toPath(), arr, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
