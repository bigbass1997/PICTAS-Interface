package com.lukestadem.pictas.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class ByteManager {
	
	private static final Logger log = LoggerFactory.getLogger(ByteManager.class);
	
	private final LinkedList<Byte> bytes;
	
	public ByteManager(){
		bytes = new LinkedList<>();
	}
	
	/**
	 * Append byte to list of bytes contained in this manager.
	 * 
	 * @param b byte to add
	 * @return index location for this byte in manager's byte list
	 */
	public int addByte(byte b){
		bytes.add(b);
		return bytes.size() - 1;
	}
	
	/**
	 * Append byte to list of bytes contained in this manager.
	 * 
	 * @param b byte to add
	 * @return index location for this byte in manager's byte list
	 */
	public int addByte(Byte b){
		bytes.add(b);
		return bytes.size() - 1;
	}
	
	/**
	 * Append bytes to list of bytes contained in this manager.
	 * 
	 * @param bs bytes to add to manager
	 * @return array of index locations for bytes in manager's byte list
	 */
	public int[] addBytes(byte... bs){
		int[] arr = new int[bs.length];
		
		for(int i = 0; i < bs.length; i++){
			arr[i] = addByte(bs[i]);
		}
		
		return arr;
	}
	
	/**
	 * Removes byte located at given index.
	 * 
	 * @param index index of byte to remove
	 */
	public void removeByte(int index){
		bytes.remove(index);
	}
	
	public int getBytesLength(){
		return bytes.size();
	}
	
	public Iterator<Byte> getBytesIterator(){
		return bytes.iterator();
	}
	
	public byte getByte(int index){
		return bytes.get(index);
	}
	
	public boolean writeToFile(String filename){
		return writeToFile(filename, 0, bytes.size() - 1);
	}
	
	public boolean writeToFile(String filename, int firstIndex, int lastIndex){
		if(firstIndex >= bytes.size()){
			log.error("Unable to write bytes to file beginning at an index larger than size of byte list. No file written.");
			return false;
		}
		if(lastIndex < firstIndex){
			log.error("Unable to write bytes to file because ending index is smaller than starting index. No file written.");
			return false;
		}
		
		if(lastIndex >= bytes.size()){
			log.warn("Attempted to write bytes to file ending at index larger than size of byte list. Ending index shrunk to byte length, file will be written.");
			lastIndex = bytes.size() - 1;
		}
		
		if(filename == null || filename.isEmpty()){
			filename = "bytemanager-defaultname";
		}
		filename = filename.trim();
		if(!filename.endsWith(".bin")){
			filename += ".bin";
		}
		
		final byte[] arr = new byte[lastIndex - firstIndex + 1];
		for(int i = 0; i < arr.length; i++){
			arr[i] = bytes.get(i + firstIndex);
		}
		
		try {
			final File file = new File(filename);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			
			final FileOutputStream fos = new FileOutputStream(file);
			fos.write(arr);
			fos.close();
			log.info("File written!");
		} catch (IOException e) {
			log.error("", e);
			return false;
		}
		
		return true;
	}
}
