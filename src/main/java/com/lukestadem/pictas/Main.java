package com.lukestadem.pictas;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.lukestadem.pictas.movies.Movie;
import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.serial.ImmediatePortController;
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
			//final Movie movie = new Movie(new File("movies/goldeneye007.m64"));
			//final Movie movie = new Movie(new File("movies/Banjo-Kazooie-DeleteSave.bk2"));
			//final Movie movie = new Movie(new File("movies/hyperresonance-banjokazooie-100p.bk2"));
			//final Movie movie = new Movie(new File("movies/wyster-twine.m64"));
			//final Movie movie = new Movie(new File("movies/xenos-diddykongracing.m64"));
			final Movie movie = new Movie(new File("movies/sm64120stars.m64"));
			//final Movie movie = new Movie(new File("movies/test-movie.m64"));
			movie.export(0);
			//movie.export(0);
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
					byteBuf.clear();
					pc.writeByte(ByteUtil.iByte(0xAA));
					
					int counter = 0;
					
					final int pages = (int) Math.ceil(movie.getNumBytes() / 256f);
					System.out.println(movie.getNumFrames() + ", " + pages);
					for(int j = 0; j < pages; j++){
						final byte[] matchArr = new byte[256];
						
						while(byteBuf.isEmpty()){}
						//System.out.println(ByteUtil.bytesToString(ArrayUtils.toPrimitive(byteBuf.toArray(new Byte[0]))));
						if(byteBuf.removeFirst() == ByteUtil.iByte(0x01)){
							pc.writeByte(ByteUtil.iByte(0x01));
							
							final byte[] bytes = new byte[256];
							for(int i = 0; i < bytes.length;){
								if(movie.hasNextFrame()){
									final Input[] frame = movie.nextFrame();
									for(Input controller : frame){
										final byte[] controllerInputs = controller.getBytes();
										//System.out.println(ByteUtil.bytesToString(controllerInputs));
										
										for(int k = 0; k < controllerInputs.length; k++){
											//System.out.println("first: "+ ByteUtil.byteToString(controllerInputs[k]));
											bytes[i] = controllerInputs[k];
											matchArr[i] = controllerInputs[k];
											//System.out.println("match: " + ByteUtil.byteToString(matchArr[i]));
											i++;
										}
									}
								} else {
									bytes[i] = 0;
									matchArr[i] = 0;
									i++;
								}
							}
							//System.out.println(ByteUtil.bytesToString(matchArr));
							pc.writeBytes(bytes);
							
							//System.out.println(counter + ": " + ByteUtil.bytesToString(bytes));
							//counter += 256;
						} else {
							pc.writeByte(ByteUtil.iByte(0x00));
						}
						//System.out.println(ByteUtil.bytesToString(matchArr));
						
						while(byteBuf.size() < 256){}
						for(int i = 0; i < 256; i++){
							byte frompic = byteBuf.removeFirst();
							//log.info("Compare: " + ByteUtil.byteToString(matchArr[i]) + " vs " + ByteUtil.byteToString(frompic));
							
							if(frompic != matchArr[i]){
								log.warn("Write/Read Mismatch! index: " + ((j * 256) + i) + ", Host: " + ByteUtil.byteToString(matchArr[i]) + " vs " + ByteUtil.byteToString(frompic));
							}
						}
					}
					
					while(byteBuf.isEmpty()){}
					if(byteBuf.removeFirst() == ByteUtil.iByte(0x01)){
						pc.writeByte(ByteUtil.iByte(0x00));
					}
					byteBuf.clear();
					log.info("Programming Complete");
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
					log.info("Dumping Complete");
				}
				
				if(cmd.equalsIgnoreCase("run")){
					pc.writeByte(ByteUtil.iByte(0x03));
					while(byteBuf.isEmpty()){}
					byteBuf.clear();
					log.info("Turn on console to start!");
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