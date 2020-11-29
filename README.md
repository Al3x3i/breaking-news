# breaking-news
Breaking news project.

# Project consists of the following elements:
    - Maven Project
    - Spring boot 2.4.0
    - Java 11
    - Lombok
    - ROME library – For parsing feeds (https://github.com/rometools/rome)
    - OpenNLP library
    
# Architecture decisions
## ROOM library
Basically it was the fastest solution to fetch RSS feeds and get titles per each item in news.
You can find more about ROME library here:
- https://rometools.github.io/rome/
- https://github.com/rometools/rome
- https://howtodoinjava.com/spring-boot/spring-boot-rome-rss-and-atom-feed/ 

### What to improve, do not load a whole document into memory. `SAX Parser` is suitable option.

### Other options for working with XML documents
Other options for parsing XML documents are listed here: https://www.baeldung.com/java-xml

## OpenNLP library
The OpenNLP is used to extract noun phrases from a singular sentence.
This project supports only English sentences.
Below are listed links which were used while implementation:

- https://www.tutorialspoint.com/opennlp/opennlp_finding_parts_of_speech.htm
- https://www.devglan.com/artificial-intelligence/open-nlp-pos-tagger-example
- http://opennlp.sourceforge.net/models-1.5/
- https://github.com/challengebiswa/opennlptest/blob/42a5cdec3a3a5a3255e457f7c2afe042e9cf78b3/src/main/java/nlp/intent/toolkit/POSTaggingExample.java
