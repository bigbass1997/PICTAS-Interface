package com.lukestadem.pictas;

import com.lukestadem.pictas.movies.Movie;
import com.lukestadem.pictas.movies.inputs.Input;
import com.lukestadem.pictas.serial.BufferedPortController;
import com.lukestadem.rendgine.Engine;
import com.lukestadem.rendgine.graphics.Color;
import com.lukestadem.rendgine.graphics.ModernImmediateRenderer2D;
import com.lukestadem.rendgine.graphics.TextureRenderer;
import com.lukestadem.rendgine.graphics.camera.Camera;
import com.lukestadem.rendgine.graphics.camera.OrthographicCamera;
import com.lukestadem.rendgine.graphics.font.BitmapFont;
import com.lukestadem.rendgine.graphics.font.FontManager;
import org.joml.Vector2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL46.*;

public class Program {
	
	private static final Logger log = LoggerFactory.getLogger(Program.class);
	
	private final Engine engine;
	private final FontManager fm;
	private final TextureRenderer tr;
	private final ModernImmediateRenderer2D mir;
	
	private final Camera guiCamera;
	
	private final Color textColor;
	
	private final Vector2i counter = new Vector2i(0, 0);
	
	private int skipCount = 0;
	
	private String statusText;
	
	public Program(){
		final int width = 1200;
		final int height = 900;
		
		engine = new Engine("PICTAS Interface", width, height, false, false, new OrthographicCamera(width, height, 0, 0, 0), 4);
		fm = new FontManager();
		
		final BitmapFont font = fm.loadFont("consolas12", "fonts/consolas-12-native.png", "fonts/consolas-12-native.fnt");
		font.letterSpacing = 0.5f;
		
		tr = new TextureRenderer();
		mir = new ModernImmediateRenderer2D(1000, 0, true, false, false);
		
		guiCamera = new OrthographicCamera(width, height, (width / 2f), (height / 2f), 0);
		
		textColor = new Color(1, 0, 1, 1);
		
		statusText = "";
		
		engine.addTask(engine -> {
			glEnable(GL_MULTISAMPLE);
		});
		
		final BitmapFont logFont = fm.loadFont("consolas14", "fonts/consolas-14-native.png", "fonts/consolas-14-native.fnt");
		logFont.letterSpacing = -2f;
		
		engine.addTask(engine -> {
			engine.runDefaultCameraMovement();
			
			statusText = "" + mir.getMesh().getVerticesPushed() + ", " + String.format("[%.3f, %.3f, %.3f]", engine.camera.position.x, engine.camera.position.y, engine.camera.position.z);
			
			tr.begin(guiCamera.combined);
			font.render(tr, String.format("%.15f", engine.getDelta()), guiCamera.position.x - (engine.window.getWidth() * 0.5f) + 5, guiCamera.position.y + (engine.window.getHeight() * 0.5f) - 20, textColor);
			font.render(tr, String.format("%.2f", engine.getFPS()), guiCamera.position.x - (engine.window.getWidth() * 0.5f) + 5, guiCamera.position.y + (engine.window.getHeight() * 0.5f) - 32, textColor);
			font.render(tr, statusText, guiCamera.position.x - (engine.window.getWidth() * 0.5f) + 5, guiCamera.position.y + (engine.window.getHeight() * 0.5f) - 44, textColor);
			tr.end();
			
			counter.y++;
		});
		
		final Movie movie = new Movie(new File("bobmario511-yoshisstory.m64"));
		final BufferedPortController pc = new BufferedPortController();
		engine.addTask(engine -> {
			if(engine.window.windowShouldClose()){
				pc.close();
			}
			
			while(pc.byteBuffer.size() > 0){
				final byte b = pc.byteBuffer.removeFirst();
				
				if(skipCount < 2){
					skipCount++;
					pc.writeBuffer.add(ByteUtil.iByte(0x00));
					pc.writeBuffer.add(ByteUtil.iByte(0x00));
					pc.writeBuffer.add(ByteUtil.iByte(0x00));
					pc.writeBuffer.add(ByteUtil.iByte(0x00));
					continue;
				}
				if(ByteUtil.equalsByte(0xFE, b)){
					final Input[] nextFrame = movie.nextFrame();
					if(nextFrame != null){
						for(Input input : nextFrame){
							for(byte bWrite : input.getBytes()){
								pc.writeBuffer.add(bWrite);
							}
							//log.info("Sending bytes: " + ByteUtil.bytesToString(input.getBytes()));
						}
					}
				}
			}
		});
	}
	
	public void start(){
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		glDepthFunc(GL_LEQUAL);
		glDepthRange(0.0f, 1.0f);
		
		engine.start();
	}
	
	public void screenshot(int ox, int oy, int width, int height){
		int[] pixelBuffer = new int[width * height];
		glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
		glReadPixels(ox, oy, width, height, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, pixelBuffer);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < pixelBuffer.length; i++){
			final int rgba8888 = pixelBuffer[i];
			final int r = ((rgba8888 & 0xff000000) >>> 24);
			final int g = ((rgba8888 & 0x00ff0000) >>> 16);
			final int b = ((rgba8888 & 0x0000ff00) >>> 8);
			final int a = ((rgba8888 & 0x000000ff));
			
			final int x = i % width;
			final int y = i / width;
			
			image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
		}
		
		try {
			ImageIO.write(image, "PNG", new File("output/image-" + counter.x + ".png"));
			counter.x++;
		} catch (IOException e) { e.printStackTrace(); }
	}
}
