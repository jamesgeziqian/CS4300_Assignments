import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

import com.jogamp.opengl.math.Matrix4;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class View {

  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  private Matrix4f proj;
  private Stack<Matrix4f> modelView;
  private Map<String, Sphere> starMap;
  private util.Material material;

  private util.ShaderProgram program;
  private int time;
  private Timer timer;
  private ShaderLocationsVault shaderLocations;

  View() {
    proj = new Matrix4f();
    proj.identity();

    modelView = new Stack<>();
    modelView.push(new Matrix4f().identity());

    timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        time++;
      }
    }, 10, 10);
    time = 0;
    starMap = new HashMap<>();
  }

  private void initObjects(GL3 gl) throws FileNotFoundException {
    float SUN_RADIUS = 80f;
    float SUN_ROTATION = (float) Math.toRadians(time);
    Matrix4f sunMatrix = new Matrix4f()
        .mul(new Matrix4f().rotate(SUN_ROTATION, 0, 1, 0))
        .scale(SUN_RADIUS, SUN_RADIUS, SUN_RADIUS);
    starMap.put("sun",
        new Sphere(gl, program, shaderLocations, sunMatrix, "sun", .988f, .831f, .251f));

    float P1_RADIUS = 20f;
    float P1_ROTATION = (float) Math.toRadians(time * 3);
    Matrix4f planet1Matrix = new Matrix4f()
        .rotate(P1_ROTATION, 0, 1, 0)
        .scale(P1_RADIUS, P1_RADIUS, P1_RADIUS);
    starMap.put("planet1",
        new Sphere(gl, program, shaderLocations, planet1Matrix, "planet1", .4f, .8f, 1));

  }

  void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = (GL3) gla.getGL().getGL3();

    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

    shaderLocations = program.getAllShaderVariables(gl);

    initObjects(gl);


  }


  void draw(GLAutoDrawable gla) {
    float PLANET1_REV_RADIUS = 100;
    GL3 gl = gla.getGL().getGL3();

    float P1_REV_RAD = 100f;
    float P1_REV_ANG = (float) Math.toRadians(time * 3);
    //set the background color to be black
    gl.glClearColor(0, 0, 0, 1);
    //clear the background
    gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL.GL_DEPTH_TEST);
    //enable the shader program
    program.enable(gl);

    // look at info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .lookAt(new Vector3f(0, 500, 500), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

    // draw sun
    starMap.get("sun").draw(gla, modelView.peek(), proj);

    // planet1 info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(
            P1_REV_RAD * (float) Math.cos(P1_REV_ANG),
            P1_REV_RAD * (float) Math.sin(P1_REV_ANG),
            0);
    starMap.get("planet1").draw(gla, modelView.peek(), proj);
    // pop planet 1 info
    modelView.pop();

    gl.glFlush();
    //disable the program
    program.disable(gl);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_FILL); //BACK TO FILL

    // pop look at info
    modelView.pop();
  }

  //this method is called from the JOGLFrame class, everytime the window resizes
  void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
    GL gl = gla.getGL();
    WINDOW_WIDTH = width;
    WINDOW_HEIGHT = height;
    gl.glViewport(0, 0, width, height);

    proj = new Matrix4f().perspective((float) Math.toRadians(60.0f),
        (float) width / height,
        0.1f,
        10000.0f);

    if (width > height) {
      proj = new Matrix4f()
          .ortho(-400f * width / height, 400f * width / height, -400, 400, 0.1f, 10000.0f);

    } else {
      proj = new Matrix4f()
          .ortho(-400, 400, -400f * height / width, 400f * height / width, 0.1f, 10000.0f);

    }

  }

  void dispose(GLAutoDrawable gla) {
    for (Sphere star : starMap.values()) {
      star.cleanup(gla);
    }
  }
}
