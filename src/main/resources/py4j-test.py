from py4j.java_collections import ListConverter
import numpy as np
class Wrapper:
	def logList(self, l, to_print):
		print(to_print)
		l = list(l)
		for i in range(len(l)):
			if isinstance(l[i], float):
				l[i] = np.log(l[i])
		l = ListConverter().convert(l, gateway._gateway_client)
		return l
