package core;


import com.google.gson.Gson;

class Out {


    String helloWorld;


    public Out(String helloWorld) {
        this.helloWorld = helloWorld;
    }


    public String getHelloWorld() {
        return helloWorld;
    }

    public void setHelloWorld(String helloWorld) {
        this.helloWorld = helloWorld;
    }



}


public class Testing {

    public static void main(String[] args) {
        int[] seq = {1,2,3,3};
        ResultOld result = new ResultOld(seq,null);
        Gson gson = new Gson();
        String hello = gson.toJson(result);
        System.out.println(hello);
    }



}
