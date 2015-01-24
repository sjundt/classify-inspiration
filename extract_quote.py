# -*- coding: latin-1 -*-
import re
import sys


class extract_quote:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]
		#Q: quotation mark
		#At: attribution
		#Ap: apostrophe
		#Patterns: "xxx" -At, "nonsense xxx nonsense", "xxx" y, y "xxx",  

		reEllipse = re.compile('.*\.\.\.$')
		regexs = []
		#quotations, attribution
		reQAt = re.compile('[^\"“”]*[\"“]([^\"“”]+)[\"”]\s*[-~–—]^\"“”]+') #"xxx" - me
		regexs.append(reQAt)
		#apostrophes, attribution
		reApAt = re.compile('[^\"“”]*[\'‘]([^\"“”\'’‘]+)[\'’]\s*[-~–—]^\"“”\'’‘]+') #'xxx' - me
		regexs.append(reApAt)
		#quotations
		reQ1 = re.compile('[^\"“”]*[\"]([^\"“”]+)[\"][^\"“”]+') #"xxx" y
		regexs.append(reQ1)
		reQ2= re.compile('[^\"“”]*[“]([[^\"“”]+)[”][^\"“”]+') #"xxx" y
		regexs.append(reQ2)
		reQ3 = re.compile('[^\"“”]+[\"]([^\"“”]+)[\"][^\"“”]*') #y "xxx"
		regexs.append(reQ3)
		reQ4= re.compile('[^\"“”]+[“]([[^\"“”]+)[”][^\"“”]*') #y "xxx"
		regexs.append(reQ4)
		#apostrophes
		reAp1 = re.compile('[^\"“”’‘]*[‘]([^\"“”’‘]+)[’][^\"“”’‘]+') #'xxx' y
		regexs.append(reAp1)
		reAp2 = re.compile('[^\"“”’‘]+[‘]([^\"“”’‘]+)[’][^\"“”’‘]*') #y 'xxx'
		regexs.append(reAp2)

		with open(output_file, 'w') as extracted_file:	
			for quote in open(input_file):
				
				if reEllipse.match(quote)==None:
					already_matched = False
					for regex in regexs:
						if not already_matched:
							match = regex.match(quote)
							if match!=None:
								quote = match.group(1)+'\n'
								extracted_file.write(quote) #remove
								already_matched = True
					if not already_matched:
						print(quote)


	if  __name__ =='__main__':main(sys.argv[1:])
