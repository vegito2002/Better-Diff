Our program MDiff takes a list of files as its input. It works in two mode, plain text patch file mode and HTML mode (enabled with `--html` flag). It generates a `.mpatch` or `.html` file for each file in the input. We process files by the their listed order, and each file are only compared with files before it, therefore no cycle is possible.

Example:
```
$ java -jar MDiff.jar 1.txt 2.txt 3.txt
$ java -jar MDiff.jar --html 1.txt 2.txt 3.txt
```

Compile:
```
mvn clean compile assembly:single
```

Mdiff file is similar to diff file as we indent the file with two spaces and put block header around unoriginal content. Character changes if applicable are annotated below a line.

```
= [filename]:[line number]
  Unchanged paragraph
---

< [filename]:[line number]
  Old content
  ---
> [new line number]
  New content
  +++
---
```

HTML version is a two column viewer where current file is displayed on the right. When you select a paragraph, its origin is displayed on the left, and changes from origin are annotated with color.

[Screenshot]
