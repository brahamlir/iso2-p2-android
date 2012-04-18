package iso2.curso11_12.grupo7.jhony;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;


/** Actividad que representa el juego. */
public class JhonyActivity extends BaseGameActivity
    implements IUpdateHandler, IOnSceneTouchListener
{
  /** Anchura de la cámara. */
  private static final int CAMERA_WIDTH = 800;
  
  /** Altura de la cámara. */
  private static final int CAMERA_HEIGHT = 480;
  
  /** Altura en píxeles a la que salta el personaje. */
  private static final int PIXELS_TO_JUMP = 72;
  
  /** Intervalos en los que avanza el salto, en píxeles. */
  private static final int PIXELS_DISP = 3;
  
  /** Intervalos de tiempo en los que avanza cada fragmento del salto. */
  private static final float MIN_JUMP_INTERVAL = 0.05f;

  /** Contador de tiempo. */
  private float _secondsPassed;
  
  /** Textura para el personaje corriendo. */
  private TiledTextureRegion _runnerTexture;
  
  /** Sprite para el personaje corriendo. */
  private AnimatedSprite _runnerSprite;
  
  /** Textura para el personaje saltando. */
  private TiledTextureRegion _jumperTexture;
  
  /** Sprite para el personaje saltando. */
  private AnimatedSprite _jumperSprite;
  
  /** Textura para el fondo más profundo. */
  private TextureRegion _backgroundBackTexture;
  
  /** Textura para el fondo de los cactus. */
  private TextureRegion _backgroundCactusTexture;
  
  /** Textura para el suelo. */
  private TextureRegion _floorTexture;
  
  /** True cuando el personaje está saltando (ascendiendo). */
  private boolean _jumping;
  
  /** True cuando el personaje está saltando (descendiendo). */
  private boolean _falling;
  
  /** Contador de píxeles saltados. */
  private int _pixelsJumped;
  
  /** Carga del motor. */
  @Override
  public Engine onLoadEngine()
  {
    _secondsPassed = 0.0f;
    _pixelsJumped = 0;
    _jumping = false;
    _falling = false;
    
    Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

    Engine engine = new Engine(new EngineOptions(true,
        ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH,
            CAMERA_HEIGHT), camera));

    return engine;
  }

  /** Carga de recursos. */
  @Override
  public void onLoadResources()
  {
    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    
    BitmapTextureAtlas playerAtlas = new BitmapTextureAtlas(256, 256,
        TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    
    _runnerTexture = 
        BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
            playerAtlas, this, "runner.png", 0, 0, 3, 1);
    
    _jumperTexture = 
        BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
            playerAtlas, this, "jumper.png", 73, 0, 1, 4);

    BitmapTextureAtlas backgroundBackAtlas = new BitmapTextureAtlas(1024, 1024,
        TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    
    _backgroundBackTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            backgroundBackAtlas, this, "background_back.png", 0, 0);
    
    BitmapTextureAtlas cactusAndFloorAtlas = new BitmapTextureAtlas(1024, 1024,
        TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    
    _backgroundCactusTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            cactusAndFloorAtlas, this, "background_cactus.png", 0, 0);
    
    _floorTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            cactusAndFloorAtlas, this, "floor.png", 0, 188);
    
    
    this.mEngine.getTextureManager().loadTextures(playerAtlas,
        backgroundBackAtlas, cactusAndFloorAtlas);
  }

  /** Carga de la escena. */
  @Override
  public Scene onLoadScene()
  {
    final Scene scene = new Scene();
    final AutoParallaxBackground autoParallaxBackground =
        new AutoParallaxBackground(0, 0, 0, 5);
    
    // Sprites del jugador.
    
    _runnerSprite = new AnimatedSprite(72, 32, _runnerTexture);
    _jumperSprite = new AnimatedSprite(24, 128, _jumperTexture);
    
    scene.registerUpdateHandler(this);
    scene.setOnSceneTouchListener(this);
    
    _runnerSprite.animate(250, true);
    scene.attachChild(_runnerSprite);
    _runnerSprite.setPosition(CAMERA_WIDTH / 3, (CAMERA_HEIGHT / 4) * 3);
    
    _jumperSprite.animate(250, true);
    scene.attachChild(_jumperSprite);
    _jumperSprite.setPosition(_runnerSprite.getX(), _runnerSprite.getY());
    _jumperSprite.setVisible(false);
    
    // Fondos.
    
    Sprite backgroundBackSprite = new Sprite(0, 0, _backgroundBackTexture);
    
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(0.0f, backgroundBackSprite));
    
    Sprite backgroundCactusSprite = new Sprite(0, _runnerSprite.getY() - 155,
        _backgroundCactusTexture);

    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-2.5f, backgroundCactusSprite));
    
    Sprite floorSprite = new Sprite(0, _runnerSprite.getY() + 32,
        _floorTexture);
 
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-10.0f, floorSprite));
    
    scene.setBackground(autoParallaxBackground);
    
    return scene;
  }

  /** Método que se ejecuta al acabar la carga. */
  @Override
  public void onLoadComplete() {}

  // Método de IUpdateHandler
  /** Método que se ejecuta en cada iteración del bucle de juego. */
  @Override
  public void onUpdate(float secondsSinceLastLoop)
  {
    if (_jumping)
    {
      _secondsPassed += secondsSinceLastLoop;
      
      if (_secondsPassed >= MIN_JUMP_INTERVAL)
      {
        _secondsPassed = 0;
        
        _jumperSprite.setPosition(_jumperSprite.getX(),
            _jumperSprite.getY() - PIXELS_DISP);
        
        _pixelsJumped += PIXELS_DISP;
        
        if (_pixelsJumped == PIXELS_TO_JUMP)
        {
          _jumping = false;
          _falling = true;
        }
      }
    }
    
    if (_falling)
    {
      _secondsPassed += secondsSinceLastLoop;
      
      if (_secondsPassed >= MIN_JUMP_INTERVAL)
      {
        _secondsPassed = 0;
        
        _jumperSprite.setPosition(_jumperSprite.getX(),
            _jumperSprite.getY() + PIXELS_DISP);
        
        _pixelsJumped -= PIXELS_DISP;
        
        if (_pixelsJumped == 0)
        {
          _falling = false;
          _jumperSprite.setVisible(false);
          _runnerSprite.setVisible(true);
        }
      }
    }
  }

  // Método de IUpdateHandler
  /** Reset para el bucle del juego. */
  @Override
  public void reset() {}

  // Método de IOnSceneTouchListener
  /** Método que se ejecuta al recibir un evento de toque en la pantalla. */
  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent)
  {
    if (!_jumping && !_falling)
    {
      _jumping = true;
      _runnerSprite.setVisible(false);
      _jumperSprite.setVisible(true);
    }
    
    return true;
  }
}