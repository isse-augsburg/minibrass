---
title: Workflow
permalink: /docs/workflow/
---
## Running MiniBrass models
The MiniBrass workflow consists of two steps:
1. Compiling a MiniBrass preference model to MiniZinc (`mbr2mzn`)
2. Executing the MiniZinc constraint model which includes the compiled output from step 1

The latter step can either be done using `minizinc`/`minisearch` or via the toolchain of `mzn2fzn`, `fzn-solver`, and `solns2out`.

![The MiniBrass workflow]({{site.baseurl}}/img/workflow.png "The MiniBrass workflow")

Blue refers to artifacts provided by the user. Orange denotes generated or library artifcats. 
