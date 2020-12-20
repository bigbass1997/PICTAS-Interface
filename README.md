[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
### Description
PICTAS-Interface is the front-end interface for the [PICTAS replay device](https://github.com/bigbass1997/PICTAS). This project is still an early prototype, code is bound to be messy and bugs likely exist.

### Usage / Communication
The PICTAS currently uses a USB to TTL module to communicate with a host computer, however this will be replaced with another microcontroller similar to how some Arduino boards connect over USB. Once the core functionality is working, the process will be something akin to: Plug in the PICTAS to the host first, then run the interface software. Load the movie file and program the PICTAS. Tell the PICTAS to ready-up. Then turn on the console to begin the replay sequence.

The replay device contains a 16MB FLASH memory microchip that stores the entire list of inputs of a particular TAS. The chip does need to reprogrammed each time you wish to playback the TAS, only when changing/updating the run.

#### Headless
In situations where a graphics front-end is inconvenient or unavailable, this program can be run headless. To do this, you must include the `--headless` command-line argument when starting. (e.g. `java -jar pictas-interface.jar --headless`) Below are all command-line arguments that will be available:
<pre>
-i               input file path (not implemented yet)
-h, --help       displays this help text and exits (not implemented yet)
    --headless   starts program without front-end graphics
</pre> 

### Movie File Compatibility
.

### Building
Gradle is used as the dependency and build management software. Included in the repo is the Gradle Wrapper which allows you to run gradle commands from the root directory of the project. You can compile and run the program with `gradlew run`. To build the source into a runnable jar, execute `gradlew build`. The resulting jar will be located in `/build/libs/`.

To generate project files for IDEA and Eclipse: `gradlew idea` and `gradlew eclipse` respectively. Your IDE may also have the ability to import Gradle projects.
