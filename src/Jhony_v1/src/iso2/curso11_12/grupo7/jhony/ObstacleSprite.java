package iso2.curso11_12.grupo7.jhony;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;


public class ObstacleSprite extends Sprite {

	private int _ects;
	
	public ObstacleSprite(float pX, float pY, TextureRegion pTextureRegion) {
		
		super(pX, pY, pTextureRegion);
	}

	public void setEcts(int ects) { _ects = ects; }
	public int getEcts() { return _ects; }
}
