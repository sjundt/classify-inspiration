# -*- coding: utf_8 -*-
import re
import sys
import io

class clean_quotes:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]

		
		regexs = []	
		regexs.append(re.compile('^(\\s)*#[JPq].*\\d+:\\s*\\d+\\s*(.+)$')) # #Jesus/#Proverbs/#quote #1Cor10:19....

		# get rid of excess hashtags
		regexs.append(re.compile('\\s*#\\S+\\s+(#\\S+\\s+|#\\S+:)+(.+)')) # multiple hashtags at beginning
		regexs.append(re.compile('\\W*\\s*#(\\S+\\s*):(.+\\S.*)')) # #....:
		regexs.append(re.compile('\\W*\\s*#(\\S+\\s*)([A-Z].+\\S.*)')) # #.... [A-Z]
		regexs.append(re.compile('(\\s*)(.*\\S.*[.])\\s*(#\\S+\\s+)+(#\\S+\\s*)*$')) # hashtags at end after a period
		regexs.append(re.compile('(\\s*)(.*\\S.*)\\s*(#\\S+\\s+)+(#\\S+\\s*)+$')) # multiple hashtags at end

		# get rid of attributions
		regexs.append(re.compile('(\\s*)([^.]+\\S[^.]*[.])(\\S+\\s+)?(\\S+\\s+)?(\\S+\\s*)?(#\\S*\\s*)*$')) # xxx. name name name? #tag #tag ....
		regexs.append(re.compile('(\\s*)(\\S.*)[-~–—]+\\s*(\\S+\\s+)?(\\S+\\s+)?(\\S+\\s*)?(#\\S*\\s*)*\\s*$')) # xxx- name name name? #tag #tag ...

		#deal with #... : quotation
		regexs.append(re.compile('\\s*(#\\S*\\s*)*\\S*\\s*:(.+\\S.*)')) # name:
		regexs.append(re.compile('(\\s*)#[Qq]uote[^:-~–—.\"\']+[:-~–—.\"\']\\s*(\\S.*)')) # quote of the day


		# #strip surrounders & beginnings
		regexs.append(re.compile('\\s*([\"\'*]+)(.*)([\"\'*]+[.]*\\s*)$')) #"....
		regexs.append(re.compile('\\s*([\"\'*]+)([^\"\']+)$')) #"....

		#extract quotations
		regexs.append(re.compile('()[^\"]*[\"]+([^\"]+)[\"]+[^\"]+$')) #"xxx" y
		regexs.append(re.compile('()[^\"]+[\"]+([^\"]+)[\"]+[^\"]*$')) #y "xxx"
		#apostrophes
		regexs.append(re.compile('()[^\"]*\'\'+([^\"]+)\'\'+[^\"]+$')) #'xxx' y
		regexs.append(re.compile('()[^\"]+\'\'+([^\"]+)\'\'+[^\"]*$')) #y 'xxx'

		#strip #quote
		regexs.append(re.compile('\\s*(.*\\S)\\s*#[Qq]uote\\s*[:-]+\\s*(.*)$')) #take after #quote:
		regexs.append(re.compile('(\\s*)(.*\\S)\\s*#[Qq]uote[^:-]*$')) #take before #quote

		with io.open(output_file, 'w', encoding='utf-8') as extracted_file:	
			for quote in open(input_file):	
				quote = unicode(quote, 'utf-8')
				for _ in range(1):
					for regex in regexs:
						match = regex.match(quote)
						if match!=None:
							quote = match.group(2)+'\n'
							quote.strip()
				match_strip = re.compile('\\s*([:–„«❝‘`|~„”\"\'-.()/]|\\d)+\\s*([^\"%].*\\S)$').match(quote)
				if match_strip!=None:
					quote = match_strip.group(2)+'\n'
				if re.compile('(^\()|(#[Qq]uote)|(^(\\s*\\S*\\s*\\S*\\s*\\S*\\s*)?$)').match(quote)==None: #starts w/ ( or contains #quote or is <=3 words
					extracted_file.write(quote.lstrip().lower())
						


	if  __name__ =='__main__':main(sys.argv[1:])
