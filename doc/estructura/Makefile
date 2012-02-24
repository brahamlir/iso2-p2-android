all:  main.pdf


main.pdf: main.tex
	pdflatex -d $<
	pdflatex -d $<
	$(RM) -f estructura.pdf
	mv main.pdf estructura.pdf

clean:
	rubber -clean
	$(RM)  estructura.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop *~
