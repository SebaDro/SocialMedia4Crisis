# R-Script for text classification with Weka

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
if (!require("koRpus")) {
  install.packages("koRpus")
}
if (!require("RTextTools")) {
  install.packages("RTextTools")
}
if (!require("tm")) {
  install.packages("tm")
}
if (!require("SnowballC")) {
  install.packages("SnowballC")
}
if (!require("RWeka")) {
  install.packages("RWeka")
}
if (!require("gmodels")) {
  install.packages("gmodels")
}
if (!require("text2vec")) {
  install.packages("text2vec")
}
if (!require("caret")) {
  install.packages("caret")
}
if (!require("FSelector")) {
  install.packages("FSelector")
}

# install openNLP model for german
# Tiger Corpus and STTS Tag Set
# install.packages("openNLPmodels.de",
#                  repos = "http://datacube.wu.ac.at/",
#                  type = "source")
#expand heap space for large query results
options(java.parameters = "-Xmx6g")

library("data.table")
library("openNLP")
library("NLP")
library("koRpus")
library("RTextTools")
library("tm")
library("SnowballC")
library("RWeka")
library("gmodels")
library("text2vec")
library("caret")
library("FSelector")


# set working directory
setwd(choose.dir())

# source for r-script with functions for corpora prcoessing
source("./scripts/corporaProcessing.R", encoding = "utf-8")

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

# do several filter steps to reduce the number of features
# and make the text corpora handy
trainingCorpFilter <- filterPipeStemCorpora(textCorpus)

# term unigram features and term frequency weighting
dtm_uni_Tf <- DocumentTermMatrix(trainingCorpFilter)
# term unigram features and term frequency-inverse document frequency
dtm_uni_TfIdf <- DocumentTermMatrix(trainingCorpFilter,
                                    control = list(
                                      weighting =
                                        function(x)
                                          weightTfIdf(x, normalize = F)
                                    ))
# term unigram features and binary weighting
dtm_uni_bin <- DocumentTermMatrix(trainingCorpFilter, control = list(weighting = weightBin))

trainingLabels <- as.factor(trainingData$label)
trainingFrame <- as.data.frame(as.matrix(dtm_uni_TfIdf))
trainingSet <- cbind(labels = trainingLabels, trainingFrame)

# Multinomial Naive Bayes
NBMulti <- make_Weka_classifier("weka/classifiers/bayes/NaiveBayesMultinomial")
nbMultimodel <- NBMulti(labels ~ ., trainingSet[1:500,])
evaluation <- evaluate_Weka_classifier(nbMultimodel, numFolds = 10, seed=42, class = T)


NB <- make_Weka_classifier("weka/classifiers/bayes/NaiveBayes")
nbModel <- NB(labels ~ ., trainingSet[1:500,])
evaluation <- evaluate_Weka_classifier(nbModel, numFolds = 10, seed=42, class = T)
evaluation

# SMO
SMO <- make_Weka_classifier("weka/classifiers/functions/SMO")
smoModel <- SMO(labels ~ ., trainingSet)
evaluation <- evaluate_Weka_classifier(smoModel, numFolds = 10, seed=42, class = T)
evaluation

# k-nearest
KNEAREST <- make_Weka_classifier("weka/classifiers/lazy/IBk")
kNearestModel <- KNEAREST(labels ~ ., trainingSet)
evaluation <- evaluate_Weka_classifier(kNearestModel, numFolds = 10, seed=42, class = T)



