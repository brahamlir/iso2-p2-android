package iso2.curso11_12.grupo7.jhony;

public class Jhony {
	
	private static final float GRAVITY = 9.8f;	// Fuerza gravedad
	private static final int SPEEDUP = 15;		// Factor de aceleracion del movimiento
	private static final float MAXSPEED = 60f;	// Velocidad maxima del salto
	
	private float _height;		// Altura a la que se encuentra Jhony
	private float _speed;		// Velocidad vertical a la que se mueve Jhony
	private boolean _jumping;	// Indica si Jhony esta o no saltando
	
	public Jhony() {
		
		setHeight(0);
		setSpeed(0);
		setJumping(false);
	}
	
	void jump(float speed) {
		
		// Se puede saltar a dos alturas
		if (speed < 0.25f * MAXSPEED)
			setSpeed(0.75f * MAXSPEED);
		else
			setSpeed(MAXSPEED);
		
		setJumping(true);
	}
	
	void land() {
		
		setHeight(0);
		setSpeed(0);
		setJumping(false);
	}
	
	float updateHeight(float time0) {
		
		float time = time0 * SPEEDUP;
		
		if (isJumping()) {
			
			setHeight(getHeight() + getSpeed() * time - 0.5f * GRAVITY * time * time);
			setSpeed(getSpeed() - GRAVITY * time);
			
			if (getHeight() <= 0)
				land();
		}
			
		return getHeight();
	}
	
	void setHeight(float height) { _height = height; }
	float getHeight() { return _height; }
	void setSpeed(float speed) { _speed = speed; }
	float getSpeed() { return _speed; }
	void setJumping(boolean jumping) { _jumping = jumping; }
	boolean isJumping() { return _jumping; }
}
