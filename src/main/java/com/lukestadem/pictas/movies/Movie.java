package com.lukestadem.pictas.movies;

import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.movies.parsers.Parser;
import com.lukestadem.pictas.movies.parsers.ParserMupen64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Movie {
	
	private static final Logger log = LoggerFactory.getLogger(Movie.class);
	
	private final Input[][] frames;
	private int frameIndex;
	
	public Movie(File file){
		final Parser parser;
		if(file.getName().endsWith(".m64")){
			parser = new ParserMupen64(file);
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
		return frameIndex < frames.length;
	}
	
	/**
	 * @return length of movie in frames (ignores number of controllers)
	 */
	public int getMovieLength(){
		return frames.length;
	}
}
