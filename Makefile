.PHONY: all clean doc compile

LIB_JARS=lib/djep-1.0.0.jar:lib/jep-2.3.0.jar:lib/peersim-1.0.5.jar

compile:
	mkdir -p target/classes
	javac -sourcepath src -classpath $(LIB_JARS) -d target/classes $(shell find src -name *.java)

run:
	mkdir -p graph
	java -ea -cp $(LIB_JARS):target/classes peersim.Simulator config.cfg

clean:
	rm -rf target
	rm -rf graph

all: clean compile run
