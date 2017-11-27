---
title: Installation
permalink: /docs/installation/
---

## Quickest start (only requires MiniBrass and MiniZinc)
1. Download and install the newest MiniZinc IDE version from http://www.minizinc.org/
2. Download [mbr2mzn.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/java/mbr2mzn.zip) and unpack it to some directory (called `MBR_DIR` in the following)
3. Download [soft_constraints.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/minizinc/soft_constraints.zip) and unpack it to some directory (called `MBR_SOFT_DIR`)
4. Locate the directory "./share/minizinc/std" of your MiniZinc installation
5. Copy the directory "soft_constraints" (in `MBR_SOFT_DIR`) into "./share/minizinc/std"
6. Download and extract the code examples
7. Compile the preference model by using `java -jar MBR_DIR/mbr2mzn.jar -m smallexample_minizinc.mbr`
8. Run `minizinc smallexample_minizinc.mzn` : Your output should state

```
x = 1; y = 2; z = 1
Valuations:  overall = 1
----------
==========
```

## Slightly slower start (requires MiniBrass, MiniZinc, and MiniSearch): 

1. Build MiniSearch according to the instructions at MiniSearch
2. Download [mbr2mzn.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/java/mbr2mzn.zip) and unpack it to some directory (called `MBR_DIR` in the following)
3. Download [soft_constraints.zip](https://github.com/isse-augsburg/minibrass/raw/master/source-code/minizinc/soft_constraints.zip) and unpack it to some directory (called `MBR_SOFT_DIR`)
4. Locate the directory "./share/minizinc/std" of your MiniSearch installation
5. Copy the directory "soft_constraints" (in `MBR_SOFT_DIR`) into "./share/minizinc/std"
6. Download and extract the code examples
7. Compile the preference model by using `java -jar MBR_DIR/mbr2mzn.jar smallexample_minisearch.mbr`
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

## Follow-up set-up and available options

To get easier access to MiniBrass, add `MBR_DIR` to your path variable (e.g., in `.bashrc` on Linux). 

```
export PATH="$PATH:MBR_DIR"
```

By means of the included `mbr2mzn` script, you can compile MiniBrass models as:

```
mbr2mzn smallexample_minisearch.mbr
```
