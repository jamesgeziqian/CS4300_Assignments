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
  private Map<String, ISimpleObjectInstance> starMap;
  private util.Material material;

  private util.ShaderProgram program;
  private float time;
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
    // sun init
    float SUN_RADIUS = 80f;
    float SUN_ROTATION = (float) Math.toRadians(time);
    Matrix4f sunMatrix = new Matrix4f()
        .mul(new Matrix4f().rotate(SUN_ROTATION, 0, 1, 0))
        .scale(SUN_RADIUS, SUN_RADIUS, SUN_RADIUS);
    starMap.put("sun",
        new Sphere(gl, program, shaderLocations, sunMatrix, "sun", .988f, .831f, .251f));

    // planet1 init
    float P1_RADIUS = 20f;
    float P1_ROTATION = (float) Math.toRadians(time * 3);
    Matrix4f planet1Matrix = new Matrix4f()
        .rotate(P1_ROTATION, 0, 1, 0)
        .scale(P1_RADIUS, P1_RADIUS, P1_RADIUS);
    starMap.put("planet1",
        new Sphere(gl, program, shaderLocations, planet1Matrix, "planet1", .4f, .8f, 1));

    // planet1 satellite1
    float P1_S1_RADIUS = 15f;
    float P1_S1_ROTATION = (float) Math.toRadians(time * 3);
    Matrix4f planet1Satellite1Matrix = new Matrix4f()
        .rotate(P1_S1_ROTATION, 0, 1, 0)
        .scale(P1_S1_RADIUS, P1_S1_RADIUS, P1_S1_RADIUS);
    starMap.put("planet1Satellite1",
        new Sphere(gl, program, shaderLocations, planet1Satellite1Matrix, "planet1Satellite1", .5f,
            .5f, .5f));

    // planet2
    float P2_RADIUS = 35f;
    float P2_ROTATION = (float) Math.toRadians(time * 1.3);
    Matrix4f planet2Matrix = new Matrix4f()
        .rotate(P2_ROTATION, 0, 1, 0)
        .scale(P2_RADIUS, P2_RADIUS, P2_RADIUS);
    starMap.put("planet2",
        new Sphere(gl, program, shaderLocations, planet2Matrix, "planet2", .757f, .267f, .055f));

    // planet3
    float P3_RADIUS = 50f;
    float P3_ROTATION = (float) Math.toRadians(time / 2);
    Matrix4f planet3Matrix = new Matrix4f()
        .rotate(P3_ROTATION, 0, 1, 0)
        .scale(P3_RADIUS, P3_RADIUS, P3_RADIUS);
    starMap.put("planet3",
        new Sphere(gl, program, shaderLocations, planet3Matrix, "planet3", .992f, .651f, 0f));

    // planet4
    float P4_RADIUS = 60f;
    float P4_ROTATION = (float) Math.toRadians(time / 6);
    Matrix4f planet4Matrix = new Matrix4f()
        .rotate(P4_ROTATION, 0, 1, 0)
        .scale(P4_RADIUS, P4_RADIUS, P4_RADIUS);
    starMap.put("planet4",
        new Sphere(gl, program, shaderLocations, planet4Matrix, "planet4", 0f, .773f, .5f));

    // planet4 satellite1
    float P4_S1_RADIUS = 10f;
    float P4_S1_ROTATION = (float) Math.toRadians(time * 5);
    Matrix4f planet4Satellite1Matrix = new Matrix4f()
        .rotate(P4_S1_ROTATION, 0, 1, 0)
        .scale(P4_S1_RADIUS, P4_S1_RADIUS, P4_S1_RADIUS);
    starMap.put("planet4Satellite1",
        new Sphere(gl, program, shaderLocations, planet4Satellite1Matrix, "planet4Satellite1", 0,
            0, 1));

    // planet4 satellite1
    float P4_S2_RADIUS = 15f;
    float P4_S2_ROTATION = (float) Math.toRadians(time * 1.5);
    Matrix4f planet4Satellite2Matrix = new Matrix4f()
        .rotate(P4_S2_ROTATION, 0, 1, 0)
        .scale(P4_S2_RADIUS, P4_S2_RADIUS, P4_S2_RADIUS);
    starMap.put("planet4Satellite2",
        new Sphere(gl, program, shaderLocations, planet4Satellite2Matrix, "planet4Satellite2", 1,
            .753f, 0.796f));

    starMap.put("orbit", new HollowCircle(gl, program, shaderLocations, "orbit", 1, 1, 1));
    starMap.put("box", new Box(gl, program, shaderLocations, "box", .7f, .7f, .7f));
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
    GL3 gl = gla.getGL().getGL3();

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
        .lookAt(new Vector3f(2000, 2000, 1000), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));

    // draw box
    starMap.get("box").draw(gla, modelView.peek(), proj);
    // draw sun
    starMap.get("sun").draw(gla, modelView.peek(), proj);

    // planet1 info
    float P1_REV_RAD = 120;
    float P1_REV_ANG = (float) Math.toRadians(time * 2);
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek().scale(P1_REV_RAD, P1_REV_RAD, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    // planet1
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(
            P1_REV_RAD * (float) Math.cos(P1_REV_ANG),
            P1_REV_RAD * (float) Math.sin(P1_REV_ANG),
            0);
    starMap.get("planet1").draw(gla, modelView.peek(), proj);
    // planet1 satellite1 info
    float P1_S1_REV_RAD = 40;
    float P1_S1_REV_ANG = (float) Math.toRadians(time * 5);
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek().scale(P1_S1_REV_RAD, P1_S1_REV_RAD, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(
            P1_S1_REV_RAD * (float) Math.cos(P1_S1_REV_ANG),
            P1_S1_REV_RAD * (float) Math.sin(P1_S1_REV_ANG),
            0);
    starMap.get("planet1Satellite1").draw(gla, modelView.peek(), proj);
    // pop planet 1 satellite 1 info
    modelView.pop();
    // pop planet 1 info
    modelView.pop();

    // planet2 info
    float P2_REV_RAD = 220;
    float P2_REV_ANG = (float) Math.toRadians(time * 1.2);
    float P2_ORBIT_ANG = (float) Math.PI / 6;
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .rotate(P2_ORBIT_ANG, 0, 1, 0)
        .scale(P2_REV_RAD, P2_REV_RAD, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    // planet 2
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .rotate(P2_ORBIT_ANG, 0, 1, 0)
        .translate(
            P2_REV_RAD * (float) Math.cos(P2_REV_ANG),
            P2_REV_RAD * (float) Math.sin(P2_REV_ANG),
            0);
    starMap.get("planet2").draw(gla, modelView.peek(), proj);
    // pop planet 2 info
    modelView.pop();

    // planet3 info
    float P3_REV_RAD_A = 450;
    float P3_REV_RAD_B = 400;
    float P3_REV_ANG = (float) Math.toRadians(time / 2);
    float P3_REV_EA = (float) Math
        .sqrt(1 - (P3_REV_RAD_B * P3_REV_RAD_B / P3_REV_RAD_A / P3_REV_RAD_A)) * P3_REV_RAD_A;
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(0 - P3_REV_EA, 0, 0)
        .scale(P3_REV_RAD_A, P3_REV_RAD_B, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    // planet3
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(0 - P3_REV_EA, 0, 0)
        .translate(
            P3_REV_RAD_A * (float) Math.cos(P3_REV_ANG),
            P3_REV_RAD_B * (float) Math.sin(P3_REV_ANG),
            0);
    starMap.get("planet3").draw(gla, modelView.peek(), proj);
    // pop planet 3 info
    modelView.pop();

    // planet4 info
    float P4_REV_RAD = 900;
    float P4_REV_ANG = (float) Math.toRadians(time * .2);
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .scale(P4_REV_RAD, P4_REV_RAD, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();

    // planet4
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(
            P4_REV_RAD * (float) Math.cos(P4_REV_ANG),
            P4_REV_RAD * (float) Math.sin(P4_REV_ANG),
            0);
    starMap.get("planet4").draw(gla, modelView.peek(), proj);
    // planet4 satellite1 info
    float P4_S1_REV_RAD = 50;
    float P4_S1_REV_ANG = (float) Math.toRadians(time * 6);
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .scale(P4_S1_REV_RAD, P4_S1_REV_RAD, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    // planet4 satellite1
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(
            P4_S1_REV_RAD * (float) Math.cos(P4_S1_REV_ANG),
            P4_S1_REV_RAD * (float) Math.sin(P4_S1_REV_ANG),
            0);
    starMap.get("planet4Satellite1").draw(gla, modelView.peek(), proj);
    // pop planet 1 satellite 1 info
    modelView.pop(); // planet1 satellite1 info
    float P4_S2_REV_RAD_A = 120;
    float P4_S2_REV_RAD_B = 110;
    float P4_S2_REV_EA = (float) Math
        .sqrt(1 - (P4_S2_REV_RAD_B * P4_S2_REV_RAD_B / P4_S2_REV_RAD_A / P4_S2_REV_RAD_A)) * P4_S2_REV_RAD_A;
    float P4_S2_REV_ANG = (float) Math.toRadians(time / 1.5);
    // orbit info
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(P4_S2_REV_EA,0,0)
        .scale(P4_S2_REV_RAD_A, P4_S2_REV_RAD_B, 1);
    starMap.get("orbit").draw(gla, modelView.peek(), proj);
    // pop orbit info
    modelView.pop();
    // planet4 satellite2
    modelView.push(new Matrix4f(modelView.peek()));
    modelView.peek()
        .translate(P4_S2_REV_EA,0,0)
        .translate(
            P4_S2_REV_RAD_A * (float) Math.cos(P4_S2_REV_ANG),
            P4_S2_REV_RAD_B * (float) Math.sin(P4_S2_REV_ANG),
            0);
    starMap.get("planet4Satellite2").draw(gla, modelView.peek(), proj);
    // pop planet 4 satellite 2 info
    modelView.pop();
    // pop planet 4 info
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
        100000.0f);

    if (width > height) {
      proj = new Matrix4f()
          .ortho(-1500f * width / height, 1500f * width / height, -1500, 1500, 0.1f, 100000.0f);

    } else {
      proj = new Matrix4f()
          .ortho(-1500, 1500, -1500f * height / width, 1500f * height / width, 0.1f, 100000.0f);

    }

  }

  void dispose(GLAutoDrawable gla) {
    for (ISimpleObjectInstance obj : starMap.values()) {
      obj.cleanup(gla);
    }
  }
}
