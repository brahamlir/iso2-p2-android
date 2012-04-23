DOCFILES := presupuesto.tex

.PHONY: all img gdd.pdf

all:  img gdd.pdf

img:
	python2.7 images.py

gdd.pdf: memoria.tex $(DOCFILES)
	pdflatex -d $<
	pdflatex -d $<

clean:
	$(RM) gdd.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop img/*-generated.pdf *~
