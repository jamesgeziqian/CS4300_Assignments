import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import util.IVertexData;
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

/**
 * This class represents the model of a box around this solar system.
 */
public class UniverseBox extends ASolarSystemModel {

  public UniverseBox(GLAutoDrawable gla, ShaderProgram program,
      ShaderLocationsVault shaderLocations) {
    super(gla, program, shaderLocations);
  }

  /**
   * Initialize the orbit that is to be drawn, which is a box of 12 edges and 8 vertices.
   *
   * @param gla the canvas that elements will be drawn on
   */
  @Override
  void initObjects(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();

    int x = 1600;
    int y = 1600;
    int z = 500;

    Vector4f point0 = new Vector4f(x, y, z, 1);
    Vector4f point1 = new Vector4f(-x, y, z, 1);
    Vector4f point2 = new Vector4f(-x, -y, z, 1);
    Vector4f point3 = new Vector4f(x, -y, z, 1);

    Vector4f point4 = new Vector4f(x, y, -z, 1);
    Vector4f point5 = new Vector4f(-x, y, -z, 1);
    Vector4f point6 = new Vector4f(-x, -y, -z, 1);
    Vector4f point7 = new Vector4f(x, -y, -z, 1);

    List<Vector4f> positions = new ArrayList<>(
        Arrays.asList(point0, point1, point2, point3, point4, point5, point6, point7));

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

    List<Integer> indices = new ArrayList<>(
        Arrays.asList(0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7));

    //now we create a polygon mesh object
    PolygonMesh mesh = new PolygonMesh();

    mesh.setVertexData(vertexData);
    mesh.setPrimitives(indices);
    mesh.setPrimitiveType(GL.GL_LINES);
    mesh.setPrimitiveSize(2);

    Map<String, String> shaderToVertexAttribute = new HashMap<>();

    //currently there are two per-vertex attributes: position and color
    shaderToVertexAttribute.put("vPosition", "position");
    meshObject = new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh,
        "Box Frame");
  }

  /**
   * Draw the box outside the  on the canvas given according to a certain look at position.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  @Override
  public void draw(GLAutoDrawable gla, Matrix4f lookAt) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);

    Matrix4f modelview = new Matrix4f(lookAt);

    FloatBuffer color = FloatBuffer.wrap(new float[]{1, 1, 1, 0});

    //pass the modelview matrix to the shader
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, modelview.get(fb16));

    //send the color of the triangle
    gl.glUniform4fv(
        shaderLocations.getLocation("vColor")
        , 1, color);

    //draw the object
    meshObject.draw(gla);
  }

  @Override
  public void dispose(GLAutoDrawable gla) {
    this.meshObject.cleanup(gla);
  }
}
