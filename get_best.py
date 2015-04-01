# -*- coding: utf_8 -*-
import re
import sys
import io

class get_best:

	def main(io_names):
		regex = re.compile('stoplistParams:\\s*(.*):\\s*accuracy: (.*)$')
		expregex = re.compile('.*uni:(.*)bi:(.*)(\\d{3})')
		files = ["data/results/data1/posneg.experiment_stoplist_results.txt", "data/results/data2/posneg.experiment_stoplist_results.txt", "data/results/data3/posneg.experiment_stoplist_results.txt"]
		# featureSet = []
		unigrams = {}
		bigrams = {}
		features = {}
		for file in files:
			accuracies = []
			accuracy_dict = {}
			for line in open(file):
				match = regex.match(line)
				if (match!=None):
					acc = float(match.group(2))
					while (acc in accuracies):
						acc = acc + 0.000000001
					accuracies.append(acc)
					accuracy_dict[acc] = match.group(1)
			print ("file:"+file)
			accuracies.sort()
			bests = set([])
			for i in range(5):
				next_num = accuracies.pop()
				featureString = accuracy_dict[next_num]
				print (next_num, featureString)
				match = expregex.match(featureString)
				if (match==None):
					raise Exception("error w/ pattern for expregex")
				else:
					if match.group(1) not in unigrams:
						unigrams[match.group(1)] = 1
					else:
						unigrams[match.group(1)] = unigrams[match.group(1)]+1
					if match.group(2) not in bigrams:
						bigrams[match.group(2)] = 1
					else:
						bigrams[match.group(2)] = bigrams[match.group(2)]+1
					if match.group(3) not in features:
						features[match.group(3)] = 1
					else:
						features[match.group(3)] = features[match.group(3)]+1
		print ("unigrams:")
		print unigrams
		print ("bigrams:")
		print bigrams
		print ("features:")
		print features

		# 		bests.add(accuracy_dict[next_num])
		# 	featureSet.append(bests)
		# in_all = featureSet.pop()
		# for best in featureSet:
		# 	in_all = in_all.intersection(best)
		# print ("Feature sets in all top 10:")
		# for elt in in_all:
		# 	print elt


	if  __name__ =='__main__':main(sys.argv[1:])
