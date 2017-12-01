---
title: Troubleshooting
permalink: /docs/troubleshooting/
---
## Executing MiniBrass

When trying to run some constraint model, I get the following error:
```
search miniBrass();
^^^^^^
Error: syntax error
```
This error is due to executing the model with Mini*Zinc* instead of Mini*Search*.
You can get MiniSearch [here](http://www.minizinc.org/minisearch/) and need to compile it from sources. Binary releases for Ubuntu and Windows (64 bit) are available on personal request. 

While some MiniBrass models run in pure MiniZinc (see the [quickest start]({{site.baseurl}}//docs/installation/) example, for instance) most of them rely on MiniSearch to show their 
advantage over conventional numeric objectives - so it pays off to give it a try!
 
## MiniSearch issues

### Compiling MiniSearch
During compilation of MiniSearch, I get the following error:
``` 
"The file lexer.lxx has been modified but flex cannot be run."
"If you are sure ${PROJECT_SOURCE_DIR}/lib/cached/lexer.yy.cpp is correct then"
"copy lexer.lxx's md5 md5 ${lexer_lxx_md5} into ${PROJECT_SOURCE_DIR}/lib/cached/md5_cached.cmake") 
```
**Solution** This error is due to *flex* and *bison* not being installed on your computer. For example, 
on Windows-based systems you can get it from `https://sourceforge.net/projects/winflexbison/` 
(be sure to rename `win_flex.exe` to `flex.exe`).  

If you encounter MiniBrass bugs, please add an issue at <https://github.com/isse-augsburg/minibrass/issues>.

For other problems, please contact the MiniBrass developers at <minibrass@isse.de>.
