# Yaml converter
This package provides the ability to convert a state machine definition
into (or from) a yaml file.

Now internally this is a litter different that what I would normally do.
Since the state machine definition is comprised of value objects, that would
be totally ugly in yaml form. So this converts that definition into an
internal type that is more suited to yaml. On reading of the yaml file,
it uses a builder to convert back into the definition. Which means it uses
the default error checking on read.

Hipster converter, right?
