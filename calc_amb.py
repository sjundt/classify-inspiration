#-*- coding: utf_8 -*-
import re
import sys
import io

class write_labels:

	def main(io_names):
		if len(io_names)!=2: 
			raise RuntimeError("Incorrect number of arguments: Enter an input_file & output_file")
		input_file = io_names[0]
		output_file = io_names[1]

		quotations={}				
		for quote in open(input_file):	
			quote = unicode(quote, 'utf-8')
			match = re.match('(.*): (\S+)$',quote.rstrip())
			if match is None:
				print quote
			quotation = match.group(1)
			response = match.group(2)
			if quotation not in quotations.keys() or quotations[quotation] is None:
				quotations[quotation]=[response]
			else:
				quotations[quotation].append(response)
		num_opt= 0
		num_mot = 0
		num_ins = 0
		num_unclear = 0
		
		opt_regex = re.compile('\s*(Optimistic)\s*')
		mot_regex = re.compile('\s*(Motivational)\s*')
		ins_regex = re.compile('\s*(Instructional)\s*')
		non_ins_regex = re.compile('\s*(Non-[Ii]nspirational)\s*')
		for quotation in quotations.keys():
			opt = False
			mot = False
			ins = False
			non_ins = False
			answers = quotations[quotation]
			for answer in answers:
				if opt_regex.match(answer)!=None:
					opt=True
				elif mot_regex.match(answer)!=None:
					mot = True
				elif ins_regex.match(answer)!=None:
					ins = True
				elif non_ins_regex.match(answer)!=None:
					non_ins = True
			if not non_ins:
				if opt:
					if not mot and not ins: 
						num_opt = num_opt+1
					else:
						num_unclear = num_unclear+1
				if mot:
					if not ins:
						num_mot = num_mot +1
					else:
						num_unclear = num_unclear+1
				else:
					num_ins = num_ins+1

		print("instructional:"+str(num_ins))
		print("motivational:"+str(num_mot))
		print("Optimistic:"+str(num_opt))
		print("ambiguous:"+str(num_unclear))




	if  __name__ =='__main__':main(sys.argv[1:])
