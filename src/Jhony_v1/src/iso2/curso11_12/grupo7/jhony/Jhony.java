package iso2.curso11_12.grupo7.jhony;


/** Clase para separar la lógica del personaje de la interfaz. */
public class Jhony {
	
	private static final float GRAVITY = 9.8f;	// Fuerza gravedad
	private static final int SPEEDUP = 15;		// Factor de aceleracion del movimiento
	private static final float MAXSPEED = 60f;	// Velocidad maxima del salto
	
	private float _height;		// Altura a la que se encuentra Jhony
	private float _speed;		// Velocidad vertical a la que se mueve Jhony
	private boolean _jumping;	// Indica si Jhony esta o no saltando
	
	/** Método constructor */
	public Jhony() {
		
		setHeight(0);
		setSpeed(0);
		setJumping(false);
	}
	
	/** Inicializa la velocidad y cambia el estado para saltar. */
	public void jump(float speed) {
		
		// Se puede saltar a dos alturas
		if (speed < 0.25f * MAXSPEED)
			setSpeed(0.75f * MAXSPEED);
		else
			setSpeed(MAXSPEED);
		
		setJumping(true);
	}
	
	/** Aterriza al personaje. */
	private void land() {
		
		setHeight(0);
		setSpeed(0);
		setJumping(false);
	}
	
	/** Actualiza la altura en función del tiempo transcurrido. */
	public float updateHeight(float time0) {
		
		float time = time0 * SPEEDUP;
		
		if (isJumping()) {
			
			setHeight(getHeight() + getSpeed() * time - 0.5f * GRAVITY * time * time);
			setSpeed(getSpeed() - GRAVITY * time);
			
			if (getHeight() <= 0)
				land();
		}
			
		return getHeight();
	}
	
	/** Fijar altura. */
	private void setHeight(float height) { _height = height; }
	
	/** Devuelve la altura a la que se encuentra el personaje. */
	private float getHeight() { return _height; }
	
	/** Fijar la velocidad del salto. */
	private void setSpeed(float speed) { _speed = speed; }
	
	/** Devuelve la velocidad del salto. */
	private float getSpeed() { return _speed; }
	
	/** Cambia el estado de salto. */
	private void setJumping(boolean jumping) { _jumping = jumping; }
	
	/** Devuelve el estado del salto. */
	public boolean isJumping() { return _jumping; }
}
