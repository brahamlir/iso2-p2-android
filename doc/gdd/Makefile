all:  main.pdf

gdd.pdf: gdd.tex
	pdflatex -d $<
	pdflatex -d $<

clean:
	rubber -clean
	$(RM) gdd.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop *~
