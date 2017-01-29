import pandas as pd
import numpy as np
import csv
import math

def main():
	
	# Get and read in data 
	all_data = pd.read_csv('../data/linearEqsBernoulliComplex.csv')
	print(all_data)
	print('\n')

	df = pd.DataFrame(columns=['GraphNo', 'p-','q-','p+','q+'])


	for graphNo in range(1,11):
		# Group data into separate graphs, by graph number
		data = all_data.where(all_data['GraphNo'] == graphNo).dropna()

		# Get all of the 'solutions' to linear equations in solution matrix
		solutions = data['Stadd1']
	
		# Put the coefficient data of all linear equations in coefficient matrix
		coeff = data['aCo']
		coeff = coeff.to_frame()
		coeff['bCo'] = data['bCo']
		coeff['cCo'] = data['cCo']

		print('Graph No: ' + str(graphNo))		

		# Solve system of linear equations and print values for a, b and c
		a, b, c = np.linalg.lstsq(coeff.as_matrix(), solutions.as_matrix())[0]
		#print('a = ' + str(a))
		#print('b = ' + str(b))
		#print('c = ' + str(c)+'\n')

		#Set m as the total number of adopters
		#m = data.tail(1)['bCo'].values

		# Find p and q based on mMinus
		mMinus = (-b-math.sqrt((b*b)-4*a*c))/(2*a)
		#print('m = (-b-sqrt(b^2-4ac))/2a = ' + str(mMinus))
		pMinus = a/mMinus
		qMinus = b + pMinus
		#print('p- = a/m = '+str(pMinus))
		#print('q- = b+p = '+str(qMinus))

		# Find p and q based on mPlus
		mPlus = (-b+math.sqrt((b*b)-4*a*c))/(2*a)
		#print('m = (-b+sqrt(b^2-4ac))/2a = ' + str(mPlus))
		pPlus = a/mPlus	
		qPlus = b + pPlus
		#print('p+ = a/m = '+str(pPlus))
		#print('q+ = b+p = '+str(qPlus)+'\n')

		df = df.append({'GraphNo':graphNo,'p-':pMinus,'q-':qMinus,'p+':pPlus,'q+':qPlus}, ignore_index=True)
	
	df.to_csv('../data/bernoulliComplexAnalysis.csv', sep=',')
	print('Finished')

if __name__ == "__main__":
	main()
