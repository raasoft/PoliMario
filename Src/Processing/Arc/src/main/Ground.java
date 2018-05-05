package main;

import processing.core.*;

public class Ground {
	
	static float y = 476;
	static float x0 = 30;
	static float x1 = 920; 
	MainApplication main;
	private PImage back;
	
	final int noCoins = 7;
	Animation coin;
	PVector coinsPos[];
	
	Ground(MainApplication aMain)
	{
		main = aMain;
		back = main.loadImage("Background.png");

		coinsPos = new PVector[noCoins];
		coin = new Animation("Coin_",8,main);
				coinsPos[0] = new PVector(580,260);
		coinsPos[1] = new PVector(830,160);
		coinsPos[2] = new PVector(380,160);
		coinsPos[3] = new PVector(25,Ground.y - 140);
		coinsPos[4] = new PVector(160,Ground.y - 50);
		coinsPos[5] = new PVector(200,Ground.y - 50);
		coinsPos[6] = new PVector(840,Ground.y - 50);
	}
	
	public void update()
	{
		PVector pos = main.gui.marioPG.getPos();
		pos.x+= 50;
		pos.y-= 60;
		for (int i = 0; i < noCoins; i++)
		{
			
			if (main.dist(pos.x, pos.y, coinsPos[i].x, coinsPos[i].y) < 30) 
			{
				coinsPos[i].x = -40;
				coinsPos[i].y = -40;
				main.sfx.play(SFX.SFX_COIN);
				main.gui.score++;
			}
		}
	}
	
	public void draw()
	{
		main.image(back, 0, 0);
		
		for (int i = 0; i < noCoins; i++)
		{
			//main.text("Coin X: " + coinsPos[i].x, coinsPos[i].x ,coinsPos[i].y);
			//main.text("Coin Y: " + coinsPos[i].y, coinsPos[i].x ,coinsPos[i].y + 20);
			
			coin.setImageSpeed(0.01f);
			coin.display(coinsPos[i].x, coinsPos[i].y);
		}
	}

}
