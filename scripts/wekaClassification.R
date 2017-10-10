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


# install openNLP model for german
# Tiger Corpus and STTS Tag Set
# install.packages("openNLPmodels.de",
#                  repos = "http://datacube.wu.ac.at/",
#                  type = "source")
#expand heap space for large query results


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

# do several filter steps to reduce the number of features
# and make the text corpora handy

trainingCorpora <- filterPipeStopwordsStemCorpora(textCorpus)

trainingCorpora[[1]]$content

# term unigram features and binary weighting
dtm_uni_bin <-
  DocumentTermMatrix(trainingCorpora, control = list(weighting = weightBin))

# term unigram features and term frequency weighting
dtm_uni_Tf <- DocumentTermMatrix(trainingCorpora)


# term unigram features and term frequency-inverse document frequency
dtm_uni_TfIdf <- DocumentTermMatrix(trainingCorpora,
                                    control = list(
                                      weighting =
                                        function(x)
                                          weightTfIdf(x, normalize = F)
                                    ))

#create a bigram tokenizer for document-term-matrix creating
BigramTokenizer <-
  function(x)
    unlist(lapply(ngrams(words(x), 2), paste, collapse = " "), use.names = FALSE)

# bigrams
dtm_bi_Tf <-
  DocumentTermMatrix(trainingCorpora, control = list(tokenize = BigramTokenizer))

trainingLabels <- as.factor(trainingData$label)
trainingFrame <- as.data.frame(as.matrix(dtm_uni_bin))
trainingSet <- cbind(labels = trainingLabels, trainingFrame)

#SVM
SMO <- make_Weka_classifier("weka/classifiers/functions/SMO")

smoModel <-
  SMO(labels ~ ., trainingSet, control = Weka_control(C=10,
    K = list("weka.classifiers.functions.supportVector.PolyKernel", E = 1)
  ))
evaluation <-
  evaluate_Weka_classifier(
    smoModel,
    numFolds = 10,
    seed = 42,
    class = T
  )

# Multinomial Naive Bayes
MultiNomialNB <-
  make_Weka_classifier("weka/classifiers/bayes/NaiveBayesMultinomial")
MultiNomialNBmodel <- MultiNomialNB(labels ~ ., trainingSet)


#Decision Trees
DecisionTree <- make_Weka_classifier("weka/classifiers/trees/J48")
DTmodel <- DecisionTree(labels ~ ., trainingSet, control = Weka_control(B = TRUE))
evaluation <-
  evaluate_Weka_classifier(DTmodel,
                           numFolds = 10,
                           seed = 42,
                           class = T)


# Naive Bayes
NaiveBayes <-
  make_Weka_classifier("weka/classifiers/bayes/NaiveBayes")
NBmodel <- NaiveBayes(labels ~ ., trainingSet)
evaluation <-
  evaluate_Weka_classifier(NBmodel,
                           numFolds = 10,
                           seed = 42,
                           class = T)

write.table(
  evaluation$detailsClass,
  file = "./evaluation/detailsClass.csv",
  sep = ";",
  row.names = T
)
write.table(
  evaluation$confusionMatrix,
  file = "./evaluation/confMatrix.csv",
  sep = ";",
  row.names = T
)

evaluation

#IbK
IBK <- make_Weka_classifier("weka.classifiers.lazy.IBk")
kParam <- 2
ibkModel <-
  IBK(labels ~ .,
      trainingSet,
      control = Weka_control(K = kParam, A = "weka.core.neighboursearch.BallTree"))

evaluation <-
  evaluate_Weka_classifier(ibkModel,
                           numFolds = 10,
                           seed = 42,
                           class = T)

#SVM
SMO <- make_Weka_classifier("weka/classifiers/functions/SMO")

smoModel <-
  SMO(labels ~ ., trainingSet)

smoModel <-
  SMO(labels ~ ., trainingSet, control = Weka_control(
    K = list("weka.classifiers.functions.supportVector.PolyKernel", E = 1)
  ))

evaluation <-
  evaluate_Weka_classifier(smoModel,
                           numFolds = 10,
                           seed = 42,
                           class = T)

evaluation$details

write.table(
  evaluation$detailsClass,
  file = "./evaluation/detailsClass.csv",
  sep = ";",
  row.names = T
)
write.table(
  evaluation$confusionMatrix,
  file = "./evaluation/confMatrix.csv",
  sep = ";",
  row.names = T
)


# SVM with Params
SMO <- make_Weka_classifier("weka/classifiers/functions/SMO")
smoModel <-
  SMO(labels ~ ., trainingSet, control = Weka_control(K = list(
    "weka.classifiers.functions.supportVector.RBFKernel"
  )))

evaluation <-
  evaluate_Weka_classifier(smoModel,
                           numFolds = 10,
                           seed = 42,
                           class = T)
evaluation$details


# Decision Tree Classification

evaluation$details

dtm_uni_pos_TfIdf <- cbind(dtm_uni_TfIdf, dtm_pos_TfIdf)

sparse <- removeSparseTerms(dtm_uni_bin, 0.9995)
inspect(sparse)
dtm_uni_TfIdf_sparse <- removeSparseTerms(dtm_uni_TfIdf, 0.9995)

dtm_uni_pos_TfIdf <- cbind(dtm_uni_TfIdf, dtm_pos_TfIdf_sparse)

weights <- chi.squared(labels ~ ., trainingSet)
weights$attr_importance
which(weights[, ] == 0)

#Evaluate Atrributes
attrEval <- InfoGainAttributeEval(labels ~ ., trainingSet)
rownames(attrEval)
filterAttr <- attrEval[which(attrEval > 0)]
filterAttr[1]
typeof(filterAttr)
names(filterAttr)

?chisq.test()
