package sk.stuba.fei.uim.vsa.pr1c;

import sk.stuba.fei.uim.vsa.pr1c.Entities.*;
import sk.stuba.fei.uim.vsa.pr1c.ui.KeyboardInput;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Project1C {

  public static void main(String[] args) {
    CarParkService service = new CarParkService();
    service.createCarPark("park","Bratislava",1);
    service.createCarPark("park2","Bratislava",3);
    service.createCarParkFloor(1L,"A");
    service.createCarParkFloor(2L,"C");
    service.createCarParkFloor(2L,"B");
    service.createParkingSpot(2L,"C","A");
    service.createParkingSpot(2L,"C","B");
    service.createParkingSpot(2L,"C","C");
    service.createParkingSpot(2L,"C","C");
    service.createParkingSpot(2L,"B","E");
    service.createParkingSpot(2L,"B","F");
    service.createParkingSpot(2L,"B","G");
    service.createParkingSpot(2L,"B","A");
    service.createParkingSpot(2L,"B","I");
    service.createParkingSpot(2L,"B","J");
    service.createParkingSpot(1L,"A","A");
    service.createParkingSpot(1L,"A","B");
    service.createParkingSpot(1L,"A","C");
    service.createParkingSpot(1L,"A","C");
    service.createParkingSpot(1L,"A","E");
    service.createParkingSpot(1L,"A","F");
    service.createParkingSpot(1L,"A","G");
    service.createParkingSpot(1L,"A","A");
    service.createParkingSpot(1L,"A","I");
    service.createParkingSpot(1L,"A","J");
    service.createUser("JAno","Stano","dasd@sadas");
    service.createUser("JAn","Standso","dasd@sadasadass");
    service.createUser("JA","Stadsano","dasd@sadsadasdas");
    service.createUser("J","Staasdno","dasd@sadasdas");
    service.createUser("JAnho","Staasdano","dasd@saadasddas");
    service.createCar(24L,"audi","a6","black","tn124dc");
    service.createCar(24L,"audy","a6","black","tn125dc");
    service.createCar(23L,"audidsa","a6","black","tn127dc");
    service.createCar(23L,"audidsa","a6","black","tn123dc");
    service.createReservation(7L,31L);
    service.createReservation(10L,28L);
    service.createReservation(12L,29L);
    service.createReservation(14L,30L);

    System.out.println("Vyber si co chces(1-4):");
    System.out.println("1.Vypisat\n2.Pridat\n3.Vymazat\n4.Koniec");
    int input = KeyboardInput.readInt();
    while (input != 4) {
      switch (input){
        case 1:
          System.out.println("1.Najst parkovisko podla mena\n2.Vypisat vsetky parkoviska");
          System.out.println("3.Najst poschodie podla identifikatora\n4.Vypisat vsetky poschodia v dome");
          System.out.println("5.Najst miesto podla id\n");
          System.out.println("6.Najst auto podla SPZ\n7.Vypisat vsetky auta podla pouzivatela");
          System.out.println("8.Najst zakaznika podla emailu\n9.Vypisat vetkych zakaznikov");
          input = KeyboardInput.readInt();
          switch (input){
            case 1:
              String name = KeyboardInput.readString("Meno");
              System.out.println(service.getCarPark(name));
              break;
            case 2:
              System.out.println(service.getCarParks());
              break;
            case 3:
              name = KeyboardInput.readString("Identifikator");
              String parkName = KeyboardInput.readString("Meno domu");
              CarPark park = (CarPark) service.getCarPark(parkName);
              System.out.println(service.getCarParkFloor(park.getId(),name));
              break;
            case 4:
              parkName = KeyboardInput.readString("Meno domu");
              park = (CarPark) service.getCarPark(parkName);
              System.out.println(service.getCarParkFloors(park.getId()));
              break;
            case 5:
              Long id = (long) KeyboardInput.readInt("ID");
              System.out.println(service.getParkingSpot(id));
              break;
            case 6:
              String spz = KeyboardInput.readString("SPZ");
              System.out.println(service.getCar(spz));
              break;
            case 7:
              String email = KeyboardInput.readString("Email");
              Customer customer = (Customer) service.getUser(email);
              System.out.println(service.getCars(customer.getId()));
              break;
            case 8:
              email = KeyboardInput.readString("Email");
              customer = (Customer) service.getUser(email);
              System.out.println(customer);
              break;
            case 9:
              System.out.println(service.getUsers());
              break;


          }

          break;
        case 2:
          System.out.println("1.Pridat nove parkovisko\n2.Prida poschodie do parkoviska\n3.Pridat miesto na poschodie\n4.Pridat auto\n5.Pridat pouzivatela\n6.Pridat rezervaciu");
          input = KeyboardInput.readInt();
          switch (input){
            case 1:
              String name = KeyboardInput.readString("Meno");
              String adress = KeyboardInput.readString("Adresa");
              int price = KeyboardInput.readInt("Cena za hodinu");
              Object o = service.createCarPark(name,adress,price);
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");

              break;
            case 2:
              String floor = KeyboardInput.readString("ID Poschodia");
              name = KeyboardInput.readString("Meno parkoviska");
              CarPark park = (CarPark) service.getCarPark(name);

              o = service.createCarParkFloor(park.getId(),floor);
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");
              break;
            case 3:
              name = KeyboardInput.readString("Meno parkoviksa");
              String id = KeyboardInput.readString("Identifikator poschodia");
              String id2 = KeyboardInput.readString("Identifikator miesta");
              park = (CarPark) service.getCarPark(name);

              o =service.createParkingSpot(park.getId(), id,id2);
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");
              break;
            case 4:
              String brand = KeyboardInput.readString("Znacka");
              String model = KeyboardInput.readString("Model");
              String color = KeyboardInput.readString("Farba");
              String spz = KeyboardInput.readString("SPZ");
              String email = KeyboardInput.readString("Email majitela");
              Customer customer = (Customer) service.getUser(email);

              o = service.createCar(customer.getId(),brand,model,color,spz);
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");
              break;
            case 5:

              name = KeyboardInput.readString("Meno");
              String name2 = KeyboardInput.readString("Priezvisko");
              String mail = KeyboardInput.readString("Email");

              o = service.createUser(name,name2,mail);
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");
              break;
            case 6:
              spz = KeyboardInput.readString("SPZ auta");
              Long spot = (long) KeyboardInput.readInt("ID miesta");
              Car car = (Car) service.getCar(spz);
              o = service.createReservation(spot, car.getId());
              if (o == null) System.out.println("Neuspesne");
              else System.out.println("uspesne");
              break;

          }
          break;
        case 3:
          System.out.println("1.Parkovisko\n2.Poschodie\n3.Miesto\n4.Auto\n5.Zakaznika");
          input = KeyboardInput.readInt();
          switch (input){
            case 1:
              long id = KeyboardInput.readInt("Zadaj ID");
              Object o = service.deleteCarPark(id);
              if (o == null){
                System.out.println("Neuspesne");
              }
              else {
                System.out.println("Uspesne");
              }
              break;
            case 2:
              id = KeyboardInput.readInt("Zadaj ID");
              o = service.deleteCarParkFloor(id);
              if (o == null){
                System.out.println("Neuspesne");
              }
              else {
                System.out.println("Uspesne");
              }
              break;
            case 3:
              id = KeyboardInput.readInt("Zadaj ID");
              o = service.deleteParkingSpot(id);
              if (o == null){
                System.out.println("Neuspesne");
              }
              else {
                System.out.println("Uspesne");
              }
              break;
            case 4:
              id = KeyboardInput.readInt("Zadaj ID");
              o = service.deleteCar(id);
              if (o == null){
                System.out.println("Neuspesne");
              }
              else {
                System.out.println("Uspesne");
              }
              break;
            case 5:
              id = KeyboardInput.readInt("Zadaj ID");
              o = service.deleteUser(id);
              if (o == null){
                System.out.println("Neuspesne");
              }
              else {
                System.out.println("Uspesne");
              }
              break;
          }
          break;
      }
      System.out.println("Vyber si co chces(1-4):");
      System.out.println("1.Vypisat\n2.Pridat\n3.Vymazat\n4.Koniec");
      input = KeyboardInput.readInt();
    }
  }



//service.deleteCarPark(2L);
  //service.deleteCar(28L);
  //service.deleteUser(22L);
  //service.deleteCarParkFloor(4L);
  //service.deleteParkingSpot(14L);
  //String input = KeyboardInput.readString();
  //System.out.println(service.getAvailableParkingSpots("park2"));
//    public static void main(String[] args) {
//      CarPark carPark = new CarPark();
//      carPark.setId(2L);
//      carPark.setPricePerHour(2);
//      carPark.setAddress("BA");
//      carPark.setName("meno");
//
//      CarParkFloor floor = new CarParkFloor();
//      floor.setId(3L);
//      floor.setSpots(null);
//      floor.setFloorIdentifier("C");
//
//      CarParkService service = new CarParkService();
//      Date date = new Date(122, Calendar.MARCH,28);





//      //System.out.println(service.getReservations(10L,date));;
//      //System.out.println(service.getMyReservations(23L));
//
//
//      /*service.createHoliday("Sviatok Prace",new Date(Calendar.YEAR,Calendar.MAY,1));
//      service.createHoliday("Vznik SR",new Date(Calendar.YEAR,Calendar.JANUARY,1));
//      //service.createHoliday("Sviatok Prace",new Date(Calendar.YEAR,Calendar.MAY,1));
//
//      System.out.println(service.getHoliday(new Date(Calendar.YEAR,Calendar.MAY,1)));
//      List<Holidays> list2 = service.getHolidays().stream()
//              .map(e -> (Holidays) e)
//              .collect(Collectors.toList());
//      System.out.println(list2.get(0).getName());
//      System.out.println(list2.get(1).getName());
//      service.deleteHoliday(33L);*/
//      //
////
////      //System.out.println(service.getUsers().get(0));
////      service.deleteUser(15L);
////      //System.out.println(service.getUsers().get(0));
////      service.getParkingSpots(2L);
////
////      service.createReservation(7L,20L);
////      service.endReservation(24L);
////      service.createReservation(7L,21L);
////      service.endReservation(25L);
////      service.createReservation(7L,22L);
////      service.endReservation(26L);
////      service.createReservation(7L,23L);
////      //System.out.println(date);
////      System.out.println(service.getReservations(7L,date));


}
