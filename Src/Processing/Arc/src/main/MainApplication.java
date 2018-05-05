package main;

import processing.core.*;
import controlP5.*;

import java.util.*;
import processing.serial.*;
import gnu.io.*;

import ddf.minim.*;

public class MainApplication extends PApplet
{
	
	Nunchuk nc;
	GUI gui;
	
	boolean pause = false;
	boolean showLogo = false;
	
	SFX sfx;
	PImage splash = loadImage("polimilogo.png");
	PFont font;
	static final PVector dimension = new PVector(1000,500);
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "main.MainApplication" });
	}
	
	public void stop()
	{
		sfx.stopAll();
		nc.stop();
		super.stop();
	}
	
	public void setup()
	{
		size((int)(dimension.x), (int)(dimension.y));  
		smooth();
		fill(color(0));
		rect(0,0,width,height);
		
		//image(splash,0,60);
		sfx = new SFX(this);
		//delay(4000);
		
		nc = new Nunchuk(this);
		gui = new GUI(this);
		
		
	}
	
	public void draw()
	{
		
		nc.update();
		sfx.update();
		gui.update();

	}
	
	float sign(float aValue)
	{
		return abs(aValue)/aValue;
	}
	int sign(int aValue)
	{
		return abs(aValue)/aValue;
	}

	void togglePause()
	{
		if (pause == true)
			pause = false;
		else
			pause = true;
		
		sfx.play(SFX.SFX_PAUSE);
		
		delay(5);
	}
	
}




class GUI
{

	MainApplication main;
	ControlP5 controlP5;
	
	int score = 0;
	int time = 0;
	int currTime;
	
	PFont fontArcade;
	PFont fontVerdana; 
	
	float mouse_x;
	float mouse_y;
	
	public Mario marioPG;
	public Cloud cloudPG;
	Ground ground;
	
	int minValAcceleration= -400;
	int maxValAcceleration = 400;
	
	final int colorSliderBack;
	final int colorSliderFore;
	final int colorSliderActive;
	
	final int colorCrossHair1;
	
	final int colorButtonStroke;
	final int colorButtonFill;
	final int colorButtonText;
	
	final int buttonX = 720;
	final int buttonY = 40;
	
	final int joyX = 495;
	final int joyY = 60;
	
	final int crossHairX = 240;
	final int crossHairY = 60;
	final int crossHairRadius = 18;
	final int crossHairRadiusZmin = 40;
	final int crossHairRadiusZmax = 130;
	
	PImage poliLogo;
	
	GUI(MainApplication aMain)
	{
		main = aMain;
		controlP5 = new ControlP5(main);
		
		currTime = main.second();
		
		fontArcade = main.loadFont("Emulogic-16.vlw");
		fontVerdana = main.loadFont("Verdana-12.vlw");
		
		marioPG = new Mario(main);
		cloudPG = new Cloud(main);
		ground = new Ground(main);
		
		colorSliderBack = main.color(200,64,0);
		colorSliderFore = main.color(248,144,72);
		colorSliderActive  = main.color(200,200,200);
		
		colorButtonStroke = main.color(160);
		colorButtonFill = main.color(240);
		colorButtonText = main.color(180);
		
		colorCrossHair1  = main.color(248,200,0);
		
		poliLogo = main.loadImage("logo_polimi.png");
		
		mouse_x = 0;
		mouse_y = 0;
		
		int yOrigin = 20;
		int xOrigin = 310;

		int i = 0;

		int sliderHeight = 11;
		int sliderWidth = 100;
		int vSpacing = 6;

		Textlabel textlabel = null;

		i++;

		Slider slider = null;
		Knob knob = null;
		
		slider = controlP5.addSlider("sliderAccelX",minValAcceleration,maxValAcceleration,0, xOrigin, yOrigin + i * (sliderHeight + vSpacing),sliderWidth,sliderHeight); i++;
		slider.setLabel("Acc. X");
		//slider.setColorCaptionLabel(color(0,0,0));
		slider.setColorForeground(colorSliderFore);
		slider.setColorBackground(colorSliderBack);
		slider.setColorActive(colorSliderFore);
		slider = controlP5.addSlider("sliderAccelY",minValAcceleration,maxValAcceleration,0, xOrigin, yOrigin + i * (sliderHeight + vSpacing),sliderWidth,sliderHeight); i++;
		slider.setLabel("Acc. Y");
		//slider.setColorCaptionLabel(color(0,0,0));
		slider.setColorForeground(colorSliderFore);
		slider.setColorBackground(colorSliderBack);
		slider.setColorActive(colorSliderFore);
		slider = controlP5.addSlider("sliderAccelZ",minValAcceleration,maxValAcceleration,0, xOrigin, yOrigin + i * (sliderHeight + vSpacing),sliderWidth,sliderHeight); i++;
		slider.setLabel("Acc. Z");
		//slider.setColorCaptionLabel(color(0,0,0));
		slider.setColorForeground(colorSliderFore);
		slider.setColorBackground(colorSliderBack);
		slider.setColorActive(colorSliderFore);
		
		i = 1;
		
		slider = controlP5.addSlider("sliderJoyX",-100,100,0, joyX + 40, sliderHeight/2 + vSpacing + yOrigin + i * (sliderHeight + vSpacing),sliderWidth,sliderHeight); i++;
		slider.setLabel("Joy. X");
		//slider.setColorCaptionLabel(color(0,0,0));
		slider.setColorForeground(colorSliderFore);
		slider.setColorBackground(colorSliderBack);
		slider.setColorActive(colorSliderFore);
		
		slider = controlP5.addSlider("sliderJoyY",-100,100,0, joyX + 40, sliderHeight/2 + vSpacing + yOrigin + i * (sliderHeight + vSpacing),sliderWidth,sliderHeight); i++;
		slider.setLabel("Joy. Y");
		//slider.setColorCaptionLabel(color(0,0,0));
		slider.setColorForeground(colorSliderFore);
		slider.setColorBackground(colorSliderBack);
		slider.setColorActive(colorSliderFore);
		
		textlabel = controlP5.addTextlabel("labelRun","RUN",buttonX+40,buttonY-2);
		textlabel.setColorValueLabel(0xFFFFFFFF);
		
		textlabel = controlP5.addTextlabel("labelFire","FIRE",buttonX+40,buttonY-2+45);
		textlabel.setColorValueLabel(0xFFFFFFFF);
		
	}

	
	void update()
	{
		if (main.second() - currTime != 0)
		{
			time++;
			currTime = main.second();
		}
		
		//cloudPG.update();
		//cloudPG.draw();
		
		ground.update();
		ground.draw();
		
		drawGUI();
		
		
		marioPG.update();
		marioPG.draw();
		
	}
	
	void drawGUI()
	{
		main.image(poliLogo, 0,0);
		
		PVector acc = main.nc.getAccel();
		float roll = main.nc.getRoll();
		float pitch = main.nc.getPitch();
		PVector joy = main.nc.getJoy();
		boolean buttonC = main.nc.getButtonC();
		boolean buttonZ = main.nc.getButtonZ();
		
		main.textFont(fontArcade, 12); 
		main.fill(main.color(255));
		main.text("SCORE: " + score, 20, 180);
		main.text("TIME: " + time, 1000 - 120, 180);
		main.textFont(fontVerdana, 12);
		
		mouse_x+= joy.x/30;
		mouse_y-= joy.y/30;
		
		controlP5.controller("sliderAccelX").setValue(acc.x);
		controlP5.controller("sliderAccelY").setValue(acc.y);
		controlP5.controller("sliderAccelZ").setValue(acc.z);
		controlP5.controller("sliderJoyX").setValue(joy.x);
		controlP5.controller("sliderJoyY").setValue(joy.y);
		
		
		//CROSSHAIR
		float crossHairRadiusZ = crossHairRadiusZmin + (crossHairRadiusZmax - crossHairRadiusZmin) * (acc.z/maxValAcceleration);
		
		if (crossHairRadiusZ < crossHairRadiusZmin)
			crossHairRadiusZ = crossHairRadiusZmin;
		if (crossHairRadiusZ > crossHairRadiusZmax)
			crossHairRadiusZ = crossHairRadiusZmax;
		
		main.stroke(colorSliderBack);
		main.strokeWeight(5);
		main.noFill();
		main.ellipse(crossHairX, crossHairY, 100, 100);
		
		float ax = (acc.x / maxValAcceleration) * 40;
		float ay = (acc.y / maxValAcceleration) * 40;
		
		main.strokeWeight(2);
		
		main.stroke(colorCrossHair1);
		main.noFill();
		main.ellipse(crossHairX,crossHairY, crossHairRadiusZ, crossHairRadiusZ);
		
		main.stroke(colorSliderBack);
		main.fill(colorSliderFore);
		main.ellipse(crossHairX + ax,crossHairY - ay, crossHairRadius, crossHairRadius);
		
		main.stroke(colorButtonStroke);
		if (buttonC == false)
			main.fill(colorButtonFill);
		else
			main.fill(colorCrossHair1);
		main.ellipse(buttonX,buttonY, 40, 28);
		main.fill(colorButtonText);
		main.text("C" , buttonX-4 ,buttonY+5);
		
		
		int yAdd = 30;
		
		main.stroke(colorButtonStroke);
		if (buttonZ == false)
			main.fill(colorButtonFill);
		else
			main.fill(colorCrossHair1);
		main.rect(buttonX-25,buttonY+yAdd, 50, 35);
		main.fill(colorButtonText);
		main.text("Z" , buttonX-3 ,buttonY+yAdd+22);
		
		main.strokeWeight(3);
		main.stroke(colorButtonStroke);
		main.fill(colorButtonFill);
		int joyRadius = 30;
		main.beginShape();
		for (int i = 0; i < 360; i += 360/8 )
			main.vertex(joyX + joyRadius * main.cos(i * main.PI / 180),
					joyY - joyRadius * main.sin(i * main.PI / 180));
		main.endShape(main.CLOSE);
		
		main.strokeWeight(1);
		main.ellipse(joyX + joy.x / 100 * joyRadius * 0.9f,
					joyY - joy.y / 100 * joyRadius * 0.9f,
					20,20);
		main.strokeWeight(2);
		main.ellipse(joyX + joy.x / 100 * joyRadius * 0.9f,
				joyY - joy.y / 100 * joyRadius * 0.9f,
				12,12);
		
		//main.text("Mario X: " + marioPG.getPos().x, 500 ,200);
		//main.text("Mario Y: " + marioPG.getPos().y, 500 ,220);
		
		//main.fill(main.color(0));
		//main.ellipse(mouse_x ,mouse_y, 5, 5);
	}

}