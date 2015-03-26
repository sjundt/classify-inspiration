# -*- coding: utf_8 -*-
import sys
import io
import re

class uniquify_containment:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]

		# find unique quotes
		unique_quotes = set()
		quotes_to_remove = set()
		for quote1 in reversed(open(input_file).readlines()):	#longest first
			unique_quotes.add(quote1)
			for quote2 in reversed(open(input_file).readlines()):	#longest first
				if len(quote1)>len(quote2):
					if quote1.find(quote2.rstrip())!=-1: #rstrip for the newline
						if len(quote2)<5:
							quotes_to_remove.add(quote2)
						else:
							quotes_to_remove.add(quote1)


		with io.open(output_file, 'w', encoding='utf-8') as unique_output:	
			for quote in unique_quotes.difference(quotes_to_remove):
				unique_output.write(unicode(quote, 'utf-8'))

	if  __name__ =='__main__':main(sys.argv[1:])