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

public class SingleChar {

  private static float HEIGHT = 4;
  private static float HALF_HEIGHT = HEIGHT / 2;
  private static int LENGTH_DIV_HEIGHT = 8;
  private static float LENGTH_VERTICAL = HEIGHT * LENGTH_DIV_HEIGHT;
  private static float LENGTH_HORIZONTAL = LENGTH_VERTICAL / 3 * 2;
  private String namePrefix;
  private ObjectInstance[] objects;
  private GL3 gl;
  private util.ShaderProgram program;
  private util.ShaderLocationsVault shaderLocations;
  private float px, py;

  /**
   * Constructor needs to know information on how to create an object and the center of a digit.
   *
   * @param x This is the x coordinate of the center.
   * @param y This is the y coordinate of the center.
   * @param gl Passed in an GL3 object help creates the ObjectInstance.
   * @param program Shader program that helps set up the ObjectInstance.
   * @param shaderLocations Shader location that helps set up the ObjectInstance.
   * @param namePrefix A string that helps name the objects.
   */
  SingleChar(float x, float y, GL3 gl,
      util.ShaderProgram program,
      util.ShaderLocationsVault shaderLocations,
      String namePrefix) {
    this.gl = gl;
    this.program = program;
    this.shaderLocations = shaderLocations;
    this.namePrefix = namePrefix;
    objects = new ObjectInstance[16];
    px = x;
    py = y;
    // creates ObjectInstance that make up the 7 segments.
    initObjects();
  }

  /**
   * Creates the 16 segments by figuring out the origin of each segment.
   */
  private void initObjects() {
    float blank = Math.max(1, HEIGHT / 100);

    float seg1OriX = px + blank;
    float seg1OriY = py;
    objects[0] = genSingleSegObj(
        genSingleSegPos(seg1OriX, seg1OriY, false),
        namePrefix + ".Seg1");

    float seg2OriX = seg1OriX + LENGTH_HORIZONTAL + blank;
    float seg2OriY = seg1OriY - blank;
    objects[1] = genSingleSegObj(
        genSingleSegPos(seg2OriX, seg2OriY, true),
        namePrefix + ".Seg2");

    float seg3OriX = seg1OriX;
    float seg3OriY = seg1OriY - 2 * blank - LENGTH_VERTICAL;
    objects[2] = genSingleSegObj(
        genSingleSegPos(seg3OriX, seg3OriY, false),
        namePrefix + ".Seg3");

    float seg4OriX = px;
    float seg4OriY = py - blank;
    objects[3] = genSingleSegObj(genSingleSegPos(seg4OriX, seg4OriY, true),
        namePrefix + ".Seg4");

    float seg5OriX = seg3OriX - 2 * blank - LENGTH_HORIZONTAL;
    float seg5OriY = seg3OriY;
    objects[4] = genSingleSegObj(genSingleSegPos(seg5OriX, seg5OriY, false),
        namePrefix + ".Seg5");

    float seg6OriX = seg5OriX - blank;
    float seg6OriY = seg4OriY;
    objects[5] = genSingleSegObj(genSingleSegPos(seg6OriX, seg6OriY, true),
        namePrefix + ".Seg6");

    float seg7OriX = seg5OriX;
    float seg7OriY = py;
    objects[6] = genSingleSegObj(genSingleSegPos(seg7OriX, seg7OriY, false),
        namePrefix + ".Seg7");

    float seg8OriX = seg6OriX;
    float seg8OriY = seg7OriY + blank + LENGTH_VERTICAL;
    objects[7] = genSingleSegObj(genSingleSegPos(seg8OriX, seg8OriY, true),
        namePrefix + ".Seg8");

    float seg9OriX = seg7OriX;
    float seg9OriY = seg8OriY + blank;
    objects[8] = genSingleSegObj(genSingleSegPos(seg9OriX, seg9OriY, false),
        namePrefix + ".Seg9");

    float seg10OriX = px;
    float seg10OriY = py + blank + LENGTH_VERTICAL;
    objects[9] = genSingleSegObj(genSingleSegPos(seg10OriX, seg10OriY, true),
        namePrefix + ".Seg10");

    float seg11OriX = seg1OriX;
    float seg11OriY = seg10OriY + blank;
    objects[10] = genSingleSegObj(genSingleSegPos(seg11OriX, seg11OriY, false),
        namePrefix + ".Seg11");

    float seg12OriX = seg2OriX;
    float seg12OriY = seg10OriY;
    objects[11] = genSingleSegObj(genSingleSegPos(seg12OriX, seg12OriY, true),
        namePrefix + ".Seg12");

    float seg13OriX1 = seg1OriX + HALF_HEIGHT;
    float seg13OriY1 = seg1OriY + HALF_HEIGHT + blank;
    float seg13OriX2 = seg12OriX - HALF_HEIGHT - blank;
    float seg13OriY2 = seg12OriY - HALF_HEIGHT;
    objects[12] = genSingleSegObj(genSingleSegPos(seg13OriX1, seg13OriY1, seg13OriX2, seg13OriY2),
        namePrefix + ".Seg13");

    float seg14OriX1 = seg1OriX + HALF_HEIGHT;
    float seg14OriY1 = seg1OriY - HALF_HEIGHT - blank;
    float seg14OriX2 = seg3OriX + LENGTH_HORIZONTAL - HALF_HEIGHT;
    float seg14OriY2 = seg3OriY + HALF_HEIGHT + blank;
    objects[13] = genSingleSegObj(genSingleSegPos(seg14OriX1, seg14OriY1, seg14OriX2, seg14OriY2),
        namePrefix + ".Seg14");

    float seg15OriX1 = seg5OriX + HALF_HEIGHT;
    float seg15OriY1 = seg5OriY + HALF_HEIGHT + blank;
    float seg15OriX2 = seg4OriX - HALF_HEIGHT - blank;
    float seg15OriY2 = seg4OriY - HALF_HEIGHT;
    objects[14] = genSingleSegObj(genSingleSegPos(seg15OriX1, seg15OriY1, seg15OriX2, seg15OriY2),
        namePrefix + ".Seg15");

    float seg16OriX1 = seg9OriX + HALF_HEIGHT;
    float seg16OriY1 = seg9OriY - HALF_HEIGHT - blank;
    float seg16OriX2 = seg7OriX + LENGTH_HORIZONTAL - HALF_HEIGHT;
    float seg16OriY2 = seg7OriY + HALF_HEIGHT + blank;
    objects[15] = genSingleSegObj(genSingleSegPos(seg16OriX1, seg16OriY1, seg16OriX2, seg16OriY2),
        namePrefix + ".Seg16");

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
    float longPartialLengthVertical = LENGTH_VERTICAL - HALF_HEIGHT;
    float longPartialLengthHorizontal = LENGTH_HORIZONTAL - HALF_HEIGHT;
    ArrayList<Vector4f> result = new ArrayList<>();
    result.add(new Vector4f(oriX, oriY, 0, 1f));
    if (vertical) {
      result.add(new Vector4f(oriX + HALF_HEIGHT, oriY - HALF_HEIGHT, 0, 1f));
      result.add(new Vector4f(oriX + HALF_HEIGHT, oriY - longPartialLengthVertical, 0, 1f));
      result.add(new Vector4f(oriX, oriY - LENGTH_VERTICAL, 0, 1f));
      result.add(new Vector4f(oriX - HALF_HEIGHT, oriY - longPartialLengthVertical, 0, 1f));
      result.add(new Vector4f(oriX - HALF_HEIGHT, oriY - HALF_HEIGHT, 0, 1f));
    } else {

      result.add(new Vector4f(oriX + HALF_HEIGHT, oriY + HALF_HEIGHT, 0, 1f));
      result.add(new Vector4f(oriX + longPartialLengthHorizontal, oriY + HALF_HEIGHT, 0, 1f));
      result.add(new Vector4f(oriX + LENGTH_HORIZONTAL, oriY, 0, 1f));
      result.add(new Vector4f(oriX + longPartialLengthHorizontal, oriY - HALF_HEIGHT, 0, 1f));
      result.add(new Vector4f(oriX + HALF_HEIGHT, oriY - HALF_HEIGHT, 0, 1f));
    }
    return result;
  }

  private ArrayList<Vector4f> genSingleSegPos(float oriX1, float oriY1, float oriX2, float oriY2) {
    float triLength = (float) Math.sqrt(.4) * HEIGHT;
    ArrayList<Vector4f> result = new ArrayList<>();
    result.add(new Vector4f(oriX1, oriY1, 0, 1f));
    if (oriY1 < oriY2) {
      result.add(new Vector4f(oriX1, oriY1 + 2 * triLength, 0, 1f));
      result.add(new Vector4f(oriX2 - triLength, oriY2, 0, 1f));
      result.add(new Vector4f(oriX2, oriY2, 0, 1f));
      result.add(new Vector4f(oriX2, oriY2 - 2 * triLength, 0, 1f));
      result.add(new Vector4f(oriX1 + triLength, oriY1, 0, 1f));
    } else {
      result.add(new Vector4f(oriX1 + triLength, oriY1, 0, 1f));
      result.add(new Vector4f(oriX2, oriY2 + 2 * triLength, 0, 1f));
      result.add(new Vector4f(oriX2, oriY2, 0, 1f));
      result.add(new Vector4f(oriX2 - triLength, oriY2, 0, 1f));
      result.add(new Vector4f(oriX1, oriY1 - 2 * triLength, 0, 1f));
    }
    return result;
  }

  /**
   * Draw the character out.
   *
   * @param gla passed in GLAutoDrawable object to help draw a character.
   * @param data Which char to draw.
   */
  void draw(GLAutoDrawable gla, int data) {
    // Create data determining which segments to display.
    HashMap<Integer, int[]> map = new HashMap<>();
    // 0 - 9
    map.put(0, new int[]{1, 2, 4, 5, 7, 8, 10, 11, 12, 14});
    map.put(1, new int[]{11, 1});
    map.put(2, new int[]{8, 10, 11, 0, 6, 5, 4, 2});
    map.put(3, new int[]{8, 10, 11, 0, 6, 1, 2, 4});
    map.put(4, new int[]{7, 6, 0, 11, 1});
    map.put(5, new int[]{10, 8, 7, 6, 13, 2, 4});
    map.put(6, new int[]{10, 8, 7, 6, 0, 1, 2, 4, 5});
    map.put(7, new int[]{8, 10, 12, 14});
    map.put(8, new int[]{8, 10, 11, 1, 2, 4, 5, 7, 6, 0});
    map.put(9, new int[]{8, 10, 11, 1, 2, 4, 6, 0, 7});
    // A - Z
    map.put(10, new int[]{5, 7, 8, 10, 11, 1, 6, 0});//A
    map.put(11, new int[]{8, 10, 11, 1, 2, 4, 9, 3, 0});//B
    map.put(12, new int[]{10, 8, 7, 5, 4, 2});//C
    map.put(13, new int[]{8, 10, 11, 1, 2, 4, 9, 3});//D
    map.put(14, new int[]{10, 8, 7, 5, 4, 2, 6, 0});//E
    map.put(15, new int[]{10, 8, 7, 5, 6, 0});//F
    map.put(16, new int[]{10, 8, 7, 5, 4, 2, 0, 1});//G
    map.put(17, new int[]{7, 5, 6, 0, 11, 1});//H
    map.put(18, new int[]{8, 10, 9, 3, 4, 2});//I
    map.put(19, new int[]{11, 1, 2, 4, 5});//J
    map.put(20, new int[]{7, 6, 5, 12, 13});//K
    map.put(21, new int[]{7, 5, 4, 2});//L
    map.put(22, new int[]{7, 5, 15, 12, 11, 1});//M
    map.put(23, new int[]{7, 5, 15, 13, 1, 11});//N
    map.put(24, new int[]{8, 10, 11, 1, 2, 4, 5, 7});//O
    map.put(25, new int[]{7, 5, 8, 10, 11, 0, 6});//P
    map.put(26, new int[]{8, 10, 11, 1, 13, 2, 4, 5, 7});//Q
    map.put(27, new int[]{7, 5, 8, 10, 11, 0, 6, 13});//R
    map.put(28, new int[]{10, 8, 7, 6, 0, 1, 2, 4});//S
    map.put(29, new int[]{8, 10, 9, 3});//T
    map.put(30, new int[]{7, 5, 4, 2, 1, 11});//U
    map.put(31, new int[]{7, 5, 14, 12});//V
    map.put(32, new int[]{7, 5, 14, 13, 1, 11});//W
    map.put(33, new int[]{15, 13, 12, 14});//X
    map.put(34, new int[]{15, 12, 3});//Y
    map.put(35, new int[]{8, 10, 12, 14, 4, 2});//Z
    for (int i = 0; i < map.get(data).length; ++i) {
      objects[map.get(data)[i]].draw(gla);
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