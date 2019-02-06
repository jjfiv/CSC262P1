# CSC262P1
Smith College / CSC262 Operating Systems / Cooperative Multitasking Shell in Java

## Getting started

Fork this repo so you can push changes as you work (and have offsite backup if your computer has a bad day).

### Import to IntelliJ or Eclipse

[Detailed Eclipse Instructions for importing maven/git projects](https://github.com/jjfiv/GuessingGame#how-to-import-this-project-into-eclipse-10), courtesy of teaching CSC212.

IntelliJ is easier, I can help.

### Build & Run from the Terminal

    # git clone the code (earlier)
    cd CSC262P1
    # edit the code
    # build
    mvn package
    # run
    java -jar target/CoopShell-1.0-SNAPSHOT.jar

## Learning Goals

Learn a little bit about how the UNIX shell works from an inside perspective, while looking at a form of scheduling that is not based on interrupts and timeslices.

## Extra Credit in CSC262

Extra credit on assignments is applied first to the assignment, if other sections are incomplete, and then are counted toward your participation grade. Going further with an assignment will give you insights that will aid your participation and understanding of the course material.

## Required Tasks

These tasks are required parts of the assignment for full credit.

### Extend some UNIX tools

- WordCount only counts words, not lines or bytes.

### Implement Some UNIX-style Tools (only piping)

You only need to implement the arguments specified in the following examples. The real-world programs are far more flexible, and you can do those things for a challenge, but my goal is to see the simplest possible example of each tool implemented as a Task subclass.

- Echo: print the arguments to the output.

```
$ echo "A B C"
o: A B C
```

- SetVar: takes two arguments: an variable name and a value
```
$ set A "Hello World"
$ echo $A
o: Hello World
```
- ListFiles: print all the files in the current directory.
```
$ ls
o: pom.xml
o: README.md
o: src
o: target
```
- SimpleGrep: find lines with an argument string on them.
```
$ ls | grep pom
o: pom.xml
```
- RegexGrep: find lines with a java.util.Regex argument string on them.
```
$ ls | rgrep "^p"
o: pom.xml
```
- Sort: sort lines that are observed and print them in order.
```
$ echo "B\nA\nC" | sort
o: A
o: B
o: C
```
- Head: display only the first $arg lines
```
$ cat alphabet.txt | head 3
o: A
o: B
o: C
```
- Tail: display only the last $arg lines
```
$ cat alphabet.txt | tail 3
o: X
o: Y
o: Z
```

## Extra Credit Ideas

### Improve ``cd`` to support tilde home expansion

- I handled absolute paths in ``ShellEnvironment``; can you add support for ``~/Downloads``?

### The shell as a Task itself.

- Try to make a Shell Task.
- It needs a ConsoleReader like the ConsoleWriter. (you'll probably need to modify Task somehow).
- When it "forks" a job, it must deal with providing it's update timeslice to its children.
- Want to have a ShellEnvironment.clone() for forking.
- Can you shell inside a shell Task?

### I'm interested in Lexing/Parsing

ShellLexer is more advanced than ShellParser. It can identify semicolons and file redirections (>) but we don't do anything with them.
- Put JavaDoc comments throughout Token, TokenType, ShellLexer, etc.
- Create a FileRedirect Task
- Support many statements on one line.

### A "more-real" Shell

- Use ProcessBuilder to allow calls to external programs.
- Use the PATH variable to help find external programs.
