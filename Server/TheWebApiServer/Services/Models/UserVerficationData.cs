namespace TheWebApiServer.Services.Models
{
    public class UserVerficationData
    {
        public int attempts {  get; set; }
        public bool isBloked {  get; set; }
        public long Code { get; set; }
    }   
}
