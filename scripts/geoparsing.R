# R-Script for the geoparsing of Facebook posts by the use of Named Entity Recognition (NER) to find toponyms.
# The NER method has been extended by a Gazetteer lookup for candidate word tokens.
#
# Note that you need a GeoNames data dump in a PostgreSQL database.
# If you want to create your own GeoNames gazetteer, you may follow the instructions from this GitHub repository:
# https://github.com/colemanm/gazetteer/blob/master/docs/geonames_postgis_import.md
#
# You also need the Stanford CoreNLP library for the NER task with a german model, which you can find here:
# https://stanfordnlp.github.io/CoreNLP/index.html#download
#
# sebastian.drost@hs-bochum.de

rm(list = ls())

if (!require("coreNLP")) {
  install.packages("coreNLP")
}
if (!require("caret")) {
  install.packages("caret")
}
if (!require("stringi")) {
  install.packages("stringi")
}
if (!require("RPostgreSQL")) {
  install.packages("RPostgreSQL")
}

library("coreNLP")
library("caret")
library("stringi")
library("RPostgreSQL")

# user name for the PostgreSQL database
dbUser = "dbUser"
# password for the PostgreSQL database
dbPassword = "dbPassword"
# PostgreSQL database name
dbName = "dbName"
# table for the GeoName data
geonameTable = "geonameTable"
# path to the CoreNLP library 
coreNlpLibLoc = "coreNlpPath"
# path to the german parameter file
coreNlpGermanParamFile = "germanParamFilePath"


#set working directory
setwd(choose.dir())

# set connection for PostgreSQL DB
con <-
  dbConnect(PostgreSQL(),
            user = dbUser,
            password = dbPassword,
            dbname = dbName)

# load CoreNLP java files
initCoreNLP(
  libLoc = coreNlpLibLoc,
  type = c("german"),
  parameterFile = coreNlpGermanParamFile,
  mem = "4g"
)

#load the raw Facebook posts from csv
geoData <-
  read.csv2(
    "./geoparsing/geoparsingData.csv",
    comment.char = "",
    stringsAsFactors = F
  )

# a simple filter pipeline to preprocess the text data
simpleFilterPipeline <- function(str) {
  # remove URLs
  str <-
    gsub("\\s?(http|www)(s?)(://)?([^\\.]*)[\\.|/](\\S*)", " ", str)
  # remove non characters
  str <- gsub("[^a-zA-Z äÄöÖüÜß-]", " ", str)
  # remove all dashes but intra-word dashes
  str <-
    gsub("( |^)-+|-+( |$)", "\\1", gsub("[^ [:alnum:]'-]", "", str))
  # remove additional whitespace
  str <- gsub("\\s+", " ", str)
  return(str)
}

# an extended filter pipeline that normalizes the text data
# by the use of POS tagging
filterPipelineExtended <- function(str) {
  # remove URLs
  str <-
    gsub("\\s?(http|www)(s?)(://)?([^\\.]*)[\\.|/](\\S*)", " ", str)
  # remove non characters
  str <- gsub("[^a-zA-Z äÄöÖüÜß-]", " ", str)
  # remove all dashes but intra-word dashes
  str <-
    gsub("( |^)-+|-+( |$)", "\\1", gsub("[^ [:alnum:]'-]", "", str))
  # remove additional whitespace
  str <- gsub("\\s+", " ", str)
  # do annotation
  annotation <- annotateString(str)
  tokens <- stri_encode(annotation$token$token, "UTF-8", "UTF-8")
  # lowercase all words with uppercase
  tokens[which(grepl("^[[:upper:]]+$", tokens))] <-
    tolower(tokens[which(grepl("^[[:upper:]]+$", tokens))])
  
  # uppercase the first character of NE and NN words
  tokens[which(annotation$token$POS == "NE")] <-
    paste(toupper(substring(tokens[which(annotation$token$POS == "NE")], 1, 1)),
          substring(tokens[which(annotation$token$POS == "NE")], 2), sep = "")
  
  tokens[which(annotation$token$POS == "NN")] <-
    paste(toupper(substring(tokens[which(annotation$token$POS == "NN")], 1, 1)),
          substring(tokens[which(annotation$token$POS == "NN")], 2), sep = "")
  
  str <- paste(tokens, collapse = " ")
  return(str)
}

# preprocess the first Facebook post with the extended processing pipeline
str <- filterPipelineExtended(geoData[1, "content"])
# annotated the first Facebook post
annObj <- annotateString(str)
annObj$token$token <-
  stri_encode(annObj$token$token, "UTF-8", "UTF-8")
# create a matrix whith columns for the word tokens, the NER annotation and the POS-Tag
content <-
  cbind(annObj$token$token, annObj$token$NER, annObj$token$POS)
# preparate the matrix for all other Facebook posts
nerMatrix <- content
colnames(nerMatrix) <- c("token", "ner", "pos")

# do the previous steps for all other Facebook posts
# and put the result into the matrix
for (i in 2:length(geoData[, "content"])) {
  print(paste("annotate post nr. ", as.character(i), collapse = ""))
  str <- filterPipelineExtended(geoData[i, "content"])
  annObj <- annotateString(str)
  annObj$token$token <-
    stri_encode(annObj$token$token, "UTF-8", "UTF-8")
  content <-
    cbind(annObj$token$token, annObj$token$NER, annObj$token$POS)
  nerMatrix <- rbind(nerMatrix, content)
}

extendedNerMatrix <- nerMatrix

selectStm <-
  paste("select * from ", geonameTable, " where name =", sep = "")

# do the extended part of the geoparsing, by searching
# for NN and NE tokens in the GeoNames database
for (i in 1:nrow(extendedNerMatrix)) {
  if (extendedNerMatrix[i, "pos"] == "NE" &&
      extendedNerMatrix[i, "ner"] != "I-LOC") {
    varName <- paste("'", extendedNerMatrix[i, "token"], "'", sep = "")
    query <- paste(selectStm, varName, sep = " ")
    rs <- dbGetQuery(con, query)
    if (length(rs) != 0) {
      extendedNerMatrix[i, "ner"] <- "I-LOC"
    }
  }
  else{
    if (extendedNerMatrix[i, "pos"] == "NN" &&
        extendedNerMatrix[i - 1, "pos"] == "APPR" &&
        extendedNerMatrix[i, "ner"] != "I-LOC") {
      varName <- paste("'", extendedNerMatrix[i, "token"], "'", sep = "")
      query <- paste(selectStm, varName, sep = " ")
      rs <- dbGetQuery(con, query)
      if (length(rs) != 0) {
        extendedNerMatrix[i, "ner"] <- "I-LOC"
      }
      
    }
  }
}

# preparate the result matrix for the evaluation
ner <- extendedNerMatrix[, "ner"]
ner[which(ner[] == "I-LOC")] <- "LOC"
ner[which(ner[] != "LOC")] <- "O"

#read the reference data
geoData <-
  read.csv2(
    "./geoparsing/geoparsingReference.csv",
    comment.char = "",
    stringsAsFactors = F
  )

geo_eval <- cbind(reference = geoData[, "reference"], predicted = ner)

# create a confusion matrix with the predicted and the reference toponyms
confMatr <-
  confusionMatrix(
    data = geo_eval[, "predicted"],
    reference = geo_eval[, "reference"],
    positive = "LOC",
    dnn = c("Prediction", "Reference"),
    mode = "prec_recall"
  )

#print the result of the geoparsing
print(confMatr)

rm(list = ls())