package com.lukestadem.pictas;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.lukestadem.pictas.movies.Movie;
import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.serial.BufferedPortController;
import com.lukestadem.pictas.serial.ImmediatePortController;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static int skipCount = 0;
	
	private static final byte[] test = new byte[]{0x00,0x00,0x00,0x00};
	private static final ConcurrentLinkedDeque<Byte> byteBuf = new ConcurrentLinkedDeque<>();
	
	public static void main(String[] args) {
		if(argExists(args, "--headless")){
			final Movie movie = new Movie(new File("bobmario511-yoshisstory.m64"));
			final ImmediatePortController pc = new ImmediatePortController();
			final SerialPortDataListener listener = new SerialPortDataListener() {
				@Override
				public int getListeningEvents(){
					return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
				}
				
				@Override
				public void serialEvent(SerialPortEvent event){
					if(event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED){
						/*for(byte b : event.getReceivedData()){
							if(ByteUtil.equalsByte(0xFE, b)){
								if(skipCount < 2){
									skipCount++;
									pc.writeBytes(ByteUtil.iByte(0x00, 0x00, 0x00, 0x00));
									continue;
								}
								
								final Input[] nextFrame = movie.nextFrame();
								if(nextFrame != null){
									for(int i = 0; i < nextFrame.length; i++){
										final byte[] inputs = nextFrame[i].getBytes();
										pc.writeBytes(inputs);
										/*for(int j = 0; j < inputs.length; j++){
											pc.writeByte(inputs[j]);
										}*/
									/*}
								}
							}
						}*/
						//pc.comPort.writeBytes(test, test.length);
						//log.info("printing test");
						final byte[] received = event.getReceivedData();
						//log.info(ByteUtil.bytesToString(received));
						for(int i = 0; i < received.length; i++){
							byteBuf.addLast(received[i]);
						}
					}
				}
			};
			pc.setPortListener(listener);
			
			final Scanner scanner = new Scanner(System.in);
			while(true){
				final String cmd = scanner.nextLine();
				if(cmd.equalsIgnoreCase("stop")){
					pc.close();
					break;
				}
				
				if(cmd.equalsIgnoreCase("ping")){
					pc.writeByte(ByteUtil.iByte(0x01));
				}
				
				if(cmd.equalsIgnoreCase("program")){
					pc.writeByte(ByteUtil.iByte(0xAA));
					
					final int pages = (int) Math.ceil(movie.getMovieLength() / 256f);
					for(int j = 0; j < pages; j++){
						while(byteBuf.isEmpty()){}
						//System.out.println(ByteUtil.bytesToString(ArrayUtils.toPrimitive(byteBuf.toArray(new Byte[0]))));
						if(byteBuf.removeFirst() == ByteUtil.iByte(0x01)){
							pc.writeByte(ByteUtil.iByte(0x01));
							
							final byte[] bytes = new byte[256];
							for(int i = 0; i < bytes.length - 4;){
								if(movie.hasNextFrame()){
									final Input[] frame = movie.nextFrame();
									for(Input controller : frame){
										final byte[] controllerInputs = controller.getBytes();
										for(int k = 0; k < controllerInputs.length; k++){
											bytes[i + k] = controllerInputs[k];
											i++;
										}
									}
								} else {
									bytes[i] = 0;
									i++;
								}
							}
							pc.writeBytes(bytes);
						} else {
							pc.writeByte(ByteUtil.iByte(0x00));
						}
					}
					
					while(byteBuf.isEmpty()){}
					if(byteBuf.removeFirst() == ByteUtil.iByte(0x01)){
						pc.writeByte(ByteUtil.iByte(0x00));
					}
					byteBuf.clear();
				}
				
				if(cmd.equalsIgnoreCase("dump")){
					byteBuf.clear();
					pc.writeByte(ByteUtil.iByte(0x02));
					while(byteBuf.size() < 16 * 1024 * 1024){
						final String s = scanner.nextLine();
						if(s.equalsIgnoreCase("end")){
							log.info("FinalSize: " + byteBuf.size());
							break;
						} else {
							log.info("BufSize: " + byteBuf.size());
						}
					}
					ByteUtil.writeToFile("flash-dump.bin", byteBuf.toArray(new Byte[0]));
					byteBuf.clear();
				}
				
				if(cmd.equalsIgnoreCase("run")){
					pc.writeByte(ByteUtil.iByte(0x03));
					while(byteBuf.isEmpty()){}
					byteBuf.clear();
				}
			}
			scanner.close();
		} else {
			(new Program()).start();
		}
	}
	
	private static boolean argExists(String[] args, String s){
		for(String arg : args){
			if(arg.contains(s)){
				return true;
			}
		}
		
		return false;
	}
}