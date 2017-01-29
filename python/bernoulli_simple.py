import pandas as pd
import numpy as np
import csv
import math

def main():
	
	# Get and read in data 
	all_data = pd.read_csv('../data/linearEqsBernoulliSimpv1.csv')
	#print(all_data)

	df = pd.DataFrame(columns=['Iteration', 'GraphNo', 'p-','q-','p+','q+','edgeProb','setP'])


	for iteration in range(1,2):
		# Group data by iteration		
		iteration_data = all_data.where(all_data['Iteration'] == iteration).dropna()

		for edgeProb in range(1,11):
			#Group data by edge probability
			edgeprob_data = iteration_data.where(iteration_data['edgeProb'] == edgeProb*0.1).dropna()	

			for p in range(1,20):
				#Group data by p probability
				p_data = edgeprob_data.where(edgeprob_data['p'] == p*0.05).dropna()
	
				for graphNo in range(1,201):		
					# Group iteration data by graph number
					graph_data = p_data.where(p_data['GraphNo'] == graphNo).dropna()
				
					# Get all of the 'solutions' to linear equations in solution matrix
					solutions = graph_data['Stadd1']
			
					# Put the coefficient data of all linear equations in coefficient matrix
					coeff = graph_data['aCo']
					coeff = coeff.to_frame()
					coeff['bCo'] = graph_data['bCo']
					coeff['cCo'] = graph_data['cCo']	
					#print('Iteration: '+str(iteration)+' Graph No: ' + str(graphNo))		
			
					# Solve system of linear equations and print values for a, b and c. 
					try:
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
	
						df = df.append({'Iteration':iteration,'GraphNo':graphNo,'p-':pMinus,'q-':qMinus,'p+':pPlus,
						'q+':qPlus,'edgeProb':edgeProb*0.1,'setP':p*0.05}, 	ignore_index=True)
					except Exception:
						#print(str(p*0.05))
						pass

				
			
	df.to_csv('../data/bernoulliSimpleAnalysis.csv', sep=',')
	print('Finished')

if __name__ == "__main__":
	main()
