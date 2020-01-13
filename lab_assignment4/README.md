steps 1,2 are unnecessary if Apache-POI jar is already included in /usr/share/java
user can also check installation with the same steps 
1. command "sudo apt-get update -y"
2. command "sudo apt-get install -y libapache-poi-java" will download Apache-POI into /usr/share/java
3. navigate to directory containing Main.java and the dataset file
4. command "javac -cp /usr/share/java/*:. Main.java" to compile 
5. command "java -cp /usr/share/java/*:. Main prog4Data.xlsx 10 10 -v" to exectue program. Program takes fileName trainingCount testCount (-v)verbose as arguments 