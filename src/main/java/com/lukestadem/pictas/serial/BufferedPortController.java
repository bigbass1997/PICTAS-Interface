package com.lukestadem.pictas.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BufferedPortController extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(BufferedPortController.class);
	
	public final ConcurrentLinkedDeque<Byte> byteBuffer;
	public final ConcurrentLinkedDeque<Byte> writeBuffer;
	
	private SerialPort comPort;
	private boolean running;
	
	public BufferedPortController(){
		this(null);
	}
	
	public BufferedPortController(SerialPortDataListener listener){
		byteBuffer = new ConcurrentLinkedDeque<>();
		writeBuffer = new ConcurrentLinkedDeque<>();
		
		log.info(Arrays.toString(SerialPort.getCommPorts()));
		comPort = SerialPort.getCommPorts()[1];
		comPort.openPort();
		log.info("Baudrate allowed? " + comPort.setBaudRate(500000));
		
		if(listener != null){
			comPort.addDataListener(listener);
		} else {
			comPort.addDataListener(new SerialPortDataListener() {
				@Override
				public int getListeningEvents(){
					return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
				}
				
				@Override
				public void serialEvent(SerialPortEvent event){
					if(event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED){
						for(byte b : event.getReceivedData()){
							byteBuffer.addLast(b);
						}
					}
				}
			});
		}
		
		running = true;
		this.start();
	}
	
	@Override
	public void run(){
		while(running){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			while(writeBuffer.size() > 0){
				writeByte(writeBuffer.removeFirst());
			}
		}
	}
	
	public void writeByte(byte b){
		comPort.writeBytes(new byte[]{b}, 1);
	}
	
	public void writeBytes(byte... bytes){
		comPort.writeBytes(bytes, bytes.length);
	}
	
	public void writeBytes(Byte... bytes){
		comPort.writeBytes(ArrayUtils.toPrimitive(bytes), bytes.length);
	}
	
	public void close(){
		running = false;
		comPort.removeDataListener();
		comPort.closePort();
	}
}
