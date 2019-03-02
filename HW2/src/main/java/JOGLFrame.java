import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * This class represents a JOGLFrame, which is a JFrame, that can be seen on screen. This class is
 * also the first layer to connect between the GPU and our data.
 */
public class JOGLFrame extends JFrame {

  private View view;
  private GLCanvas canvas;
  private int x, y;

  public JOGLFrame(String title) {
    //routine JFrame setting stuff
    super(title);
    setSize(400, 400); //this opens a 400x400 window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //when X is pressed, close program

    //Our View class is the actual driver of the OpenGL stuff
    view = new View();

    GLProfile glp = GLProfile.getGL2GL3();
    GLCapabilities caps = new GLCapabilities(glp);
    canvas = new GLCanvas(caps);

    add(canvas);

    canvas.addMouseListener(new MouseAdapter() {

      /**
       * This method is called each time mouse button is pressed on the image.
       * The position of mouse is stored as initial value of dragging.
       *
       * @param e the mouse event representing a mouse press
       */
      @Override
      public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        repaint();
      }
    });

    canvas.addMouseMotionListener(new MouseAdapter() {

      /**
       * This method is called each time the image is dragged by mouse, which
       * is going to result in a rotation of 3D objects showing on the screen.
       *
       * @param e the mouse event representing a mouse drag
       */
      @Override
      public void mouseDragged(MouseEvent e) {
        int x2 = e.getX();
        int y2 = e.getY();
        view.rotateOmega(x2 - x, y2 - y);
        x = x2;
        y = y2;
        repaint();
      }
    });

    canvas.addGLEventListener(new GLEventListener() {

      /**
       * This method initializes the canvas space.
       *
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void init(
          GLAutoDrawable glAutoDrawable) { //called the first time this canvas is created. Do your initialization here
        try {
          view.init(glAutoDrawable);
          glAutoDrawable.getGL().setSwapInterval(1);
        } catch (Exception e) {
          JOptionPane.showMessageDialog(JOGLFrame.this, e.getMessage(), "Error while loading",
              JOptionPane.ERROR_MESSAGE);
        }
      }

      /**
       * This method will be called when the canvas is destroyed.
       *
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void dispose(GLAutoDrawable glAutoDrawable) { //called when the canvas is destroyed.
        view.dispose(glAutoDrawable);
      }

      /**
       * This method will be called every time this window must be redrawn.
       *
       * @param glAutoDrawable the canvas space that we will be drawing on
       */
      @Override
      public void display(
          GLAutoDrawable glAutoDrawable) { //called every time this window must be redrawn
        view.draw(glAutoDrawable);
      }

      /**
       * This method will be called every time this canvas is resized.
       *
       * @param glAutoDrawable the canvas space that we will be drawing on
       * @param width the width of window
       * @param height the height of window
       */
      @Override
      public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width,
          int height) { //called every time this canvas is resized
        view.reshape(glAutoDrawable, x, y, width, height);
        repaint(); //refresh window
      }
    });

    //Add an animator to the canvas
    AnimatorBase animator = new FPSAnimator(canvas, 100);
    animator.setUpdateFPSFrames(100, null);
    animator.start();
  }
}
