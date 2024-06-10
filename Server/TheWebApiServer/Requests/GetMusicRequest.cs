using System.ComponentModel.DataAnnotations;

namespace TheWebApiServer.Requests
{
    public class GetMusicRequest
    {
        [Required]
        public int MusicId { get; set; }
    }
}
