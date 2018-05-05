package main;

import ddf.minim.*;

public class SFX
{
	Minim minim;
	MainApplication main;
	
	private int SFX_NO = 5;
	static final int SFX_JUMP = 0;
	static final int SFX_PAUSE = 1;
	static final int SFX_COIN = 2;
	static final int SFX_BG = 3;
	static final int SFX_FIRE = 4;

	AudioSample[] samples;
	
	SFX(MainApplication aMain)
	{
		main = aMain;
		minim = new Minim(main);
		samples = new AudioSample[SFX_NO];
		samples[SFX_JUMP] = minim.loadSample("Jump.wav");
		samples[SFX_PAUSE] = minim.loadSample("Pause.wav");
		samples[SFX_COIN] = minim.loadSample("Coin.wav");
		samples[SFX_FIRE] = minim.loadSample("Fireball.wav");
		
		AudioPlayer bg =  minim.loadFile("BG.mp3");
		bg.play();
//		delay(10);
		//main.sfx.play(SFX.SFX_BG);
		
		
		
	}
	
	public void play(int aSnd)
	{
		samples[aSnd].trigger();
	}
	
	void stopAll()
	{
	  // always close Minim audio classes when you are done with them
		for (AudioSample s : samples)
			s.close();
		
	  minim.stop();
	}
	
	void update()
	{
		
	}
}