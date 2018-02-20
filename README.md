# Multi-Diff

### Introduction
Multi-Diff is an innovating utility project that aims to enhance the legacy `diff` utility with additional practical functionality and better algorithmic awareness.   
* **More intelligent algorithm**: The `BetterDiff` algorithm developed in this project knows better to handle edit distance calculation with awareness of **brackets matching**. For instance, we try to make `BetterDiff` output diff patch result of
```java
   for(int i=0; i<10; i++) {
       System.out.println("First line");
   }
 - for(int i=0; i<10; i++) {
 -     System.out.println("Second line");
 - }
   for(int i=0; i<10; i++) {
       System.out.println("Third line");
   }
```
After deleting the second block. The wildly used `diff` utility instead will output:
```java
   for(int i=0; i<10; i++) {
       System.out.println("First line");
   }
   for(int i=0; i<10; i++) {
 -     System.out.println("Second line");
 - }
 - for(int i=0; i<10; i++) {
       System.out.println("Third line");
   }
```
This result does not explain the correct bracket pairing. 
* **Multi-file Referencing**: as the *Multi* in the project name. We try to integrate out algorithm into a more niche domain to provide better functionality. Consider such a scenario: 

<img src="https://www.dropbox.com/s/pni6ojruwl4nn25/Screenshot%202018-02-19%2019.03.13.png?raw=1" width="300">

Each node stands for a single text file. And there are inheritance relationships in between files, in that some files result from direct modification from its parent. This file itself can derive more files futher. Within a folder containing such files, our **objective** is to find the most likely parent of this file. We then find in this most likely parent for each of the child file's paragraph. 

This is a feature suggested by our Professor. When you have to consantly modify some document, and also have to maintain all the slightly different but hierarchically versions, you occasionally forget the modification order and relation between different versions. Each version might serve a specific purpose or is oriented to a certain client, amongst possibly thousands.   
Let's say you had client A with version 1 before, and B with 2, who had a certain relationship with A. Now suppose you again have to provide some version to person C, who you know has relation with A similar to that of B with A. Naturally you want to provide something similar to version 2 to C, but with proper modification like that made from 1 to 2. If you happen to forget the conceptual relationship you previously know by heart between A and B, that is, you have ver2, you know B, but you forgot about A, you just know there was somebody like that based on version of whom ver2 came from. Even though you have thousands of clients to deal with, having version 2 alone is enough for you to pinpoint version 1 and inspire you on how to modify ver1 to get ver3 to C.  
We analyzed this use case and provided a solution with visual interface. 

Before we explain how, open `run` folder and open `3.txt.html` in your browser and you will see the result. 

<img src="https://www.dropbox.com/s/6fv86tys7cfcubk/Screenshot%202018-02-19%2019.26.35.png?raw=1" width="600">

The left side will be displaying the text file of the most likely predecessor. The right side will be displaying the child file whose parent you want to find. Click on any paragraph on the right, you will see one highlighted paragraph on each side indicating a parent-child edge in the version tree. Detailed diff information calculated with the `BetterDiff` algorithm will be displayed within the two linked paragraphs.  

Please read the very detailed [**project write up**](write-up/writeup.pdf) for more information. Certain simplifying assumptions are made to achieve better result in limitted domain.

### Compile and Run
Look inside the `run` folder, we included `MDiff.jar` which you can use on any series of files within the same folder:
```bash
$ java -jar MDiff.jar 1.txt 2.txt 3.txt
$ java -jar MDiff.jar --html 1.txt 2.txt 3.txt
```
The first line will produce diff-patch format diff information calculated with `BetterDiff`. The second line will produce HTML files that provide visual functionality as discussed above. Per the specification in the [**project write up**](write-up/writeup.pdf), please make sure files in the command line arguments corresponds to files of increasing modification timestamp. That is, make sure that `3.txt` is a version after `2.txt`, and `2.txt` before `1.txt`.  

You have seen above what the HTML files will work like after running the second line above. The first line will produce `mpatch` files in the standard format:
```diff
= [filename]:[line number]
  Unchanged paragraph
---

< [filename]:[line number]
  Old content
  ---
> [current line number]
  New content
  +++
---
```

In case you want to compile for yourself, go into the source code folder `src`:
```bash
$ mvn clean compile assembly:single
```
