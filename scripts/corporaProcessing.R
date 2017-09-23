#R-Script for some preparation functions
#that process text corpora

if (!require("openNLP")) {
  install.packages("openNLP")
}
if (!require("NLP")) {
  install.packages("NLP")
}
if (!require("tm")) {
  install.packages("tm")
}
if (!require("koRpus")) {
  install.packages("koRpus")
}
if (!require("SnowballC")) {
  install.packages("SnowballC")
}

library("openNLP")
library("NLP")
library("tm")
library("koRpus")
library("SnowballC")

# install openNLP model for german
# Tiger Corpus and STTS Tag Set
# install.packages("openNLPmodels.de",
#                  repos = "http://datacube.wu.ac.at/",
#                  type = "source")

#create annotators
sent_token_annotator <- Maxent_Sent_Token_Annotator(language = "de")
word_token_annotator <- Maxent_Word_Token_Annotator(language = "de")
pos_token_annotator <- Maxent_POS_Tag_Annotator(language = "de")

#read stopwords from file
stopwords <-
  read.table(
    "./scripts/german_stopwords_plain.txt",
    header = FALSE,
    encoding = "UTF-8",
    skip = 9,
    stringsAsFactors = FALSE
  )
stopwords <- as.character(stopwords[, 1])

#system parameters for koRpus
# set.kRp.env(
#   TT.cmd = "manual",
#   lang = "de",
#   TT.options = list(path = "C:\\TreeTagger\\", preset = "de")
# )

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

#remove non alpha numeric characters
#but keep dashes and umlauts
removeNonAlphanumericCharacters <- function(corpora) {
  corpora <-
    tm_map(corpora,
           content_transformer(gsub),
           pattern = "[^a-zA-Z0-9 äÄöÖüÜß-]",
           replacement = " ")
  return(corpora)
}

#transforms a character term into lemmas
# toLemmas <- function(value) {
#   valueChars <- as.character(value)
#   tags <- treetag(valueChars, encoding = "UTF-8", format = "obj")
#   lemma <- tags@TT.res$lemma
#   lemma[which(tags@TT.res$lemma == "<unknown>")] <-
#     tags@TT.res$token[which(tags@TT.res$lemma == "<unknown>")]
#   value <- paste(lemma, collapse = " ")
# }

#change the content from the text corpora with
#pos tagged words
# createLemmaCorpora <- function(corpora) {
#   corpora <- tm_map(corpora, content_transformer(toLemmas))
#   return(corpora)
# }

#transforms a character into pos tagged words
toPosTaggedWords <- function(value) {
  valueString <- as.String(value)
  
  # first do sentence annotation
  sentAnn <- annotate(valueString, sent_token_annotator)
  
  # second do word annotation given the
  # sentence annotation object to start with
  wordAnn <- annotate(valueString, word_token_annotator, sentAnn)
  
  #third do POS annotation given the
  #word annotation object to start with
  posTags <- annotate(valueString, pos_token_annotator, wordAnn)
  
  #get the word subset and the POS tags,
  #combine both and concatenate the single
  #tagged words
  wordSub <- subset(posTags, type == "word")
  tags  <- sapply(wordSub$features , "[[", "POS")
  taggedWords <- sprintf("%s/%s", valueString[wordSub], tags)
  taggedContent <- paste(taggedWords, collapse = " ")
  
  #overwrite  the content from the text corpus
  value <- taggedContent
}

#transforms a character into pos tags
toPosTags <- function(value) {
  valueString <- as.String(value)
  
  # first do sentence annotation
  sentAnn <- annotate(valueString, sent_token_annotator)
  
  # second do word annotation given the
  # sentence annotation object to start with
  wordAnn <- annotate(valueString, word_token_annotator, sentAnn)
  
  #third do POS annotation given the
  #word annotation object to start with
  posTags <- annotate(valueString, pos_token_annotator, wordAnn)
  
  #concatenate the POS tags
  wordSub <- subset(posTags, type == "word")
  tags  <- sapply(wordSub$features , "[[", "POS")
  taggedContent <- paste(tags, collapse = " ")
  
  #overwrite  the content from the text corpus
  value <- taggedContent
}

#change the content from the text corpora with
#pos tagged words
createPosTaggedWordCorpora <- function(corpora) {
  #create annotators
  sent_token_annotator <-
    Maxent_Sent_Token_Annotator(language = "de")
  word_token_annotator <-
    Maxent_Word_Token_Annotator(language = "de")
  pos_token_annotator <- Maxent_POS_Tag_Annotator(language = "de")
  
  corpora <- tm_map(corpora, content_transformer(toPosTaggedWords))
  
  return(corpora)
}

#change the content from the text corpora with
#pos tags
createPosTagCorpora <- function(corpora) {
  #create annotators
  sent_token_annotator <-
    Maxent_Sent_Token_Annotator(language = "de")
  word_token_annotator <-
    Maxent_Word_Token_Annotator(language = "de")
  pos_token_annotator <- Maxent_POS_Tag_Annotator(language = "de")
  
  corpora <- tm_map(corpora, content_transformer(toPosTags))
  
  return(corpora)
}

#apply several filter steps on the raw text corpora
#to reduce the number of features
filterPipeStemCorpora <- function(corpora) {
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
  corpora <- tm_map(corpora, stemDocument, language = "de")
  #remove additional whitespaces
  corpora <-  tm_map(corpora, stripWhitespace)
  return (corpora)
}

#apply several filter steps on the raw text corpora
#to reduce the number of features
# filterPipeLemmaCorpora <- function(corpora) {
#   #convert to lower case
#   corpora <- tm_map(corpora, content_transformer(tolower))
#   #remove URLS
#   corpora <- removeURLs(corpora)
#   #remove non alphanumeric characters
#   corpora <- removeNonAlphanumericCharacters(corpora)
#   #remove punctuation
#   corpora <-
#     tm_map(corpora, removePunctuation, preserve_intra_word_dashes = T)
#   #remove numbers
#   corpora <- tm_map(corpora, removeNumbers)
#   #remove stopwords
#   corpora <- tm_map(corpora, removeWords, stopwords)
#   #lemmatize the words
#   corpora <- createLemmaCorpora(corpora)
#   #remove additional whitespaces
#   corpora <-  tm_map(corpora, stripWhitespace)
#   return (corpora)
# }

#apply several filter steps on the raw text corpora
#to reduce the number of features for pos tagging
filterPipePosCorpora <- function(corpora) {
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
