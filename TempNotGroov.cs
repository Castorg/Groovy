    public class HotelEntity : BaseEntity
    {
        public string Country { get; set; }
        public string City { get; set; }
        public string Address { get; set; }
        public int Stars { get; set; }
        public virtual List<RoomEntity> Rooms { get; set; }
        public virtual NotPrimaryServicesEntity NotPrimaryServices { get; set; }

    }

    public class ParkingEntity
    {
        public int CountPlaces { get; set; }

        public string Address { get; set; }

        public decimal BasicCostPerQuantum { get; set; }
    }

    public class RoomEntity : BaseEntity
    {
        public int Floor { get; set; }
        public int RoomNumber { get; set; }
        public decimal BasicCostPerQuantum { get; set; }
        public RoomState RoomState { get; set; }
        public int QuantityOfPlaces { get; set; }
        public string Notes { get; set; }
    }

    public class NotPrimaryServicesEntity : BaseEntity
    {
        public virtual List<ExcursionEntity> Excursions { get; set; }

        public virtual FoodServiceEntity FoodServices { get; set; }

        public virtual CleaningServieEntity ccc { get; set; }
    }

    public class CleaningServieEntity
    {
        public string CleaningType { get; set; }
        public decimal BasicCostPerQuantum { get; set; }
        public string Address { get; set; }
    }

    public class InterestPlacesEntity
    {
        public string PlaceName { get; set; }
        public string Site { get; set; }
        public DayOfWeek WorkDay { get; set; }
        public decimal BasicCostPerQuantum { get; set; }
        public virtual ParkingEntity NearParking { get; set; }
    }

    public class ExcursionEntity : BaseEntity
    {
        public string ExcursionName { get; set; }

        public decimal BasicCostPerQuantum { get; set; }

    }

    
    public class FoodServiceEntity
    {
        public string Name { get; set; }


    }
    public class TourEntity
    {
        public string TourName { get; set; }
        public string Country { get; set; }
        public string TourType { get; set; }
        public virtual string HotelEntity { get; set; }
    }

    public class BookingEntity
    {
        public string BookingState { get; set; }
        public DateTime StartBookingTime { get; set; }
        public DateTime EndBookingTime { get; set; }
    }

    public enum RoomState
    {
        Busy,
        Empty,
        NotAvailable,
        Booked
    }
