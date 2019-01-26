import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import util.*;

import java.nio.FloatBuffer;


/**
 * <p>
 * The View class is the "controller" of all our OpenGL stuff. It cleanly
 * encapsulates all our OpenGL functionality from the rest of Java GUI, managed
 * by the JOGLFrame class.
 */
public class View {
    private int WINDOW_WIDTH, WINDOW_HEIGHT;
    private Matrix4f proj;
    private SingleDigit[] digits;
    private TwoDots[] dots;
    private ShaderLocationsVault shaderLocations;


    private Vector4f color;

    ShaderProgram program;


    public View() {
        proj = new Matrix4f();
        proj.identity();
        digits = new SingleDigit[6];
        dots = new TwoDots[2];
        shaderLocations = null;
        WINDOW_WIDTH = WINDOW_HEIGHT = 0;
    }

    public void init(GLAutoDrawable gla) throws Exception {
        GL3 gl = (GL3) gla.getGL().getGL3();

        //compile and make our shader program. Look at the ShaderProgram class for details on how this is done
        program = new ShaderProgram();
        program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");

        shaderLocations = program.getAllShaderVariables(gl);

        // Create all the digits data
        int complement = 0; // This int help create intervals between numbers
        int starting = 55;
        int interval = 50;
        for (int i = 0; i < 6; ++i) {
            digits[i] = new SingleDigit(starting + interval * i + (i / 2)*(interval), 283,
                    gl, program, shaderLocations, "Digit" + i);
            complement++;
        }
        // Create all the dots.
        dots[0] = new TwoDots(175, 250, 30, gl, program, shaderLocations, "DotsGroup1");
        dots[1] = new TwoDots(325, 250, 30, gl, program, shaderLocations, "DotsGroup1");

    }


    public void draw(GLAutoDrawable gla) {
        GL3 gl = gla.getGL().getGL3();

        FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
        FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);

        // set color to green
        color = new Vector4f(0, 1, 0, 1);

        //set the background color to be black
        gl.glClearColor(0, 0, 0, 0);
        //clear the background
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);
        //enable the shader program
        program.enable(gl);

        //pass the projection matrix to the shader
        gl.glUniformMatrix4fv(
                shaderLocations.getLocation("projection"),
                1, false, proj.get(fb16));

        //send the color of the triangle
        gl.glUniform4fv(
                shaderLocations.getLocation("vColor")
                , 1, color.get(fb4));

        //draw the objects
        digits[0].draw(gla, Utility.getHour() / 10);
        digits[1].draw(gla, Utility.getHour() % 10);
        digits[2].draw(gla, Utility.getMin() / 10);
        digits[3].draw(gla, Utility.getMin() % 10);
        digits[4].draw(gla, Utility.getSec() / 10);
        digits[5].draw(gla, Utility.getSec() % 10);
        for (TwoDots dot : dots) {
            dot.draw(gla);
        }

        gl.glFlush();
        //disable the program

        program.disable(gl);
    }

    //this method is called from the JOGLFrame class, everytime the window resizes
    public void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
        GL gl = gla.getGL();
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
        gl.glViewport(0, 0, width, height);

        proj = new Matrix4f().ortho2D(0, 500,
                0, 500 * (float) height / width);

    }

    public void dispose(GLAutoDrawable gla) {
        for (TwoDots dot : dots) {
            dot.cleanup(gla);
        }
        for (SingleDigit digit : digits) {
            digit.cleanup(gla);
        }
    }
}
