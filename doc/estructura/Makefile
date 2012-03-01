all:  main.pdf


main.pdf: main.tex
	pdflatex -d $<
	pdflatex -d $<
	$(RM) -f Shurdroid_Estructura.pdf
	mv main.pdf Shurdroid_Estructura.pdf

clean:
	$(RM) Shurdroid_Estructura.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop *~
