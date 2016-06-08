import socket;
import time;
import datetime;
import random;

LOGIN_PACKAGE = '78780d01000000000000000100018cdd0d0a'

def location_package(speed, ignition):
	now = datetime.datetime.now()
	latitude = 3130.54 + 120*random.random() - 60
	longitude = 1261.2 + 60*random.random() - 30
	print('latitude=%f, longitude=%f' % (latitude/60, longitude/60))
	return '78781f12%02x%02x%02x%02x%02x%02xcf%08x%08x%02x%c48f01cc00287d001fb8000000000d0a' % (now.year%100, now.month, now.day, now.hour, now.minute, now.second, int(latitude*30000), int(longitude*30000), speed, 'd' if ignition else '5')


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("localhost",5023))
s.send(bytearray.fromhex(LOGIN_PACKAGE))
s.recv(1024)
print('connected')
input('press enter...')
for i in range(1, 10):
	package = location_package(150, True)
	print(package)
	s.send(bytearray.fromhex(package))
	s.recv(1024)
	print('sent packet #%02d' % i)
	input('press enter...')
s.send(bytearray.fromhex(location_package(0, False)))
s.recv(1024)
print('sent packet #10')
s.close()
time.sleep(5)
