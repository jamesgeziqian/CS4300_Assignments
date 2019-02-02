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
import java.util.Stack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import util.Material;
import util.ObjectInstance;
import util.ShaderLocationsVault;
import util.ShaderProgram;

public class Sphere {

  private ObjectInstance meshObj;
  private Material material;
  private ShaderLocationsVault shaderLocations;
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

    this.modelView = modelView;
    this.shaderLocations = shaderLocations;

    InputStream in = new FileInputStream("models/sphere.obj");
    util.PolygonMesh tmesh = util.ObjImporter.importFile(new VertexAttribProducer(), in, true);

    Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();
    shaderToVertexAttribute.put("vPosition", "position");

    meshObj = new util.ObjectInstance(gl, program, shaderLocations,
        shaderToVertexAttribute,
        tmesh, name);

    util.Material mat = new util.Material();
    mat.setAmbient(r, g, b);
    mat.setDiffuse(1, 1, 1);
    mat.setSpecular(1, 1, 1);
    material = mat;
  }

  Sphere(GL3 gl,
      ShaderProgram program,
      ShaderLocationsVault shaderLocations,
      String name,
      float r,
      float g,
      float b) throws FileNotFoundException {
    this(gl, program, shaderLocations, new Matrix4f().identity(), name, r, g, b);
  }

  void draw(GLAutoDrawable gla, Matrix4f outModelView, Matrix4f proj) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);
    Matrix4f mv = new Matrix4f(outModelView).mul(new Matrix4f(modelView));
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

    meshObj.draw(gla);
  }

  void cleanup(GLAutoDrawable gla){
    meshObj.cleanup(gla);
  }
}
