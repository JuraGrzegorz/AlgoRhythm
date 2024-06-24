namespace TheWebApiServer.Services.Models
{
    public class UserVerficationData
    {
        public int attempts {  get; set; }
        public bool isBlocked {  get; set; }
        public long Code { get; set; }
    }   
}
