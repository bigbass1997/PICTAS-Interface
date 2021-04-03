[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
#### This project has been reimplemented in Rust. The new project repo can be found here: [https://github.com/bigbass1997/pictas-interface-rs](https://github.com/bigbass1997/pictas-interface-rs)

### Description
PICTAS-Interface is the host interface software for the [PICTAS replay device](https://github.com/bigbass1997/PICTAS). This project is still an early prototype, code is likely to be messy and buggy. Others are allowed to write their own interface software if they wish; the PICTAS does _not_ require that this particular software is used for communication.

### Usage / Communication
The PICTAS currently uses a USB to TTL module to communicate with a host computer, however this will be replaced with another microcontroller similar to how some Arduino boards connect over USB. The general process looks like this: Load movie into this software. Program the movie's inputs onto the PICTAS device. Turn on and while holding the console's reset button, run the the start command (CLI) or press the start button (GUI).

The replay device contains a 16MB FLASH memory IC that stores the movie's inputs. It does _not_ need to be reprogrammed each time you wish to playback the TAS, only when changing/updating the run. Communication details will be found in the PICTAS's readme file.

#### Headless (CLI)
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

To generate project files for IDEA and Eclipse: `gradlew idea` and `gradlew eclipse` respectively. Your IDE may also have the ability to import Gradle projects directly.
