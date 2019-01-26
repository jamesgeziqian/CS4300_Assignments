import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Vector4f;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class that represents a single digit. There is 7 ObjectInstance stored each
 * representing a segment. It figures out which segments to draw when passed in numbers.
 */
class SingleDigit {

  private static float HEIGHT = 4;
  private static int LENGTH_DIV_HEIGHT = 8;
  private static float LENGTH = HEIGHT * LENGTH_DIV_HEIGHT;
  private String namePrefix;
  private ObjectInstance[] objects;
  private GL3 gl;
  private util.ShaderProgram program;
  private util.ShaderLocationsVault shaderLocations;
  private float px, py;

  /**
   * Constructor needs to know information on how to create an object and the left top point of a
   * digit.
   *
   * @param x This is the x coordinate of the left top point.
   * @param y This is the y coordinate of the right top point.
   * @param gl Passed in an GL3 object help creates the ObjectInstance.
   * @param program Shader program that helps set up the ObjectInstance.
   * @param shaderLocations Shader location that helps set up the ObjectInstance.
   * @param namePrefix A string that helps name the objects.
   */
  SingleDigit(float x, float y, GL3 gl,
      util.ShaderProgram program,
      util.ShaderLocationsVault shaderLocations,
      String namePrefix) {
    this.gl = gl;
    this.program = program;
    this.shaderLocations = shaderLocations;
    this.namePrefix = namePrefix;
    objects = new ObjectInstance[7];
    px = x;
    py = y;
    // creates ObjectInstance that make up the 7 segments.
    initObjects();
  }

  /**
   * Creates the 7 segments by figuring out the origin of each segment.
   * segment are allocated in following order:
   * +----A----+
   * |         |
   * F         B
   * |         |
   * +----G----+
   * |         |
   * E         C
   * |         |
   * +----D----+
   *
   */
  private void initObjects() {
    float blank = Math.max(1, HEIGHT / 100);

    float segAOriX = px + blank;
    float segAOriY = py;
    objects[0] = genSingleSegObj(
        genSingleSegPos(segAOriX, segAOriY, false),
        namePrefix + ".SegA");

    float segBOriX = segAOriX + LENGTH + blank;
    float segBOriY = segAOriY - blank;
    objects[1] = genSingleSegObj(
        genSingleSegPos(segBOriX, segBOriY, true),
        namePrefix + ".SegB");

    float segCOriX = segBOriX;
    float segCOriY = segBOriY - 2 * blank - LENGTH;
    objects[2] = genSingleSegObj(
        genSingleSegPos(segCOriX, segCOriY, true),
        namePrefix + ".SegC");

    float segDOriX = segAOriX;
    float segDOriY = segAOriY - 4 * blank - 2 * LENGTH;
    objects[3] = genSingleSegObj(genSingleSegPos(segDOriX, segDOriY, false),
        namePrefix + ".SegD");

    float segEOriX = segAOriX - blank;
    float segEOriY = segDOriY + blank + LENGTH;
    objects[4] = genSingleSegObj(genSingleSegPos(segEOriX, segEOriY, true),
        namePrefix + ".SegE");

    float segFOriX = segEOriX;
    float segFOriY = segBOriY;
    objects[5] = genSingleSegObj(genSingleSegPos(segFOriX, segFOriY, true),
        namePrefix + ".SegE");

    float segGOriX = segAOriX;
    float segGOriY = segEOriY + blank;
    objects[6] = genSingleSegObj(genSingleSegPos(segGOriX, segGOriY, false),
        namePrefix + ".SegG");

  }

  /**
   * Create a single segment ObjectInstance. Set up a PolygonMesh using position data. Then set up
   * the ObjectInstance using the mesh and given string as name.
   *
   * @param positions Data of positions
   * @param str Data of naming the object
   * @return A single segment ObjectInstance
   */
  private ObjectInstance genSingleSegObj(List<Vector4f> positions, String str) {
    List<IVertexData> vertexData = Utility.parseToIVertexData(positions);
    PolygonMesh<IVertexData> mesh = new PolygonMesh<>();
    mesh.setVertexData(vertexData);
    mesh.setPrimitives(getIndicesSingleSeg());
    mesh.setPrimitiveType(GL.GL_TRIANGLES);
    mesh.setPrimitiveSize(3);
    Map<String, String> shaderToVertexAttribute = new HashMap<>();
    shaderToVertexAttribute.put("vPosition", "position");
    return new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh, str);
  }

  /**
   * Hard code the indices to a List of Integer. Each segment shares the same indices data.
   *
   * @return Indices for a single segment.
   */
  private List<Integer> getIndicesSingleSeg() {
    // Draw the 4 triangles.
    ArrayList<Integer> result = new ArrayList<>();
    result.add(0);
    result.add(1);
    result.add(5);

    result.add(1);
    result.add(5);
    result.add(4);

    result.add(1);
    result.add(2);
    result.add(4);

    result.add(2);
    result.add(3);
    result.add(4);
    return result;
  }

  /**
   * This is the creation of positioning data of a single segment.
   *
   * @param oriX The x coordinate of the origin.
   * @param oriY The y coordinate of the origin.
   * @param vertical Whether it is a vertical segment or a horizontal segment.
   * @return Data of position of a single segment.
   */
  private ArrayList<Vector4f> genSingleSegPos(float oriX, float oriY, boolean vertical) {
    float halfHeight = HEIGHT / 2;
    float longPartialLength = LENGTH - halfHeight;
    ArrayList<Vector4f> result = new ArrayList<>();
    if (vertical) {
      result.add(new Vector4f(oriX, oriY, 0, 1f));
      result.add(new Vector4f(oriX + halfHeight, oriY - halfHeight, 0, 1f));
      result.add(new Vector4f(oriX + halfHeight, oriY - longPartialLength, 0, 1f));
      result.add(new Vector4f(oriX, oriY - LENGTH, 0, 1f));
      result.add(new Vector4f(oriX - halfHeight, oriY - longPartialLength, 0, 1f));
      result.add(new Vector4f(oriX - halfHeight, oriY - halfHeight, 0, 1f));
    } else {
      result.add(new Vector4f(oriX, oriY, 0, 1f));
      result.add(new Vector4f(oriX + halfHeight, oriY + halfHeight, 0, 1f));
      result.add(new Vector4f(oriX + longPartialLength, oriY + halfHeight, 0, 1f));
      result.add(new Vector4f(oriX + LENGTH, oriY, 0, 1f));
      result.add(new Vector4f(oriX + longPartialLength, oriY - halfHeight, 0, 1f));
      result.add(new Vector4f(oriX + halfHeight, oriY - halfHeight, 0, 1f));
    }
    return result;
  }

  /**
   * Draw the numbers out.
   *
   * @param gla passed in GLAutoDrawable object to help draw a number.
   * @param num Which number to draw.
   */
  void draw(GLAutoDrawable gla, int num) {
    // Create data determining which segments to display.
    HashMap<Integer, int[]> map = new HashMap<>();
    map.put(0, new int[]{0, 1, 2, 3, 4, 5});
    map.put(1, new int[]{1, 2});
    map.put(2, new int[]{0, 1, 3, 4, 6});
    map.put(3, new int[]{0, 1, 2, 3, 6});
    map.put(4, new int[]{1, 2, 5, 6});
    map.put(5, new int[]{0, 2, 3, 5, 6});
    map.put(6, new int[]{0, 2, 3, 4, 5, 6});
    map.put(7, new int[]{0, 1, 2});
    map.put(8, new int[]{0, 1, 2, 3, 4, 5, 6});
    map.put(9, new int[]{0, 1, 2, 3, 5, 6});
    for (int i = 0; i < map.get(num).length; ++i) {
      objects[map.get(num)[i]].draw(gla);
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
}
