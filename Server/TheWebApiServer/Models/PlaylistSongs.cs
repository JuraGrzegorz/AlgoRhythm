using System.ComponentModel.DataAnnotations.Schema;

namespace TheWebApiServer.Models
{
    public class PlaylistSongs
    {
        public int Id { get; set; }
        public int SongId { get; set; }
        [ForeignKey("SongId")]
        public Song Song { get; set; }
        public int PlayListId { get; set; }
        [ForeignKey("PlayListId")]
        public Playlist Playlist { get; set; }

    }
}
