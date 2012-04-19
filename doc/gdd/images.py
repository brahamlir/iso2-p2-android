#!/usr/bin/python
# -*- encoding: utf-8 -*-

import os

if __name__ == "__main__":
  os.system("rm -f img/*-generated.pdf")

  for f in os.listdir("img/"):
    if f[-4:] == ".eps":
      os.system("epstopdf \"img/" + f + "\" --outfile=\"img/" + f[:-4] + "-generated.pdf\"")
