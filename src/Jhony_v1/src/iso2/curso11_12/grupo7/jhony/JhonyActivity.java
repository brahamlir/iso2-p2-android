package iso2.curso11_12.grupo7.jhony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

  /** Intervalos en los que avanzan las cajas, en píxeles. */
  private static final int BOX_DISP = 3;
  
  /** Intervalos de tiempo en los que avanzan las cajas. */
  private static final float MIN_BOX_INTERVAL = 0.05f;
  
  /** Intervalos de tiempo en los que se generan las cajas. */
  private static final float MIN_GENERATE_INTERVAL = 2.0f;
  
  /** Escena. */
  Scene _scene;
  
  /** Contador de tiempo para los saltos. */
  private float _jumpSecondsPassed;
  
  /** Contador de tiempo para mover las cajas. */
  private float _boxSecondsPassed;
  
  /** Contador de tiempo para generar las cajas. */
  private float _generateBoxSecondsPassed;
  
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
  
  /** Sprite para el suelo. */
  private Sprite _floorSprite;
  
  /** True cuando el personaje está saltando (ascendiendo). */
  private boolean _jumping;
  
  /** True cuando el personaje está saltando (descendiendo). */
  private boolean _falling;
  
  /** Contador de píxeles saltados. */
  private int _pixelsJumped;
  
  /** Textura para la caja. */
  private TextureRegion _boxTexture;
  
  /** Lista de cajas a esquivar. */
  private List<Sprite> _boxes;
  
  /** Generador de números aleagorios. */
  private Random _rnd;
    
  /** Carga del motor. */
  @Override
  public Engine onLoadEngine()
  {
    _jumpSecondsPassed = 0.0f;
    _boxSecondsPassed = 0.0f;
    _generateBoxSecondsPassed = 0.0f;
    _pixelsJumped = 0;
    _jumping = false;
    _falling = false;
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
        TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    
    _runnerTexture = 
        BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
            playerAtlas, this, "runner.png", 0, 0, 3, 1);
    
    _jumperTexture = 
        BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
            playerAtlas, this, "jumper.png", 73, 0, 1, 4);
    
    _boxTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            playerAtlas, this, "box.png", 145, 0);

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
    final AutoParallaxBackground autoParallaxBackground =
        new AutoParallaxBackground(0, 0, 0, 5);
    
    // Sprites del jugador.
    
    _runnerSprite = new AnimatedSprite(72, 32, _runnerTexture);
    _jumperSprite = new AnimatedSprite(24, 128, _jumperTexture);
    
    _scene.registerUpdateHandler(this);
    _scene.setOnSceneTouchListener(this);
    
    _runnerSprite.animate(250, true);
    _scene.attachChild(_runnerSprite);
    _runnerSprite.setPosition(CAMERA_WIDTH / 3, (CAMERA_HEIGHT / 4) * 3);
    
    _jumperSprite.animate(250, true);
    _scene.attachChild(_jumperSprite);
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
    
    _floorSprite = new Sprite(0, _runnerSprite.getY() + 32,
        _floorTexture);
    
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-10.0f, _floorSprite));
    
    _scene.setBackground(autoParallaxBackground);
    
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
    // Actualizar contador para mover las cajas.
    _boxSecondsPassed += secondsSinceLastLoop;
    _generateBoxSecondsPassed += secondsSinceLastLoop;
    
    // Saltar
    if (_jumping)
    {
      _jumpSecondsPassed += secondsSinceLastLoop;
      
      if (_jumpSecondsPassed >= MIN_JUMP_INTERVAL)
      {
        _jumpSecondsPassed = 0;
        
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
    
    // Caer
    if (_falling)
    {
      _jumpSecondsPassed += secondsSinceLastLoop;
      
      if (_jumpSecondsPassed >= MIN_JUMP_INTERVAL)
      {
        _jumpSecondsPassed = 0;
        
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
    if (!_jumping && !_falling)
    {
      _jumping = true;
      _runnerSprite.setVisible(false);
      _jumperSprite.setVisible(true);
    }
    
    return true;
  }
}