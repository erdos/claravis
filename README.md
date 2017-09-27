# claravis

A Leiningen plugin for visualizing Clara rule data flow.

[Clara Rule Engine](https://github.com/cerner/clara-rules) is a super cool project.
But sometimes the data flow can get messy due to the high number of rules and facts.
This project helps you visualize your data flow in order to catch bugs more efficiently.

## Usage

1. Clone this repo somewhere `git clone https://github.com/erdos/claravis`
2. Go to repo directory and install mvn dependency: `$ lein install`
3. Add the following to the `:plugins` section of your `project.clj` file: `[claravis "0.1.0-SNAPSHOT"]`
4. In the project folder execute the command: `$ lein claravis my.namespace.core output.png`
5. See the generated graph in the `output.png` file.

_Pro tip: you can add the plugin to your `~/.lein/profiles.clj` instead of `project.clj` if you wish to use it across multiple projects._

## Example

This is the generated example for the `clara.examples.shopping` namespace of
the [Clara Examples](https://github.com/cerner/clara-examples) repository.

<div style="text-align:center"><img src="https://github.com/erdos/claravis/blob/master/doc/shopping-example.png?raw=true" /></div>

The tabular data are the **facts** and the brown boxes are the **rules**.
You can see the queries at the bottom.

## License

Copyright © 2017 Janos Erdos

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
