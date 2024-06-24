using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.AspNetCore.Identity;

namespace TheWebApiServer.Models
{
    public class Playlist
    {
        public int Id { get; set; } 
        public string Name { get; set; }
        public string UserId { get; set; }
        [ForeignKey("UserId")]
        public IdentityUser User { get; set; }
        public List<PlaylistSongs> PlaylistSongs { get; set; }
        public string? ShareCode { get; set; }
    }
}
