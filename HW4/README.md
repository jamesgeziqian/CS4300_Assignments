# CS4300 HW4

## Set Config Files to Run the Code

  All the config files are under `src/main/resources/configs`

  For example, just copy `src/main/resources/configs/camera.config` to configuration and the code is going to run.

  `camera.config` is displaying the building. The main window is showing the global view and the small top right window is showing the drone camera's view.

  Similarily, other 3 files work as well, including `tower_global.config`, `tower_drone.config` and `YMCA_global.config`.

## Implemented Functionality
  All requirements in Assignment 4 is accomplished.

  All "w", "a", "s", "d", up, down, right, left is implemented to control the position and posture of the camera. "f" and "c" titles the camera. Pressing space exchanges the main window and the top right window. Trackball functionality is kept. Only when the main window is in global view, the trackball is enabled.
