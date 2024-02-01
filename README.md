# Compiler from scratch

I am writing a toy compiler for a C-like language for fun, learning, and exploration.
I am documenting this process [on my blog](https://llew.netlify.app/posts).

## Running the code

If you want to mess around with the code, you'll need an up-to-date Java and Maven installation.
The compiler comes with a basic CLI, which you can invoke like this:

```bash
mvn compile exec:java -Dexec.args="<arg1> <arg2> ... <argN>"
```

You can pass arguments in the string above, for example `--help` will list out all the commands you can use.

To run the tests, you can do `mvn test`.
