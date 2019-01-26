import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Vector4f;
import util.IVertexData;
import util.ObjectInstance;
import util.PolygonMesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the class rendering the dots between digits
 */
class TwoDots {

  private static float RADIUS = 2;
  private static int NUM_OF_TRI_IN_CIRCLE = 32;
  private ObjectInstance[] objects;
  private GL3 gl;
  private util.ShaderProgram program;
  private util.ShaderLocationsVault shaderLocations;
  private float px, py, interval;
  private String namePrefix;

  /**
   * Constructor needs to know information on how to create an object and the left top point center
   * of a two dots.
   *
   * @param x This is the x coordinate of the center.
   * @param y This is the y coordinate of the center.
   * @param interval The interval between two dots.
   */
  TwoDots(float x, float y, float interval, GL3 gl,
      util.ShaderProgram program,
      util.ShaderLocationsVault shaderLocations,
      String namePrefix) {
    this.gl = gl;
    this.program = program;
    this.shaderLocations = shaderLocations;
    this.px = x;
    this.py = y;
    this.interval = interval;
    this.namePrefix = namePrefix;
    objects = new ObjectInstance[2];
    initObjects();
  }

  // create two objects representing the two dots.
  private void initObjects() {
    objects[0] = genSingleDotObj(px, py + interval / 2, namePrefix + ".Dot1");
    objects[1] = genSingleDotObj(px, py - interval / 2, namePrefix + ".Dot2");
  }

  /**
   * Draw the dots out.
   *
   * @param gla passed in GLAutoDrawable object to help draw a number.
   */
  void draw(GLAutoDrawable gla) {
    for (ObjectInstance obj : objects) {
      obj.draw(gla);
    }
  }

  /**
   * Clean up all the ObjectInstance.
   *
   * @param gla GLAutoDrawable objects that passed to all objects that helps cleanup.
   */
  void cleanup(GLAutoDrawable gla) {
    for (ObjectInstance obj : objects) {
      obj.cleanup(gla);
    }
  }

  /**
   * This creates a single dot.
   *
   * @param x The x coordinate of the center of a dot
   * @param y The y coordinate of the center of a dot
   * @param str The name of that dot.
   * @return Returns an ObjectInstance represents the dot in given position.
   */
  private ObjectInstance genSingleDotObj(float x, float y, String str) {
    ArrayList<Vector4f> positions = new ArrayList<>();
    float theta = (float) Math.PI * 2 / NUM_OF_TRI_IN_CIRCLE;

    positions.add(new Vector4f(x, y, 0, 1f));
    for (int i = 0; i < NUM_OF_TRI_IN_CIRCLE; ++i) {
      positions.add(
          new Vector4f(x + RADIUS * (float) Math.cos(theta * i),
              y + RADIUS * (float) Math.sin(theta * i),
              0, 1f));
    }
    positions.add(new Vector4f(x + RADIUS, y, 0, 1f));
    List<IVertexData> vertexData = Utility.parseToIVertexData(positions);
    PolygonMesh<IVertexData> mesh = new PolygonMesh<>();
    mesh.setVertexData(vertexData);
    mesh.setPrimitives(getIndicesSingleDot());
    mesh.setPrimitiveType(GL.GL_TRIANGLE_FAN);
    mesh.setPrimitiveSize(3);
    Map<String, String> shaderToVertexAttribute = new HashMap<>();
    shaderToVertexAttribute.put("vPosition", "position");
    return new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh, str);
  }

  /**
   * This is a helper method generating the indices. I used GL_TRIANGLE_FAN as primitive type, so
   * all I need is list all the numbers. Do not need to do what we have done in class.
   *
   * @return A list of interger representing the indices.
   */
  private List<Integer> getIndicesSingleDot() {
    ArrayList<Integer> result = new ArrayList<>();
    for (int i = 0; i < NUM_OF_TRI_IN_CIRCLE + 2; ++i) {
      result.add(i);
    }
    return result;
  }


}
