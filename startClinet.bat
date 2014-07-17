::add item timestap 0
java -cp .;gson.jar org.gbc.kinect.TcpSender 192.168.0.101 8000 {"type":"add","action":{"timestamp":"0","velocity":{"x":1123,"y":256,"z":464},"position":{"x":1.123,"y":2.56,"z":4.64}}}
::add item timestap 10
java -cp .;gson.jar org.gbc.kinect.TcpSender 192.168.0.101 8000 {"type":"add","action":{"timestamp":"10","velocity":{"x":1123,"y":256,"z":464},"position":{"x":1.123,"y":2.56,"z":4.64}}}
::pools for all items between 0 and 100 milliseconds
java -cp .;gson.jar org.gbc.kinect.TcpSender 192.168.0.101 8000 {"type":"poll","start":0,"end":10000}