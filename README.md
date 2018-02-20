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

<img src="https://www.dropbox.com/s/pni6ojruwl4nn25/Screenshot%202018-02-19%2019.03.13.png?raw=1" width="500">



