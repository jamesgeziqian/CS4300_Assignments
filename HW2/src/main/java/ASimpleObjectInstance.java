import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.util.ArrayList;
import java.util.List;
import util.Material;
import util.ObjectInstance;
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
}
