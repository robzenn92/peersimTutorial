.PHONY: all clean doc compile

LIB_JARS=lib/djep-1.0.0.jar:lib/jep-2.3.0.jar:lib/peersim-1.0.5.jar

prepare:
	mkdir -p target/classes
	mkdir -p out/graph

compile:
	javac -sourcepath src -classpath $(LIB_JARS) -d target/classes $(shell find src -name *.java)

run:
	java -ea -cp $(LIB_JARS):target/classes peersim.Simulator src/main/resources/newscast.cfg

clean:
	rm -rf target
	rm -rf out

all: clean compile run
