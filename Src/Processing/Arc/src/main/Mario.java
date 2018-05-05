package main;

import processing.core.*;
import java.util.*;

class Mario
{
	MainApplication main;
	
	PVector acc;
	PVector pos;
	PVector vel;
	PVector accMax; 
	PVector velMax; 
	
	static final float friction = 0.07f;
	
	boolean jump;
	
	Animation anim[];
	int currAnim;
	
	final int ANIM_WALK = 0;
	final int ANIM_JUMP = 1;
	final float imgSpd = 0.5f;
	
	ArrayList<Fireball> fireBallList;
	Animation animFireBall;
	
	final float ACC_THRESOLD = 50;
	Mario(MainApplication aMain)
	{
		main = aMain;
		
		fireBallList = new ArrayList<Fireball>();
		animFireBall = new Animation("Fire_",4,main);
		animFireBall.setImageSpeed(0.3f);
		
		jump = false;
		pos = new PVector(0,Ground.y);
		vel = new PVector(0,0);
		acc = new PVector(0,0);
		velMax = new PVector(2,15);
		accMax = new PVector(3,3);
		anim = new Animation[3];
		anim[ANIM_WALK] = new Animation("Mario_WALK_",3,main);
		anim[ANIM_WALK].setImageSpeed(imgSpd);
		anim[ANIM_WALK].setScale(new PVector(1,1));
		
		anim[ANIM_JUMP] = new Animation("Mario_JUMP_",1,main);
		anim[ANIM_JUMP].setScale(new PVector(1,1));
		
		currAnim = ANIM_WALK;
	}
	
	void setCurrAnim(int aAnim)
	{
		currAnim = aAnim;
	}
	int getCurrAnim()
	{
		return currAnim;
	}
	
	PVector getPos()
	{
		PVector vect = new PVector(pos.x,pos.y);
		return vect;
	}
	
	void setPos(PVector aVect)
	{
		pos.x = aVect.x;
		pos.y = aVect.y;
	}

	PVector getAcc()
	{
		PVector vect = new PVector(acc.x,acc.y);
		return vect;
	}
	void setAcc(PVector aVect)
	{
		acc.x = aVect.x;
		acc.y = aVect.y;
	}
	PVector getVel()
	{
		PVector vect = new PVector(vel.x,vel.y);
		return vect;
	}
	void setVel(PVector aVect)
	{
		vel.x = aVect.x;
		vel.y = aVect.y;
	}
	
	void update()
	{
		//main.nc.update();
				
		float ncRoll = main.nc.getRoll();
		PVector ncAcc = main.nc.getAccel();
		boolean ncZisP = main.nc.buttonZisPressed();
		boolean ncCisP = main.nc.buttonCisPressed();
		boolean ncZ = main.nc.getButtonZ();
		boolean ncC = main.nc.getButtonC();
		
		/*if (ncCisP == true)
		{
			main.togglePause();
		}*/
		
		if (main.pause == true)
			return;
		
		if (ncZisP == true)
		{
			//main.togglePause();
			int offsetX = +10;
			
			if (main.sign(getVel().x) > 0)
			{
				offsetX += 70;
			}
			
			PVector pos = new PVector(getPos().x + offsetX, getPos().y - 50);
			PVector vel = new PVector(main.sign(getVel().x)*5, 0);
			
			Fireball newfireball = new Fireball(pos,vel);
			fireBallList.add(0,newfireball);
			main.sfx.play(SFX.SFX_FIRE);
		}
		
		for (Fireball ball : fireBallList)
		{
			ball.update();
			animFireBall.display(ball.getPos().x,ball.getPos().y);
		}
		
		acc.x = 0;
		acc.y = +0.4f;
		
		if (main.abs(ncAcc.x) > ACC_THRESOLD)
		{
			acc.x = main.abs(ncAcc.x) * main.sign(ncRoll) / 1000;
		}
		
		if (main.abs(acc.x) > accMax.x)
			acc.x = accMax.x * main.sign(acc.x);
		
		if (main.abs(acc.y) > accMax.y)
			acc.y = accMax.y * main.sign(acc.y);
		
		vel.x += acc.x;
		vel.y += acc.y;
		
		if (jump == false && (ncAcc.z >  ACC_THRESOLD ))
		{
			main.sfx.play(SFX.SFX_JUMP);
			jump = true;
			vel.y -= 10;
			if (ncAcc.z >  ACC_THRESOLD * 5.5)
				vel.y -= 6;
		}
	
		float max_vel_nc =  main.abs(ncAcc.x) / 200;
		if (max_vel_nc > 1 || jump == true || main.abs(acc.x) < 0.03)
			max_vel_nc = 1;
			
		int velMultipler = 1;
		if (ncC == true || jump == true)
			velMultipler = 3;
		if (main.abs(vel.x) > velMax.x * max_vel_nc * velMultipler)
			vel.x = velMax.x * main.sign(vel.x) * max_vel_nc * velMultipler;
		
		if (main.abs(vel.y) > velMax.y)
			vel.y = velMax.y * main.sign(vel.y);
		
		if (main.abs(acc.x) < 0.015 && jump == false)
		{
			vel.x = vel.x * (1-friction);
		}
		
		pos.x += vel.x;
		pos.y += vel.y;		
		
		if (pos.y > Ground.y)
		{
			pos.y = Ground.y;
			vel.y = 0;
			jump = false;
		}
		
		//RED BLOCK
		if (pos.y > Ground.y - 96 && pos.x > 420 && pos.x < 520 && vel.y > 0)
		{
			pos.y = Ground.y - 96;
			vel.y = 0;
			jump = false;
		}
		
		//BLUE BLOCK
		if (pos.y < Ground.y - 120 && pos.y > Ground.y - 158 && pos.x > 420+60 && pos.x < 520+60 && vel.y > 0)
		{
			pos.y = Ground.y - 158;
			vel.y = 0;
			jump = false;
		}
		
		//CASTLE - FIRST FLOOR
		if (pos.y > Ground.y - 96 && pos.x > 700 && pos.x < 870 && vel.y > 0)
		{
			pos.y = Ground.y - 96;
			vel.y = 0;
			jump = false;
		}
		
		//CASTLE - SECOND FLOOR
		if (pos.y < Ground.y - 120 && pos.y > Ground.y - 158 && pos.x > 730 && pos.x < 850 && vel.y > 0)
		{
			pos.y = Ground.y - 158;
			vel.y = 0;
			jump = false;
		}
		
		//TUBE
		if (pos.y > Ground.y - 96 && pos.x > -100 && pos.x < Ground.x0 && vel.y > 0)
		{
			pos.y = Ground.y - 96;
			vel.y = 0;
			jump = false;
		}
		
		//
		if (pos.x < Ground.x0 && pos.y > Ground.y - 50 )
		{
			pos.x = Ground.x0;
			vel.x *= -0.3;
		}
		else if (pos.x < -40 )
		{
			pos.x = -40;
			vel.x *= -0.3;
		}
		else if (pos.x > Ground.x1)
		{
			pos.x = Ground.x1;
			vel.x *= -0.3;
		}
		
	}
	
	void draw()
	{

		if (main.pause == true)
		{
			anim[currAnim].setImageSpeed(0);
		}
		else
		{
		
			if (jump == true)
				currAnim = ANIM_JUMP;
			else
				currAnim = ANIM_WALK;
			
			if (acc.x == 0)
			{
				anim[currAnim].setImageSpeed(0);
				anim[currAnim].setImageFrame(0);
			}
			else
			{
				PVector oldScale = anim[currAnim].getScale();
				anim[currAnim].setScale(new PVector(main.abs(oldScale.x) * main.sign(acc.x) * -1, oldScale.y));
				
				float spdOffset = 0.025f;
				anim[currAnim].setImageSpeed( spdOffset + (imgSpd-spdOffset) * main.abs(vel.x) / ( velMax.x * 3 )); // aumentare la vel all'aumentare della velocità
			}
			
		}
		
		anim[currAnim].display(pos.x +anim[currAnim].getWidth()/2 ,pos.y - anim[currAnim].getHeight() );
	}
}

class Fireball
{
	PVector pos;
	PVector vel;
	PVector acc;
	
	Fireball(PVector aPos, PVector aVel0)
	{
		
		pos = new PVector(aPos.x, aPos.y);
		vel = new PVector(aVel0.x, aVel0.y);
		acc = new PVector(0,0);
	}
	
	PVector getPos()
	{
		PVector vect = new PVector(pos.x,pos.y);
		return vect;
	}
	void setPos(PVector aVect)
	{
		pos.x = aVect.x;
		pos.y = aVect.y;
	}
	PVector getAcc()
	{
		PVector vect = new PVector(acc.x,acc.y);
		return vect;
	}
	void setAcc(PVector aVect)
	{
		acc.x = aVect.x;
		acc.y = aVect.y;
	}
	PVector getVel()
	{
		PVector vect = new PVector(vel.x,vel.y);
		return vect;
	}
	void setVel(PVector aVect)
	{
		vel.x = aVect.x;
		vel.y = aVect.y;
	}
	
	void update()
	{
		acc.x = 0;
		acc.y = +0.4f;
		
		vel.x += acc.x;
		vel.y += acc.y;

		pos.x += vel.x;
		pos.y += vel.y;
		
		if (vel.y < -4)
			vel.y = -4;
		if (vel.y > 4)
			vel.y = 4;
		
		if (pos.y > Ground.y - 25)
		{
			pos.y = Ground.y - 25;
			vel.y *= -1;
		}
		
		//RED BLOCK
		if (pos.y > Ground.y - 41 && pos.x > 420 && pos.x < 520 && vel.y > 0)
		{
			pos.y = Ground.y - 41;
			vel.y *= -1;
		}
		/*
		//BLUE BLOCK
		if (pos.y < Ground.y - 65 && pos.y > Ground.y - 158 && pos.x > 420+60 && pos.x < 520+60 && vel.y > 0)
		{
			pos.y = Ground.y - 100;
			vel.y *= -1;
		}
		
		//CASTLE - FIRST FLOOR
		if (pos.y > Ground.y - 40 && pos.x > 700 && pos.x < 870 && vel.y > 0)
		{
			pos.y = Ground.y - 40;
			vel.y *= -1;
		}
		
		//CASTLE - SECOND FLOOR
		if (pos.y < Ground.y - 65 && pos.y > Ground.y - 158 && pos.x > 730 && pos.x < 850 && vel.y > 0)
		{
			pos.y = Ground.y - 100;
			vel.y *= -1;
		}
		
		//TUBE
		if (pos.y > Ground.y - 40 && pos.x > -100 && pos.x < Ground.x0 && vel.y > 0)
		{
			pos.y = Ground.y - 40;
			vel.y *= -1;
		}
		*/
		//
		/*if (pos.x < Ground.x0 && pos.y > Ground.y - 50 )
		{
			pos.x = Ground.x0;
			vel.x *= -0.3;
		}*/
		
	}
}


class Cloud
{
	MainApplication main;
	
	PVector acc;
	PVector pos;
	PVector vel;
	PVector accMax; 
	PVector velMax;
	PVector velMin; 
	
	static final float friction = 0.02f;
	
	
	Animation anim;
	
	final float imgSpd = 0.5f;
	
	Cloud(MainApplication aMain)
	{
		main = aMain;
		
		
		pos = new PVector(50,220);
		vel = new PVector(1,1);
		acc = new PVector(0,0);
		velMax = new PVector(3,3);
		velMin = new PVector(1,1);
		accMax = new PVector(3,3);
		anim = new Animation("Cloud_",1,main);
		anim.setImageSpeed(imgSpd);

	}
	
	PVector getPos()
	{
		PVector vect = new PVector(pos.x,pos.y);
		return vect;
	}
	void setPos(PVector aVect)
	{
		pos.x = aVect.x;
		pos.y = aVect.y;
	}
	PVector getAcc()
	{
		PVector vect = new PVector(acc.x,acc.y);
		return vect;
	}
	void setAcc(PVector aVect)
	{
		acc.x = aVect.x;
		acc.y = aVect.y;
	}
	PVector getVel()
	{
		PVector vect = new PVector(vel.x,vel.y);
		return vect;
	}
	void setVel(PVector aVect)
	{
		vel.x = aVect.x;
		vel.y = aVect.y;
	}
	
	void update()
	{
		PVector ncJoy = main.nc.getJoy();
		
		
		vel.x = ncJoy.x / 30;
		
		if (main.abs(vel.x) < 1)
			vel.x = 1  * main.sign(vel.x);
		
		vel.y = -ncJoy.y / 30;
		
		if (main.abs(vel.y) < 1)
			vel.x = 1 * main.sign(vel.y);
		
		/*if (main.abs(acc.x) > accMax.x)
			acc.x = accMax.x * main.sign(acc.x);
		
		if (main.abs(acc.y) > accMax.y)
			acc.y = accMax.y * main.sign(acc.y);
		
		vel.x += acc.x;
		vel.y += acc.y;
		
	
		/*if (main.abs(vel.x) > velMax.x)
			vel.x = velMax.x * main.sign(vel.x);
		
		if (main.abs(vel.y) > velMax.y)
			vel.y = velMax.y * main.sign(vel.y);
		
		if (main.abs(vel.x) < velMin.x)
			vel.x = velMin.x * main.sign(vel.x);
		
		if (main.abs(vel.y) < velMin.y)
			vel.y = velMin.y * main.sign(vel.y);
		*/
		pos.x += vel.x;
		pos.y += vel.y;		
		
		if (pos.y > 250)
		{
			pos.y = 250;
			vel.y *= -1;
		}
		
		if (pos.y < 180)
		{
			pos.y = 180;
			vel.y *= -1;
		}
	
		if (pos.x < 0 )
		{
			pos.x = 1;
			vel.x *= -1;
		}
		else if (pos.x > Ground.x1)
		{
			pos.x = Ground.x1-1;
			vel.x *= -1;
		}
		
	}
	
	void draw()
	{
		anim.display(pos.x +anim.getWidth()/2 ,pos.y - anim.getHeight() );
	}
}

