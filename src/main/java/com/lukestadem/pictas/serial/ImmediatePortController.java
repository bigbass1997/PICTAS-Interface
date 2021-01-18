package com.lukestadem.pictas.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ImmediatePortController extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(ImmediatePortController.class);
	
	public final SerialPort comPort;
	private boolean running;
	
	public ImmediatePortController(){
		this(null);
	}
	
	public ImmediatePortController(SerialPortDataListener listener){
		
		log.info(Arrays.toString(SerialPort.getCommPorts()));
		comPort = SerialPort.getCommPorts()[1];
		comPort.openPort();
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING | SerialPort.TIMEOUT_READ_BLOCKING, 50, 50);
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
							
						}
					}
				}
			});
		}
		
		running = true;
		this.start();
	}
	
	public void setPortListener(SerialPortDataListener listener){
		comPort.removeDataListener();
		comPort.addDataListener(listener);
	}
	
	@Override
	public void run(){
		while(running){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writeByte(byte b){
		comPort.writeBytes(new byte[]{b}, 1);
	}
	
	public void writeBytes(byte... bytes){
		for(byte b : bytes){
			writeByte(b);
		}
	}
	
	//public void writeBytes(Byte... bytes){
	//	comPort.writeBytes(ArrayUtils.toPrimitive(bytes), bytes.length);
	//}
	
	public void close(){
		running = false;
		comPort.removeDataListener();
		comPort.closePort();
	}
}
