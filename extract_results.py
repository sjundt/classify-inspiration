# -*- coding: utf_8 -*-
import re
import sys
import io

class extract_results:

	def main(io_names):
		if len(io_names)<2: 
			raise RuntimeError("Too few arguments: Enter an input_file & output_file [& categorization]")
		input_file = io_names[0]
		output_file = io_names[1]

		with io.open(output_file, 'a', encoding='utf-8') as results_file:	
			first_line = True
			questions = {}
			for quote in open(input_file):	
				quote = unicode(quote, 'utf-8')
				if len(io_names)==2:
					if first_line:
						answer_string = re.match('.*\"reject\"\t(.*)',quote.strip())
						answers = answer_string.group(1).strip().split("\t")
						question_re = re.compile('\"Answer[.](.*)\"')
						i=1
						for answer in answers:
							match = re.match(question_re , answer)
							if match==None:
								print (answer)
							questions[i]=match.group(1).strip()
							i=i+1
						first_line = False
					else:
						responses_string = re.match('.*\"(\t+)\"(Nonsense|Instructional|Non-inspirational|Optimistic|Motivational|Inspirational)\"\t\"(Nonsense|Instructional|Non-inspirational|Optimistic|Motivational|Inspirational)\"\t\"(Nonsense|Instructional|Non-inspirational|Optimistic|Motivational|Inspirational)\"\t\"(Nonsense|Instructional|Non-inspirational|Optimistic|Motivational|Inspirational)\"\t\"(Nonsense|Instructional|Non-inspirational|Optimistic|Motivational|Inspirational)\"$', quote)
						if (responses_string==None):
							print quote
						else:
							first_quote_num = len(responses_string.group(1))
							for i in range(5):
								response = responses_string.group(i+2)
								question = questions[first_quote_num+i]
								results_file.write(question+": "+response+"\n")
				else: #categorization
					if not first_line:
						responses_string = re.match('\s*\"[^\"]*\",\"([^\"]*)\".*\"(Non-Inspirational|no_agreement|Inspirational)\",\"[^\"]*\"\s*$', quote)
						response = responses_string.group(2)
						question = responses_string.group(1)
						results_file.write(question+": "+response+"\n")
					else:
						first_line = False



	if  __name__ =='__main__':main(sys.argv[1:])
