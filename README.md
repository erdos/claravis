# claravis

A Leiningen plugin for visualizing Clara rule data flow.

## Usage

1. Clone this repo somewhere `git clone https://github.com/erdos/claravis`
2. Go to repo directory and install mvn dependency: `$ lein install`
3. Add the following to the `:plugins` section of your `project.clj` file: `[claravis "0.1.0-SNAPSHOT"]`
4. In the project folder execute the command: `$ lein claravis my.namespace.core output.png`
5. See the generated graph in the `output.png` file.


## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
