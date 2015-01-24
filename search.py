from TwitterSearch import *
import datetime 

def getOrder(tag, filename):
    tso = TwitterSearchOrder() # create a TwitterSearchOrder object
    tso.set_keywords(['-filter:links', tag]) # let's define all words we would like to have a look for
    tso.set_language('en') # we want to see English tweets only
    tso.set_include_entities(False) # and don't give us all the entity information
    with open(filename) as last:
        since_id = int(last.read().replace("\s", ''))
        tso.set_since_id(since_id)
    return tso


try:
    tsoOverheard = getOrder('#overheard', "last_collected_overheard")
    tsoQuote = getOrder('#quote', "last_collected_quote")
    queries = [('overheard',tsoOverheard), ('quote',tsoQuote)]

    # it's about time to create a TwitterSearch object with our secret tokens
    with open('access') as access_file:
        codes = access_file.read().split("\n")
        ts = TwitterSearch(
            consumer_key = codes[0],
            consumer_secret = codes[1],
            access_token = codes[2],
            access_token_secret = codes[3]
         )

    for (name, tso) in queries:
        first = True
        with open("hash_"+name, 'a') as output: #change to 'a'
            for tweet in ts.search_tweets_iterable(tso):
                output.write(tweet['text'].replace('\n', ' ')+"\n")
                if first:
                    first = False
                    with open('last_collected_'+name, 'w') as last:
                        last.write(str(ts.get_minimal_id()))


except TwitterSearchException as e: # take care of all those ugly errors if there are some
    print(e)