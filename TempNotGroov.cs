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
        public int QuantityPlace { get; set; }
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

        public string ExcursionTime { get; set; }

        public decimal BasicCostPerQuantum { get; set; }

        public string StartTime { get; set; }

        public string Duration { get; set; }

        public WorkTime WorkTime { get; set; }
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

    public class SelectAllModel
    {
        public bool SelectAll { get; set; }
        public bool Include { get; set; }
        public List<string> Ids { get; set; }

    }
    public class VirtualUserWalletEntity : BaseEntity
    {
        public string CartOwner { get; set; }
        public string ValletNumber { get; set; }
        public string CardId { get; set; }
        public decimal RealAvailableMoney { get; set; }
        public decimal VirtualSpentCredit { get; set; }
        public List<Transaction> Transaction { get; set; }
    }

    public class Transaction : BaseEntity
    {
        public string Address { get; set; }

        public decimal Price { get; set; }

        public int Quantity { get; set; }

        public string State { get; set; }

    }

    public enum RoomState
    {
        Busy,
        Empty,
        NotAvailable,
        Booked
    }
    public class WorkTime
    {
        WorkTime()
        {
            this.WorkDays = new List<DayOfWeek>();
            this.WeekEndDays = new List<DayOfWeek>();
        }
        public List<DayOfWeek> WorkDays { get; set; }
        public List<DayOfWeek> WeekEndDays { get; set; }
    }
