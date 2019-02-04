import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector4f;
import util.IVertexData;
import util.Material;
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

public abstract class ASimpleObjectInstance implements ISimpleObjectInstance {

  protected List<ObjectInstance> meshObjList;
  protected ShaderLocationsVault shaderLocations;
  protected ShaderProgram program;
  protected GL3 gl;
  protected String name;
  protected float r, g, b;
  protected Material material;

  ASimpleObjectInstance(
      GL3 gl,
      ShaderProgram program,
      ShaderLocationsVault shaderLocations,
      String name) {
    meshObjList = new ArrayList<>();
    this.gl = gl;
    this.program = program;
    this.shaderLocations = shaderLocations;
    this.name = name;
  }

  ASimpleObjectInstance(
      GL3 gl,
      ShaderProgram program,
      ShaderLocationsVault shaderLocations,
      String name,
      float r,
      float g,
      float b) {
    this(gl, program, shaderLocations, name);
    this.r = r;
    this.g = g;
    this.b = b;
    util.Material mat = new util.Material();
    mat.setAmbient(this.r, this.g, this.b);
    mat.setDiffuse(1, 1, 1);
    mat.setSpecular(1, 1, 1);
    material = mat;
  }

  public void cleanup(GLAutoDrawable gla) {
    for (ObjectInstance obj : meshObjList) {
      obj.cleanup(gla);
    }
  }

  static List<IVertexData> transFormPositions(List<Vector4f> positions) {
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
    return vertexData;
  }

  static PolygonMesh<IVertexData> usualMeshSetup(List<Vector4f> positions, List<Integer> indices) {
    PolygonMesh<IVertexData> mesh = new PolygonMesh<>();
    mesh.setVertexData(transFormPositions(positions));
    mesh.setPrimitives(indices);
    mesh.setPrimitiveType(GL.GL_LINES);
    mesh.setPrimitiveSize(3);
    return mesh;
  }

  void usualObjAdd(List<Vector4f> positions, List<Integer> indices) {
    Map<String, String> shaderToVertexAttribute = new HashMap<>();
    shaderToVertexAttribute.put("vPosition", "position");
    meshObjList.add(new ObjectInstance(
        this.gl,
        this.program,
        this.shaderLocations,
        shaderToVertexAttribute,
        usualMeshSetup(positions, indices),
        this.name));
  }

}
