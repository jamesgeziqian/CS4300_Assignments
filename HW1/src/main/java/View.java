import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.joml.Vector4f;
import util.*;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ashesh on 9/18/2015.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly encapsulates all our
 * OpenGL functionality from the rest of Java GUI, managed by the JOGLFrame class.
 */
public class View {

  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  private Matrix4f proj, modelview;
  private Map<Character, ObjectInstance> objs;
  private ObjectInstance separater;
  private boolean separate;
  private ShaderLocationsVault shaderLocations;
  private int hour, min, sec;
  private Timer timer;
  private String message;

  ShaderProgram program;

  /**
   * Construct a View object. Set up current time and message to show.
   */
  public View() {
    proj = new Matrix4f();
    modelview = new Matrix4f();
    proj.identity();

    objs = null;
    separater = null;
    shaderLocations = null;
    WINDOW_WIDTH = WINDOW_HEIGHT = 0;
    Calendar calendar = Calendar.getInstance();
    hour = calendar.get(Calendar.HOUR_OF_DAY);
    min = calendar.get(Calendar.MINUTE);
    sec = calendar.get(Calendar.SECOND);
    timer = new Timer();
    message = "GMT-5 x";
  }

  /**
   * Separate a positive integer by digit into a int array.
   *
   * @param n the number that is to be separated
   * @return an array in which each element representing a digit
   */
  private static int[] separateNumber(int n, int digits) {
    if (0 > n) {
      throw new IllegalArgumentException("Positive number please.");
    }
    LinkedList<Integer> buffer = new LinkedList<>();
    int temp = n;
    while (temp >= 10) {
      int r = n % 10;
      buffer.add(0, r);
      temp = (n - r) / 10;
    }
    buffer.add(0, temp);

    int[] result = new int[digits];
    int index = 0;
    if (buffer.size() < digits) {
      for (index = 0; index < digits - buffer.size(); ++index) {
        result[index] = 0;
      }
    }

    for (Integer i : buffer) {
      result[index] = i;
      ++index;
    }

    return result;
  }

  /**
   * This method initialize all the data that will be rendered by creating ObjectInstances for all
   * things that will be rendered and set a timer up to refresh the time.
   *
   * @param gla the canvas that we will be drawing on
   * @throws Exception pass the exception created by methods called to the upper level
   */
  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = gla.getGL().getGL3();

    //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

    shaderLocations = program.getAllShaderVariables(gl);

    // Prepare ObjectInstances of all characters that can be drawn
    Map<Character, PolygonMesh> positions = SISD.vertexData(SISD.alphabetSISD);

    Map<String, String> shaderToVertexAttribute = new HashMap<>();

    //currently there are one per-vertex attributes: position
    shaderToVertexAttribute.put("vPosition", "position");

    objs = new HashMap<>();
    for (Entry<Character, PolygonMesh> entry : positions.entrySet()) {
      ObjectInstance obj = new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute,
          entry.getValue(), "triangles");
      objs.put(entry.getKey(), obj);
    }

    // set up the ObjectInstance of the separater
    List<Vector4f> circle = new ArrayList<>();
    circle.add(new Vector4f(0f, 0f, 0f, 1f));

    int radius = 1;
    int SLICES = 100;

    for (int i = 0; i <= SLICES; i++) {
      double angle = 2 * Math.PI * i / SLICES;
      circle.add(new Vector4f((float) (radius * Math.cos(angle)),
          (float) (radius * Math.sin(angle)), 0, 1));
    }

    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i <= SLICES; i++) {
      indices.add(i);
      indices.add(i + 1);
    }

    //set up vertex attributes (in this case we have only position)
    List<IVertexData> vertexData = new ArrayList<>();
    VertexAttribProducer producer = new VertexAttribProducer();
    for (Vector4f pos : circle) {
      IVertexData v = producer.produce();
      v.setData("position", new float[]{pos.x,
          pos.y,
          pos.z,
          pos.w});
      vertexData.add(v);
    }

    //now we create a polygon mesh object
    PolygonMesh mesh;
    mesh = new PolygonMesh();

    mesh.setVertexData(vertexData);
    mesh.setPrimitives(indices);
    mesh.setPrimitiveType(GL.GL_TRIANGLE_FAN);
    mesh.setPrimitiveSize(3);

    separater = new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh,
        "triangles");

    program.disable(gl);

    // set the timer to alert once every second
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        int temp = sec; // temporarily stores the previous sec value
        sec = calendar.get(Calendar.SECOND);

        // If a sec has passed, flash the separator
        if (temp != sec) {
          separate = !separate;
        }
      }
    }, 0, 100);
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
    gl.glClear(gl.GL_COLOR_BUFFER_BIT);
    //enable the shader program
    program.enable(gl);

    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));

    // set color to be green
    FloatBuffer color = FloatBuffer.wrap(new float[]{0, 1, 0, 0});
    gl.glUniform4fv(shaderLocations.getLocation("vColor"), 1, color);

    // Decide which number will be drawn
    int[] hour = separateNumber(this.hour, 2);
    int[] min = separateNumber(this.min, 2);
    int[] sec = separateNumber(this.sec, 2);
    int[] time = new int[]{hour[0], hour[1], min[0], min[1], sec[0], sec[1]};

    // Draw all six numbers on the canvas
    int i = 0;
    for (int s : time) {

      // Set the offset along x axis
      modelview = new Matrix4f();
      // modelview.scale(5, 5, 5).translate(-48f + 16 * i, 0, 0);
//      modelview.scale(5,5,5);
//      modelview.translate(-48f + 16 * i, 0, 0);
      modelview = modelview.scale(5,5,5);
      modelview = modelview.translate(-48f + 16 * i, 0, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));

      // Draw the object
      objs.get(Character.forDigit(s, 10)).draw(gla);
      ++i;
    }

    // Draw the message that will be shown on the screen
    char[] messageArray = message.toUpperCase().toCharArray();
    i = 0;
    for (char c : messageArray) {
      // Set the offset along x axis, and make it always centered
      modelview = new Matrix4f();
      modelview.scale(2, 2, 2).translate(-8 * messageArray.length + 16 * i, 80, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));

      // Draw the object
      objs.get(c).draw(gla);
      ++i;
    }

    // Draw the separator according to the current state
    if (separate) {
      modelview = new Matrix4f();
      modelview.scale(5, 5, 5).translate(-18f, 15f, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));
      separater.draw(gla);

      modelview = new Matrix4f();
      modelview.scale(5, 5, 5).translate(-18f, 7f, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));
      separater.draw(gla);

      modelview = new Matrix4f();
      modelview.scale(5, 5, 5).translate(14f, 15f, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));
      separater.draw(gla);

      modelview = new Matrix4f();
      modelview.scale(5, 5, 5).translate(14f, 7f, 0);
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));
      separater.draw(gla);
    }

    gl.glFlush();
    //disable the program
    program.disable(gl);
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

    // Make the content always centered and maintaining a good proportion
//    int dest, offsetX, offsetY;
//    dest = Math.min(width, height);
//    if (width < height) {
//      offsetX = 0;
//      offsetY = (height - dest) / 2;
//    } else {
//      offsetX = (width - dest) / 2;
//      offsetY = 0;
//    }

    float projX, projY;
    if (width < height) {
      projX = 600;
      projY = ((float) height / width) * 600;
    } else {
      projY = 600;
      projX = ((float) width / height) * 600;
    }

    // gl.glViewport(offsetX, offsetY, dest, dest);
    gl.glViewport(0, 0, width, height);

    /// proj = new Matrix4f().ortho2D(-300, 300, -300, 300);
    proj = new Matrix4f().ortho2D(-projX / 2, projX / 2, -projY / 2, projY / 2);
  }

  /**
   * This method is called when the window is closed and will cleanup all the ObjectInstances.
   *
   * @param gla the canvas that we will be drawing on
   */
  public void dispose(GLAutoDrawable gla) {
    for (ObjectInstance obj : objs.values()) {
      obj.cleanup(gla);
    }
  }
}
