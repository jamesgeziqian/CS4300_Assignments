import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.event.*;


public class JOGLFrame extends JFrame {

  private View view;
  private GLCanvas canvas;
  private int mouseX, mouseY;

  public JOGLFrame(String title) {
    //routine JFrame setting stuff
    super(title);
    setSize(800, 800); //this opens a 400x400 window
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //when X is pressed, close program

    //Our View class is the actual driver of the OpenGL stuff
    view = new View();

    GLProfile glp = GLProfile.getGL2GL3();
    GLCapabilities caps = new GLCapabilities(glp);
    canvas = new GLCanvas(caps);

    add(canvas);

    EventListener listener = new EventListener();
    canvas.addGLEventListener(listener);

    canvas.addMouseListener(listener);
    canvas.addMouseMotionListener(listener);
    canvas.addMouseWheelListener(listener);
    canvas.addKeyListener(listener);
    canvas.setFocusable(true);
    canvas.requestFocus();

    //Add an animator to the canvas
    AnimatorBase animator = new FPSAnimator(canvas, 300);
    animator.setUpdateFPSFrames(100, null);
    animator.start();
  }

  class EventListener extends MouseAdapter implements GLEventListener, KeyListener {

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

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { //called when the canvas is destroyed.
      view.dispose(glAutoDrawable);
    }

    @Override
    public void display(
        GLAutoDrawable glAutoDrawable) { //called every time this window must be redrawn

      view.draw(glAutoDrawable);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width,
        int height) { //called every time this canvas is resized
      view.reshape(glAutoDrawable, x, y, width, height);
      repaint(); //refresh window
    }


    @Override
    public void mousePressed(MouseEvent e) {
      mouseX = e.getX();
      mouseY = canvas.getHeight() - e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      view.setRotationMatrix(e.getX() - mouseX, canvas.getHeight() - e.getY() - mouseY);
      mouseX = e.getX();
      mouseY = canvas.getHeight() - e.getY();
      canvas.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

  }


}
