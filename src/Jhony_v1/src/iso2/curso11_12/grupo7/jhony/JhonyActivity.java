package iso2.curso11_12.grupo7.jhony;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
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
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;


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
  
  /** Velocidad parallax */
  private static final float PARALLAX_SPEED = 70f;
  
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
  
  /** Textura para los ECTS. */
  private TextureRegion _ectsTexture;
  
  /** Lista de cajas a esquivar. */
  private List<Sprite> _boxes;
  
  /** Generador de números aleagorios. */
  private Random _rnd;
  
  /** Clase con la logica del jugador */
  private Jhony _jhony; 
  
  /** Region de textura de Jhony */
  private TiledTextureRegion _jhonyTexture;
  
  /** Sprite animado de Jhony */
  private AnimatedSprite _jhonySprite;
  
  /** Flag de aterrizaje de Jhony */
  private boolean _jhonyLanded;
  
  /** Posicion en X de Jhony */
  private float _jhonyX;
  
  /** Posicion en Y de Jhony */
  private float _jhonyY;
  
  /** Flag de presion de la pantalla */
  private boolean _screenPressed; 
  
  /** Activo al soltar la pantalla */
  private boolean _screenJustReleased;
  
  /** Tiempo de presion de la pantalla */
  private float _pressionTime;
  
  /** Contador de tiempo para generar cajas */
  private float _contBox;
  
  /** Tiempo minimo entre obstaculos */
  private static final float MINBOX = 0.5f;
  
  /** Tiempo entre obstaculos según dificultad */
  private static float _timeBox;
  
  /** Puntuacion de la partida */
  private int _score;
  
  /** Objeto fuente para el texto */
  private Font _scoreFont;

  /** Texto de la puntuación */
  private ChangeableText _scoreText;
  
  /** Lista de obstaculos */
  private List<ObstacleSprite> _obstacles;
  
  /** Sonido */
  private Sound _soundAprendo;
  
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
    _score = 0;
    _rnd = new Random();
    _timeBox = 10;
    _contBox = MINBOX + _timeBox * _rnd.nextFloat();
    _obstacles = new ArrayList<ObstacleSprite>();
    _boxes = Collections.synchronizedList(new LinkedList<Sprite>());
    
    Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

    Engine engine = new Engine(new EngineOptions(true,
        ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH,
            CAMERA_HEIGHT), camera).setNeedsSound(true));

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
            playerAtlas, this, "jhony.png", 0, 0, 5, 2);
    
    _boxTexture =
        BitmapTextureAtlasTextureRegionFactory.createFromAsset(
            playerAtlas, this, "box.png", 0, 128);

    _ectsTexture =
            BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                playerAtlas, this, "ects.png", 0, 160);
    
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
    
    // Texto.
    BitmapTextureAtlas scoreAtlas = new BitmapTextureAtlas(
    		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    
    _scoreFont = new Font(scoreAtlas, 
    		Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, 
    		true, Color.WHITE);
	
	this.mEngine.getTextureManager().loadTextures(playerAtlas,
	        backgroundBackAtlas, cactusAndFloorAtlas, scoreAtlas);
	
	this.mEngine.getFontManager().loadFont(_scoreFont);
	
	// Sonido
	SoundFactory.setAssetBasePath("mfx/");
	try {
		this._soundAprendo = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "aprendo.ogg");
	} catch (final IOException e) {
		Debug.e(e);
	}
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
    _jhonySprite.animate(new long[] {75, 75, 75, 75, 75, 75, 75, 75}, 0, 7, true);
    
    // Escalado del sprite.
    _jhonySprite.setScaleCenter(_jhonySprite.getWidth() / 2, _jhonySprite.getHeight());
    _jhonySprite.setScale(2);
    
    _scene.attachChild(_jhonySprite);
    
    // Texto.
    _scoreText = new ChangeableText(
    		10, 10, _scoreFont, "Creditos ECTS: 0","Creditos ECTS: XXXXX".length());
    _scene.attachChild(_scoreText);
    
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
        new ParallaxEntity(-PARALLAX_SPEED / 2, backgroundCactusSprite));
    
    _floorSprite = new Sprite(0, _jhonyY + _jhonySprite.getHeight(),
        _floorTexture);
    
    autoParallaxBackground.attachParallaxEntity(
        new ParallaxEntity(-PARALLAX_SPEED, _floorSprite));
    
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
			
			_jhonySprite.animate(new long[] {75, 75, 75, 75, 75, 75, 75, 75}, 0, 7, true);
			_jhonyLanded = true;;	
		}
		
		if (_screenJustReleased) {
			
			_jhonySprite.animate(new long[] {100, 100}, 8, 9, true);
    		_jhony.jump(_pressionTime * 100);
    		_pressionTime = 0;
    		_screenJustReleased = false;
    		_jhonyLanded = false;
		}
	}
	
    if (_screenPressed)
    	_pressionTime += secondsSinceLastLoop;
    
    _timeBox = 5f - _score / 50f;
    if (_timeBox < 0)_timeBox = 0;
    
    _contBox -= secondsSinceLastLoop;
    if (_contBox <= 0) {
        _contBox = MINBOX + _timeBox * _rnd.nextFloat();
    	
        Log.d("ContBox", "ContBox = " + _contBox);
        
        ObstacleSprite os;
        float obstacleY;
        
        if (_rnd.nextInt(2) == 0) {
        	
        	os = new ObstacleSprite(0, 0, _boxTexture);
        	os.setEcts(-1);
        	obstacleY = _jhonyY + _jhonySprite.getHeight() - os.getHeight();
        }
        else {
        	
        	os = new ObstacleSprite(0, 0, _ectsTexture); 
        	os.setEcts(1 + _rnd.nextInt(10));
        	obstacleY = _jhonyY + _jhonySprite.getHeight() - os.getHeight() - (100 * _rnd.nextInt(3));
        }
        
        Log.d("Obstacle", "ObstacleY = " + obstacleY);
        os.setPosition(CAMERA_WIDTH, obstacleY);
        _obstacles.add(os);
        _scene.attachChild(os);
    }
    
    Iterator<ObstacleSprite> it = _obstacles.iterator();
    while (it.hasNext()) {
    	
    	ObstacleSprite os = it.next();
    	
    	os.setPosition(os.getX() - 5 * PARALLAX_SPEED * secondsSinceLastLoop, os.getY());
    	

    	if (os.getX() < 0) {
    		it.remove();
    		_scene.detachChild(os);
    	}
    	else if (os.collidesWith(_jhonySprite)) {
    		if (os.getEcts() < 0) {
    			
    			Log.d("Jhony", "Game Over");
    			_score = 0;
    			_scoreText.setText("Creditos ECTS: " + _score);
    		}
    		else {
    			
    			_score += os.getEcts();
    			_scoreText.setText("Creditos ECTS: " + _score);
    			it.remove();
    			_scene.detachChild(os);
    			_soundAprendo.play();
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
   
	if (touchEvent.isActionDown())
    	_screenPressed = true;
    
    if (touchEvent.isActionUp()) {
    	
    	_screenPressed = false;
    	_screenJustReleased = true;
    }
    
    return true;
  }
}