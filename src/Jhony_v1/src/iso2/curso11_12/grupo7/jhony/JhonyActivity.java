package iso2.curso11_12.grupo7.jhony;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class JhonyActivity extends BaseGameActivity {

	private static final int CAMERA_WIDTH = 800;	// Anchura de la camara
	private static final int CAMERA_HEIGHT = 480;	// Altura de la camara
	
	@Override
	public Engine onLoadEngine() {
		
		Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		Engine engine = new Engine(new EngineOptions(
				true, ScreenOrientation.LANDSCAPE, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), 
				camera
		));
		
		return engine;
	}

	@Override
	public void onLoadResources() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scene onLoadScene() {
		
		final Scene scene = new Scene();

		scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		
		return scene;
	}
	
	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
}