import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import util.ShaderLocationsVault;
import util.ShaderProgram;

/**
 * This class draws a ball
 */
public class Sphere extends ASimpleObjectInstance {

  private Matrix4f modelView;

  Sphere(
      GL3 gl,
      ShaderProgram program,
      ShaderLocationsVault shaderLocations,
      Matrix4f modelView,
      String name,
      float r,
      float g,
      float b) throws FileNotFoundException {
    super(gl, program, shaderLocations, name, r, g, b);
    this.modelView = modelView;
    init();
  }

  /**
   * Read the sphere obj file and create a object of a ball
   */
  private void init() throws FileNotFoundException {
    InputStream in = new FileInputStream("models/sphere.obj");
    util.PolygonMesh mesh = util.ObjImporter.importFile(new VertexAttribProducer(), in, true);

    Map<String, String> shaderToVertexAttribute = new HashMap<>();
    shaderToVertexAttribute.put("vPosition", "position");

    meshObjList.add(new util.ObjectInstance(gl, program, shaderLocations,
        shaderToVertexAttribute,
        mesh, name));
  }

  @Override
  public void draw(GLAutoDrawable gla, Matrix4f outModelView, Matrix4f proj) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);
    Matrix4f mv = new Matrix4f(outModelView).mul(new Matrix4f(this.modelView));
    //pass the projection matrix to the shader
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));

    //pass the modelview matrix to the shader
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, mv.get(fb16));

    //send the color of the triangle
    gl.glUniform4fv(
        shaderLocations.getLocation("vColor")
        , 1, material.getAmbient().get(fb4));

    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); //OUTLINES

    meshObjList.get(0).draw(gla);
  }
}
