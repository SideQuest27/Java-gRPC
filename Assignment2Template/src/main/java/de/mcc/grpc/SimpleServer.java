package de.mcc.grpc;
import de.mcc.Assignment2Grpc;
import de.mcc.Assignment2OuterClass;
import de.mcc.LocalComsGrpc;
import de.mcc.LocalComsOuterClass;
import de.mcc.grpc.auth.AuthEncoder;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.mcc.Assignment2OuterClass.ZombieApocalypseAbility.*;
import static de.mcc.LocalComsOuterClass.ZombieApocalypseAbi.*;
import static de.mcc.LocalComsOuterClass.ZombieApocalypseAbi.IMPERSONATE_ZOMBIESL;

public class SimpleServer
{
    static final String target = "--------------";
    static final String isisEmail = "-------------------";
    static final String isisMatrikelNr = "-------";
    static final String authString = AuthEncoder.generateAuthString(isisEmail, isisMatrikelNr);

    public static void main(String[] args) throws IOException, InterruptedException
    {
        server.s.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                {
                    try {
                        server.s.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("Server has Shut Down!");
                })
        );
        server.s.awaitTermination();
    }
    static class server
    {
        static Server s = Grpc.newServerBuilderForPort(9090, InsecureServerCredentials.create())
                .addService(new ServiceExt())
                .build();
    }
    static class ServiceExt extends LocalComsGrpc.LocalComsImplBase
    {
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        Assignment2Grpc.Assignment2BlockingStub stub = Assignment2Grpc.newBlockingStub(channel);
        @Override
        public void dummy(LocalComsOuterClass.DummyReq request, StreamObserver<LocalComsOuterClass.DummyRes> responseObserver)
        {
            System.out.println(request);
            if(request.getAuth().equals("5d131462686d05d768c049261c934a497c54c00ee8200f15d3d82497d57b95e5"))
            {
                LocalComsOuterClass.DummyRes response=
                        LocalComsOuterClass
                                .DummyRes
                                .newBuilder()
                                .setOutput(1338)
                                .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                System.out.println("Connection to local-client established...");
                ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
                Assignment2Grpc.Assignment2BlockingStub stub = Assignment2Grpc.newBlockingStub(channel);
                Assignment2OuterClass.DummyRequest req=Assignment2OuterClass.DummyRequest.newBuilder().setAuth(authString).setInput(1337).build();
                Assignment2OuterClass.DummyResponse res=stub.dummy(req);
                if(res.getOutput()==1338)
                {
                    System.out.println("Connection to TU.Berlin-server established...\n");
                }
            }
        }
        @Override
        public void stockTicker(LocalComsOuterClass.StockTickerReq request, StreamObserver<LocalComsOuterClass.StockTickerRes> responseObserver)
        {
            Assignment2OuterClass.StockTickerRequest stkrequest =
                    Assignment2OuterClass
                    .StockTickerRequest.newBuilder()
                            .setAuth(authString)
                            .setTickerName(request.getTickerName())
                            .build();
            Assignment2OuterClass.StockTickerResponse stkresponce = stub.stockTicker(stkrequest);
            List<String> timestamps = stkresponce.getPricesList()
                    .stream()
                    .map(time -> time.getUnixTimestamp()).map(time -> LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()))
                    .map(time1 -> time1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toList();
            List<Double> prices = stkresponce.getPricesList()
                    .stream()
                    .map(combo ->(double) combo.getPriceCts()/100)
                    .toList();
            List<String> data = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++)
            {
                data.add("Date/time :- " + timestamps.get(i) + " [Price :" + prices.get(i) + " Eur]");
            }
            LocalComsOuterClass.StockTickerRes.Builder stkres = LocalComsOuterClass.StockTickerRes.newBuilder();
            for(int i=0; i< data.size();i++)
            {
                LocalComsOuterClass.StockTickerSingleRes stockTickerSingleResponse = LocalComsOuterClass.StockTickerSingleRes
                        .newBuilder()
                        .setData(data.get(i))
                        .build();

                stkres.addData(stockTickerSingleResponse);
            }
            responseObserver.onNext(stkres.build());
            responseObserver.onCompleted();
            System.out.println(" <-Data recieved from Tu.Berlin Server \n Data sent to client->\n");
        }

        @Override
        public void weatherInfo(LocalComsOuterClass.WeatherReq request, StreamObserver<LocalComsOuterClass.WeatherRes> responseObserver) {
            Assignment2OuterClass.WeatherRequest wthrrequest= Assignment2OuterClass.WeatherRequest
                    .newBuilder()
                    .setAuth(authString)
                    .setLat(request.getLat())
                    .setLon(request.getLon())
                    .setUnixTimestampBeginning(request.getUnixTimestampBeginning())
                    .setUnixTimestampEnd(request.getUnixTimestampEnd())
                    .build();
            Assignment2OuterClass.WeatherResponse wthrresponce= stub.weatherInfo(wthrrequest);
            List<String>wthrtimestamps = wthrresponce.getWeatherList()
                    .stream()
                    .map(group->group.getUnixTimestamp())
                    .map(time -> LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault()))
                    .map(time1 -> time1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toList();
            List<Double> tempreture = wthrresponce.getWeatherList()
                    .stream()
                    .map(couple->(double)couple.getTempDeg())
                    .toList();
            List<String> data = new ArrayList<>();
            for (int i = 0; i < wthrtimestamps.size(); i++)
            {
                data.add("Date/time :- " + wthrtimestamps.get(i) + " [temp:- :" + tempreture.get(i) + " deg.C]");
            }
            LocalComsOuterClass.WeatherRes.Builder wthrresbldr= LocalComsOuterClass.WeatherRes.newBuilder();
            for(int i=0;i< wthrresponce.getWeatherList().size();i++)
            {
                LocalComsOuterClass.WeatherSingleRes weatherSingleResponse = LocalComsOuterClass.WeatherSingleRes.newBuilder()
                        .setData(data.get(i))
                        .build();
                wthrresbldr.addWeather(weatherSingleResponse);
            }
            responseObserver.onNext(wthrresbldr.build());
            responseObserver.onCompleted();
            System.out.println(" <-Data recieved from Tu.Berlin Server \n Data sent to client->\n");
        }

        @Override
        public void zombieApocalypseSimulator(LocalComsOuterClass.ZombieApocalypseReq request, StreamObserver<LocalComsOuterClass.ZombieApocalypseRes> responseObserver)
        {
            Object abi;
            if(request.getAbilities(0).equals(FAST_RUNNERL))
            {
                abi=FAST_RUNNER;
            }
            else if (request.getAbilities(0).equals(AWESOME_ZOMBIE_DANCE_MOVESL))
            {
                abi=AWESOME_ZOMBIE_DANCE_MOVES;
            }
            else if (request.getAbilities(0).equals(IMPERSONATE_ZOMBIESL))
            {
                abi=IMPERSONATE_ZOMBIES;
            }
            else abi=IMUNE_TO_BITES;
            Assignment2OuterClass.ZombieApocalypseRequest zarequest = Assignment2OuterClass
                    .ZombieApocalypseRequest
                    .newBuilder()
                    .setAuth(authString)
                    .setName(request.getName())
                    .addAbilities((Assignment2OuterClass.ZombieApocalypseAbility) abi)
                    .setStrength(request.getStrength())
                    .build();
            Assignment2OuterClass.ZombieApocalypseResponse zaresponce = stub.zombieApocalypseSimulator(zarequest);
            LocalComsOuterClass.ZombieApocalypseRes res =  LocalComsOuterClass.ZombieApocalypseRes.newBuilder()
                    .setPointsTotal(zaresponce.getPointsTotal())
                    .setProbabilityToSurviveOneYear(zaresponce.getProbabilityToSurviveOneYear())
                    .setProbabilityToSurviveTwoYears(zaresponce.getProbabilityToSurviveTwoYears())
                    .setEvaluation(zaresponce.getEvaluation().replaceAll("\'","")).build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
            System.out.println(" <-Data recieved from Tu.Berlin Server \n Data sent to client->\n");
        }
    }
}
