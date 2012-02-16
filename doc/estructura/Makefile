all:  main.pdf


main.pdf: main.tex
	pdflatex -d $<
	pdflatex -d $<
	mv main.pdf estructura.pdf

clean:
	rubber -clean
	$(RM)  main.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop *~