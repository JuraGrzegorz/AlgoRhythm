using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.AspNetCore.Identity;

namespace TheWebApiServer.Models
{
    public class Favourites
    {
        public int Id { get; set; }
        public string UserId {  get; set; }
        [ForeignKey("UserId")]
        public IdentityUser User { get; set; }
        public int SongId { get; set; }
        [ForeignKey("SongId")]
        public Song Song { get; set; }
        public DateTime AddTime { get; set; }
    }
}
