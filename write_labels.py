# -*- coding: utf_8 -*-
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
		num_inspirational= 0
		num_non_inspirational = 0
		num_nonsense = 0
		num_unclear = 0
		num_neg_nonsense = 0
		num_nonsense_pos = 0
		with io.open(output_file, 'w', encoding='utf-8') as results_file:	
			with io.open('nonsense_pos_ambiguous', 'w', encoding='utf-8') as nonsense_pos_file:
				with io.open('nonsense_neg_ambiguous', 'w', encoding='utf-8') as nonsense_neg_file:
					with io.open('pos_neg_ambiguous', 'w', encoding='utf-8') as pos_neg_ambiguous_file:
						ins_regex = re.compile('\s*(Optimistic|Motivational|Instructional|Inspirational)\s*')
						non_ins_regex = re.compile('\s*(Non-[Ii]nspirational)\s*')
						for quotation in quotations.keys():
							insp = False
							non_insp = False
							nonsense = False
							answers = quotations[quotation]
							for answer in answers:
								if ins_regex.match(answer)!=None:
									insp=True
								elif non_ins_regex.match(answer)!=None:
									non_insp = True
								else:
									nonsense = True
							if insp:
								if not non_insp and not nonsense: #insp only
									results_file.write("+1 "+quotation+"\n")
									num_inspirational = num_inspirational+1
								elif non_insp: #insp/non_insp
									pos_neg_ambiguous_file.write(quotation+"\n")
									num_unclear = num_unclear+1
								else: #insp/nonsense
									nonsense_pos_file.write(quotation+"\n")
									num_nonsense_pos = num_nonsense_pos+1
							elif non_insp:
								if not nonsense: #non_insp only
									results_file.write("-1 "+quotation+"\n")
									num_non_inspirational= num_non_inspirational+1
								else: #nonsense/non_insp
									nonsense_neg_file.write(quotation+"\n")
									num_neg_nonsense = num_neg_nonsense+1
							else:
								num_nonsense = num_nonsense+1

		print("inspirational:"+str(num_inspirational))
		print("non-inspirational:"+str(num_non_inspirational))
		print("nonsense:"+str(num_nonsense))
		print("ambiguous pos/neg:"+str(num_unclear))
		print("ambiguous pos/nonsense:"+str(num_nonsense_pos))
		print("ambiguous neg/nonsense:"+str(num_neg_nonsense))
		print("agreement/ambiguous posneg:"+str((num_inspirational+num_non_inspirational+0.0)/(num_unclear+num_non_inspirational+num_inspirational)))



	if  __name__ =='__main__':main(sys.argv[1:])
