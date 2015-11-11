\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{notes}[2013/10/04 v0.3 Notes class]

\DeclareOption*{%
  \PassOptionsToClass{\CurrentOption}{amsart}%
}
\ProcessOptions\relax
\PassOptionsToPackage{fleqn}{amsmath}%
\LoadClass[a4paper,12pt,fleqn,leqno,tbtags]{amsart}
\def\maketitle{\par
  \@topnum\z@ % this prevents figures from falling at the top of page 1
  \@setcopyright
  \thispagestyle{firstpage}% this sets first page specifications
  \uppercasenonmath\shorttitle
  \ifx\@empty\shortauthors \let\shortauthors\shorttitle
  \else \andify\shortauthors
  \fi
  \@maketitle@hook
  \begingroup
  \@maketitle
  \toks@\@xp{\shortauthors}\@temptokena\@xp{\shorttitle}%
  \toks4{\def\\{ \ignorespaces}}% defend against questionable usage
%  \edef\@tempa{%
%    \@nx\markboth{\the\toks4
%      \@nx\MakeUppercase{\the\toks@}}{\the\@temptokena}}%
% AK: There are problems with using amsart's \markboth with babel
% http://www.tug.org/pipermail/macostex-archives/2004-December/011991.html
% and http://www.latex-project.org/cgi-bin/ltxbugs2html?pr=babel/3238
% Perhaps my amsart.cls (2.20) and babel (3.8d) installation are too old.
% Changed definition to using \markleft and \markright.
  \edef\@tempa{%
    \@nx\markleft{\the\toks4 \@nx\MakeUppercase{\the\toks@}}
    \@nx\markright{\the\@temptokena}}%
  \@tempa
  \endgroup
  \c@footnote\z@
  \@cleartopmattertags
}
% Redefine section, subsection, and paragraph
\def\section{\@startsection{section}{1}%
  \z@{1.5\linespacing\@plus.5\linespacing}{.5\linespacing}%
  {\normalfont\scshape\centering}}
\def\subsection{\@startsection{subsection}{2}%
  \z@{1\linespacing\@plus.3\linespacing}{.5\linespacing}%
  {\normalfont\itshape}}
\let\c@paragraph\relax
\newcounter{paragraph}
\makeatother%
\setcounter{secnumdepth}{3}
\setcounter{tocdepth}{2}
\renewcommand{\theparagraph}{\arabic{paragraph}}
\makeatletter%
\def\l@paragraph{\@tocline{4}{0pt}{1pc}{7pc}{}}
\renewcommand{\paragraph}[1][\@empty]{\refstepcounter{paragraph}%
\@startsection{paragraph}{4}{\z@}%
%{3.25ex \@plus.75ex \@minus.25ex}%
{\linespacing\@plus.2\linespacing\@minus.2\linespacing}
{-1em}%
{\normalfont\normalsize\bfseries}%
{\theparagraph%
\def\tmpex{Example}\def\tmpp{#1}%
\ifx\tmpp\@empty\else.\hskip1ex%
\ifx\tmpp\tmpex\normalfont\textsc{Example}\else\normalfont\bfseries #1\fi\fi}}
\renewenvironment{quotation}{\list{}{%
    \leftmargin\mathindent \listparindent\normalparindent
    \itemindent\z@
    \rightmargin\leftmargin \parsep\z@ \@plus\p@}%
  \item[]%
}{%
  \endlist
}
% Redefine line of table of contents
\def\@tocline#1#2#3#4#5#6#7{\relax
  \ifnum #1>\c@tocdepth % then omit
  \else
    \par \addpenalty\@secpenalty\addvspace{#2}%
    \begingroup \hyphenpenalty\@M
    \@ifempty{#4}{%
      \@tempdima\csname r@tocindent\number#1\endcsname\relax
    }{%
      \@tempdima#4\relax
    }%
    \parindent\z@ \leftskip#3\relax \advance\leftskip\@tempdima\relax
    %\rightskip\@pnumwidth plus4em \parfillskip-\@pnumwidth
    \rightskip\@pnumwidth plus4em \parfillskip-\@pnumwidth \advance\parfillskip#3\relax
    #5\leavevmode\hskip-\@tempdima #6\nobreak\relax
    \leaders\hbox to 1em{\hss.\hss}\hfil\hbox to\@pnumwidth{\@tocpagenum{#7}}\par
    %\hfil\hbox to\@pnumwidth{\@tocpagenum{#7}}\par
    \nobreak
    \endgroup
  \fi}

%\RequirePackage[fleqn]{amsmath}
\RequirePackage{amssymb,amsthm}
% Redefine proof
%\renewenvironment{proof}[1][\proofname]{\par
%  \pushQED{\qed}%
%  \normalfont\topsep\abovedisplayskip% \topsep6\p@\@plus6\p@\relax
%  \trivlist
%  \item[\hskip\labelsep
%        \itshape
%    #1\@addpunct{.}]\ignorespaces
%}{%
%  \popQED\endtrivlist\@endpefalse
%}
%\newtheoremstyle{def}{\abovedisplayskip}{\belowdisplayskip}{\upshape}{}{\bfseries}{.}{ }{}
%\theoremstyle{def}
%\newtheorem{definition}{Definition}
%\newtheorem{lemma}[definition]{Lemma}
%\newtheorem{proposition}[definition]{Satz}
%\newtheorem{corollary}{Korollar}
\RequirePackage{latexsym,stmaryrd}
\let\url\@undefined
\RequirePackage{url}

% Formatting
% \advance\topmargin by -1.2cm
% %\advance\textheight by 1cm
% \textheight647pt
% \advance\evensidemargin by -1.1cm
% \advance\oddsidemargin by -1.1cm
% \advance\textwidth by 2.2cm
\RequirePackage[a4paper,centering,scale=.8]{geometry}
\parindent0pt
\RequirePackage{times}
\RequirePackage[scaled=.9]{helvet}
\RequirePackage{microtype}
%\newif\ifdoublespaced\doublespacedtrue
\newif\ifdoublespaced\doublespacedfalse
\ifdoublespaced\renewcommand{\baselinestretch}{1.5}\else\fi
\renewcommand{\topfraction}{0.99}
\renewcommand{\bottomfraction}{0.99}
\renewcommand{\textfraction}{0.01}
\setlength\textfloatsep{6mm plus 4pt minus 4pt}
\allowdisplaybreaks

\usepackage{babel}
\useshorthands{"}
\defineshorthand{"=}{-\allowhyphens}


\RequirePackage{enumitem}
%\RequirePackage{cleveref}

% Theorem style
\theoremstyle{plain}
\newtheorem*{theorem}{\normalfont\textsc{Theorem}}
\newtheorem*{proposition}{\normalfont\textsc{Proposition}}
\newtheorem*{corollary}{\normalfont\textsc{Corollary}}
\newtheorem*{lemma}{\normalfont\textsc{Lemma}}
\theoremstyle{definition}
\newtheorem*{definition}{\normalfont\textsc{Definition}}
\newtheorem*{example}{\normalfont\textsc{Example}}

\AtBeginDocument{\leftmargini20pt\mathindent28pt}
