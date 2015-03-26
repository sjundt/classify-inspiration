classify-inspiration
====================
Used in fulfillment of the Senior Exercise at Pomona College.

Pulls tweets containing quotations for later processing and use in a classification task.
hash_quote-- #quotes, -filter:links, 1/3/15
hash_overheard -- #overheard, -filter:links

Get rid of retweets:
egrep -v '@|(\s|^|[.:,!?#";|~/])[Rr]([Tt]|[Ee][tT][wW][Ee][Ee][Tt])(\s|$|[.:!/?,";|~])'  hash_quote > quote.noRT

Fixes quotation marks:
sed 's/[“”❝¨]/"/g' quote.noRT > quote.noRT.1
sed "s/[‘’]/'/g" quote.noRT.1 > quote.noRT.2


Extract quotations from tweets.
extract_quote.py

Uniq & Sort:
LC_ALL=C sort -u --ignore-case  quote.noRT.e > quote.noRT.e.s

Cleans quotations.
clean_quotes.py
egrep '^(["#a-zA-Z0-9])\s*\S+\s+\S+' quote.noRT.e.s.clean.s > quote.noRT.e.s.clean.s.small
sed 's/#//g' quote.noRT.e.s.clean.s.small > quote.noRT.e.s.clean.s.small.hash

Unique quotations:
Sort quotations by length for containment script:
cat testfile | awk '{ print length, $0 }' | sort -n | cut -d" " -f2-
unique_containment.py
LC_ALL=C sort -u --ignore-case
uniquify.py

TO PREPARE FOR AMAZON TURK: 
Replace all " by ', replace all &amp; by &, add "" around the quotation for csv.
sed "s/\"/'/g" 
sed 's/\&amp;/\&/g'
sed 's// /g'
awk '{ print "\""$0"\""}' 

shuffle the file & split into small batches of ~500 each:
perl -MList::Util -e 'print List::Util::shuffle <>' dataset_copy > shuffled_dataset

on little files:
sed 's/"//g'
awk '{ print "\"\“"$0"\”\""}'
mt_consolidate.py


TO GET RESULTS FROM AMAZON TURK:
- download batch, put all HITID in _success file
./getResults.sh -successfile  -outputfile 
extract_results.py to data/results/extracted
sed 's/[“”❝¨]//g'
write_labels.py to data/_results


perl -MList::Util -e 'print List::Util::shuffle <>'
split into training & testing

Write the feature files: 
MakePOS.java

create a parsed file for training data set: lexparsermy.sh
WriteDictionary.java training_set parsed_data

Multiclassify.java
>>RESULTS are in name.multiclassify_results.txt

Get the best features:
perl svmtoWeight.pl model > name.featureWeights
BestWords.java









                                                              