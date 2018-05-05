package main;

import processing.core.PApplet;
import processing.core.PVector;
import processing.serial.Serial;

public class Nunchuk {

	float roll;
	float pitch;
	PVector accel = new PVector(0,0,0);
	PVector joy = new PVector(0,0);
	boolean buttonZ;
	boolean buttonC;
	boolean buttonZold;
	boolean buttonCold;
	boolean cIsPressed;
	boolean zIsPressed;
	
	int currMillisec;

	//boolean commuteZ = true;
	boolean commuteC = true;

	float xmag, ymag = 0;
	float newXmag, newYmag = 0;

	Serial myPort; // The serial port

	static final int BAUDRATE = 115200;
	static final char DELIM = ','; // the delimeter for parsing incoming data

	MainApplication main;

	Nunchuk(MainApplication main)
	{
		this.main = main;
		System.out.println(Serial.list());
		myPort = new Serial((PApplet)(main), Serial.list()[1], BAUDRATE);
		myPort.clear();
	}

	public float getRoll()
	{
		return roll;
	}
	private void setRoll(float aRoll)
	{
		roll = aRoll;
	}

	public float getPitch()
	{
		return pitch;
	}
	private void setPitch(float aPitch)
	{
		pitch = aPitch;
	}
	public PVector getJoy()
	{
		return joy;
	}
	private void setJoy(PVector aJoy)
	{
		joy = aJoy;
	}
	public PVector getAccel()
	{
		return accel;
	}
	private void setAccel(PVector aAccel)
	{
		accel = aAccel;
	}
	public boolean getButtonZ()
	{
		return buttonZ;
	}
	private void setButtonZ(boolean aState)
	{
		if (buttonZ == false && aState == true)
			zIsPressed = true;
		else
			zIsPressed = false;
		buttonZ = aState;
	}
	public boolean getButtonC()
	{
		return buttonC;
	}
	private void setButtonC(boolean aState)
	{
		
		if (buttonC == false && aState == true)
		{
			cIsPressed = true;
		}
		else
		{
			cIsPressed = false;
		}

		buttonC = aState;
	}
	public boolean buttonCisPressed()
	{
		return cIsPressed;		
	}
	public boolean buttonZisPressed()
	{
		return zIsPressed;		
	}

	void update() {
		// read incoming data until you get a newline:
			String serialString = myPort.readStringUntil('\n');
			
			if (commuteC == true)
			{
				currMillisec = main.abs(main.millis()); 
			}
			if (main.abs(main.millis() - currMillisec) > 50)
			{

				if (commuteC == false)
					commuteC = true;
				//if (commuteZ == false)
					//commuteZ = true;
			}
			
			// if the read data is a real string, parse it:
			if (serialString != null) {

				System.out.print(serialString);

				// split it into substrings on the DELIM character:
				String[] numbers = main.split(serialString, DELIM);
				int sensorCount = 9; // number of values to expect

				// convert each subastring into an int
				if (numbers.length == sensorCount) {

					try
					{
						setRoll(Float.parseFloat(numbers[0]));
						setPitch(Float.parseFloat(numbers[1]));
						setAccel(new PVector(
								Float.parseFloat(numbers[2]),
								Float.parseFloat(numbers[3]),
								Float.parseFloat(numbers[4]))
						);
						setJoy(new PVector(
								Float.parseFloat(numbers[5]),
								Float.parseFloat(numbers[6]))
						);
						
						buttonZold = getButtonZ();
						if (Integer.parseInt(numbers[7]) == 1)
							setButtonZ(true);
						else
							setButtonZ(false);
							
						buttonCold = getButtonC();
						if (Integer.parseInt(main.trim(numbers[8])) == 1)
							setButtonC(true);
						else
							setButtonC(false);		
				
					}
					catch (NumberFormatException e)
					{
						System.out.println("EXCEPTION CAUGHT!");
					}
				}

				/*System.out.println(getRoll()+","+getPitch()+
						","+getAccel().x+
						","+getAccel().y+
						","+getAccel().z+
						","+getJoy().x+
						","+getJoy().y+
						","+getButtonZ()+
						","+getButtonC());
						*/
			}
	}
	
	void stop()
	{
		myPort.stop();
	}
}
