package com.lukestadem.pictas.movies.parsers;

import com.lukestadem.pictas.movies.inputs.Input;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
	
	public enum Type {
		M64, BK2,
		FM2,
		R08, R16,
		OTHER
	}
	
	protected final File file;
	
	public final Type type;
	
	public Parser(File file, Type type){
		this.file = file;
		this.type = type;
	}
	
	/**
	 * Retrieves the input data from this parser's file. The returned data represents an array of frames,
	 * where each frame has an array of Input objects which contains the inputs for each controller.
	 * <br><br>
	 * For example, a file containing 589 frames, using 2 controllers, would be represented as <code>Input[589][2]</code>.
	 * Where each frame has two controllers worth of input.
	 * 
	 * @return inputs of every controller for every frame
	 */
	public abstract Input[][] getFrames();
	
	/**
	 * Reads text lines from parser's file into a list of Strings.
	 * <br><br>
	 * This method uses <code>Files.readAllLines()</code> for best performance.
	 * 
	 * @return list of every line from file
	 */
	protected List<String> getFileLines(){
		try {
			return Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Reads text lines into a list of Strings, from a file inside a zip archive. The zip archive is assumed to be this parser's file.
	 * 
	 * @param zipRelativePath path to the text file relative to the root of the zip archive
	 * @return list of every line from file
	 */
	protected List<String> getFileLines(String zipRelativePath){
		try {
			final ZipFile zipFile = new ZipFile(file);
			final ZipInputStream stream = zipFile.getInputStream(zipFile.getFileHeader(zipRelativePath));
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			final List<String> lines = new ArrayList<>();
			String line;
			while((line = reader.readLine()) != null){
				lines.add(line);
			}
			reader.close();
			
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Reads all bytes from parser's file into a byte array.
	 * <br><br>
	 * This method uses <code>Files.readAllBytes()</code> for best performance.
	 * 
	 * @return array of bytes from file
	 */
	protected byte[] getFileBytes(){
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Reads all bytes into a byte array, from a file inside a zip archive. The zip archive is assumed to be this parser's file.
	 * 
	 * @param zipRelativePath path to the file relative to the root of the zip archive
	 * @return array of bytes from file
	 */
	protected byte[] getFileBytes(String zipRelativePath){
		try {
			final ZipFile zipFile = new ZipFile(file);
			final ZipInputStream stream = zipFile.getInputStream(zipFile.getFileHeader(zipRelativePath));
			
			final byte[] arr = new byte[stream.available()];
			stream.read(arr);
			stream.close();
			
			return arr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
