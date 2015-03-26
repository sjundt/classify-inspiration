# -*- coding: utf_8 -*-
import re
import sys
import io

def write_it(file,quote):
		if re.compile('(#overheard)|(^(\\s*\\S*\\s*\\S*\\s*\\S*\\s*)?$)').match(quote)==None: #contains #overheard or is <=3 words
			file.write(quote)

class extract_quote:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]
		#Q: quotation mark
		#At: attribution
		#Ap: apostrophe
		#Patterns: "xxx" -At, "nonsense "xxx" nonsense", "xxx" y, y "xxx",  

		reEllipse = re.compile('.*\.\.\.$')
		regexs = []
		#quotations, attribution
		reQAt = re.compile(u'[^\"]*[\"]+(.*\\S.*)[\"]+\\s*[-~–—].+$') #"xxx" - me

		regexs.append(reQAt)
		reQAt2 = re.compile(u'[\"][^\"]+[\"]+([^\"]+)[\"]+[^\"]+[\"]$') #"nonsense "xxx" nonsense"
		regexs.append(reQAt2)
		#apostrophes, attribution
		reApAt = re.compile(u'[^\"]*[\']+([^a-z][^\"\']+)[\']+\s*[-~–—][^\"\']+$') #'xxx' - me
		regexs.append(reApAt)
		#quotations
		reQ1 = re.compile(u'[^\"]*[\"]+([^\"]+)[\"]+[^\"]+$') #"xxx" y
		regexs.append(reQ1)
		reQ3 = re.compile(u'[^\"]+[\"]+([^\"]+)[\"]+[^\"]*$') #y "xxx"
		regexs.append(reQ3)
		#apostrophes
		reAp1 = re.compile(u'[^\"]*\'+([^a-z][^\"]+)\'+[^\"]+$') #'xxx' y
		regexs.append(reAp1)
		reAp2 = re.compile(u'[^\"]+\'+([^a-z][^\"]+)\'+[^\"]*$') #y 'xxx'
		regexs.append(reAp2)

		regexs.append(re.compile(u'[^\"«»]*[«»]+([^»\"«]+)[«»]+[^\"»«]+$')) #«xxx» y
		regexs.append(re.compile(u'[^\"«»]+[«»]+([^»\"«]+)[«»]+[^\"»«]*$')) #y «xxx»
		
		# regexs.append(re.compile(u'\\s*#Quote\\s*\\d+\\s*[-~–—:]\\s*([^.]+[.])[^.]*$')) #Quote \\d+ - xxx. nonsense

		# # #overheard xxx .
		regexs.append(re.compile(u'\\s*#overheard:?\\s*([^\".!?]+[.!?]+)\\s*$')) # #quote xxx.
		regexs.append(re.compile(u'\\s*([^\".!?]+[.!?]+)\\s*(#overheard)?\\s*$')) # #quote xxx.


		# regexs.append(re.compile(u'\\s*([^.!]+[.!])\\s*(#\\S*\\s*)*(\\S+\\s+)?(\\S+\\s+)?(\\S+\\s*)?(#\\S*\\s*)*$')) # xxx. name name name? #tag #tag ....
		# regexs.append(re.compile(u'\\s*([^.!]+[.!])\\s*[-~–—]+.*$')) # xxx. - ...

		# regexs.append(re.compile(u'\\s*(\\S.*)[-~–—]+\\s*(\\S+\\s+)?(\\S+\\s+)?(\\S+\\s*)?(#\\S*\\s*)*\\s*$')) # xxx- name name name? #tag #tag ...
		# regexs.append(re.compile(u'\\s*#[Qq]uote\\s+([^.!]+[.!])\\s*(\\S+\\s+)?(\\S+\\s+)?(\\S+\\s*)?(#\\S+\\s*)*$')) # #quote xxx. name name name?
		# # regexs.append(re.compile(u'\\s*([^#]+)(#\\S+\\s+)*#\\S+\\s*$')) # xxx. #y #z #t...

		# # #quote .x.x.x. . <three words or hashtags
		# regexs.append(re.compile(u'\\s*([^-~–—:].*[.])\\s*(#\\S*\\s*)*([^. ]+\\s+){0,3}(#\\S*\\s*)*$'))



		with io.open(output_file, 'w', encoding='utf-8') as extracted_file:	
			with io.open("not_catching_oh", 'w', encoding='utf-8') as not_catching:	
				for quote in open(input_file):	
					quote = unicode(quote, 'utf-8')
					if reEllipse.match(quote)==None:
						already_matched = False
						for regex in regexs:
							if not already_matched:
								match = regex.match(quote)
								if match!=None:
									quote = match.group(1)+'\n'
									write_it(extracted_file,quote.lstrip().lower())
									already_matched = True
						if not already_matched:
							if quote.count("\"")%2==0 and quote.count("\"")>1: #even number of quotations >1
								quote_split = quote.split('\"')
								for i in range(len(quote_split)):
									if i%2==1: #this is between quotations
										write_it(extracted_file,quote_split[i].lstrip().lower())
							else:		
								not_catching.write(quote)

	if  __name__ =='__main__':main(sys.argv[1:])
