import com.jogamp.opengl.GL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector4f;
import util.IVertexData;
import util.PolygonMesh;

/**
 * This class stores all the original data of a sixteen-segment display. All pubic methods are
 * static so user do not need to construct a SISD object.
 */
public class SISD {

  private static final Vector4f[][] basicBones;
  private static final int[] basicIndices;
  private static final Vector4f[][] lookupBones;
  private static final int[][] lookupIndices;
  private static final Map<Character, int[]> lookupAlphabet;

  /**
   * This field stores all chars that is supported by SISD for other classes who would like to
   * check.
   */
  public static final char[] alphabetSISD;

  static {
    alphabetSISD = "1234567890-+QWERTYUIOPASDFGHJKLZXCVBNM ".toCharArray();

    basicIndices = new int[]{0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5};

    basicBones = new Vector4f[3][];
    basicBones[0] = new Vector4f[]{
        new Vector4f(2f, 2f, 0, 1f),
        new Vector4f(1f, 1f, 0, 1f),
        new Vector4f(2f, 0f, 0, 1f),
        new Vector4f(5f, 0f, 0, 1f),
        new Vector4f(6f, 1f, 0, 1f),
        new Vector4f(5f, 2f, 0, 1f)};
    basicBones[1] = new Vector4f[]{
        new Vector4f(0f, 10f, 0, 1f),
        new Vector4f(0f, 2f, 0, 1f),
        new Vector4f(1f, 1f, 0, 1f),
        new Vector4f(2f, 2f, 0, 1f),
        new Vector4f(2f, 10f, 0, 1f),
        new Vector4f(1f, 11f, 0, 1f)};
    basicBones[2] = new Vector4f[]{
        new Vector4f(4f, 10f, 0, 1f),
        new Vector4f(2f, 4f, 0, 1f),
        new Vector4f(2f, 2f, 0, 1f),
        new Vector4f(3f, 2f, 0, 1f),
        new Vector4f(5f, 8f, 0, 1f),
        new Vector4f(5f, 10f, 0, 1f)};

    lookupBones = new Vector4f[16][];

    lookupBones[0] = copyBone(basicBones[0]);
    moveBone(lookupBones[0], 0, 20, 0, 0);

    lookupBones[1] = copyBone(basicBones[0]);
    moveBone(lookupBones[1], 5, 20, 0, 0);

    lookupBones[2] = copyBone(basicBones[1]);
    moveBone(lookupBones[2], 0, 10, 0, 0);

    lookupBones[3] = copyBone(basicBones[1]);
    moveBone(lookupBones[3], 5, 10, 0, 0);

    lookupBones[4] = copyBone(basicBones[1]);
    moveBone(lookupBones[4], 10, 10, 0, 0);

    lookupBones[5] = copyBone(basicBones[0]);
    moveBone(lookupBones[5], 0, 10, 0, 0);

    lookupBones[6] = copyBone(basicBones[0]);
    moveBone(lookupBones[6], 5, 10, 0, 0);

    lookupBones[7] = copyBone(basicBones[1]);
    moveBone(lookupBones[7], 0, 0, 0, 0);

    lookupBones[8] = copyBone(basicBones[1]);
    moveBone(lookupBones[8], 5, 0, 0, 0);

    lookupBones[9] = copyBone(basicBones[1]);
    moveBone(lookupBones[9], 10, 0, 0, 0);

    lookupBones[10] = copyBone(basicBones[0]);
    moveBone(lookupBones[10], 0, 0, 0, 0);

    lookupBones[11] = copyBone(basicBones[0]);
    moveBone(lookupBones[11], 5, 0, 0, 0);

    lookupBones[12] = copyBone(basicBones[2]);
    mirrorX2D(lookupBones[12]);
    moveBone(lookupBones[12], 0, 22, 0, 0);

    lookupBones[13] = copyBone(basicBones[2]);
    moveBone(lookupBones[13], 5, 10, 0, 0);

    lookupBones[14] = copyBone(basicBones[2]);
    moveBone(lookupBones[14], 0, 0, 0, 0);

    lookupBones[15] = copyBone(basicBones[2]);
    mirrorY2D(lookupBones[15]);
    moveBone(lookupBones[15], 12, 0, 0, 0);

    lookupIndices = new int[16][];
    for (int i = 0; i < 16; ++i) {
      lookupIndices[i] = basicIndices.clone();
      for (int j = 0; j < lookupIndices[i].length; ++j) {
        lookupIndices[i][j] += i * 6;
      }
    }

    lookupAlphabet = new HashMap<>();
    lookupAlphabet.put('0', new int[]{0, 1, 2, 4, 7, 9, 10, 11, 13, 14});
    lookupAlphabet.put('1', new int[]{4, 9});
    lookupAlphabet.put('2', new int[]{0, 1, 4, 5, 6, 7, 10, 11});
    lookupAlphabet.put('3', new int[]{0, 1, 4, 5, 6, 9, 10, 11});
    lookupAlphabet.put('4', new int[]{2, 4, 5, 6, 9});
    lookupAlphabet.put('5', new int[]{0, 1, 2, 5, 6, 9, 10, 11});
    lookupAlphabet.put('6', new int[]{0, 1, 2, 5, 6, 7, 9, 10, 11});
    lookupAlphabet.put('7', new int[]{0, 1, 4, 9});
    lookupAlphabet.put('8', new int[]{0, 1, 2, 4, 5, 6, 7, 9, 10, 11});
    lookupAlphabet.put('9', new int[]{0, 1, 2, 4, 5, 6, 9, 10, 11});
    lookupAlphabet.put('A', new int[]{0, 1, 2, 4, 5, 6, 7, 9});
    lookupAlphabet.put('B', new int[]{0, 1, 3, 4, 5, 6, 8, 9, 10, 11});
    lookupAlphabet.put('C', new int[]{0, 1, 2, 7, 10, 11});
    lookupAlphabet.put('D', new int[]{0, 1, 3, 4, 8, 9, 10, 11});
    lookupAlphabet.put('E', new int[]{0, 1, 2, 5, 7, 10, 11});
    lookupAlphabet.put('F', new int[]{0, 1, 2, 5, 7});
    lookupAlphabet.put('G', new int[]{0, 1, 2, 7, 9, 10, 11});
    lookupAlphabet.put('H', new int[]{2, 4, 5, 6, 7, 9});
    lookupAlphabet.put('I', new int[]{0, 1, 3, 8, 10, 11});
    lookupAlphabet.put('J', new int[]{4, 7, 9, 10, 11});
    lookupAlphabet.put('K', new int[]{2, 5, 7, 13, 15});
    lookupAlphabet.put('L', new int[]{2, 7, 10, 11});
    lookupAlphabet.put('M', new int[]{2, 4, 7, 9, 12, 13});
    lookupAlphabet.put('N', new int[]{2, 4, 7, 9, 12, 15});
    lookupAlphabet.put('O', new int[]{0, 1, 2, 4, 7, 9, 10, 11});
    lookupAlphabet.put('P', new int[]{0, 1, 2, 4, 5, 6, 7});
    lookupAlphabet.put('Q', new int[]{0, 1, 2, 4, 7, 9, 10, 11, 15});
    lookupAlphabet.put('R', new int[]{0, 1, 2, 4, 5, 6, 7, 15});
    lookupAlphabet.put('S', new int[]{0, 1, 2, 5, 6, 9, 10, 11});
    lookupAlphabet.put('T', new int[]{0, 1, 3, 8});
    lookupAlphabet.put('U', new int[]{2, 4, 7, 9, 10, 11});
    lookupAlphabet.put('V', new int[]{2, 7, 13, 14});
    lookupAlphabet.put('W', new int[]{2, 4, 7, 9, 14, 15});
    lookupAlphabet.put('X', new int[]{12, 13, 14, 15});
    lookupAlphabet.put('Y', new int[]{8, 12, 13});
    lookupAlphabet.put('Z', new int[]{0, 1, 10, 11, 13, 14});
    lookupAlphabet.put('+', new int[]{3, 5, 6, 8});
    lookupAlphabet.put('-', new int[]{5, 6});
    lookupAlphabet.put(' ', new int[]{});
  }

  /**
   * This method transform an array of characters into a list of PolygonMesh that have all the data
   * needed.
   *
   * @param data array of chars that will be transformed
   * @return a list of PolygonMesh that represents all the passed-in chars
   */
  public static Map<Character, PolygonMesh> vertexData(char[] data) {
    Map<Character, PolygonMesh> result = new HashMap<>();
    for (char num : data) {
      PolygonMesh mesh = vertexData(num);
      result.put(num, mesh);
    }
    return result;
  }

  /**
   * This method will make a deep copy of all the Vector4f passed in as an array.
   *
   * @param bone an array of Vector4f representing a segment in SISD
   * @return a deep copy of all the passed-in vectors
   */
  private static Vector4f[] copyBone(Vector4f[] bone) {
    if (bone.length != 6) {
      throw new IllegalArgumentException("Not an appropriate bone.");
    }
    Vector4f[] newBone = new Vector4f[6];
    for (int i = 0; i < bone.length; ++i) {
      newBone[i] = new Vector4f(bone[i].x, bone[i].y, bone[i].z, bone[i].w);
    }
    return newBone;
  }

  /**
   * This method moves each Vector4fs in an array by a same specified x and y value.
   *
   * @param bone array of Vector4fs that is to be moved
   * @param x the x value that vectors is going to be moved
   * @param y the y value that vectors is going to be moved
   * @param z the z value that vectors is going to be moved
   * @param w the x value that vectors is going to be moved
   * @return array of Vector4fs that has been moved
   */
  private static Vector4f[] moveBone(Vector4f[] bone, float x, float y, float z, float w) {
    for (Vector4f vector : bone) {
      vector.add(x, y, z, w);
    }
    return bone;
  }

  /**
   * This method rotate an array of 2D Vector4fs along x-axis for exactly PI / 2 radian.
   *
   * @param bone array of Vector4fs that is to be rotated
   * @return array of Vector4fs that is to be rotated
   */
  private static Vector4f[] mirrorX2D(Vector4f[] bone) {
    for (Vector4f vector : bone) {
      vector.rotateX((float) Math.PI);
      vector.setComponent(2, 0f);
      vector.setComponent(3, 1f);
    }
    return bone;
  }

  /**
   * This method rotate an array of 2D Vector4fs along y-axis for exactly PI / 2 radians.
   *
   * @param bone array of Vector4fs that is to be rotated
   * @return array of Vector4fs that is to be rotated
   */
  private static Vector4f[] mirrorY2D(Vector4f[] bone) {
    for (Vector4f vector : bone) {
      vector.rotateY((float) Math.PI);
      vector.setComponent(2, 0f);
      vector.setComponent(3, 1f);
    }
    return bone;
  }

  /**
   * This method method transform an array of characters into a list of PolygonMesh that have all
   * the data needed.
   *
   * @param num the char that will be transformed into PolygonMesh
   * @return the PolygonMesh representing the passed-in char
   */
  private static PolygonMesh vertexData(char num) {
    // Add all position coordinates into
    List<Vector4f> positions = new ArrayList<>();
    for (Vector4f[] position : lookupBones) {
      positions.addAll(Arrays.asList(position));
    }

    //set up vertex attributes (in this case we have only position)
    List<IVertexData> vertexData = new ArrayList<>();
    VertexAttribProducer producer = new VertexAttribProducer();
    for (Vector4f pos : positions) {
      IVertexData v = producer.produce();
      v.setData("position", new float[]{pos.x,
          pos.y,
          pos.z,
          pos.w});
      vertexData.add(v);
    }

    int[] bones = lookupAlphabet.get(num);

    // set up indices for vertices
    ArrayList<Integer> indices = new ArrayList<>();
    for (int bone : bones) {
      int[] temp = lookupIndices[bone];
      for (int i : temp) {
        indices.add(i);
      }
    }

    PolygonMesh mesh = new PolygonMesh();

    mesh.setVertexData(vertexData);
    mesh.setPrimitives(indices);

    mesh.setPrimitiveType(GL.GL_TRIANGLES);
    mesh.setPrimitiveSize(3);
    return mesh;
  }
}
