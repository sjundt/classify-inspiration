classify-inspiration
====================
Used in fulfillment of the Senior Exercise at Pomona College.

Pulls tweets containing quotations for later processing and use in a classification task.
hash_quote-- #quotes, -filter:links, 1/3/15
hash_overheard -- #overheard, -filter:links

Get rid of retweets:
egrep -v '@|(\s|^|[“-.:,!?#";|~/])[Rr]([Tt]|[Ee][tT][wW][Ee][Ee][Tt])(\s|$|[.:!/?,"“-;|~])' hash_quote > quote.noRT

Fixes quotation marks:
sed 's/[“”]/"/g' quote.noRT > quote.noRT.i
sed "s/[‘’]/'/g" quote.noRT.i > quote.noRT.q

Extract quotations from tweets.
extract_quote.py

Uniq & Sort:
LC_ALL=C sort -u --ignore-case  quote.noRT.q.e > quote.noRT.q.e.s

Cleans quotations.
clean_quotes.py
egrep '^(["#a-zA-Z0-9])\S+\s+\S+' quote.noRT.q.e.s.clean.s > quote.noRT.q.e.s.clean.s.small
sed 's/#//g' quote.noRT.q.e.s.clean.s.small > quote.noRT.q.e.s.clean.s.small.hash





                                                              