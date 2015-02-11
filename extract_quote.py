# -*- coding: utf_8 -*-
import re
import sys
import io

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
		reApAt = re.compile(u'[^\"]*[\']+([^\"\']+)[\']+\s*[-~–—][^\"\']+$') #'xxx' - me
		regexs.append(reApAt)
		#quotations
		reQ1 = re.compile(u'[^\"]*[\"]+([^\"]+)[\"]+[^\"]+$') #"xxx" y
		regexs.append(reQ1)
		reQ3 = re.compile(u'[^\"]+[\"]+([^\"]+)[\"]+[^\"]*$') #y "xxx"
		regexs.append(reQ3)
		#apostrophes
		reAp1 = re.compile(u'[^\"]*\'\'+([^\"]+)\'\'+[^\"]+$') #'xxx' y
		regexs.append(reAp1)
		reAp2 = re.compile(u'[^\"]+\'\'+([^\"]+)\'\'+[^\"]*$') #y 'xxx'
		regexs.append(reAp2)

		regexs.append(re.compile(u'[^\"«»]*[«»]+([^»\"«]+)[«»]+[^\"»«]+$')) #«xxx» y
		regexs.append(re.compile(u'[^\"«»]+[«»]+([^»\"«]+)[«»]+[^\"»«]*$')) #y «xxx»
		
		regexs.append(re.compile(u'\\s*#Quote\\s*\\d+\\s*[-~–—:]\\s*([^.]+[.])[^.]*$')) #Quote \\d+ - xxx. nonsense

		# #quote xxx .
		reAlone =  re.compile(u'#quote:?([^\".!?]+)[.!?]$') # #quote xxx.
		regexs.append(reAlone)

		regexs.append(re.compile(u'\\s*([^.]+\\S[^.]*[.])(\\S+\\s+)?(\\S[.]\\s+)?(\\S+\\s*)?(#[qQ]uote.*)?$')) # xxx. name i. name? #quote
		regexs.append(re.compile(u'\\s*([^-~–—]+\\S[^-~–—]*)[-~–—]\\s*(\\S+\\s+)?(\\S[.]\\s+)?(\\S+\\s*)?(#[qQ]uote.*)?$')) # xxx- name i. name? #quote
		regexs.append(re.compile(u'\\s*#[Qq]uote\\s+([^.!]+\\S[^.]*[.!])\\s*(\\S+\\s+)?(\\S[.]\\s+)?(\\S+\\s*)?(#\\S+\\s*)*$')) # #quote xxx. name i. name?
		# regexs.append(re.compile(u'\\s*([^#]+)(#\\S+\\s+)*#\\S+\\s*$')) # xxx. #y #z #t...




		with io.open(output_file, 'w', encoding='utf-8') as extracted_file:	
			with io.open("not_catching", 'w', encoding='utf-8') as not_catching:	
				for quote in open(input_file):	
					quote = unicode(quote, 'utf-8')
					if reEllipse.match(quote)==None:
						already_matched = False
						for regex in regexs:
							if not already_matched:
								match = regex.match(quote)
								if match!=None:
									quote = match.group(1)+'\n'
									extracted_file.write(quote)
									already_matched = True
						if not already_matched:
							not_catching.write(quote)


	if  __name__ =='__main__':main(sys.argv[1:])
