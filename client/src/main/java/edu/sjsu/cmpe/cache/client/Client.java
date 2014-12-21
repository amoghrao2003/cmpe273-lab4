package edu.sjsu.cmpe.cache.client;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        //CacheServiceInterface cache = new DistributedCacheService("http://localhost:3000");
        //CacheServiceInterface cache1 = new DistributedCacheService("http://localhost:3001");
        //CacheServiceInterface cache2 = new DistributedCacheService("http://localhost:3002");

        System.out.println("Add values in all three nodes");
        //Add the keys as in part 1
        new CRDTClient().insertKeys("http://localhost:3001","http://localhost:3000","http://localhost:3002");
        
        System.out.println("Values added now turn down one server");
        Thread.sleep(30000);
        //Add key two two nodes while switching off third node
        new CRDTClient().insertKeys("http://localhost:3001","http://localhost:3000","http://localhost:3002");
        System.out.println("Values added now in two servers start third one");
        
        Thread.sleep(30000);
        //Final read to check the read repair
        new CRDTClient().readKeys("http://localhost:3001","http://localhost:3000","http://localhost:3002"); 
        System.out.println("Read repair Done");
        
    }

}
