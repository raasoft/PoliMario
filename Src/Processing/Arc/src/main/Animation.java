package main;

import processing.core.*;

public class Animation {
	PImage[] images;
	int imageCount;
	float frame;
	float imageSpeed;
	PVector scale;
	
	MainApplication main;

	Animation(String imagePrefix, int count, MainApplication aMain) {
		main = aMain;
		imageCount = count;
		images = new PImage[imageCount];
		imageSpeed = 1;
		scale = new PVector (1,1);

		for (int i = 0; i < imageCount; i++) {
			// Use nf() to number format 'i' into four digits
			String filename = imagePrefix + PApplet.nf(i, 1) + ".png";
			images[i] = main.loadImage(filename);
		}
	}

	void display(float xpos, float ypos) {
		frame = (frame + imageSpeed) % imageCount;
		main.pushMatrix();
		main.scale(scale.x, scale.y);
		
		float offsetX = 0;
		if (scale.x < 0)
			offsetX = getWidth() * main.sign(scale.x);
		main.image(images[(int)(frame)], offsetX + main.sign(scale.x) *  xpos / main.abs(scale.x) , ypos / main.abs(scale.y));
		main.popMatrix();
	}

	int getWidth() {
		return images[0].width;
	}
	int getHeight() {
		return images[0].height;
	}
	
	void setImageSpeed(float aValue)
	{
		imageSpeed = aValue;
	}
	float imageSpeed()
	{
		return imageSpeed;
	}
	void setImageFrame(float aValue)
	{
		frame = aValue;
	}
	float imageFrame()
	{
		return frame;
	}
	void setScale(PVector aVector)
	{
		scale.x = aVector.x;
		scale.y = aVector.y;
	}
	PVector getScale()
	{
		return scale;
	}
}
