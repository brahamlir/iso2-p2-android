package iso2.curso11_12.grupo7.jhony;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/** Wrapper que incluye el atributo Ects a los sprites. */
public class ObstacleSprite extends Sprite {

	/** Ects del obstaculo, -1 si es un obstaculo negativo. */
	private int _ects;
	
	/** Constructor de la clase. */
	public ObstacleSprite(float pX, float pY, TextureRegion pTextureRegion, int ects) {
		
		super(pX, pY, pTextureRegion);
		setEcts(ects);
	}

	/** Fijar Ects. */
	public void setEcts(int ects) { _ects = ects; }
	
	/** Devuelve los Ects. */
	public int getEcts() { return _ects; }
}
