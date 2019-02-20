import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

import java.util.Timer;
import java.util.TimerTask;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by ashesh on 9/18/2015.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly encapsulates all our
 * OpenGL functionality from the rest of Java GUI, managed by the JOGLFrame class.
 */
public class View {

  private enum TypeOfCamera {GLOBAL, RING}

  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  private int time;
  private Timer timer;
  private Stack<Matrix4f> modelView;
  private Matrix4f projection, trackballTransform;
  private float trackballRadius;
  private Vector2f mousePos;
  private Vector3f cameraPosition, centerPosition;
  private int isClockwise;

  private util.ShaderProgram program;
  private util.ShaderLocationsVault shaderLocations;
  private int projectionLocation;
  private sgraph.IScenegraph<VertexAttrib> scenegraph;
  private TypeOfCamera cameraMode;


  /**
   * Construct a View object. Set up current position and rotation.
   */
  public View() {
    projection = new Matrix4f();
    modelView = new Stack<>();
    trackballRadius = 300;
    trackballTransform = new Matrix4f();
    scenegraph = null;
    time = 0;
    timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (cameraMode.equals(TypeOfCamera.RING)) {
          time++;
        }
      }
    }, 10, 10);
    cameraMode = TypeOfCamera.RING;
    cameraPosition = new Vector3f(0, 0, 500);
    centerPosition = new Vector3f(0, 0, 0);
    isClockwise = 1;
  }

  /**
   * Set the camera position with the float array provided. The provided float array should have a
   * length of 3, representing the position of camera in 3D cartesian coordinates.
   *
   * @param position a float array representing position of camera
   */
  public void setCameraPosition(float[] position) {
    if (position != null && position.length >= 3) {
      this.cameraPosition = new Vector3f(position[0], position[1], position[2]);
    }
  }

  /**
   * Set the direction the camera is moving. Positive int to be counterclockwise, and negative int
   * to be clockwise.
   *
   * @param isClockwise an int which its sign represents the direction
   */
  public void setOrientation(int isClockwise) {
    if (isClockwise != 0) {
      this.isClockwise = isClockwise / Math.abs(isClockwise);
    }
  }

  /**
   * Set the camera position with the float array provided. The provided float array should have a
   * length of 3, representing the position of camera in 3D cartesian coordinates.
   *
   * @param position a float array representing position of camera
   */
  public void setCenterPosition(float[] position) {
    if (position != null && position.length >= 3) {
      this.centerPosition = new Vector3f(position[0], position[1], position[2]);
    }
  }

  /**
   * Set the camera to be global.
   */
  public void setGlobal() {
    cameraMode = TypeOfCamera.GLOBAL;
  }

  /**
   * Set the camera to be moving around the object.
   */
  public void setRing() {
    cameraMode = TypeOfCamera.RING;
  }

  /**
   * Initialize the scene graph that is to be drawn.
   *
   * @param gla an OpenGL canvas that scene graph will be drawn upon
   * @param in an input stream of an XML file that represents the scene graph
   * @throws Exception when the input stream is not valid
   */
  public void initScenegraph(GLAutoDrawable gla, InputStream in) throws Exception {
    GL3 gl = gla.getGL().getGL3();

    if (scenegraph != null) {
      scenegraph.dispose();
    }

    program.enable(gl);

    scenegraph = sgraph.SceneXMLReader.importScenegraph(in
        , new VertexAttribProducer());

    sgraph.IScenegraphRenderer renderer = new sgraph.GL3ScenegraphRenderer();
    renderer.setContext(gla);
    Map<String, String> shaderVarsToVertexAttribs = new HashMap<>();
    shaderVarsToVertexAttribs.put("vPosition", "position");
    shaderVarsToVertexAttribs.put("vNormal", "normal");
    shaderVarsToVertexAttribs.put("vTexCoord", "texcoord");
    renderer.initShaderProgram(program, shaderVarsToVertexAttribs);
    scenegraph.setRenderer(renderer);
    program.disable(gl);
  }

  /**
   * Initialize the shader program for drawing.
   *
   * @param gla an OpenGL canvas that scene graph will be drawn upon
   * @throws Exception when the shader is not valid
   */
  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = gla.getGL().getGL3();

    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new util.ShaderProgram();

    program.createProgram(gl, "shaders/default.vert", "shaders/default"
        + ".frag");

    shaderLocations = program.getAllShaderVariables(gl);

    //get input variables that need to be given to the shader program
    projectionLocation = shaderLocations.getLocation("projection");
  }

  /**
   * This method will draw images on the canvas space according to the current state.
   *
   * @param gla an OpenGL canvas that scene graph will be drawn upon
   */
  public void draw(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();

    gl.glClearColor(0, 0, 0, 1);
    gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(gl.GL_DEPTH_TEST);

    program.enable(gl);

    while (!modelView.empty()) {
      modelView.pop();
    }

    /*
     * In order to change the shape of this triangle, we can either move the vertex positions above, or "transform" them
     * We use a modelview matrix to store the transformations to be applied to our triangle.
     * The state of our modelview matrix will depend on the state of the camera, a global or a turntable camera.
     */
    modelView.push(new Matrix4f());
    if (cameraMode.equals(TypeOfCamera.GLOBAL)) {
      modelView.peek().lookAt(
          new Vector3f(cameraPosition),
          new Vector3f(centerPosition),
          new Vector3f(0, 1, 0))
          .mul(trackballTransform);
    } else {
      float phi = (float) Math.PI / 360 * time;
      float radius = (float) Math
          .sqrt(Math.pow(cameraPosition.x, 2) + Math.pow(cameraPosition.z, 2));

      modelView.peek().lookAt(
          new Vector3f(isClockwise * radius * (float) Math.cos(phi), cameraPosition.y,
              isClockwise * radius * (float) Math.sin(phi)),
          new Vector3f(centerPosition),
          new Vector3f(0, 1, 0));
    }

    /*
     *Supply the shader with all the matrices it expects.
     */
    FloatBuffer fb = Buffers.newDirectFloatBuffer(16);
    gl.glUniformMatrix4fv(projectionLocation, 1, false, projection.get(fb));

    scenegraph.draw(modelView);
    /*
     *OpenGL batch-processes all its OpenGL commands.
     *The next command asks OpenGL to "empty" its batch of issued commands, i.e. draw
     *
     *This a non-blocking function. That is, it will signal OpenGL to draw, but won't wait for it to
     *finish drawing.
     *
     *If you would like OpenGL to start drawing and wait until it is done, call glFinish() instead.
     */
    gl.glFlush();

    program.disable(gl);
  }

  /**
   * Called when the mouse is pressed. Record the position where the mouse is pressed.
   *
   * @param x the x coordinate of the mouse
   * @param y the y coordinate of the mouse
   */
  public void mousePressed(int x, int y) {
    mousePos = new Vector2f(x, y);
  }

  /**
   * Called when the mouse is released.
   *
   * @param x the x coordinate of the mouse
   * @param y the y coordinate of the mouse
   */
  public void mouseReleased(int x, int y) {
  }

  /**
   * This method is called each time the image is dragged by mouse, which is going to result in a
   * rotation of 3D objects showing on the screen.
   *
   * @param x the x coordinate of the mouse
   * @param y the y coordinate of the mouse
   */
  public void mouseDragged(int x, int y) {
    // When in global mode, we enables the track ball. If in Ring mode, we do nothing
    if (cameraMode.equals(TypeOfCamera.GLOBAL)) {
      Vector2f newM = new Vector2f(x, y);

      Vector2f delta = new Vector2f(newM.x - mousePos.x, newM.y - mousePos.y);
      mousePos = new Vector2f(newM);

      trackballTransform = new Matrix4f().rotate(delta.x / trackballRadius, 0, 1, 0)
          .rotate(delta.y / trackballRadius, 1, 0, 0)
          .mul(trackballTransform);
    }
  }

  /**
   * This method will be called each time the window is reshaped and the content will be resized
   * properly to maintain an appropriate proportion.
   *
   * @param gla the canvas that we will be drawing on
   * @param width the new width of window
   * @param height the new height of window
   */
  public void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
    GL gl = gla.getGL();
    WINDOW_WIDTH = width;
    WINDOW_HEIGHT = height;
    gl.glViewport(0, 0, width, height);

    projection = new Matrix4f()
        .perspective((float) Math.toRadians(120.0f), (float) width / height, 0.1f, 10000.0f);
    // proj = new Matrix4f().ortho(-400,400,-400,400,0.1f,10000.0f);

  }

  /**
   * This method is called when the window is closed and will cleanup all the ObjectInstances.
   *
   * @param gla the canvas that we will be drawing on
   */
  public void dispose(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();
  }
}