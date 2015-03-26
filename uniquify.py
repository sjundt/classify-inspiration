# -*- coding: utf_8 -*-
import sys
import io
import re
import difflib

class uniquify:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]

		# find unique quotes
		with io.open(output_file, 'w', encoding='utf-8') as unique_quotes:	
			prev_quote = ""
			for quote in open(input_file):	
				s = difflib.SequenceMatcher(None, quote, prev_quote)
				if s.ratio()<0.7:
					unique_quotes.write(unicode(quote, 'utf-8'))
				prev_quote = quote

	if  __name__ =='__main__':main(sys.argv[1:])