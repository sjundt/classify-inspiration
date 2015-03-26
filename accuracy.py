#authors: sarah jundt, george price, shannon lubetich
#modified 3/2015 by sarah jundt for senior project
#gets the accuracy of test results 
#args: paths for predicted_values, actual_values 
import sys
from itertools import izip

predPath = sys.argv[1]
dataPath = sys.argv[2]

total = 0
correct = 0
correct_positive = 0
correct_negative = 0
diff_positive = 0
diff_negative = 0
wrong_positive = 0
wrong_negative = 0
with open(predPath) as predFile, open(dataPath) as dataFile: 
    for d, p in izip(predFile, dataFile):
        if (float(p)<0 and float(d)<0):
        	correct = correct+1
        	correct_negative = correct_negative + 1
        elif (float(p)>0 and float(d)>0):
        	correct = correct+1
        	correct_positive = correct_positive + 1
        elif (float(p)>0):
        	diff_positive = diff_positive + float(p)
        	wrong_positive = wrong_positive + 1
        else:
        	diff_negative = diff_negative + (0.0 - float(p))
        	wrong_negative = wrong_negative + 1
        total = total+1
print "accuracy:",float(correct)/total
print "total correct:", correct,  "total:", total
print "positive correct:", correct_positive,  "negative correct:", correct_negative
print "for misclassified positive, average positive score:", (float(diff_positive)/wrong_positive), "for misclassified negative, average negative score:", (float(diff_negative)/wrong_negative)

