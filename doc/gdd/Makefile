DOCFILES := presupuesto.tex

.PHONY: all gdd.pdf

all:  gdd.pdf

gdd.pdf: gdd.tex $(DOCFILES)
	pdflatex -d $<
	pdflatex -d $<

clean:
	rubber -clean
	$(RM) gdd.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop *~
