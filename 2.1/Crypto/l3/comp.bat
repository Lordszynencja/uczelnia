javac Z1.java
java Z1 dist
java Z1 test
java Z1 e CTR 3 test\1.txt test\enc.txt
java Z1 e CBC 3 test\1.txt test\enc.txt
java Z1 e OFB 3 test\1.txt test\enc.txt
java Z1 d OFB 3 test\enc.txt test\dec.txt
java Z1 o OFB 1 test\enc.txt test\dec.txt
java Z1 c OFB 1 test\dec.txt test\enc.txt test\out1.txt
PAUSE