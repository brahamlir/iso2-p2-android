package iso2.curso11_12.grupo7.jhony;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
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

  /** Intervalos en los que avanzan las cajas, en píxeles. */
  private static final int BOX_DISP = 3;
  
  /** Intervalos de tiempo en los que avanzan las cajas. */
  private static final float MIN_BOX_INTERVAL = 0.05f;
  
  /** Intervalos de tiempo en los que se generan las cajas. */
  private static final float MIN_GENERATE_INTERVAL = 2.0f;
  
  /** Escena. */
  Scene _scene;
  
  /** Contador de tiempo para mover las cajas. */
  private float _boxSecondsPassed;
  
  /** Contador de tiempo para generar las cajas. */
  private float _generateBoxSecondsPassed;
  
  /** Textura para el fondo más profundo. */
  private TextureRegion _backgroundBackTexture;
  
  /** Textura para el fondo de los cactus. */
  private TextureRegion _backgroundCactusTexture;
  
  /** Textura para el suelo. */
  private TextureRegion _floorTexture;
  
  /** Sprite para el suelo. */
  private Sprite _floorSprite;
  
  /** Textura para la caja. */
  private TextureRegion _boxTexture;
  
  /** Lista de cajas a esquivar. */
  private List<Sprite> _boxes;
  
  /** Generador de números aleagorios. */
  private Random _rnd;
  
  private Jhony _jhony; // Clase con la logica del jugador
  private TiledTextureRegion _jhonyTexture; // Region de textura de Jhony
  private AnimatedSprite _jhonySprite; // Sprite animado de Jhony
  private boolean _jhonyLanded; // Flag de aterrizaje de Jhony
  private float _jhonyX; // Posicion en X de Jhony
  private float _jhonyY; // Posicion en Y de Jhony
  
  private boolean _screenPressed; // Flag de presion de la pantalla
  private boolean _screenJustReleased; // Activo al soltar la pantalla
  private float _pressionTime; // Tiempo de presion de la pantalla
  
  
  /** Carga del motor. */
  @Override
  public Engine onLoadEngine()
  {
    _boxSecondsPassed = 0.0f;
    _generateBoxSecondsPassed = 0.0f;
    _screenPressed = false;
    _screenJustReleased = false;
    _pressionTime = 0;
    _jhonyLanded = true;
    _rnd = new Random();
    _boxes = Collections.synchronizedList(new LinkedList<Sprite>());
    
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
        TextureOptions.NEAREST_PREMULTIPLYALPHA);
    
    _jhonyTexture = 
        BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
            playerAtlas, this, "jhony.png", 0, 0, 4, 2);
    
    _boxTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            playerAtlas, this, "box.png", 128, 0);

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
    _scene = new Scene();
    
    // Sprites del jugador.
    _jhony = new Jhony();
    
    // Posicion en la pantalla.
    _jhonyX = CAMERA_WIDTH / 3;
    _jhonyY = (CAMERA_HEIGHT / 4) * 3;
    
    _jhonySprite = new AnimatedSprite(_jhonyX, _jhonyY, _jhonyTexture);
    _jhonySprite.animate(new long[] {100, 100, 100}, 0, 2, true);
    
    // Escalado del sprite.
    _jhonySprite.setScaleCenter(_jhonySprite.getWidth() / 2, _jhonySprite.getHeight());
    _jhonySprite.setScale(2);
    
    _scene.attachChild(_jhonySprite);
    
    // Fondos.
    final AutoParallaxBackground autoParallaxBackground =
            new AutoParallaxBackground(0, 0, 0, 5);
    
    Sprite backgroundBackSprite = new Sprite(0, 0, _backgroundBackTexture);
    
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(0.0f, backgroundBackSprite));
    
    Sprite backgroundCactusSprite = new Sprite(
    	0, _jhonyY + _jhonySprite.getHeight() - _backgroundCactusTexture.getHeight(),
        _backgroundCactusTexture);

    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-15.0f, backgroundCactusSprite));
    
    _floorSprite = new Sprite(0, _jhonyY + _jhonySprite.getHeight(),
        _floorTexture);
    
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-30.0f, _floorSprite));
    
    _scene.setBackground(autoParallaxBackground);
    
    _scene.registerUpdateHandler(this);
    _scene.setOnSceneTouchListener(this);
    
    return _scene;
  }

  /** Método que se ejecuta al acabar la carga. */
  @Override
  public void onLoadComplete() {}
  
  // Método de IUpdateHandler
  /** Método que se ejecuta en cada iteración del bucle de juego. */
  @Override
  public void onUpdate(float secondsSinceLastLoop)
  {
    // Jhony
	_jhonySprite.setPosition(_jhonyX, _jhonyY - _jhony.updateHeight(secondsSinceLastLoop));
	
	if (!_jhony.isJumping()) {
		
		if (!_jhonyLanded) {
			
			_jhonySprite.animate(new long[] {100, 100, 100}, 0, 2, true);
			_jhonyLanded = true;;	
		}
		
		if (_screenJustReleased) {
			
			_jhonySprite.animate(new long[] {100, 100, 100, 100}, 4, 7, true);
    		_jhony.jump(_pressionTime * 100);
    		_pressionTime = 0;
    		_screenJustReleased = false;
    		_jhonyLanded = false;
		}
	}
	
    if (_screenPressed)
    	_pressionTime += secondsSinceLastLoop;
    
 // Actualizar contador para mover las cajas.
    _boxSecondsPassed += secondsSinceLastLoop;
    _generateBoxSecondsPassed += secondsSinceLastLoop;
    
    // Generar cajas.
    if (_generateBoxSecondsPassed >= MIN_GENERATE_INTERVAL)
    {
      _generateBoxSecondsPassed = 0.0f;
      
      if ((int)(_rnd.nextFloat() * 10.0f) <= 1 )
      {
        Sprite s = new Sprite(32, 32, _boxTexture);
        _boxes.add(s);
        _scene.attachChild(s);
        s.setPosition(CAMERA_WIDTH, _floorSprite.getY() - 32);
      }
    }
    
    // Mover cajas.
    if (_boxSecondsPassed >= MIN_BOX_INTERVAL)
    {
      _boxSecondsPassed = 0.0f;
      for (Sprite s: _boxes)
        s.setPosition(s.getX() - BOX_DISP, s.getY());
    }
    
    // Eliminar cajas fuera de la imagen.
    Stack<Integer> indexToRemove = new Stack<Integer>();
    
    int i = 0;
    
    for (Sprite s: _boxes)
    {
      if (s.getX() + 32 < 0)
        indexToRemove.push(i);
      ++i;
    }
    
    while (!indexToRemove.empty())
    {
      int n = (int)indexToRemove.pop();
      _scene.detachChild(_boxes.get(n) );
      _boxes.remove(n);
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
   
	if (touchEvent.isActionDown())
    	_screenPressed = true;
    
    if (touchEvent.isActionUp()) {
    	
    	_screenPressed = false;
    	_screenJustReleased = true;
    }
    
    return true;
  }
}