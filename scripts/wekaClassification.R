# R-Script for the evaluation of text classification for Facebook posts.
# The classification task is done by the use of the machine learning library WEKA.
# 
# Note that you have to install WEKA from this website first: 
# https://www.cs.waikato.ac.nz/~ml/weka/
#
# For further instructions, please look at the documentation for the RWeka package:
# https://cran.r-project.org/web/packages/RWeka/index.html
# 
# sebastian.drost@hs-bochum.de

rm(list = ls())

if (!require("data.table")) {
  install.packages("data.table")
}
if (!require("openNLP")) {
  install.packages("openNLP")
}
if (!require("NLP")) {
  install.packages("NLP")
}
if (!require("tm")) {
  install.packages("tm")
}
if (!require("RWeka")) {
  install.packages("RWeka")
}
if (!require("rJava")) {
  install.packages("rJava")
}

library(rJava)
.jinit(parameters="-Xmx6g")

library("data.table")
library("openNLP")
library("NLP")
library("tm")
library("RWeka")

# set working directory
setwd(choose.dir())

# source for r-script with functions for corpora prcoessing
source("./scripts/corporaProcessing.R")

# load the labeled training data from csv
trainingData <-
  read.csv2(
    "./training/trainingData.csv",
    comment.char = "",
    stringsAsFactors = F
  )

# create a VectorSource for the corpus constructor
vecSource <- VectorSource(trainingData$content)

# create a volatile corpora object
textCorpus <-
  VCorpus(vecSource, readerControl = list(language = "de"))

# do several filter steps, stopword removal and stemming
# to reduce the number of text features and make the text corpora handy
trainingCorpora <- filterPipeStopwordsStemCorpora(textCorpus)

# create a term-document-matrix with binary weights
dtmBin <-
  DocumentTermMatrix(trainingCorpora, control = list(weighting = weightBin))

# create a term-document-matrix with term frequency weights
dtmTf <- DocumentTermMatrix(trainingCorpora)


# create a term-document-matrix with term frequency-inverse document frequency weights
dtmTfIdf <- DocumentTermMatrix(trainingCorpora,
                                    control = list(
                                      weighting =
                                        function(x)
                                          weightTfIdf(x, normalize = F)
                                    ))

# preparate a training matrix with the manually anottated labels and the
# text features from the document-term-matrix
trainingLabels <- as.factor(trainingData$label)
trainingFrame <- as.data.frame(as.matrix(dtmBin))
trainingSet <- cbind(labels = trainingLabels, trainingFrame)

#create a SVM classifier from WEKA
SMO <- make_Weka_classifier("weka/classifiers/functions/SMO")

# create a SVM model with a linear Poly Kernel
smoModel <-
  SMO(labels ~ ., trainingSet, control = Weka_control(C=1,
    K = list("weka.classifiers.functions.supportVector.PolyKernel", E = 1)
  ))

#evaluate the model by a 10-fold-cross-validation
evaluation <-
  evaluate_Weka_classifier(
    smoModel,
    numFolds = 10,
    seed = 42,
    class = T
  )

print(evaluation)

rm(list = ls())
