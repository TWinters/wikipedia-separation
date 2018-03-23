package be.kuleuven.alsn;

import static spark.Spark.get;

public class HelloWorld {
    /**
     * Run the program and then open http://localhost:4567/hello
     *
     * @param args
     */
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
