DOCFILES := presupuesto.tex

.PHONY: all img memoria.pdf

all:  img memoria.pdf

img:
	python2.7 images.py

memoria.pdf: memoria.tex $(DOCFILES)
	pdflatex -d $<
	pdflatex -d $<

clean:
	$(RM) memoria.pdf *.aux *.log *.out *.eps *.toc *.lof *.lop img/*-generated.pdf *~
