# -*- coding: utf_8 -*-
import sys
import io
import re
import difflib

class put_quotes:

	def main(io_names):
		if len(io_names)!=3: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file & number")
		input_file = io_names[0]
		output_file = io_names[1]
		num_per_line = int(io_names[2])


		# put 5 quotes on a line, tab delimited
		with io.open(output_file, 'w', encoding='utf-8') as output:
			current_num = 1
			for i in range(1,num_per_line):
				output.write("quotation"+str(i)+",")
			output.write("quotation"+str(num_per_line)+"\n")
			for quote in open(input_file):	
				if current_num==num_per_line:
					output.write(quote.rstrip()+"\n")
					current_num=1
				else:
					output.write(quote.rstrip()+",")
					current_num = current_num+1

	if  __name__ =='__main__':main(sys.argv[1:])