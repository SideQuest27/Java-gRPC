package de.mcc.grpc;
import de.mcc.LocalComsGrpc;
import de.mcc.LocalComsOuterClass;
import de.mcc.grpc.auth.AuthEncoder;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import static de.mcc.LocalComsOuterClass.ZombieApocalypseAbi.*;

public class SimpleClient {
    static final String isisEmail = "------------------------";
    static final String isisMatrikelNr = "-------------";
    static final String authString = AuthEncoder.generateAuthString(isisEmail, isisMatrikelNr);
    static final String localServerTarget ="localhost:9090";

    public static void main(String[] args) throws InterruptedException
    {
        ManagedChannel channel = Grpc.newChannelBuilder(localServerTarget, InsecureChannelCredentials.create()).build();
        LocalComsGrpc.LocalComsBlockingStub stub = LocalComsGrpc.newBlockingStub(channel);
        LocalComsOuterClass.DummyReq request = LocalComsOuterClass.DummyReq.newBuilder().setAuth(authString).setInput(1337).build();
        LocalComsOuterClass.DummyRes response = stub.dummy(request);
        if (response.getOutput() == 1338)
        {
            System.out.println("Connection to local-server established...\n");
        }
        Scanner sc = new Scanner(System.in);
        while (true)
        {
            System.out.println(
                    "Welcome to the DATA-HUB" +
                    "\n\nWhat are you searching for:-" +
                            "\n" +
                            "(1):- Stock Market Data. \n" +
                            "(2):- Weather Reports. \n" +
                            "(3):- Or Your Chances of surviving a Zombie Fallout ;). \n \n" +
                            "PLEASE ENTER THE SERVICE-NUMBER for THE SERVICE-> ");
            int Service = sc.nextInt();
            if(Service>3) Service=20;
            switch (Service)
            {
            case 1:
            sc.nextLine();
            System.out.print("Please enter the Stock Ticker :- ");
            String stockname = sc.nextLine();
            LocalComsOuterClass.StockTickerReq stkrequest =
                    LocalComsOuterClass
                            .StockTickerReq.newBuilder()
                            .setAuth(authString)
                            .setTickerName(stockname)
                            .build();
            LocalComsOuterClass.StockTickerRes stkresponce = stub.stockTicker(stkrequest);
            List<String> responce= stkresponce.getDataList()
                    .stream()
                    .map(data->data.toString())
                    .map(val->val.replaceAll("data:",""))
                    .toList();
            responce.stream().forEach(System.out::print);
            case 10:
            LocalComsOuterClass.DummyReq request1 = LocalComsOuterClass.DummyReq.newBuilder().setAuth(authString).setInput(1337).build();
            LocalComsOuterClass.DummyRes response1 = stub.dummy(request1);
            if (response1.getOutput() == 1338)
            {
                System.out.println("\nConnection to local-server intact...\n");
            }
            break;
            case 2:
            LocalComsOuterClass.WeatherReq wthrrequest =
                    LocalComsOuterClass
                            .WeatherReq.newBuilder()
                            .setAuth(authString)
                            .setLat(234.23312)
                            .setLon(2342.34)
                            .setUnixTimestampBeginning(111111)
                            .setUnixTimestampEnd(Instant.now().getEpochSecond())
                            .build();
            LocalComsOuterClass.WeatherRes wthrresponce = stub.weatherInfo(wthrrequest);
            List<String> responce1= wthrresponce.getWeatherList()
                    .stream()
                    .map(data->data.toString())
                    .map(val->val.replaceAll("data:",""))
                    .toList();
            responce1.stream().forEach(System.out::print);
            case 20:
            LocalComsOuterClass.DummyReq request2 = LocalComsOuterClass.DummyReq.newBuilder().setAuth(authString).setInput(1337).build();
            LocalComsOuterClass.DummyRes response2 = stub.dummy(request2);
            if (response2.getOutput() == 1338)
            {
                System.out.println("\nConnection to local-server intact...\n");
            }
            break;
            case 3:
                System.out.println("Please enter your characters strength value (1->10) :- ");
                int strength=sc.nextInt();
                Object abi;
                if(strength<=2)
                {
                    abi=FAST_RUNNERL;
                }
                else if (strength>=3&&strength<=5)
                {
                    abi=AWESOME_ZOMBIE_DANCE_MOVESL;
                }
                else if (strength>=6&&strength<=8)
                {
                    abi=IMPERSONATE_ZOMBIESL;
                }
                else abi =AWESOME_ZOMBIE_DANCE_MOVESL;
                LocalComsOuterClass.ZombieApocalypseReq zarequest =
                        LocalComsOuterClass
                                .ZombieApocalypseReq
                                .newBuilder()
                                .setAuth(authString)
                                .setName("Karan")
                                .addAbilities((LocalComsOuterClass.ZombieApocalypseAbi) abi)
                                .setStrength(strength)
                                .build();
            LocalComsOuterClass.ZombieApocalypseRes zaresponce = stub.zombieApocalypseSimulator(zarequest);
            System.out.println(zaresponce);
            case 30:
            LocalComsOuterClass.DummyReq request3 = LocalComsOuterClass.DummyReq.newBuilder().setAuth(authString).setInput(1337).build();
            LocalComsOuterClass.DummyRes response3 = stub.dummy(request3);
            if (response3.getOutput() == 1338)
            {
                System.out.println("Connection to local-server intact...\n");
            }
            break;
            }
        }
    }
}
