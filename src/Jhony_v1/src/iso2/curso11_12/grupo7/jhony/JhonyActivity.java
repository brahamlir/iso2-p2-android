package iso2.curso11_12.grupo7.jhony;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class JhonyActivity extends BaseGameActivity implements IUpdateHandler
{
  private static final int CAMERA_WIDTH = 800; // Anchura de la camara
  private static final int CAMERA_HEIGHT = 480; // Altura de la camara

  private float secondsPassed;
  private BitmapTextureAtlas backAtlas;
  private TiledTextureRegion regionArcher;
  private AnimatedSprite animatedArcher;
  private AnimatedSprite animatedArcher2;
  
  @Override
  public Engine onLoadEngine()
  {
    secondsPassed = 0;
    
    Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

    Engine engine = new Engine(new EngineOptions(true,
        ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH,
            CAMERA_HEIGHT), camera));

    return engine;
  }

  @Override
  public void onLoadResources()
  {
    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    backAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    regionArcher = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(backAtlas, this, "player.png", 0, 0, 3, 4);
    this.mEngine.getTextureManager().loadTextures(backAtlas);
  }

  @Override
  public Scene onLoadScene()
  {
    final Scene scene = new Scene();

    scene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f));
    
    animatedArcher = new AnimatedSprite(72, 128, this.regionArcher);
    animatedArcher2 = new AnimatedSprite(72, 128, this.regionArcher);
    
    scene.registerUpdateHandler(this);
    
    animatedArcher.animate(500, true);
    scene.attachChild(animatedArcher);
    animatedArcher.setPosition(320, 220);
    
    animatedArcher2.animate(500, true);
    scene.attachChild(animatedArcher2);
    animatedArcher2.setPosition(100, 100);
    
    return scene;
  }

  @Override
  public void onLoadComplete()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onUpdate(float secondsSinceLastLoop)
  {
    secondsPassed += secondsSinceLastLoop;
    
    secondsPassed = 0;
    animatedArcher2.setPosition(animatedArcher2.getX() + 10 * secondsSinceLastLoop, animatedArcher2.getY());
  }

  @Override
  public void reset() {}
}