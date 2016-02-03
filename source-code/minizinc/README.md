# MiniZinc Code of MiniBrass 

To see how all files work together, please refer to our [main page](http://isse-augsburg.github.io/constraint-relationships/).

### Installation
MiniBrass files are easily deployed by copying all relevant .mzn files into a *soft_constraints* folder. Have a look at `deploy.sh` to see which files are copied. 
To install, just execute
```
./deploy.sh 
```
on a Unix machine or translate the file to an adequate .bat file.
You then just have to add the *soft_constraints* folder to your MiniZinc standard library directory, i.e., `share/minizinc/std` of your MiniZinc/MiniSearch installation.  

### Project overview
* **bug-cases/** contains some buggy models
* **examples/** provides case studies of MiniBrass 
* **globals/** contains standard decompositions of soft global constraints
* **example-problems/** shows problems referred to in the documentation
* **tests/** offers some models that highlight aspects of the library (referred to in the slides)


