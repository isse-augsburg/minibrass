---
title: Installation
permalink: /docs/installation/
---

## Quickest start (only requires MiniBrass and MiniZinc)
1. Download and install the newest MiniZinc IDE version from http://www.minizinc.org/
2. Download [mbr2mzn.jar](https://github.com/isse-augsburg/minibrass/raw/master/source-code/java/mbr2mzn.jar)
3. Download [soft_constraints.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/minizinc/soft_constraints.zip) and unpack it
4. Locate the directory "./share/minizinc/std" of your MiniZinc installation
5. Copy the directory "soft_constraints" into "./share/minizinc/std"
6. Download and extract the code examples
7. Compile the preference model by using `java -jar mbr2mzn.jar -m smallexample_minizinc.mbr`
8. Run `minizinc smallexample_minizinc.mzn` : Your output should state

```
x = 1; y = 2; z = 1
Valuations:  overall = 1
----------
==========
```

## Slightly slower start (requires MiniBrass, MiniZinc, and MiniSearch): 

1. Build MiniSearch according to the instructions at MiniSearch
2. Download [mbr2mzn.jar](https://github.com/isse-augsburg/minibrass/raw/master/source-code/java/mbr2mzn.jar)
3. Download [soft_constraints.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/minizinc/soft_constraints.zip) and unpack it
4. Locate the directory "./share/minizinc/std" of your MiniSearch installation
5. Copy the directory "soft_constraints" into "./share/minizinc/std"
6. Download and extract the code examples
7. Compile the preference model by using `java -jar mbr2mzn.jar smallexample_minisearch.mbr`
8. Run `minisearch smallexample_minisearch.mzn` : Your output should state

```
Intermediate solution:x = 1; y = 1; z = 1
Valuations:  overall = 1..2
----------
Intermediate solution:x = 1; y = 1; z = 3
Valuations:  overall = 1..1
----------
Intermediate solution:x = 1; y = 2; z = 1
Valuations:  overall = 2..2
----------
==========
```
