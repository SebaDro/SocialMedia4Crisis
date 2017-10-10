# R-Script for corpora processing, to preprocess large text corpora
# and reduce the number of text features.
# 
# sebastian.drost@hs-bochum.de
if (!require("openNLP")) {
  install.packages("openNLP")
}
if (!require("NLP")) {
  install.packages("NLP")
}
if (!require("tm")) {
  install.packages("tm")
}


library("openNLP")
library("NLP")
library("tm")

# You have to install the openNLP model for the german language first.
# So please uncomment the following lines and install the german langugae packages.
# install.packages("openNLPmodels.de",
#                  repos = "http://datacube.wu.ac.at/",
#                  type = "source")

#create token annotators
sent_token_annotator <- Maxent_Sent_Token_Annotator(language = "de")
word_token_annotator <- Maxent_Word_Token_Annotator(language = "de")
pos_token_annotator <- Maxent_POS_Tag_Annotator(language = "de")

#read stopwords from file
stopwords <-
  read.table(
    "./data/german_stopwords_plain.txt",
    header = FALSE,
    encoding = "UTF-8",
    skip = 9,
    stringsAsFactors = FALSE
  )
stopwords <- as.character(stopwords[, 1])

#use regex to remove urls that can occur
#in several different forms
removeURLs <- function(corpora) {
  corpora <-
    tm_map(corpora,
           content_transformer(gsub),
           pattern = "\\s?(http|www)(s?)(://)?([^\\.]*)[\\.|/](\\S*)",
           replacement = " ")
  return(corpora)
}

# remove non alpha numeric characters
# but keep dashes and umlauts
removeNonAlphanumericCharacters <- function(corpora) {
  corpora <-
    tm_map(corpora,
           content_transformer(gsub),
           pattern = "[^a-zA-Z0-9 äÄöÖüÜß-]",
           replacement = " ")
  return(corpora)
}

# remove non alpha numeric characters
# but keep dashes and umlauts and punctuation
removeNonAlphanumericCharactersButPunctuation <- function(corpora) {
  corpora <-
    tm_map(corpora,
           content_transformer(gsub),
           pattern = "[^a-zA-Z0-9 äÄöÖüÜß[:punct:]]",
           replacement = " ")
  return(corpora)
}

# apply several filter steps on the raw text corpora
# to reduce the number of features for the bag-of-words approach
filterPipeBOWCorpora <- function(corpora) {
  #convert to lower case
  corpora <- tm_map(corpora, content_transformer(tolower))
  #remove URLS
  corpora <- removeURLs(corpora)
  #remove non alphanumeric characters
  corpora <- removeNonAlphanumericCharacters(corpora)
  #remove punctuation
  corpora <-
    tm_map(corpora, removePunctuation, preserve_intra_word_dashes = T)
  #remove numbers
  corpora <- tm_map(corpora, removeNumbers)
  #remove additional whitespaces
  corpora <-  tm_map(corpora, stripWhitespace)
  return (corpora)
}

# apply several filter steps on the raw text corpora
# and remove stopwords to reduce the number of features
filterPipeStopwordsCorpora <- function(corpora) {
  #convert to lower case
  corpora <- tm_map(corpora, content_transformer(tolower))
  #remove URLS
  corpora <- removeURLs(corpora)
  #remove non alphanumeric characters
  corpora <- removeNonAlphanumericCharacters(corpora)
  #remove punctuation
  corpora <-
    tm_map(corpora, removePunctuation, preserve_intra_word_dashes = T)
  #remove numbers
  corpora <- tm_map(corpora, removeNumbers)
  #remove stopwords
  corpora <- tm_map(corpora, removeWords, stopwords)
  
  #remove additional whitespaces
  corpora <-  tm_map(corpora, stripWhitespace)
  return (corpora)
}

# apply several filter steps on the raw text corpora
# and remove stopwords and stem the text to reduce the number of features
filterPipeStopwordsStemCorpora <- function(corpora) {
  #convert to lower case
  corpora <- tm_map(corpora, content_transformer(tolower))
  #remove URLS
  corpora <- removeURLs(corpora)
  #remove non alphanumeric characters
  corpora <- removeNonAlphanumericCharacters(corpora)
  #remove punctuation
  corpora <-
    tm_map(corpora, removePunctuation, preserve_intra_word_dashes = T)
  #remove numbers
  corpora <- tm_map(corpora, removeNumbers)
  #remove stopwords
  corpora <- tm_map(corpora, removeWords, stopwords)
  #stem the words
  corpora <- tm_map(corpora,
                    content_transformer(stemDocument),
                    language = "german")
  #remove additional whitespaces
  corpora <-  tm_map(corpora, stripWhitespace)
  return (corpora)
}

