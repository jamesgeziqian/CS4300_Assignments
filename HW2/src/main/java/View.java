import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

import util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;


/**
 * Created by ashesh on 9/18/2015.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly encapsulates all our
 * OpenGL functionality from the rest of Java GUI, managed by the JOGLFrame class.
 */
public class View {

  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  private Matrix4f proj, lookAt, rotation;
  private ISolarSystemModel[] solarSystem;

  private ShaderProgram program;
  private ShaderLocationsVault shaderLocations;


  /**
   * Construct a View object. Set up current position and rotation.
   */
  public View() {
    proj = new Matrix4f().identity();
    lookAt = new Matrix4f().identity();
    rotation = new Matrix4f().identity();
  }

  /**
   * Initialize the solar system model and shader program.
   *
   * @param gla the canvas that the program will be drawing on
   * @throws Exception shader program will throw out exception when shader files are not found
   */
  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = gla.getGL().getGL3();

    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

    shaderLocations = program.getAllShaderVariables(gl);

    solarSystem = new ISolarSystemModel[3];
    solarSystem[0] = new CelestialBody(gla, program, shaderLocations);
    solarSystem[1] = new Orbit(gla, program, shaderLocations);
    solarSystem[2] = new UniverseBox(gla, program, shaderLocations);
  }


  /**
   * This method will draw images on the canvas space according to the current state.
   *
   * @param gla the canvas that we will be drawing on
   */
  public void draw(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);

    //set the background color to be black
    gl.glClearColor(0, 0, 0, 1);
    //clear the background
    gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL.GL_DEPTH_TEST);
    //enable the shader program
    program.enable(gl);

    //pass the projection matrix to the shader
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));

    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); //OUTLINES

    lookAt = new Matrix4f().lookAt(new Vector3f(0, 0, 3000), new Vector3f(0,
        0, 0), new Vector3f(0, 1, 0));

    Matrix4f afterRotation = new Matrix4f(lookAt).mul(rotation);

    for (ISolarSystemModel system : solarSystem) {
      system.draw(gla, new Matrix4f(afterRotation));
    }

    gl.glFlush();
    //disable the program
    program.disable(gl);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_FILL); //BACK TO FILL
  }

  /**
   * Rotate the model according to the direction user dragged the image to. Gimbals lock is avoided
   * by applying the new rotation on top of all previous rotations.
   *
   * @param x horizontal dragging will result in a rotation about y-axis
   * @param y vertical dragging will result in a rotation about x-axis
   */
  public void rotateOmega(int x, int y) {
    Matrix4f rotationMatrix = new Matrix4f().identity();
    rotationMatrix.rotateY((float) Math.toRadians(x));
    rotationMatrix.rotateX((float) Math.toRadians(y));
    this.rotation = rotationMatrix.mul(this.rotation);
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

    proj = new Matrix4f().perspective((float) Math.toRadians(120.0f),
        (float) width / height,
        0.1f,
        10000.0f);
  }

  /**
   * This method is called when the window is closed and will cleanup all the ObjectInstances.
   *
   * @param gla the canvas that we will be drawing on
   */
  public void dispose(GLAutoDrawable gla) {
    for (ISolarSystemModel system : solarSystem) {
      system.dispose(gla);
    }
  }
}
