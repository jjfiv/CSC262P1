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
- ``cd``, the builtin does not support the user's home. How do we find the home in Java?

### Implement Some UNIX-style Tools

- Echo: print the arguments to the output.
- SetVar: takes two arguments: an variable name and a value
- ListFiles: print all the files in the current directory.
- SimpleGrep: find lines with an argument string on them.
- RegexGrep: find lines with a java.util.Regex argument string on them.
- Sort: sort lines that are observed and print them in order.
- Head: display only the first $arg lines
- Tail: display only the last $arg lines

## Extra Credit Ideas

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
