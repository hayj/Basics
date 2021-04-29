import socket
from py4j.java_collections import SetConverter, MapConverter, ListConverter
from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
import json
import time
import logging
logging.basicConfig(level=20, format="%(message)s")


def pick_random_free_port\
(
	nb_ports=1,
	raise_exception=True,
	max_iteration=10000,
):
	'''
	Returns a free port (or several ports in a tuple according to the parameter `nb_ports`).
	'''
	ports = []
	iteration = 0
	while (len(ports) < nb_ports) and (iteration < max_iteration):
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
			sock.bind(('', 0))
			sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			ports.append(sock.getsockname()[1])
		iteration += 1
	if len(ports) == 0:
		if raise_exception:
			raise Exception("No free port found.")
		else:
			return None
	if nb_ports == 1:
		return ports[0]
	else:
		return tuple(ports)


ports = pick_random_free_port(2)
java_port = ports[0]
python_port = ports[1]
logging.info("java-port: " + str(java_port))
logging.info("python-port: " + str(python_port))

wrapper = Wrapper()
gateway = ClientServer(
	java_parameters=JavaParameters(port=java_port),
	python_parameters=PythonParameters(port=python_port),
	python_server_entry_point=wrapper)