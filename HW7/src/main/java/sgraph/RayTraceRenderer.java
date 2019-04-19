package sgraph;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rayTracer.HitRecord;
import rayTracer.ThreeDRay;
import util.Material;
import util.TextureImage;

/**
 * This class represents a renderer that support ray tracing function. All other features are
 * succeed from LightScenegraphRenderer.
 */
public class RayTraceRenderer extends LightScenegraphRenderer {

  public RayTraceRenderer() {
    super();
  }

  /**
   * Check if a ray hit the object specified. If hit, return all hit records of the ray on that
   * object, including ins and outs.
   *
   * @param objectName the name of the object that is to be checked
   * @param ray the ray that is to be checked
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  @Override
  public List<HitRecord> checkHit(String objectName, ThreeDRay ray, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = new ArrayList<>();
    switch (objectName) {
      case "sphere":
        result.addAll(
            checkHitSphere(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      case "sphereInside":
        result.addAll(
            checkHitSphereInside(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      case "box":
        result.addAll(
            checkHitBox(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      case "boxOneSide":
        result.addAll(
            checkHitBoxOneSide(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      case "cylinder":
        result.addAll(
            checkHitCylinder(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      case "cone":
        result.addAll(
            checkHitCone(
                ray.getStartingPoint(),
                ray.getDirection(),
                new Matrix4f(modelView),
                mat,
                textureName));
        break;
      default:
        System.out.println("Not supported shape: " + objectName);
    }
    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a cylinder.
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitCylinder(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = new ArrayList<>();
    Matrix4f invertedMV = new Matrix4f();
    modelView.invert(invertedMV);
    Vector4f s = new Vector4f();
    Vector4f v = new Vector4f();
    invertedMV.transform(start, s);
    invertedMV.transform(vector, v);

    // intersect with the top surface
    float tTop = (1 - s.y) / v.y;
    float topInterX = s.x + tTop * v.x;
    float topInterZ = s.z + tTop * v.z;
    float topDisSq = topInterX * topInterX + topInterZ * topInterZ;
    if (topDisSq <= 1) {
      HitRecord topHit = new HitRecord();
      topHit.setT(tTop);
      // set material
      topHit.setMaterial(mat);

      // intersection in View
      Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(tTop));
      topHit.setIntersection(intersectionInView);

      Vector4f normal = new Vector4f(0, 1, 0, 0);
      Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
      invTranspose.transform(normal);
      topHit.setNormal(normal.x, normal.y, normal.z);

      TextureImage image = this.textures.get(textureName);
      if (!textureName.equals("") && image != null) {
        topHit.setTextureImage(image);
        float theta = (float) Math.atan2(-topInterZ, -topInterX);
        float texCoordX = (float) (theta / (2 * Math.PI)) + .5f;
        topHit.setTextureCoordinate(texCoordX, 1);
      }
      result.add(topHit);
    }

    // intersect with the bottom surface
    float tBot = -s.y / v.y;
    float botInterX = s.x + tBot * v.x;
    float botInterZ = s.z + tBot * v.z;
    float botDisSq = botInterX * botInterX + botInterZ * botInterZ;
    if (botDisSq <= 1) {
      HitRecord botHit = new HitRecord();
      botHit.setT(tBot);
      // set material
      botHit.setMaterial(mat);

      // intersection in View
      Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(tBot));
      botHit.setIntersection(intersectionInView);

      Vector4f normal = new Vector4f(0, -1, 0, 0);
      Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
      invTranspose.transform(normal);
      botHit.setNormal(normal.x, normal.y, normal.z);

      TextureImage image = this.textures.get(textureName);
      if (!textureName.equals("") && image != null) {
        botHit.setTextureImage(image);
        float theta = (float) Math.atan2(-botInterX, -botInterZ);
        float texCoordX = (float) (theta / (2 * Math.PI)) + .5f;
        botHit.setTextureCoordinate(texCoordX, 0);
      }
      result.add(botHit);
    }

    float a = v.x * v.x + v.z * v.z;
    float b = 2 * (v.x * s.x + v.z * s.z);
    float c = s.x * s.x + s.z * s.z - 1;
    float delta = b * b - 4 * a * c;
    if (delta >= 0) {
      float tsmall = (float) (-b - Math.sqrt(delta)) / (2 * a);
      float tBig = (float) (-b + Math.sqrt(delta)) / (2 * a);
      float t;
      if (tsmall >= 0) {
        t = tsmall;
      } else {
        t = tBig;
      }

      float x = s.x + t * v.x;
      float y = s.y + t * v.y;
      float z = s.z + t * v.z;
      if (y <= 1 && y >= 0) {
        HitRecord hit = new HitRecord();
        hit.setT(t);
        // set material
        hit.setMaterial(mat);

        // intersection in View
        Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(t));
        hit.setIntersection(intersectionInView);

        // set normal
        Vector4f intersection = new Vector4f(s).add(new Vector4f(v).mul(t));
        Vector4f normal = new Vector4f(intersection.x, 0, intersection.z, 0);
        Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
        invTranspose.transform(normal);
        hit.setNormal(normal.x, normal.y, normal.z);

        TextureImage image = this.textures.get(textureName);
        if (!textureName.equals("") && image != null) {
          hit.setTextureImage(image);
          float theta = (float) Math.atan2(-z, -x);
          float texCoordX = (float) (theta / (2 * Math.PI)) + .5f;
          hit.setTextureCoordinate(texCoordX, 1 - y);
        }
        result.add(hit);
      }
    }

    // set refraction
    hitSetFraction(result, vector);

    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a cone.
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitCone(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = new ArrayList<>();
    Matrix4f invertedMV = new Matrix4f();
    modelView.invert(invertedMV);
    Vector4f s = new Vector4f();
    Vector4f v = new Vector4f();
    invertedMV.transform(start, s);
    invertedMV.transform(vector, v);

    // intersect with the bottom surface
    float tBot = -s.y / v.y;
    float botInterX = s.x + tBot * v.x;
    float botInterZ = s.z + tBot * v.z;
    float botDisSq = botInterX * botInterX + botInterZ * botInterZ;
    if (botDisSq <= 1.001) {
      HitRecord botHit = new HitRecord();
      botHit.setT(tBot);
      // set material
      botHit.setMaterial(mat);

      // intersection in View
      Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(tBot));
      botHit.setIntersection(intersectionInView);

      Vector4f normal = new Vector4f(0, -1, 0, 0);
      Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
      invTranspose.transform(normal);
      botHit.setNormal(normal);

      TextureImage image = this.textures.get(textureName);
      if (!textureName.equals("") && image != null) {
        botHit.setTextureImage(image);
        float theta = (float) Math.atan2(-botInterX, botInterZ);
        float texCoordY = (float) Math.sqrt(botDisSq);
        float texCoordX = (float) (((theta / (2 * Math.PI)) + 1.25f) % 1f) * texCoordY;
        botHit.setTextureCoordinate(texCoordX, texCoordY);
      }
      result.add(botHit);
    }

    // intersect with side surface
    float a = v.y * v.y - v.x * v.x - v.z * v.z;
    float b = 2 * (s.y * v.y - s.x * v.x - s.z * v.z - v.y);
    float c = (s.y - 1) * (s.y - 1) - s.x * s.x - s.z * s.z;
    float delta = b * b - 4 * a * c;
    if (delta >= 0) {
      // intersection with double cone
      float t1 = (float) (-b + Math.sqrt(delta)) / (2 * a);
      float t2 = (float) (-b - Math.sqrt(delta)) / (2 * a);

      Vector4f intersection1 = new Vector4f(s).add(new Vector4f(v).mul(t1));
      Vector4f intersection2 = new Vector4f(s).add(new Vector4f(v).mul(t2));

      // check if t is in range
      List<Float> t = new ArrayList<>();
      List<Vector4f> intersections = new ArrayList<>();
      if (t1 > 0 && intersection1.y >= -0.001 && intersection1.y <= 1.001) {
        t.add(t1);
        intersections.add(intersection1);
      }
      if (t2 > 0 && intersection2.y >= -0.001 && intersection2.y <= 1.001) {
        t.add(t2);
        intersections.add(intersection2);
      }

      for (int i = 0; i < t.size(); i++) {
        float curT = t.get(i);
        Vector4f intersection = intersections.get(i);
        Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(curT));
        Vector4f normal;
        if (intersection.y < 0.999f) {
          normal = new Vector4f(intersection.x / (1 - intersection.y), 1f,
              intersection.z / (1 - intersection.y), 0);
        } else {
          // the top vertex is singular
          normal = new Vector4f(0, 1, 0, 0);
        }
        Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
        invTranspose.transform(normal);

        HitRecord hitRecord = new HitRecord();
        hitRecord.setT(curT);
        hitRecord.setIntersection(intersectionInView);
        hitRecord.setNormal(normal);
        hitRecord.setMaterial(mat);

        TextureImage image = this.textures.get(textureName);
        if (!textureName.equals("") && image != null) {
          hitRecord.setTextureImage(image);
          float theta = (float) Math.atan2(-intersection.z, -intersection.x);
          float texCoordX = (float) (theta / (2 * Math.PI)) + .5f;
          hitRecord.setTextureCoordinate(texCoordX, 1 - intersection.y);
        }
        result.add(hitRecord);
      }
    }

    // set refraction
    hitSetFraction(result, vector);

    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a box.
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitBox(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {

    List<HitRecord> result = new ArrayList<>();
    Matrix4f invertedMV = new Matrix4f();
    modelView.invert(invertedMV);
    Vector4f s = new Vector4f();
    Vector4f v = new Vector4f();
    invertedMV.transform(start, s);
    invertedMV.transform(vector, v);

    float txMin = Math.min((-0.5f - s.x) / v.x, (0.5f - s.x) / v.x);
    float txMax = Math.max((-0.5f - s.x) / v.x, (0.5f - s.x) / v.x);
    float tyMin = Math.min((-0.5f - s.y) / v.y, (0.5f - s.y) / v.y);
    float tyMax = Math.max((-0.5f - s.y) / v.y, (0.5f - s.y) / v.y);
    float tzMin = Math.min((-0.5f - s.z) / v.z, (0.5f - s.z) / v.z);
    float tzMax = Math.max((-0.5f - s.z) / v.z, (0.5f - s.z) / v.z);

    float tMin = Math.max(Math.max(txMin, tyMin), tzMin);
    float tMax = Math.min(Math.min(txMax, tyMax), tzMax);
    if (tMin <= tMax) {
      List<Float> tlist = new ArrayList<>();
      tlist.add(tMin);
      tlist.add(tMax);

      for (Float t : tlist) {
        // hit point goes in the polygon
        HitRecord hit = new HitRecord();
        hit.setT(t);

        // set material
        hit.setMaterial(mat);

        // intersection in View
        Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(t));
        hit.setIntersection(intersectionInView);

        // calculate normal vector
        Vector4f normal = new Vector4f(0, 0, 0, 0);
        // find intersection in obj coordinate system
        Vector4f intersection = new Vector4f(s).add(new Vector4f(v).mul(t));
        // find intersection in obj coordinate system
        if (intersection.x >= .499f && intersection.x <= .501f) {
          normal.x = 1f;
        } else if (intersection.x <= -.499f && intersection.x >= -.501f) {
          normal.x = -1f;
        }
        if (intersection.y >= .499f && intersection.y <= .501f) {
          normal.y = 1f;
        } else if (intersection.y <= -.499f && intersection.y >= -.501f) {
          normal.y = -1f;
        }
        if (intersection.z >= .499f && intersection.z <= .501f) {
          normal.z = 1f;
        } else if (intersection.z <= -.499f && intersection.z >= -.501f) {
          normal.z = -1f;
        }
        Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
        invTranspose.transform(normal);
        hit.setNormal(normal.x, normal.y, normal.z);

        // set texture
        // the origin of the texture coordinates is at lower left corner
        // so that the texture matches openGL
        TextureImage image = this.textures.get(textureName);
        if (!textureName.equals("") && image != null) {
          hit.setTextureImage(image);
          float textureX = 0, textureY = 0;
          if (intersection.x >= .499f && intersection.x <= .501f) { // right
            textureX = .5f + .25f * (intersection.z + .5f);
            textureY = .25f + .25f * (intersection.y + .5f);
          } else if (intersection.x <= -.499f && intersection.x >= -.501f) { // left
            textureX = .25f - .25f * (intersection.z + .5f);
            textureY = .25f + .25f * (intersection.y + .5f);
          } else if (intersection.y >= .499f && intersection.y <= .501f) { // top
            textureX = .25f + .25f * (intersection.x + .5f);
            textureY = .5f + .25f * (intersection.z + .5f);
          } else if (intersection.y <= -.499f && intersection.y >= -.501f) { // bottom
            textureX = .25f + .25f * (intersection.x + .5f);
            textureY = .25f - .25f * (intersection.z + .5f);
          } else if (intersection.z >= .499f && intersection.z <= .501f) { // front
            textureX = 1f - .25f * (intersection.x + .5f);
            textureY = .25f + .25f * (intersection.y + .5f);
          } else if (intersection.z <= -.499f && intersection.z >= -.501f) { // back
            textureX = .25f + .25f * (intersection.x + .5f);
            textureY = .25f + .25f * (intersection.y + .5f);
          }
          hit.setTextureCoordinate(textureX, -(textureY - 1));
        }
        result.add(hit);

      }

    }
    // set refraction
    hitSetFraction(result, vector);
    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a box. Texture looks like box.obj
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitBoxOneSide(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = new ArrayList<>();
    Matrix4f invertedMV = new Matrix4f();
    modelView.invert(invertedMV);
    Vector4f s = new Vector4f();
    Vector4f v = new Vector4f();
    invertedMV.transform(start, s);
    invertedMV.transform(vector, v);

    float txMin = Math.min((-0.5f - s.x) / v.x, (0.5f - s.x) / v.x);
    float txMax = Math.max((-0.5f - s.x) / v.x, (0.5f - s.x) / v.x);
    float tyMin = Math.min((-0.5f - s.y) / v.y, (0.5f - s.y) / v.y);
    float tyMax = Math.max((-0.5f - s.y) / v.y, (0.5f - s.y) / v.y);
    float tzMin = Math.min((-0.5f - s.z) / v.z, (0.5f - s.z) / v.z);
    float tzMax = Math.max((-0.5f - s.z) / v.z, (0.5f - s.z) / v.z);

    float tMin = Math.max(Math.max(txMin, tyMin), tzMin);
    float tMax = Math.min(Math.min(txMax, tyMax), tzMax);
    if (tMin <= tMax) {
      List<Float> tlist = new ArrayList<>();
      tlist.add(tMin);
      tlist.add(tMax);

      for (Float t : tlist) {
        // hit point goes in the polygon
        HitRecord hit = new HitRecord();
        hit.setT(t);

        // set material
        hit.setMaterial(mat);

        // intersection in View
        Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(t));
        hit.setIntersection(intersectionInView);

        // calculate normal vector
        Vector4f normal = new Vector4f(0, 0, 0, 0);
        // find intersection in obj coordinate system
        Vector4f intersection = new Vector4f(s).add(new Vector4f(v).mul(t));
        // find intersection in obj coordinate system
        if (intersection.x >= .499f && intersection.x <= .501f) {
          normal.x = 1f;
        } else if (intersection.x <= -.499f && intersection.x >= -.501f) {
          normal.x = -1f;
        }
        if (intersection.y >= .499f && intersection.y <= .501f) {
          normal.y = 1f;
        } else if (intersection.y <= -.499f && intersection.y >= -.501f) {
          normal.y = -1f;
        }
        if (intersection.z >= .499f && intersection.z <= .501f) {
          normal.z = 1f;
        } else if (intersection.z <= -.499f && intersection.z >= -.501f) {
          normal.z = -1f;
        }
        Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
        invTranspose.transform(normal);
        // set refraction
        hit.setNormal(normal.x, normal.y, normal.z);

        // set texture
        // the origin of the texture coordinates is at lower left corner
        // so that the texture matches openGL
        TextureImage image = this.textures.get(textureName);
        if (!textureName.equals("") && image != null) {
          hit.setTextureImage(image);
          float textureX = 0, textureY = 0;
          if (intersection.x >= .499f && intersection.x <= .501f) { // right
            textureX = .5f + intersection.z;
            textureY = .5f + intersection.y;
          } else if (intersection.x <= -.499f && intersection.x >= -.501f) { // left
            textureX = .5f - intersection.z;
            textureY = .5f + intersection.y;
          } else if (intersection.y >= .499f && intersection.y <= .501f) { // top
            textureX = .5f + intersection.x;
            textureY = .5f - intersection.z;
          } else if (intersection.y <= -.499f && intersection.y >= -.501f) { // bottom
            textureX = .5f + intersection.x;
            textureY = .5f + intersection.z;
          } else if (intersection.z >= .499f && intersection.z <= .501f) { // front
            textureX = .5f + intersection.x;
            textureY = .5f + intersection.y;
          } else if (intersection.z <= -.499f && intersection.z >= -.501f) { // back
            textureX = .5f - intersection.x;
            textureY = .5f + intersection.y;
          }
          hit.setTextureCoordinate(textureX, textureY);
        }
        result.add(hit);

      }

    }
    this.hitSetFraction(result, vector);
    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a sphere.
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitSphere(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = new ArrayList<>();
    Matrix4f invertedMV = new Matrix4f();
    modelView.invert(invertedMV);
    Vector4f s = new Vector4f();
    Vector4f v = new Vector4f();
    invertedMV.transform(start, s);
    invertedMV.transform(vector, v);

    float a = v.x * v.x + v.y * v.y + v.z * v.z;
    float b = 2f * (s.x * v.x + s.y * v.y + s.z * v.z);
    float c = s.x * s.x + s.y * s.y + s.z * s.z - 1;

    float delta = b * b - 4 * a * c;
    if (delta >= -0.001f) {
      float t1 = (-b + (float) Math.sqrt(delta)) / (2 * a);
      float t2 = (-b - (float) Math.sqrt(delta)) / (2 * a);
      List<Float> tlist = new ArrayList<>();
      tlist.add(t1);
      tlist.add(t2);
      for (Float t : tlist) {
        // hit point
        HitRecord hit = new HitRecord();

        hit.setT(t);

        // set material
        hit.setMaterial(mat);

        // set intersection in View
        Vector4f intersectionInView = new Vector4f(start).add(new Vector4f(vector).mul(t));
        hit.setIntersection(intersectionInView);

        // compute normal vector in view coordinate
        // intersection in obj coordinate system
        Vector4f intersection = new Vector4f(s).add(new Vector4f(v).mul(t));
        // normal vector in obj coordinate system
        Vector4f normal = new Vector4f(intersection);
        normal.w = 0;
        Matrix4f invTranspose = new Matrix4f(modelView).transpose().invert();
        invTranspose.transform(normal);

        // set normal vector
        hit.setNormal(normal.x, normal.y, normal.z);

        // set texture
        // the origin of the texture coordinates is at lower left corner
        // so that the texture matches openGL
        TextureImage image = this.textures.get(textureName);
        if (!textureName.equals("") && image != null) {
          hit.setTextureImage(image);
          float phi = (float) Math.asin(-intersection.y);
          float theta = (float) Math.atan2(intersection.z, -intersection.x);
          float imageT = phi / (float) Math.PI + .5f;
          float imageS = theta / (2 * (float) Math.PI) + .5f;
          hit.setTextureCoordinate(imageS, imageT);
        }
        // add to list
        result.add(hit);
      }


    }
    // set refraction
    hitSetFraction(result, vector);
    return result;
  }

  /**
   * This is a helper to check how a specified ray hit a sphere, but the normal is facing inside.
   *
   * @param start the start point of the ray
   * @param vector the direction of the ray
   * @param modelView the modelView transformation from world to view coordinates
   * @param mat the material of the object that is to be checked
   * @param textureName the name of the texture of the object that is to be checked
   * @return a list of all hit records of the ray on the object
   */
  private List<HitRecord> checkHitSphereInside(Vector4f start, Vector4f vector, Matrix4f modelView,
      Material mat, String textureName) {
    List<HitRecord> result = checkHitSphere(start, vector, modelView, mat, textureName);
    for (HitRecord hit : result) {
      Vector4f normal = hit.getNormal();
      normal.mul(-1);
      hit.setNormal(normal);
      Vector4f intersect = hit.getIntersection();
      intersect = intersect.add(normal.mul(0.1f));
      hit.setIntersection(intersect);
    }
    return result;
  }

  /**
   * Set refraction on hit records passed in.
   *
   * @param records a list of hit records that is to be checked
   * @param rayVec the direction of the ray
   */
  private void hitSetFraction(List<HitRecord> records, Vector4f rayVec) {
    // set refraction
    for (HitRecord hit : records) {
      Material mat = hit.getMaterial();
      Vector4f normal = new Vector4f(hit.getNormal()).normalize();
      Vector4f vec = new Vector4f(rayVec).normalize();
      float cosIn = vec.x * normal.x + vec.y * normal.y + vec.z * normal.z;
      if (cosIn >= 0.001) {
        hit.setFromRefraction(mat.getRefractiveIndex());
        hit.setToRefraction(1f);
        hit.setFlipNormal(true);
      } else {
        hit.setFromRefraction(1f);
        hit.setToRefraction(mat.getRefractiveIndex());
      }
    }
  }
}
