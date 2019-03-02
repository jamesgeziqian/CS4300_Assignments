
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.*;

/**
 * Created by ashesh on 9/18/2015.
 */

/**
 * This class represents a JOGLFrame, which is a JFrame, that can be seen on screen. This class is
 * also the first layer to connect between the GPU and our data.
 */
public class JOGLFrame extends JFrame {

  private View view;
  private GLCanvas canvas;

  public JOGLFrame(String title) {
    //routine JFrame setting stuff
    super(title);
    setSize(400, 400); //this opens a 400x400 window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //when X is pressed, close program

    //Our View class is the actual driver of the OpenGL stuff
    view = new View();

    GLProfile glp = GLProfile.getMaxProgrammable(true);
    GLCapabilities caps = new GLCapabilities(glp);

    canvas = new GLCanvas(caps);

    add(canvas);

    canvas.addGLEventListener(new GLEventListener() {

      /**
       * This method initializes the canvas space.
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void init(
          GLAutoDrawable glAutoDrawable) { //called the first time this canvas is created. Do your initialization here
        try {
          view.init(glAutoDrawable);

          glAutoDrawable.getGL().setSwapInterval(0);

        } catch (Exception e) {
          JOptionPane.showMessageDialog(JOGLFrame.this, e.getMessage(), "Error while loading",
              JOptionPane.ERROR_MESSAGE);
        }
      }

      /**
       * This method will be called when the canvas is destroyed.
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void dispose(GLAutoDrawable glAutoDrawable) {
        view.dispose(glAutoDrawable);
      }

      /**
       * This method will be called every time this window must be redrawn.
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void display(GLAutoDrawable glAutoDrawable) {
        view.draw(glAutoDrawable);
      }

      /**
       * This method will be called every time this canvas is resized.
       * @param glAutoDrawable the canvas space that we will be drawing on
       * @param x
       * @param y
       * @param width the width of window
       * @param height the height of window
       */
      @Override
      public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        view.reshape(glAutoDrawable, x, y, width, height);
        repaint(); //refresh window
      }
    });

    //Add an animator to the canvas
    AnimatorBase animator = new FPSAnimator(canvas, 300);
    animator.setUpdateFPSFrames(60, null);
    animator.start();
  }
}
