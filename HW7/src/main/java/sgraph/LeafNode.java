package sgraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import java.util.Stack;
import rayTracer.HitRecord;
import rayTracer.ThreeDRay;
import util.Light;

/**
 * This node represents the leaf of a scene graph. It is the only type of node that has actual
 * geometry to render.
 *
 * @author Amit Shesh
 */
public class LeafNode extends AbstractNode {

  /**
   * The name of the object instance that this leaf contains. All object instances are stored in the
   * scene graph itself, so that an instance can be reused in several leaves
   */
  protected String objInstanceName;
  /**
   * The material associated with the object instance at this leaf
   */
  protected util.Material material;

  protected String textureName;

  public LeafNode(String instanceOf, IScenegraph graph, String name) {
    super(graph, name);
    this.objInstanceName = instanceOf;
  }


  /*
   *Set the material of each vertex in this object
   */
  @Override
  public void setMaterial(util.Material mat) {
    material = new util.Material(mat);
  }

  /**
   * Set texture ID of the texture to be used for this leaf
   */
  @Override
  public void setTextureName(String name) {
    textureName = name;
  }

  @Override
  public List<HitRecord> rayCast(Stack<Matrix4f> modelView, ThreeDRay ray,
      IScenegraphRenderer renderer) {
    return renderer.checkHit(objInstanceName, ray, new Matrix4f(modelView.peek()), material, textureName);
  }

  /*
   * gets the material
   */
  public util.Material getMaterial() {
    return material;
  }

  @Override
  public INode clone() {
    LeafNode newclone = new LeafNode(this.objInstanceName, scenegraph, name);
    newclone.setMaterial(this.getMaterial());
    return newclone;
  }


  /**
   * Delegates to the scene graph for rendering. This has two advantages:
   * <ul>
   * <li>It keeps the leaf light.</li>
   * <li>It abstracts the actual drawing to the specific implementation of the scene graph
   * renderer</li>
   * </ul>
   *
   * @param context the generic renderer context {@link sgraph.IScenegraphRenderer}
   * @param modelView the stack of modelview matrices
   */
  @Override
  public void draw(IScenegraphRenderer context, Stack<Matrix4f> modelView)
      throws IllegalArgumentException {
    if (objInstanceName.length() > 0) {
      context.drawMesh(objInstanceName, material, textureName, modelView.peek());
    }
  }

  @Override
  public Map<Light, Matrix4f> getLights(Stack<Matrix4f> modelView) {
    Map<Light, Matrix4f> result = new HashMap<>();
    for (Light light : this.lights) {
      result.put(light, new Matrix4f(modelView.peek()));
    }
    return result;
  }
}
