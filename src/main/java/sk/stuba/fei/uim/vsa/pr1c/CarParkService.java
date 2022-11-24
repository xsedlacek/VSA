package sk.stuba.fei.uim.vsa.pr1c;

import sk.stuba.fei.uim.vsa.pr1c.Entities.*;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class CarParkService extends AbstractCarParkService implements Serializable {
    protected EntityManagerFactory emf;

    public CarParkService() {
        this.emf = Persistence.createEntityManagerFactory("vsa-project-c");
    }

    protected void close() {
        emf.close();
    }

    @Override
    public Object createCarPark(String name, String address, Integer pricePerHour) {

        if (name == null) return null;

        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        CarPark carPark = (CarPark) getCarPark(name);
        if (carPark == null){
            transaction.begin();
            carPark = new CarPark();
            carPark.setAddress(address);
            carPark.setName(name);
            carPark.setPricePerHour(pricePerHour);
            manager.persist(carPark);
            transaction.commit();
            return carPark;
        }
        return null;
    }

    @Override
    public Object getCarPark(Long carParkId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(CarPark.class,carParkId);
    }

    @Override
    public Object getCarPark(String carParkName) {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<CarPark> query = manager.createNamedQuery(CarPark.FIND_BY_NAME,CarPark.class);
        query.setParameter("name",carParkName);
        try{
            query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return query.getSingleResult();
    }

    @Override
    public List<Object> getCarParks() {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<Object> query = manager.createNamedQuery(CarPark.FIND_ALL,Object.class);

        return  query.getResultList();
    }

    @Override
    public Object updateCarPark(Object carPark) {
        if (carPark == null) return null;

        EntityManager manager = emf.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        CarPark carPark1 = manager.find(CarPark.class,((CarPark) carPark).getId());
        carPark1.setAddress(((CarPark) carPark).getAddress());
        carPark1.setName(((CarPark) carPark).getName());
        carPark1.setPricePerHour(((CarPark) carPark).getPricePerHour());
        transaction.commit();

        return carPark1;
    }

    @Override
    public Object deleteCarPark(Long carParkId) {

        EntityManager manager = emf.createEntityManager();
        CarPark carPark = manager.find(CarPark.class,carParkId);

        if (carPark.getId() != null) {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Map<String,List<Object>> map = getOccupiedParkingSpots(carPark.getName());
            for (CarParkFloor f: carPark.getFloors()) {
                List<ParkingSpot> spots = map.get(f.getFloorIdentifier()).stream()
                        .map(e -> (ParkingSpot) e)
                        .collect(Collectors.toList());
                for (ParkingSpot s: spots) {
                    endReservation(s.getCar().getReservation().getId());
                }
            }
            manager.remove(carPark);
            transaction.commit();
            return carPark;
        }
        return null;
    }

    @Override
    public Object createCarParkFloor(Long carParkId, String floorIdentifier) {

        if (floorIdentifier == null || carParkId == null) return null;

        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNativeQuery("SELECT FLOORIDENTIFIER FROM CAR_PARK_FLOOR WHERE car_park_id = " + carParkId + " and FLOORIDENTIFIER = '" + floorIdentifier +"'");
        try{
            query.getSingleResult();
            return null;

        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            CarParkFloor carParkFloor = new CarParkFloor();
            carParkFloor.setFloorIdentifier(floorIdentifier);
            manager.persist(carParkFloor);
            transaction.commit();

            transaction.begin();
            CarPark carPark = manager.find(CarPark.class, carParkId);
            List<CarParkFloor> floors = carPark.getFloors();
            floors.add(carParkFloor);

            carPark.setFloors(floors);
            transaction.commit();
            return carParkFloor;
        }

    }

    @Override
    public Object getCarParkFloor(Long carParkFloorId) {
        EntityManager manager = emf.createEntityManager();
        return manager.find(CarParkFloor.class,carParkFloorId);
    }

    @Override
    public List<Object> getCarParkFloors(Long carParkId) {
        if (carParkId == null) return null;

        EntityManager manager = emf.createEntityManager();
        CarPark carPark = manager.find(CarPark.class,carParkId);
        List<Object> list = carPark.getFloors().stream()
                .map(e -> (Object) e)
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Object updateCarParkFloor(Object carParkFloor) {

        if (carParkFloor == null) return null;

        EntityManager manager = emf.createEntityManager();

        if (manager.find(CarParkFloor.class,((CarParkFloor) carParkFloor).getId()) != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(carParkFloor);
            transaction.commit();
        }

        return manager.find(CarParkFloor.class,((CarParkFloor) carParkFloor).getId());
    }

    @Override
    public Object deleteCarParkFloor(Long carParkFloorId) {

        if (carParkFloorId == null) return null;
        EntityManager manager = emf.createEntityManager();
        CarParkFloor carParkFloor = manager.find(CarParkFloor.class,carParkFloorId);

        if (carParkFloor.getId() != null){

            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            List<ParkingSpot> spots = carParkFloor.getSpots();
            for (ParkingSpot s :spots) {
                Car car = s.getCar();
                if (car != null){
                    endReservation(car.getReservation().getId());
                }
            }


            manager.remove(carParkFloor);
            transaction.commit();

            return carParkFloor;
        }
        return null;

    }

    @Override
    public Object createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {

        if (floorIdentifier == null || spotIdentifier == null || carParkId == null) return null;


        EntityManager manager = emf.createEntityManager();
        Query q = manager.createNativeQuery("SELECT PARKINGSPOTIDENTIFIER FROM PARKING_SPOT" +
                "    JOIN CAR_PARK_FLOOR CPF on CPF.ID = PARKING_SPOT.floor_id " +
                "WHERE car_park_id = "+ carParkId +" and FLOORIDENTIFIER = '"+ floorIdentifier +"' and PARKINGSPOTIDENTIFIER = '"+ spotIdentifier +"'");
        try{
            q.getSingleResult();
            return null;
        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            ParkingSpot parkingSpot = new ParkingSpot();
            parkingSpot.setParkingSpotIdentifier(spotIdentifier);
            manager.persist(parkingSpot);
            transaction.commit();
            try{
                TypedQuery<CarParkFloor> query = manager.createNamedQuery(CarParkFloor.FIND_BY_FLOOR_IDENTIFIER,CarParkFloor.class);
                query.setParameter("floor_id",floorIdentifier);
                CarParkFloor carParkFloor = query.getSingleResult();
                transaction.begin();
                List<ParkingSpot> spots = carParkFloor.getSpots();
                spots.add(parkingSpot);
                carParkFloor.setSpots(spots);
                transaction.commit();
            }
            catch (NoResultException ex) {
                return null;
            }


            return parkingSpot;

        }

    }

    @Override
    public Object getParkingSpot(Long parkingSpotId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(ParkingSpot.class,parkingSpotId);
    }

    @Override
    public List<Object> getParkingSpots(Long carParkId, String floorIdentifier) {
        EntityManager manager = emf.createEntityManager();

        Query query = manager.createNativeQuery("SELECT * FROM PARKING_SPOT " +
                "JOIN CAR_PARK_FLOOR CPF on PARKING_SPOT.floor_id = CPF.ID  " +
                "WHERE CPF.FLOORIDENTIFIER = '" + floorIdentifier + "' and car_park_id = "+ carParkId);

        List<ParkingSpot> spots= query.getResultList();
        List<Object> list = spots.stream()
                .map(e -> (Object) e)
                .collect(Collectors.toList());

        return list;

    }

    @Override
    public Map<String, List<Object>> getParkingSpots(Long carParkId) {

        EntityManager manager = emf.createEntityManager();
        Map<String,List<Object>> map = new HashMap<>();
        CarPark park = manager.find(CarPark.class,carParkId);
        List<CarParkFloor> floors = park.getFloors();
        for (CarParkFloor f: floors) {
            List<Object> list = f.getSpots().stream()
                    .map(e -> (Object) e)
                    .collect(Collectors.toList());

            map.put(f.getFloorIdentifier(),list);

        }

        return map;
    }

    @Override
    public Map<String, List<Object>> getAvailableParkingSpots(String carParkName) {

        Map<String,List<Object>> map = new HashMap<>();

        CarPark park = (CarPark) getCarPark(carParkName);
        List<CarParkFloor> floors = park.getFloors();
        for (CarParkFloor f: floors) {
            List<ParkingSpot> spots = new ArrayList<>();
            for (ParkingSpot spot: f.getSpots()) {
                if (spot.getCar() == null){
                    spots.add(spot);
                }
            }
            List<Object> list = spots.stream()
                    .map(e -> (Object) e)
                    .collect(Collectors.toList());
            map.put(f.getFloorIdentifier(),list);
        }

        return map;

    }

    @Override
    public Map<String, List<Object>> getOccupiedParkingSpots(String carParkName) {
        Map<String,List<Object>> map = new HashMap<>();

        CarPark park = (CarPark) getCarPark(carParkName);
        List<CarParkFloor> floors = park.getFloors();
        for (CarParkFloor f: floors) {
            List<ParkingSpot> spots = new ArrayList<>();
            for (ParkingSpot spot: f.getSpots()) {
                if (spot.getCar() != null){
                    spots.add(spot);
                }
            }
            List<Object> list = spots.stream()
                    .map(e -> (Object) e)
                    .collect(Collectors.toList());
            map.put(f.getFloorIdentifier(),list);
        }
        return map;
    }

    @Override
    public Object updateParkingSpot(Object parkingSpot) {
        if (parkingSpot == null) return null;

        EntityManager manager = emf.createEntityManager();
        if (manager.find(CarParkFloor.class,((ParkingSpot) parkingSpot).getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            manager.merge(parkingSpot);
            transaction.commit();
        }
        return parkingSpot;
    }

    @Override
    public Object deleteParkingSpot(Long parkingSpotId) {

        EntityManager manager = emf.createEntityManager();
        ParkingSpot spot = manager.find(ParkingSpot.class,parkingSpotId);
        if (spot != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Car car = spot.getCar();
            endReservation(car.getReservation().getId());
            manager.remove(spot);
            transaction.commit();
        }

        return spot;
    }

    @Override
    public Object createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {

        EntityManager manager = emf.createEntityManager();
        TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_PLATE,Car.class);
        query.setParameter("plate",vehicleRegistrationPlate);
        try{
            query.getSingleResult();
            return null;
        }catch (NoResultException e){
            Customer customer = manager.find(Customer.class,userId);
            if (customer == null) return null;

            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            Car car = new Car(brand,model,vehicleRegistrationPlate,colour);
            manager.persist(car);
            transaction.commit();

            transaction.begin();
            customer = manager.find(Customer.class,userId);
            List<Car> cars = customer.getCars();
            cars.add(car);
            customer.setCars(cars);
            transaction.commit();

            return car;

        }
    }

    @Override
    public Object getCar(Long carId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(Car.class,carId);
    }

    @Override
    public Object getCar(String vehicleRegistrationPlate) {
        if (vehicleRegistrationPlate == null) return null;
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_PLATE,Car.class);
        query.setParameter("plate",vehicleRegistrationPlate);

        try {
            Car car = query.getSingleResult();
            return car;
        }
        catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<Object> getCars(Long userId) {

        EntityManager manager = emf.createEntityManager();
        Customer customer = manager.find(Customer.class,userId);
        if (customer == null)return null;

        List<Car> cars= customer.getCars();

        List<Object> list = cars.stream()
                .map(e -> (Object) e)
                .collect(Collectors.toList());

        return list;

    }

    @Override
    public Object updateCar(Object car) {
        if (car == null) return null;

        EntityManager manager = emf.createEntityManager();
        if (manager.find(CarParkFloor.class,((Car) car).getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            manager.merge(car);
            transaction.commit();
        }
        return manager.find(CarParkFloor.class,((Car) car).getId());
    }

    @Override
    public Object deleteCar(Long carId) {

        EntityManager manager = emf.createEntityManager();
        Car car = manager.find(Car.class,carId);

        if (car != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            endReservation(car.getReservation().getId());
            transaction.commit();
            transaction.begin();
            manager.remove(car);
            transaction.commit();
        }

        return car;
    }

    @Override
    public Object createUser(String firstname, String lastname, String email) {

        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNativeQuery("SELECT EMAIL FROM USER WHERE EMAIL = '"+ email +"'");

        try{
            query.getSingleResult();
            return null;

        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            Customer customer = new Customer(firstname,lastname,email);
            manager.persist(customer);
            transaction.commit();
            return customer;
        }

    }

    @Override
    public Object getUser(Long userId) {

        EntityManager manager = emf.createEntityManager();

        return manager.find(Customer.class,userId);
    }

    @Override
    public Object getUser(String email) {
        if (email == null) return null;

        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        transaction.begin();
        TypedQuery<Customer> query = manager.createNamedQuery(Customer.FIND_BY_EMAIL,Customer.class);
        query.setParameter("email",email);
        Customer customer = query.getSingleResult();
        transaction.commit();

        return customer;
    }

    @Override
    public List<Object> getUsers() {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<Customer> query = manager.createNamedQuery(Customer.FIND_ALL,Customer.class);

        try {
            List<Customer> customers = query.getResultList();
            List<Object> list = customers.stream()
                    .map(e -> (Object) e)
                    .collect(Collectors.toList());

            return list;
        }catch (NoResultException e){
            return new ArrayList<>();
        }


    }

    @Override
    public Object updateUser(Object user) {
        if(user == null) return null;
        EntityManager manager = emf.createEntityManager();
        if (manager.find(CarParkFloor.class,((Customer) user).getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(user);
            transaction.commit();
        }
        return manager.find(CarParkFloor.class,((Customer) user).getId());
    }

    @Override
    public Object deleteUser(Long userId) {

        EntityManager manager = emf.createEntityManager();
        Customer customer = manager.find(Customer.class,userId);

        if (customer != null){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            List<Car> cars =customer.getCars();
            for (Car car:cars) {
                endReservation(car.getReservation().getId());
            }
            manager.remove(customer);
            transaction.commit();

        }

        return customer;
    }

    @Override
    public Object createReservation(Long parkingSpotId, Long carId) {
        if (parkingSpotId == null || carId == null) return null;
        EntityManager manager = emf.createEntityManager();
        ParkingSpot spot = manager.find(ParkingSpot.class,parkingSpotId);
        Car car = manager.find(Car.class,carId);

        if (car == null) return null;
        if (spot.getCar() == null){
            try{
                EntityTransaction transaction = manager.getTransaction();
                transaction.begin();
                Reservation reservation = new Reservation();
                Query query = manager.createNativeQuery("SELECT CP.PRICEPERHOUR FROM PARKING_SPOT JOIN CAR_PARK_FLOOR CPF on CPF.ID = PARKING_SPOT.floor_id JOIN CAR_PARK CP on CP.ID = CPF.car_park_id where PARKING_SPOT.id = " + parkingSpotId);
                reservation.setPrice((Integer) query.getSingleResult());
                spot.setCar(car);
                car.setReservation(reservation);
                manager.persist(reservation);
                transaction.commit();

                return reservation;
            }catch (NoResultException e){
                return null;
            }

        }

        return null;
    }

    @Override
    public Object endReservation(Long reservationId) {
        if (reservationId == null) return null;

        EntityManager manager = emf.createEntityManager();
        Reservation reservation = manager.find(Reservation.class,reservationId);

        if (reservation != null){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            reservation = manager.find(Reservation.class,reservationId);
            LocalDateTime aDateTime = LocalDateTime.now();
            reservation.setEndTime(aDateTime);

            Duration timeElapsed = Duration.between(reservation.getBeginTime(), reservation.getEndTime());
            reservation.setPrice((int) (reservation.getPrice() * (timeElapsed.toHours()+1)));

            TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_RESERVATION,Car.class);
            query.setParameter("id",reservationId);
            Car car = query.getSingleResult();
            TypedQuery<ParkingSpot> q = manager.createNamedQuery(ParkingSpot.FIND_BY_CAR,ParkingSpot.class);
            q.setParameter("id",car.getId());
            ParkingSpot spot = q.getSingleResult();
            spot.setCar(null);
            car.setReservation(null);
            transaction.commit();

        }

        return reservation;
    }

    @Override
    public List<Object> getReservations(Long parkingSpotId, Date date) {

        EntityManager manager = emf.createEntityManager();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(date);
        Query query = manager.createNativeQuery("SELECT * FROM RESERVATION JOIN CAR C on RESERVATION.ID = C.RESERVATION_ID JOIN PARKING_SPOT PS on C.ID = PS.CAR_ID " +
                "WHERE PS.ID  = "+ parkingSpotId +" and RESERVATION.BEGINTIME LIKE '"+ strDate +"%'");


        try {

            return query.getResultList();

        }catch (NoResultException e){
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> getMyReservations(Long userId) {

////        EntityManager manager = emf.createEntityManager();
//
//        Query query = manager.createNativeQuery("SELECT * FROM RESERVATION " +
//                "JOIN CAR C on RESERVATION.ID = C.RESERVATION_ID " +
//                "JOIN USER U on U.ID = C.user " +
//                "WHERE user = "+ userId );
//
//        try {
//
//            return query.getResultList();
//        }catch (NoResultException e){
//            return new ArrayList<>();
//        }
return null;
    }

    @Override
    public Object updateReservation(Object reservation) {
        return null;
    }

    @Override
    public Object createHoliday(String name, Date date) {
        if (name == null || date == null) return null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
            String strDate = formatter.format(date);
            EntityManager manager = emf.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Holidays holidays = new Holidays(name,strDate);
            manager.persist(holidays);
            transaction.commit();
            return holidays;
        }catch (RollbackException e){
            return null;
        }

    }

    @Override
    public Object getHoliday(Date date) {
        if (date == null) return null;
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
            String strDate = formatter.format(date);
            EntityManager manager = emf.createEntityManager();
            TypedQuery<Holidays> query = manager.createNamedQuery(Holidays.FIND_BY_DATE,Holidays.class);
            query.setParameter("date",strDate);
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<Object> getHolidays() {
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Holidays> query = manager.createNamedQuery(Holidays.FIND_ALL,Holidays.class);

        List<Object> list = query.getResultList().stream()
                .map(e -> (Object) e)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Object deleteHoliday(Long holidayId) {

        EntityManager manager = emf.createEntityManager();
        Holidays holidays = manager.find(Holidays.class,holidayId);

        if (holidays != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Query query = manager.createNativeQuery("DELETE FROM HOLIDAYS WHERE ID = " + holidayId);
            query.executeUpdate();
            transaction.commit();
        }

        return holidays;
    }
}
